package tests;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Test;

import Game.GamePreferences;
import Game.Enum.GameType;
import System.GameCenter;
import serviceLayer.*;
import user.User;

public class AcceptanceTests {
	
	private serviceLayerInterface service = serviceLayer.getInstance();
	private GameCenter gameCenter = (GameCenter) GameCenter.getInstance();
	
	@After
	public void removeUserAndGames() {
		try {
			gameCenter.removeAll();
		}
		catch(Exception e){}
	}

	@Test
	public void registerTestCase() {
		System.out.println("registerTestCase");
		//missing argument
		assertEquals(service.register("usr1 123456 user_name user@name.com"), "REG FAILED");
		assertEquals(service.register("REG 123456 user_name user@name.com"), "REG FAILED");
		assertEquals(service.register("REG usr1 user_name user@name.com"), "REG FAILED");
		assertEquals(service.register("REG usr1 123456 user@name.com"), "REG FAILED");
		assertEquals(service.register("REG usr1 123456 user_name "), "REG FAILED");
		
		//short password
		assertEquals(service.register("REG usr1 1 user_name user@name.com"), "REG FAILED");
		assertEquals(service.register("REG usr1 1234567 user_name user@name.com"), "REG FAILED");
		
		//illegal email
		assertEquals(service.register("REG usr1 12345678 user_name user123"), "REG FAILED");
		assertEquals(service.register("REG usr1 12345678 user_name @.com"), "REG FAILED");
		assertEquals(service.register("REG usr1 12345678 user_name asd@.com"), "REG FAILED");
		assertEquals(service.register("REG usr1 12345678 user_name asd@qwe"), "REG FAILED");
		
		//successful registration
		assertEquals(service.register("REG usr1 12345678 user1 user1@example.com"), "REG DONE");
		assertEquals(service.register("REG usr2 12345678 user2 user2@example.com"), "REG DONE");
		assertEquals(service.register("REG usr3 12345678 user3 user3@example.com"), "REG DONE");
		assertEquals(service.register("REG usr4 12345678 user4 user4@example.com"), "REG DONE");
		assertEquals(service.register("REG usr5 12345678 user5 user5@example.com"), "REG DONE");
		
		//already exists user
		assertEquals(service.register("REG usr1 12345678 agbaria agbaria@gmail.com"), "REG FAILED");
	}

	
	
	@Test
	public void loginTestCase() {
		System.out.println("loginTestCase");
		//missing argument
		assertEquals(service.login("usr 123456789", null), "LOGIN FAILED");
		assertEquals(service.login("LOGIN 123456789", null), "LOGIN FAILED");
		assertEquals(service.login("LOGIN usr", null), "LOGIN FAILED");
		
		//user does not exist
		assertEquals(service.login("LOGIN usr 123456789", null), "LOGIN FAILED");
		
		//incorrect password
		assertEquals(service.login("LOGIN usr1 123456789", null), "LOGIN FAILED");
		
		//successful login
		service.register("REG usr6 12345678 user6 user6@example.com");
		assertEquals(service.login("LOGIN usr6 12345678", null), "LOGIN DONE usr6 user6 0 0 -1");
	}

	@Test
	public void logoutTestCase() {
		System.out.println("logoutTestCase");
		//missing argument
		assertEquals(service.logout("LOGOUT"), "LOGOUT FAILED NAME OR FULL INSTRUCTION MISSED");
		assertEquals(service.logout("user1"), "LOGOUT FAILED NAME OR FULL INSTRUCTION MISSED");

		//successful logout
		service.login("LOGIN usr6 12345678", null);
		assertEquals(service.logout("LOGOUT usr6"), "LOGOUT DONE");
	}

	@Test
	public void editUserPasswordTestCase() {
		System.out.println("editUserPasswordTestCase");
		//missing argument
		assertEquals(service.editUserPassword("*USER_NAME* *OLDPASSWORD* *NEWPASSWORD*"), "EDITPASS FAILED");
		assertEquals(service.editUserPassword("EDITPASS *OLDPASSWORD* *NEWPASSWORD*"), "EDITPASS FAILED");
		assertEquals(service.editUserPassword("EDITPASS *USER_NAME* *NEWPASSWORD*"), "EDITPASS FAILED");
		assertEquals(service.editUserPassword("EDITPASS *USER_NAME* *OLDPASSWORD*"), "EDITPASS FAILED");
		
		service.register("REG usr6 12345678 user6 user6@example.com");
		//wrong old password
		assertEquals(service.editUserPassword("EDITPASS usr6 12345679 1234567899"), "EDITPASS FAILED");
		
		//short password
		assertEquals(service.editUserPassword("EDITPASS usr6 12345678 123"), "EDITPASS FAILED");
		
		//successful password editing
		assertEquals(service.editUserPassword("EDITPASS usr6 12345678 123123123"), "EDITPASS DONE");
	}
	
