package data;

import java.time.LocalTime;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class TimeAdapter extends XmlAdapter<String, LocalTime> {
	@Override
	public String marshal( LocalTime date ) throws Exception {
		return date.toString();
	}
	@Override
	public LocalTime unmarshal( String date ) throws Exception {
		return LocalTime.parse( date );
	}
}