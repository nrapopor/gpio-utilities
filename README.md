# gpio-utilities

## Synopsis

A simple utility to test GPIO pins on BeagleBone Black   

## Description

This utility is used for GPIO pin testing of my custom cape for BeagleBone Black. 
I've leveraged a bulldog library, as well as the RGB-123 cape projects (see attributions below). 

The idea behind this utility is the 48 pins I have on my cape (they are named P0 - P47)    
this maps to the bulldog library pins which are in the fomat P[8,9]_(01-46)  where the first part refers    
to the header P8 or P9 and the secon part referes to the actual pin number 01 - 46, per header. Also most of   
the pins have a GPIOX_XX name, that identifies the GPIO bank 0 - 3 and the pin ID on that bank.    
The kernel pin number needed to address that pin with scripting is calculated from the bank mltiplied by 32    
then the actual pin value added to it. Simple really :stuck_out_tongue_winking_eye: 

### Gory Details 
* This utility can be used a cople of ways.
  1. Trigger a pin or a list of pins by bulldog name or cape name as input or output    
     - This will turn on/off the selected pin(s) with 1/2 second interval 10 times (for output)
     - wait for the pin to go high -- print PRESSED message, when button is relased (pin return to low)    
       print RELEASED message
  2. Query the pin (or all pins) using cape name or bulldog name and get the description that    
     will include all the known names for the pin and the bank and pin location    
     The output can be configured to be ``JSON/XML/CSV`` with the default being ``JSON``
* NOTE: Since the bulldog library is using ``/dev/mem`` the JVM will need to be launched    
        with ``sudo`` or adding the user to the group that has access to the ``/dev/mem``
        e.g. ``kmem``.

### Default settings
    to find out default settings run the utility with a ``-h`` argument    
 

## How to use

