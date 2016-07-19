package comp110;

import java.io.IOException;

public class CSVGenRunner {

  public static void main(String[] args) throws IOException {

    String scenarioName = "real-world-approx-two-hour-chunks";
    int teamSize = 45;
    //days are zero indexed
    int startDay = 0;
    int endDay = 6;
    //hours are military time (zero indexed as well)
    int startHour = 9;
    int endHour = 21;
    int averageAvailability = 20;
    int averageCapacity = 4;

    CSVGenerator generator =
        new CSVGenerator(scenarioName, teamSize, startDay, endDay, startHour, endHour, averageAvailability, averageCapacity);
    generator.generateFiles();

  }
}
