package comp110;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;

public class Controller {

	private UI _ui;
	private Storage _storage;
	private Parser _parser;
	private String _username;
	private String _password;
	private String _fileFromStorage;
	private boolean _filesPulled = false;
	private Employee _employee;
	private Credentials credentials;
	private boolean _done = false;


	public Controller(UI ui) {

		//initialize UI, storage, and parser
		_storage = new Storage(this);
		_parser = new Parser();
		
		

		_ui = ui;

		//get username and password from UI ... 





		//get available object from parser

	}

	public void run() {

	}
	
	public void setDone(boolean done) {
		_done = done;
		if (done) {
			_storage.cleanup();
		}
	}
	
	public boolean getDone() {
		return _done;
	}

	public void uiUsernamePasswordCallback(Credentials credentials) {
		_username = credentials.getUsername();
		_password = credentials.getPassword();
		_storage.setUsername(_username);
		_storage.setPassword(_password);


		//pull files
		_storage.pullFiles();
	}

	public void storagePullCompleteCallback(boolean success, String message) {
		if (success) {
			_employee = _parser.parseEmployee(_storage.getFilesPath() + File.separator + "data" + File.separator + "spring-17" + File.separator + _username + ".csv");
		}

		//display available object on ui
		Platform.runLater(() ->_ui.displayAvailable(_employee));
	}
	
	public void storagePushCompleteCallback(boolean success, String message) {
		
	}


	public void uiRequestSchedule(ActionEvent event) {
		Schedule schedule = null;
		try {
			FileInputStream fileIn = new FileInputStream("testData/schedule.ser");
			ObjectInputStream in = new ObjectInputStream(fileIn);
			schedule = (Schedule) in.readObject();
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
		_ui.displaySchedule(schedule);
	}

	public void uiRequestEmployeeAvailability(ActionEvent event) {

	}


	public void uiRequestSwaps(ActionEvent event) {
		_ui.displayPossibleSwaps(null);
	}

	public void uiRequestSaveAvailability(ActionEvent event) {

	}

	public void handleCheck(ActionEvent event) {
		CheckBox check = (CheckBox) event.getSource();
		HBox parent = (HBox) check.getParent();
		if (check.isSelected()) {
			parent.setBackground(new Background(new BackgroundFill(Color.GREEN, null, null)));
		} else {
			parent.setBackground(new Background(new BackgroundFill(Color.RED, null, null)));
		}
	}
}
