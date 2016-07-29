package comp110;

import java.util.Random;

public class Scorer {

  private final static String UTILIZATION = "Staff capacity utilization";
  private final static String PREFERRED_CAPACITY_MET = "Shifts where employees meet or exceed capacity";
  private final static String CONTIGUOUS_BLOCKS = "Employees are scheduled for between 2-4 contiguous hours";
  private final static String GENDER_REPRESENTATION = "Gender representation in every shift";
  private final static String COMBINED_EXPERIENCE = "Average experience in every shift is > 1.5";
  private final static String AVAILABILITY = "No staff is scheduled for a shift they are not available for";
  private final static String SEED_BASED_DETERMINISM =
      "Randomization is encouraged, but only using the Random instance provided";

  private Schedule _schedule;
  private SchedulingAlgo _algo;

  public Scorer(Staff staff, Week week, SchedulingAlgo algo) {
    _schedule = new Schedule(staff, week);
    _algo = algo;
  }

  public Scorecard run(int trials) {
    Scorecard bestRun = null;
    for (int i = 0; i < trials; i++) {
      Scorecard run = Scorer.evaluate(_schedule, _algo, new Random());
      if (bestRun == null || run.getScore() > bestRun.getScore()) {
        bestRun = run;
      }
    }
    return bestRun;
  }

  public RunReport runWithReport(int trials, boolean verbose) {
    RunReport report = new RunReport(trials, verbose);
    for (int i = 0; i < trials; i++) {
      report.add(Scorer.evaluate(_schedule, _algo, new Random()));
    }
    return report;
  }

  public static Scorecard evaluate(Schedule input, SchedulingAlgo algo, Random random) {
    Schedule output = algo.run(input.copy(), random);
    Scorecard scorecard = new Scorecard(output);
    scorecard.add(Scorer.preferredCapacityMet(output));
    scorecard.add(Scorer.utilization(output));
    scorecard.add(Scorer.contiguous(output));
    scorecard.add(Scorer.combinedExpertise(output));
    scorecard.add(Scorer.genderRepresentation(output));
    scorecard.add(Scorer.availability(output));
    scorecard.add(Scorer.seedBasedNondeterminism(input.copy(), algo));
    return scorecard;
  }

  private static Scoreline seedBasedNondeterminism(Schedule input, SchedulingAlgo algo) {
    Random seed = new Random(0);
    Schedule reference = algo.run(input.copy(), seed);
    boolean passes = true;
    for (int i = 0; i < 2; i++) {
      seed = new Random(0);
      Schedule test = algo.run(input.copy(), seed);
      if (test.equals(reference) == false) {
        passes = false;
        break;
      }
    }
    return new Scoreline(SEED_BASED_DETERMINISM, passes ? 1.0 : 0.0);
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
            result.add(Week.dayString(day) + " at " + shift.getHour() + ":00 has " + shift.size() + " of "
                + shift.getCapacity() + " slots filled");
          }
        }
      }
    }

    result.setValue(shiftsMeetingPreferredCapacity / (double) week.getNumberOfShifts());

    return result;
  }

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
   * Contiguous score is the % of hours scheduled in sequences of of 2-4 hours.
   * 
   * The implementation of this scoreline is not efficient. Hide your eyes.
   */
  private static Scoreline contiguous(Schedule schedule) {
    Scoreline result = new Scoreline(CONTIGUOUS_BLOCKS, 0.0);
    int contiguousShifts = 0;

    Week week = schedule.getWeek();
    Shift[][] shifts = week.getShifts();
    for (int day = 0; day < shifts.length; day++) {
      for (int hour = 0; hour < shifts[day].length; hour++) {
        if (shifts[day][hour].getCapacity() == 0) {
          continue;
        }

        Shift shift = shifts[day][hour];
        for (Employee employee : shift) {
          int contiguous = 1; // Current shift

          // Look back
          for (int hourBefore = hour - 1; hourBefore >= 0; hourBefore--) {
            if (shifts[day][hourBefore].contains(employee)) {
              contiguous++;
            } else {
              break;
            }
          }

          // Look ahead
          for (int hourAfter = hour + 1; hourAfter < shifts[day].length; hourAfter++) {
            if (shifts[day][hourAfter].contains(employee)) {
              contiguous++;
            } else {
              break;
            }
          }

          if (contiguous < 2) {
            result.add(Week.dayString(day) + " at " + shift.getHour() + ":00 " + employee.getName()
                + " scheduled for a single hour block");
          } else if (contiguous > 4) {
            result.add(Week.dayString(day) + " at " + shift.getHour() + ":00 " + employee.getName()
                + " scheduled for over 4 hours contiguously");
          } else {
            contiguousShifts++;
          }
        }
      }
    }

    result.setValue(contiguousShifts / (double) week.getScheduledHours());

    if (Double.isNaN(result.getValue())) {
      result.setValue(0.0);
    }

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
            result.add(Week.dayString(day) + " at " + shift.getHour() + ":00 has " + women + " women and " + men
                + " men scheduled");
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

    Scoreline result = new Scoreline(COMBINED_EXPERIENCE, 0.0);

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
            result.add(Week.dayString(day) + " at " + shift.getHour() + ":00 has an average expertise of " + average);
          }
        }
      }
    }

    result.setValue(shiftsMeetingThreshold / (double) week.getNumberOfShifts());

    return result;
  }

  /*
   * Check to make sure no staff was scheduled for a time they are not available
   * for
   */

  private static Scoreline availability(Schedule schedule) {
    boolean notAvailable = false;

    Scoreline result = new Scoreline(AVAILABILITY, 0.0);

    Week week = schedule.getWeek();
    Shift[][] shifts = week.getShifts();

    for (int day = 0; day < shifts.length; day++) {
      for (Shift shift : shifts[day]) {
        for (Employee employee : shift) {
          if (!employee.isAvailable(day, shift.getHour())) {
            notAvailable = true;
            result.add(employee.getName() + " was scheduled for " + Week.dayString(day) + " at " + shift.getHour()
                + " and is not available");
          }
        }
      }
      result.setValue(notAvailable ? -1000 : 1);
    }
    return result;
  }

}
