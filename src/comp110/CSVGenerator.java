package comp110;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;


public class CSVGenerator {
  
  private String _testName;
  private int _teamSize;
  private int _startDay;
  private int _endDay;
  private int _startTime;
  private int _endTime;
  private int _averageAvailability;
  private int _averageCapacity;
  
  public CSVGenerator(String testName, int teamSize, int startDay, int endDay,
      int startTime, int endTime, int averageAvailability, int averageCapacity) throws IOException {
    _testName = testName;
    _teamSize = teamSize;
    _startDay = startDay;
    _endDay = endDay;
    _startTime = startTime;
    _endTime = endTime;
    _averageAvailability = averageAvailability;
    _averageCapacity = averageCapacity;
    
    generateCSV();
  }
  
  public String getTestName() {
    return _testName;
  }

  public void setTestName(String testName) {
    _testName = testName;
  }

  public int getTeamSize() {
    return _teamSize;
  }

  public void setTeamSize(int teamSize) {
    _teamSize = teamSize;
  }

  public int getStartDay() {
    return _startDay;
  }

  public void setStartDay(int startDay) {
    _startDay = startDay;
  }

  public int getEndDay() {
    return _endDay;
  }

  public void setEndDay(int endDay) {
    _endDay = endDay;
  }

  public int getStartTime() {
    return _startTime;
  }

  public void setStartTime(int startTime) {
    _startTime = startTime;
  }

  public int getEndTime() {
    return _endTime;
  }

  public void setEndTime(int endTime) {
    _endTime = endTime;
  }

  public int getAverageAvailability() {
    return _averageAvailability;
  }

  public void setAverageAvailability(int averageAvailability) {
    _averageAvailability = averageAvailability;
  }

  public int getAverageCapacity() {
    return _averageCapacity;
  }

  public void setAverageCapacity(int averageCapacity) {
    _averageCapacity = averageCapacity;
  }

  private void generateCSV() throws IOException{
    File path = new File("data/" + _testName);
    File outputDir = new File(path.getPath() + "/staff");
    
    if(!path.exists()){
      path.mkdir();
      outputDir.mkdir();
    }
    
    for(int i = 0; i < _teamSize; i++){
      FileWriter outputCSV = new FileWriter(new File(outputDir.getPath() + "/" + i + ".csv"));
      outputCSV.append("Name:," + i + ",,,,,,\n");
      outputCSV.append("Gender (enter M or F):," + (generateRandomInt(0, 1) == 1 ? "M" : "F") + ",,,,,,\n");
      outputCSV.append("Level (1 - in 401; 2 - in 410/411; 3 - in major)," + generateRandomInt(1, 3) + ",,,,,,");
      
      
      outputCSV.flush();
      outputCSV.close();
    }
    
  }
  
  /*
   * Helper method that generates a random number between min and max (inclusive)
   */
  private static int generateRandomInt(int min, int max){
    Random rand = new Random();
    
    return rand.nextInt((max - min) + 1) + min;
    
  }
  
}
