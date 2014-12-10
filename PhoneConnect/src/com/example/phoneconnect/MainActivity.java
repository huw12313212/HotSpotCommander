package com.example.phoneconnect;

import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import HotSpotCommander.HotSpotClientEventHandler;
import HotSpotCommander.HotSpotClientInterface;
import HotSpotCommander.HotSpotTCPClient;
import android.content.Context;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


public class MainActivity extends ActionBarActivity {
	
  
	HotSpotClientInterface client;
	TextView wifiText;
	TextView textEvents;
	Button connectButton;
	Button disconnectButton;
	Button sendButton;
	EditText editText;
	
	final int EVENT_COUNT = 10;
	List<String> eventList = new ArrayList<String>();
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_main);
        
        wifiText = (TextView)this.findViewById(R.id.textWifi);
        textEvents = (TextView)this.findViewById(R.id.textEvent);
        connectButton = (Button)this.findViewById(R.id.buttonConnect);
        disconnectButton = (Button)this.findViewById(R.id.buttonDisconnect);
        sendButton = (Button)this.findViewById(R.id.buttonSend);
        editText = (EditText)this.findViewById(R.id.editText);
        

        String ip = getAccessPointIP();
        wifiText.setText("\n Connect to"+ip);

        
        client = new HotSpotTCPClient();
        client.RegisterHandler(new HotSpotClientEventHandler(){

			@Override
			public void OnConnected(Socket server) {
				// TODO Auto-generated method stub
				
				addEvent(server.getInetAddress()+" Connected");
			}

			@Override
			public void OnDisconnected(Socket server) {
				// TODO Auto-generated method stub

				addEvent(server.getInetAddress()+" Disconnected");
			}

			@Override
			public void OnReceiveMessage(Socket server, String message) {
				// TODO Auto-generated method stub
				
				addEvent(server.getInetAddress()+":"+message);
			}});
        
    	UpdateView();

    }
    
    int eventCounter = 0;
    
    private void addEvent(String str)
    {
    	eventList.add(eventCounter+++":"+str);
    	if(eventList.size()> EVENT_COUNT)eventList.remove(0);
    	
    	UpdateView();
    }
    
    private void UpdateView()
    {
    	String eventText = "";
    	
    	for(String s : eventList)
    	{
    		eventText += s+"\n";
    	}
    	
    	this.wifiText.setText("Connected:"+this.client.IsConnected());
    	this.textEvents.setText(eventText);
    	
    	this.connectButton.setEnabled(!this.client.IsConnecting());
    	this.disconnectButton.setEnabled(this.client.IsConnecting());
    	this.sendButton.setEnabled(this.client.IsConnected());
    	
    }
    
    
    private String getAccessPointIP()
    {
        DhcpInfo d;
        WifiManager wifii;
        
        wifii= (WifiManager) getSystemService(Context.WIFI_SERVICE);
        d=wifii.getDhcpInfo();
        String ip = intToIp(d.gateway);
        
        return ip;
    }

    public String intToIp(int i) {

    	   return ((i) & 0xFF ) + "." +
    	               ((i >> 8 ) & 0xFF) + "." +
    	               ((i >> 16 ) & 0xFF) + "." +
    	               ( i >> 24 & 0xFF) ;
    	}

    
    public void onSendClick(View view) 
    {	
    	if(client.IsConnected())
    	{
			client.SendMessage(editText.getText().toString());
    	}
    	
    	UpdateView();
	}
    
    public void onConnectClick(View view) {

    	if(!client.IsConnected())
    	{
    	try {
			client.Connect(getAccessPointIP(),5566);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	}
    	

    	UpdateView();
	}
    
    public void onDisconnectClick(View view) {

    	if(client.IsConnecting()||client.IsConnected())
    	{
			client.Disconnect();
    	}
    	

    	UpdateView();
	}
    
	@Override
	protected void onStop() {
		super.onStop();
		
		client.Disconnect();
		addEvent("Client Disconnected");
		
	}
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
