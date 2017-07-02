package Game;

import Game.Enum.CardType;

/**
 * 
 * @author ahmad
 * This class holding the Card information
 */
public class Card {
   private int number;
   private CardType type;
   
   public Card(int number, CardType type){
	   
	   this.number = number;
	   this.type=type;
	   
   }

public int getNumber() {
	return number;
}

public CardType getType() {
	return type;
}

 
  
}
