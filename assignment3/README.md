# Object Relational Mapping
In this assignment, we explore the use of an object-relational mapping framework to bridge the impedance mismatch between an object-oriented program and a relational database.  We will be using Eclipselink, an implementation of the Jakarta Persistence Architecture (JPA).  In this assignment, we use Eclipselink in a standalone CLI that reads input from the console and inserts it into a database server (such as the one you set up in the first assignment). 

### Assignment code
After finish the code, use Maven build tool in Intellij to build .jar file. Here are steps:

1. clinic-data-model -> Lifecycle -> clean
2. install
3. There will be a .jar files; clinicdb.jar in the folder: ```C:\Users\user\tmp\cs548```

### Create a database on EC2 by using docker container
1. Please see assignment 1 to initialize the database

### Run .jar file to connect to database on EC2
1. Use this command line: ```java -jar clinicdb.jar --password xxxx --server jdbc:postgresql://ec2_ip:5432/cs548```
2. Create data and add to database

### Use Intellij to connect to database
1. Click database icon; create a new connection and choose Postgresql as database source.
2. Paste url ```jdbc:postgresql://ec2_ip:5432/cs548```; User is cs548user and Password is xxxx.
3. After the connection is created, check the data through Intellij.
