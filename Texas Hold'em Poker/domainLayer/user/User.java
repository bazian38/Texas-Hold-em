package user;

import java.util.HashMap;
import java.util.LinkedList;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.sun.istack.internal.logging.Logger;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import Game.Card;
import Game.Game;
import Game.GameInterface;
import Game.Player;
import communicationLayer.ConnectionHandler;
import dbLayer.UsersTable;
import Game.Spectator;

@Entity
@Table(name="users")
public class User implements UserInterface {
	

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="id")
	private	String id;
	
	@Column(name="username")
	private	String ID;
	
	@Column(name="name")
	private String name;
	
	@Column(name="email")
	private String email;
	
	@Column(name="password")
	private String password;
	
	@Column(name="totalCash")
	private int totalCash;
	
	@Column(name="score")
	private int score;
	
	@Column(name="league")
	private int league;
	
	@Column(name="avatar")
	private String avatar;


	@Transient
	private UserStatus status;
	
	@Transient
	Logger my_log;
	
	@Transient
	private boolean isWaitingForAction;
	
	@Transient
	private HashMap<String, Boolean> isWaitingForActionMap;
	
	@Transient
	private ConnectionHandler handler;

	@Transient
	private UsersTable DBUser;
	public User(){
		
	}
	
	
	public User(String id,String ID, String password, String name, String email, int totalCash, int score, int league,String avatar){
		DBUser = UsersTable.getInstance();
		//for database use only
		this.id = id;
		
		this.ID = ID;
		this.password=password;
		this.name = name;
		this.email = email;
		this.totalCash = totalCash ;
		this.score = score;
		isWaitingForAction=false;
		status = UserStatus.DISCONNECTED;
		my_log = Logger.getLogger(User.class);
		isWaitingForActionMap = new HashMap<String,Boolean>();
		this.league = league;
		this.avatar=avatar;

	}
	
	public void init(){

			isWaitingForAction=false;
			status = UserStatus.CONNECTED;
			my_log = Logger.getLogger(User.class);
			isWaitingForActionMap = new HashMap<String,Boolean>();

		
	}
	
	public void getLog(LinkedList<String> i_game_logs){
		
		   for(String s :i_game_logs){
			   my_log.info(s);
		   }
	}
	
	public void getLog(String  i_game_logs){
		 
		  
		 my_log.info(i_game_logs);
		  
	}
	
	public void editName(String newName) {
		UsersTable.getInstance().UpdateUserName(this.id, newName);
		this.name = newName;
	}
	
	public void editEmail(String newEmail) {
		UsersTable.getInstance().UpdateUserEmail(this.id, newEmail);
		this.email = newEmail;
	}
	
	public void editPassword(String newPassword) {
		UsersTable.getInstance().UpdateUserPassword(this.id, newPassword);
		this.password = newPassword;
	}
	
	public String getID() {
		return ID;
	}

	public void setID(String iD) {
		ID = iD;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		UsersTable.getInstance().UpdateUserName(this.id, name);
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		UsersTable.getInstance().UpdateUserEmail(this.id, email);
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		UsersTable.getInstance().UpdateUserPassword(this.id, password);
		this.password = password;
	}

	public int getTotalCash() {
		return totalCash;
	}

	public void setTotalCash(int totalCash) {
		UsersTable.getInstance().UpdateUserTotalCash(this.id, totalCash+"");
		this.totalCash = totalCash;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		UsersTable.getInstance().UpdateUserScore(this.id, score+"");
		this.score = score;
	}

	public UserStatus getStatus() {
		return status;
	}

	public void setStatus(UserStatus status) {
		this.status = status;
	}

	public int getLeague() {
		return league;
	}

	public void setLeague(int league) {
		UsersTable.getInstance().UpdateUserLeague(this.id, league+"");
		this.league = league;
	}

	/**
	 * HNAD= *TYPE,NUMBER,TYPE,NUMBER*
	 * *AVATAR*=STRING
	 * CASH= *NUMBER*
	 * PLAYERS = "*PLAYER USER NAME*,*PLAYER NAME*,*CASH*,*HAND*,*AVATAR* "{0,n}
	 * 
	 * CARDS = "*CARD NUMBER* *CARD TYPE* "{0,n}
	 * 
	 * GAME FULL DETAILS= "GameID=*ID*&players=*PLAYERS*&activePlayers=*PLAYERS*&blindBit=*NUMBER*&CurrentPlayer=*PLAYER USER NAME*&
	 * table=*CARDS*&MaxPlayers=*NUMBER*&activePlayersNumber=*NUMBER*&cashOnTheTable=*NUMBER*&CurrentBet=*NUMBER*"
	 * this function sends GAMEUPDATED message to the client "GAMEUPDATE *GAME FULL DETAILS*"
	 */
	public void GameUpdated(GameInterface game) {
		if(handler !=null&&this.status == UserStatus.CONNECTED)
		this.handler.send("GAMEUPDATE "+GameToString((Game)game));
		
	}

	public void SendMSG(String game){
		this.handler.send(game);
		
	}
	private String GameToString(Game game){

		String result="GameID="+game.getGameID();
		result= result+"&players=";
		Player[] players= game.getPlayers();
		for(int i=0;i<players.length;i++){
			String hand = getCardsPlayer(players, i);
			
			result = result+players[i].getUser().getID()+","+ players[i].getUser().getName()+"," +players[i].getUser().getTotalCash()+","+ hand+","+players[i].getUser().getAvatar()+",";
		}
		result = result + "&activePlayers=";
		 players= game.getActivePlayers();
		for(int i=0;i<players.length;i++){
			String hand = getCardsPlayer(players, i);
			
			result = result+players[i].getUser().getID()+","+ players[i].getUser().getName()+"," +players[i].getUser().getTotalCash()+","+ hand+","+players[i].getUser().getAvatar()+",";
		}
		result = result + "&spectators=";
		Spectator [] spectators= game.getSpectators();
		for(int i=0;i<spectators.length;i++){
			result = result+spectators[i].getUser().getID()+","+ spectators[i].getUser().getName()+",";
		}
		result = result + "&blindBit="+game.getBlindBit();
		if(game.getCurrentPlayer()!=null) result = result+"&CurrentPlayer="+game.getCurrentPlayer().getUser().getID();
		else result = result+"&CurrentPlayer=";
		//result = result + game.getCurrentPlayer()!=null ? "&CurrentPlayer="+game.getCurrentPlayer().getUser().getID(): "&CurrentPlayer=";
		result = result + "&table=";
		
		for(int i=0;i<game.getCardsOnTable();i++){
			
			result = result+game.getTable()[i].getNumber()+","+ game.getTable()[i].getType()+",";

		}
		result = result + "&MaxPlayers="+game.getpreferences().getMaxPlayersNum();
		result = result + "&cashOnTheTable="+game.getCashOnTheTable();
		result = result + "&CurrentBet="+game.getCurrentBet();
		return result;
	}

	private String getCardsPlayer(Player[] players, int i) {
		String hand =""; 
		Card[]	PlayerCards=	players[i].getCards();
		if(PlayerCards[0]!=null&&PlayerCards[1]!=null){
			hand +=PlayerCards[0].getType()+" "+PlayerCards[0].getNumber()+" ";
			hand+=PlayerCards[1].getType()+" "+PlayerCards[1].getNumber();
		}
		else{
			hand+="NULL NULL NULL NULL";
		}
		return hand;
	}
	/**
	 * this function sends TAKEACTION request to the client to make some action "TAKEACTION *GAME ID*"
	 */
	@Override
	public boolean takeAction(String GameID,int minBit) {
		if(isWaitingForActionMap == null) isWaitingForActionMap = new HashMap<>(); 
		if(this.handler!=null&&this.status == UserStatus.CONNECTED){
		if(this.isWaitingForActionMap.containsKey(GameID))return false;
		else{
			
			this.isWaitingForActionMap.put(GameID, true);
		}
		
		
		this.handler.send("TAKEACTION "+GameID+" "+minBit);
		while(this.isWaitingForActionMap.get(GameID));
		this.isWaitingForActionMap.remove(GameID);
		return true;
	}
	return false;
}
	@Override
	public boolean changeMoney(int money) {
		if(totalCash  + money >0){
		totalCash+=money;
		UsersTable.getInstance().UpdateUserTotalCash(this.id, totalCash+"");
		return true;}
		return false;
	}




	public void giveHandler(ConnectionHandler handler) {
	    this.handler = handler;
	}

	@Override
	public void actionMaked(String GameID) {
		if(isWaitingForActionMap == null) isWaitingForActionMap = new HashMap<>();
		if(this.isWaitingForActionMap.containsKey(GameID)){
		this.isWaitingForActionMap.replace(GameID, false);
		}
	}
	public boolean isWaiting(){
		
		return this.isWaitingForAction;
	}

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		UsersTable.getInstance().UpdateUserAvatar(this.id, avatar);
		this.avatar = avatar;
	}

}
