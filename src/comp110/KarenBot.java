package comp110;

import static java.lang.System.out;

import java.io.IOException;
import java.util.Random;

import com.sun.javafx.binding.StringFormatter;

import comp110.krisj.WorstAlgo;

public class KarenBot {
  
  public enum Day {
    SUNDAY, MONDAY, TUESDAY, WEDNESDAY,
    THURSDAY, FRIDAY, SATURDAY 
}

  public static void main(String[] args) throws IOException {

    // Replace WorstAlgo with your own SchedulingAlgo class to begin.
    // Be sure to make a package with your own onyen name to avoid collisions.
    SchedulingAlgo algo = new WorstAlgo();

    String scenario = "test-small";
    Week week = DataIO.parseWeek("data/" + scenario + "/week.csv", scenario);
    Staff staff = DataIO.parseStaff("data/" + scenario + "/staff");

    Schedule schedule = new Schedule(staff, week);
    schedule = algo.run(schedule, new Random());
    Scorecard report = Scorer.score(schedule);

    // Output Results
    log("Schedule", schedule.getWeek());
    log("Diagnostics", report.getDiagnostics());
    String score = StringFormatter.format("%.3f - Schedule Score%n", report.getScore()).get();
    log(score, report);
  }

  private static void log(String header, Object body) {
    out.println("======================");
    out.println(header);
    out.println("======================");
    out.println(body);
  }
}