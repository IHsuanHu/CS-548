# Service-Oriented Architecture

In this assignment, we add a service layer to the application from the previous assignment.  The services represent the use case scenarios for the application, orchestrating the domain logic.  This service layer connects the domain model to a Web application representing the presentation logic.  Later we will add Web service support for adding data to the database, for now we are only able to query it.

### Assignment code
After finish the code, use Maven build tool in Intellij to build .jar file. Here are steps:
1. clinic-root -> Lifecycle -> clean
2. install
3. There will be a clinic-webapp.war in the folder: ```C:\Users\user\tmp\cs548```

### Build webapp docker container
1. With the docker file below, run command line ```docker build -t ca548/clinic```
```Dockerfile
FROM payara/micro:6.2023.9-jdk17
COPY --chown=payara:payara clinic-webapp.war ${DEPLOY_DIR}
CMD [ "--contextroot", "clinic", "--deploy", "/opt/payara/deployments/clinic-webapp.war" ]
ENV JVM_ARGS="--add-opens=java.base/java.io=ALL-UNNAMED"
```

### Database creation 
1. Create a container and initialize a postgresql database in it, which is similar to the previous assignment

### Webapp connect to database
1. run this command line: ```docker run -d --name clinic --network cs548-network -p 8080:8080 -p 8181:8181 -e DATABASE_USERNAME=cs548user -e DATABASE_PASSWORD=xxxx -e DATABASE=cs548 -e DATABASE_HOST=cs548db cs548/clinic```
2. run ```docker logs containerID``` to check the deployment is successed or not; Payara Micro URLs will show up.
