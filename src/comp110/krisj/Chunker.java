package comp110.krisj;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import comp110.Employee;
import comp110.Schedule;
import comp110.Week;

public class Chunker {

  private Schedule _schedule;
  private List<Chunk> _chunks;

  public Chunker() {
    _chunks = new ArrayList<Chunk>();
  }

  public void loadSchedule(Schedule schedule) {
    _schedule = schedule;

    for (Employee employee : _schedule.getStaff()) {
      Employee clone = employee.copy(); // We'll copy to avoid mutating original
      int[][] availability = clone.getAvailability();
      for (int day = 0; day < availability.length; day++) {
        for (int hour = 0; hour < availability[day].length; hour++) {
          if (availability[day][hour] >= 1) {
            // We've found the start of a contiguous chunk!
            int start = hour;
            // We're going to try to find the end of this chunk now.
            int end; // Declaring end outside of for so that it is visible after
                     // for completes
            for (end = start; end < availability[day].length; end++) {
              if (end - start < 3 && availability[day][end] != 0) {
                // We're still within the chunk, so we're going set the
                // availability to 0 to avoid double counting the chunk when we
                // iterate to the next hour.
                availability[day][end] = 0;
              } else {
                // We've found the end of the chunk
                break;
              }
            }
            end -= 1; // We either counted 1 past the end of the chunk or the
                      // end of the array.
            _chunks.add(new Chunk(employee, day, start, end));
          }
        }
      }
    }

    // Remove chunks of only one hour
    _chunks = _chunks.stream().filter((chunk) -> chunk.getStart() < chunk.getEnd()).collect(Collectors.toList());
  }

  public List<Chunk> chunksFor(int day, int hour) {
    List<Chunk> chunks = new ArrayList<Chunk>();
    for (Chunk chunk : _chunks) {
      if (chunk.intersects(day, hour)) {
        chunks.add(chunk);
      }
    }
    return chunks;
  }

  public void scheduleChunk(Schedule schedule, Chunk chunk) {
    Week week = schedule.getWeek();

    for (int hour = chunk.getStart(); hour <= chunk.getEnd(); hour++) {
      if (chunk.getEmployee().getCapacityRemaining() > 0) {
        week.getShift(chunk.getDay(), hour).add(chunk.getEmployee());
      }
    }

    // This is a hack. I'm not sure what is broken that doesn't allow us to simply do:
    // _chunks.remove(chunk)
    for (int i = 0; i < _chunks.size(); i++) {
      Chunk c = _chunks.get(i);
      if (c.equals(chunk)) {
        _chunks.remove(i);
        return;
      }
    }
  }

  public Schedule toSchedule() {
    return _schedule;
  }

}
