Flatfile
========

A complete flat-file parsing solution flexible enough to handle structured or unstructured data written in pure Java.

Flatfile was created in less than a month by Jennifer Gohlke under a strict deadline. At the time, existing flatfile parsing libraries did not offer scripting outside of the development environment.

This project uses Maven to manage builds and dependency management. The completed build includes OSGi metadata which allows Flatfile to be a first class citizen in many OSGi environments. Flatfile was designed with Fuse ESB in mind, but with small modifications could be deployed in any OSGi container.

Features
-------------

Flatfile contains an XML grammar for processing flat files that is [completely documented](doc/Mapping%20File%20Format.pdf?raw=true). It also provides a broad range of available inline data transformation and processing tools.

* Text Append/Prepend/Insert/Substring
* Text Trimming/Stripping
* Captialization Adjustments
* Whitespace Compression
* Regular Expression Replacements
* Regular Expression Text Extraction with Groups
* String Paddings
* Address Formatting
* Delimited List Processing/Expansion/Joins
* Boolean/Numeric/Date/Time Transforms
* Timezone Aware
* Extensible through arbitrary XML attributes which are passed upstream
* Stored Procedure Support (Currently only MSSQL supported)
