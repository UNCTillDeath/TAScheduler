package comp110;

import com.jfoenix.controls.JFXCheckBox;




public class TimedCheckBox extends JFXCheckBox {
	
	// variables
	private int m_day;
	int m_hour;

	// functions
	public TimedCheckBox(int day, int hour) {
		super();
		this.m_day = day;
		this.m_hour = hour;
	}
	
	public int getDay(){
		return this.m_day;
	}

	public int getHour(){
		return this.m_hour;
	}
}