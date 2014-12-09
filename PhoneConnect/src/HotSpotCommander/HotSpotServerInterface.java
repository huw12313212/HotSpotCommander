package HotSpotCommander;

import java.net.Socket;
import java.util.List;

public interface HotSpotServerInterface {
	
	public void Start(int port) throws Exception;
	public void Stop();
	
	public void RegisterHandler(HotSpotServerEventHandler handler);
	public void UnRegisterHandler(HotSpotServerEventHandler handler);
	public void ClearHandler();
	
	public List<Socket> GetConnectedClients();
	
	public void BroadcastMessage(String message);
	public void SendMessage(String message);
	
	public boolean IsConnected();
	public boolean IsListening();
	
}
