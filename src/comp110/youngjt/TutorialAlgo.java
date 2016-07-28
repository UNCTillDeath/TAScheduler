package comp110.youngjt;

import java.util.ArrayList;
import java.util.Random;

import comp110.Employee;
import comp110.KarenBot;
import comp110.Schedule;
import comp110.SchedulingAlgo;
import comp110.Shift;
import comp110.Staff;
import comp110.Week;
import comp110.FXCrew.ShiftsByStaffConstraints;

public class TutorialAlgo implements SchedulingAlgo {

  public static void main(String[] args) {
    KarenBot bot = new KarenBot(new TutorialAlgo());
    String scenario = "real-world-approx-two-hour-chunks";
    int trials = 1000;
    bot.run(scenario, trials);
  }

  public Schedule run(Schedule schedule, Random random) {
    Week week = schedule.getWeek();
    Staff staff = schedule.getStaff();
    Shift[][] shifts = week.getShifts();
    ChunkList chunkList = new ChunkList();

    for (int day = 0; day < shifts.length; day++) {
      for (int hour = 0; hour < shifts[0].length; hour++) {
        for (Employee employee : staff) {
          if (employee.isAvailable(day, hour)) {
            if (hour != 23) {
              if (employee.isAvailable(day, hour + 1)) {
                chunkList.getChunks().add(new Chunk(employee, day, hour, hour + 1));
              }
            }
          }
        }
      }
    }

    ShiftsByStaffConstraints constraints = new ShiftsByStaffConstraints(schedule);
    constraints.loadSchedule(schedule);

    for (Shift shift : constraints.getSortedShifts()) {
      ArrayList<Chunk> availableChunks = chunkList.getAvailableChunks(shift);
      if (availableChunks.size() > 0) {
        Chunk chunkToSchedule = availableChunks.get(random.nextInt(availableChunks.size()));
        scheduleChunk(chunkToSchedule, shifts);
      }
    }

    // POSTPROCESS
    for (int i = 0; i < 10; i++) {
      for (int day = 0; day < shifts.length; day++) {
        for (int hour = 0; hour < shifts[0].length; hour++) {
          for (Employee employee : staff) {
            if (employee.isAvailable(day, hour) && shifts[day][hour].getCapacityRemaining() > 0 && employee.getCapacityRemaining() > 0) {
              shifts[day][hour].add(employee);
            }
          }
        }
      }
    }

    return schedule;
  }

  public void scheduleChunk(Chunk chunk, Shift[][] shifts) {
    for (int i = 0; i < chunk.getSize(); i++) {
      if (shifts[chunk.getDay()][chunk.getStartHour() + i].getCapacityRemaining() > 0 && chunk.getEmployee().isAvailable(chunk.getDay(), chunk.getStartHour() + i)
          && chunk.getEmployee().getCapacityRemaining() > 0) {
        shifts[chunk.getDay()][chunk.getStartHour() + i].add(chunk.getEmployee());
      }
    }
  }

}
