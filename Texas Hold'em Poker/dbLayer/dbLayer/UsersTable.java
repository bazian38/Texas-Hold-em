package dbLayer;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.criterion.Restrictions;
import org.hibernate.query.Query;

import user.User;

public class UsersTable {

	private SessionFactory factory;
	private Session session;
	private static UsersTable instancee = new UsersTable();
	
	private UsersTable(){}
	public static UsersTable getInstance(){
		return instancee;
	}
	private void init(){
		
		// create session factory
		factory = new Configuration()
			.configure("hibernate.cfg.xml")
			.addAnnotatedClass(User.class)
			.buildSessionFactory();
		
		// create session
		session = factory.getCurrentSession();
		session.beginTransaction();
	}
	public static void main(String[] args) {
		UsersTable test = UsersTable.getInstance();
		TestConnection();
		test.StoreUser(new User("4534","4534","passwor43d", "na543me", "em453ail", 999, 999, 999,"avatar"));
		test.StoreUser(new User("45344","45344","passwor43deeww", "na543me", "em453ail", 999, 999, 999,"avatar"));
		test.StoreUser(new User("1556","aaaa","passwor43daaaa", "na543me", "em453ail", 999, 999, 999,"avatar"));
		test.RemoveUser("2");
		System.out.println(test.GetUser("13"));
		System.out.println(test.GetAllUsers().size());
		test.UpdateUserEmail("1" , "aa@aa");
		test.UpdateUserName("4" , "akjf");
		test.UpdateUserTotalCash("6" , "80");
		test.UpdateUserScore("7" , "800");
		test.UpdateUserLeague("8" , "3");
		test.UpdateUserAvatar("9" , "www");
		System.out.println(test.CompairePasswords("4534", "Password"));
		System.out.println(test.CompairePasswords("4534", "password"));
		System.out.println(test.GetUserByID("aaaa").getPassword());
	}

	/*
	 * Store a user to the users table and return its object
	 */
	public void StoreUser( User UserToStore){
		
		init();
		String OldUserId= UserToStore.getID();
		String NewUserId;
		try {			
			
			System.out.println("Saving the User...");
			 NewUserId = (String) session.save(UserToStore);
			// commit transaction
			session.getTransaction().commit();
			

			
			System.out.println("New User stored with id: " + OldUserId);
			}
			finally {
				
				factory.close();
			}
		UpdateUserID(NewUserId, OldUserId);
		}
	
	/*
	 * delete a user from the users table and return its object
	 */
	public User RemoveUser(String UserId) {
			
		User myUser;
		init();
		try {								
			
			
			// retrieve student based on the id: primary key
			System.out.println("\nGetting User with id: " + UserId);
			
			 myUser = session.get(User.class, UserId);
			
			 if (myUser != null){
				 
				// delete the student
				 System.out.println("Deleting student: " + myUser); 
				 
				 session.delete(myUser);
				 
				// commit the transaction
				session.getTransaction().commit();
				System.out.println("Done!");
			 }else{
				 
				 System.out.println("User not exist!");
			 }
			
		}
		finally {
			factory.close();
		}
		return myUser;
	}
	
	/*
	 * get a user from the users table and return its object
	 */
	public User GetUser(String UserId) {
			
		User myUser;
		init();
		try {								
			
			// retrieve user based on the id: primary key
			System.out.println("\nGetting User with id: " + UserId);
			
			 myUser = session.get(User.class, UserId);
			 
			 if (myUser != null){
				 				 
				// commit the transaction
				session.getTransaction().commit();
				System.out.println("Done!");
			 }else{
				 
				 System.out.println("User not exist!");
			 }
		}
		finally {
			factory.close();
		}
		return myUser;
	}
	
	/*
	 * get a user from the users table and return its object
	 */
	public User GetUserByID(String UserId) {
			
		User myUser;
		List results;
		init();
		try {								
			
			// retrieve user based on the id: primary key
			System.out.println("\nGetting User with id: GetUserByID " + UserId);
			String hql = "FROM User E WHERE E.ID = '"+UserId +"'";
			Query query = session.createQuery(hql);
			 results = query.list();
			
			/*
			Criteria cr = session.createCriteria(User.class);
			cr.add(Restrictions.eq("username", UserId));*/
			//results = cr.list();
			 if (results != null){
				 				 
				// commit the transaction
				session.getTransaction().commit();
				System.out.println("Done!");
			 }else{
				 
				 System.out.println("User not exist!");
			 }
		}
		finally {
			factory.close();
		}
		if(results == null || results.size() == 0)return null;
		return (User) results.iterator().next();
	}

	
	/*
	 * check user password
	 */
	public boolean CompairePasswords(String UserId,String Password) {
		
		User myUser;
		init();
		try {								
			
			// retrieve user based on the id: primary key
			System.out.println("\nGetting User with id: " + UserId);
			
			 myUser = session.get(User.class, UserId);
			
			 if (myUser != null){
				 				 
				// commit the transaction
				session.getTransaction().commit();
				System.out.println("Done!");
			 }else{
				 
				 System.out.println("User not exist!");
				 return false;
			 }
		}
		finally {
			factory.close();
		}
		return myUser.getPassword().equals(Password);
	}
	
	
	/*
	 * Get all users as a list
	 */
	public java.util.List GetAllUsers(){
		
		
		init();
		java.util.List AllUsers;
		try {								
			
			System.out.println("\nGetting All Users");
			
			AllUsers = session.createCriteria(User.class).list();

		}
		finally {
			factory.close();
		}
		return AllUsers;
	
	}
	
	public void UpdateUserEmail(String UserId , String FieldValue){
		UpdateUser(UserId ,"email", FieldValue);
	}
	
	public void UpdateUserID(String UserId , String FieldValue){
		UpdateUser(UserId ,"id", FieldValue);
	}
	
	public void UpdateUserName(String UserId , String FieldValue){
		UpdateUser(UserId ,"name", FieldValue);
	}
	
	public void UpdateUserTotalCash(String UserId , String FieldValue){
		UpdateUser(UserId ,"totalCash", FieldValue);
	}
	
	public void UpdateUserScore(String UserId , String FieldValue){
		UpdateUser(UserId ,"score", FieldValue);
	}
	
	public void UpdateUserLeague(String UserId , String FieldValue){
		UpdateUser(UserId ,"league", FieldValue);
	}
	
	public void UpdateUserAvatar(String UserId , String FieldValue){
		UpdateUser(UserId ,"avatar", FieldValue);
	}

	public void UpdateUserPassword(String UserId , String FieldValue){
		UpdateUser(UserId ,"password", FieldValue);
	}
	
	public void UpdateUser(String UserId ,String FieldName, String FieldValue) {

		init();
		
		try {								
			
			System.out.println("Updating User...");
			
			
			
			String query = "UPDATE users SET "+FieldName+" = '"+ FieldValue +"' WHERE id = '"+UserId + "'";
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
			myConn.close();
		}
		catch (Exception exc) {
			
			
			System.out.println("Connection Faild!!!");
			exc.printStackTrace();
		}
		
	}

}





