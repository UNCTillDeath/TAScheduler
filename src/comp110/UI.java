package comp110;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.RowConstraints;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

public class UI extends Application {
  
  private Stage _availabilityStage;
  private Stage _schedule;
  private GridPane _grid;
  
  public UI(){
    _schedule = new Stage();

  }
  

  @Override
  public void start(Stage primaryStage) throws Exception {
    _availabilityStage = primaryStage;
    renderAvailabilityStage();
  }
  
  private void renderAvailabilityStage(){
    Group availabilityRoot = new Group();
    Scene availabilityScene = new Scene(availabilityRoot);

    _grid = new GridPane();
    _grid.setGridLinesVisible(true);
    
    for (int day = 0; day < 8; day++){ //8 to account for first column with hours
      Label dayLabel = new Label(Week.dayString(day));
      dayLabel.setMaxWidth(Double.MAX_VALUE);
      dayLabel.setAlignment(Pos.CENTER);
      _grid.add(dayLabel, day, 0);
      for (int hour = 0; hour < 24; hour++){ 
        HBox box = new HBox();
        if (day == 0){ //if header row
          box.getChildren().add(new Label("time"));
          
        }
        else {
          CheckBox check = new CheckBox();
          check.setOnAction(this::handleCheck);
          box.getChildren().add(check);
          box.setAlignment(Pos.CENTER);
          box.setMinHeight(30);
          box.setMinWidth(60);
        }
        _grid.add(box, day, hour + 1); //+1 to account for header row
      }
    }
    
        
    
    availabilityRoot.getChildren().add(_grid);
    _availabilityStage.setScene(availabilityScene);
    _availabilityStage.setTitle("COMP110 TA Availability");
    _availabilityStage.sizeToScene();
    _availabilityStage.setResizable(false);
    displayAvailable(null);
    
  }
  
  private void handleCheck(ActionEvent event) {
    CheckBox check = (CheckBox) event.getSource();
    HBox parent = (HBox) check.getParent();
    if (check.isSelected()){
      parent.setBackground(new Background(new BackgroundFill(Color.GREEN, null, null)));
    } else {
      parent.setBackground(new Background(new BackgroundFill(Color.RED, null, null)));
    }
  }


  public void displayAvailable(Employee e){
    _availabilityStage.show();
  }
  
  public static void main(String[] args){
    Application.launch(args);
  }

}
