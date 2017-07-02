package communicationLayer;

import java.io.IOException;
import java.net.ServerSocket;


public class ThreadPerClientServer implements Runnable  {

	private ServerSocket serverSocket;
	private int listenPort;

	public ThreadPerClientServer(int port) {
		serverSocket = null;
		listenPort = port;
	}

	public void run() {
		try {
			serverSocket = new ServerSocket(listenPort);
			System.out.println("Listening...");
		} catch (IOException e) {
			System.out.println("Cannot listen on port " + listenPort);
		}

		while (true&&serverSocket!=null) {
			try {
				ConnectionHandler newConnection = new ConnectionHandler(serverSocket.accept(), new MyServerProtocol());
				new Thread(newConnection).start();
			} catch (IOException e) {
				System.out.println("Failed to accept on port " + listenPort);
			}
		}
	}

	// Closes the connection
	public void close() throws IOException {
		serverSocket.close();
	}


}
