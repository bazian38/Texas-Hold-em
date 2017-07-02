package System;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import Game.Game;
import Game.GameInterface;
import Game.GamePreferences;
import Game.Player;
import Game.Spectator;
import Game.Enum.GameType;
import communicationLayer.ConnectionHandler;
import dbLayer.GamesTable;
import dbLayer.UsersTable;
import user.User;
import user.UserInterface;
import user.UserStatus;

public class GameCenter implements GameCenterInterface{
   private  ConcurrentLinkedQueue<User> users; 
   private ConcurrentLinkedQueue<Game> games;
   private ConcurrentLinkedQueue<Game> inActiveGames;
   private GamesTable DBGames;
   private UsersTable DBUsers;
   private final static Logger LOGGER = Logger.getLogger(GameCenter.class.getName());
   private AtomicInteger gameIdGen;
   private AtomicInteger userIdGen;
   private static GameCenterInterface singleton = new GameCenter( );
   public static GameCenterInterface getInstance( ) {
	      return singleton;
	   }
   
   private GameCenter(){
	   users = new ConcurrentLinkedQueue<User>();
	   games= new ConcurrentLinkedQueue<Game>();
	   gameIdGen = new AtomicInteger(1);
	   userIdGen = new AtomicInteger(1);
	   DBGames = GamesTable.getInstance();
	   DBUsers = UsersTable.getInstance();
	   inActiveGames = new ConcurrentLinkedQueue<Game>();
	   List<User> usersTmp = (List<User>)DBUsers.GetAllUsers();
	   for(User user : usersTmp){
		   user.setStatus(UserStatus.DISCONNECTED);
		   users.add(user);
	   }
	   
	   List<Game> gamesTmp = (List<Game>)DBGames.GetAllGames();
	   for(Game user : gamesTmp){
		   inActiveGames.add(user);
	   }
	   
   }
   
   
   public User getUser(String ID)
   {
	   User user = null;
	   for (User usr : users) 
		     if(usr.getID().equals(ID))
		    	 user=usr;
	   return user;
   }
   
   /**
    * 
    * @param email
    * @return true if email is valid email address
    */
   private boolean isValidEmailAddress(String email)
   {
	   Pattern pattern = Pattern.compile("[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}");
       Matcher mat = pattern.matcher(email);
       return mat.matches();
   }
   
  
  /**
   * this function get all the details of unregistered user, check them under the game policy, if there is'nt problem with one or more of the details make a new user and add him to the system
   *  @return true if the user can register to the system, else return false;
   */
   public boolean register(String ID, String password, String name, String email,String avatar) 
   {
	   User newUser;

	   for (User usr : users) 
		     if(usr.getID().equals(ID))
		     {
		    	 LOGGER.info("Error: this ID already exist in the system");
		         return false;
		     }
	   if(DBUsers.GetUserByID(ID)!=null) {
	    	 LOGGER.info("Error: this ID already exist in the system");
	         return false;
	   }
	   if(!isValidEmailAddress(email))
		   {
		    LOGGER.info("Error: invalid email address");
		    return false;
		   }
	   if(password.length()<8)
		   {
		     LOGGER.info("Error: the password is too short");
		     return false;
		   }
	   newUser=new User(userIdGen.getAndIncrement()+"",ID, password, name, email, 5000, 0,-1,avatar);
	   DBUsers.StoreUser(newUser);
	   return true;
   }
   
   public void addUser(User user){
	   
	   users.add(user);
	   
   }
   

   

	

   
   /**
    * in this function please see the Create game requirement in the Assignment 1 and add the relevant params according to the game preferences, 
    * @param user
    * @return GAME ID if the user can init game with the giver preferences, "" else 
    */
   public String createGame(String UserID,GamePreferences preference){
	   
	   GamePreferences preferences;
	   try {
		   preferences = preference;
	   }
	   catch(Exception e) {
		   LOGGER.info("Error: game pregerences don't match requirements");
		   return "";
	   }
	   
	   Game newGame = new Game(preferences, gameIdGen.getAndIncrement()+"");
	   games.add(newGame);
	   
	   if(joinGame(newGame.getGameID(),UserID)) {
		   DBGames.StoreGame(newGame);
		   Thread th = new Thread(newGame);
		   th.start();
		   return newGame.getGameID();
	   }
	   else{
		   games.remove(newGame);
	   }
	   return "";
   }
   
   /**
    * see Search/filter active games in assignment 1 and the relevant use case
    * @param playerName
    * @param potSize
    * @return
    */
   public LinkedList<Game> searchGamesByPotSize(int potSize){
	   
	  LinkedList<Game> can_join = new LinkedList<Game>();
	  
		  for(Game i_game : games){
				 if(i_game.getpreferences().getGameTypePolicy()==GameType.POT_LIMIT && i_game.getpreferences().getLimit()==potSize){
					  can_join.add(i_game);
				 
			  }}

		  for(Game i_game : inActiveGames){
				 if(i_game.getpreferences().getGameTypePolicy()==GameType.POT_LIMIT && i_game.getpreferences().getLimit()==potSize){
					  can_join.add(i_game);
				 
			  }}
		  
	  return  can_join;
   }
   
