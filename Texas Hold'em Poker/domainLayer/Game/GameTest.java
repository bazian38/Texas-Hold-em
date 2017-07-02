package Game;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import Game.Enum.CardType;
import Game.Enum.GameType;
import user.User;

public class GameTest {
     
	
    static Game g ;
    static User p1 = new User("1","1", "123", "Ahmad", "bazian@post.bgu.ac.il", 5000000, 100, 2,"");
    static User p2 =  new User("2","2", "123", "Ahmad", "bazian@post.bgu.ac.il", 5000000, 100, 2,"");
    static User p3 = new User("3","3", "123", "Ahmad", "bazian@post.bgu.ac.il", 5000000, 100, 2,"");
    User playerWithNoCash = new User("4","4", "123", "Ahmad", "bazian@post.bgu.ac.il", 5, 5, 2,"");
    User playerWithNoLeague = new User("3","3", "123", "Ahmad", "bazian@post.bgu.ac.il", 5000000, 100, 3,"");
    @BeforeClass
    public static void setUp(){
    	GamePreferences prefs = null;
		try {
			prefs = new GamePreferences(GameType.NO_LIMIT, 0, 20, 0, 20, 2, 8, true, true, 2);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	g = new Game(prefs, "123");
       }
   
    @Before
    public  void before(){
    	
       	GamePreferences prefs = null;
    		try {
    			prefs = new GamePreferences(GameType.NO_LIMIT, 0, 20, 0, 20, 2, 8, true, true, 2);
    		} catch (Exception e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
    	g = new Game(prefs, "123");
		g.joinGame(p1);
		g.joinGame(p2);
		g.joinGame(p3);
    	
    }
    
    @After
    public  void after(){
       	GamePreferences prefs = null;
    		try {
    			prefs = new GamePreferences(GameType.NO_LIMIT, 0, 20, 0, 20, 2, 8, true, true, 2);
    		} catch (Exception e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
    	g = new Game(prefs, "123");
		g.joinGame(p1);
		g.joinGame(p2);
		g.joinGame(p3);
    	
    }

	@Test
	public void testJoin() {
		
		try {
			
		}
		catch(Exception e) {
			return;
		}
		
		g.joinGame(p1);
		g.joinGame(p2);
		g.joinGame(p3);
		g.joinGame(p3);
		g.joinGame(playerWithNoCash);
		g.joinGame(playerWithNoLeague);
        Player[] players = g.getPlayers();   
	    assertEquals(g.getPlayerNumber(),3);
	    assertEquals(players[0].getUser(),p1);
	    assertEquals(players[1].getUser(),p2);
	    assertEquals(players[2].getUser(),p3);

	}
	@Test
	public void testSpectate() throws Exception{
		assertTrue(g.spectate(playerWithNoCash));
		GamePreferences prefs2 = new GamePreferences(GameType.NO_LIMIT, 0, 20, 0, 20, 2, 8, false, true, 2);
		Game gg = new Game(prefs2, "1234");
		assertFalse(gg.spectate(playerWithNoCash));
	}
	@Test
	public void testDealCardForTable(){
		
		g.dealCardsForTable(1);
		assertTrue(g.getCardsOnTable()==1);
		assertTrue(g.getTable()[0] !=null);
		assertTrue(g.getTable()[0].getNumber() >=0 && g.getTable()[0].getNumber() <=13);
		g.dealCardsForTable(2);
		assertTrue(g.getCardsOnTable()==3);
		assertTrue(g.getTable()[0] !=null);
		assertTrue(g.getTable()[1] !=null);
		assertTrue(g.getTable()[2] !=null);
		assertFalse(g.dealCardsForTable(3));
		assertTrue(g.getCardsOnTable()==3);
	}
	@Test
	public void FourOfAKind(){
		g.ExchangeWaitingPlayers();
		g.dealCardsForPlayers();
		Player[] players =g.getPlayers();
		players[0].giveCards(new Card(2, CardType.CLUBS), new Card(2,CardType.DIAMONDS));
		
		g.dealCardsForTable(5);
		g.getTable()[0] = new Card(2,CardType.HEARTS);
		g.getTable()[1] = new Card(2,CardType.SPADES);
		Player [] winners = g.FourOfAKind();
		boolean found = false;
		for(int i=0;i<winners.length;i++){
			if(winners[i] == players[0])
				found = true;
			
		}
		assertTrue(found);
		assertTrue(winners!=null );
		assertTrue(winners.length !=0);
		assertTrue(winners.length <=3);
	}
}