	@Test
	public void editUserEmailTestCase() {
		System.out.println("editUserEmailTestCase");
		service.register("REG usr6 12345678 user6 user6@example.com");
		//missing argument
		assertEquals(service.editUserEmail("*USER_NAME* *NEWEMAIL*"), "EDITUSEREMAIL FAILED");
		assertEquals(service.editUserEmail("EDITUSEREMAIL *NEWEMAIL*"), "EDITUSEREMAIL FAILED");
		assertEquals(service.editUserEmail("EDITUSEREMAIL *USER_NAME*"), "EDITUSEREMAIL FAILED");
		
		//illegal email
		assertEquals(service.editUserEmail("EDITUSEREMAIL usr6 newEmail"), "EDITUSEREMAIL FAILED");
		assertEquals(service.editUserEmail("EDITUSEREMAIL usr6 @.com"), "EDITUSEREMAIL FAILED");
		assertEquals(service.editUserEmail("EDITUSEREMAIL usr6 asd@.com"), "EDITUSEREMAIL FAILED");
		assertEquals(service.editUserEmail("EDITUSEREMAIL usr6 asd@qwe"), "EDITUSEREMAIL FAILED");
		
		//successful email editing
		assertEquals(service.editUserEmail("EDITUSEREMAIL usr6 new@example.com"), "EDITUSEREMAIL DONE");
		
	}
	
	@Test
	public void editUserAvatarTestCase() {
		System.out.println("editUserAvatarTestCase");
		// TODO Auto-generated method stub
		
	}

	@Test
	public void createGameTestCase() {
		System.out.println("createGameTestCase");
		//missing argument
		assertEquals(service.createGame("*USER_NAME* *GAME_PREF*"), "CREATEGAME FAILED");
		assertEquals(service.createGame("CREATEGAME *GAME_PREF*"), "CREATEGAME FAILED");
		assertEquals(service.createGame("CREATEGAME *USER_NAME*"), "CREATEGAME FAILED");
		
		String prefs = "gameTypePolicy=NO_LIMIT&potLimit=0&buyInPolicy=0&chipPolicy=10&"
				+ "minBet=00&minPlayersNum=2&maxPlayersNum=8&spectatable=T&leaguable=T&league=0";
		
		String prefs2 = "gameTypePolicy=NO_LIMIT&potLimit=0&buyInPolicy=2000&chipPolicy=10&"
				+ "minBet=00&minPlayersNum=2&maxPlayersNum=8&spectatable=T&leaguable=T&league=0";
		
		String prefs3 = "gameTypePolicy=NO_LIMIT&potLimit=0&buyInPolicy=10&chipPolicy=10000&"
				+ "minBet=00&minPlayersNum=2&maxPlayersNum=8&spectatable=T&leaguable=T&league=0";
		
		
		//non existing user
		assertEquals(service.createGame("CREATEGAME usr " + prefs), "CREATEGAME FAILED");
		
		User senior = new User("1","sen_user", "123456789", "firstname", "first@mail.com", 1000, 100000, 0,"");
		gameCenter.addUser(senior);
		//successful create game
		assertNotEquals(service.createGame("CREATEGAME sen_user " + prefs), "CREATEGAME FAILED");
		assertNotEquals(service.createGame("CREATEGAME sen_user " + prefs), "CREATEGAME FAILED");
		
		//no enough cash for buyIn
		assertEquals(service.createGame("CREATEGAME sen_user " + prefs2), "CREATEGAME FAILED");
		
		//no enough cash for chipPolicy
		assertEquals(service.createGame("CREATEGAME sen_user " + prefs3), "CREATEGAME FAILED");
		
	}

