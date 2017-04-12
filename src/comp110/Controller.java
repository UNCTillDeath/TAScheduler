package comp110;

import java.io.IOException;
import javafx.application.Platform;

public class Controller implements Storage.StorageListener {

	private UI _ui;
	private Storage _storage;
	private Parser _parser;
	private Schedule _schedule; 

	public Controller(UI ui) {

		//initialize UI, storage, and parser
		this._storage = new Storage(this, ".");
		_parser = new Parser(ui);
		_ui = ui;
		this._schedule = null;
	}

	public void cleanup() {
		this._storage.delete_storage();
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
		this._schedule = _parser.parseSchedule(_storage.get_schedule_json_filename(), _storage.get_path_to_onyen_csv_directory());
		_ui.setSchedule(_schedule);
		
    }
    
    public void storage_save_files_complete(boolean success, String message){
		// let the ui know
		Platform.runLater(() -> _ui.githubPushResult(success, message));    	
    }

	public void uiRequestSchedule() {
		//this._schedule = _parser.parseSchedule(_storage.get_schedule_json_filename(), _storage.get_path_to_onyen_csv_directory());
		
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
			Platform.runLater(() -> _ui.displayPossibleSwaps());

	}

	public void uiRequestSaveAvailability(Employee employee, String commit_message) {
		// need to tell parser to save this employee object and what filename to save it as
		if (employee == null){
			// unable to save
			_ui.displayMessage("Unable to save employee object: employee is null");
			return;
		}
		
		String filename = this._storage.get_availability_csv_filename_from_onyen(employee.getOnyen());
		try{
			// have the parser write out the file
			this._parser.writeFile(employee, filename);
			// have storage push to the repo
			this._storage.save_files(commit_message);
		} catch (IOException e){
			// unable to save
			_ui.displayMessage("Unable to save employee object: " + e.getMessage());
		}	
	}
	
	public void uiRequestChangeSchedule(Schedule schedule, String commit_message){
		_schedule = schedule; //not strictly needed I don't think but nice to keep things in sync
		_parser.writeScheduleToJson(schedule, _storage.get_schedule_json_filename());
		_storage.save_files(commit_message);
	}
}