package comp110;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

import javafx.event.ActionEvent;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;

public class Controller {
  UI ui = new UI();

  public void run() {

  }

  public void storagePullCompleteCallback(boolean success, String message) {

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
    ui.displaySchedule(schedule);
  }

  public void uiRequestEmployeeAvailability(ActionEvent event) {

  }

  public void uiUsernamePasswordCallback() {

  }

  public void uiRequestSwaps(ActionEvent event) {
    ui.displayPossibleSwaps(null);
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
