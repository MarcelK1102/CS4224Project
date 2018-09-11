# Developer Guide

- [Setting Up](#setting-up)
- [Running Experiment](#running-experiment)

## Setting Up

### Prerequisites
1. CentOS 7.x (Operating system)
2. **JDK `1.8.0_60`**  or later
3. Download this project file to all servers

### Scripts and Apps
Below are the scripts and application file needed for the setup.

| Scripts        | Location         | 
| ------------- |:-------------:|
| cassandra.sh | /scripts |
| import.sh | /scripts |
| create.sh | /scripts |
| benchmark.sh | /scripts |
| app.jar | /app |
| loader.jar | /app |
| DbState.jar | /app |

### Download and Setup Cassandra Database
(Note: Repeat this step to download and setup the Cassandra database for all the 5 servers)
1. Copy `cassandra.sh` to the server `/temp` directory.
2. Go `/temp` directory and use an editor (e.g. vim) to open `cassandra.sh` and edit the IP address of the seed nodes. For example, if your seed nodes IP address are (192.168.48.249, 192.168.48.251, 192.168.48.253):
```sh
# Add the seed nodes here

SEEDS[0]='192.168.48.249'
SEEDS[1]='192.168.48.251'
SEEDS[2]='192.168.48.253'
```
3. Run `chmod 700 cassandra.sh` to make the script executable.
4. Run `./cassandra.sh` (this script will download cassandra and modify the cassandra yaml file).
5. Run `./apache-cassandra-3.11.0/bin/cassandra` to start the server.

### Download Data Files
(Note: Repeat this step to download the data files for all the 5 servers)
1. Go to the server `/temp` directory and run `wget http://www.comp.nus.edu.sg/~cs4224/4224-project-files.zip`
2. After the file has downloaded, run `unzip 4224-project-files.zip`

### Massage Data Files for Import
(Note: Do this step on your 5th server)
1. Copy `loader.jar` to the server `/temp/4224-project-files/data-files/` directory.
2. Go to `/temp/4224-project-files/data-files/` directory and run `java -jar loader.jar`

### Create Database Tables and Import Data to Database
(Note: Do this step on your 5th server)
1. Copy `create.sh` to the server `/temp` directory (skip if you have already copied).
2. Go to `/temp` directory and run `chmod 700 create.sh` to make the script executable.
3. Run `./create.sh <IP Address> <Port>`
4. Copy `import.sh` to the server `/temp/4224-project-files/data-files/` directory (skip if you have already copied).
5. Go to `/temp/4224-project-files/data-files/` directory and run `chmod 700 import.sh` to make the script executable.
6. Run `./import.sh <IP Address> <Port>`

<b>Note</b>

`<IP Address>` is your Cassandra database IP Address<br/>
`<Port>` is your Cassandra database port (default: 9042)

## Running Experiment
(Note: Copy `benchmark.sh` to all 5 server in their `/temp/4224-project-files/xact-files` directory and copy `DbState.jar` to the 5th server `/temp/4224-project-files/xact-files`)

For each experiment, repeat the steps below for each server:
(Note: After each experiment, run [Create Database Tables and Import Data to Database](#create-database-tables-and-import-data-to-database) step only on the 5th server)

1. Go to the server `/temp/4224-project-files/xact-files` directory.
2. Run `./benchmark.sh <NC Level> <Consistency Level> <Server Index> <IP Address>`
3. At the end of each experiment, all output will be saved into a file in the following format `<Ci>_<NC Level>_<Consistency Level>_output.txt`. (e.g. `1_10_ONE_output.txt`)

(e.g. `./benchmark.sh 40 ONE 5 192.168.48.253` will be run on your 5th server for experiment NC: 40 and Consistency Level: ONE)

<b>Note</b>

`<NC Level>`: 10, 20, 40<br/>
`<Consistency Level>`: ONE, QUORUM<br/>
`<Server Index>`: 1, 2, 3, 4, 5<br/>
`<IP Address>` is your current Cassandra database IP Address
