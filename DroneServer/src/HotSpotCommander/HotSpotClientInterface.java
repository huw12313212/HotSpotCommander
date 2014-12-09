package HotSpotCommander;

public interface HotSpotClientInterface {

	public void Connect(String ip,int port);
	public void Disconnect();
	
	public void RegisterHandler(HotSpotClientEventHandler handler);
	public void SendMessage(String message);
	
	public boolean IsConnected();
	
}
