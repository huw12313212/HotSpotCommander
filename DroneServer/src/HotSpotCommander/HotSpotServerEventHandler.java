package HotSpotCommander;

import java.net.Socket;

public interface HotSpotServerEventHandler {

	public void OnConnected(Socket client);
	public void OnDisconnected(Socket client);
	
	public void OnReceiveMessage(Socket client,String message);
	
	
}
