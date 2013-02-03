package com.admin.data;

public class Game {
	
	private Player home, away;
	private int hgoals, agoals, type;
	private String extra;
	
	public static final int ROBIN = 1;
	public static final int PLAYOFF = 2;
	
	
	
	/**
	* Constructor for game.
	* @param h home player
	* @param a away player
	* @param hg home goals
	* @param ag away goals
	* @param type round robin or playoff (ROBIN/PLAYOFF)
	* @param t tournament in which game is played
	* @param extra null (normal game) / j (overtime) / lv (walkover) / pg (previous group)
	*/
	public Game(Player h, Player a, int hg, int ag, int type, String extra) {
		this.home = h;
		this.away = a;
		this.hgoals = hg;
		this.agoals = ag;
		this.type = type;
		this.extra = extra;
	}
	
	public String getHome() {
		return home.getName();
	}
	
	public String getAway() {
		return away.getName();
	}
	
	public int getHgoals() {
		return hgoals;
	}
	
	public int getAgoals() {
		return agoals;
	}
	
	public int getType() {
		return type;
	}
	
	public String getExtra() {
		return extra;
	}
	/**
	* Returns a string represantation of game
	* @return a string represantation of game
	*/
	public String toString() {
		String s = home.getName() + "- " + away.getName() + " " + hgoals + " - " + agoals;
		if (extra != null && !extra.equals("")) {
			s += extra;
		}
		return s;
	}
	
	
	
}