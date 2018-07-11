package data;
import javax.xml.bind.annotation.*;

@XmlRootElement(name = "EventLocation")
public class EventLocation {
	private double latitude;
    private double longitude;
    
    public EventLocation(double la, double lo) {
    	this.setLatitude(la);
    	this.setLongitude(lo);
    }
    public EventLocation() {}
    
    @XmlElement
	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}
	@XmlElement
	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
}
