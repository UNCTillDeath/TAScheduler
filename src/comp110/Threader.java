package comp110;

import java.util.ArrayList;
import java.util.List;

public class Threader {

  private static List<Thread> _threads;

  public Threader(KarenBot bot, int numberOfThreads, String scenario, int trials) {
    _threads = new ArrayList<Thread>();
    for (int i = 0; i < numberOfThreads; i++) {
      _threads.add(new Thread(new KarenBotThread(bot.copy(), scenario, (trials / numberOfThreads))));
    }
    for (Thread t : _threads) {
      t.start();
    }

  }

}
