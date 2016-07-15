package comp110;

import static java.lang.System.out;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Random;

import comp110.krisj.WorstAlgo;

public class KarenBot {
  
  public enum Day {
    SUNDAY, MONDAY, TUESDAY, WEDNESDAY,
    THURSDAY, FRIDAY, SATURDAY 
}

  public static void main(String[] args) throws IOException {

    // To hack -- replace WorstAlgo with a class in your own package that
    // implements SchedulingAlgo
    SchedulingAlgo algo = new WorstAlgo();

    // TODO: Read in week capacity schedule from CSV
    Week week = new Week();
    for (int day = 0; day < 7; day++) {
      for (int hour = 9; hour < 11; hour++) {
        Shift shift = week.getShift(day, hour);
        shift.setCapacity(2);
      }
    }

    Staff staff = new Staff();
    parseCSVs(staff);

    Schedule schedule = new Schedule(staff, week);
    schedule = algo.run(schedule, new Random());
    Scorecard report = Scorer.score(schedule);

    out.println("=============");
    out.println(" Schedule ");
    out.println("=============");
    out.println(schedule.getWeek());

    out.println("=============");
    out.println(" Diagnostics ");
    out.println("=============");

    for (Scoreline scoreline : report) {
      if (scoreline.size() > 0) {
        out.println(scoreline.getLabel());
        for (String issue : scoreline) {
          out.println("\t" + issue);
        }
      }
    }

    out.println("\n");
    out.println("======================");
    out.format("%.3f - Schedule Score%n", report.getScore());
    out.println("======================");

    for (Scoreline scoreline : report) {
      out.format("%.3f - %s%n", scoreline.getValue(), scoreline.getLabel());
    }

  }

  private static void parseCSVs(Staff staff) throws IOException {
    File csvDirectory = new File("data/schedule");
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

  }
}