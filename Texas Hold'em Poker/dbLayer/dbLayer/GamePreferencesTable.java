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
import user.User;

public class GamePreferencesTable {

	private SessionFactory factory;
	private Session session;
	private static GamePreferencesTable instance = new GamePreferencesTable();
	
	public static GamePreferencesTable getInstance(){
		return instance;
	}
	private GamePreferencesTable(){}
	private void init(){
		
		// create session factory
		factory = new Configuration()
			.configure("hibernate.cfg.xml")
			.addAnnotatedClass(GamePreferences.class)
			.buildSessionFactory();
		
		// create session
		session = factory.getCurrentSession();
		session.beginTransaction();
	}
	
	
	public static void main(String[] args) {

		TestConnection();
		GamePreferencesTable instance = GamePreferencesTable.getInstance();
		GamePreferences prefs = null;
		int prefID=0;
		try {
			prefs = new GamePreferences(GameType.LIMIT, 0, 20, 0, 20, 2, 8, true, true, 2);
			prefID=prefs.getPreferences_id();
			System.out.println(prefID);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		instance.StoreGamePreferences(prefs);
		prefs = instance.GetGamePreferences(prefs.getPreferences_id() + "");
		
	}

	/*
	 * Store a GamePreferences to the GamePreferences table and return its object
	 */
	public void StoreGamePreferences( GamePreferences GamePreferencesToStore){
		
		init();
		String OldGamePreferencesId= ""+GamePreferencesToStore.getPreferences_id();
		String NewGamePreferencesId;
		try {			
			
			System.out.println("Saving the GamePreferences...");
			 NewGamePreferencesId = ""+(int) session.save(GamePreferencesToStore);
			// commit transaction
			session.getTransaction().commit();
			

			
			System.out.println("New Prefernce Game stored with id: " + GamePreferencesToStore.getPreferences_id());
			}
			finally {
				
				factory.close();
			}
		}

	
	public void UpdateGamePreferencesId(String UserId , String FieldValue){
		UpdateGamePreferences(UserId ,"preferences_id", FieldValue);
	}
	
	
	public void UpdateGamePreferences(String GamePreferencesId ,String FieldName, String FieldValue) {

		init();
		
		try {								
			
			System.out.println("Updating GamePreferences...");
			
			
			
			String query = "UPDATE gamepreferences SET "+FieldName+" = '"+ FieldValue +"' WHERE preferences_id = '"+GamePreferencesId + "'";
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
		java.util.List AllGamePreferences;
		try {								
			
			System.out.println("\nGetting All Games");
			
			AllGamePreferences = session.createCriteria(GamePreferences.class).list();

		}
		finally {
			factory.close();
		}
		return AllGamePreferences;
	
	}
	
	
	/*
	 * delete a GamePreferences from the GamePreferences table and return its object
	 */
	public GamePreferences RemoveGamePreferences(String GamePreferencesId) {
			
		GamePreferences TargetGamePreferences;
		init();
		try {								
			
			
			// retrieve game based on the id: primary key
			System.out.println("\nGetting GamePreferences with id: " + GamePreferencesId);
			
			 TargetGamePreferences = session.get(GamePreferences.class, GamePreferencesId);
			
			 if (TargetGamePreferences != null){
				 
				// delete the student
				 System.out.println("Deleting GamePreferences: " + TargetGamePreferences); 
				 
				 session.delete(TargetGamePreferences);
				 
				// commit the transaction
				session.getTransaction().commit();
				System.out.println("Done!");
			 }else{
				 
				 System.out.println("GamePreferences not exist!");
			 }
			
		}
		finally {
			factory.close();
		}
		return TargetGamePreferences;
	}
	
	
	
	/*
	 * get a game from the games table and return its object
	 */
	public GamePreferences GetGamePreferences(String GamePreferencesId) {
			
		GamePreferences TargetGamePreferences;
		init();
		try {								
			
			// retrieve Game based on the id: primary key
			System.out.println("\nGetting GamePreferences with id: " + GamePreferencesId);
			
			 TargetGamePreferences = session.get(GamePreferences.class, Integer.parseInt(GamePreferencesId));
			
			 if (TargetGamePreferences != null){
				 				 
				// commit the transaction
				session.getTransaction().commit();
				System.out.println("Done!");
			 }else{
				 
				 System.out.println("GamePreferences not exist!");
			 }
		}
		finally {
			factory.close();
		}
		return TargetGamePreferences;
	}
	
	
	

	

}





