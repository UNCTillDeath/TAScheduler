package comp110;

import static java.lang.System.out;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import com.sun.javafx.binding.StringFormatter;

public class KarenBot {

  private SchedulingAlgo _algo;

  public KarenBot(SchedulingAlgo algo) {
    _algo = algo;
  }

  public void run(String scenario, int trials) {
    this.run(scenario, trials, false);
  }

  public void run(String scenario, int trials, boolean verbose) {
    SchedulingAlgo algo = _algo;

    // Load Data
    Week week;
    Staff staff;

    try {
      week = DataIO.parseWeek("data/" + scenario + "/week.csv", scenario);
      staff = DataIO.parseStaff("data/" + scenario + "/staff");
    } catch (IOException e) {
      e.printStackTrace();
      System.exit(1);
      return;
    }

    // Verify everyone has at least 20 available shifts for scheduling
    verifyHours(staff, week);

    // Run Algorithm for N trials and score it
    Scorer scorer = new Scorer(staff, week, algo);
    RunReport report = scorer.runWithReport(trials, verbose);

    // Output Results
    output(report);
  }

  private void verifyHours(Staff staff, Week week) {
    for (Employee e : staff) {
      int employeeHoursAvailability = 0;
      for (int day = 0; day < e.getAvailability().length; day++) {
        for (int hour = 0; hour < e.getAvailability()[0].length; hour++) {
          if (e.isAvailable(day, hour) && week.getShift(day, hour).getCapacity() > 0) {
            employeeHoursAvailability++;
          }
        }
      }
      if (employeeHoursAvailability < 20) {
        System.out.println(e.getName() + " only has " + employeeHoursAvailability + " available for scheduling (" + e.getCapacity() + ")");
      }
    }

  }

  private static void output(RunReport report) {
    Scorecard scorecard = report.getHigh();
    writeOutput(scorecard);
    log("Diagnostics", scorecard.getDiagnostics());
    log("Schedule", scorecard.getSchedule().getWeek());
    String score = StringFormatter.format("%.3f - Highest Score", scorecard.getScore()).get();
    log(score, scorecard);
    log("Stats (n:" + report.getTrials() + ")", report.getStats());
  }

  private static void writeOutput(Scorecard scorecard) {
    ArrayList<ArrayList<ArrayList<Employee>>> shifts = shiftsAsArray(scorecard.getSchedule().getWeek());
    try {
      FileWriter output = new FileWriter(new File("data/output.csv"));
      output.write(",Sunday, Monday, Tuesday, Wednesday, Thursday, Friday, Saturday\n");
      for (int hour = getEarliestHour(scorecard.getSchedule().getWeek()); hour < getLatestHour(scorecard.getSchedule().getWeek()); hour++) {
        output.write(hour + " -- " + (hour + 1));
        int max = getMaxSize(hour, scorecard.getSchedule().getWeek());
        if (max == 0) {
          output.write("\n");
        }
        for (int i = 0; i < max; i++) {
          output.write(",");
          for (int day = 0; day < 7; day++) {
            if (i < shifts.get(day).get(hour).size()) {
              output.write(shifts.get(day).get(hour).get(i).toString() + ",");
            } else {
              output.write(",");
            }
          }
          output.write("\n");
        }
      }
      output.close();
    } catch (IOException e) {
    }
  }

  private static void log(String header, Object body) {
    out.println("======================");
    out.println(header);
    out.println("======================");
    out.println(body);
  }

  public void runForKaren(String scenario, int day, int hour, boolean outputCapacityVerification) {
    SchedulingAlgo algo = _algo;

    // Load Data
    Week week;
    Staff staff;

    try {
      week = DataIO.parseWeek("data/" + scenario + "/week.csv", scenario);
      staff = DataIO.parseStaff("data/" + scenario + "/staff");
    } catch (IOException e) {
      e.printStackTrace();
      System.exit(1);
      return;
    }

    // Run Algorithm for N trials and score it
    getWhoIsAvailable(staff, day, hour);
    
    //write out a CSV of staff and their capacity
    if(outputCapacityVerification){
      int capacitySum = 0;
      try {
        FileWriter output = new FileWriter(new File("data/capacityVerification.csv"));
        output.write("Name,Capacity\n");
        for (Employee e : staff){
          capacitySum += e.getCapacity();
          output.write(e.getName() + "," + e.getCapacity() + "\n");
        }
        output.write("Total Capacity," + capacitySum);
        output.flush();
        output.close();
      } catch (IOException e) {
        e.printStackTrace();
        System.exit(1);
      }
    }
    
  }

  public void getWhoIsAvailable(Staff staff, int day, int hour) {
    System.out.println("Day: " + day + " Hour: " + hour);
    for (Employee e : staff) {
      if (e.isAvailable(day, hour)) {
        System.out.println(e.getName());
      }
    }
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
}