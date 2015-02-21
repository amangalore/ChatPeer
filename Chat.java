import java.net.*;
import java.io.*;
import java.util.*;

public class Chat {
	public static void main (String[] args) throws Exception{
		if(args.length != 1){
			System.err.println("Invalid arguments: "+
					"chat <clientID>");
			System.exit(1);
		}
		File file = new File("Directory.txt");
		Scanner scan = new Scanner(file);
		//read in names and locations of peers;
		String user1 = scan.next();
		String socketstringBob1 = scan.next();
		String socketstringBob2 = scan.next();
		String user2 = scan.next();
		String socketstringMary1 = scan.next();
		String socketstringMary2 = scan.next();
		String user3 = scan.next();
		String socketstringJesse1 = scan.next();
		String socketstringJesse2 = scan.next();
		int outbound1 = 0;
		int outbound2 = 0;
		int inbound = 0;
		int Bobsocket1 = Integer.parseInt(socketstringBob1);
		int Bobsocket2 = Integer.parseInt(socketstringBob2);
		int Marysocket1 = Integer.parseInt(socketstringMary1);
		int Marysocket2 = Integer.parseInt(socketstringMary2);
		int Jessesocket1 = Integer.parseInt(socketstringJesse1);
		int Jessesocket2 = Integer.parseInt(socketstringJesse2);
		String name = args[0];
		
		if (name == "Bob"){			
			outbound1 = Marysocket1;
			outbound2 = Jessesocket1;
			inbound = Bobsocket1;
		}
		else if (name == "Mary"){
			outbound1 = Bobsocket1;
			outbound2 = Jessesocket1;
			inbound = Marysocket1;
		}
		else if (name == "Jesse"){
			outbound1 = Bobsocket1;
			outbound2 = Marysocket1;
			inbound = Jessesocket1;
		}
		
		OutboundHandler outHandler = new OutboundHandler(outbound1, outbound2, name);
		Thread outThread = new Thread(outHandler);				
		outThread.start();				
		
		ServerSocket listenSock = new ServerSocket(inbound);				
		while(true){	
			Socket clientSock = listenSock.accept();					
			InboundHandler inhandler = new InboundHandler(clientSock);
			Thread inThread = new Thread(inhandler);
			inThread.start();
		}
	
	}
}

final class OutboundHandler implements Runnable{
	
	int firstout;
	int secondout;
	String host;
	
	public OutboundHandler(int firstout,int secondout,String host) throws Exception{
		this.firstout = firstout;
		this.secondout = secondout;
		this.host = host;
	}
	
	public void run(){
		
		String message = " ";
		sendMessage("join", null);
		System.out.println("Welcome to chat client!");
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		while(true){
			try{
				message = reader.readLine();
			} catch (IOException ioe) {
		        System.out.println("IO error trying to read message!");
		        System.exit(1);
		    }

		    if(message.equals("exit")){
		    	break;
		    }

		    //Print your message to your screen and then send message to other clients
			System.out.println(host + ": "+ message);
			sendMessage("msg", message);
		}
		
		sendMessage("exit", null);
		
		
	}
	
	private void sendMessage(String type, String message){
		if (type == "join"){
		try{
			Socket socket = new Socket("127.0.0.1", firstout);
		     DataOutputStream os = new DataOutputStream(socket.getOutputStream());
		     String send = "status "+ host + " " + "joined";
		     os.writeChars(send);
		     os.close();
		     socket.close();
		   } catch (UnknownHostException e) {
		     //if client isn't online ignore this
		   } catch  (IOException e) {
		     //ignore errors
		   }
		try{
			Socket socket2 = new Socket("127.0.0.1", secondout);
		     DataOutputStream os2 = new DataOutputStream(socket2.getOutputStream());
		     String send2 = "status "+ host + " " + "joined";
		     os2.writeChars(send2);
		     os2.close();
		     socket2.close();
		   } catch (UnknownHostException e) {
		     //if client isn't online ignore this
		   } catch  (IOException e) {
		     //ignore errors
		   }
		}
		else if (type.equals("msg")){
			try{
			Socket socket = new Socket("127.0.0.1", firstout);
			DataOutputStream os = new DataOutputStream(socket.getOutputStream());
		     String send = "msg "+ host + " " + message;
		     os.writeChars(send);
		     os.close();
		     socket.close();
		   } catch (UnknownHostException e) {
		     //if client isn't online ignore this
		   } catch  (IOException e) {
		     //ignore this
		   }
			try{
				Socket socket2 = new Socket("127.0.0.1", secondout);
				DataOutputStream os = new DataOutputStream(socket2.getOutputStream());
			     String send2 = "msg "+ host + " " + message;
			     os.writeChars(send2);
			     os.close();
			     socket2.close();
			   } catch (UnknownHostException e) {
			     //if client isn't online ignore this
			   } catch  (IOException e) {
			     //ignore this
			   }
			}
		else if (type.equals("exit")){
			try{
				 Socket socket = new Socket("127.0.0.1", firstout);
			     DataOutputStream os = new DataOutputStream(socket.getOutputStream());
			     String send = "status " + host + " " + "left";
			     os.writeChars(send);
			     os.close();
			     socket.close();
			   } catch (UnknownHostException e) {
			     //if client isn't online ignore this
			   } catch  (IOException e) {
			     //
			   }
			try{
				 Socket socket = new Socket("127.0.0.1", secondout);
			     DataOutputStream os = new DataOutputStream(socket.getOutputStream());
			     String send2 = "status " + host + " " + "left";
			     os.writeChars(send2);
			     os.close();
			     socket.close();
			   } catch (UnknownHostException e) {
			     //if client isn't online ignore this
			   } catch  (IOException e) {
			     //
			   }
			//Exit the program with a sucess.
		    System.out.println("Quiting Chat Client ...");
		    System.exit(0);	
			
		}
		else{
			System.err.println("Incorrect message Type!");
			System.exit(1);
		}
		   
	}
}

final class InboundHandler implements Runnable{
	Socket socket;

	// Constructor
	public InboundHandler(Socket socket) throws Exception{
		this.socket = socket; //save the socket object to "this" instance of the class
	}

	public void run(){
		String input = null;
		try{
			InputStream is = socket.getInputStream();
			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader br = new BufferedReader(isr);

			// Get and display the header lines.
			input = br.readLine();
		} catch  (IOException e) {
	     
	   	}

		String[] tokens = input.split(" ");
		String msgType = tokens[0];
		String peerName = tokens[1];
		String message = " ";

		for(int i = 2; i<tokens.length; i++){
          message = message+" " + tokens[i];
        } 
        msgType = msgType.trim();

        if (msgType.length() == 11){
        	System.out.println(peerName + " has " + message+" the chat.");
        } else{
        	System.out.println(peerName + ": " + message);
        }
	}
}
