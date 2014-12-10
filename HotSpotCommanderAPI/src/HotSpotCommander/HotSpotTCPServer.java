package HotSpotCommander;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

public class HotSpotTCPServer implements HotSpotServerInterface {

	ServerSocket serverSocket = null;
    Handler uiHandler = null;
    Thread serverThread = null;
    int serverPort = 5566;
    
	Dictionary<Socket,Thread> threads;
	Dictionary<Socket,PrintWriter> printWriters;
	List<Socket> clients;
	List<HotSpotServerEventHandler> eventHandlers = new ArrayList<HotSpotServerEventHandler>();
	
	boolean listening = false;
	
	
	
	@Override
	public void Start(int port) throws Exception {

		if(listening) throw new Exception("Already Started");
		
		ClearAllState();
		
		serverPort = port;
		listening = true;
		
		//uiThread?
		uiHandler = new Handler(Looper.getMainLooper());
		
		
		threads = new Hashtable<Socket,Thread>();
		printWriters = new Hashtable<Socket,PrintWriter>();
		clients = new ArrayList<Socket>();
		
		this.serverThread = new Thread(new ServerListenRunnable());
		this.serverThread.start();
		
	}
	
	class ServerListenRunnable implements Runnable {

		public void run() {
			Socket socket = null;
			try {
				serverSocket = new ServerSocket(serverPort);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			
			while (!Thread.currentThread().isInterrupted()) {

				try {
					
					socket = serverSocket.accept();
					
					CommunicationRunnable commThread = new CommunicationRunnable(socket);
					Thread t = new Thread(commThread);
					t.start();
					
					PrintWriter out = new PrintWriter(new BufferedWriter(
							new OutputStreamWriter(socket.getOutputStream())),
							true);
					
					printWriters.put(socket, out);
					threads.put(socket, t);
					clients.add(socket);
					
					uiHandler.post(new connectionRunnable(socket));
					

				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	class CommunicationRunnable implements Runnable {

		private Socket clientSocket;

		private BufferedReader input;

		public CommunicationRunnable(Socket clientSocket) {

			this.clientSocket = clientSocket;

			try {

				this.input = new BufferedReader(new InputStreamReader(this.clientSocket.getInputStream()));

			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		public void run() {

			while (!Thread.currentThread().isInterrupted()) {

				try {

					String read = input.readLine();

					uiHandler.post(new receiveMessageRunnable(read,this.clientSocket));

				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	private void ClearAllState() throws IOException
	{
		if(this.serverThread!=null)
		{
			this.serverThread.interrupt();
			this.serverThread = null;
		}
		
		
		if(clients!=null)
		{
			for(Socket s : clients)
			{
				threads.get(s).interrupt();
				printWriters.get(s).close();
				s.close();
			}
		}
		
		threads = null;
		clients = null;
		printWriters =null;
		
		if(serverSocket!=null)
		{
			serverSocket.close();
		}
		
	   
		
		uiHandler = null;
	}

	@Override
	public void Stop() {
		
		try {
			ClearAllState();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		listening = false;
	}

	@Override
	public void RegisterHandler(HotSpotServerEventHandler handler) {
		// TODO Auto-generated method stub
		eventHandlers.add(handler);
	}

	@Override
	public List<Socket> GetConnectedClients() {
		// TODO Auto-generated method stub
		
		ArrayList<Socket> list = new ArrayList<Socket>();
		
		for(Socket s : clients)
		{
			list.add(s);
		}
		
		return list;
	}

	@Override
	public void BroadcastMessage(String message) {
		// TODO Auto-generated method stub
		
		for(Socket s : clients)
		{
			printWriters.get(s).println(message);
		}
	}

	@Override
	public void SendMessage(String message) {
		// TODO Auto-generated method stub
		
		printWriters.get(clients.get(0)).println(message);
		

	}

	@Override
	public boolean IsConnected() {
		
		if(clients==null)return false;
		else return clients.size()>0;
	}
	
	class receiveMessageRunnable implements Runnable 
	{
		private String msg;
		private Socket client;
		
		public receiveMessageRunnable(String str,Socket socket) {
			this.msg = str;
			this.client = socket;
		}

		@Override
		public void run() {
			 for(HotSpotServerEventHandler handler : eventHandlers)
			 {
				 handler.OnReceiveMessage(client, msg) ;
			 }
		}
	}
	
	class connectionRunnable implements Runnable 
	{
		private Socket client;
		
		public connectionRunnable(Socket socket) {
			this.client = socket;
		}

		@Override
		public void run() {
			 for(HotSpotServerEventHandler handler : eventHandlers)
			 {
				 handler.OnConnected(client) ;
			 }
		}
	}

	@Override
	public boolean IsListening() {
		// TODO Auto-generated method stub
		return listening;
	}

	@Override
	public void UnRegisterHandler(HotSpotServerEventHandler handler) {
		
		eventHandlers.remove(handler);
		
	}

	@Override
	public void ClearHandler() {
		// TODO Auto-generated method stub
		eventHandlers.clear();
		
	}
	

}
