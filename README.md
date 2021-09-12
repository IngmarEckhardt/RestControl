# RestControl
The CatControl-MariaDB has now a REST-API and will get a MVC-Api. It work with Spring, but is prepared for deployment in a external Tomcat.

The App need a MariaDB-Connection and a place for a static Backupfile.

The Runtime-Exceptions should only occur the first time you run the app, because you have no Backupfile, but it will be written in the first run, 
if you have at least one Cat-Entity in your database. If you have have also a empty MariaDB you should have alsothe second run these runtime exceptions, 
because it write the backupfile only at the start of the program as a copy of the database.
