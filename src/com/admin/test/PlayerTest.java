//package com.admin.test;
//
//import com.admin.data.*;
//import com.vaadin.ui.Label;
//
//public class PlayerTest {
//	
//	public static void main(String[] args) {
//		Player p1 = new Player("Juhis");
//		Player p2 = new Player("Mikko");
//		Game g = new Game(p1, p2, 5, 2, Game.ROBIN, null);
//		p1.addGame(g);
//		p2.addGame(g);
//		System.out.println(p1);
//		System.out.println(p2);
//		
//		Player p1 = new Player("Juhis");
//		Player p2 = new Player("Mikko");
//		
//		Game g = new Game(p1, p2, 1, 1, Game.ROBIN, null);;
//
//		p1.addGame(g);
//		p2.addGame(g);
//	
//		Label game = new Label(g.toString());
//		Label player1 = new Label(p1.toString());
//		Label player2 = new Label(p2.toString());
//		
//		layout.addComponent(header);
//		layout.addComponent(game);
//		layout.addComponent(player1);
//		layout.addComponent(player2);
//	}
//}
