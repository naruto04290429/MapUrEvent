package data;

import java.time.LocalDate;
import java.time.LocalTime;

import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@XmlRootElement(name = "EventTime")
public class EventTime{
    
    private LocalDate date;
    private LocalTime time;
    
    public EventTime(LocalDate d, LocalTime t) {
    	this.setDate(d);
    	this.setTime(t);
    }
    public EventTime() {}
    
    @XmlElement
    @XmlJavaTypeAdapter(DateAdapter.class)
	public LocalDate getDate() {
		return date;
	}
	public void setDate(LocalDate date) {
		this.date = date;
	}
	
	@XmlElement
    @XmlJavaTypeAdapter(TimeAdapter.class)
	public LocalTime getTime() {
		return time;
	}
	public void setTime(LocalTime time) {
		this.time = time;
	}
}
