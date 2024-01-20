# Domain-Driven Design
In this assignment, we take the data model from the previous assignment and flesh it out to a domain model by adding domain logic in the objects.  We incorporate key patterns from domain-driven design, including factory, repository and aggregate patterns.  We start to incorporate dependency injection by specifying the repositories (domain access objects) as CDI beans.  We deploy this in a "dummy" Web application, to which we will add a service layer in the next assignment.

### Assignment code
After finish the code, use Maven build tool in Intellij to build .jar file. Here are steps:

clinic-root -> Lifecycle -> clean
install
There will be a clinic-webapp.war in the folder: ```C:\Users\user\tmp\cs548```

### Build webapp docker container
1. With the docker file below, run command line ```docker build -t ca548/clinic```
FROM payara/micro:6.2023.9-jdk17
COPY --chown=payara:payara clinic-webapp.war ${DEPLOY_DIR}
CMD [ "--contextroot", "clinic", "--deploy", "/opt/payara/deployments/clinic-webapp.war" ]
ENV JVM_ARGS="--add-opens=java.base/java.io=ALL-UNNAMED"

### Database creation 
1. Create a container and initialize a postgresql database in it, which is similar to the previous assignment

### Webapp connect to database
1. run this command line: ```docker run -d --name clinic --network cs548-network -p 8080:8080 -p 8181:8181 -e DATABASE_USERNAME=cs548user -e DATABASE_PASSWORD=xxxx -e DATABASE=cs548 -e DATABASE_HOST=cs548db cs548/clinic```
2. run ```docker logs containerID``` to check the deployment is successed or not.


