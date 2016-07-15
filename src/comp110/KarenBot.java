package comp110;

import static java.lang.System.out;

import java.io.IOException;

import com.sun.javafx.binding.StringFormatter;

import comp110.krisj.WorstAlgo;

public class KarenBot {

  public static void main(String[] args) throws IOException {
    // Replace WorstAlgo with your own SchedulingAlgo class to begin.
    // Be sure to make a package with your own onyen name to avoid collisions.
    SchedulingAlgo algo = new WorstAlgo();
    String scenario = "test-small";
    int trials = 100;

    // Auto Generate Data Test
    String testName = "auto-test";
    // days are zero indexed
    CSVGenerator generator = new CSVGenerator(testName, 40, 0, 6, 9, 21, 20, 5);

    // Load Data
    Week week = DataIO.parseWeek("data/" + scenario + "/week.csv", scenario);
    Staff staff = DataIO.parseStaff("data/" + scenario + "/staff");

    // Run Algorithm for N trials and score it
    Scorer scorer = new Scorer(staff, week, algo);
    Scorecard scorecard = scorer.run(trials);

    // Output Results
    output(scorecard);
  }

  private static void output(Scorecard scorecard) {
    log("Schedule", scorecard.getSchedule().getWeek());
    log("Diagnostics", scorecard.getDiagnostics());
    String score = StringFormatter.format("%.3f - Schedule Score", scorecard.getScore()).get();
    log(score, scorecard);
  }

  private static void log(String header, Object body) {
    out.println("======================");
    out.println(header);
    out.println("======================");
    out.println(body);
  }
}