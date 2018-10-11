# cs4224-project
### Installation
in order to install cassandra on all the nodes follow these steps:
- go to the script folder 
  - edit forall.sh
    - Set MASTER to any seed node
    - Add all other nodes to the SEEDS csv list
    - If needed change the path to the project directory
    - (optional) change VERSION if required
- Run forall.sh
    - chmod +x forall.sh
    - ./forall.sh

This will install cassandra on all the seeds along with master. 
This script creates keyspace/tables/views and inserts data so no further operations are required for the database

### Running the program
For the purpose of running the program we have provided another script located in the script folder: run.sh.
In order to run this script first edit the script with the following:
-  Set MASTER to any seed node
-  Add all other nodes to the SEEDS csv list
-  Set JAR to the path of the supplied app.jar
-  Set XACT to the path of the transaction folder on the server (this could ideally be a shared folder)

Run the script: ./run.sh NC CL
-   NC is the number of client and can be any number in [1;40]
-   CL is consistency level, and can be either "one" or "quorum"

### Compile from source
In order to compile the program, simply use the included gradlew file 
-   first make it executeable
-   then call "./gradelw fatJar" or "./gradlew run" for simply running the program
