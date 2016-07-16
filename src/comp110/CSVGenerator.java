package comp110;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

public class CSVGenerator {

  public static void main(String[] args) throws IOException {

    String scenarioName = "auto-test";
    int teamSize = 40;
    int startDay = 0;
    int endDay = 6;
    int startHour = 9;
    int endHour = 21;
    int averageAvailability = 20;
    int averageCapacity = 5;

    CSVGenerator generator =
        new CSVGenerator(scenarioName, teamSize, startDay, endDay, startHour, endHour, averageAvailability, averageCapacity);
    generator.generateCSV();

  }

  private String _testName;
  private int _teamSize;
  private int _startDay;
  private int _endDay;
  private int _startTime;
  private int _endTime;
  private int _averageAvailability;
  private int _averageCapacity;

  public CSVGenerator(String testName, int teamSize, int startDay, int endDay, int startTime, int endTime,
      int averageAvailability, int averageCapacity) throws IOException {
    _testName = testName;
    _teamSize = teamSize;
    _startDay = startDay;
    _endDay = endDay;
    _startTime = startTime;
    _endTime = endTime;
    _averageAvailability = averageAvailability;
    _averageCapacity = averageCapacity;
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

  private void generateCSV() throws IOException {

    File path = new File("data/" + _testName);
    File outputDir = new File(path.getPath() + "/staff");

    if (!path.exists()) {
      path.mkdir();
      outputDir.mkdir();
    }

    for (int i = 0; i < _teamSize; i++) {
      FileWriter outputCSV = new FileWriter(new File(outputDir.getPath() + "/" + i + ".csv"));
      outputCSV.append("Name:," + i + ",,,,,,\n");
      outputCSV.append("Gender (enter M or F):," + (generateRandomInt(0, 1) == 1 ? "M" : "F") + ",,,,,,\n");
      // may want to allow someone to specify their own min max and std dev
      // through constructor, this made sense for now
      outputCSV.append("Capacity:," + generateRandomInt(0, 7, _averageCapacity, 1.5) + ",,,,,,\n");
      outputCSV.append("Level (1 - in 401; 2 - in 410/411; 3 - in major)," + generateRandomInt(1, 3) + ",,,,,,\n");
      outputCSV.append(",0. Sun,1. Mon,2. Tue,3. Weds,4. Thu,5. Fri,6. Sat\n");

      // generate schedule
      outputCSV.append(generateEmployeeSchedule());

      outputCSV.flush();
      outputCSV.close();
    }

  }

  /*
   * Helper method that generates a random number between min and max
   * (inclusive)
   */
  private static int generateRandomInt(int min, int max) {
    Random rand = new Random();
    return rand.nextInt((max - min) + 1) + min;

  }

  /*
   * Helper method that generates a number between min and max (inclusive)
   * around a given mean
   * http://stackoverflow.com/questions/2751938/random-number-within-a-range-
   * based-on-a-normal-distribution - idk
   */
  private static int generateRandomInt(int min, int max, int mean, double standardDeviation) {
    Random rand = new Random();

    double r = Math.sqrt(-2 * Math.log(rand.nextDouble()));
    double theta = 2 * Math.PI * rand.nextDouble();
    double rand1 = r * Math.cos(theta);
    double rand2 = r * Math.sin(theta);

    // randomly pick between the two generated values
    double normalizedRandom = Math.random() > 0.5 ? rand2 : rand1;

    // now we scale the normalized number around our mean and standard deviation
    int randomOutput = (int) (normalizedRandom * standardDeviation) + mean + 1;

    // sanity check on our randomly generated value
    if (randomOutput > max) {
      return max;
    } else if (randomOutput < min) {
      return min;
    } else {
      return randomOutput;
    }
  }

  private String generateEmployeeSchedule() {
    Week week = new Week("new");
    // min max and std dev could be specified programmatically if we wanted
    // size is the number of shifts available to be scheduled for a given person
    int size = generateRandomInt(5, 40, _averageAvailability, 5);
    for (int i = 0; i < size; i++) {
      int day = generateRandomInt(_startDay, _endDay);
      int hour = generateRandomInt(_startTime, _endTime);
      week.getShifts()[day][hour] = new Shift(day, hour, 1);

    }
    return week.toCSV();
  }

}
