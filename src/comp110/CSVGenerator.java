package comp110;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.LinkedBlockingQueue;

public class CSVGenerator {

  public static void main(String[] args) throws IOException {

    String scenarioName = "hello-world";
    int teamSize = 10;
    int startDay = 0;
    int endDay = 6;
    int startHour = 9;
    int endHour = 10;
    int averageAvailability = 20;
    int averageCapacity = 14;

    CSVGenerator generator =
        new CSVGenerator(scenarioName, teamSize, startDay, endDay, startHour, endHour, averageAvailability, averageCapacity);
    generator.generateFiles();

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

  private void generateFiles() throws IOException {

    File path = new File("data/" + _testName);
    File outputDir = new File(path.getPath() + "/staff");

    if (!path.exists()) {
      path.mkdir();
      outputDir.mkdir();
    } else {
      // Clear existing CSV files
      for (File csv : outputDir.listFiles()) {
        if (csv.getName().contains(".csv")) {
          csv.delete();
        }
      }
    }

    Map<Boolean, Queue<String>> names = getNames();

    for (int i = 0; i < _teamSize; i++) {
      Employee employee = generateEmployee(names);
      FileWriter outputCSV =
          new FileWriter(new File(outputDir.getPath() + "/" + employee.getName().toLowerCase() + ".csv"));
      outputCSV.append(generateEmployeeProfile(employee));
      outputCSV.append(generateEmployeeSchedule());
      outputCSV.flush();
      outputCSV.close();
    }

  }

  Map<Boolean, Queue<String>> getNames() {
    Map<Boolean, Queue<String>> names = new HashMap<>();
    Queue<String> women = new LinkedBlockingQueue<String>(Arrays.asList(new String[] {
        "Karen",
        "Han Bit",
        "Kaylee",
        "Dana",
        "Meggie",
        "Caleigh",
        "Nancy",
        "Brea",
        "Katie",
        "Helen",
        "Sydney",
        "Tabatha",
        "Victoria",
        "Jennifer",
        "Saumya",
        "Kyra",
        "Sarah",
        "Abba",
        "Melissa",
        "Saster",
        "Wanyi",
        "Kate",
        "Gabi",
        "Srihita",
        "Lydia",
        "Vicky",
        "Hillary",
        "Michelle",
        "JLo",
        "Kim",
        "Katy",
        "TSwift",
        "Melinda",
        "Sheryl",
        "Meg",
        "Oprah",
        "Ruth",
        "Bonnie",
        "Ertharin",
        "Laurene",
        "Nicola",
        "Beth",
        "Amy",
        "Pollyanna",
        "Bidya",
        "Rosalind",
        "Mary",
        "Marissa" }));

    Queue<String> men = new LinkedBlockingQueue<String>(Arrays.asList(new String[] {
        "Jeffrey",
        "Muttaqee",
        "Dorian",
        "Connor",
        "Ahmed",
        "Dong",
        "Cody",
        "Grant",
        "Ben",
        "Duncan",
        "Stephen",
        "Mohamed",
        "Shane",
        "Hayden",
        "Jay",
        "Jesse",
        "Hank",
        "Mark",
        "Brooks",
        "Ahmad",
        "Kanye",
        "Zuck",
        "Gates",
        "Jobs",
        "Gary",
        "Larry",
        "Sergey",
        "Patrick",
        "John",
        "Jack",
        "Future",
        "Lil Jon",
        "Zayn",
        "Snoop" }));
    names.put(true, women);
    names.put(false, men);
    return names;
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

  private Employee generateEmployee(Map<Boolean, Queue<String>> names) {
    boolean isFemale = generateRandomInt(1, 10) > 4;
    String name = names.get(isFemale).poll();
    int capacity = generateRandomInt(0, 7, _averageCapacity, 1.5);
    int level = generateRandomInt(1, 3);
    return new Employee(name, capacity, isFemale, level, new int[0][0]);
  }

  private String generateEmployeeSchedule() {
    Week week = new Week("new");
    // min max and std dev could be specified programmatically if we wanted
    // size is the number of shifts available to be scheduled for a given person
    int size = generateRandomInt(5, ((_endDay - _startDay) * (_endTime - _startTime)), _averageAvailability, 5);
    for (int i = 0; i < size; i++) {
      int day = generateRandomInt(_startDay, _endDay);
      int hour = generateRandomInt(_startTime, _endTime);

      // check to see if we have already scheduled a shift at that time
      if (week.getShifts()[day][hour].getCapacity() == 0) {
        week.getShifts()[day][hour] = new Shift(day, hour, 1);
      }
      // else try again
      else {
        i--;
      }
    }
    return week.toCSV();
  }

  private String generateEmployeeProfile(Employee employee) {
    StringBuilder sb = new StringBuilder();
    sb.append("Name:," + employee.getName() + ",,,,,,\n");
    sb.append("Gender (enter M or F):," + (employee.getIsFemale() ? "F" : "M") + ",,,,,,\n");
    // may want to allow someone to specify their own min max and std dev
    // through constructor, this made sense for now
    sb.append("Capacity:," + employee.getCapacity() + ",,,,,,\n");
    sb.append("Level (1 - in 401; 2 - in 410/411; 3 - in major)," + employee.getLevel() + ",,,,,,\n");
    sb.append(",0. Sun,1. Mon,2. Tue,3. Weds,4. Thu,5. Fri,6. Sat\n");
    return sb.toString();
  }

}
