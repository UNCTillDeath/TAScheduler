package comp110;

public class KarenBotThread implements Runnable {

  KarenBot _bot;
  String   _scenario;
  int      _trials;

  public KarenBotThread(KarenBot bot, String scenario, int trials) {
    _bot = bot;
    _scenario = scenario;
    _trials = trials;
  }

  @Override
  public void run() {
    _bot.run(_scenario, _trials);

  }

}
