package comp110;

import static java.lang.System.out;

public class KarenBot {

  public static void main(String[] args) {

    Week week = new Week();
    String[] days = {
        "0. SUN",
        "1. MON",
        "2. TUE",
        "3. WED",
        "4. THU",
        "5. FRI",
        "6. SAT" };

    for (String day : days) {
      Shifts shifts = new Shifts();
      for (byte i = 10; i <= 12; i++) {
        shifts.add(new Shift(i, 2));
      }
      week.put(day, shifts);
    }

    Staff staff = new Staff();
    Employee kris = new Employee("Kris", 5, false, 2);
    Employee sarah = new Employee("Sarah", 5, true, 2);
    staff.add(kris);
    staff.add(sarah);
    week.get("0. SUN").get(0).add(kris);
    week.get("0. SUN").get(0).add(sarah);

    Schedule schedule = new Schedule(staff, week);
    // TODO - pass schedule off to an algorithm to generate (10x times?)
    Scorecard report = Scorer.score(schedule);

    out.format("%.3f - Schedule Rating%n", report.getScore());
    out.println("=============");

    for (Scoreline scoreline : report) {
      out.format("%.3f - %s%n", scoreline.getValue(), scoreline.getLabel());
    }

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

  }

}