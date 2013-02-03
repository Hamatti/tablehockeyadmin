package com.admin.db;


import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import com.admin.data.Game;
import com.admin.data.Player;
import com.admin.data.TNMTParser;
import com.vaadin.data.util.sqlcontainer.SQLContainer;
import com.vaadin.data.util.sqlcontainer.connection.JDBCConnectionPool;
import com.vaadin.data.util.sqlcontainer.connection.SimpleJDBCConnectionPool;
import com.vaadin.data.util.sqlcontainer.query.FreeformQuery;
import com.vaadin.ui.Notification;

public class DBConnection {
	
	private static JDBCConnectionPool pool;
	
	@SuppressWarnings("deprecation")
	public static void connect() {
		try {
			String driver = "com.mysql.jdbc.Driver";
			String server = "";
			String database = "";
			String user = "";
			String pass = "";
			
			pool = new SimpleJDBCConnectionPool(
				        driver,
				        "jdbc:mysql://" + server + "/" + database, user, pass, 2, 5);
			
		} catch(SQLException e) {
			Notification.show("Could not connect to SQL server", Notification.TYPE_ERROR_MESSAGE);
		}
	}
	
	public static SQLContainer getStandings(int wanted_league) {
		connect();
		FreeformQuery query = new FreeformQuery(
		        " SELECT name, sum(games) as G, sum(wins) as W, sum(ties) as T, sum(losses) as L, sum(points) as P, sum(gf) as GF, sum(ga) as GA, sum(gd) as GD " +
		        " FROM players, playerstats " +
		        " WHERE id = p_id AND (SELECT DISTINCT l_id FROM playerstats, tournaments WHERE t_id = id AND l_id =" + wanted_league + ")  " +
		        " GROUP BY name " +
		        " ORDER BY P desc, GD desc, GF desc, W desc", pool, "name");
		try {
			return new SQLContainer(query);
			
		} catch (SQLException e) {
			Notification.show("RETRIEVING PLAYERSTATS (createTQ()) FAILED");
			return null;
		}
		
	}
	