This utility will need to be built using ``maven`` [https://maven.apache.org/](https://maven.apache.org/ "https://maven.apache.org/")    

### Windows
1.	Download and install(unzip) Maven I recommend a short path with no spaces in it like ``c:\tools\``    
    so the final path to mave would be something like ``c:\tools\apache-maven-X.X.X`` where     
    ``X.X.X`` is the maven version
2.	Create a ``.m2`` folder under your user id    
   on Windows 7+ this means ``C:\Users\<your login id>``     
   on older versions ``C:\Documents and Settings\<your login id>``    
  - NOTE: This cannot be done graphicaly in ``explorer`` only on the command line using 
```cmd
     md .m2     
```
3.	Copy the ``settings.xml`` from maven instalation folder like ``c:\tools\apache-maven-3.3.9\conf``
  - NOTE: I recommend against installing into "Program Files" we will be using command line and     
        scripting. Spaces in folder/file names and scripts do not mix.
4.	Edit the ``settings.xml`` to add the ``server`` entry into the ``<servers>`` section    
```xml
		<server>
			<id>bbb-name</id>
			<username>user</username>
			<password>password</password>
		</server>
```
  - NOTE: there should already be a ``<servers>`` section in the in the document    
        add the ``server`` between the ``<servers>`` and ``</servers>`` tags
5.	The values are as follows 

   | Tag  | value   | description 
   | :---: | :---:  | :--- 
   | id | bbb-name  | * The name of the Beaglebone black. if you did not name it before, add the proper entry in your ``C:\Windows\system32\drivers\etc\hosts`` file. for example  ``bbb-name 192.168.1.145`` obviously using your own name and the actuall BBB IP address
   | username | ubuntu/debian/pi | the name of the user id on the BBB/Raspberry PI
   | password | password | the user password on the BBB/PI usually `ubuntu/temppwd` `pi/raspberry` respectively

6.	On the BeagleBone Black in your ``home`` folder create two folders ``bin`` and ``projects`` These    
   folders are needed for the tool deployement on the BBB :
```sh
    mkdir ~/bin
    mkdir ~/projects
```	
  - NOTE: When running the build please make sure that the BBB is connected to your local network    
         and is configured with the same ip as the one that you put in the hosts file
         
7.	Install java JDK (yes JDK not JRE). Please use the latest java (which at the time of this writing    
    is version 8.
    - Download Java JDK from [oracle website](http://www.oracle.com/technetwork/java/javase/downloads/index.html "http://www.oracle.com/technetwork/java/javase/downloads/index.html")    
    - run installation
    - Create a link to the ``C:\Program Files\Java`` from a path that does     
      not have spaces in it. This will need to be run from an Administrator console    
      ``Right-Click`` on the ``Cmd.exe`` link and click *Run As Administrator*    
      For example: 
```cmd
        mklink /J C:\tools\java "C:\Program Files\Java"
        mklink /J C:\tools\java32 "C:\Program Files (x86)\Java"
```
9.	We will need both Java and Maven in the PATH and JAVA_HOME and M2 and M2_HOME    
   (also M3 and M3_HOME) set to properly run maven. 
   - NOTE: (M3/M3_HOME may not be needed, however this was an issue at one point so I'm keeping them)
   - You can add the following to your system environment variables and PATH or create a    
     setenv_m3.cmd in a convient location (one that's been added to the system path, for me it's ``c:\work\bin``
     or simply put it into the current folder (where you checked out this project).

```cmd
        @set M2=c:\tools\apache-maven-3.3.9
        @set M3=%M2%
        @set M2_HOME=%M2%
        @set M3_HOME=%M2%
        @set JAVA_HOME=C:\tools\java\jdk1.8.0_92
        @set PATH=%M2%\bin;%JAVA_HOME%\bin;%PATH%
        @echo M2=%M2%
        @echo JAVA_HOME=%JAVA_HOME%
```


8.	Ok enough foreplay lets get down to cases. In the checked out folder run:
```cmd
           setenv_m3.cmd
           mvn wagon:update-maven-3
           mvn clean install
```
   - NOTE: There are missing dependencies required to run the waggon plugin I'm using    
           so before building, the ``wagon:update-maven-3`` target must be executed. This only   
           needs to happen once per version of maven.

9.	You will need Java installed on the BBB.  -- Well Duh (_8^(1)
   - The step 1 from the Linux instructions will work on BBB

10.  If everything goes well: 
   - the bin folder on the BBB will have the shell script wrappers for    
      the project.
      
>      -rwxr-xr-x  1 ubuntu ubuntu  168 Apr 22 09:59 deployTest.sh
>      -rwxr-xr-x  1 ubuntu ubuntu  214 Apr 22 09:59 runTest.sh
>      -rwxr-xr-x  1 ubuntu ubuntu  405 Apr 22 09:59 testBank.sh

   - They are :
   | Script  | Purpose 
   | :---   | :--- 
   | `deployTest.sh`| (Re)deploys the project after the build
   | `runTest.sh`   | A simple wrapper to execute the jar
   | `testBank.sh`  | A shortcut to test individual bank of pins on the cape

   - The projects folder will have the following : 
  > drwxrwxr-x  4 ubuntu ubuntu    4096 Apr 23 13:06 gpio-utilities-`<version>`
  >-rw-r--r--  1 ubuntu ubuntu 1373806 Apr 22 09:59 gpio-utilities-`<version>`-bin-dist.tar.gz

10.  To see the available options, run the (in the BBB terminal)
```sh
    runTest.sh -h
```

### Unix or Linux systems
- NOTE: Order of installs is important java then maven
1.	Install Oracle JDK. on debian or ubuntu based systems you can use this script:
```sh
    #!/bin/bash
    RELEASE=`lsb_release -cs`
    LINE_CNT=`grep  "http://ppa.launchpad.net/webupd8team" /etc/apt/sources.list | wc -l`
    if [[ ! ${LINE_CNT} -gt 1 ]]; then
            sudo echo "deb http://ppa.launchpad.net/webupd8team/java/ubuntu ${RELEASE} main" | sudo tee -a /etc/apt/sources.list
            sudo echo "deb-src http://ppa.launchpad.net/webupd8team/java/ubuntu ${RELEASE} main" | sudo tee -a /etc/apt/sources.list
            sudo apt-key adv --keyserver keyserver.ubuntu.com --recv-keys EEA14886
    fi
    sudo apt-get update
    sudo apt-get install oracle-java8-installer
```   
   - if you are not that lucky go to [oracle website](http://www.oracle.com/technetwork/java/javase/downloads/index.html "http://www.oracle.com/technetwork/java/javase/downloads/index.html")    
     and download the package appropriate for your system (rpm or tar.gz)  
     + if rpm (put the correct version instead of ``8u92``  use the latest :
```sh
    ## Oracle java 1.8.0_92 ##
    sudo rpm -Uvh jdk-8u92-linux-x64.rpm
    ## java ##
    sudo alternatives --install /usr/bin/java java /usr/java/latest/jre/bin/java 200000
    ## javaws ##
    sudo alternatives --install /usr/bin/javaws javaws /usr/java/latest/jre/bin/javaws 200000
     
    ## Java Browser (Mozilla) Plugin 32-bit ##
    sudo alternatives --install /usr/lib/mozilla/plugins/libjavaplugin.so libjavaplugin.so /usr/java/latest/jre/lib/i386/libnpjp2.so 200000
     
    ## Java Browser (Mozilla) Plugin 64-bit ##
    sudo alternatives --install /usr/lib64/mozilla/plugins/libjavaplugin.so libjavaplugin.so.x86_64 /usr/java/latest/jre/lib/amd64/libnpjp2.so 200000
     
    ## Install javac only if you installed JDK (Java Development Kit) package ##
    sudo alternatives --install /usr/bin/javac javac /usr/java/latest/bin/javac 200000
    sudo alternatives --install /usr/bin/jar jar /usr/java/latest/bin/jar 200000
```
   + if tar.gz (place the file in your home folder (put the correct version instead of    
     ``8u92``  use the latest :
```sh
    ## Oracle java 1.8.0_92 ##
    tar -xvf jdk-8u92-linux-x64.tar.gz
```
   + If this is your option the JAVA_HOME will be ~/jdk1.8.0_92 (or whatever the current version is)
     add the following lines to your ~/.bashrc or .profile (replacing the ``<your user id>`` with your    
     actual user id
```sh
    export JAVA_HOME=/home/<your user id>/jdk1.8.0_92
    export PATH=${JAVA_HOME}/bin:${PATH}
```
   + NOTE: The settings will not take effect until you logout/login again. Do that before doing step 2 

2.	Download and install(unzip) Maven or again on debian, ubuntu or debian clones:
```sh
    sudo apt-get maven
```
   + If downloading and unzipping manualy
```sh
    tar -xvf apache-maven-<version>-bin.tar.gz
```
   + If this is your option the M2 will be ~/apache-maven-<version> 
     add the following lines to your ~/.bashrc or .profile after the java lines (replacing the     
     ``<your user id>`` with your actual user id and <version> with the actuall maven version
```sh
    export M2=/home/<your user id>/apache-maven-<version>
    export M3=${M2}
    export M2_HOME=${M2}
    export M3_HOME=${M2}
    export PATH=${M2}/bin:${PATH}
```
   + NOTE: Ignore step 8 and any references to the ``setenv_m3.sh`` script. The settings will    
           not take effect until you logout/login again. 
3.	Create a ``.m2`` folder under your user id (on *nix this means ``/home/<your login id>``)     
```sh
     mkdir ~/.m2     
```
4.	Copy the ``settings.xml`` from maven instalation folder like ``/usr/share/maven/conf``
  - NOTE: This assumes you used a package installer to install maven (on a debian clone).     
    On other systems location of maven may be different. you can dermine where the maven home as     
    well as java home locations are by running
```sh
    mvn --version
```
5.	Edit the ``settings.xml`` to add the ``server`` entry into the ``<servers>`` section    
```xml
		<server>
			<id>bbb-name</id>
			<username>user</username>
			<password>password</password>
		</server>
```
  - NOTE: there should already be a ``<servers>`` section in the in the document    
        add the ``server`` between the ``<servers>`` and ``</servers>`` tags
6.	The values are as follows 

   | Tag  | value   | description 
   | :---: | :---  | :--- 
   | id | bbb-name  | * The name of the Beaglebone black. if you did not name it before, add the proper entry in your ``/etc/hosts file`` hosts file >> ``bbb-name 192.168.1.145`` obviously using your own name and the actuall BB Black ip address
   | username | ubuntu/debian/pi | the name of the user id on the BBB/PI
   | password | password | the user password on the BBB usually ubuntu/temppwd/raspberry respectively

7.	On the BeagleBone Black in your ``home`` folder create two folders ``bin`` and ``projects`` These    
   folders are needed for the tool deployement on the BBB :
```sh
    mkdir ~/bin     
    mkdir ~/projects
```
  - NOTE: When running the build please make sure that the BBB is connected to your local network    
         and is configured with the same ip as the one that you put in the hosts file
         
8.	We will need both Java and Maven in the PATH and JAVA_HOME and M2 and M2_HOME    
   (also M3 and M3_HOME) set to properly run maven. 
   - NOTE: (M3/M3_HOME may not be needed, however this was an issue at one point so I'm keeping them)
   - You can add the following to your .profile (or .bashrc) however I do not like fixing specific versions     
     in the initialization. place setenv_m3.sh in ``~/bin``
     or simply put it into the current folder (where you checked out this project).

```sh
    export M2=/usr/share/maven/
    export M3=${M2}
    export M2_HOME=${M2}
    export M3_HOME=${M2}
    export JAVA_HOME=/usr/lib/jvm/java-8-oracle/
    export PATH=${M2}/bin:${JAVA_HOME}/bin:${PATH}
    echo M2=${M2}
    echo JAVA_HOME=${JAVA_HOME}
```

9.	Ok enough foreplay lets get down to cases. In the checked out folder run:
```sh
           . ~/bin/setenv_m3.sh
           sudo mvn wagon:update-maven-3
           mvn clean install
```
   - NOTE: There are missing dependencies required to run the waggon plugin I'm using    
           so before building, the ``wagon:update-maven-3`` target must be executed. This only    
           needs to happen once per version of maven. if this is a package install ``sudo`` will    
           needed otherwise no

10.  If everything goes well: 
   - The bin folder on the BBB will have the shell script wrappers for    
      the project.
      
```sh
        -rwxr-xr-x  1 ubuntu ubuntu  168 Apr 22 09:59 deployTest.sh
        -rwxr-xr-x  1 ubuntu ubuntu  214 Apr 22 09:59 runTest.sh
        -rwxr-xr-x  1 ubuntu ubuntu  405 Apr 22 09:59 testBank.sh
```
 - They are :

   | Script  | Purpose 
   | :---  | :--- 
   | `deployTest.sh`| (Re)deploys the project after the build
   | `runTest.sh`   | A simple wrapper to execute the jar
   | `testBank.sh`  | A shortcut to test individual bank of pins on the cape

   - The projects folder will have the following : 
```sh
   drwxrwxr-x  4 ubuntu ubuntu    4096 Apr 23 13:06 gpio-utilities-`<version>`
   -rw-r--r--  1 ubuntu ubuntu 1373806 Apr 22 09:59 gpio-utilities-`<version>`-bin-dist.tar.gz
```
11.  To see the available options, run the (in the BBB terminal)
```sh
    runTest.sh -h
```

## Notes on this Project
This project was created in early 2016 and all the references to maven and Java versions will     
probably get old very quickly 
            
## Author and Legal information

### Author

Nick Rapoport

### Copyright

Copyright&copy;  2016 Nick Rapoport -- All rights reserved (free 
for duplication under the AGPLv3)

### License

AGPLv3

## Based On 

#### Projects
- [rgb-123.com - beaglebone-black-24-output-cape](http://rgb-123.com/product/beaglebone-black-24-output-cape/ "http://rgb-123.com/product/beaglebone-black-24-output-cape/")
- [pxe3/bulldog](https://github.com/px3/bulldog "https://github.com/px3/bulldog")

#### Date
2016-04-02

