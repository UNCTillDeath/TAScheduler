package comp110;

import java.io.*;

public class Storage{

    // constants
    private static final String DEFAULT_GITHUB_REPO_OWNER = "UNCTillDeath";
    private static final String DEFAULT_GITHUB_REPO_NAME = "TAScheduler";
    private static final String DEFAULT_GITHUB_USERNAME = "";
    private static final String DEFAULT_GITHUB_PASSWORD = "";
    private static final String DEFAULT_COMMIT_MESSAGE = "Updated TA Availability by TAScheduler";
    private static final boolean DEFAULT_ECHO_TO_CONSOLE = false;
    private static final String DEFAULT_CONFIG_FILE = "./storage_config.ini";

    // variables
    private Controller m_controller;
    private String m_github_repo_owner;
    private String m_github_repo_name;
    private String m_username;
    private String m_password;
    private boolean m_echo_to_console;


    public Storage(Controller controller){
        // save the controller for later use
        this.m_controller = controller;

        // echo default
        this.m_echo_to_console = DEFAULT_ECHO_TO_CONSOLE;

        // look for a local storage config file
        FileInputStream in = null;
        BufferedReader reader = null;
        try {
            // open file for reading
            in = new FileInputStream(DEFAULT_CONFIG_FILE);
            // create a buffered reader to read strings from the file
            reader = new BufferedReader(new InputStreamReader(in));
            // read the repo and set default username and password
            this.m_github_repo_owner = reader.readLine();
            this.m_github_repo_name = reader.readLine();
            this.m_username = DEFAULT_GITHUB_USERNAME;
            this.m_password = DEFAULT_GITHUB_PASSWORD;
        }
        catch (IOException e){
            // unable to find file to open or error reading from file
            // set defaults for the repo, username, and password
            this.m_github_repo_owner = DEFAULT_GITHUB_REPO_OWNER;
            this.m_github_repo_name = DEFAULT_GITHUB_REPO_NAME;
            this.m_username = DEFAULT_GITHUB_USERNAME;
            this.m_password = DEFAULT_GITHUB_PASSWORD;
        }
        finally{
            // close the reader and file
            try {
                if (reader != null) {
                    reader.close();
                }
                if (in != null){
                    in.close();
                }
            }
            catch (IOException e){
                // exception in close the reader and file?
                // should not be possible to get here
                // even if it does we have did all the work we wish do to
                // so everything is setup
            }
        }
    }

    public void cleanup(){
        // delete the local repo
        deleteDirectory(new File(this.getFilesPath()));
    }

