package HotSpotCommander;

public interface HotSpotClientInterface {

	public void Connect(String ip,int port) throws Exception;
	public void Disconnect();
	
	public void RegisterHandler(HotSpotClientEventHandler handler);
	public void UnRegisterHandler(HotSpotClientEventHandler handler);
	public void ClearHandler();
	
	
	public void SendMessage(String message);
	
	public boolean IsConnected();
	public boolean IsConnecting();
	
}