   public LinkedList<Game> searchGamesByPrefs(GamePreferences userPrefs){
	   
	  LinkedList<Game> can_join = new LinkedList<Game>();

		  for(Game i_game : games){
				 if(userPrefs.checkEquality(i_game.getpreferences())){
					  can_join.add(i_game);
				 
			  }
				 }
		  
		  for(Game i_game : inActiveGames){
				 if(userPrefs.checkEquality(i_game.getpreferences())){
					  can_join.add(i_game);
				 
			  }
				 }


		  
	  return  can_join;
   }
   
   
   public LinkedList<Game> searchGamesByPlayerName(String name){
	   
	  LinkedList<Game> can_join = new LinkedList<Game>();

		  for(Game i_game : games){			 
			  Player[] p = i_game.getPlayers();
			  for(Player pp : p){
				 if(pp.getUser().getName().equals(name)){
					  can_join.add(i_game);
					  break;
				 }
			  }}
		  
		  for(Game i_game : inActiveGames){			 
			  Player[] p = i_game.getPlayers();
			  for(Player pp : p){
				 if(pp.getUser().getName().equals(name)){
					  can_join.add(i_game);
					  break;
				 }
			  }}

		  
	  return  can_join;
   }
   
   public LinkedList<Game> listJoinableGames(String UserID){
	   UserInterface user = getUser(UserID);
	   LinkedList<Game> can_join = new LinkedList<Game>();
	   for(Game game : games){
		   if(game.isJoinAbleGame(user))
			   can_join.add(game);
		   
		   
	   }
	   
	   return can_join;
	   
   }
   
   public LinkedList<Game> listSpectatableGames(){
	   LinkedList<Game> can_join = new LinkedList<Game>();
	   for(Game game : games){
		   if(game.getpreferences().isSpectatable())
			   can_join.add(game);
		   
		   
	   }
	   
	   return can_join;
	   
   }
   
   public boolean spectateGame(String UserID,String GameID){
	   GameInterface game = getActiveGameByID(GameID);
	   UserInterface user = getUser(UserID);
	   if(user !=null && game!=null)
	   return game.spectate(user);
       return false;
   }
   
   public boolean joinGame(String gameID, String UserID){
	   GameInterface game = getActiveGameByID(gameID);
	   UserInterface user = getUser(UserID);
	   if(game!=null && user !=null){
		   return game.joinGame(user);
		   
	   }
	   
	   return false;
   }
   
   
   public GameInterface getGameByID(String gameID){
	   for(Game game : games){
		   if(game.getGameID().equals(gameID))
		     return game;
		   
	   }
	   
	   for(Game game : inActiveGames){
		   if(game.getGameID().equals(gameID))
		     return game;
		   
	   }
	   return null;
	   
	   
   }

   public GameInterface getActiveGameByID(String gameID){
	   for(Game game : games){
		   if(game.getGameID().equals(gameID))
		     return game;
		   
	   }

	   return null;
	   
	   
   }
	public boolean editUserPassword(String userID, String oldPassword, String newPassword) {
		if(newPassword.isEmpty()) {
			  LOGGER.warning("Error: empty password is invalid");
			  return false;
		}
		if(newPassword.length() < 8) {
			  LOGGER.info("Error: the password is too short");
			  return false;
		}
		for (User usr : users) {
		     if(usr.getID().equals(userID) && usr.getPassword().equals(oldPassword)) {
		    	 
		    	usr.editPassword(newPassword);
		    	return true;
		    	
		     }
		}
		
		return false;
	}
	
	public boolean editUserName(String userID, String newName) {
		if(newName.isEmpty()) {
			  LOGGER.info("Error: empty name is invalid");
			  return false;
		}
		
		for (User usr : users) {
		     if(usr.getID().equals(userID)) {
		    	usr.editName(newName);
		    	return true;
		     }
		}
		return false;
	}

	public boolean editUserEmail(String userID, String newEmail) {
		if(newEmail.isEmpty()) {
			  LOGGER.info("Error: empty email is invalid");
			  return false;
		}
		if(!isValidEmailAddress(newEmail)) {    
			LOGGER.info("Error: invalid email address");
			 return false;
		}
		for (User usr : users) {
		     if(usr.getID().equals(userID)) {
		    	usr.editEmail(newEmail);
		    	return true;
		     }
		}
		return false;
	}
	
	public boolean editUserAvatar(String userID, String newAvatar) {
		if(newAvatar.isEmpty()) {
			  LOGGER.info("Error: empty AVATAR is invalid");
			  return false;
		}
		for (User usr : users) {
		     if(usr.getID().equals(userID)) {
		    	usr.setAvatar(newAvatar);
		    	return true;
		     }
		}
		return false;
	}