	@SuppressWarnings("deprecation")
	/*
	 * Writes data from parser to database
	 */
	public static void writeToDatabase(TNMTParser parser, String league_name) {
		/* If not connected, connect */
		if (pool == null) {
			connect();
		}
		String filename = parser.getFilename();
		/* Remove leading path (D:/vaadinuploads/)  */ 
		String[] filenameparts = filename.split("/");
		filename = filenameparts[filenameparts.length-1];
		
		/* Tournament name is everything before filetype so filenames must follow guidelines */
		String tournament = filename.split("\\.")[0];
		
		/* Date is parsed from filename (from 20123001 -> 2012-30-01) */
		String dateS = filename.split("_")[1];
		String year = dateS.substring(0,4);
		String month = dateS.substring(4,6);
		String day = dateS.substring(6,8);
		String final_date = year +"-"+month+"-"+day;
		
		/* Database variables */
		Connection conn = null;
		int league_id = -1;
		int tournament_id = -1;
		
		/* LET'S CHECK IF FILE ALREADY UPLOADED */
		
		try {
			conn = pool.reserveConnection();
			Statement initialCheck = conn.createStatement();
			ResultSet initRS = initialCheck.executeQuery("SELECT id FROM tournaments WHERE filename = '" + filename + "'");
			if (initRS.next()) {
				Notification.show("Turnaus on jo kannassa.", Notification.TYPE_HUMANIZED_MESSAGE);
				initialCheck.close();
				return;
			}
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		finally {
			pool.releaseConnection(conn);
		}
		
		
		/* FETCH LEAGUE ID */
		
		try {
			conn = pool.reserveConnection();
			Statement statement = conn.createStatement();
			ResultSet league_rs = statement.executeQuery("SELECT id FROM leagues WHERE name = '" + league_name + "'");
           
			if (!league_rs.next()) {
				statement.executeUpdate("INSERT INTO leagues (name) VALUES ('"+league_name+"')");
				conn.commit();
			}
			
			league_rs = statement.executeQuery("SELECT id FROM leagues WHERE name = '" + league_name + "'");
			if(league_rs.next()) {
				league_id = league_rs.getInt("id");
			}
			statement.close();
			conn.commit();
			
		} catch (SQLException e) {
			e.printStackTrace();
			Notification.show("SQL Failed on phase: LEAGUE", Notification.TYPE_ERROR_MESSAGE);
			return;
		} finally {
            pool.releaseConnection(conn);
		}
		
		/* FETCH TOURNAMENT ID */
		try {
			if (league_id < 0) {
				Notification.show("LeagueID was not fetched properly, execution halts",  Notification.TYPE_ERROR_MESSAGE);
				return;
			}
			conn = pool.reserveConnection();
			Statement tour_stat = conn.createStatement();
			ResultSet tournament_rs = tour_stat.executeQuery("SELECT id FROM tournaments WHERE filename ='" + filename + "'");
			if (tournament_rs.getFetchSize() == 0) {
				tour_stat.executeUpdate("INSERT INTO tournaments (name, filename, day, l_id) VALUES ('" + tournament +"','" + filename +"','" + final_date + "'," + league_id +")");
				conn.commit();
			}
			tournament_rs = tour_stat.executeQuery("SELECT id FROM tournaments WHERE filename ='" + filename+"'");
			if(tournament_rs.next()) {
				tournament_id = tournament_rs.getInt("id");
			}
			tour_stat.close();
			conn.commit();	
		} catch (SQLException e) {
			Notification.show("SQL Failed on phase: TOURNAMENT", Notification.TYPE_ERROR_MESSAGE);
			return;
		} finally {
            pool.releaseConnection(conn);
		}
		
		/* DO DB OPERATIONS ON EACH PLAYER */
		if(tournament_id < 0) {
			Notification.show("TournamentID was not fetched properly, execution halts",  Notification.TYPE_ERROR_MESSAGE);
			return;
		}
		for (Player p : parser.getPlayers()) {
			int player_id = -1;
			try {
				conn = pool.reserveConnection();
				Statement player_stat = conn.createStatement();
				ResultSet player_rs = player_stat.executeQuery("SELECT id FROM players WHERE name ='" + p.getName()+"'");
				if (!player_rs.next()) {
					player_stat.executeUpdate("INSERT INTO players (name) VALUES ('" + p.getName() + "')");
					conn.commit();
				}
				player_rs = player_stat.executeQuery("SELECT id FROM players WHERE name ='" + p.getName()+"'");
				if(player_rs.next()) {
					player_id = player_rs.getInt("id");
				}
				player_stat.close();
				Statement stats_stat = conn.createStatement();
				System.out.println(p.getName() + ": " + player_id);
				
				stats_stat.executeUpdate("INSERT INTO playerstats (p_id, t_id, games, wins, ties, losses, points, gf, ga, gd)" +
										 "VALUES (" + player_id + "," + tournament_id + "," + p.getGames() + "," + p.getWins() + "," + p.getTies() + "," + p.getLosses() + "," + p.getPoints() + "," + p.getGf() + "," + p.getGa() + "," + p.getGd()+")");
				stats_stat.close();
				conn.commit();
				
			} catch (SQLException e) {
				e.printStackTrace();
				Notification.show("SQL Failed on phase: PLAYER OR PLAYERSTATS", Notification.TYPE_ERROR_MESSAGE);
				return;
			} finally {
				pool.releaseConnection(conn);
			}
		}
			/* ADD GAMES TO DB */
			for(Game g : parser.getGames()) {
				System.out.println(g);
				int home_id = -1;
				int away_id = -1;
				try {
					conn = pool.reserveConnection();
					Statement player_stat = conn.createStatement();
					ResultSet player_rs = player_stat.executeQuery("SELECT id FROM players WHERE name ='" + g.getHome()+"'");
					if (player_rs.next()) {
						home_id = player_rs.getInt("id");
					}
					player_rs = player_stat.executeQuery("SELECT id FROM players WHERE name ='" + g.getAway()+"'");
					if (player_rs.next()) {
						away_id = player_rs.getInt("id");
					}
					if (g.getExtra() != null) {
						player_stat.executeUpdate("INSERT INTO games (home, away, t_id, hg, ag, matchtype, extra) " +
								                              "VALUES ( " + home_id + "," + away_id + "," + tournament_id + "," + g.getHgoals() + "," + g.getAgoals() + "," + g.getType() + ",'" + g.getExtra()+"')");
						conn.commit();
					}
					else {
						player_stat.executeUpdate("INSERT INTO games (home, away, t_id, hg, ag, matchtype) " +
	                              "VALUES ( " + home_id + "," + away_id + "," + tournament_id + "," + g.getHgoals() + "," + g.getAgoals() + "," + g.getType() +")");
						conn.commit();
					}
					
				} catch (SQLException e) {
					e.printStackTrace();
					Notification.show("SQL Failed on phase: GAMES", Notification.TYPE_ERROR_MESSAGE);
					return;
				} finally {
					pool.releaseConnection(conn);
				}
		
			}
	
		}
	


	/**
	 * @return the pool
	 */
	public static JDBCConnectionPool getPool() {
		return pool;
	}

	public static SQLContainer getGames(String name) {
		try {
			if(pool == null) {
				connect();
			}
			FreeformQuery games  = new FreeformQuery("SELECT games.id, p.name as home, p2.name as away, tournaments.name, hg, ag, games.matchtype, games.extra " +
													 "FROM games, players p, players p2, tournaments " +
													 "WHERE tournaments.id = games.t_id " +
													 "AND home = p.id " +
													 "AND away = p2.id " +
													 "AND (home = (SELECT id FROM players WHERE name = '" + name + "') OR away = (SELECT id FROM players WHERE name = '" + name +"'))", pool, "ID");
			return new SQLContainer(games);
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
		
		
		
	}
	
	public static ArrayList<String> getTournaments() {
		Connection conn = null;
		ArrayList<String> filenames = new ArrayList<String>();
		if (pool == null) {
			connect();
		}
		try {
			conn = pool.reserveConnection();
			Statement stat = conn.createStatement();
			ResultSet tournaments = stat.executeQuery("SELECT filename FROM tournaments");
			while(tournaments.next()) {
				filenames.add(tournaments.getString("filename"));
			}
			stat.close();
			conn.commit();
			return filenames;
			
		} catch(SQLException e) {
			e.printStackTrace();
			return null;
		}
		finally {
			pool.releaseConnection(conn);
		}
		
	}

	@SuppressWarnings("deprecation")
	public static void removeTournament(String tournamentfilename) {
		Connection conn = null;
		if (pool == null) {
			connect();
		}
		try {
			conn = pool.reserveConnection();
			Statement stat = conn.createStatement();
			stat.executeUpdate("DELETE FROM tournaments WHERE filename = '" + tournamentfilename + "'");
			stat.close();
			conn.commit();
			
			Notification.show("Poisto onnistui", Notification.TYPE_HUMANIZED_MESSAGE);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally {
			pool.releaseConnection(conn);
		}
		
		
	}
}
