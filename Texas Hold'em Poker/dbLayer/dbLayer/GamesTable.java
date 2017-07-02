package dbLayer;

import java.sql.Connection;
import java.sql.DriverManager;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import Game.Game;
import Game.GamePreferences;
import Game.Enum.GameType;

public class GamesTable {

	private SessionFactory factory;
	private Session session;
	private static GamesTable instance = new GamesTable();
	//Singletone
	private GamesTable(){}
	private void init(){
		
		// create session factory
		factory = new Configuration()
			.configure("hibernate.cfg.xml")
			.addAnnotatedClass(Game.class)
			.buildSessionFactory();
		
		// create session
		session = factory.getCurrentSession();
		session.beginTransaction();
	}
	public static GamesTable getInstance(){
		return instance;
	}
	public static void main(String[] args) {

		GamesTable testing = GamesTable.getInstance();
		GamePreferences prefs = null;
		try {
			prefs = new GamePreferences(GameType.NO_LIMIT, 0, 20, 0, 20, 2, 8, true, true, 2);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		TestConnection();
		testing.StoreGame(new Game(prefs, "123"));
		testing.StoreGame(new Game ("200", "gdhh"));
		testing.StoreGame(new Game ("20", "gdhgnghh"));
		testing.StoreGame(new Game ("2", "gdhgngjkhhh"));
		testing.RemoveGame("2");
		System.out.println(testing.GetAllGames().size());
		testing.UpdateGameDescription("5","yahoo");
	}

	/*
	 * Store a user to the users table and return its object
	 */
	public void StoreGame( Game GameToStore){
		
		init();
		String OldGameId= GameToStore.getGameID();
		String NewGameId;
		try {			
			
			System.out.println("Saving the Game...");
			 NewGameId = (String) session.save(GameToStore);
			// commit transaction
			session.getTransaction().commit();
			

			
			System.out.println("New Game stored with id: " + OldGameId);
			}
			finally {
				
				factory.close();
			}
		if (GameToStore.getpreferences()!=null){
			GamePreferencesTable.getInstance().StoreGamePreferences(GameToStore.getpreferences());
			UpdateGamePreferencesId(NewGameId , ""+GameToStore.getpreferences().getPreferences_id());
		}
		}
	
	public void UpdateGameID(String UserId , String FieldValue){
		UpdateGame(UserId ,"id", FieldValue);
	}
	
	public void UpdateGamePreferencesId(String UserId , String FieldValue){
		UpdateGame(UserId ,"preferences_id", FieldValue);
	}
	
	public void UpdateGame(String GameId ,String FieldName, String FieldValue) {

		init();
		
		try {								
			
			System.out.println("Updating Game...");
			
			
			
			String query = "UPDATE games SET "+FieldName+" = '"+ FieldValue +"' WHERE id = '"+GameId + "'";
			try {

				session.createSQLQuery(query).executeUpdate();
				session.getTransaction().commit();
				session.close();
			}
			catch (HibernateException erro){
				session.getTransaction().rollback();
				session.close();
			} 
			
			System.out.println("Done!");
		}
		finally {
			factory.close();
		}
	}
	
	public static void TestConnection() {

		String jdbcUrl = "jdbc:mysql://localhost:3306/poker_db?useSSL=false";
		String user = "root";
		String pass = "baZima38";
		
		try {
			System.out.println("Connecting to database: " + jdbcUrl);
			
			Connection myConn =
					DriverManager.getConnection(jdbcUrl, user, pass);
			
			System.out.println("Connection successful!!!");
			
		}
		catch (Exception exc) {
			
			
			System.out.println("Connection Faild!!!");
			exc.printStackTrace();
		}
		
	}
	
	/*
	 * Get all Games as a list
	 */
	public java.util.List GetAllGames(){
		
		
		init();
		java.util.List AllGames;
		try {								
			
			System.out.println("\nGetting All Games");
			
			AllGames = session.createCriteria(Game.class).list();
            for(Game game : (java.util.List<Game>)AllGames){
            	game.setPreferences(GamePreferencesTable.getInstance().GetGamePreferences(game.getGameID()));
            }
		}
		finally {
			factory.close();
		}
		return AllGames;
	
	}
	
	
	/*
	 * delete a game from the games table and return its object
	 */
	public Game RemoveGame(String GameId) {
			
		Game TargetGame;
		init();
		try {								
			
			
			// retrieve game based on the id: primary key
			System.out.println("\nGetting Game with id: " + GameId);
			
			 TargetGame = session.get(Game.class, GameId);
			
			 if (TargetGame != null){
				 
				// delete the student
				 System.out.println("Deleting Game: " + TargetGame); 
				 
				 session.delete(TargetGame);
				 
				// commit the transaction
				session.getTransaction().commit();
				System.out.println("Done!");
			 }else{
				 
				 System.out.println("Game not exist!");
			 }
			
		}
		finally {
			factory.close();
		}
		return TargetGame;
	}
	
	
	
	/*
	 * get a game from the games table and return its object
	 */
	public Game GetGame(String GameId) {
			
		Game TargetGame;
		init();
		try {								
			
			// retrieve Game based on the id: primary key
			System.out.println("\nGetting Game with id: " + GameId);
			
			 TargetGame = session.get(Game.class, GameId);
			
			 if (TargetGame != null){
				 				 
				// commit the transaction
				session.getTransaction().commit();
				System.out.println("Done!");
			 }else{
				 
				 System.out.println("Game not exist!");
			 }
		}
		finally {
			factory.close();
		}
		return TargetGame;
	}
		
	public void UpdateGameDescription(String UserId , String FieldValue){
		UpdateGame(UserId ,"description", FieldValue);
	}
	

}





