package comp110.FXCrew;

import java.util.ArrayList;

import comp110.Shift;

public class ChunkList {

  private ArrayList<Chunk> _chunks;

  public ChunkList() {
    _chunks = new ArrayList<Chunk>();
  }

  public ArrayList<Chunk> getChunks() {
    return _chunks;
  }

  public ArrayList<Chunk> getAvailableChunks(Shift shift) {
    ArrayList<Chunk> availableChunks = new ArrayList<Chunk>();

    for (Chunk chunk : _chunks) {
      if ((chunk.getDay() == shift.getDay()) && (chunk.getStartHour() == shift.getHour())) {
        availableChunks.add(chunk);
      }
    }
    return availableChunks;
  }
}
