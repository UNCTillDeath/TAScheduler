package comp110;

import javafx.event.ActionEvent;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;

public class Controller {
  public void run(){

  }
  public void storagePullCompleteCallback(boolean success, String message){

  }
  public void uiRequestSchedule(ActionEvent event){

  }
  public void uiRequestAvailable(){

  }
  public void uiUsernamePasswordCallback(){

  }
  public void uiRequestSwaps(){

  }
  public void uiRequestSaveAvailability(ActionEvent event){
    
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
