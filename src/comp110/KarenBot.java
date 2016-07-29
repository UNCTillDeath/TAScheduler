package comp110;

import static java.lang.System.out;

import java.io.IOException;

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

    // Run Algorithm for N trials and score it
    Scorer scorer = new Scorer(staff, week, algo);
    RunReport report = scorer.runWithReport(trials, verbose);

    // Output Results
    output(report);
  }

  private static void output(RunReport report) {
    Scorecard scorecard = report.getHigh();
    log("Diagnostics", scorecard.getDiagnostics());
    log("Schedule", scorecard.getSchedule().getWeek());
    String score = StringFormatter.format("%.3f - Highest Score", scorecard.getScore()).get();
    log(score, scorecard);
    log("Stats (n:" + report.getTrials() + ")", report.getStats());
  }

  private static void log(String header, Object body) {
    out.println("======================");
    out.println(header);
    out.println("======================");
    out.println(body);
  }
}