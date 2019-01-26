import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This class is used for generating a runnable for a Threadpool.
 */
class Task implements Runnable
{
    private File myFile;
    private ConcurrentHashMap<String,Long> myHashMap;

    /**
     *
     * @param file this is the file that the thread is responsible for
     * @param filesRowCount this is the concurrent hashmap to store the previous number of rows in the file
     */
    public Task(File file, ConcurrentHashMap<String, Long> filesRowCount)
    {
        myFile = file ;
        myHashMap =filesRowCount;
    }

    public void run()
    {

        Long numOfRows = Long.valueOf(0);
        Boolean fileAccessible = Boolean.FALSE;
        while(fileAccessible == Boolean.FALSE) {

            try {
                Scanner input = new Scanner(myFile);
                while (input.hasNextLine() && input.nextLine()!=null) { numOfRows += 1;}
                fileAccessible = Boolean.TRUE;
                input.close();
            } catch (FileNotFoundException e) {
            }
        }
        //check if we have a previous count of files
        //otherwise we will just output the count
        if(myHashMap.containsKey(myFile.getName())){
            //check if we hav gained lines since the previous count
            if(myHashMap.get(myFile.getName())<numOfRows){
                Long outputRows = numOfRows-myHashMap.get(myFile.getName());
                myHashMap.put(myFile.getName(),numOfRows);
                System.out.println(myFile.getName()+" +"+outputRows);
            }
            //check if we have lost lines
            else if(myHashMap.get(myFile.getName())>numOfRows){
                Long outputRows = myHashMap.get(myFile.getName())-numOfRows;
                myHashMap.put(myFile.getName(),numOfRows);
                System.out.println(myFile.getName()+" -"+outputRows);

            }
            // if we haven't gained or lost lines, we do nothing
        }

        else{
            myHashMap.put(myFile.getName(),numOfRows);
            System.out.println(myFile.getName() +" "+ numOfRows);
        }
        System.out.flush();

    }
} 