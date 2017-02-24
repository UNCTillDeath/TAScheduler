package comp110;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.RowConstraints;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

public class UI extends Application {

  private Stage      _availabilityStage;
  private Stage      _scheduleStage;
  private Stage      _swapStage;
  private GridPane   _grid;
  private Controller _controller;
  private static UI _staticUI;

  public UI () {
	  _staticUI = this;
  }
  
  @Override
  public void start(Stage primaryStage) throws Exception {
    _availabilityStage = primaryStage;
    displayAvailable(null);
  }
  
  public static UI getInstance() {
	  return _staticUI;
  }
  
  public void setController(Controller controller) {
	  _controller = controller;
  }

  private void renderAvailabilityStage(Employee e) {
    Group availabilityRoot = new Group();
    Scene availabilityScene = new Scene(availabilityRoot);
    BorderPane rootPane = new BorderPane();

    HBox topBar = new HBox();
    TextField onyenField = new TextField("Enter onyen here");
    topBar.getChildren().add(onyenField);
    rootPane.setTop(topBar);

    Button pullScheduleButton = new Button("Pull Schedule");
    topBar.getChildren().add(pullScheduleButton);
    pullScheduleButton.setOnAction(_controller::uiRequestEmployeeAvailability);

    Button showScheduleButton = new Button("Show Current Schedule");
    showScheduleButton.setOnAction(_controller::uiRequestSchedule);
    topBar.getChildren().add(showScheduleButton);

    Button showSwapAvailabilityButton = new Button("Show Swaps");
    showSwapAvailabilityButton.setOnAction(_controller::uiRequestSwaps);
    topBar.getChildren().add(showSwapAvailabilityButton);

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
          check.setOnAction(_controller::handleCheck);
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

  private void renderScheduleStage(Schedule schedule) {
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
  
  private void renderSwapStage(Schedule schedule){
    Group root = new Group();
    Scene scene = new Scene(root);
    _swapStage.setScene(scene);
    
    
    _swapStage.sizeToScene();
    _swapStage.setTitle("Available for Swaps");
  }

  private GridPane writeSchedule(Schedule schedule) {
    GridPane schedulePane = new GridPane();
    schedulePane.setAlignment(Pos.CENTER);
    schedulePane.setGridLinesVisible(true);
    ArrayList<ArrayList<ArrayList<Employee>>> shifts = shiftsAsArray(schedule.getWeek());
    
    for (int day = 0; day < 7; day++) {
      schedulePane.add(new Label(Week.dayString(day)), day + 1, 0); //+1 to account for hour column
    }
   
    int hourRow = 0;    
    for (int hour = getEarliestHour(schedule.getWeek()); hour < getLatestHour(
        schedule.getWeek()); hour++) {
      Label dayLabel = new Label((hour % 12 == 0 ? 12 : hour % 12) + " -- " + ((hour + 1) % 12 == 0 ? 12 : (hour + 1) % 12));
      dayLabel.setMaxWidth(Double.MAX_VALUE);
      dayLabel.setAlignment(Pos.CENTER);
      schedulePane.add(dayLabel, 0, hourRow + 1); //+1 to account for day row
      
      int max = getMaxSize(hour, schedule.getWeek());
      
      for (int i = 0; i < max; i++) {
        //output.write(",");
        for (int day = 0; day < 7; day++) {
          if (i < shifts.get(day).get(hour).size()) {
           schedulePane.add(new Label(shifts.get(day).get(hour).get(i).toString()), day + 1, hourRow + i + 1); //+1 to account for day row
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
    renderAvailabilityStage(e);
    _availabilityStage.show();
  }

  public void displaySchedule(Schedule schedule) {
    _scheduleStage = new Stage();
    renderScheduleStage(schedule);
    _scheduleStage.show();

  }
  
  public void displayPossibleSwaps(Schedule schedule){
    _swapStage = new Stage();
    renderSwapStage(schedule);
    _swapStage.show();
  }
  
  public Credentials getUsernamePassword() {
	  
	  return new Credentials("test", "test");
  }

  


}
