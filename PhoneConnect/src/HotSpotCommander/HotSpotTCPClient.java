package HotSpotCommander;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import android.os.Handler;
import android.os.Looper;

public class HotSpotTCPClient implements HotSpotClientInterface {

    int serverPort = 5566;
    Handler uiHandler;
    Socket clientSocket;
    String serverIP;
    PrintWriter pw;
    
    Thread communicationThread;
    Thread clientTrhead;
    List<HotSpotClientEventHandler> eventHandlers = new ArrayList<HotSpotClientEventHandler>();
    
    
    boolean isConnected = false;
	
	@Override
	public void Connect(String ip, int port) throws Exception {
		// TODO Auto-generated method stub
		
		if(isConnected)throw new Exception("is already connected ?");
		ClearAllState();
		
		isConnected = true;
		serverIP = ip;
		serverPort = port;
		
		uiHandler = new Handler(Looper.getMainLooper());	
		
		clientTrhead = new Thread(new ClientThread());
		clientTrhead.start();
		
	}
	
	
    class ClientThread implements Runnable {

		@Override
		public void run() {

			try {

				clientSocket = new Socket(serverIP, serverPort);
				pw = new PrintWriter(new BufferedWriter(
						new OutputStreamWriter(clientSocket.getOutputStream())),
						true);
				
				
				uiHandler.post(new connectionRunnable(clientSocket));
				
				
				CommunicationThread commThread = new CommunicationThread(clientSocket);
				communicationThread = new Thread(commThread);
				communicationThread.run();
				
				

			} catch (UnknownHostException e1) {
				e1.printStackTrace();
			} catch (IOException e1) {
				e1.printStackTrace();
			}

		}
	}
    
	class CommunicationThread implements Runnable {

		private Socket serverSocket;
		private BufferedReader input;
		public CommunicationThread(Socket clientSocket) {

			this.serverSocket = clientSocket;

			try {

				this.input = new BufferedReader(new InputStreamReader(this.serverSocket.getInputStream()));

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		public void run() {

			while (!Thread.currentThread().isInterrupted()) {

				try {
					String read = input.readLine();
					uiHandler.post(new receiveMessageRunnable(read));

				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	
	class receiveMessageRunnable implements Runnable 
	{
		private String msg;
		
		public receiveMessageRunnable(String str) {
			this.msg = str;
		}

		@Override
		public void run() {
			for(HotSpotClientEventHandler e : eventHandlers)
			{
				e.OnReceiveMessage(clientSocket, msg);
			}
		}
	}
	
	class connectionRunnable implements Runnable 
	{
		private Socket server;
		
		public connectionRunnable(Socket socket) {
			this.server = socket;
		}

		@Override
		public void run() {
			for(HotSpotClientEventHandler e : eventHandlers)
			{
				e.OnConnected(server);
			}
			
		}
	}
	
	
	private void ClearAllState()
	{
		if(clientSocket!=null)
		{
			try {
				clientSocket.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			clientSocket = null;
		}
		
		if(communicationThread!=null)
		{
			communicationThread.interrupt();
			communicationThread = null;
		}
		
		if(pw!=null)
		{
			pw.close();
			pw = null;
		}
		
		if(clientTrhead!=null)
		{
			clientTrhead.interrupt();
			clientTrhead = null;
		}
		
		uiHandler = null;
	}

	@Override
	public void Disconnect() {
		// TODO Auto-generated method stub
		
		ClearAllState();
		
		isConnected = false;
		
	}

	@Override
	public void RegisterHandler(HotSpotClientEventHandler handler) {
		// TODO Auto-generated method stub
		eventHandlers.add(handler);
	}

	@Override
	public void SendMessage(String message) {
		// TODO Auto-generated method stub
		pw.println(message);
	}

	@Override
	public boolean IsConnected() {
		// TODO Auto-generated method stub
		
		if(clientSocket==null)return false;
		
		return clientSocket.isConnected();
	}

	@Override
	public void UnRegisterHandler(HotSpotClientEventHandler handler) {
		// TODO Auto-generated method stub
		eventHandlers.remove(handler);
	}

	@Override
	public void ClearHandler() {
		// TODO Auto-generated method stub
		eventHandlers.clear();
	}


}
