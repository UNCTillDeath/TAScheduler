package comp110;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Callback;

public class UI extends Application {

  private Stage         _passwordStage;
  private Stage         _availabilityStage;
  private Stage         _scheduleStage;
  private Stage         _swapStage;
  private GridPane      _grid;
  private Controller    _controller;
  private TextField     _usernameField;
  private PasswordField _passwordField;
  private TextField     _onyenField;
  private Employee      _currentEmployee;
  private Button        _showSwapAvailabilityButton;
  private Button _performSwapButton;
  private Schedule _schedule;
  private int _currentDay1;
  private int _currentDay2;

  @Override
  public void start(Stage primaryStage) throws Exception {
    _controller = new Controller(this);
    _passwordStage = new Stage();
    Group passwordGroup = new Group();
    Scene passwordScene = new Scene(passwordGroup);
    _passwordStage.setScene(passwordScene);

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

    _usernameField = new TextField();
    _passwordField = new PasswordField();
    Label usernameLabel = new Label("Username");
    Label passwordLabel = new Label("Password ");
    Button submitButton = new Button("Login");
    submitButton.setOnAction(this::loginToGithub);
    hbox3.getChildren().add(submitButton);
    hbox1.getChildren().addAll(usernameLabel, _usernameField);
    hbox2.getChildren().addAll(passwordLabel, _passwordField);
    vbox.getChildren().addAll(hbox1, hbox2, hbox3);
    passwordGroup.getChildren().add(vbox);
    _passwordStage.sizeToScene();
    _passwordStage.setResizable(false);
    _passwordStage.show();

    _availabilityStage = primaryStage;
    _availabilityStage.setOnCloseRequest(event -> {
      _controller.cleanup();
      try {
        // give time for cleanup to complete
        Thread.sleep(2000);
      } catch (Exception e) {
        /* dont care about an exception here */}
    });
  }

  private void loginToGithub(ActionEvent event) {
    _controller.uiUsernamePasswordCallback(
        new Credentials(_usernameField.getText(), _passwordField.getText()));
    displayAvailable(null);
    // _passwordStage.close();
  }

  private void renderAvailabilityStage(Employee e) {
    Group availabilityRoot = new Group();
    Scene availabilityScene = new Scene(availabilityRoot);
    BorderPane rootPane = new BorderPane();

    HBox topBar = new HBox();
    _onyenField = new TextField("Enter onyen here");
    topBar.getChildren().add(_onyenField);
    rootPane.setTop(topBar);

    Button pullScheduleButton = new Button("Pull Schedule");
    topBar.getChildren().add(pullScheduleButton);
    pullScheduleButton.setOnAction(this::onyenSubmit);

    Button showScheduleButton = new Button("Show Current Schedule");
    showScheduleButton.setOnAction(_controller::uiRequestSchedule);
    topBar.getChildren().add(showScheduleButton);

    _showSwapAvailabilityButton = new Button("Show Swaps");
    if (e == null) { // only do this first time we paint the scene
      _showSwapAvailabilityButton.setDisable(true);
    }
    _showSwapAvailabilityButton.setOnAction(this::buttonPressShowPotentialSwaps);
    topBar.getChildren().add(_showSwapAvailabilityButton);
    
    _performSwapButton = new Button("Swap");
    if (e == null){
      _performSwapButton.setDisable(true);
    }
    _performSwapButton.setOnAction(this::buttonPressSwap);
    topBar.getChildren().add(_performSwapButton);

    _grid = new GridPane();
    _grid.setGridLinesVisible(true);

    for (int day = 0; day < 8; day++) { // 8 to account for first column with
                                        // hours
      if (day != 0) {
        Label dayLabel = new Label(Week.dayString(day - 1));
        dayLabel.setMaxWidth(Double.MAX_VALUE);
        dayLabel.setAlignment(Pos.CENTER);
        _grid.add(dayLabel, day, 0);
      }
      for (int hour = 0; hour < 12; hour++) {
        HBox box = new HBox();
        if (day == 0) {
          int time = (hour + 9) % 12;
          Label timeLabel = new Label((time % 12 == 0 ? 12 : time) + " -- "
              + ((time + 1) % 12 == 0 ? 12 : time + 1));
          timeLabel.setMaxWidth(Double.MAX_VALUE);
          timeLabel.setAlignment(Pos.CENTER);
          box.getChildren().add(timeLabel);

        } else {
          CheckBox check = new CheckBox();
          if (e != null) {
            if (e.isAvailable(day - 1, hour + 9)) { // map day and hour onto our
                                                    // space
              check.setSelected(true);
              box.setBackground(
                  new Background(new BackgroundFill(Color.GREEN, null, null)));
            }
          }
          check.setOnAction(this::handleCheck);
          box.getChildren().add(check);
          box.setAlignment(Pos.CENTER);
          box.setMinHeight(30);
          box.setMinWidth(60);
        }
        _grid.add(box, day, hour + 1); // +1 to account for header row
      }
    }
    rootPane.setCenter(_grid);

    HBox bottomBar = new HBox();
    Button saveButton = new Button("Save");
    saveButton.setPrefWidth(465);
    saveButton.setOnAction(_controller::uiRequestSaveAvailability);
    bottomBar.getChildren().add(saveButton);
    rootPane.setBottom(bottomBar);

    availabilityRoot.getChildren().add(rootPane);
    _availabilityStage.setScene(availabilityScene);
    _availabilityStage.setTitle("COMP110 TA Availability");
    _availabilityStage.sizeToScene();
    _availabilityStage.setResizable(false);

  }

