package comp110;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class DataIO {

  static Week parseWeek(String csvPath, String scenario) throws IOException {
    Week week = new Week(scenario);
    BufferedReader csvReader = new BufferedReader(new FileReader(csvPath));

    csvReader.readLine(); // discard first header line

    for (int hour = 0; hour < 24; hour++) {
      String scheduleLine = csvReader.readLine();
      for (int day = 0; day < 7; day++) {
        week.getShifts()[day][hour] = new Shift(day, hour, Integer.parseInt(scheduleLine.split(",")[day + 1]));
      }
    }
    csvReader.close();
    return week;
  }

  static Staff parseStaff(String dir) throws IOException {
    Staff staff = new Staff();
    File csvDirectory = new File(dir);
    BufferedReader csvReader = null;
    int counter = 0;
    for (File csv : csvDirectory.listFiles()) {
      String onyen = csv.getName().substring(0, csv.getName().length() - 4);
      String name = "";
      String gender = "";
      int capacity = 0;
      int level = 0;
      try {
        csvReader = new BufferedReader(new FileReader(csv));
        // parsing employee info
        name = csvReader.readLine().split(",")[1]; // ugly
        gender = csvReader.readLine().split(",")[1];
        capacity = Integer.parseInt(csvReader.readLine().split(",")[1]);
        level = Integer.parseInt(csvReader.readLine().split(",")[1]);
        counter++;
      } catch (Exception e) {
        System.err.println("Error parsing: " + name + counter);
        e.printStackTrace();
      }

      csvReader.readLine(); // throw away header line with days

      // read in schedule
      int[][] availability = new int[7][24];
      for (int hour = 0; hour < 24; hour++) {
        String scheduleLine = csvReader.readLine();
        for (int day = 0; day < 7; day++) {
          // Offset by 1 accounts for label in CSV
          try {
            availability[day][hour] = Integer.parseInt(scheduleLine.split(",")[day + 1]);
          } catch (NumberFormatException e) {
            System.err.println("Error:" + name);
            e.printStackTrace();
          }
        }
      }
      staff.add(new Employee(name, onyen, capacity, gender.equals("M") ? false : true, level, availability));
    }
    return staff;
  }

}
