package comp110;

public class Employee {

  private String _name;
  private int _capacity;
  private boolean _isFemale;
  private int _level; // 1: in 401, 2: in 410/411, 3: in major
  private int[][] _availability;

  public Employee(String name, int capacity, boolean isFemale, int level, int[][] availability) {
    _availability = availability;
    _name = name;
    _capacity = capacity;
    _isFemale = isFemale;
    _level = level;
  }

  public String getName() {
    return _name;
  }

  public void setName(String name) {
    _name = name;
  }

  public int getCapacity() {
    return _capacity;
  }

  public void setCapacity(int capacity) {
    _capacity = capacity;
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
  
  public int[][] getAvailability(){
    return _availability;
  }
  
  public void setAvailability(int[][] availability){
    _availability = availability;
  }

}