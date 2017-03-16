package comp110;


import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.PullResult;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.dircache.DirCache;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.transport.PushResult;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import java.io.File;
import java.io.IOException;

/**
 * Created by djsteffey on 3/6/2017.
 */

public class Storage_v2 {
    // interface / inner class
    public interface Storage_v2Listener{
        void storage_get_files_complete(boolean success, String message);
        void storage_save_files_complete(boolean success, String message);
    }

    // constants
    private static final String DEFAULT_LOCAL_REPO_FOLDER = "/repo";
    private static final String DEFAULT_GITHUB_REPO = "https://github.com/UNCTillDeath/TAScheduler.git";
    private static final String DEFAULT_GITHUB_USERNAME = "";
    private static final String DEFAULT_GITHUB_PASSWORD = "";
    private static final String DEFAULT_COMMIT_MESSAGE = "Updated TA Availability by TAScheduler - Android";
    private static final boolean DEFAULT_ECHO_TO_CONSOLE = false;
    private static final String DEFAULT_CONFIG_FILE = "/storage_config.ini";


    // variables
    private Storage_v2Listener m_listener;
    private String m_application_directory;
    private String m_username;
    private String m_password;

    // functions
    public Storage_v2(Storage_v2Listener listener, String application_directory){
        this.m_listener = listener;
        this.m_application_directory = application_directory;
        this.m_username = DEFAULT_GITHUB_USERNAME;
        this.m_password = DEFAULT_GITHUB_PASSWORD;
    }

    public void set_username(String username){
        this.m_username = username;
    }

    public void set_password(String password){
        this.m_password = password;
    }

    public String get_username(){
        return this.m_username;
    }

    public String get_password(){
        return this.m_password;
    }

    public String get_availability_csv_filename_from_onyen(String onyen){
        return this.m_application_directory + DEFAULT_LOCAL_REPO_FOLDER + "/data/spring-17/staff/" + onyen + ".csv";
    }

    public String get_schedule_json_filename(){
        return this.m_application_directory + DEFAULT_LOCAL_REPO_FOLDER + "/testData/schedule.json";
    }

    public String get_path_to_onyen_csv_directory(){
        return this.m_application_directory + DEFAULT_LOCAL_REPO_FOLDER + "/data/spring-17/staff/";
    }

    public void get_files(){
        // start a thread to do the actual work
        new Thread(new Runnable() {
            @Override
            public void run() {
                // first do we need to just pull or do a full clone?
                // let just see if the folder exists
                File directory = new File(Storage_v2.this.m_application_directory + DEFAULT_LOCAL_REPO_FOLDER);
                if (directory.exists() == true){
                    // local repo directory exists so should be able to just do a pull
                	Git git = null;
                    try{
                    	git = Git.open(new File(Storage_v2.this.m_application_directory + DEFAULT_LOCAL_REPO_FOLDER));
                        if (Storage_v2.this.repo_pull(git) == true){
                            // pull successful
                        	git.getRepository().close();
                            Storage_v2.this.m_listener.storage_get_files_complete(true, "");
                        }
                        else{
                            // pull not successful
                        	git.getRepository().close();
                            Storage_v2.this.m_listener.storage_get_files_complete(false, "Unable to pull repo from github");
                        }
                    } catch (IOException e){
                    	git.getRepository().close();
                        Storage_v2.this.m_listener.storage_get_files_complete(false, "Local files exists but unable to create git object");
                    }
                }
                else{
                    // local repo doesnt exists so need to clone
                    if (Storage_v2.this.repo_clone(Storage_v2.this.m_application_directory + DEFAULT_LOCAL_REPO_FOLDER) == true){
                        // clone successful
                        Storage_v2.this.m_listener.storage_get_files_complete(true, "");
                    }
                    else
                    {
                        // clone not successful
                        Storage_v2.this.m_listener.storage_get_files_complete(false, "Unable to clone repo from github");
                    }
                }
            }
        }).start();
    }

