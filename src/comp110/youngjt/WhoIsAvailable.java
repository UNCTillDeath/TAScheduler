package comp110.youngjt;

import java.util.Random;

import comp110.KarenBot;
import comp110.Schedule;
import comp110.SchedulingAlgo;

public class WhoIsAvailable implements SchedulingAlgo {
  public static void main(String[] args) {
    KarenBot bot = new KarenBot(new WhoIsAvailable());
    String scenario = "spring-17";
    int trials = 1;
    bot.runForKaren(scenario, 0, 12, false);
  }

  @Override
  public Schedule run(Schedule input, Random random) {
    return null;
  }
}
