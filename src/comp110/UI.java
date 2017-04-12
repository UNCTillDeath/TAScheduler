package comp110;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.jfoenix.*;
import com.jfoenix.controls.*;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TabPane.TabClosingPolicy;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class UI extends Application {

	private Stage _passwordStage;
	private Stage _availabilityStage;
	private Stage _scheduleStage;
	private Stage _swapStage;
	private JFXTabPane tabPane;
	private Tab availabilityTab;
	private Tab scheduleTab;
	private Tab swapTab;
	private int k;
	private GridPane _grid;
	private Controller _controller;
	private JFXTextField _usernameField;
	private JFXPasswordField _passwordField;
	private JFXButton _passwordSubmitButton;
	private JFXButton _saveAvailabilityButton;
	private JFXTextField _onyenField;
	private Employee _currentEmployee;
	private JFXButton _showSwapAvailabilityButton;
	private JFXButton _performSwapButton;
	private Schedule _schedule;
	private JFXListView<Label> list;
	private ScrollPane scroll;
	private GridPane schedulePane;
	private int _swapDay1;
	private int _swapDay2;
	private int _swapHour1;
	private int _swapHour2;
	private Employee _swapEmployee1;
	private Employee _swapEmployee2;
	private boolean _scheduleStageIsOpen;
	boolean _continueToSwap;
	private String _addOrDrop;
	private int _dropDay;
	private int _dropHour;
	private int _addDay;
	private int _addHour;
	private Employee _employeeToAddOrDrop;
	private BorderPane _addOrDropPane;
	private JFXButton _addOrDropButton;
	private JFXComboBox<String> _addOrDropJFXComboBox;


	@Override
	public void start(Stage primaryStage) throws Exception {
		// create controller
		_controller = new Controller(this);
		this.list = new JFXListView<Label>();

		// create the dialog to collect github username/password
		// and show it as the first thing
		_passwordStage = new Stage();
		_passwordStage.setTitle("Login to Github");
		_passwordStage.getIcons().add(new Image(getClass().getResource("karen.png").toString()));
		Group passwordGroup = new Group();
		StackPane wrapper = new StackPane();
		wrapper.getStyleClass().add("back");
		wrapper.getChildren().add(passwordGroup);
		
		Scene passwordScene = new Scene(wrapper);
			
		passwordScene.getStylesheets().add("comp110/style.css");
		_passwordStage.setScene(passwordScene);
		// prevent user from closing stage directly, we only want to close it
		// programmatically after authentication
		_passwordStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			@Override
			public void handle(WindowEvent event) {
				// if they close without entering password kill the program
				System.exit(0);
			}
		});
		VBox vbox = new VBox();
		vbox.setPadding(new Insets(10, 0, 0, 10));
		vbox.setSpacing(10);
		HBox hbox1 = new HBox();
		HBox hbox2 = new HBox();
		HBox hbox3 = new HBox();
		hbox1.setSpacing(10);
		hbox1.setAlignment(Pos.CENTER_LEFT);
		hbox2.setSpacing(10);
		hbox2.setAlignment(Pos.CENTER_LEFT);
		hbox3.setSpacing(10);
		hbox3.setAlignment(Pos.CENTER);

		_usernameField = new JFXTextField();
		_usernameField.getStyleClass().add("whitelabel");
		_passwordField = new JFXPasswordField();
		_passwordField.getStyleClass().add("whitelabel");
		// bind enter key to button press
		_passwordField.setOnKeyPressed((event) -> {
			if (event.getCode() == KeyCode.ENTER) {
				loginToGithub(null);
			}
		});
		Label usernameLabel = new Label("Username");
		usernameLabel.getStyleClass().add("whitelabel");
		Label passwordLabel = new Label("Password ");
		passwordLabel.getStyleClass().add("whitelabel");
		_passwordSubmitButton = new JFXButton("Login");
		_passwordSubmitButton.getStyleClass().add("button-raised");
		// bind enter key to button press
		_passwordSubmitButton.defaultButtonProperty().bind(_passwordSubmitButton.focusedProperty());
		_passwordSubmitButton.setOnAction(this::loginToGithub);
		hbox3.getChildren().add(_passwordSubmitButton);
		hbox1.getChildren().addAll(usernameLabel, _usernameField);
		hbox2.getChildren().addAll(passwordLabel, _passwordField);
		vbox.getChildren().addAll(hbox1, hbox2, hbox3);
		passwordGroup.getChildren().add(vbox);
		_passwordStage.sizeToScene();
		_passwordStage.setResizable(false);
		_availabilityStage = primaryStage;
		_availabilityStage.getIcons().add(new Image(getClass().getResource("karen.png").toString()));
		// load a blank stage behind password box so it looks pretty
		
		// password stage should be modal
		_passwordStage.initModality(Modality.APPLICATION_MODAL);
		_passwordStage.showAndWait();

		// code to call on exit of the application
		_availabilityStage.setOnCloseRequest(event -> {
			// call the controller cleanup
			_controller.cleanup();
			try {
				// give time for cleanup to complete
				Thread.sleep(2000);
			} catch (Exception e) {
				/* dont care about an exception here */}
		});
		_scheduleStageIsOpen = false;
	}

	private void loginToGithub(ActionEvent event) {
		// user has entered the username and password for github
		// send that info to the controller so it can pull the files
		_controller.uiUsernamePasswordCallback(new Credentials(_usernameField.getText(), _passwordField.getText()));

		// disable the password submit button until pull is done
		_passwordSubmitButton.setDisable(true);
		
		
	}
	public void setMainStage(){
		
		
		//Set up Stage
		Stage mainStage = new Stage();
		mainStage.getIcons().add(new Image(getClass().getResource("karen.png").toString()));
		mainStage.setTitle("TA Scheduler: COMP 110");
		
		//Set Main Group and Scene/
		Group mainGroup = new Group();
		Scene mainScene = new Scene(mainGroup);
		mainScene.getStylesheets().add("comp110/style.css");
		
		//Set up schedulePane
		
		//Set up Main Tab Pane
		JFXTabPane tabPane = new JFXTabPane();
		tabPane.getStyleClass().add("back");
		this.availabilityTab = new Tab();
		availabilityTab.setText("Availability");
		this.scheduleTab = new Tab();
		scheduleTab.setText("Schedule");
		this.swapTab = new Tab();
		swapTab.setText("Show Swaps");
		availabilityTab.getStyleClass().add("jfx-tab");
		swapTab.getStyleClass().add("jfx-tab");
		scheduleTab.getStyleClass().add("jfx-tab");
		tabPane.getTabs().add(availabilityTab);
		tabPane.getTabs().add(scheduleTab);
		tabPane.getTabs().add(swapTab);
		
		//Render Availability Tab
		availabilityTab.setContent(renderAvailabilityStage(null));
		
		mainGroup.getChildren().add(tabPane);
		mainStage.setScene(mainScene);
		mainStage.setTitle("COMP110 TA Availability");
		mainStage.sizeToScene();
		mainStage.setResizable(true);
		mainStage.show();
		
		this.schedulePane = new GridPane();
		this.scroll = new ScrollPane();
		scroll.setId("scroll");
		this.schedulePane = writeSchedule(_schedule);
		renderSwapStage();	
	}

	private BorderPane renderAvailabilityStage(Employee e) {
		BorderPane rootPane = new BorderPane();
		rootPane.getStyleClass().add("back");
		VBox topBox = new VBox(10);
		topBox.setPadding(new Insets(10, 10, 10, 10));
		topBox.setAlignment(Pos.CENTER);
		rootPane.setTop(topBox);

		HBox topBar = new HBox(5);
		// populate fields with employee
		if (e != null) {
			this._onyenField = new JFXTextField(e.getOnyen());
		} else {
			_onyenField = new JFXTextField("Enter onyen here");
			_onyenField.setOnKeyPressed((event) -> {
				if (event.getCode() == KeyCode.ENTER) {
					getAvailability(null);
				}
			});

		}
		topBar.getChildren().add(_onyenField);
		topBox.getChildren().add(topBar);

		// create button to get availability
		JFXButton getAvailabilityButton = new JFXButton("Get Availability");
		topBar.getChildren().add(getAvailabilityButton);
		getAvailabilityButton.setOnAction(this::getAvailability);

		// create button to show current schedule
		//JFXButton showScheduleButton = new JFXButton("Show Current Schedule");
		//showScheduleButton.setOnAction(this::requestScheduleButtonPressed);
		//topBar.getChildren().add(showScheduleButton);
		 												
		// create button to show the swap stage stuff
		//_showSwapAvailabilityButton = new JFXButton("Show Swaps");
		//_showSwapAvailabilityButton.setDisable(true);

		//_showSwapAvailabilityButton.setOnAction(this::buttonPressShowPotentialSwaps);
		//topBar.getChildren().add(_showSwapAvailabilityButton);

		// create button to do the swap stage stuff
		//_performSwapButton = new JFXButton("Swap");
		//_performSwapButton.setPrefWidth(54);

		//_performSwapButton.setDisable(true);

		//_performSwapButton.setOnAction(this::buttonPressSwap);

		// middle bar with employee demographic info and swap button
		HBox middleBar = new HBox(5);
		topBox.getChildren().add(middleBar);

		JFXTextField nameField = new JFXTextField();
		if (e != null) {
			nameField.setText(e.getName());
		} else {
			nameField.setText("Name");
		}
		nameField.textProperty().addListener((observable, oldValue, newValue) -> {
			if (e != null) {
				e.setName(newValue);
			}
		});
		JFXComboBox<String> genderDropdown = new JFXComboBox<String>();
		if (e != null) {
			genderDropdown.getSelectionModel().select(e.getIsFemale() ? "Female" : "Male");
		}
		genderDropdown.getItems().addAll("Male", "Female");
		genderDropdown.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				_currentEmployee.setIsFemale(newValue.equals("Female") ? true : false);
				_saveAvailabilityButton.setDisable(false);
			}
		});

		JFXComboBox<Integer> capacityDropdown = new JFXComboBox<Integer>();
		for (int i = 1; i <= 10; i++) {
			capacityDropdown.getItems().add(i);
		}
		if (e != null) {
			// have to -1 because it is pulling by index and list is zero
			// indexed
			capacityDropdown.getSelectionModel().select(e.getCapacity() - 1);
		}
		capacityDropdown.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Integer>() {
			@Override
			public void changed(ObservableValue<? extends Integer> observable, Integer oldValue, Integer newValue) {
				_currentEmployee.setCapacity(newValue);
				_saveAvailabilityButton.setDisable(false);
			}
		});

		JFXComboBox<String> levelDropdown = new JFXComboBox<String>();
		levelDropdown.getItems().addAll("1 - In 401", "2 - In 410/411", "3 - In Major");
		if (e != null) {
			// have to -1 because it is pulling by index and list is zero
			// indexed
			levelDropdown.getSelectionModel().select(e.getLevel() - 1);
		}
		levelDropdown.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				// grab the level # and use it to update employee
				_currentEmployee.setLevel(Integer.parseInt(newValue.split(" ")[0]));
				_saveAvailabilityButton.setDisable(false);
			}
		});

		middleBar.getChildren().addAll(nameField, genderDropdown, capacityDropdown, levelDropdown);

		// this grid contains the checkboxes to mark availability
		_grid = new GridPane();
		_grid.setGridLinesVisible(true);

		for (int day = 0; day < 8; day++) {
			// 8 to account for first column with hours
			if (day != 0) {
				// all labels are -1 offset
				Label dayLabel = new Label(Week.dayString(day - 1));
				dayLabel.setMaxWidth(Double.MAX_VALUE);
				dayLabel.setAlignment(Pos.CENTER);
				_grid.add(dayLabel, day, 0);
			}
			for (int hour = 0; hour < 12; hour++) {
				HBox box = new HBox();
				if (day == 0) {
					// if we are at hour column write out hour labels
					int time = (hour + 9) % 12;
					Label timeLabel = new Label(
							(time % 12 == 0 ? 12 : time) + " -- " + ((time + 1) % 12 == 0 ? 12 : time + 1));
					timeLabel.setTextAlignment(TextAlignment.CENTER);
					_grid.add(timeLabel, day, hour + 1);

				} else {
					TimedCheckBox check = new TimedCheckBox(day - 1, hour + 9);
					check.getStyleClass().add("jfx-check-box");
					if (e != null) {
						if (e.isAvailable(day - 1, hour + 9)) {
							// map day and hour onto our space
							check.setSelected(true);
							box.setBackground(new Background(new BackgroundFill(Color.GREEN, null, null)));
						}
					}
					// when checked handoff to handleCheck
					check.setOnAction(this::handleCheck);
					box.getChildren().add(check);
					box.setAlignment(Pos.CENTER);
					box.setMinHeight(30);
					box.setMinWidth(60);
					_grid.add(box, day, hour + 1); // +1 to account for header
													// row
				}
			}
		}
		_grid.setAlignment(Pos.CENTER);
		_grid.idProperty().set("availgrid");
		//_grid.setHgap(10); 
		
		
		_grid.setPadding(new Insets(10, 10, 10, 10));
		rootPane.setCenter(_grid);

		// create the save button
		HBox bottomBar = new HBox(5);
		bottomBar.setPadding(new Insets(10,10,10,10));
		_saveAvailabilityButton = new JFXButton("Save");
		_saveAvailabilityButton.setDisable(true);
		_saveAvailabilityButton.setPrefWidth(465);
		_saveAvailabilityButton.setOnAction(this::saveButtonPressed);
		bottomBar.getChildren().add(_saveAvailabilityButton);
		bottomBar.setAlignment(Pos.CENTER);
		rootPane.setBottom(bottomBar);
		
		return rootPane;
	}

	private void buttonPressShowPotentialSwaps(ActionEvent event) {
		// request controller to give it the information for the swaps
		_controller.uiRequestSwaps();
	}

	private void buttonPressSwap(ActionEvent event) {
		// start the perform swap stage
		this.renderPerformSwapStage();
	}

	private void renderPerformSwapStage() {
		Stage performSwapStage = new Stage();
	    performSwapStage.getIcons().add(new Image(getClass().getResource("karen.png").toString()));
		Group root = new Group();
		TabPane rootTabPane = new TabPane();
		rootTabPane.setTabClosingPolicy(TabClosingPolicy.UNAVAILABLE);
		BorderPane rootSwapPane = new BorderPane();
		Tab t1 = new Tab("Swap", rootSwapPane);
		Tab t2 = new Tab("Add/Drop", this.getAddDropPane());
		rootTabPane.getTabs().addAll(t1, t2);
		root.getChildren().add(rootTabPane);
		Scene scene = new Scene(root);
		scene.getStylesheets().add("comp110/style.css");
		performSwapStage.setScene(scene);
		Button saveButton = new Button("Swap!");
		HBox topBox = new HBox();
		HBox bottomBox = new HBox();
		// TOP BOX STUFF
		javafx.collections.ObservableList<String> dayList1 = FXCollections.observableArrayList(getDaysList());
		ListView<String> dayListView1 = new ListView<String>(dayList1);
		topBox.getChildren().add(dayListView1);
		ListView<String> hourListView1 = new ListView<String>();
		topBox.getChildren().add(hourListView1);
		_swapDay1 = 0;
		dayListView1.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				_swapDay1 = Week.dayInt(newValue);
				javafx.collections.ObservableList<String> hours = FXCollections
						.observableArrayList(getHoursList(newValue));
				// newValue is the new day
				hourListView1.setItems(hours);
			}
		});

		ListView<Label> personListView1 = new ListView<Label>();
		topBox.getChildren().add(personListView1);
		hourListView1.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				int newHour = Integer.parseInt(newValue.split(" ")[0]);
				if (newHour < 9) {
					// convert hour back into military time
					newHour += 12;
				}
				_swapHour1 = newHour;
				List<Label> scheduledEmployees = new ArrayList<Label>();
				for (Employee e : _schedule.getWeek().getShift(_swapDay1, newHour)) {
					Label toAdd = new Label(e.getName());
					if (toAdd.getText().equals(_currentEmployee.getName())) {
						toAdd.setTextFill(Color.RED);
					}
					scheduledEmployees.add(toAdd);
				}
				javafx.collections.ObservableList<Label> people = FXCollections.observableArrayList(scheduledEmployees);
				personListView1.setItems(people);
			}
		});

		personListView1.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Label>() {
			@Override
			public void changed(ObservableValue<? extends Label> observable, Label oldValue, Label newValue) {
				_swapEmployee1 = _schedule.getStaff().getEmployeeByName(newValue.getText());
				if (_swapEmployee1 != null && _swapEmployee2 != null) {
					saveButton.setDisable(false);
				}
			}
		});

		// BOTTOM BOX STUFF
		// everything is identical to the top box logic
		javafx.collections.ObservableList<String> dayList2 = FXCollections.observableArrayList(getDaysList());
		ListView<String> dayListView2 = new ListView<String>(dayList2);
		bottomBox.getChildren().add(dayListView2);
		ListView<String> hourListView2 = new ListView<String>();
		bottomBox.getChildren().add(hourListView2);
		_swapDay2 = 0;
		dayListView2.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				_swapDay2 = Week.dayInt(newValue);
				javafx.collections.ObservableList<String> hours = FXCollections
						.observableArrayList(getHoursList(newValue));
				hourListView2.setItems(hours);
			}
		});

		ListView<Label> personListView2 = new ListView<Label>();
		bottomBox.getChildren().add(personListView2);
		hourListView2.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				int newHour = Integer.parseInt(newValue.split(" ")[0]);
				if (newHour < 9) {
					newHour += 12;
				}
				_swapHour2 = newHour;
				List<Label> scheduledEmployees = new ArrayList<Label>();
				for (Employee e : _schedule.getWeek().getShift(_swapDay2, newHour)) {
					Label toAdd = new Label(e.getName());
					if (toAdd.getText().equals(_currentEmployee.getName())) {
						toAdd.setTextFill(Color.RED);
					}
					scheduledEmployees.add(toAdd);
				}
				javafx.collections.ObservableList<Label> people = FXCollections.observableArrayList(scheduledEmployees);
				personListView2.setItems(people);
			}
		});

		personListView2.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Label>() {
			@Override
			public void changed(ObservableValue<? extends Label> observable, Label oldValue, Label newValue) {
				_swapEmployee2 = _schedule.getStaff().getEmployeeByName(newValue.getText());
				if (_swapEmployee1 != null && _swapEmployee2 != null) {
					saveButton.setDisable(false);
				}
			}
		});

		saveButton.setDisable(true);
		// TODO figure out how to not hardcode this and just make it fill stage
		saveButton.setPrefWidth(744);
		saveButton.setOnAction(this::performSwap);
		rootSwapPane.setBottom(saveButton);

		dayListView1.setPrefHeight(250);
		dayListView2.setPrefHeight(250);
		hourListView1.setPrefHeight(250);
		hourListView2.setPrefHeight(250);
		personListView1.setPrefHeight(250);
		personListView2.setPrefHeight(250);

		rootSwapPane.setTop(topBox);
		rootSwapPane.setCenter(bottomBox);
		performSwapStage.sizeToScene();
		performSwapStage.setResizable(false);
		performSwapStage.setTitle("Perform Swap");
		performSwapStage.show();
	}

	private BorderPane getAddDropPane() {
		_addOrDropPane = new BorderPane();

		_addOrDropJFXComboBox = new JFXComboBox<String>();
		_addOrDropJFXComboBox.getSelectionModel().selectFirst();
		_addOrDropJFXComboBox.setPrefWidth(744);
		_addOrDropJFXComboBox.setItems(FXCollections.observableArrayList("Drop", "Add"));
		_addOrDropJFXComboBox.getSelectionModel().selectFirst(); // set Drop as
																// default
		_addOrDrop = "Drop";
		_addOrDropJFXComboBox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				_addOrDrop = newValue;
				if (_addOrDrop.equals("Add")) {
					_addOrDropPane.getChildren().clear();
					_addOrDropPane.setTop(_addOrDropJFXComboBox);
					_addOrDropPane.setBottom(_addOrDropButton);
					setupForAdd();
				} else {
					_addOrDropPane.getChildren().clear();
					_addOrDropPane.setTop(_addOrDropJFXComboBox);
					_addOrDropPane.setBottom(_addOrDropButton);
					setupForDrop();

				}
			}
		});
		_addOrDropPane.setTop(_addOrDropJFXComboBox);

		this.setupForDrop(); // first time through we want to setup for drop

		_addOrDropButton = new JFXButton("Save");
		_addOrDropButton.setPrefWidth(744);
		_addOrDropButton.setOnAction(this::addDropButtonPress);
		_addOrDropPane.setBottom(_addOrDropButton);
		return _addOrDropPane;
	}

	private void setupForDrop() {

		javafx.collections.ObservableList<String> dayList = FXCollections.observableArrayList(getDaysList());
		ListView<String> dayListView = new ListView<String>(dayList);
		_addOrDropPane.setLeft(dayListView);
		ListView<String> hourListView = new ListView<String>();
		_addOrDropPane.setCenter(hourListView);
		dayListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				_dropDay = Week.dayInt(newValue);
				javafx.collections.ObservableList<String> hours = FXCollections
						.observableArrayList(getHoursList(newValue));
				hourListView.setItems(hours);
			}
		});

		ListView<Label> personListView = new ListView<Label>();
		_addOrDropPane.setRight(personListView);
		hourListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				_dropHour = Integer.parseInt(newValue.split(" ")[0]);
				if (_dropHour < 9) {
					_dropHour += 12;
				}
				List<Label> scheduledEmployees = new ArrayList<Label>();
				for (Employee e : _schedule.getWeek().getShift(_dropDay, _dropHour)) {
					Label toAdd = new Label(e.getName());
					if (_currentEmployee != null && toAdd.getText().equals(_currentEmployee.getName())) {
						toAdd.setTextFill(Color.RED);
					}
					scheduledEmployees.add(toAdd);
				}
				javafx.collections.ObservableList<Label> people = FXCollections.observableArrayList(scheduledEmployees);
				personListView.setItems(people);
			}
		});
		personListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Label>() {
			@Override
			public void changed(ObservableValue<? extends Label> observable, Label oldValue, Label newValue) {
				_employeeToAddOrDrop = _schedule.getStaff().getEmployeeByName(newValue.getText());
			}
		});
	}

	private void setupForAdd() {
		javafx.collections.ObservableList<String> dayList = FXCollections.observableArrayList(getDaysList());
		ListView<String> dayListView = new ListView<String>(dayList);
		_addOrDropPane.setLeft(dayListView);
		ListView<String> hourListView = new ListView<String>();
		_addOrDropPane.setCenter(hourListView);
		dayListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				_addDay = Week.dayInt(newValue);
				javafx.collections.ObservableList<String> hours = FXCollections
						.observableArrayList(getHoursList(newValue));
				hourListView.setItems(hours);
			}
		});

		ListView<Label> personListView = new ListView<Label>();
		_addOrDropPane.setRight(personListView);
		hourListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				_addHour = Integer.parseInt(newValue.split(" ")[0]);
				if (_addHour < 9) {
					_addHour += 12;
				}
				List<Label> allEmployees = new ArrayList<Label>();
				for (Employee e : _schedule.getStaff()) {
					Label toAdd = new Label(e.getName());
					if (_currentEmployee != null && toAdd.getText().equals(_currentEmployee.getName())) {
						toAdd.setTextFill(Color.RED);
					}
					allEmployees.add(toAdd);
				}
				javafx.collections.ObservableList<Label> people = FXCollections.observableArrayList(allEmployees);
				personListView.setItems(people);
			}
		});
		personListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Label>() {
			@Override
			public void changed(ObservableValue<? extends Label> observable, Label oldValue, Label newValue) {
				_employeeToAddOrDrop = _schedule.getStaff().getEmployeeByName(newValue.getText());
			}
		});

	}

	private void addDropButtonPress(ActionEvent event) {

		if (_addOrDrop.equals("Add")) {
			_schedule.getWeek().getShift(_addDay, _addHour).add(_employeeToAddOrDrop);
		} else { // must be a drop
			_schedule.getWeek().getShift(_dropDay, _dropHour).remove(_employeeToAddOrDrop);

		}
		// if schedule is open we need to refresh the view
		if (_scheduleStageIsOpen) {
			this.renderScheduleStage(_schedule);
		}
		// tell controller to push changes
		if (_addOrDrop.equals("Add")) {
			_controller.uiRequestChangeSchedule(_schedule,
					_addOrDrop.toUpperCase() + ": " + _employeeToAddOrDrop + " " + Week.dayString(_addDay) + " "
							+ ((_addHour % 12) == 0 ? 12 : (_addHour % 12)) + " -- "
							+ (((_addHour + 1) % 12) == 0 ? 12 : ((_addHour + 1) % 12)));
		} else {
			_controller.uiRequestChangeSchedule(_schedule,
					_addOrDrop.toUpperCase() + ": " + _employeeToAddOrDrop + " " + Week.dayString(_dropDay) + " "
							+ ((_dropHour % 12) == 0 ? 12 : (_dropHour % 12)) + " -- "
							+ (((_dropHour + 1) % 12) == 0 ? 12 : ((_dropHour + 1) % 12)));
		}
	}

	private void performSwap(ActionEvent event) {
		String unavailableEmployee = "";
		if (!_swapEmployee1.isAvailable(_swapDay2, _swapHour2)) {
			unavailableEmployee = _swapEmployee1.getName();
		} else if (!_swapEmployee2.isAvailable(_swapDay1, _swapHour1)) {
			if (unavailableEmployee.equals("")) {
				unavailableEmployee = _swapEmployee2.getName();
			} else {
				unavailableEmployee += " and " + _swapEmployee2.getName();
			}
		}
		// show a warning that one of the employees is unavailable for the shift
		// they are swapping into
		_continueToSwap = true;
		if (unavailableEmployee != null) {
			Stage dialogueBox = new Stage();
		    dialogueBox.getIcons().add(new Image(getClass().getResource("karen.png").toString()));
			//dialogueBox.getIcons().add(new Image(getClass().getClassLoader().getResourceAsStream("karen.png")));
			Group root = new Group();
			Scene scene = new Scene(root);
			scene.getStylesheets().add("comp110/style.css");

			VBox vbox = new VBox();
			vbox.setPadding(new Insets(10, 0, 0, 10));
			vbox.setSpacing(10);

			HBox hbox1 = new HBox();
			hbox1.setSpacing(10);
			hbox1.setAlignment(Pos.CENTER);

			BorderPane pane = new BorderPane();

			Label text = new Label(unavailableEmployee
					+ " is listed as unavailable on their csv for the time you are trying to swap.\n");
			Label text2 = new Label("Are you sure you want to continue?");
			text.setTextAlignment(TextAlignment.CENTER);
			text2.setTextAlignment(TextAlignment.CENTER);
			pane.setTop(text);
			pane.setBottom(text2);
			BorderPane.setAlignment(text, Pos.CENTER);
			BorderPane.setAlignment(text2, Pos.CENTER);
			hbox1.getChildren().add(pane);

			HBox hbox2 = new HBox();
			hbox2.setSpacing(10);
			hbox2.setAlignment(Pos.CENTER);

			Button yes = new Button("Yes");
			yes.defaultButtonProperty().bind(yes.focusedProperty());
			yes.setOnAction((event1) -> {
				// if they are ok with this just close the box and continue
				dialogueBox.close();
			});
			Button no = new Button("No");
			no.setOnAction((event1) -> {
				// if not set the flag so we don't swap
				_continueToSwap = false;
				dialogueBox.close();
			});
			hbox2.getChildren().addAll(yes, no);

			vbox.getChildren().addAll(hbox1, hbox2);
			root.getChildren().add(vbox);
			dialogueBox.initModality(Modality.APPLICATION_MODAL);
			dialogueBox.setResizable(false);
			dialogueBox.setTitle("Unavailable Employee");
			dialogueBox.setScene(scene);
			dialogueBox.sizeToScene();
			dialogueBox.showAndWait();
			dialogueBox.setOnCloseRequest((event1) -> {
				event1.consume();
				_continueToSwap = false;
			});
		}
		// now check and see if we should proceed
		if (!_continueToSwap) {
			return;
		}
		// remove employees
		_schedule.getWeek().getShift(_swapDay1, _swapHour1).remove(_swapEmployee1);
		_schedule.getWeek().getShift(_swapDay2, _swapHour2).remove(_swapEmployee2);
		// add employees
		_schedule.getWeek().getShift(_swapDay1, _swapHour1).add(_swapEmployee2);
		_schedule.getWeek().getShift(_swapDay2, _swapHour2).add(_swapEmployee1);

		// if schedule is open we need to refresh the view
		if (_scheduleStageIsOpen) {
			this.renderScheduleStage(_schedule);
		}
		// tell controller to push changes
		_controller.uiRequestChangeSchedule(_schedule, "SWAPPED: " + _swapEmployee1.getName() + " "
				+ Week.dayString(_swapDay1) + " " + ((_swapHour1 % 12) == 0 ? 12 : (_swapHour1 % 12)) + " -- "
				+ (((_swapHour1 + 1) % 12) == 0 ? 12 : ((_swapHour1 + 1) % 12)) + " with " + _swapEmployee2.getName()
				+ " " + Week.dayString(_swapDay2) + " " + ((_swapHour2 % 12) == 0 ? 12 : (_swapHour2 % 12)) + " -- "
				+ (((_swapHour2 + 1) % 12) == 0 ? 12 : ((_swapHour2 + 1) % 12)));
	}

	// only return a list of days that have scheduled shifts in them
	private List<String> getDaysList() {
		List<String> daysList = new ArrayList<String>();
		for (int day = 0; day < 7; day++) {
			for (int hour = 0; hour < _schedule.getWeek().getShifts()[day].length; hour++) {
				// if at least one shift is populated
				if (_schedule.getWeek().getShifts()[day][hour].size() > 0 && !daysList.contains(Week.dayString(day))) {
					daysList.add(Week.dayString(day));
				}
			}

		}
		return daysList;
	}

	private List<String> getHoursList(String day) {
		List<String> hoursList = new ArrayList<String>();
		int minHour = -1;
		// find min
		for (int i = 0; i < _schedule.getWeek().getShifts()[Week.dayInt(day)].length; i++) {
			if (_schedule.getWeek().getShift(Week.dayInt(day), i).size() > 0) {
				minHour = i;
				break;
			}
		}
		// find max
		int maxHour = -1;
		for (int i = _schedule.getWeek().getShifts()[Week.dayInt(day)][minHour]
				.getHour(); i < _schedule.getWeek().getShifts()[Week.dayInt(day)].length; i++) {
			if (_schedule.getWeek().getShift(Week.dayInt(day), i).size() == 0) {
				break;
			}
			maxHour = i;
		}
		for (int i = _schedule.getWeek().getShifts()[Week.dayInt(day)][minHour]
				.getHour(); i <= _schedule.getWeek().getShifts()[Week.dayInt(day)][maxHour].getHour(); i++) {
			hoursList.add(((i % 12) == 0 ? 12 : (i % 12)) + " -- " + (((i + 1) % 12) == 0 ? 12 : ((i + 1) % 12)));
		}
		return hoursList;
	}

	private void getAvailability(ActionEvent event) {
		// ask the controller to load an employee availbility file based on the
		// onyen
		String onyen = this._onyenField.getText();
		if ((onyen.equals("") == true) || (onyen.equals("Enter onyen here")) == true) {
			// need to put an onyen in first
			this.displayMessage("Please first enter an onyen");
		} else {
			// ask controller to load it
			this._controller.uiRequestEmployeeAvailability(_onyenField.getText());
		}
	}

	private void renderScheduleStage(Schedule schedule) {
		_scheduleStageIsOpen = true;
		if (_schedule == null) {
			// only want to do this first time we get the schedule, otherwise UI
			// has most up to date
			// version of schedule and controller version is out of date
			_schedule = schedule;

		}
		BorderPane wrap = new BorderPane();
		wrap.setId("wrapper");
		wrap.setPrefSize(700, 500);
		list.getItems().clear();
		
		if(_currentEmployee != null){
			Label emp = new Label("Me (" + _currentEmployee.getName() + ")");
			emp.setId("Employee");
			list.getItems().add(emp);
			
		}
		Label week = new Label("Week");
		week.setId("week");
		list.getItems().add(week);
	
		for(int i = 0 ; i < 6 ; i++){
			Label day = new Label(Week.dayString(i));
			day.setId(Week.dayString(i));
			list.getItems().add(day);
			
		}
		list.getStyleClass().add("mylistview");
		list.setId("schedulelist");
		
		//schedulePane = writeSchedule(_schedule);
		for(Label i : list.getItems()){
			i.getStyleClass().add("schedule-labels");
		}
		
		
		
		Group scheduleGroup = new Group();
		
		list.setOnMouseClicked(this::scheduleChange);
		
		scroll.setPrefSize(500, 500);
		
		
		
		scroll.setContent(schedulePane);
		// this handles resize of nodes if user resizes stage
		wrap.setLeft(list);
		wrap.setRight(scroll);
		
		scheduleGroup.getChildren().add(wrap);
		scheduleTab.setContent(scheduleGroup);
		
	}


	private void renderSwapStage() {
		Group root = new Group();
		BorderPane rootPane = new BorderPane();
		root.getChildren().add(rootPane);
		
		javafx.collections.ObservableList<String> scheduledShifts = FXCollections
				.observableArrayList(this.getScheduledShifts(_schedule));
		ListView<String> scheduledShiftsListView = new ListView<String>(scheduledShifts);
		HBox listBox = new HBox();
		listBox.getChildren().add(scheduledShiftsListView);

		ListView<String> availableSwapsListView = new ListView<String>();
		HBox swapBox = new HBox();
		swapBox.getChildren().add(availableSwapsListView);
		_performSwapButton = new JFXButton("Swap");
		
		
		if(_currentEmployee == null) _performSwapButton.setDisable(true);
		else _performSwapButton.setDisable(false);
		_performSwapButton.setOnAction(this::buttonPressSwap);
		scheduledShiftsListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				// we sort the list of potential swaps by likelihood it will be
				// compatible
				javafx.collections.ObservableList<String> availableToSwap = FXCollections
						.observableArrayList(getOrderedPotentialSwaps(_schedule, Week.dayInt(newValue.split(" ")[0]),
								Integer.parseInt(newValue.split(" ")[1])));
				availableSwapsListView.setItems(availableToSwap);
			}
		});
		rootPane.setLeft(listBox);
		rootPane.setRight(swapBox);
		_performSwapButton.setAlignment(Pos.CENTER);
		_performSwapButton.getStyleClass().add("button-raised");
		rootPane.setBottom(_performSwapButton);
		
		swapTab.setContent(root);
	}

	// the hour gets passed in as regular time and needs to be converted to
	// military time
	private ArrayList<String> getOrderedPotentialSwaps(Schedule schedule, int day, int hour) {
		if (hour < 9) { // only hours from 9am to pm are valid so this works
			hour += 12;
		}
		ArrayList<String> swapCandidates = new ArrayList<String>();
		swapCandidates.addAll(schedule.getStaff().getWhoIsAvailable(day, hour));
		// remove yourself from the list
		swapCandidates.remove(_currentEmployee.getName());
		// now score each one
		Map<Employee, Double> scoredEmployees = new HashMap<Employee, Double>();
		for (String otherEmployeeName : swapCandidates) {
			Employee otherEmployee = schedule.getStaff().getEmployeeByName(otherEmployeeName);
			scoredEmployees.put(otherEmployee, 0.0);
			ArrayList<Shift> scheduledShifts = new ArrayList<Shift>();
			// get the shifts this employee is scheduled for
			for (int i = 0; i < schedule.getWeek().getShifts().length; i++) {
				for (int j = 0; j < schedule.getWeek().getShifts()[i].length; j++) {
					for (Employee e : schedule.getWeek().getShift(i, j)) {
						if (e.getName().equals(otherEmployee.getName())) {
							scheduledShifts.add(schedule.getWeek().getShift(i, j));
						}
					}
				}
			}
			// now see which shifts of other employee currentEmployee is
			// available for
			for (Shift shift : scheduledShifts) {
				if (_currentEmployee.isAvailable(shift.getDay(), shift.getHour())) {
					scoredEmployees.put(otherEmployee, scoredEmployees.get(otherEmployee) + 1);
				}
			}
			// divide by total number of shifts otherEmployee has
			scoredEmployees.put(otherEmployee, scoredEmployees.get(otherEmployee) / scheduledShifts.size());
		}
		// now that we have populated the map, sort by score write out the
		// candidates in order
		scoredEmployees = sortByValue(scoredEmployees);
		ArrayList<String> orderedSwapCandidates = new ArrayList<String>();
		for (Employee e : scoredEmployees.keySet()) {
			// only write out employees we have the potential to swap with
			if (scoredEmployees.get(e) > 0) {
				orderedSwapCandidates.add(e.getName() + " " + scoredEmployees.get(e));
			}
		}

		return orderedSwapCandidates;
	}

	// http://stackoverflow.com/questions/109383/sort-a-mapkey-value-by-values-java
	public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map) {
		List<Map.Entry<K, V>> list = new LinkedList<>(map.entrySet());
		Collections.sort(list, new Comparator<Map.Entry<K, V>>() {
			@Override
			public int compare(Map.Entry<K, V> o1, Map.Entry<K, V> o2) {
				return (o1.getValue()).compareTo(o2.getValue()) * -1;
			}
		});

		Map<K, V> result = new LinkedHashMap<>();
		for (Map.Entry<K, V> entry : list) {
			result.put(entry.getKey(), entry.getValue());
		}
		return result;
	}

	// returns strings in the proper label format of all the shifts
	// _currentEmployee is scheduled for
	private ArrayList<String> getScheduledShifts(Schedule schedule) {
		ArrayList<String> scheduledShifts = new ArrayList<String>();
		for (int day = 0; day < schedule.getWeek().getShifts().length; day++) {
			for (int hour = 0; hour < schedule.getWeek().getShifts()[day].length; hour++) {
				for (Employee e : schedule.getWeek().getShift(day, hour)) {
					if (_currentEmployee != null && e.getName().equals(_currentEmployee.getName())) {
						scheduledShifts.add(Week.dayString(day) + " " + (hour % 12 == 0 ? 12 : hour % 12) + " -- "
								+ ((hour + 1) % 12 == 0 ? 12 : (hour + 1) % 12));
					}
				}
			}
		}
		return scheduledShifts;
	}

	private GridPane writeSchedule(Schedule schedule) {
		GridPane weekPane = new GridPane();
		weekPane.setId("pane");
		weekPane.setAlignment(Pos.CENTER);
		weekPane.setGridLinesVisible(true);
		ArrayList<ArrayList<ArrayList<Employee>>> shifts = shiftsAsArray(schedule.getWeek());

		for (int day = 0; day < 7; day++) {
			// +1 for hour column
			weekPane.add(new Label(Week.dayString(day)), day + 1, 0);
		}

		int hourRow = 0;
		for (int hour = getEarliestHour(schedule.getWeek()); hour < getLatestHour(schedule.getWeek()); hour++) {
			Label dayLabel = new Label(
					(hour % 12 == 0 ? 12 : hour % 12) + " -- " + ((hour + 1) % 12 == 0 ? 12 : (hour + 1) % 12));
			dayLabel.setMaxWidth(Double.MAX_VALUE);
			dayLabel.setAlignment(Pos.CENTER);
			// +1 to account for day row
			weekPane.add(dayLabel, 0, hourRow + 1);

			int max = getMaxSize(hour, schedule.getWeek());

			for (int i = 0; i < max; i++) {
				for (int day = 0; day < 7; day++) {
					if (i < shifts.get(day).get(hour).size()) {
						Label scheduledEmployee = new Label(shifts.get(day).get(hour).get(i).toString());
						// highlight your name on the schedule
						if ((this._currentEmployee != null)
								&& (shifts.get(day).get(hour).get(i).toString().equals(_currentEmployee.getName()))) {
							scheduledEmployee.setTextFill(Color.RED);
						}
						// +1 to account for day row
						weekPane.add(scheduledEmployee, day + 1, hourRow + i + 1);
					}
				}
			}
			hourRow += max;
		}
		return weekPane;
	}
	
	private GridPane writeDay(Schedule schedule, int day){
		GridPane dayPane = new GridPane();
		dayPane.setId("pane");
		dayPane.setAlignment(Pos.CENTER);
		dayPane.setGridLinesVisible(true);
		ArrayList<ArrayList<ArrayList<Employee>>> shifts = shiftsAsArray(schedule.getWeek());
		dayPane.add(new Label(Week.dayString(day)), 2, 0);
		
		int counter = 0;
		int col = 0; 
		int hourRow = 0;
		for (int hour = getEarliestHour(schedule.getWeek()); hour < getLatestHour(schedule.getWeek()); hour++) {
			if(counter % 3 == 0){
				col += 2;
				hourRow = 1; 
			}
			Label dayLabel = new Label(
					(hour % 12 == 0 ? 12 : hour % 12) + " -- " + ((hour + 1) % 12 == 0 ? 12 : (hour + 1) % 12));
			dayLabel.setMaxWidth(Double.MAX_VALUE);
			dayLabel.setAlignment(Pos.CENTER);
			// +1 to account for day row
			dayPane.add(dayLabel, col, hourRow + 1);

			int max = shifts.get(day).get(hour).size();
			if(max == 0) max = 1;

			for (int i = 0; i < max; i++) {
					if (i < shifts.get(day).get(hour).size()) {
						Label scheduledEmployee = new Label(shifts.get(day).get(hour).get(i).toString());
						// highlight your name on the schedule
						if ((this._currentEmployee != null)
								&& (shifts.get(day).get(hour).get(i).toString().equals(_currentEmployee.getName()))) {
							scheduledEmployee.setTextFill(Color.RED);
						}
						// +1 to account for day row
						dayPane.add(scheduledEmployee, col + 1, hourRow + i + 1);
					}
					
			}
			hourRow += max;
			counter++;
		}
		return dayPane;
	}
	
	private GridPane writeEmployee(Schedule schedule, Employee e){
		GridPane empPane = new GridPane();
		empPane.setId("pane");
		empPane.setAlignment(Pos.CENTER);
		empPane.setGridLinesVisible(true);
		ArrayList<ArrayList<ArrayList<Employee>>> shifts = shiftsAsArray(schedule.getWeek());
		int col = 0; 
		for (int day = 0; day < 7; day++) {
			int dayShifts = 1; 
			if(isScheduled(e, schedule, day)){
				empPane.add(new Label(Week.dayString(day)), col, 0);
				for(int hour = getEarliestHour(schedule.getWeek()); hour < getLatestHour(schedule.getWeek()); hour++){
					for(int i = 0; i < shifts.get(day).get(hour).size(); i++){
						if(shifts.get(day).get(hour).get(i).toString().equals(e.getName())){
							empPane.add(new Label((hour % 12 == 0 ? 12 : hour % 12) + " -- " + ((hour + 1) % 12 == 0 ? 12 : (hour + 1) % 12)), col, dayShifts); 
							dayShifts++;
						}
				}
			}
				col++;
			}
		}
		return empPane;
	}
	
	private boolean isScheduled(Employee e, Schedule schedule, int day){
		ArrayList<ArrayList<ArrayList<Employee>>> shifts = shiftsAsArray(schedule.getWeek());
			for(int hour = getEarliestHour(schedule.getWeek()); hour < getLatestHour(schedule.getWeek()); hour++){
				for(int i = 0; i < shifts.get(day).get(hour).size(); i++){
					if(shifts.get(day).get(hour).get(i).toString().equals(e.getName())){
						return true; 
					}
			}
		}
			return false; 
	}
	// gets the size of the longest shift for any given hour across all days
	private static int getMaxSize(int hour, Week week) {
		int max = 0;
		for (int day = 0; day < 7; day++) {
			if (week.getShift(day, hour).size() > max) {
				max = week.getShift(day, hour).size();
			}
		}
		return max;
	}

	// gets earliest scheduled hour in the week
	private static int getEarliestHour(Week week) {
		int min = 10000;
		for (int day = 0; day < 7; day++) {
			for (int hour = 0; hour < 24; hour++) {
				if (week.getShift(day, hour).size() > 0) {
					if (hour < min) {
						min = hour;
					}
				}
			}
		}
		return min;
	}

	// gets latest scheduled hour in the week
	private static int getLatestHour(Week week) {
		int max = 0;
		for (int day = 0; day < 7; day++) {
			for (int hour = 0; hour < 24; hour++) {
				if (week.getShift(day, hour).size() > 0) {
					if (hour > max) {
						max = hour;
					}
				}
			}
		}
		return max + 1;
	}

	// turn the week object into 3D array to make it easier to write out
	private static ArrayList<ArrayList<ArrayList<Employee>>> shiftsAsArray(Week week) {
		ArrayList<ArrayList<ArrayList<Employee>>> shifts = new ArrayList<ArrayList<ArrayList<Employee>>>();
		for (int day = 0; day < 7; day++) {
			shifts.add(new ArrayList<ArrayList<Employee>>());
			for (int hour = 0; hour < 24; hour++) {
				shifts.get(day).add(new ArrayList<Employee>());
				for (Employee e : week.getShift(day, hour)) {
					shifts.get(day).get(hour).add(e);
				}
			}
		}

		return shifts;
	}

	public void displayAvailable(Employee e) {
		// called from the controller when an Employee object is ready for
		// display
		_currentEmployee = e;
	
		availabilityTab.setContent(renderAvailabilityStage(_currentEmployee));
		renderScheduleStage(_schedule);
		renderSwapStage();	
		
	}

	public void displaySchedule(Schedule schedule) {
		

		// once we have the schedule we can enable the other buttons
		// TODO perhaps changes this so that schedule is available from the
		// start
		//this._showSwapAvailabilityButton.setDisable(false);
		//this._performSwapButton.setDisable(false);
		this._schedule = schedule;
		
		
	}

	public void displayPossibleSwaps() {
		_swapStage = new Stage();
		_swapStage.getIcons().add(new Image(getClass().getResource("karen.png").toString()));
		renderSwapStage();
		_swapStage.show();
	}

	public Credentials getUsernamePassword() {
		return new Credentials(_usernameField.getText(), _passwordField.getText());
	}

	public void handleCheck(ActionEvent event) {
		_saveAvailabilityButton.setDisable(false);
		TimedCheckBox check = (TimedCheckBox) event.getSource();
		check.getStyleClass().add("jfx-check-box");
		
		int[][] updatedAvailability = _currentEmployee.getAvailability();
		System.out.println("Day: " + check.getDay() + " Hour: " + check.getHour());
		HBox parent = (HBox) check.getParent();
		if (check.isSelected()) {
			parent.setBackground(new Background(new BackgroundFill(Color.GREEN, null, null)));
			updatedAvailability[check.getDay()][check.getHour()] = 1;
		} else {
			parent.setBackground(new Background(new BackgroundFill(Color.RED, null, null)));
			updatedAvailability[check.getDay()][check.getHour()] = 0;

		}
		_currentEmployee.setAvailability(updatedAvailability);
	}

	public void displayMessage(String message) {
		Alert alert = new Alert(Alert.AlertType.ERROR);
		alert.setHeaderText("Error");
		alert.setContentText(message);
		alert.showAndWait();
	}

	// called whenever someone inputs an invalid onyen
	public void createNewEmployeeCSV(String onyen) {
		Stage dialogueBox = new Stage();
		dialogueBox.getIcons().add(new Image(getClass().getResource("karen.png").toString()));
		Group root = new Group();
		Scene scene = new Scene(root);
		scene.getStylesheets().add("comp110/style.css");

		VBox vbox = new VBox();
		vbox.setPadding(new Insets(10, 0, 0, 10));
		vbox.setSpacing(10);

		HBox hbox1 = new HBox();
		hbox1.setSpacing(10);
		hbox1.setAlignment(Pos.CENTER_LEFT);

		Label text = new Label(onyen + " does not have an availability object yet. Would you like to create one?");
		hbox1.getChildren().add(text);

		HBox hbox2 = new HBox();
		hbox2.setSpacing(10);
		hbox2.setAlignment(Pos.CENTER);

		Button yes = new Button("Yes");
		yes.defaultButtonProperty().bind(yes.focusedProperty());
		yes.setOnAction((event) -> {
			_currentEmployee = new Employee("", _onyenField.getText(), 0, false, 0, new int[7][24]);
			this.renderAvailabilityStage(_currentEmployee);
			dialogueBox.close();
		});
		Button no = new Button("No");
		no.setOnAction((event) -> {
			dialogueBox.close();
			_onyenField.setText("Enter onyen here");
			this.displayMessage("Try again, enter a valid onyen");
		});
		hbox2.getChildren().addAll(yes, no);

		vbox.getChildren().addAll(hbox1, hbox2);
		root.getChildren().add(vbox);
		dialogueBox.initModality(Modality.APPLICATION_MODAL);
		dialogueBox.setResizable(false);
		dialogueBox.setTitle("Invalid onyen");
		dialogueBox.setScene(scene);
		dialogueBox.sizeToScene();
		dialogueBox.showAndWait();

	}

	public void githubPullResult(boolean success, String message) {
		if (success == true) {
			// login was successful
			// move to next stage
			// display a null employee because we dont have the onyen yet
			_controller.uiRequestSchedule();
			setMainStage();
			// can close the password stage
			_passwordStage.close();
		} else {
			// pull failed...highly likely wrong username and password
			this.displayMessage("Unable to pull files from github");
			// renable the submit button
			_passwordSubmitButton.setDisable(false);
		}
	}

	public void githubPushResult(boolean success, String message) {
		if (success == true) {
			// save was successful
		} else {
			// push failed
			this.displayMessage("Unable to push files to github");
		}
	}

	public void saveButtonPressed(ActionEvent e) {
		// need to send to controller to save the current modified Employee
		// object
		if (_currentEmployee != null) {
			_controller.uiRequestSaveAvailability(_currentEmployee,
					"AVAILABILITY CHANGE: " + _currentEmployee.getName());
		}
	}

	public void requestScheduleButtonPressed(ActionEvent e) {
		// ask the controller for the schedule
		_controller.uiRequestSchedule();
	}
	
	public void setSchedule(Schedule s){
		_schedule = s;
	}
	
	public void scheduleChange(MouseEvent e){
		schedulePane.getChildren().clear();
		if(list.getSelectionModel().getSelectedItem() == null) return;
		switch(list.getSelectionModel().getSelectedItem().getId()){
	 	case "week":
        	schedulePane = writeSchedule(_schedule);
        	break;
        case "Employee":
        	if(_currentEmployee != null){
        		schedulePane = writeEmployee(_schedule, _currentEmployee);
        	}
        	break;
        case "Sunday":
        	schedulePane = writeDay(_schedule, 0);
        	renderScheduleStage(_schedule);
        	break;
        case "Monday":
        	schedulePane = writeDay(_schedule, 1);
        	renderScheduleStage(_schedule);
        	break;
        case "Tuesday":
        	schedulePane = writeDay(_schedule, 2);
        	renderScheduleStage(_schedule);
        	break;
        case "Wednesday":
        	schedulePane = writeDay(_schedule, 3);
        	renderScheduleStage(_schedule);
        	break;
        case "Thursday":
        	schedulePane = writeDay(_schedule, 4);
        	renderScheduleStage(_schedule);
        	break;
        case "Friday":
        	schedulePane = writeDay(_schedule, 5);
        	renderScheduleStage(_schedule);
        	break;
        default:
        	schedulePane = writeSchedule(_schedule);
        	break;
		
	}
		renderScheduleStage(_schedule);
}
	
}