	@Test
	public void listJoinableGamesTestCase() {
		System.out.println("listJoinableGamesTestCase");
		//missing argument
		assertEquals(service.listJoinableGames("*USER_NAME*"), "LISTJOINABLEGAMES FAILED");
		assertEquals(service.listJoinableGames("LISTJOINABLEGAMES"), "LISTJOINABLEGAMES FAILED");
		
		User senior = new User("1","senior", "123456789", "name2", "name2@mail.com", Integer.MAX_VALUE, 100000, 5,"");
		gameCenter.addUser(senior);
		assertEquals(service.listJoinableGames("LISTJOINABLEGAMES senior").split(" ")[1], "DONE");
		
		try {
			User p = new User("1","ppp", "123456789", "ppp", "ppp@mail.com", 100000, 100000, 5,"");
			gameCenter.addUser(p);
			gameCenter.createGame("senior", new GamePreferences(GameType.NO_LIMIT, 0, 0, 100, 100, 2, 8, true, true, 5));
			gameCenter.createGame("senior", new GamePreferences(GameType.NO_LIMIT, 0, 0, 100, 100, 2, 8, true, true, 5));
			gameCenter.createGame("senior", new GamePreferences(GameType.NO_LIMIT, 0, 0, 100, 100, 2, 8, true, true, 5));
			
			
		} catch (Exception e) {}
		
		int c = 0;
		for (String str : service.listJoinableGames("LISTJOINABLEGAMES ppp").split(" ")) {
			if(str.equals("ENDGAME")) c++;
		}
		assertEquals(c, 3);
		
		User noLeague = new User("1","new", "123456789", "new", "new@mail.com", Integer.MAX_VALUE, 100000, -1,"");
		gameCenter.addUser(noLeague);
		
		//new player with league -1
		assertTrue(service.listJoinableGames("LISTJOINABLEGAMES new").split(" ").length > 3);
		
		User poor = new User("1","poor", "123456789", "poor", "poor@mail.com", 0, 0, 0,"");
		gameCenter.addUser(poor);
		
		assertEquals(service.listJoinableGames("LISTJOINABLEGAMES poor"), "LISTJOINABLEGAMES DONE ");
		
	}

	@Test
	public void searchGamesByPrefsTestCase() {
		System.out.println("searchGamesByPrefsTestCase");
		//missing argument
		assertEquals(service.searchGamesByPrefs("*GAME_PREF*"), "SEARCHGAMESBYPREFS FAILED");
		assertEquals(service.searchGamesByPrefs("SEARCHGAMESBYPREFS"), "SEARCHGAMESBYPREFS FAILED");
		
		User senior = new User("1","sen_user", "123456789", "firstname", "first@mail.com", 1000, 100000, 5,"");
		gameCenter.addUser(senior);
		try {
			gameCenter.createGame("sen_user", new GamePreferences(GameType.NO_LIMIT, 0, 0, 10, 0, 2, 8, true, true, 5));
			gameCenter.createGame("sen_user", new GamePreferences(GameType.NO_LIMIT, 0, 0, 10, 0, 2, 8, true, true, 5));
			gameCenter.createGame("sen_user", new GamePreferences(GameType.LIMIT, 1000, 500, 100, 100, 4, 5, false, false, 0));
		} catch (Exception e) {}
		
		String prefs1 = "gameTypePolicy=NO_LIMIT&potLimit=0&buyInPolicy=0&chipPolicy=10&"
				+ "minBet=0&minPlayersNum=2&maxPlayersNum=8&spectatable=T&leaguable=T&league=5";
		
		String prefs2 = "gameTypePolicy=LIMIT&potLimit=1000&buyInPolicy=500&chipPolicy=100&"
				+ "minBet=100&minPlayersNum=4&maxPlayersNum=5&spectatable=F&leaguable=F&league=0";
		
		String prefs3 = "gameTypePolicy=NO_LIMIT&potLimit=0&buyInPolicy=10&chipPolicy=10000&"
				+ "minBet=00&minPlayersNum=2&maxPlayersNum=8&spectatable=T&leaguable=T&league=0";
		
		int c1 = 0, c2 = 0;
		for (String str : service.searchGamesByPrefs("SEARCHGAMESBYPREFS " + prefs1).split(" ")) {
			if(str.equals("ENDGAME")) c1++;
		}
		
		for (String str : service.searchGamesByPrefs("SEARCHGAMESBYPREFS " + prefs2).split(" ")) {
			if(str.equals("ENDGAME")) c2++;
		}
		
		//2 and 1 matching games 
		assertEquals(c1, 2);
		assertEquals(c2, 1);
		
		//no matching games
		assertEquals(service.searchGamesByPrefs("SEARCHGAMESBYPREFS " + prefs3), "SEARCHGAMESBYPREFS DONE ");
		
	}

	@Test
	public void searchGamesByPlayerNameTestCase() {
		System.out.println("searchGamesByPlayerNameTestCase");
		//missing argument
		assertEquals(service.searchGamesByPlayerName("*USER_NAME*"), "SEARCHGAMESBYPLAYERNAME FAILED");
		assertEquals(service.searchGamesByPlayerName("SEARCHGAMESBYPLAYERNAME"), "SEARCHGAMESBYPLAYERNAME FAILED");
		
		User senior = new User("1","sen_user", "123456789", "name1", "first@mail.com", 1000, 100000, 5,"");
		gameCenter.addUser(senior);
		try {
			gameCenter.createGame("sen_user", new GamePreferences(GameType.NO_LIMIT, 0, 0, 10, 0, 2, 8, true, true, 5));
			gameCenter.createGame("sen_user", new GamePreferences(GameType.NO_LIMIT, 0, 0, 10, 0, 2, 8, true, true, 5));
			gameCenter.createGame("sen_user", new GamePreferences(GameType.LIMIT, 1000, 500, 100, 100, 4, 5, false, false, 0));
		} catch (Exception e) {}
		
		int c1 = 0;
		for (String str : service.searchGamesByPlayerName("SEARCHGAMESBYPLAYERNAME name1").split(" ")) {
			if(str.equals("ENDGAME")) c1++;
		}
		
		//games with matching player name
		assertEquals(c1, 3);
		
		//no matching games
		assertEquals(service.searchGamesByPlayerName("SEARCHGAMESBYPLAYERNAME asdqwe"), "SEARCHGAMESBYPLAYERNAME DONE ");
		
	}