    public void save_files(){
        // start a thread to do the actual work
        new Thread(new Runnable() {
            @Override
            public void run() {
                // create a git object from the local repo
                // let just see if the folder exists
                File directory = new File(Storage_v2.this.m_application_directory + DEFAULT_LOCAL_REPO_FOLDER);
                if (directory.exists() == false){
                    // no local to push
                    Storage_v2.this.m_listener.storage_save_files_complete(false, "No local repo to push to github");
                    return;
                }

                // create the git object
                Git git = null;
                try {
                    git = Git.open(new File(Storage_v2.this.m_application_directory + DEFAULT_LOCAL_REPO_FOLDER));
                } catch (IOException e){
                    Storage_v2.this.m_listener.storage_save_files_complete(false, "Unable to create git object from local repo");
                    return;
                }
                
                // pull again to detect any changes first
                if (Storage_v2.this.repo_pull(git) == false){
                    git.getRepository().close();
                    Storage_v2.this.m_listener.storage_save_files_complete(false, "Unable to execute pre-push pull from github");
                    return;
                }

                // execute add
                if (Storage_v2.this.repo_add(git) == false){
                    git.getRepository().close();
                    Storage_v2.this.m_listener.storage_save_files_complete(false, "Unable to add file changes to local repo");
                    return;
                }

                // execute commit
                if (Storage_v2.this.repo_commit(git, DEFAULT_COMMIT_MESSAGE) == false){
                    git.getRepository().close();
                    Storage_v2.this.m_listener.storage_save_files_complete(false, "Unable to commit file changes to local repo");
                    return;
                }

                // push
                if (Storage_v2.this.repo_push(git) == false){
                    git.getRepository().close();
                    Storage_v2.this.m_listener.storage_save_files_complete(false, "Unable to push local repo to github");
                    return;
                }

                // success
                git.getRepository().close();
                Storage_v2.this.m_listener.storage_save_files_complete(true, "");
            }
        }).start();
    }

    private boolean repo_clone(String directory){
        // build the clone command
        CloneCommand cmd = Git.cloneRepository();
        cmd.setURI(DEFAULT_GITHUB_REPO);
        cmd.setCredentialsProvider(new UsernamePasswordCredentialsProvider(this.m_username, this.m_password));
        cmd.setDirectory(new File(directory));

        // execute the clone command
        Git git = null;
        try{
            git = cmd.call();
        } catch(GitAPIException e){
            return false;
        }
        git.getRepository().close();
        return true;
    }

    private boolean repo_pull(Git git){
        try{
            PullResult pr = git.pull().setCredentialsProvider(
                    new UsernamePasswordCredentialsProvider(this.m_username, this.m_password)).call();
            return pr.isSuccessful();
        } catch (GitAPIException e){
            return false;
        }
    }

    private boolean repo_add(Git git){
        try{
            DirCache cache = git.add().addFilepattern(".").call();
            return true;
        } catch (GitAPIException e){
            return false;
        }
    }

    private boolean repo_commit(Git git, String commit_message){
        try{
            RevCommit rc = git.commit().setMessage(commit_message).call();
            return true;
        } catch (GitAPIException e){
            return false;
        }
    }

    private boolean repo_push(Git git){
        try{
            Iterable<PushResult> push_results = git.push().setCredentialsProvider(
                    new UsernamePasswordCredentialsProvider(this.m_username, this.m_password)).call();
            return true;
        } catch (GitAPIException e){
            return false;
        }
    }

    public boolean delete_storage(){
        File directory = new File(this.m_application_directory + DEFAULT_LOCAL_REPO_FOLDER);
        return this.delete_directory(directory);
    }

    private boolean delete_directory(File directory){
        if (directory.isDirectory()){
            String[] children = directory.list();
            for (int i = 0; i < children.length; ++i){
                if (this.delete_directory(new File(directory, children[i])) == false){
                    return false;
                }
            }
        }
        return directory.delete();
    }
}
