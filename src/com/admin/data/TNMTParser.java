package com.admin.data;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class TNMTParser {
	
	private BufferedReader bufread;
	private ArrayList<Player> players;
	private ArrayList<Game> games, tens;
	private int nroOfPlayers;
	private String filename;
	


	/** 
	* Constructs a parser for reading TNMT tournament files
	*/
	public TNMTParser() {
		players = new ArrayList<Player>();
		games = new ArrayList<Game>();
		tens = new ArrayList<Game>();
		
	}
	
	/**
	 * @return the players
	 */
	public ArrayList<Player> getPlayers() {
		return players;
	}

	/**
	 * @param players the players to set
	 */
	public void setPlayers(ArrayList<Player> players) {
		this.players = players;
	}

	/**
	 * @return the games
	 */
	public ArrayList<Game> getGames() {
		return games;
	}

	/**
	 * @param games the games to set
	 */
	public void setGames(ArrayList<Game> games) {
		this.games = games;
	}

	/**
	 * @return the filename
	 */
	public String getFilename() {
		return filename;
	}

	/**
	 * @param filename the filename to set
	 */
	public void setFilename(String filename) {
		this.filename = filename;
	}

	/**
	* Opens a .tnmt file
	* @param filename path and filename for file to open
	* @return true if file and only if file was opened correctly
	*/
	public boolean open() {
		try {
			this.bufread = new BufferedReader(new InputStreamReader(new FileInputStream(filename), "UTF8"));
			return true;
		}
		catch(Exception e) {
			return false;
		}		
	}
	
	/** 
	* Reads through a .tnmt file and calls stat updates
	* @param parser parser with wanted file open
	*/
	public void parse(String filename) {
		this.filename = filename;  
		open();
		try {
			while (bufread.ready()) {
				String line = bufread.readLine();
				String[] parts = line.split(":");
				
				if(parts[0].equals("PLAYERS")) {
					nroOfPlayers = Integer.parseInt(parts[1]);
					for (int i = 0; i < nroOfPlayers; i++) {
						String line2 = bufread.readLine();
						String playerName = line2.split(":")[0];
												
						if(!isFound(playerName)) {
							players.add(new Player(playerName));
						}
					}
				}
				
				if(parts[0].equals("ROUND")) {
					for (int i = 0; i < ((nroOfPlayers/2)+1); i++) {
						String line3 = bufread.readLine();
						String[] parts3 = line3.split(":");
						Game g = null;
						if (parts3.length == 4 || parts3.length == 5) {
							Player home = getPlayer(parts3, 0);
							Player away = getPlayer(parts3, 1);
							String extra = null;
							if (parts3.length==5) extra = parts3[4];
							g = new Game(home, away, Integer.parseInt(parts3[2]), Integer.parseInt(parts3[3]), Game.ROBIN, extra);
							
							home.addGame(g);
							away.addGame(g);
							
							
							if (Integer.parseInt(parts3[2]) >= 10 || Integer.parseInt(parts3[3]) >= 10) {
								tens.add(g);
							}
							games.add(g);
						}
					}
				}
				
				if(parts[0].equals("PLAYOFFPAIR")) {
					for (int i = 0; i < 7; i++) {
						String line4 = bufread.readLine();
						String[] parts4 = line4.split(":");
						Game g2 = null;
						if (parts4.length == 4 || parts4.length == 5) {
							Player home = getPlayer(parts4, 0);
							Player away = getPlayer(parts4, 1);
							String extra = null;
							if (parts4.length==5) extra = parts4[4];
							
							g2 = new Game(home, away, Integer.parseInt(parts4[2]), Integer.parseInt(parts4[3]), Game.PLAYOFF, extra);
						
						
							if (Integer.parseInt(parts4[2]) >= 10 || Integer.parseInt(parts4[3]) >= 10) {
								tens.add(g2);
							}
							games.add(g2);
						}
						
					}
				}
			}
			
		}
	
		catch (Exception e) {
			return;
		}
	}
	
	/** 
	* Closes open file
	* @return true if and only if file was closed properly
	*/
	public boolean close() {
		this.bufread = null;
		return true;
	}
	
	public boolean isOpen() {
		return this.bufread.equals(null);
	}
	
	/**
	* Returns wanted player
	* @param p array of contents of line
	* @param o index in which p is read
	* @return wanted player
	* Initial condition: !isfound(p[o])
	*/
	
	private Player getPlayer(String[] p, int o) {
		
		for (int i = 0; i < players.size(); i++) {
			if (p[o].equals(players.get(i).getName())) {
				return players.get(i);
			}
		}
		return null;	
	}
	
	private boolean isFound(String n) {
		if (players.isEmpty()) { return false; }
		boolean found = false;
		for (int i = 0; i < players.size(); i++) {
			if (n.equals(players.get(i).getName()) ) {
				found = true; break;
			}
		}
		return found;
	}
	
}