package logMonitor.withTailCommand;

import java.io.*;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LogMonitor implements Runnable{
    static String tailCommand;
    static String searchingKeyword;
    static String logFilePath;

    static Process proc;
    static long logFileSize;
    static long currentLogFileSize;
    static BufferedReader reader;

    public static void main(String[] args) throws IOException, InterruptedException {
        System.out.println("Start the LogMonitor program");
        String path = "/Users/selakapiumal/Documents/blog/Log_monitor/Log_Monitor/config_file/config.properties";
        //String path = args[0]; //java -jar Log_Monitor-1.0-SNAPSHOT-jar-with-dependencies.jar path-to-config.properties-file

        Properties prop = new Properties();
        File initialFile = new File(path);
        InputStream inputStream = null;

        try {
            inputStream = new FileInputStream(initialFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        if (inputStream != null) {
            try {
                prop.load(inputStream);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("config.property file is not found");
        }

        //read the properties from config.properties
        tailCommand = prop.getProperty("tailCommand");
        searchingKeyword = prop.getProperty("SearchingKeyword");
        logFilePath = prop.getProperty("logFilePath");

        logFileSize = currentLogFileSize = getLogFileSize(logFilePath); //Calculating initial log file size

        //scheduled task to check the log file size.
        //This is to determine whether the log file has rotated
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new MyTask(), 0, 10*1000); // 1 minute

        //check whether the new line contains searchingKeyword
        decision();

    }

    //To check whether the new line contains searchingKeyword
    //then execute the logic using a concurrent thread
    static void decision() throws IOException, InterruptedException {

        System.out.println("decision got executed!\n");
        ExecutorService executor = Executors.newCachedThreadPool(); //initiated a thread group
        proc = executeCommand(tailCommand); //Tail the carbon log file

        String line = "";
        while((line = reader.readLine()) != null) {  //read line by line from the output of tail command
            if (logFileSize <= currentLogFileSize){
                if (line.contains(searchingKeyword)){ //if new line contains searchingKeyword
                    System.out.print(line + "\n");
                    Runnable worker = new LogMonitor(); //execute a new thread to do the operation
                    executor.execute(worker);
                }
            } else { //log file has rotated
                System.out.print("the log file has rotated\n");
                proc = executeCommand(tailCommand); //start the tail command again with the new rotated log
            }
            logFileSize = currentLogFileSize; //update the file size variable
        }
        proc.waitFor();
    }

    //execute terminal commands such as tail, grep
    static Process executeCommand(String command) throws IOException {
        Process proc = Runtime.getRuntime().exec(command);
        reader = new BufferedReader(new InputStreamReader(proc.getInputStream())); //out put of the executed command
        return proc;
    }

    //return log file size in bytes
    static long getLogFileSize(String path) {
        File file = new File(path);
        long fileSizeInBytes = file.length();
        return fileSizeInBytes;
    }

    //target operation
    public void run() {
        System.out.println(Thread.currentThread().getName()+" (Start) run()\n");
    }

    //scheduled task
    private static class MyTask extends TimerTask {
        public void run() {
            // Your code here
            currentLogFileSize = getLogFileSize(logFilePath);
            System.out.println("Executing task... " + logFileSize +"/"+ currentLogFileSize);
        }
    }
}

