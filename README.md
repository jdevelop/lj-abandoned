lj-abandoned
============

List of Livejournal friends with dates of last journal update.

Building
============

After getting sources, you need to build the app with

    mvn assembly:assembly

Then you may take the archive in target/abandoned-0.1-pack.tar.gz, unpack it and 
proceed to the next section.

Running
============

Go to the directory where you unpacked the TGZ archive from previous step. 
And simply run it as

> java -jar abandoned-0.1.jar 40 %livejournal-username%

where *40* is the number of threads to use for downloading content of FOAF feed.
