JStock is a free stock market software for 26 countries. It provides Stock watchlist, Intraday stock price snapshot, Stock indicator editor, Stock indicator scanner and Portfolio management. Free SMS/email alert supported. It integrates seamless with Android. JStock Android can be downloaded separately from http://goo.gl/t24hN

JStock long term product roadmap can be found from http://goo.gl/55gGQS and http://goo.gl/ClN7zT

# Development / Running

## Netbeans
JStock comes as a native Netbeans project with the dependendencies in `libs/` directory.  Simply open JStock directly and build and run.

## Maven
JStock is also a Maven project with a `pom.xml` allowing it to manage its dependendencies externally (those in the `libs/` directory aren't all used though some are as there are no external counterparts).  You can also use any IDE with maven projects and instructions are given below for Eclipse.

### Command-Line
Maven can be installed as a command-line application in Linux, MacOSX and Windows (DOS).

#### Prerequisites
* The dependencies in `libs/` that have no external counterpart need to be installed in the local Maven repository so they can be referenced as dependencies in the `pom.xml` file.
# In a command window (Linux, MacOSX or Windows DOS) run the `installLocalDependenciesToMaven.sh` or `installLocalDependenciesToMaven.bat` file.

To run JStock directly from the class files:
    cd jstock
    mvn clean compile exec:java

These steps can be run separately.  What they each do is:
    mvn clean   // removes the target directory
    mvn compile // creates the target directory, compiles the source and copies the resource files
    mvn exec:java   // runs the application specifying the main class to be 'org.yccheok.jstock.gui.MainFrame'
    
JStock can also be packaged into a jar to be distributed and run with a 'java -jar jstock.jar'
    mvn packaged    // it will run the compile stage first if required

To create a Maven artifact in the local repository:
    mvn install 

#### TODO
# the `installLocalDependenciesToMaven.bat` needs to be written
# create windows and linux executables through Maven.  
# Currently the 'package' stage creates a Macintosh app however that doesn't work due to how Apple / Oracle have changed how Java runs under OSX.  This needs to be fixed.

### Eclipse
Eclipse can create native projects from Maven projects.  

#### Prerequisites
* Eclipse needs to have the `m2e` plugin installed.
# The dependencies in `libs/` that have no external counterpart need to be installed in the local Maven repository so they can be referenced as dependencies in the `pom.xml` file.
# In a command window (Linux, MacOSX or Windows DOS) run the `installLocalDependenciesToMaven.sh` or `installLocalDependenciesToMaven.bat` file.

To setup Eclipse with the Maven project:
# Checkout / Clone JStock to a separate directory to where the Eclipse Workspace is (you don't want to commit Eclipse project files).  Such as `~/jstock`
# Open Eclipse and use a workspace directory that isn't under the jstock that was checked out / cloned.  Such as `~/(My )Documents/JStock`
# Right-click on the project area and select `Import > Maven > Existing Maven project`
# Browse to the source that was checked out / cloned and Eclipse will show the available `pom.xml` files (Maven projects)
# Select the `pom.xml` and hit import

Now to make a Run Configuration to start JStock:
# Click on the jstock Maven project you just imported
# Right Click > Run As > Run Configurations 
# Click on `Maven build` and hit the `New Launch Configuration` button just above the list box
# Name it something like `JStock exec:java`
# Click the `Browse Workspace` button under `Base Directory:` text box and select the JStock project.  The text box should be populated with `${workspace_loc:/jstock}`
# In the `Goals:` text box write `exec:java`
# Click `Apply` and then `Run`
# The JStock GUI should launch showing any data that had already been setup

#### TODO 
* the `installLocalDependenciesToMaven.bat` needs to be written :)