    public void pullFiles(){
        // construct a thread to run the pull asynchronously
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // first make sure any old data is gone
                    deleteDirectory(new File("./" + Storage.this.m_github_repo_name));

                    // create the process to pull the files
                    ProcessBuilder pb = new ProcessBuilder("git", "clone", "https://" + Storage.this.m_username +
                            ":" + Storage.this.m_password + "@github.com/" + Storage.this.m_github_repo_owner + "/" +
                            Storage.this.m_github_repo_name + ".git");

                    // set the directory to the current directory
                    // the clone will cause a new directory to be created with the name of the repo
                    pb.directory(new File("."));
                    pb.redirectErrorStream(true);

                    // execute the command
                    Process p = pb.start();

                    // display all the output from the command
                    BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
                    String line = "";
                    String all_lines = "";
                    while ((line = reader.readLine()) != null){
                        if (Storage.this.m_echo_to_console == true) {
                            System.out.println(line);
                        }
                        all_lines += line + '\n';
                    }

                    // wait for process to finish
                    try {
                        p.waitFor();
                    }
                    catch (InterruptedException e){
                        // process was interrupted
                        Storage.this.m_controller.storagePullCompleteCallback(false, e.getMessage());
                        return;
                    }

                    // check the return value of the command
                    if (p.exitValue() != 0){
                        // unable to pull...lots of reasons but something descriptive should be stored in all_lines
                        Storage.this.m_controller.storagePullCompleteCallback(false, all_lines);
                        return;
                    }
                    else{
                        // pull is successful
                        Storage.this.m_controller.storagePullCompleteCallback(true, all_lines);
                        return;
                    }
                } catch (IOException e) {
                    // unable to pull for some reason.  some exception
                    Storage.this.m_controller.storagePullCompleteCallback(false, e.getMessage());
                }
            }
        });
        thread.start();
    }

    public void pushFiles(){
        // construct a thread to run the push asynchronously
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // first make sure that the directory is there to push
                    File directory = new File(Storage.this.getFilesPath());
                    if (directory.exists() == false){
                        // probably didnt do a pull/clone first
                        Storage.this.m_controller.storagePushCompleteCallback(false, "Repo directory does not exists.  Did we forget to pull first?");
                        return;
                    }

                    // git add .
                    ProcessBuilder pb = new ProcessBuilder("git", "add", ".");

                    // set the directory
                    pb.directory(new File(Storage.this.getFilesPath()));
                    pb.redirectErrorStream(true);

                    // execute the command
                    Process p = pb.start();

                    // display all the output from the command
                    BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
                    String line = "";
                    String all_lines = "";
                    while ((line = reader.readLine()) != null){
                        if (Storage.this.m_echo_to_console == true) {
                            System.out.println(line);
                        }
                        all_lines += line + "\n";
                    }

                    // wait for process to finish
                    try {
                        p.waitFor();
                    }
                    catch (InterruptedException e){
                        // process was interrupted
                        Storage.this.m_controller.storagePushCompleteCallback(false, e.getMessage());
                        return;
                    }

                    // add successful
                    // execute commit -m "message"
                    pb = new ProcessBuilder("git", "commit", "-m", DEFAULT_COMMIT_MESSAGE);

                    // set the directory
                    pb.directory(new File(Storage.this.getFilesPath()));
                    pb.redirectErrorStream(true);

                    // execute the command
                    p = pb.start();

                    // display all the output from the command
                    reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
                    line = "";
                    all_lines = "";
                    while ((line = reader.readLine()) != null){
                        if (Storage.this.m_echo_to_console == true) {
                            System.out.println(line);
                        }
                        all_lines += line + "\n";
                    }

                    // wait for process to finish
                    try {
                        p.waitFor();
                    }
                    catch (InterruptedException e){
                        // process was interrupted
                        Storage.this.m_controller.storagePushCompleteCallback(false, e.getMessage());
                        return;
                    }

                    // need to do a pull to get any remote changes that happened since we did our initial pull
                    // create the process to pull the files
                    pb = new ProcessBuilder("git", "pull");

                    // set directory and redirect error to standard input stream
                    pb.directory(new File(Storage.this.getFilesPath()));
                    pb.redirectErrorStream(true);

                    // execute the command
                    p = pb.start();

                    // display all the output from the command
                    reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
                    line = "";
                    all_lines = "";
                    while ((line = reader.readLine()) != null){
                        if (Storage.this.m_echo_to_console == true) {
                            System.out.println(line);
                        }
                        all_lines += line + '\n';
                    }

                    // wait for process to finish
                    try {
                        p.waitFor();
                    }
                    catch (InterruptedException e){
                        // process was interrupted
                        Storage.this.m_controller.storagePushCompleteCallback(false, e.getMessage());
                        return;
                    }


                    // pull successful
                    // execute push
                    pb = new ProcessBuilder("git", "push");

                    // set the directory
                    pb.directory(new File(Storage.this.getFilesPath()));
                    pb.redirectErrorStream(true);

                    // execute the command
                    p = pb.start();

                    // display all the output from the command
                    reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
                    line = "";
                    all_lines = "";
                    while ((line = reader.readLine()) != null){
                        if (Storage.this.m_echo_to_console == true) {
                            System.out.println(line);
                        }
                        all_lines += line + "\n";
                    }

                    // wait for process to finish
                    try {
                        p.waitFor();
                    }
                    catch (InterruptedException e){
                        // process was interrupted
                        Storage.this.m_controller.storagePushCompleteCallback(false, e.getMessage());
                        return;
                    }

                    // check the return value of the command
                    if (p.exitValue() != 0){
                        // unable to pull...lots of reasons but something descriptive should be stored in last_line
                        Storage.this.m_controller.storagePushCompleteCallback(false, all_lines);
                        return;
                    }

                    // all steps complete successfully
                    Storage.this.m_controller.storagePushCompleteCallback(true, "");

                } catch (IOException e) {
                    // unable to push for some reason.  some exception
                    Storage.this.m_controller.storagePushCompleteCallback(false, e.getMessage());
                }
            }
        });
        thread.start();
    }

    public String getFilesPath(){
        return "./" + this.m_github_repo_name;
    }
    
    public String getFilePathToOnyen(String onyen){
    	return (this.getFilesPath() + "/data/spring-17/staff/" + onyen + ".csv");
    }
    
    public String getFilePathToSchedule(){
    	return this.getFilesPath() + "needtofigurethisout";
    }

    public void setUsername(String username){
        this.m_username = username;
    }

    public void setPassword(String password){
        this.m_password = password;
    }

    public void setEchoToConsole(boolean echo){
        this.m_echo_to_console = echo;
    }

    private static boolean deleteDirectory(File directory){
        if (directory.isDirectory()){
            String[] children = directory.list();
            for (int i = 0; i < children.length; ++i){
                boolean success = deleteDirectory(new File(directory, children[i]));
                if (success == false){
                    // need to force a delete somehow....
                	// this is very likely because a file was not being closed
                }
            }
        }
        return directory.delete();
    }
}