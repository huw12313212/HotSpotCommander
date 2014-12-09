package HotSpotCommander;

import java.net.Socket;

public interface HotSpotClientEventHandler {
	
	public void OnConnected(Socket server);
	public void OnDisconnected(Socket server);
	
	public void OnReceiveMessage(Socket server,String message);
	

}
