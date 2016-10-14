package comp110.youngjt;

import java.util.Random;

import comp110.KarenBot;
import comp110.Schedule;
import comp110.SchedulingAlgo;

public class VerifyCapacity implements SchedulingAlgo {
  public static void main(String[] args) {
    KarenBot bot = new KarenBot(new VerifyCapacity());
    String scenario = "week1";
    int trials = 1;
    //args don't matter except for passing true so we get the csv output
    bot.runForKaren(scenario, 2, 15, true);
  }

  @Override
  public Schedule run(Schedule input, Random random) {
    return null;
  }
}
