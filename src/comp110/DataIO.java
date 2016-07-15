package comp110;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class DataIO {

  static Week parseWeek(String dir) throws IOException {
    // TODO read in data
    Week week = new Week();
    new Week();
    for (int day = 0; day < 7; day++) {
      for (int hour = 9; hour < 11; hour++) {
        Shift shift = week.getShift(day, hour);
        shift.setCapacity(2);
      }
    }
    return week;
  }

  static Staff parseStaff(String dir) throws IOException {
    Staff staff = new Staff();
    File csvDirectory = new File(dir);
    BufferedReader csvReader;
    for (File csv : csvDirectory.listFiles()) {
      csvReader = new BufferedReader(new FileReader(csv));
      // parsing employee info
      String name = csvReader.readLine().split(",")[1]; // ugly
      String gender = csvReader.readLine().split(",")[1];
      int capacity = Integer.parseInt(csvReader.readLine().split(",")[1]);
      int level = Integer.parseInt(csvReader.readLine().split(",")[1]);
  
      csvReader.readLine(); // throw away header line with days
  
      // read in schedule
      int[][] availability = new int[7][24];
      for (int hour = 0; hour < 24; hour++) {
        String scheduleLine = csvReader.readLine();
        for (int day = 0; day < 7; day++) {
          // Offset by 1 accounts for label in CSV
          availability[day][hour] = Integer.parseInt(scheduleLine.split(",")[day + 1]);
        }
      }
      staff.add(new Employee(name, capacity, gender.equals("M") ? false : true, level, availability));
  
      // testing
      // for (int i = 0; i < availability.length; i++) {
      // for (int j = 0; j < availability[0].length; j++) {
      // System.out.print(availability[i][j]);
      // }
      // System.out.print("\n");
      // }
    }
    return staff;
  }

}
