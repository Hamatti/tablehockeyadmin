/** 
* Player models a table hockey player in statistics
* @author Juha-Matti Santala / jumasan@utu.fi
* @version 0.1 / 10/06/2011
*/

package com.admin.data;

import java.util.ArrayList;

public class Player implements Comparable<Player> {
	
	private String name;
	private int games, wins, ties, losses, gf, ga, points, gd;
	private ArrayList<Game> gamelist;
	
	/**
	* Constructor with player's name
	* @param n name of the player
	*/
	public Player(String n) {
		this.gamelist = new ArrayList<Game>();
		this.name = n;
	}
		
	/* GETTERS */
		
	/**
	* Returns name of the player
	* @return name of the player
	*/
	public String getName() { return name; }
		
	/** Returns total amount of played games
	* @return amount of games
	*/
	public int getGames() { return games;}
		
	/** Returns total amount of wins
	* @return amount of wins
	*/
	public int getWins() { return wins;}
		
	/** Returns total amount of ties
	* @return amount of ties
	*/
	public int getTies() { return ties;}
	
	/** Returns total amount of losses
	* @return amount of losses
	*/
	public int getLosses() { return losses; }
		
	/** Returns total amount of scored goals
	* @return amount of scored goals
	*/
	public int getGf() { return gf;}
		
	/** Returns total amount of allowed goals
	* @return amount of allowed goals
	*/
	public int getGa() { return ga;}
	
	public int getPoints() { return points;}
	
	/**
	 * @return the gd
	 */
	public int getGd() {
		return gd;
	}

	/**
	 * @param gd the gd to set
	 */
	public void setGd(int gd) {
		this.gd = gd;
	}

	
	/** Changes the name of the player
	* @param n new name for player
	*/
	public void changeName(String n) {
		this.name =n;
	}
			
	/**
	* Adds a new game for player and counts stats
	* @param g game to be added
	*/
	public void addGame(Game g) {
		this.games++;
		if (g.getHome().equals(this.getName())) {
			if (g.getHgoals() > g.getAgoals()) {
				this.wins++;
			}
			else if (g.getHgoals() < g.getAgoals()) {
				this.losses++;
			}
			else {
				this.ties++;
			}
			this.gf += g.getHgoals();
			this.ga += g.getAgoals();
		}
		else {
			if (g.getHgoals() < g.getAgoals()) {
				this.wins++;
			}
			else if (g.getHgoals() > g.getAgoals()) {
				this.losses++;
			}
			else {
				this.ties++;
			}
			this.gf += g.getAgoals();
			this.ga += g.getHgoals();	
		}
		this.countPoints();
		this.gd = this.gf - this.ga;
		gamelist.add(g);
	}
	/**
	 * @return the gamelist
	 */
	public ArrayList<Game> getGamelist() {
		return gamelist;
	}

	/**
	* Compares this object with the specified object for order. Returns a negative integer, zero, or a positive integer as this object is less than, equal to, or greater than the specified object. 
	* @param p2 player to be compared to
	* @return a negative integer, zero, or a positive integer as this object is less than, equal to, or greater than the specified object.
	*/
	public int compareTo(Player p2) {
		if (this.points > p2.points) return 1;
		else if (this.points < p2.points) return -1;
		else {
			if (this.gf - this.ga > p2.gf - p2.ga) return 1;
			else if (this.gf - this.ga < p2.gf - p2.ga) return -1;
			else {
				if (this.gf > p2.gf) return 1;
				else if (this.gf < p2.gf) return -1;
				else {
					if (this.wins > p2.wins) return 1;
					else if (this.wins < p2.wins) return -1;
					else return 0;
				}
			}
		}
		
	}
	
	/** 
	* Counts the points with following rule:
	* 2 points for a win, 1 for a tie, 0 for a loss.
	*/
	private void countPoints() {
		this.points = this.wins*2 + this.ties;
	}
	
	@Override
	public String toString() {
		return "Player [name=" + name + ", games=" + games + ", wins=" + wins
				+ ", ties=" + ties + ", losses=" + losses + ", gf=" + gf
				+ ", ga=" + ga + ", points=" + points + "]";
	}
	

	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof Player)) {
			return false;
		}
		Player other = (Player) obj;
		if (ga != other.ga) {
			return false;
		}
		if (games != other.games) {
			return false;
		}
		if (gf != other.gf) {
			return false;
		}
		if (losses != other.losses) {
			return false;
		}
		if (name == null) {
			if (other.name != null) {
				return false;
			}
		} else if (!name.equals(other.name)) {
			return false;
		}
		if (points != other.points) {
			return false;
		}
		if (ties != other.ties) {
			return false;
		}
		if (wins != other.wins) {
			return false;
		}
		return true;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ga;
		result = prime * result + games;
		result = prime * result + gf;
		result = prime * result + losses;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + points;
		result = prime * result + ties;
		result = prime * result + wins;
		return result;
	}
		

}