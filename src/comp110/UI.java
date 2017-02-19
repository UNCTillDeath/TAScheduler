package comp110;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
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

  private Stage    _availabilityStage;
  private Stage    _schedule;
  private GridPane _grid;
  private Controller _controller;

  @Override
  public void start(Stage primaryStage) throws Exception {
    _availabilityStage = primaryStage;
    _schedule = new Stage();
    _controller = new Controller();
    displayAvailable(null);
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
    pullScheduleButton.setOnAction(_controller::uiRequestSchedule);
    
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
          Label timeLabel = new Label((time % 12 == 0 ? 12 : time) + " - " + ((time + 1) % 12 == 0 ? 12 : time + 1));
          timeLabel.setMaxWidth(Double.MAX_VALUE);
          timeLabel.setAlignment(Pos.CENTER);
          box.getChildren().add(timeLabel);

        } else {
          CheckBox check = new CheckBox();
          if (e != null){
            if (e.isAvailable(day - 1, hour + 9)){ //map day and hour onto our space
              check.setSelected(true);
              box.setBackground(new Background(new BackgroundFill(Color.GREEN, null, null)));
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
    saveButton.setPrefWidth(460);
    saveButton.setOnAction(_controller::uiRequestSaveAvailability);
    bottomBar.getChildren().add(saveButton);
    rootPane.setBottom(bottomBar);
    
    availabilityRoot.getChildren().add(rootPane);
    _availabilityStage.setScene(availabilityScene);
    _availabilityStage.setTitle("COMP110 TA Availability");
    _availabilityStage.sizeToScene();
    _availabilityStage.setResizable(false);

  }

  

  public void displayAvailable(Employee e) {
    renderAvailabilityStage(e);
    _availabilityStage.show();
  }

  public static void main(String[] args) {
    Application.launch(args);
  }

}
