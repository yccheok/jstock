JStock is a free stock market software for 28 countries. It provides Stock watchlist, Stock indicator editor, Stock indicator scanner and Portfolio management. Free email alert supported. It integrates seamless with Android. JStock Android can be downloaded separately from http://goo.gl/t24hN

JStock long term product roadmap can be found from http://goo.gl/55gGQS and http://goo.gl/ClN7zT

1. Development / Running

## Netbeans
JStock comes as a native Netbeans project with the dependendencies in `libs/` directory.  Simply open JStock directly and build and run.

## Maven
JStock is also a Maven project with a `pom.xml` allowing it to manage its dependendencies externally (those in the `libs/` directory aren't all used though some are as there are no external counterparts.  The `installLocalDependenciesToMaven.sh / .bat` scripts help here).  You can also use any IDE with maven projects and instructions are given below for Eclipse.

### Command-Line
Maven can be installed as a command-line application in Linux, MacOSX and Windows (DOS).

#### Prerequisites
* The dependencies in `libs/` that have no external counterpart need to be installed in the local Maven repository so they can be referenced as dependencies in the `pom.xml` file.
1. In a command window (Linux, MacOSX or Windows DOS) run the `installLocalDependenciesToMaven.sh` or `installLocalDependenciesToMaven.bat` file.

To run JStock directly from the class files:

    cd jstock
    mvn clean compile exec:java

These steps can be run separately.  What they each do is:

    mvn clean   // removes the target directory
    mvn compile // creates the target directory, compiles the source and copies the resource files
    mvn exec:java   // runs the application specifying the main class to be 'org.yccheok.jstock.gui.MainFrame'
    
JStock can also be packaged into a jar to be distributed and run with a 'java -jar jstock.jar'.  The jar is created under the target directory.

    mvn package    // it will run the compile stage first if required

To create a Maven artifact in the local repository.  It will be under `~/.m2/repository/org/yccheok/jstock`:

    mvn install 

#### TODO
1. the `installLocalDependenciesToMaven.bat` needs to be written
1. create windows and linux executables through Maven.  
1. Currently the 'package' stage creates a Macintosh app however that doesn't work due to how Apple / Oracle have changed how Java runs under OSX.  The packaging needs to be fixed to create MacOSX apps.

### Eclipse
Eclipse can create native projects from Maven projects.  

#### Prerequisites
* Eclipse needs to have the `m2e` plugin installed.

1. The dependencies in `libs/` that have no external counterpart need to be installed in the local Maven repository so they can be referenced as dependencies in the `pom.xml` file.
1. In a command window (Linux, MacOSX or Windows DOS) run the `installLocalDependenciesToMaven.sh` or `installLocalDependenciesToMaven.bat` file.

To setup Eclipse with the Maven project:

1. Checkout / Clone JStock to a separate directory to where the Eclipse Workspace is (you don't want to commit Eclipse project files).  Such as `~/jstock`
1. Open Eclipse and use a workspace directory that isn't under the jstock that was checked out / cloned.  Such as `~/(My )Documents/JStock`
1. Right-click on the project area and select `Import > Maven > Existing Maven project`
1. Browse to the source that was checked out / cloned and Eclipse will show the available `pom.xml` files (Maven projects)
1. Select the `pom.xml` and hit import

Now to make a Run Configuration to start JStock:

1. Click on the jstock Maven project you just imported
1. Right Click > Run As > Run Configurations 
1. Click on `Maven build` and hit the `New Launch Configuration` button just above the list box
1. Name it something like `JStock exec:java`
1. Click the `Browse Workspace` button under `Base Directory:` text box and select the JStock project.  The text box should be populated with `${workspace_loc:/jstock}`
1. In the `Goals:` text box write `exec:java`
1. Click `Apply` and then `Run`
1. The JStock GUI should launch showing any data that had already been setup
1. Thereafter you just need to go `Run Toolbar icon > Jstocck exec:java`


