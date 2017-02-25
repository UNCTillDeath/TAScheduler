package comp110;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class Parser{
  public Employee parseEmployee(String file){
	  File csv = new File(file);
	  BufferedReader csvReader = null;
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
       
      } catch (Exception e) {
        System.err.println("Error parsing: " + name);
        e.printStackTrace();
      }

      try {
		csvReader.readLine();
	} catch (IOException e1) {
		System.err.println("IO Error: " + name);
		e1.printStackTrace();
	} // throw away header line with days

      // read in schedule
      int[][] availability = new int[7][24];
      for (int hour = 0; hour < 24; hour++) {
        String scheduleLine = "";
		try {
			scheduleLine = csvReader.readLine();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			System.err.println("IO Error: " + name);
			e1.printStackTrace();
		}
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
      return new Employee(name, onyen, capacity, gender.equals("M") ? false : true, level, availability);
    }
  public Staff parseSchedule(String dir){
	  Staff staff = new Staff();
	  File csvDirectory = new File(dir);
	  BufferedReader csvReader = null;
	  int counter = 0;
	  for (File csv : csvDirectory.listFiles()) {
		  staff.add(parseEmployee(csv.toString()));
	  }
	  return staff; 
  }
  public void writeFile(){

  }
  
  public static void main(String[] args){
	  Parser parser = new Parser();
	  Staff staff = parser.parseSchedule("C:/Users/Keith Whitley/git/TAScheduler/data/spring-17/staff/");
	  System.out.println(staff.toString());
	  
  }
  
}
