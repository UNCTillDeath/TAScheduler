package comp110;

public class Preference {

  private final String _day;

  private final byte _hour;

  public Preference(String day, byte hour) {
    _day = day;
    _hour = hour;
  }

  public String getDay() {
    return _day;
  }

  public byte getHour() {
    return _hour;
  }

}