  private void buttonPressShowPotentialSwaps(ActionEvent event) {
    _controller.uiRequestSwaps();
  }
  
  private void buttonPressSwap(ActionEvent event){
    this.renderPerformSwapStage();
  }
  
  private void renderPerformSwapStage(){
    Stage performSwapStage = new Stage();
    Group root = new Group();
    BorderPane rootPane = new BorderPane();
    root.getChildren().add(rootPane);
    Scene scene = new Scene(root);
    performSwapStage.setScene(scene);
    HBox topBox = new HBox();
    HBox bottomBox = new HBox();
    //TOP BOX STUFF
    javafx.collections.ObservableList<String> dayList1 = FXCollections
        .observableArrayList(getDaysList());
    ListView<String> dayListView1 = new ListView<String>(dayList1);
    topBox.getChildren().add(dayListView1);
    ListView<String> hourListView1 = new ListView<String>();
    topBox.getChildren().add(hourListView1);
    _currentDay1 = 0;
    dayListView1.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
      @Override
      public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
        _currentDay1 = Week.dayInt(newValue);
          javafx.collections.ObservableList<String> hours = FXCollections
              .observableArrayList(getHoursList(newValue)); //newValue is the new day
          hourListView1.setItems(hours);
      }
  });

    ListView<String> personListView1 = new ListView<String>();
    topBox.getChildren().add(personListView1);
    hourListView1.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
      @Override
      public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
          int newHour = Integer.parseInt(newValue.split(" ")[0]);
          if (newHour < 9){
            newHour += 12;
          }
          List<String> scheduledEmployees = new ArrayList<String>();
          for (Employee e : _schedule.getWeek().getShift(_currentDay1, newHour)){
            scheduledEmployees.add(e.getName());
          }
          javafx.collections.ObservableList<String> people = FXCollections
              .observableArrayList(scheduledEmployees);
          personListView1.setItems(people);
      }
  });
    
    //BOTTOM BOX STUFF
    
    javafx.collections.ObservableList<String> dayList2 = FXCollections
        .observableArrayList(getDaysList());
    ListView<String> dayListView2 = new ListView<String>(dayList2);
    bottomBox.getChildren().add(dayListView2);
    ListView<String> hourListView2 = new ListView<String>();
    bottomBox.getChildren().add(hourListView2);
    _currentDay2 = 0;
    dayListView2.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
      @Override
      public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
        _currentDay2 = Week.dayInt(newValue);
          javafx.collections.ObservableList<String> hours = FXCollections
              .observableArrayList(getHoursList(newValue)); //newValue is the new day
          hourListView2.setItems(hours);
      }
  });

    ListView<String> personListView2 = new ListView<String>();
    bottomBox.getChildren().add(personListView2);
    hourListView2.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
      @Override
      public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
          int newHour = Integer.parseInt(newValue.split(" ")[0]);
          if (newHour < 9){
            newHour += 12;
          }
          List<String> scheduledEmployees = new ArrayList<String>();
          for (Employee e : _schedule.getWeek().getShift(_currentDay1, newHour)){
            scheduledEmployees.add(e.getName());
          }
          javafx.collections.ObservableList<String> people = FXCollections
              .observableArrayList(scheduledEmployees);
          personListView2.setItems(people);
      }
  });
    
    Button saveButton = new Button("Swap!");
    saveButton.setPrefWidth(744);
    rootPane.setBottom(saveButton);
    //TODO set on action and add change listeners to get current employee value
    
    
    rootPane.setTop(topBox);
    rootPane.setCenter(bottomBox);
    performSwapStage.sizeToScene();
    performSwapStage.setTitle("Perform Swap");
    performSwapStage.show();
  }
  
  //only return a list of days that have scheduled shifts in them
  private List<String> getDaysList(){
    List<String> daysList = new ArrayList<String>();
    for (int day = 0; day < 7; day++){
      for (int hour = 0; hour < _schedule.getWeek().getShifts()[day].length; hour++){
        if (_schedule.getWeek().getShifts()[day][hour].size() > 0 && !daysList.contains(Week.dayString(day))){ //at least one shift is populated
          daysList.add(Week.dayString(day));
        }
      }
      

    }
    return daysList;
  }
  
  private List<String> getHoursList(String day){
    List<String> hoursList = new ArrayList<String>();
    int minHour = -1;
    //find min
    for (int i = 0; i < _schedule.getWeek().getShifts()[Week.dayInt(day)].length; i++){
      if (_schedule.getWeek().getShift(Week.dayInt(day), i).size() > 0){
        minHour = i;
        break;
      }
    }
    //find max
    int maxHour = -1;
    for (int i = _schedule.getWeek().getShifts()[Week.dayInt(day)][minHour].getHour(); i < _schedule.getWeek().getShifts()[Week.dayInt(day)].length; i++){
      if (_schedule.getWeek().getShift(Week.dayInt(day), i).size() == 0){
        break;
      }
      maxHour = i;
    }
    for(int i = _schedule.getWeek().getShifts()[Week.dayInt(day)][minHour].getHour(); i <= _schedule.getWeek().getShifts()[Week.dayInt(day)][maxHour].getHour(); i++){
      hoursList.add(((i % 12) == 0 ? 12 : (i % 12)) + " -- " + (((i + 1) % 12) == 0 ? 12 : ((i + 1) % 12)));
    }
    return hoursList;
  }

  private void onyenSubmit(ActionEvent event) {
    _controller.uiRequestEmployeeAvailability(_onyenField.getText());
    _showSwapAvailabilityButton.setDisable(false);
    _performSwapButton.setDisable(false);
  }

  private void renderScheduleStage(Schedule schedule) {
    _schedule = schedule;
    Group root = new Group();
    Scene scene = new Scene(root);
    _scheduleStage.setScene(scene);
    GridPane schedulePane = writeSchedule(schedule);
    ScrollPane scroll = new ScrollPane();
    scroll.setPrefSize(700, 800);
    scroll.setContent(schedulePane);
    root.getChildren().add(scroll);
    _scheduleStage.sizeToScene();
    _scheduleStage.setTitle("Current Schedule");

  }

  private void renderSwapStage(Schedule schedule) {
    Group root = new Group();
    Scene scene = new Scene(root);
    BorderPane rootPane = new BorderPane();
    root.getChildren().add(rootPane);
    _swapStage.setScene(scene);
    javafx.collections.ObservableList<String> scheduledShifts = FXCollections
        .observableArrayList(this.getScheduledShifts(schedule));
    ListView<String> scheduledShiftsListView = new ListView<String>(scheduledShifts);
    HBox listBox = new HBox();
    listBox.getChildren().add(scheduledShiftsListView);
    

    ListView<String> availableSwapsListView = new ListView<String>();
    HBox swapBox = new HBox();
    swapBox.getChildren().add(availableSwapsListView);
    scheduledShiftsListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
      @Override
      public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
          System.out.println("ListView selection changed from oldValue = " 
                  + oldValue + " to newValue = " + newValue);
          javafx.collections.ObservableList<String> availableToSwap = FXCollections
              .observableArrayList(getOrderedPotentialSwaps(schedule, Week.dayInt(newValue.split(" ")[0]), Integer.parseInt(newValue.split(" ")[1])));
          availableSwapsListView.setItems(availableToSwap);
      }
  });
    rootPane.setLeft(listBox);
    rootPane.setRight(swapBox);


    _swapStage.sizeToScene();
    _swapStage.setTitle("Available for Swaps");
  }

  //the hour gets passed in as regular time and needs to be converted to military time
  private ArrayList<String> getOrderedPotentialSwaps(Schedule schedule, int day, int hour) {
    if (hour < 9){ //only hours from 9am to pm are valid so this works
      hour += 12;
    }
    ArrayList<String> swapCandidates = new ArrayList<String>();
    swapCandidates.addAll(schedule.getStaff().getWhoIsAvailable(day, hour));
    swapCandidates.remove(_currentEmployee.getName()); //remove yourself from the list
    //now score each one
    Map<Employee, Double> scoredEmployees = new HashMap<Employee, Double>();
    for (String otherEmployeeName : swapCandidates){
      Employee otherEmployee = schedule.getStaff().getEmployeeByName(otherEmployeeName);
      scoredEmployees.put(otherEmployee, 0.0);
      ArrayList<Shift> scheduledShifts = new ArrayList<Shift>();
      //get the shifts this employee is scheduled for
      for (int i = 0; i < schedule.getWeek().getShifts().length; i++){
        for (int j = 0; j < schedule.getWeek().getShifts()[i].length; j++){
          for (Employee e : schedule.getWeek().getShift(i, j)){
            if (e.getName().equals(otherEmployee.getName())){
              scheduledShifts.add(schedule.getWeek().getShift(i, j));
            }
          }
        }
      }
      //now see which shifts of other employee currentEmployee is available for
      for (Shift shift : scheduledShifts){
        if (_currentEmployee.isAvailable(shift.getDay(), shift.getHour())){
          scoredEmployees.put(otherEmployee, scoredEmployees.get(otherEmployee) + 1);
        }
      }
      //divide by total number of shifts otherEmployee has
      scoredEmployees.put(otherEmployee, scoredEmployees.get(otherEmployee) / scheduledShifts.size());
    }
    //now that we have populated the map, sort by score write out the candidates in order
    scoredEmployees = sortByValue(scoredEmployees);
    ArrayList<String> orderedSwapCandidates = new ArrayList<String>();
    for (Employee e : scoredEmployees.keySet()){
      //only write out employees we have the potential to swap with
      if (scoredEmployees.get(e) > 0){
        orderedSwapCandidates.add(e.getName() + " " + scoredEmployees.get(e));
      }
    }
    
    return orderedSwapCandidates;
  }
  
  // http://stackoverflow.com/questions/109383/sort-a-mapkey-value-by-values-java
  public static <K, V extends Comparable<? super V>> Map<K, V>
      sortByValue(Map<K, V> map) {
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

  private ArrayList<String> getScheduledShifts(Schedule schedule) {
    ArrayList<String> scheduledShifts = new ArrayList<String>();
    for (int day = 0; day < schedule.getWeek().getShifts().length; day++){
      for (int hour = 0; hour < schedule.getWeek().getShifts()[day].length; hour++){
        for (Employee e : schedule.getWeek().getShift(day, hour)){
          if (e.getName().equals(_currentEmployee.getName())){
            scheduledShifts.add(Week.dayString(day) + " " + (hour % 12 == 0 ? 12 : hour % 12) + " -- " + ((hour + 1) % 12 == 0 ? 12 : (hour + 1) % 12));
          }
        }
      }
    }
    return scheduledShifts;
  }

  private GridPane writeSchedule(Schedule schedule) {
    GridPane schedulePane = new GridPane();
    schedulePane.setAlignment(Pos.CENTER);
    schedulePane.setGridLinesVisible(true);
    ArrayList<ArrayList<ArrayList<Employee>>> shifts = shiftsAsArray(schedule.getWeek());

    for (int day = 0; day < 7; day++) {
      schedulePane.add(new Label(Week.dayString(day)), day + 1, 0); // +1 to
                                                                    // account
                                                                    // for hour
                                                                    // column
    }

    int hourRow = 0;
    for (int hour = getEarliestHour(schedule.getWeek()); hour < getLatestHour(
        schedule.getWeek()); hour++) {
      Label dayLabel = new Label((hour % 12 == 0 ? 12 : hour % 12) + " -- "
          + ((hour + 1) % 12 == 0 ? 12 : (hour + 1) % 12));
      dayLabel.setMaxWidth(Double.MAX_VALUE);
      dayLabel.setAlignment(Pos.CENTER);
      schedulePane.add(dayLabel, 0, hourRow + 1); // +1 to account for day row

      int max = getMaxSize(hour, schedule.getWeek());

      for (int i = 0; i < max; i++) {
        // output.write(",");
        for (int day = 0; day < 7; day++) {
          if (i < shifts.get(day).get(hour).size()) {
            Label scheduledEmployee = new Label(shifts.get(day).get(hour).get(i).toString());
            //highlight your name on the schedule
            if (shifts.get(day).get(hour).get(i).toString().equals(_currentEmployee.getName())){
              scheduledEmployee.setTextFill(Color.RED);
            }
            schedulePane.add(scheduledEmployee,
                day + 1, hourRow + i + 1); // +1 to account for day row
          }
        }
      }
      hourRow += max;
    }
    return schedulePane;
  }

  private static int getMaxSize(int hour, Week week) {
    int max = 0;
    for (int day = 0; day < 7; day++) {
      if (week.getShift(day, hour).size() > max) {
        max = week.getShift(day, hour).size();
      }
    }
    return max;
  }

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
    _currentEmployee = e;
    renderAvailabilityStage(e);
    _availabilityStage.show();
    _passwordStage.close();
  }

  public void displaySchedule(Schedule schedule) {
    _scheduleStage = new Stage();
    renderScheduleStage(schedule);
    _scheduleStage.show();

  }

  public void displayPossibleSwaps(Schedule schedule) {
    _swapStage = new Stage();
    renderSwapStage(schedule);
    _swapStage.show();
  }

  public Credentials getUsernamePassword() {
    return new Credentials(_usernameField.getText(), _passwordField.getText());
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

  public void displayMessage(String message) {
    Alert alert = new Alert(Alert.AlertType.ERROR);
    alert.setHeaderText("Error");
    alert.setContentText(message);
    alert.showAndWait();

  }

}
