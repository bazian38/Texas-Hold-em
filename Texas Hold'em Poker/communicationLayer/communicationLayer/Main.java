package communicationLayer;

import java.io.IOException;




public class Main {
	public static void main(String[] args) throws IOException {
		runThreadPerClient(args);
	

	}
	private static void runThreadPerClient(String [] args){
       if(args!=null&&args.length!=1){
	  System.err.println("Usage: java Thread per Client <port> ");
      System.exit(1);
	
}
		int port = Integer.parseInt(args[0]);

		ThreadPerClientServer server = new ThreadPerClientServer(port);
		Thread serverThread = new Thread(server);
		serverThread.start();
		try {
			serverThread.join();
		} catch (InterruptedException e) {
			System.out.println("Server stopped");
		}
	}
}
