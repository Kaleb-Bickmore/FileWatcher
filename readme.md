# FileWatcher
The purpose of this application is to watch a directory for user specified files. We count the lines of those files, and tell the difference in the number of lines if we have encountered them before.
## How to run
This project uses gradle and java 1.8 and I built the project in intellij.
There is a `FileWatcher.jar` file that will run the last built project. Inside a terminal/console
use the command `java -jar FileWatcher.jar <directory> <file pattern>`.

## Classes

### Main.java
This is the driver of the code. It also checks the command line input for validity. This will continue looping as long as our InputThread is alive. It uses a queue to look at each file in the directory that matches our glob string.
### Task.java
This class implements a runnable to act as a thread for our thread pool in main. it waits till a file is not in use to count the number of lines in it. It then stores this information inside a concurrent hash map. This makes it so we can tell if a file has lost or gained lines in the code.
### InputThread.java
This is an implementation of a thread with the sole purpose of watching the users input. It waits till it sees `"quit"` and will end the process after 5 seconds or when the threads finish.
