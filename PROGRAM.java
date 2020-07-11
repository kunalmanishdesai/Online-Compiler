import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class PROGRAM {

    final String path,language,foldername;
    final long timeLimit;

    static final int MAX_T = 10;

    ArrayList<String> command = new ArrayList<String>(Arrays.asList("cmd.exe","/c"));

    static final HashMap<String,String> compileCommands = new HashMap<String,String>(){
        {
            put("java","javac ");
            put("c","gcc ");
            put("cpp","g++ ");
        }
    };

    static final HashMap<String,String> runCommands = new HashMap<String,String>(){
        {
            put("java","java ");
            put("c","./a.out");
            put("cpp","./a.out");
            put("go","go ");
            put("nodejs","node ");
            put("python2","python2 ");
            put("python3","python3 ");
        }
    };

    public PROGRAM(String path,String language,long timeLimit){
        this.path = path;
        this.language = language;
        this.timeLimit = timeLimit;
        String[] givenPath = path.split("/");//Programs/HelloWorld.java
        this.foldername = givenPath[givenPath.length-1];
    }

    private boolean compile() throws IOException, InterruptedException {
        
        File file = new File("Output/"+this.foldername);
        file.mkdir();

        ProcessBuilder builder = new ProcessBuilder("cmd.exe","/c",
            compileCommands.get(this.language)+this.path).
            redirectErrorStream(true).
            redirectOutput(new File(file,"compileOutput.txt"));

        Process process = builder.start();
        process.waitFor();
        process.destroy();

        if(process.exitValue() == 0) return true;
        else return false;
    }

    private boolean run(){

        //building command.
        String command;
        
        switch(this.language){
            case "c": 
            case "cpp":
                command = runCommands.get(this.language);
                break;
            
            case "java":
                command = runCommands.get(this.language)+this.path;
                break;
            default:
                command = runCommands.get(this.language)+this.path+"."+this.language;
        }

        Callable<Boolean> thread1 = new Task(command,"Input/"+this.foldername+"/input1.txt","Output/"+this.foldername+"/output1.txt");
        Callable<Boolean> thread2 = new Task(command,"Input/"+this.foldername+"/input2.txt","Output/"+this.foldername+"/output2.txt");

        ExecutorService executor = Executors.newFixedThreadPool(MAX_T);
        Future<Boolean> tc1 = executor.submit(thread1),tc2=executor.submit(thread2);
        try{
        if(tc1.get(this.timeLimit,TimeUnit.SECONDS)) System.out.println("Testcase 1 Passed");
        else System.out.println("Testcase 1 TLE");

        if(tc2.get(this.timeLimit,TimeUnit.SECONDS)) System.out.println("Testcase 2 Passed");
        else System.out.println("Testcase 2 TLE");

        return true;
        }catch(Exception e){
            e.printStackTrace();
            return false;
        }
    }
    public void executeProgram(PROGRAM program){
        
        try{
            boolean compiled = program.compile();
            if(compiled)//compiled 
            {
                boolean ans =program.run();
                System.out.println(ans);
            }else System.out.println("Compilation Error");

        }catch(Exception e){
            e.printStackTrace();
            System.out.println("Something went wrong....,Sorry for misconveniance");
        }
    }
}

class Task implements Callable<Boolean>{
    
    String command,inputpath,outputpath,language;

    public Task(String command,String inputpath,String outputpath){
        this.command =command;
        this.inputpath = inputpath;
        this.outputpath = outputpath;
    }
    public Boolean call() throws InterruptedException, IOException {

        ProcessBuilder builder = new ProcessBuilder("cmd.exe","/c",this.command)
        .redirectErrorStream(true)
        .redirectOutput(new File(outputpath))
        .redirectInput(new File(inputpath));

        Process process = builder.start();
        process.waitFor(); 
        process.destroy();

        if(process.exitValue() == 0) return true;
        else return false;
    }
}