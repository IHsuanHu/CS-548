# Micro Profile

Micro Profile defines a set of APIs for Java microservices.  It borrows some aspects of Jakarta EE, including CDI and JAX-RS (but not JPA or JMS).  It adds other APIs for tracing, configuration, health checking and fault tolerance, as well as a declarative API for REST clients. In this assignment, you will deploy the applications from previous assignments as microservices, using Payara Micro.

### Assignment code
After finish the code, use Maven build tool in Intellij to build .jar file. Here are steps:

1. clinic-root -> Lifecycle -> clean
2. install
3. There will be clinic-webapp.war, clinic-rest.war, clinic-rest-client.jar, and clinic-domain.war in the folder: C:\Users\user\tmp\cs548

### Build domain docker container
With the docker file below, run command line ```docker build -t cs548/clinic-domain .```
```Dockerfile
FROM payara/micro:6.2023.2-jdk17
COPY --chown=payara:payara clinic-domain.war ${DEPLOY_DIR}
CMD [ "--contextroot", "api", "--deploy", "/opt/payara/deployments/clinic-domain.war" ]
```

### Build rest docker container
With the docker file below, run command line ```docker build -t cs548/rest .```
```Dockerfile
FROM payara/micro:6.2023.2-jdk17
COPY --chown=payara:payara clinic-rest.war ${DEPLOY_DIR}
CMD [ "--contextroot", "api", "--deploy", "/opt/payara/deployments/clinic-rest.war" ]
ENV JVM_ARGS="--add-opens=java.base/java.io=ALL-UNNAMED"
```

### Build webapp docker container
With the docker file below, run command line ```docker build -t cs548/chat .```
```Dockerfile
FROM payara/micro:6.2023.2-jdk17
COPY --chown=payara:payara clinic-webapp.war ${DEPLOY_DIR}
CMD [ "--contextroot", "clinic","--deploy", "/opt/payara/deployments/clinic-webapp.war" ]
ENV JVM_ARGS="--add-opens=java.base/java.io=ALL-UNNAMED"
```
### Database creation
Create a container and initialize a postgresql database in it, which is similar to the previous assignment.

```bash
// Some command line here may help
docker run -d --name cs548db --network cs548-network -p 5432:5432 -v /data:/var/lib/postgresql/data -e POSTGRES_PASSWORD=xxxx -e PGDATA=/var/lib/postgresql/data/pgdata postgres
//create user
docker run -it --rm --network cs548-network postgres /bin/bash
createuser cs548user -P --createdb -h cs548db -U postgres
```

### Microservice connect to database
1. run this command line: ```docker run -d --name clinic-domain --network cs548-network -p 5050:8080 -e DATABASE_USERNAME=cs548user -e DATABASE_PASSWORD=xxxx -e DATABASE=cs548 -e DATABASE_HOST=cs548db cs548/clinic-domain```
2. run ```docker logs containerID``` to check the deployment is successed or not; Payara Micro URLs will show up.
3. Access to WADL and health ckeck:
  1. Open a browser
  2. Use this url to have an access to WADL: ```http://localhost:8080/api/application.wadl```
  3. Use this urls to have an access to do health check: ```http://localhost:8080/health/live```, ```http://localhost:8080/health/ready```

### REST connect to microservice
1. run this command line: ```docker run -d --name clinic-rest --network cs548-network -p 9090:8080 -e DATABASE_USERNAME=cs548user -e DATABASE_PASSWORD=xxxx -e DATABASE=cs548 -e DATABASE_HOST=cs548db cs548/rest```
2. run ```docker logs containerID``` to check the deployment is successed or not; Payara Micro URLs will show up.
3. Access to WADL and health ckeck:
  1. Open a browser
  2. Use this url to have an access to WADL: ```http://localhost:9090/api/application.wadl```
  3. Use this urls to have an access to do health check: ```http://localhost:9090/health/live```, ```http://localhost:9090/health/ready```

### Webapp connect to microservice
1. run this command line: ```docker run -d --name chat --network cs548-network -p 8080:8080 -e DATABASE_USERNAME=cs548user -e DATABASE_PASSWORD=xxxx -e DATABASE=cs548 -e DATABASE_HOST=cs548db cs548/chat```
2. run ```docker logs containerID``` to check the deployment is successed or not; Payara Micro URLs will show up.


### Use REST clinic side to connect to REST application
1. In the root directory, run this command line: ```java -jar clinic-rest-client.jar --server http://localhost:9090/api/```
2. Use help to check all the command in the project and add more data.

