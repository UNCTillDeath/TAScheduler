package comp110;

import java.io.Serializable;

public class Employee implements Serializable {

	private static final long serialVersionUID = 1L;
	
  private String _name;
  private String _onyen;
  private int          _capacity;
  private int          _capacityUsed;
  private boolean      _isFemale;
  private int          _level;       // 1: in 401, 2: in 410/411, 3: in major
  private int[][]      _availability;

  public Employee(String name, String onyen, int capacity, boolean isFemale, int level, int[][] availability) {
    _onyen = onyen;
    _availability = availability;
    _name = name;
    _capacity = capacity;
    _isFemale = isFemale;
    _level = level;
    _capacityUsed = 0;
  }

  public int hashCode() {
    return _name.hashCode();
  }

  public boolean equals(Employee other) {
    boolean equals = true;
    equals = equals && _name.equals(other._name);
    equals = equals && _capacity == other._capacity;
    equals = equals && _isFemale == other._isFemale;
    equals = equals && _level == other._level;
    if (equals) {
      for (int day = 0; day < _availability.length; day++) {
        for (int hour = 0; hour < _availability[day].length; hour++) {
          if (_availability[day][hour] != other._availability[day][hour]) {
            return false;
          }
        }
      }
    }
    return equals;
  }

  public String getName() {
    return _name;
  }
  
  public void setName(String name){
	  _name = name;
  }
  
  public String getOnyen() {
    return _onyen;
  }

  public int getCapacity() {
    return _capacity;
  }
  
  public void setCapacity(int capacity){
	  _capacity = capacity;
  }

  public int getCapacityUsed() {
    return _capacityUsed;
  }

  void setCapacityUsed(int capacityUsed) {
    _capacityUsed = capacityUsed;
  }

  public int getCapacityRemaining() {
    return _capacity - _capacityUsed;
  }

  public boolean getIsFemale() {
    return _isFemale;
  }

  public void setIsFemale(boolean isFemale) {
    _isFemale = isFemale;
  }

  public int getLevel() {
    return _level;
  }

  public void setLevel(int level) {
    _level = level;
  }

  public int[][] getAvailability() {
    return _availability;
  }

  public void setAvailability(int[][] availability) {
    _availability = availability;
  }

  public boolean isAvailable(int day, int hour) { // we could make day an enum
                                                  // but then we would need a
                                                  // map to calculate its
                                                  // position in the native
                                                  // array and that just seemed
                                                  // like a lot
    return _availability[day][hour] == 1 ? true : false;
  }

  public boolean isAvailable(int day, int startHour, int endHour) {
    for (int i = startHour; i <= endHour; i++) {
      if (_availability[day][i] == 0) {
        return false;
      }
    }
    return true;
  }

  public Employee copy() {
    int[][] availability = new int[_availability.length][_availability[0].length];
    for (int day = 0; day < _availability.length; day++) {
      for (int hour = 0; hour < _availability[0].length; hour++) {
        availability[day][hour] = _availability[day][hour];
      }
    }
    Employee copy = new Employee(_name, _onyen, _capacity, _isFemale, _level, availability);
    return copy;
  }
  
  public String toString(){
    return this.getName();
  }

}