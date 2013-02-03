package com.admin.data;

import java.util.ArrayList;
import java.util.Random;

import com.vaadin.data.util.BeanItemContainer;

@SuppressWarnings("serial")
public class PlayerContainer extends BeanItemContainer<Player> {
	
	private static ArrayList<Player> players;
	
	/**
	 * Natural property order for Person bean. Used in tables and forms.
	 */
	public static final Object[] NATURAL_COL_ORDER = new Object[] {
			"name", "games", "wins", "ties", "losses", "points", "gf", "ga", "gd" };

	/**
	 * "Human readable" captions for properties in same order as in
	 * NATURAL_COL_ORDER.
	 */
	public static final String[] COL_HEADERS_ENGLISH = new String[] {
			"Name", "G", "W", "T", "L", "P", "GF", "GA", "+/-"};
	
	public PlayerContainer() throws IllegalArgumentException {
		super(Player.class);
		
	}

	@Override
	public boolean addContainerProperty(Object propertyId, Class<?> type,
			Object defaultValue) throws UnsupportedOperationException {
	
		return false;
	}
	
	public static PlayerContainer createWithTestData(int seed) {
		final String[] names = {"Ahti Lampi", "Santtu Sainio", "Teemu Koskela", "Janne Ollila", "Ville Hietala", "Mika Myllykangas", "Sami Hellström", "Otto Pesälä", "Petrus Miettinen", "Kristian Iso-Tryykäri", "Antti Suojanen", "Jan Pelkonen", "Magnus Ahlberg"};
		PlayerContainer c = null;
		players = new ArrayList<Player>();
		
		Random r = new Random(seed);
		c = new PlayerContainer();
		for (String name : names) {
			players.add(new Player(name));
		}
		for (int i = 0; i < 100; i++) {
			
			Player p1 = players.get(r.nextInt(players.size()));
			Player p2 = players.get(r.nextInt(players.size()));
			if (p1.equals(p2)) {
				continue;
			}
			for (int j = 0; j < 2; j++) {
				Game g = new Game(p1, p2, r.nextInt(12), r.nextInt(12), Game.ROBIN, null);
				p1.addGame(g);
				p2.addGame(g);
			}
			
		}
		for (Player p : players) {
			c.addItem(p);
		}
		Object[] propertyId = {"points", "gd", "gf", "wins"};
		boolean[] ascending = {false, false, false, false};
		c.sort(propertyId,ascending);
		return c;
	}

	public static ArrayList<Player> getPlayers() {
		return players;
	}

	

}
