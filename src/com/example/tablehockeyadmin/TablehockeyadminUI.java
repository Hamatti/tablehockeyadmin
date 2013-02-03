package com.example.tablehockeyadmin;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import com.admin.data.TNMTParser;
import com.admin.db.DBConnection;
import com.vaadin.annotations.Theme;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.util.sqlcontainer.SQLContainer;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.server.FileResource;
import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.Upload;
import com.vaadin.ui.Upload.Receiver;
import com.vaadin.ui.Upload.SucceededEvent;
import com.vaadin.ui.Upload.SucceededListener;
import com.vaadin.ui.VerticalLayout;


@Theme("reindeer")
/**
 * Main UI class
 */

@SuppressWarnings("serial")
public class TablehockeyadminUI extends UI {

	public static final String UPLOADFOLDER = "" ;
	private SQLContainer cor;
	private static String leagueName;	
	
	/**
	 * @return the leagueName
	 */
	public static String getLeagueName() {
		return leagueName;
	}

	@Override
	protected void init(VaadinRequest request) {
		/* Create main layout */
		VerticalLayout layout = new VerticalLayout();
		layout.setMargin(true);
		setContent(layout);
		
		/* Create header from image */
		
		FileResource resource = new FileResource(new File("D:/workspace/tablehockeyadmin/WebContent/VAADIN/themes/hockey/img/header.png"));
		Embedded banner = new Embedded("", resource);
		
		/* Create tab-navigation */
		TabSheet nav = new TabSheet();
		nav.addTab(this.getOverviewTab()).setCaption("SARJATAULUKOT");
		//nav.addTab(this.getGamesTab()).setCaption("OTTELUT");
		//nav.addTab(this.getPlayersTab()).setCaption("PELAAJAT");
		nav.addTab(this.getUploadTab()).setCaption("TIEDOSTONHALLINTA");
		
		nav.setSizeFull();
		layout.addComponent(banner);
		layout.addComponent(nav);
			
	}

	/** 
	 * TODO: List players from database
	 * @return HorizontalSplitPanel for tab view
	 */
	@SuppressWarnings("unused")
	private Component getPlayersTab() {
		HorizontalSplitPanel hor = new HorizontalSplitPanel();
		Table players = new Table();
		players.setSizeFull();
		players.setSortEnabled(true);
		SQLContainer cor = DBConnection.getStandings(0);
		players.setContainerDataSource(cor);
		hor.addComponent(players);
		
		return hor;
	}
	
