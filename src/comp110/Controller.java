package comp110;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import javafx.application.Platform;
import javafx.event.ActionEvent;

public class Controller {

	private UI _ui;
	private Storage _storage;
	private Parser _parser;
	private String _username;
	private String _password;
	private Employee _employee;
	private Schedule _schedule;


	public Controller(UI ui) {

		//initialize UI, storage, and parser
		_storage = new Storage(this);
		_parser = new Parser();
		_ui = ui;
	}

	public void cleanup() {
		_storage.cleanup();
	}
	
	public void uiUsernamePasswordCallback(Credentials credentials) {
		// save username and password for github
		_username = credentials.getUsername();
		_password = credentials.getPassword();
		_storage.setUsername(_username);
		_storage.setPassword(_password);

		// pull files from github
		_storage.pullFiles();
	}

	public void storagePullCompleteCallback(boolean success, String message) {
		if (success == false) {
			// need to display some kind of message saying it failed
			// the message string should contain information on why it failed
		}
	}
	
	public void storagePushCompleteCallback(boolean success, String message) {
		if (success == false){
			// need to display some kind of message saying it failed
			// the message string should contain information on why it failed
		}
	}

	public void uiRequestSchedule(ActionEvent event) {
		try {
			FileInputStream fileIn = new FileInputStream("testData/schedule.ser");
			ObjectInputStream in = new ObjectInputStream(fileIn);
			_schedule = (Schedule) in.readObject();
			in.close();
			fileIn.close();
		} catch (IOException e) {
			e.printStackTrace();
			return;
		} catch (ClassNotFoundException c) {
			System.out.println("Class not found");
			c.printStackTrace();
			return;
		}
		Platform.runLater(() -> _ui.displaySchedule(_schedule));
	}

	public void uiRequestEmployeeAvailability(String onyen) {
		// parse the employee
		_employee = _parser.parseEmployee(_storage.getFilesPath() + File.separator + "data" + File.separator + "spring-17" + File.separator + "staff" + File.separator + onyen + ".csv");

		//display available object on ui
		Platform.runLater(() -> _ui.displayAvailable(_employee));
	}

	public void uiRequestSwaps() {
		Platform.runLater(() -> _ui.displayPossibleSwaps(_schedule));
	}

	public void uiRequestSaveAvailability(ActionEvent event) {

	}
	
	public Schedule getSchedule(){
	  return _schedule;
	}
}
