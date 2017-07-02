package tests;

import java.util.logging.Logger;

import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

public class temp {
	
	Logger logger = Logger.getLogger(temp.class.getName());
	
	@Test
	public void runTest() {
		Result result;
		result = JUnitCore.runClasses(AcceptanceTests.class);
		printResult(result, "Register");
		
	}
	
	private void printResult(Result res, String testCaseName) {
		System.out.println(testCaseName + ": tests failed - " + res.getFailureCount());
		for (Failure failure : res.getFailures())
	         System.out.println(failure.toString());
		
	    System.out.println();
	}
}
