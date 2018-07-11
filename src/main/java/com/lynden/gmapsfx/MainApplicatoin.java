package com.lynden.gmapsfx;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;

import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXTimePicker;
import com.lynden.gmapsfx.javascript.event.UIEventType;
import com.lynden.gmapsfx.javascript.object.DirectionsPane;
import com.lynden.gmapsfx.javascript.object.GoogleMap;
import com.lynden.gmapsfx.javascript.object.InfoWindow;
import com.lynden.gmapsfx.javascript.object.InfoWindowOptions;
import com.lynden.gmapsfx.javascript.object.LatLong;
import com.lynden.gmapsfx.javascript.object.MapOptions;
import com.lynden.gmapsfx.javascript.object.MapTypeIdEnum;
import com.lynden.gmapsfx.javascript.object.Marker;
import com.lynden.gmapsfx.javascript.object.MarkerOptions;
import com.lynden.gmapsfx.service.directions.DirectionStatus;
import com.lynden.gmapsfx.service.directions.DirectionsRenderer;
import com.lynden.gmapsfx.service.directions.DirectionsResult;
import com.lynden.gmapsfx.service.directions.DirectionsServiceCallback;
import com.lynden.gmapsfx.service.elevation.ElevationResult;
import com.lynden.gmapsfx.service.elevation.ElevationServiceCallback;
import com.lynden.gmapsfx.service.elevation.ElevationStatus;
import com.lynden.gmapsfx.service.geocoding.GeocoderStatus;
import com.lynden.gmapsfx.service.geocoding.GeocodingResult;
import com.lynden.gmapsfx.service.geocoding.GeocodingService;
import com.lynden.gmapsfx.service.geocoding.GeocodingServiceCallback;

import data.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;

import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import netscape.javascript.JSObject;
import parser.EventXMLIteraction;

