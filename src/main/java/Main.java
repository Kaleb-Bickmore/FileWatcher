import java.io.File;
import java.nio.file.FileSystems;
import java.nio.file.PathMatcher;
import java.util.*;
import java.util.concurrent.*;

public class Main {
    /**
     *
     * @param args input parameters to our application
     * @throws InterruptedException
     */
    public static void main(String args[]) throws InterruptedException {
        if(args.length==0||args[0]==null ){
            System.out.println("Please specify a directory, exiting.");
            return;
        }
        if(args.length==1||args[1]==null){
            String temp[] = new String[2];
            temp[0] = args[0];
            temp[1]="";
            args = temp;
        }

        //END
        HashMap<String, Long> filesModifiedDate = new HashMap<String, Long>();// Holds the modified time of each tracked file
        ConcurrentHashMap<String, Long> filesRowCount= new ConcurrentHashMap<String, Long>();//holds the row count of each file
        int numCores = Runtime.getRuntime().availableProcessors();//total number of cores available on the machine running
        LinkedList<File> myQueue = new LinkedList<File>();//simple queue implementation using a linkedlist

        File dir = new File(args[0]);
        //check if this is a valid directory
        if(!dir.exists()){
            System.out.println(args[0]+" is not a directory, exiting.");
            return;
        }
        System.out.println("File Watcher version 1.0.0");
        System.out.println("Enter quit to end this program.");
        InputThread userInputThread = new InputThread();
        userInputThread.start();
        ExecutorService pool = Executors.newFixedThreadPool(numCores);
        PathMatcher pathMatcher = FileSystems.getDefault().getPathMatcher("glob:**"+args[1]);
        // This loop stays alive while the user has not typed quit
        Boolean firstTime = Boolean.TRUE;
        while (userInputThread.isAlive()) {
            //every time we run out of items in our queue, we will wait for 10 seconds and process all files again
            if (myQueue.peek() == null) {
                if(!firstTime)
                {
                    TimeUnit.SECONDS.sleep(10);
                }
                firstTime = Boolean.FALSE;
                File[] listOfAllFiles = dir.listFiles();
                for (File myFile : listOfAllFiles) {
                    if (pathMatcher.matches(myFile.toPath())) {
                        myQueue.add(myFile);
                    }
                }
            }
            File currentFile;
            try {
                currentFile = myQueue.remove();
            }catch (NoSuchElementException e){
                continue;
            }

            Boolean processFile = Boolean.FALSE;
            //Check if the File has been added to the hash map at all
            if (filesModifiedDate.containsKey(currentFile.getName()))
            {
                // check if the file has been modified since we last processed it
                if(filesModifiedDate.get(currentFile.getName())<currentFile.lastModified())
                {
                    filesModifiedDate.replace(currentFile.getName(),currentFile.lastModified());
                    processFile = Boolean.TRUE;
                }
            }
            else
                {
                processFile = Boolean.TRUE;
                filesModifiedDate.put(currentFile.getName(),currentFile.lastModified());
            }
            //Finally we spin off a thread to process the file if the date is modified, or its a new file
            if(processFile== Boolean.TRUE)
            {
                Runnable myThread = new Task(currentFile,filesRowCount);
                pool.execute(myThread);
            }
        }

        pool.shutdown();
        pool.awaitTermination(5,TimeUnit.SECONDS);
        pool.shutdownNow();
    }
}