lj-abandoned
============

List of Livejournal friends with dates of last journal update.

Building
============

After getting sources, you need to build the app with

    mvn assembly:assembly

Then you may take the archive in target/abandoned-0.1-pack.tar.gz, unpack it and 
proceed to the next section.

If you do not to waste time on Java-related stuff - you may take pre-built archive 
from **dist/** folder.

Running
============

Go to the directory where you unpacked the TGZ archive from previous step. 
And simply run it as

    java -jar abandoned-0.1.jar 40 %livejournal-username%

where **40** is the number of threads to use for downloading content of FOAF feed.

The output is HTML document being streamed to **stdout**, so you might want to 
redirect it as

    java -jar abandoned-0.1.jar 40 %username# > friends-table.html

or

    java -jar abandoned-0.1.jar 40 %username# 2>&1 | tee friends-table.html

the last variant will allow to track the output in console and save file at the same
time. This case is **not tested on Windows**.