	/**
	 * Contains front page with standings table
	 * @return
	 */
	private Component getOverviewTab() {
		final HorizontalSplitPanel split = new HorizontalSplitPanel();
		split.setSplitPosition(45, Unit.PERCENTAGE);
		split.setLocked(true);
		
		/* Layout for left side of split panel */
		VerticalLayout leftside = new VerticalLayout();
		leftside.setMargin(true);
		
		/* Radio button group for different leagues */
		final OptionGroup leagues = new OptionGroup("Select league");
		leagues.addItem("Turun liiga");
		leagues.addItem("Naantalin liiga");
		leagues.addItem("P‰‰skyvuoren liiga");
		
		/* For layout reasons - option group can be made horizontal if using themes and css
		 * I have to ask someone about this
		 * https://vaadin.com/book/-/page/components.selecting.html
		 */
		leagues.addStyleName("horizontal");
		leagues.setImmediate(true);
		
		/* Table for showing standings data */
		final Table table = new Table();
		table.setSizeFull();
		table.setSortEnabled(true);
		table.setWidth(100, Unit.PERCENTAGE);
		
		/* Initial database query for table data */
		cor = DBConnection.getStandings(2);
		table.setContainerDataSource(cor);
		
		/* 
		 * Listener for clicking individual player and showing his/her games on the right side of split panel 
		 */
		table.addItemClickListener(new ItemClickEvent.ItemClickListener() {
            
            public void itemClick(ItemClickEvent event) {
            		String name = event.getItemId().toString();
            		Label description = new Label("Matchtype: 1 = runkosarja, 2 = pudotuspelit <br />" +
            									  "Extra: j = jatkoaika, dq = hyl‰tty, lv = luovutusvoitto", ContentMode.HTML);
            		SQLContainer games = DBConnection.getGames(name);
            		Table gametable = new Table();
            		gametable.setContainerDataSource(games);
            		VerticalLayout gameLayout = new VerticalLayout();
            		gameLayout.setSizeFull();
            		gameLayout.setMargin(true);
            		gameLayout.addComponent(description);
            		gameLayout.addComponent(gametable);
            		split.setSecondComponent(gameLayout);
            }
        });
		
		/* Button and it's actions for changin wanted league
		 * TODO: Make changing based on database's id values instead of hard coded
		 */
		Button btn = new Button("Change");		
		btn.addClickListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				String choice = (String) leagues.getValue();
				if(choice.equals("Turun liiga")) {
					cor = DBConnection.getStandings(2);
				}
				else if(choice.equals("Naantalin liiga")) {
					cor = DBConnection.getStandings(1);
				}
				else {
					cor = DBConnection.getStandings(3);
				}
				
				/* For resetting data */
				table.setContainerDataSource(null);
				table.setContainerDataSource(cor);
				table.setSelectable(true);
			}
		});
		
		/* 
		 * Add components to layouts 
		 */
		leftside.addComponent(leagues);
		leftside.addComponent(btn);
		leftside.addComponent(table);
		split.setFirstComponent(leftside);
		return split;
	}
	
	/* Currently not in use */
	@SuppressWarnings("unused")
	private Component getGamesTab() {
		GridLayout main = new GridLayout(1, 1);
		main.setSizeFull();
		main.setMargin(true);
		
		Table table = new Table();
		table.setContainerDataSource(cor);
		table.setWidth(50, Unit.PERCENTAGE);
		
		main.addComponent(table);
		return main;
	}
	
	/* 
	 * Tab for adding and removing tournaments
	 */
	@SuppressWarnings("deprecation")
	private Component getUploadTab() {
		HorizontalSplitPanel split = new HorizontalSplitPanel();
		split.setSplitPosition(40, Unit.PERCENTAGE);
		split.setLocked(true);
		split.setWidth(100, Unit.PERCENTAGE);
		split.setHeight(500, Unit.PIXELS);
		
		VerticalLayout leftside = new VerticalLayout();
		
		FormLayout uploadform = new FormLayout();
		uploadform.setMargin(true);
		uploadform.setSizeUndefined();
		
		FormLayout removeform = new FormLayout();
		removeform.setSizeFull();
		removeform.setMargin(true);
		
		HorizontalLayout rightside = new HorizontalLayout();
		rightside.setMargin(true);
		rightside.setSizeFull();
		
		final TextField tf = new TextField("Liigan nimi");
		tf.setImmediate(true);
		
		/* 
		 * Listen for value change on dropbox menu
		 */
		tf.addListener(new Property.ValueChangeListener() {
		    public void valueChange(ValueChangeEvent event) {
		        // Add selected value to attribute leagueName
		        leagueName = (String) tf.getValue();
		    }
		});
		
		/*
		 * Information for upload
		 */
		Label uploaddescription = new Label("Filename must be in format [leaguename]_[YYYYMMDD].tnmt");
		Upload upload = new Upload();
		upload.setButtonCaption("L‰het‰ turnaustiedosto");
		upload.setCaption("Tiedosto");
		
		final ComboBox select = new ComboBox("Valitse poistettava turnaus");
		/* TODO: get list of all tournaments on database and add them to select */
		
		ArrayList<String> tournaments = DBConnection.getTournaments();
		if (tournaments != null) {
			for (String s : tournaments) {
				select.addItem(s);
			}
		}
		
		Button remove = new Button("Poista turnaus");
		
		remove.addClickListener(new ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				String tournamentfilename = (String)select.getValue();
				DBConnection.removeTournament(tournamentfilename);
			}
		});
		
		final TNMTUploader uploader = new TNMTUploader(); 
		upload.setReceiver(uploader);
		upload.addListener(uploader);
				
		Label filelist = getFiles();
		
		uploadform.addComponent(tf);
		uploadform.addComponent(uploaddescription);
		uploadform.addComponent(upload);
		
		removeform.addComponent(select);
		removeform.addComponent(remove);
		
		leftside.addComponent(uploadform);
		leftside.addComponent(removeform);
		rightside.addComponent(filelist);
		
		split.setFirstComponent(leftside);
		split.setSecondComponent(rightside);
		
		return split;
	}
	
	public Label getFiles() {
		String files = "";
		File folder = new File(UPLOADFOLDER);
		File[] listOfFiles = folder.listFiles(); 
		for (int i = 0; i < listOfFiles.length; i++) {
			File f = listOfFiles[i]; 
			if (f.isFile() && listOfFiles[i].getName().endsWith(".tnmt"))	{
				String[] tourn = f.getName().split("_");
				files += (tourn[0] + " @ " + tourn[1].substring(0,4) + "/" + tourn[1].substring(4,6) + "/" + tourn[1].substring(6,8) +": " + f.getName()) + "<br />";
			}
			
		}
		Label filelist = new Label(files, ContentMode.HTML);
		return filelist;
	}
}



@SuppressWarnings("serial")
class TNMTUploader implements Receiver, SucceededListener {
    public File file;
    
    @SuppressWarnings("deprecation")
	public OutputStream receiveUpload(String filename,
                                      String mimeType) {
        // Create upload stream
        FileOutputStream fos = null; // Stream to write to
        try {
            // Open the file for writing.
            file = new File(TablehockeyadminUI.UPLOADFOLDER + filename);
            fos = new FileOutputStream(file);
        } catch (final java.io.FileNotFoundException e) {
            Notification.show(
                    "Could not open file<br/>", e.getMessage(),
                    Notification.TYPE_ERROR_MESSAGE);
            return null;
        }
        return fos; // Return the output stream to write to
    }

	@SuppressWarnings("deprecation")
	@Override
	public void uploadSucceeded(SucceededEvent event) {
		
		TNMTParser parser = new TNMTParser();
		parser.parse(TablehockeyadminUI.UPLOADFOLDER + event.getFilename());
				
		DBConnection.writeToDatabase(parser, TablehockeyadminUI.getLeagueName());
		Notification.show("File " + event.getFilename() + " succesfully uploaded", Notification.TYPE_TRAY_NOTIFICATION);
		
		
		
		
	}
};