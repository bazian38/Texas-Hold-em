package communicationLayer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;



public class ConnectionHandler implements Runnable{

private BufferedReader in;
private PrintWriter out;
private Socket clientSocket;
private ServerProtocol<String> protocol;

public ConnectionHandler(Socket acceptedSocket, ServerProtocol<String> p) {
	in = null;
	out = null;
	clientSocket = acceptedSocket;
	protocol = p;
	System.out.println("Accepted connection from client!");
	System.out.println("The client is from: " + acceptedSocket.getInetAddress() + ":" + acceptedSocket.getPort());
	
}











private void close() {
	try {
		if (in != null) {
			in.close();
		}
		if (out != null) {
			out.close();
		}

		clientSocket.close();
	} catch (IOException e) {
		System.out.println("Exception in closing I/O");
	}
	
}
public void run() {

	try {
		initialize();
	} catch (IOException e) {
		System.out.println("Error in initializing I/O");
	}

	try {
		process();
	} catch (IOException e) {
		System.out.println("Error in I/O");
	}
		System.out.println("Connection closed - bye bye...");
	close();	
}

public void initialize() throws IOException {
	// Initialize I/O
	in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(), "UTF-8"));
	out = new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream(), "UTF-8"), true);
	System.out.println("I/O initialized");
}
public void process() throws IOException {
	String msg;
	while ((msg = in.readLine()) != null)
	{
		System.out.println("Received \"" + msg + "\" from client");
		
	 protocol.processMessage(msg,resp->{
           synchronized (protocol) {
			
		
		 if(resp!=null) out.println(resp);
		 System.out.println("SENDING: "+resp);}
		 
	 },this);
		
		
	}
	

	

	}

int counter = 0;	
public void send(String msg){
	synchronized(out){
         counter++;

		System.out.println("SENDING +"+counter+" : "+msg);
	    if(msg!=null) out.println(msg);}
}

}
