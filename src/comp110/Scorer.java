package comp110;

public class Scorer {

  private final static String UTILIZATION = "Capacity Utilization";
  private final static String AT_LEAST_TWO_EMPLOYEES = "2+ Employees / Shift";

  public static Scorecard score(Schedule schedule) {
    Scorecard scorecard = new Scorecard(schedule);

    scorecard.add(Scorer.utilization(schedule));
    scorecard.add(Scorer.atLeastTwoEmployeesPerShift(schedule));

    return scorecard;
  }

  /*
   * Other ideas for Scorelines:
   * 
   * - Nondeterminism: % difference in allocation between two generations
   * 
   * - Gender representation: % of shifts with both a woman and a man
   * 
   * - Expertise minimum: some way of ensuring we avoid two LAs in 401 from
   * being paired. Ideal is mixing expertise.
   * 
   * - Preferred capacity: each shift has some preferred capacity we would like
   * to meet or exceed. % that do (maybe this is duplicate of the min # of 2?)
   * 
   * - Contiguous shifts: % of scheduled hours which are not single, isolated
   * hours with breaks in between
   */

  /*
   * Utilization is the total # of hours scheduled over the total # of Employee
   * hours available.
   */
  private static Scoreline utilization(Schedule schedule) {
    if (schedule.getWeek().getScheduledHours() > 0) {
      double scheduledHours = schedule.getWeek().getScheduledHours();
      double capacityHours = schedule.getStaff().getCapacity();
      return new Scoreline(UTILIZATION, scheduledHours / capacityHours);
    } else {
      return new Scoreline(UTILIZATION, 0.0);
    }
  }

  /*
   * % of shifts that have at least 2 Employees.
   */
  private static Scoreline atLeastTwoEmployeesPerShift(Schedule schedule) {
    int shiftsWithAtLeastTwoEmployees = 0;

    Scoreline result = new Scoreline(AT_LEAST_TWO_EMPLOYEES, 0.0);

    Week week = schedule.getWeek();

    for (String day : week.keySet()) {
      for (Shift shift : week.get(day)) {
        if (shift.size() >= 2) {
          shiftsWithAtLeastTwoEmployees++;
        } else {
          result.add(day + " at " + shift.getHour() + ":00 has " + shift.size() + " employees schedule");
        }
      }
    }

    result.setValue(shiftsWithAtLeastTwoEmployees / (double) week.getNumberOfShifts());

    return result;
  }

}
