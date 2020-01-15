# Tableexporter

This program is used to convert the git history of a repository into an R friendly CSV format

## How to Use

To start the Programm use the following command

java -jar target/tableexport-0.0.1-SNAPSHOT-jar-with-dependencies.jar

in the Projekt Directory

After starting the program, each url of a repository in the URLList.txt is downloaded and converted into a CSV format.
The URLList.txt has the following format:

- Number(Rank),Name,URL
   