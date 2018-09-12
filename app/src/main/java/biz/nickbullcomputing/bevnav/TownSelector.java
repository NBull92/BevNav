package biz.nickbullcomputing.bevnav;

public class TownSelector {
	private String _locationID;
	private String _locationName;

	public TownSelector(String locationID, String locationName) {
		_locationID = locationID;
		_locationName = locationName;
	}

	public String getID() {
		return _locationID;
	}

	public String getName() {
		return _locationName;
	}
}
