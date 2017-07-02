package Game;

/**
 * 
 * @author ahmad
 * This interface is responsible on managing the Deck  
 */
public interface DeckInterface {
  
	/**
	 *  This query shuffling the Card Deck 
	 */
	void shuffle();
	
	/**
	 * This method get one card from the deck
	 * @return
	 */
	Card getCard();
}
