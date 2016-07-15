package comp110;

import static java.lang.System.out;

import java.util.Random;

import comp110.krisj.WorstAlgo;

public class KarenBot {

  public static void main(String[] args) {

    // To hack -- replace WorstAlgo with a class in your own package that
    // implements SchedulingAlgo
    SchedulingAlgo algo = new WorstAlgo();

    Week week = new Week();
    for (int day = 0; day < 7; day++) {
      for (int hour = 9; hour < 11; hour++) {
        Shift shift = week.getShift(day, hour);
        shift.setCapacity(2);
      }
    }

    // TODO: Load Staff from I/O
    Staff staff = new Staff();
    staff.add(new Employee("Karen", 10, true, 3));
    staff.add(new Employee("Han Bit", 10, true, 3));
    staff.add(new Employee("Helen", 8, true, 2));
    staff.add(new Employee("Kate", 6, true, 1));
    staff.add(new Employee("Jeffrey", 10, false, 3));
    staff.add(new Employee("Muttaqee", 10, false, 3));
    staff.add(new Employee("Ben", 8, false, 2));
    staff.add(new Employee("Hank", 6, false, 1));

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

}