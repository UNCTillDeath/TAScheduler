package comp110;

import java.io.IOException;

public class CSVGenRunner {

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
}
