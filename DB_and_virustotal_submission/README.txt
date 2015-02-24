MalwareAnalysisDBProject
contains the java project we used to develope
an easy to use UI to make some useful queries to the DB.
It also contains the classes used to submit apks to VirusTotal analysis.

- com.progetto_sicurezza.dbconfig
Contains DBConfiguration.java
wich stores configuration info used to connect mysql DBMS.

- com.progetto_sicurezza.ui.InterfaceUI.java
Contains main method for the DB GUI
Results files are stored in the working directory.

- com.progetto_sicurezza.dao
- com.progetto_sicurezza.model
DAO e Model for the DB.

- com.progetto_sicurezza.virusTotal
Contains the code we used to submit malwares to VirusTotal
command line args:
args[0]: directory location of apks to submit.
args[1]: destination directory.


################################################

malware_data_collection.sql
cointains the schema of our mysql db.

################################################

Authors:
Raffaele Esposito
Michele Meninno
Pasquale Battista
Agostino Delucia
