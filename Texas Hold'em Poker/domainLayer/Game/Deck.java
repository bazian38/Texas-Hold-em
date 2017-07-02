package Game;

import java.util.Random;

import Game.Enum.CardType;

public class Deck implements DeckInterface{
   private Card[] cards; 
   private int currentCardPlace;
   public Deck(){
	   
	    cards = new Card[52];
	   currentCardPlace = 0;
	   for(int i=1;i<=13;i++)
	   {
		   cards[(i-1)] = new Card(i,CardType.CLUBS);
		   cards[(i-1)+13] = new Card(i,CardType.DIAMONDS);
		   cards[(i-1)+26] = new Card(i,CardType.HEARTS);
		   cards[(i-1)+(13*3)] = new Card(i,CardType.SPADES);
	   }
	   
	   
   }
   
	public void shuffle() {
	    int newI;
	    Card temp;
	    Random randIndex = new Random();

	    for (int i = 0; i < 52; i++) {

	        // pick a random index between 0 and cardsInDeck - 1
	        newI = randIndex.nextInt(52);

	        // swap cards[i] and cards[newI]
	        temp = cards[i];
	        cards[i] = cards[newI];
	        cards[newI] = temp;
	    }
	}


	public Card getCard() {
		currentCardPlace++;
		if(currentCardPlace <=52)
		return cards[(currentCardPlace-1)];
		return null;
	}

}
