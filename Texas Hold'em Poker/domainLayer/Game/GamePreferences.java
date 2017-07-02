package Game;

import java.util.LinkedList;
import java.util.concurrent.ThreadLocalRandom;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import Game.Enum.GameType;


@Entity
@Table(name="GamePreferences")
public class GamePreferences {
	
	@Transient
	 private GameType gameTypePolicy;
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="preferences_id")
	 private int preferences_id;
	
	
	@Column(name="lmt")
	 private int Limit;
	
	@Column(name="buyInPolicy")
	 private int buyInPolicy;
	
	@Column(name="chipPolicy")
	 private int chipPolicy;
	 
	 @Column(name="minBet")
	 private int minBet;
	 
	 @Column(name="minPlayersNum")
	 private int minPlayersNum;
	 
	 @Column(name="maxPlayersNum")
	 private int maxPlayersNum;
	 
	 @Column(name="spectatable")
	 private boolean spectatable;
	 
	 @Column(name="leaguable")
	 private boolean leaguable;
	 
	 @Column(name="league")
	 private int league;
	 
	 @Transient
	 private LinkedList<checker> CheckPlayer;
	 
	 @Transient
	 private LinkedList<checkingEquality> CheckEquality;
	 
	 @Column(name="checking")
	 private boolean  checking;
	 
	 @Column(name="checkingEquality")
	 private boolean  checkingEquality;
	 
	 public GamePreferences(){}
	 
	 
	 public GamePreferences(GameType gameTypePolicy,int potLimit, int buyInPolicy,
			int chipPolicy, int minBet, int minPlayersNum, int maxPlayersNum,
			boolean spectatable,boolean leaguable,int league) throws Exception {
		 this.preferences_id =  ThreadLocalRandom.current().nextInt(500, Integer.MAX_VALUE-10);
       CheckPlayer = new LinkedList<checker>();
       CheckEquality = new LinkedList<checkingEquality>();
       checking = true;
       checkingEquality = true;
	   this.buyInPolicy = buyInPolicy;
       if(buyInPolicy > 0){

    	   CheckPlayer.add((checker)player->{
    		   checking = checking && player.getUser().getTotalCash() > this.buyInPolicy;
    	   });
    	   
    	   CheckEquality.add(game->{
    		   checkingEquality = checkingEquality && game.getBuyInPolicy() == this.buyInPolicy;
    	   });
    	   
       }
	   this.chipPolicy = chipPolicy;
       if(chipPolicy > 0 ){
    	   

    	   CheckPlayer.add((checker)player->{
    		   checking = checking && player.getUser().getTotalCash() > this.chipPolicy + this.buyInPolicy;
    	   });
    	   
    	   CheckEquality.add(game->{
    		   checkingEquality = checkingEquality && game.getChipPolicy()== this.chipPolicy;
    	   });
    	   
       }
       this.minBet = minBet; 
       if(minBet > 0){ 
    	   CheckPlayer.add((checker)player->{
    		   checking = checking && player.getUser().getTotalCash() > this.minBet + this.chipPolicy + this.buyInPolicy;
    	   });
    	   
    	   CheckEquality.add(game->{
    		   checkingEquality = checkingEquality && game.getMinBet()== this.minBet;
    	   });
    	   
       }
       
       if(  ( minPlayersNum < 2 || 
    		 maxPlayersNum < 2 || minPlayersNum > maxPlayersNum) && (minPlayersNum!=-1 || maxPlayersNum!=-1))
			 throw new Exception("illegal arguments");
		 
		this.gameTypePolicy = gameTypePolicy;
		this.Limit=potLimit;
		   CheckEquality.add(game->{
    		   checkingEquality = checkingEquality && game.getGameTypePolicy()== this.gameTypePolicy && game.getLimit() == this.Limit;
    	   });
    	
		this.minPlayersNum = minPlayersNum;
		this.maxPlayersNum = maxPlayersNum;
		   CheckEquality.add(game->{
    		   checkingEquality = checkingEquality && (this.minPlayersNum == -1|| this.minPlayersNum == game.getMinPlayersNum()) && (this.maxPlayersNum == -1|| this.maxPlayersNum == game.getMaxPlayersNum());
    	   });
		
		this.spectatable = spectatable;
		   CheckEquality.add(game->{
    		   checkingEquality = checkingEquality && game.isSpectatable()== this.spectatable;
    	   });
		   
		this.leaguable = leaguable;
		if(this.leaguable){
			this.league = league;
			CheckPlayer.add((checker)player->{
	 		   checking = checking && (player.getUser().getLeague() == -1 || player.getUser().getLeague() == this.league);
	 	   });
			
			CheckEquality.add(game->{
	    		   checkingEquality = checkingEquality && game.getLeague() == this.league;
	    	   });
			
		}
		
	}

	public GameType getGameTypePolicy() {
		return gameTypePolicy;
	}

	public int getBuyInPolicy() {
		return buyInPolicy;
	}

	public int getChipPolicy() {
		return chipPolicy;
	}

	public int getMinBet() {
		return minBet;
	}

	public int getMinPlayersNum() {
		return minPlayersNum;
	}

	public int getMaxPlayersNum() {
		return maxPlayersNum;
	}

	public boolean isSpectatable() {
		return spectatable;
	}
	 public boolean isLeaguable() {
		return leaguable;
	}

	public int getLeague() {
		return league;
	}
	
	public boolean checkPlayer(Player player){
		CheckPlayer.forEach(c ->{c.check(player);});
		boolean checkingResult = checking;
		checking = true;
		return checkingResult;
		
	}

	
	public boolean checkEquality(GamePreferences game){
		CheckEquality.forEach(c ->{c.check(game);});
		boolean checkingResult = checkingEquality;
		checkingEquality = true;
		return checkingResult;
		
	}
	public int getLimit() {
		return Limit;
	}
	
	public int getPreferences_id() {
		return preferences_id;
	}


	public void setPreferences_id(int preferences_id) {
		this.preferences_id = preferences_id;
	}


}