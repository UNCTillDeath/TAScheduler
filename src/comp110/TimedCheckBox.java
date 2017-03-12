package comp110;

import javafx.scene.control.CheckBox;

//decorator for JavaFX checkbox
public class TimedCheckBox extends CheckBox {
	private int _day;
	int _hour;

	public TimedCheckBox(int day, int hour) {
		super();
		_day = day;
		_hour = hour;
	}
	
	public int getDay(){
		return _day;
	}
	public int getHour(){
		return _hour;
	}

}
