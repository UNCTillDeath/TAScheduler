package comp110;

public class Scorer {

  private final static String UTILIZATION = "Staff capacity utilization";
  private final static String PREFERRED_CAPACITY_MET = "Shifts where employees meet or exceed capacity";
  private final static String GENDER_REPRESENTATION = "Gender representation in every shift";
  private final static String COMBINED_EXPERTISE = "Average expertise in every shift is > 1.5";

  public static Scorecard score(Schedule schedule) {
    Scorecard scorecard = new Scorecard(schedule);

    scorecard.add(Scorer.utilization(schedule));
    scorecard.add(Scorer.preferredCapacityMet(schedule));
    scorecard.add(Scorer.genderRepresentation(schedule));
    scorecard.add(Scorer.combinedExpertise(schedule));

    return scorecard;
  }

  /*
   * Other ideas for Scorelines:
   * 
   * - Nondeterminism: % difference in allocation between two generations -
   * Determinism: given same seed twice -- differences?!?
   * 
   * - Contiguous shifts: % of scheduled hours which are not single, isolated
   * hours
   */

  /*
   * Utilization is the total # of hours scheduled over the total # of Employee
   * hours available.
   */
  private static Scoreline utilization(Schedule schedule) {
    Scoreline result = new Scoreline(UTILIZATION, 0.0);

    if (schedule.getWeek().getScheduledHours() > 0) {
      double scheduledHours = schedule.getWeek().getScheduledHours();
      double capacityHours = schedule.getStaff().getCapacity();
      double percentUsed = scheduledHours / capacityHours;
      if (percentUsed <= 1.0) {
        // avoid gaming by overbooking
        result.setValue(percentUsed);
      } else {
        result.add("You cannot overbook the staff. You scheduled " + (percentUsed * 100.0)
            + " of the hours available.");
      }
    }
    return result;
  }

  /*
   * % of shifts that have preferred capacity met
   */
  private static Scoreline preferredCapacityMet(Schedule schedule) {
    int shiftsMeetingPreferredCapacity = 0;

    Scoreline result = new Scoreline(PREFERRED_CAPACITY_MET, 0.0);

    Week week = schedule.getWeek();
    Shift[][] shifts = week.getShifts();
    for (int day = 0; day < shifts.length; day++) {
      for (Shift shift : shifts[day]) {
        if (shift.getCapacity() > 0) {
          if (shift.size() >= shift.getCapacity()) {
            shiftsMeetingPreferredCapacity++;
          } else {
            result.add(day + " at " + shift.getHour() + ":00 has " + shift.size() + " of " + shift.getCapacity()
                + " slots filled");
          }
        }
      }
    }

    result.setValue(shiftsMeetingPreferredCapacity / (double) week.getNumberOfShifts());

    return result;
  }

  /*
   * % of shifts that have both genders present.
   */
  private static Scoreline genderRepresentation(Schedule schedule) {
    int shiftsWithGenderRepresentation = 0;

    Scoreline result = new Scoreline(GENDER_REPRESENTATION, 0.0);

    Week week = schedule.getWeek();
    Shift[][] shifts = week.getShifts();
    for (int day = 0; day < shifts.length; day++) {
      for (Shift shift : shifts[day]) {
        int women = 0, men = 0;
        for (Employee employee : shift) {
          if (employee.getIsFemale()) {
            women++;
          } else {
            men++;
          }
        }

        if (shift.getCapacity() > 0) {
          if (women > 0 && men > 0) {
            shiftsWithGenderRepresentation++;
          } else {
            result.add(day + " at " + shift.getHour() + ":00 has " + women + " women and " + men + " men scheduled");
          }
        }
      }
    }

    result.setValue(shiftsWithGenderRepresentation / (double) week.getNumberOfShifts());

    return result;
  }

  /*
   * % of shifts that have an average expertise of > 2.0
   */
  private static Scoreline combinedExpertise(Schedule schedule) {
    int shiftsMeetingThreshold = 0;

    Scoreline result = new Scoreline(COMBINED_EXPERTISE, 0.0);

    Week week = schedule.getWeek();
    Shift[][] shifts = week.getShifts();
    for (int day = 0; day < shifts.length; day++) {
      for (Shift shift : shifts[day]) {
        double expertise = 0.0;
        for (Employee employee : shift) {
          expertise += employee.getLevel();
        }
        double average = shift.size() > 0 ? expertise / shift.size() : 0.0;

        if (shift.getCapacity() > 0) {
          if (average > 1.5) {
            shiftsMeetingThreshold++;
          } else {
            result.add(day + " at " + shift.getHour() + ":00 has an average expertise of " + average);
          }
        }
      }
    }

    result.setValue(shiftsMeetingThreshold / (double) week.getNumberOfShifts());

    return result;
  }

}
