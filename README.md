# cs4224-project
### Installation
in order to install MongoDB on all the nodes follow these steps:
- go to the script folder 
  - edit forall.sh
    - Add all nodes to the SHARDS list
    - If needed change the path to the project directory
- Run forall.sh
    - chmod +x forall.sh
    - ./forall.sh

This will install MongoDB on all the shards along with master. This will run a config server on the tree first given shards, and a shardserver and mongos instance on all of the shards 

### Running the program
For the purpose of running the program we have provided another script located in the script folder: run.sh.
In order to run this script first edit the script with the following:
-  Set MASTER to any shard node
-  Add all other nodes to the SHARDS list
-  Set JAR to the path of the supplied app.jar
-  Set XACT to the path of the transaction folder on the server (this could ideally be a shared folder)

Run the script: ./run.sh NC CL
-   NC is the number of client and can be any number in [1;40]
-   CL is read/write consistency level, and can be either "local" or "majority"

### Compile from source
In order to compile the program, simply use the included gradlew file 
-   first make it executeable
-   then call "./gradelw fatJar" or "./gradlew run" for simply running the program
