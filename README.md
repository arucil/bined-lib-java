Delta Hexadecimal Library
=========================

Hexadecimal viewer/editor component library for Java / Swing.

Homepage: http://deltahex.exbin.org  

Screenshot
----------

![DeltaHex-Example Screenshot](images/deltahex_example.png?raw=true)

Features
--------

- Hexadecimal representation of data and ascii preview
- Insert and overwrite edit modes
- Support for selection and clipboard
- Optional scrollbars
- Support for text encodings
- Support for showing unprintable/whitespace characters
- Support for undo/redo

Todo
----

- Searching for text / hexadecimal code with matching highlighting
- Delta mode - Only changes are stored in memory
- Support for huge files

Compiling
---------

Java Development Kit (JDK) version 7 or later is required to build this project.

For project compiling Gradle 2.0 build system is used. You can either download and install gradle and run

  gradle build

command in project folder or gradlew or gradlew.bat scripts to download separate copy of gradle to perform the project build.

Build system website: http://gradle.org

Development
-----------

The Gradle build system provides support for various IDEs. See gradle website for more information.

There is gradle support plugin, which can be used to some degree, but some projects need other way of handling as described in their readme files.

Gradle support plugin website: http://plugins.netbeans.org/plugin/44510/gradle-support

License
-------

Apache License, Version 2.0 - see LICENSE-2.0.txt
