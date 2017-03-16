package comp110;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import javafx.application.Platform;
import javafx.event.ActionEvent;

public class Controller implements Storage_v2.Storage_v2Listener {

	private UI _ui;
	private Storage_v2 _storage;
	private Parser _parser;
	private Schedule _schedule; 

	public Controller(UI ui) {

		//initialize UI, storage, and parser
		_storage = new Storage_v2(this, ".");
		_parser = new Parser();
		_ui = ui;
		this._schedule = null;
	}

	public void cleanup() {
		_storage.delete_storage();
	}
	
	public void uiUsernamePasswordCallback(Credentials credentials) {
		// save username and password for github
		_storage.set_username(credentials.getUsername());
		_storage.set_password(credentials.getPassword());

		// pull files from github
		_storage.get_files();
	}

	
    public void storage_get_files_complete(boolean success, String message){
		// let the ui know
		Platform.runLater(() -> _ui.githubPullResult(success, message));    	
    }
    
    public void storage_save_files_complete(boolean success, String message){
		// let the ui know
		Platform.runLater(() -> _ui.githubPushResult(success, message));    	
    }

	public void uiRequestSchedule() {
		/*************************************************
		 * should be calling the parseSchedule function on the parse
		 * and returning the Schedule it creates.  That function is not
		 * yet done.  so change this up when it is complete
		 **************************************************/
		this._schedule = _parser.parseSchedule(_storage.get_schedule_json_filename());
		
		// tell the ui to show the schedule...this can/will be null if there was an exception
		// ui needs to be ready to handle null schedule
		Platform.runLater(() -> _ui.displaySchedule(this._schedule));
	}

	public void uiRequestEmployeeAvailability(String onyen) {
		// parse the employee
		Employee employee = _parser.parseEmployee(_storage.get_availability_csv_filename_from_onyen(onyen));
		
		if (employee == null){
			_ui.createNewEmployeeCSV(onyen);
			//_ui.displayMessage("Unable to pull availability for " + onyen);
		}
		else {
			//display available object on ui
			Platform.runLater(() -> _ui.displayAvailable(employee));	
		}
	}

	public void uiRequestSwaps() {
		// load the schedule and sent it to ui
		/*************************************************
		 * should be calling the parseSchedule function on the parse
		 * and returning the Schedule it creates.  That function is not
		 * yet done.  so change this up when it is complete
		 **************************************************/
		if (this._schedule != null){
			// schedule already loaded so just return it
			Platform.runLater(() -> _ui.displayPossibleSwaps(this._schedule));
			return;
		}
		
		// not yet loaded so do so
		this._schedule = null; //_parser.parseSchedule(_storage.getFilePathToSchedule());
		FileInputStream fileIn = null;
		ObjectInputStream in = null;
		try {
			// open the filestreams and read the object
			fileIn = new FileInputStream(_storage.get_schedule_json_filename());
			in = new ObjectInputStream(fileIn);
			this._schedule = (Schedule) in.readObject();
		} catch (Exception e) {
			// probably count open schedule for some reason
			// tell the ui to display an appropriate message
			e.printStackTrace();
			_ui.displayMessage("Unable to load Schedule file");
		} finally{
			// close the file streams
			// putting it in finally ensures even on exception it gets done
			try{
				if (in != null){
					in.close();
				}
			} catch (Exception e){}
			try{
				if (fileIn != null){
					fileIn.close();
				}
			} catch (Exception e){}
		}
		if (this._schedule == null){
			// error loading schedule
			this._ui.displayMessage("Unable to load schedule");
		}
		else {
			// tell ui to do the swaps
			Platform.runLater(() -> _ui.displayPossibleSwaps(this._schedule));
		}
	}

	public void uiRequestSaveAvailability(Employee employee) {
		// need to tell parser to save this employee object and what filename to save it as
		if (employee == null){
			// unable to save
			this._ui.displayMessage("Unable to save employee object: employee is null");
			return;
		}
		
		String filename = this._storage.get_availability_csv_filename_from_onyen(employee.getOnyen());
		try{
			// have the parser write out the file
			this._parser.writeFile(employee, filename);
			// have storage push to the repo
			this._storage.save_files();
		} catch (IOException e){
			// unable to save
			this._ui.displayMessage("Unable to save employee object: " + e.getMessage());
		}	
	}
}
