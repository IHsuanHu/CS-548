# JSON Schema and Binding
We will be working with JSON data, so in this assignment we use some tools that are useful for JSON data processing.  We use the Gson library to serialize and deserialize data as JSON.  We also look at a schema for the form of JSON data we will see in future assignments, and how to perform validation on the data.

### Assignment code
After finish the code, use Maven build tool in Intellij to build .jar file. Here are steps:
1. clinic-root -> Lifecycle -> clean
2. install
3. There will be two .jar files; jsonbind.jar and jsonschema.jar in the folder: ```C:\Users\user\tmp\cs548```

### Execute jsonbind.jar
1. In the root directory, run this command line: ```Java -jar jsonbind.jar```
### Export JSON data
2. Use ```help``` to check all the command in the project.
3. Use ```save db.json``` to export the created JSON data

### Json data validation
1. In the directory with ```db.json``` file, run this command line: ```Java -jar jsonschema.jar < db.json```; then the result will show.
