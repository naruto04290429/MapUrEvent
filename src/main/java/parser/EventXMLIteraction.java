package parser;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import data.Event;
import javafx.event.ActionEvent;
import javafx.stage.FileChooser;

public class EventXMLIteraction {  
	
	public static void converttoXML(Event input) throws Exception{  
	    JAXBContext contextObj = JAXBContext.newInstance(Event.class);  
	  
	    Marshaller marshallerObj = contextObj.createMarshaller();  
	    marshallerObj.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);  
	  
	    Event e = input;
	    File saveFolder = new File("enentData");
	    saveFolder.mkdir();
	    File saveData = null;
	    int num = 1;
	    while(true) {
	    	saveData = new File(saveFolder,"event"+num+".xml");
	    	if(saveData.exists()) 
	    		num = num + 1;
	    	else break;
	    }
	    marshallerObj.marshal(e, new FileOutputStream(saveData));    
	}  

	
	private static ArrayList<File> LoadXml() throws Exception{
		File folder = new File("enentData/");
		File[] listOfFiles = folder.listFiles();
		ArrayList<File> xmlfiles = new ArrayList<>();
		
		for(int i = 0; i < listOfFiles.length; i++){
			String filename = listOfFiles[i].getName();
			if(filename.endsWith(".xml")||filename.endsWith(".XML")) {
				xmlfiles.add(listOfFiles[i]); 
			}
		}
		return xmlfiles;
	}


	public static ArrayList<Event> retrievefromXML() {  
		ArrayList<Event> events = new ArrayList<>();
		try {
			ArrayList<File> test = LoadXml();
			for(int i=0; i<test.size();i++) {
				File temp = test.get(i);    
		        JAXBContext jaxbContext = JAXBContext.newInstance(Event.class);    
		        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();    
		        Event e = (Event) jaxbUnmarshaller.unmarshal(temp);
		        events.add(e);
			}  
		} catch (Exception e) { e.printStackTrace(); }
		return events;
	}
	
	public static void deleteXML(String content) {
		ArrayList<Event> events = new ArrayList<>();
		try {
			ArrayList<File> test = LoadXml();
			int deleteIndex = 0;
			for(int i=0; i<test.size();i++) {
				File temp = test.get(i);    
		        JAXBContext jaxbContext = JAXBContext.newInstance(Event.class);    
		        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();    
		        Event e = (Event) jaxbUnmarshaller.unmarshal(temp);
		        //events.add(e);
		        if (e.getContent().equals(content)) {
		        	deleteIndex = i+1;
		        	break;
		        }
			}
			if(deleteIndex != 0) {
				File deleteFile = new File("enentData/event"+deleteIndex+".xml");
				deleteFile.delete();
			}
		} catch (Exception e) { e.printStackTrace(); }
	}
}  