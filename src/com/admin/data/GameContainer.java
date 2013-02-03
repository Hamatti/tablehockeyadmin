package com.admin.data;

import java.util.ArrayList;
import com.vaadin.data.util.BeanItemContainer;

@SuppressWarnings("serial")
public class GameContainer extends BeanItemContainer<Game> {

	/**
	 * Natural property order for Person bean. Used in tables and forms.
	 */
	public static final Object[] NATURAL_COL_ORDER = new Object[] {
			"home", "away", "hgoals", "agoals", "extra" };

	/**
	 * "Human readable" captions for properties in same order as in
	 * NATURAL_COL_ORDER.
	 */
	public static final String[] COL_HEADERS_ENGLISH = new String[] {
			"Home", "Away", "", "", ""};
	
	
	public GameContainer() throws IllegalArgumentException {
		super(Game.class);
		// TODO Auto-generated constructor stub
	}
	@Override
	public boolean addContainerProperty(Object propertyId, Class<?> type,
			Object defaultValue) throws UnsupportedOperationException {
		// TODO Auto-generated method stub
		return false;
	}
	
	public static GameContainer createWithTestData() {
		ArrayList<Player> players = PlayerContainer.getPlayers();
		GameContainer c = new GameContainer();
		for (Player p : players) {
			for(Game g : p.getGamelist()) {
				c.addItem(g);
			}
		}
			
		return c;
	}

}