public class MainApplicatoin extends Application implements MapComponentInitializedListener, 
	ElevationServiceCallback, GeocodingServiceCallback, DirectionsServiceCallback {

	protected GoogleMapView mapComponent;
    protected GoogleMap map;
    protected DirectionsPane directions;
    
    //GUI
    private TextField contentText;
    private Label showClcik;
    private JFXDatePicker startDate;
    private JFXDatePicker endDate;
    private JFXTimePicker startTime;
    private JFXTimePicker endTime;
    private Button sendData;
    private Button delete;
    private Marker currentMarker;
   
    //----Data
    private String content;
    private double latitude;
    private double longitude;
    
    private int numofEvents;
    
	@Override
	//Set up the environment
    public void start(final Stage stage) throws Exception {
        mapComponent = new GoogleMapView();
        mapComponent.addMapInitializedListener(this);
        mapComponent.setDisableDoubleClick(true);
                
        BorderPane bp = new BorderPane();
        bp.setCenter(mapComponent);
        
        setupRightTab(bp);
        
        Scene scene = new Scene(bp);
        stage.setScene(scene);
        stage.show();
    }

    DirectionsRenderer renderer;
    
    @Override
    public void mapInitialized() {
        Thread t = new Thread( () -> {
           try {
               Thread.sleep(3000);
               System.out.println("Calling showDirections from Java");
               Platform.runLater(() -> mapComponent.getMap().hideDirectionsPane());
           } catch( Exception ex ) {
               ex.printStackTrace();
           }
        });
        t.start();
        //Once the map has been loaded by the Webview, initialize the map details.
        LatLong center = new LatLong(38.986849, -76.944750);
        mapComponent.addMapReadyListener(() -> {
            // This call will fail unless the map is completely ready.
            checkCenter(center);
        });
        
        MapOptions options = new MapOptions();
        options.center(center)
                .mapMarker(true)
                .zoom(9)
                .overviewMapControl(false)
                .panControl(false)
                .rotateControl(false)
                .scaleControl(false)
                .streetViewControl(false)
                .zoomControl(false)
                .mapType(MapTypeIdEnum.ROADMAP);

        map = mapComponent.createMap(options);
        
        map.setHeading(123.2);//map.showDirectionPane();
        
        System.out.println("Initializing:................");
        System.out.println("content: "+content);
        System.out.println("latitude: "+latitude);
        System.out.println("longitude: "+longitude);
        
        EventsShowonMap();
        
        //Allow the label shows the location on the label and 
        //update the current marker by clicking the mouse
        map.addUIEventHandler(UIEventType.click, (JSObject obj) -> {
            LatLong ll = new LatLong((JSObject) obj.getMember("latLng"));
            latitude = ll.getLatitude();
            longitude = ll.getLongitude();            
            showClcik.setText("latitude: "+latitude+"\n"+"longitude: "+longitude);
                		
            if(currentMarker!=null) {
            	currentMarker.setVisible(false);
            	map.removeMarker(currentMarker);
            }
            MarkerOptions currentMarkerOptions = new MarkerOptions();
            currentMarkerOptions.position(ll).visible(true);
            currentMarker = new Marker(currentMarkerOptions);
            map.addMarker(currentMarker);
        });
        // Clicking submit
        sendData.setOnAction(new EventHandler<ActionEvent>() {	
			@Override
			public void handle(ActionEvent arg0) {
				System.out.println("In the Submit......");
				content = contentText.getText();
				LocalDate date_s = startDate.getValue();
				LocalDate date_e = endDate.getValue();
		        LocalTime time_s = startTime.getValue();
		        LocalTime time_e = endTime.getValue();
		        // Avoid users mistakenly input data
		        if( content.equals(" ") || latitude==0 || longitude==0 ||
		        	date_s == null || date_e == null ||
		        	time_s == null || time_e == null ||
		        	(date_s.compareTo(date_e) > 0) ||
		        	(date_s.compareTo(date_e) == 0 && time_s.compareTo(time_e) >=0)) {
		        	Alert alert = new Alert(AlertType.INFORMATION);
		        	alert.setTitle("Input Error");
		        	alert.setHeaderText("You should enter the right input foramt.");
		        	alert.setContentText("1. Content must be filled in.\n"+
		        						 "2. Location must be pointed on the map.\n"+
		        						 "3. Time duration must be selected.\n"+
		        						 "4. Start time must be smaller then finish time.");
		        	alert.showAndWait();
		        }
		        else {
		        	System.out.println("Convert into XML......");
					Event add = new Event(content, new EventLocation(latitude,longitude), 
							new EventTime(date_s, time_s),
							new EventTime(date_e, time_e));
					contentText.setText(" ");
					try {
						EventXMLIteraction.converttoXML(add);mapInitialized();
					} catch (Exception e) { e.printStackTrace(); }
		        }
			}
        });
        
        // Clicking delete
        delete.setOnAction(new EventHandler<ActionEvent>() {
        	@Override
			public void handle(ActionEvent arg0) {
        		content = contentText.getText();
        		if( content.equals(" ") ) {
        			Alert alert = new Alert(AlertType.INFORMATION);
		        	alert.setTitle("Input Error");
		        	alert.setHeaderText("You should enter the right input foramt.");
		        	alert.setContentText("Content must be filled in.");
		        	alert.showAndWait();
        		}
        		else {
        			EventXMLIteraction.deleteXML(content);
        			mapInitialized();
        		}
        		
        	}
        });	
    }
	
	
    private void checkCenter(LatLong center) {}
    
    /**
     * The main() method is ignored in correctly deployed JavaFX application.
     * main() serves only as fallback in case the application can not be
     * launched through deployment artifacts, e.g., in IDEs with limited FX
     * support. NetBeans ignores main().
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        System.setProperty("java.net.useSystemProxies", "true");
        launch(args);
    }

    @Override
    public void elevationsReceived(ElevationResult[] results, ElevationStatus status) {
        if(status.equals(ElevationStatus.OK)){
            for(ElevationResult e : results){
                System.out.println(" Elevation on "+ e.getLocation().toString() + " is " + e.getElevation());
            }
        }
    }

    @Override
    public void geocodedResultsReceived(GeocodingResult[] results, GeocoderStatus status) {
        if(status.equals(GeocoderStatus.OK)){
            for(GeocodingResult e : results){
                System.out.println(e.getVariableName());
                System.out.println("GEOCODE: " + e.getFormattedAddress() + "\n" + e.toString());
            }
        }
        
    }

    @Override
    public void directionsReceived(DirectionsResult results, DirectionStatus status) {
        if(status.equals(DirectionStatus.OK)){
            
            System.out.println("OK");
            
            DirectionsResult e = results;
            new GeocodingService();
            
            System.out.println("SIZE ROUTES: " + e.getRoutes().size() + "\n" + "ORIGIN: " + e.getRoutes().get(0).getLegs().get(0).getStartLocation());
            System.out.println("LEGS SIZE: " + e.getRoutes().get(0).getLegs().size());
            System.out.println("WAYPOINTS " +e.getGeocodedWaypoints().size());
            
            try{
                System.out.println("Distancia total = " + e.getRoutes().get(0).getLegs().get(0).getDistance().getText());
            } catch(Exception ex){
                System.out.println("ERRO: " + ex.getMessage());
            }
            System.out.println("LEG(0)");
            System.out.println(e.getRoutes().get(0).getLegs().get(0).getSteps().size());
            
            System.out.println(renderer.toString());
        }
    }
   
    //Set up the GUI
    private void setupRightTab(BorderPane bp) {
    	VBox background = new VBox();
    	Label evetLabel;
    	//Event
    	evetLabel = new Label("Event:");
        contentText = new TextField ();
        contentText.setText(" ");
        HBox hb = new HBox();
        hb.getChildren().addAll(evetLabel, contentText);
        hb.setSpacing(10);
        
        //Location
        showClcik = new Label();
        
        //Date
        startDate = new JFXDatePicker();
        endDate = new JFXDatePicker();
        
        //Time
        startTime = new JFXTimePicker();
        endTime = new JFXTimePicker();

        //Container of date and time
        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        Label label1 = new Label("From");
        Label label2 = new Label("To");
        gridPane.add(label1, 0, 0);
        gridPane.add(label2, 0, 3);
        gridPane.add(startDate, 0, 1);
        gridPane.add(startTime, 0, 2);
        gridPane.add(endDate, 0, 4);
        gridPane.add(endTime, 0, 5);
        //Submit
        sendData = new Button("Submit");
        
        //Delete
        delete = new Button("Delete");
        
        //Background Setting
        background.setPadding(new Insets(0, 20, 10, 20));
        background.setSpacing(10);
        background.getChildren().addAll(evetLabel, contentText, new Label("Locatoin: "), showClcik, gridPane, sendData,delete);
        bp.setLeft(background);
    }
    
    //Set up the event information on the map
    private void EventsShowonMap() {
    	
    	ArrayList<Event> events = EventXMLIteraction.retrievefromXML();
        numofEvents = events.size();
        
        for(int i=0; i<numofEvents;i++) {
        	Event e = events.get(i);
        
	    	MarkerOptions temp = new MarkerOptions();
	        LatLong markerLatLong = new LatLong(e.getPlace().getLatitude(), e.getPlace().getLongitude());
	        temp.position(markerLatLong)
	        .visible(true);
	        
	        Marker tempMarker = new Marker(temp);
	        
	        map.addMarker(tempMarker);
	        
	        EventTime ss, ee;
	        ss = e.getStart();
	        ee = e.getEnd();
	        
	        InfoWindowOptions infoOptions = new InfoWindowOptions();
	        infoOptions.content("<h3>Event: "+e.getContent()+"<br><br>"+
	                ss.getDate().toString()+" "+ss.getTime().toString()+" to"+"<br>"+
	                ee.getDate().toString()+" "+ee.getTime().toString()+"</h3>")
	        		.position(markerLatLong);
	
	        InfoWindow window = new InfoWindow(infoOptions);
	        map.addUIEventHandler(tempMarker, UIEventType.click, (JSObject obj) -> {
	        	window.open(map);
	        });
        }
    }
}