	public boolean login(String ID, String password,ConnectionHandler handler) {
		for (User usr : users) {
		     if(usr.getID().equals(ID)) {
		    	 if(usr.getPassword().equals(password)) {
		    		 usr.setStatus(UserStatus.CONNECTED);
		    		 usr.giveHandler(handler);
		    		 return true;
		    	 }
		    	 else {
		    		 LOGGER.info("Error: incorrect password");
		    		 return false;
		    	 }
		     }
		}
		
		User user = DBUsers.GetUserByID(ID);
   	 	if(user != null && user.getPassword().equals(password)) {
   	 		user.init();
   	 		users.add(user);
   	 		user.giveHandler(handler);
   	 		return true;
   	 	}
		LOGGER.info("Error: unrecognize id");
		return false;
	}
	
	public void logout(String ID) {
		User user = getUser(ID);
		if(user!=null){
			for(Game g: games){
				
				leaveGame(g.getGameID(),ID);
				
			}
			 users.removeIf(filter -> filter.getID().equals(ID));
		     user.setStatus(UserStatus.DISCONNECTED);
	 		
		
		}
	}
	
	
	
	public boolean leaveGame(String GameID,String UserID){

		   Game game = (Game)getActiveGameByID(GameID);
		   if(game==null) return false;
		   User user = getUser(UserID);
		   user.actionMaked(GameID);
		   boolean result = game.leaveGame(user);
		   System.out.println("Players Number in The Game: " +game.getPlayerNumber());
		   if(game.getPlayerNumber()==0)
			   {
			    int numberGames = games.size();
			   	this.games.removeIf(filter -> filter.getGameID().equals(GameID));
			   	if (numberGames > games.size()) System.out.println("Game Deleted and it's InActive");
			   	DBGames.UpdateGameDescription(GameID, game.getGameReplay());
			   	this.inActiveGames.add(game);
			   }
           return result;

	}

	public boolean check(String userID, String gameID) {
		GameInterface game = getActiveGameByID(gameID);
		User user = getUser(userID);
		if(game != null && user != null) {
			return game.check(user);
		}
		return false;
	}

	public boolean bet(String userID, String gameID, int money) {
		GameInterface game = getActiveGameByID(gameID);
		User user = getUser(userID);
		if(game != null && user != null) {
			return game.bet(user, money);
		}
		return false;
	}

	public boolean fold(String userID, String gameID) {
		GameInterface game = getActiveGameByID(gameID);
		User user = getUser(userID);
		if(game != null && user != null) {
			return game.fold(user);
		}
		return false;
	}

	public boolean raise(String userID, String gameID, int money) {
		return bet(userID, gameID, money);
	}

	public boolean call(String userID, String gameID, int money) {
		return bet(userID, gameID, money);
	}
	
	/**
	 * FOR TESTING PURPOSE ONLY
	 */
	
	public void removeAll() {
		for (Game game : games) {
			game.killGame();
		}
		this.users = new ConcurrentLinkedQueue<User>();
		this.games = new ConcurrentLinkedQueue<Game>();
		
	}
	
	public void ChatMsg(String GameID, String UserID, String MsgParts){
		
		Game currentGame = (Game) getActiveGameByID(GameID);
		String userType=null;
		if(currentGame!=null)
		for(Player p:currentGame.getPlayers()){
			
			if(p.getUser().getID().equals(UserID)){
				userType="player";
				currentGame.SendMSG(MsgParts, userType);
				break;
			}
			
		}
		if(userType==null && currentGame!=null)
        for(Spectator s:currentGame.getSpectators()){
			
			if(s.getUser().getID().equals(UserID)){
				userType="spectator";
				currentGame.SendMSG(MsgParts, userType);
				break;
			}
			
		}
	}
	
	public void WhisperMsg(String GameID, String UserID, String receiverID, String MsgParts){
		
		Game currentGame = (Game) getActiveGameByID(GameID);
		boolean isPlayer=false;
		if(currentGame!=null)
		for(Player p:currentGame.getPlayers()){
			
			if(p.getUser().getID().equals(UserID)){
				currentGame.WhisperMSG(MsgParts, receiverID);
				isPlayer=true;
				break;
			}
			
		}
		if(!isPlayer && currentGame!=null)
	        for(Spectator s:currentGame.getSpectators()){
				
				if(s.getUser().getID().equals(UserID)){
					currentGame.WhisperMSG(MsgParts, receiverID);
					break;
				}
				
			}
	}
	
	public String getGameReplay(String gameID)
	{
		for(Game g: this.games)
			if(g.getGameID().equals(gameID))
				return g.getGameReplay();
		
		for(Game g: this.inActiveGames)
			if(g.getGameID().equals(gameID))
				return g.getGameReplay();
		return null;
	}

}
