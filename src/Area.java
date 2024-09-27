// There are two times in the program where it is useful to add properties to an area object before 
// using these properties to do operations concerning the database.
public class Area {
	
	//name field
	private String name;
	
	// x coordinate field
	private double XCoordinate;
	
	// y coordinate field
	private double YCoordinate;


	// Set and get name
	public void setName(String _name) {
		name = _name;

	}

	public String getName() {
		return name;
	}

	// Get and set xCoordinate
	public double getXCoordinate() {
		return XCoordinate;
	}

	public void setXCoordinate(double xCoordinate) {
		XCoordinate = xCoordinate;
	}

	// Get and set YCoordinate
	public double getYCoordinate() {
		return YCoordinate;
	}

	public void setYCoordinate(double yCoordinate) {
		YCoordinate = yCoordinate;
	}
}

