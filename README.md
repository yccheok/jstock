JStock
=====
JStock is a free stock market software for 26 countries. It provides Stock watchlist, Intraday stock price snapshot, Stock indicator editor, Stock indicator scanner and Portfolio management. Free SMS/email alert supported. It integrates seamless with Android. JStock Android can be downloaded separately from http://goo.gl/t24hN

JStock long term product roadmap can be found from http://goo.gl/55gGQS and http://goo.gl/ClN7zT

[![Gitter chat](https://badges.gitter.im/yccheok/client-app.png)](https://gitter.im/yccheok/jstock)

Development & Building
----
JStock is a Netbeans project.

0. Download or checkout jstock - https://github.com/yccheok/jstock
1. Install Netbans - https://netbeans.org/downloads
2. Open Netbeans and from the File Menu choose 'Open Project' and select the directory you checkedout
3. JStock does not include Junit - this needs to be added.
 * Add the JUnit plugin in Netbeans
 * Right-click on the project and choose `properties > Libraries` and press `Add Library`.  Find `JUnit` in the list and hit `Add Library`.
4. JStock does not include JavaFx this needs to be added manually:
 4. JavaFX has been bundled with the JDK since 7u6.  Locate the jfxrt.jar under `$JAVA_HOME` and copy it into `jstock/libs`.
 3. Right-click on the project and choose `properties > Libraries` and press `Add Jar / Folder`.  Find the `jfxrt.jar` that was just added, choose `Reference as relative path` and press `Choose`.
4. Choose `Netbeans > Run > Clean and Build Project` and this will build the file `dist/jstock.jar`.  You can run jstock from the command-line independent of Netbeans with:

`# Run from the command-line`

    cd jstock
    java -jar dist/jstock.jar
    blah

5. Alternatively to run jstock directly in Netbeans - `Netbeans > Run > Run`
