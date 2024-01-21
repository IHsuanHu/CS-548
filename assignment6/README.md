# REST Web Service

In this assignment, we add a Web service to our suite of applications, deployed in parallel with the Web application from the previous assignment.  This Web service allows clinical data to be uploaded to the database, whereas the Web application only allows the database to be queried.  We also provide a Web service client, a command-line program that supports operations for uploading data to the Web service.  In a real-world scenario, this latter might be a mobile app that periodically uploads its data to a cloud database.

### Assignment code
After finish the code, use Maven build tool in Intellij to build .jar file. Here are steps:
1. clinic-root -> Lifecycle -> clean
2. install
3. There will be clinic-webapp.war, clinic-rest.war, and clinic-rest-client.jar in the folder: ```C:\Users\user\tmp\cs548```

### Build webapp docker container
1. With the docker file below, run command line ```docker build -t cs548/chat .```
```Dockerfile
FROM payara/micro:6.2023.9-jdk17
COPY --chown=payara:payara clinic-webapp.war ${DEPLOY_DIR}
CMD [ "--contextroot", "clinic", "--deploy", "/opt/payara/deployments/clinic-webapp.war" ]
ENV JVM_ARGS="--add-opens=java.base/java.io=ALL-UNNAMED"
```
### Build rest docker container
1. With the docker file below, run command line ```docker build -t cs548/clinic-rest .```
```Dockerfile
FROM payara/micro:6.2023.9-jdk17
COPY --chown=payara:payara clinic-rest.war ${DEPLOY_DIR}
CMD [ "--contextroot", "api", "--deploy", "/opt/payara/deployments/clinic-rest.war" ]
ENV JVM_ARGS="--add-opens=java.base/java.io=ALL-UNNAMED"
```
### Database creation
1. Create a container and initialize a postgresql database in it, which is similar to the previous assignment

### Webapp connect to database
1. run this command line: ```docker run -d --name chat --network cs548-network -p 8080:8080 -p 8181:8181 -e DATABASE_USERNAME=cs548user -e DATABASE_PASSWORD=0000 -e DATABASE=cs548 -e DATABASE_HOST=cs548db cs548/chat```
2. run ```docker logs containerID``` to check the deployment is successed or not; Payara Micro URLs will show up.

### Access to application
1. Open a browser
2. Use this url to have an access to web application: ```http://localhost:8080/clinic```

### REST application connect to database
1. run this command line: ```docker run -d --name clinic-rest --network cs548-network -p 9090:8080 -e DATABASE_USERNAME=cs548user -e DATABASE_PASSWORD=0000 -e DATABASE=cs548 -e DATABASE_HOST=cs548db cs548/clinic-rest```
2. run ```docker logs containerID``` to check the deployment is successed or not; Payara Micro URLs will show up.

### Access to WADL
1. Open a browser
2. Use this url to have an access to web application: ```http://localhost:9090/api/application.wadl```

### Use REST clinic side to connect to REST application
1. In the root directory, run this command line: ```java -jar clinic-rest-client.jar --server http://localhost:9090/api/```
2. Use ```help``` to check all the command in the project.

