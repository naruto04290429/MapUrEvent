package data;

import javax.xml.bind.annotation.*;

@XmlRootElement(name = "Event")
public class Event {
	
	private String content;
	private EventLocation place;
	private EventTime start;
	private EventTime end;
	
	public Event(String content, EventLocation place, EventTime start, EventTime end) {
		this.setContent(content);
		this.setPlace(place);
		this.start = start;
		this.end = end;
	}
	
	public Event() {}  
	
	@XmlElement
	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
	
	@XmlElement
	public EventLocation getPlace() {
		return place;
	}

	public void setPlace(EventLocation place) {
		this.place = place;
	}
	
	@XmlElement
	public EventTime getStart() {
		return start;
	}
	public void setStart(EventTime start) {
		this.start = start;
	}
	
	@XmlElement
	public EventTime getEnd() {
		return end;
	}
	public void setEnd(EventTime end) {
		this.end = end;
	}
}