	@Test
	public void searchGamesByPotSizeTestCase() {
		System.out.println("searchGamesByPotSizeTestCase");
		//missing argument
		assertEquals(service.searchGamesByPotSize("*POT_SIZE*"), "SEARCHGAMESBYPOTSIZE FAILED");
		assertEquals(service.searchGamesByPotSize("SEARCHGAMESBYPOTSIZE"), "SEARCHGAMESBYPOTSIZE FAILED");
		
		User senior = new User("1","sen_user", "123456789", "name1", "first@mail.com", 1000, 100000, 5,"");
		gameCenter.addUser(senior);
		try {
			gameCenter.createGame("sen_user", new GamePreferences(GameType.NO_LIMIT, 100, 0, 10, 0, 2, 8, true, true, 5));
			gameCenter.createGame("sen_user", new GamePreferences(GameType.POT_LIMIT, 100, 0, 10, 0, 2, 8, true, true, 5));
			gameCenter.createGame("sen_user", new GamePreferences(GameType.POT_LIMIT, 1000, 500, 100, 100, 4, 5, false, false, 0));
		} catch (Exception e) {}
		
		//one matching game
		assertEquals(service.searchGamesByPotSize("SEARCHGAMESBYPOTSIZE 100").split(" ").length, 4);
		assertEquals(service.searchGamesByPotSize("SEARCHGAMESBYPOTSIZE 1000").split(" ").length, 4);
		
	}

	@Test
	public void joinGameTestCase() {
		System.out.println("joinGameTestCase");
		//missing argument
		assertEquals(service.joinGame("*GAME_ID* *USER_NAME*"), "JOINGAME FAILED BAD INSTRUCTION");
		assertEquals(service.joinGame("JOINGAME *USER_NAME*"), "JOINGAME FAILED BAD INSTRUCTION");
		assertEquals(service.joinGame("JOINGAME *GAME_ID*"), "JOINGAME FAILED BAD INSTRUCTION");
		
		User senior = new User("1","sen_user", "123456789", "name1", "first@mail.com", 10000, 100000, 1,"");
		User newUser = new User("1","newUser", "123456789", "newUser", "newUser@mail.com", 100, 100000, 1,"");
		gameCenter.addUser(senior);
		gameCenter.addUser(newUser);
		try {
			String id1 = gameCenter.createGame("sen_user", new GamePreferences(GameType.NO_LIMIT, 0, 0, 10, 0, 2, 8, true, true, 5));
			String id2 = gameCenter.createGame("sen_user", new GamePreferences(GameType.NO_LIMIT, 0, 500, 500, 50, 2, 8, true, false, 0));
			String id3 = gameCenter.createGame("sen_user", new GamePreferences(GameType.NO_LIMIT, 0, 0, 0, 0, 2, 5, false, true, 5));
			
			//join again same game
			assertEquals(service.joinGame("JOINGAME " + id1 + " sen_user"), "JOINGAME FAILED Can't join the game");
			
			//can't join game (no enough cash)
			assertEquals(service.joinGame("JOINGAME " + id2 + " newUser"), "JOINGAME FAILED Can't join the game");
			
			//can't join game (non matching league)
			assertEquals(service.joinGame("JOINGAME " + id3 + " newUser"), "JOINGAME FAILED Can't join the game");
		} catch (Exception e) {}
		
		//join again same game
		assertEquals(service.joinGame("JOINGAME  *USER_NAME*"), "JOINGAME FAILED Can't join the game");
		
	}

	@Test
	public void listSpectatableGamesTestCase() {
		System.out.println("listSpectatableGamesTestCase");
		// TODO Auto-generated method stub
		
	}

	@Test
	public void spectateGameTestCase() {
		System.out.println("spectateGameTestCase");
		// TODO Auto-generated method stub
		
	}

	@Test
	public void leaveGameTestCase() {
		System.out.println("leaveGameTestCase");
		// TODO Auto-generated method stub
		
	}

	@Test
	public void ActionTestCase() {
		System.out.println("ActionTestCase");
		// TODO Auto-generated method stub
		
	}

}
