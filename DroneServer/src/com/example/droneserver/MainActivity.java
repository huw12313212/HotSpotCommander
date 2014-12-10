package com.example.droneserver;

import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import HotSpotCommander.HotSpotServerEventHandler;
import HotSpotCommander.HotSpotServerInterface;
import HotSpotCommander.HotSpotTCPServer;
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
	
    TextView wifiText;
    TextView eventsText;
    Button buttonStart;
    Button buttonDisconnect;
    Button buttonSend;
    EditText editText;
    
    HotSpotServerInterface server;
    
    final int MAX_EVENT_COUNT = 10;
    List<String> events = new ArrayList<String>();
    
    int eventCount = 0;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_main);
        
        wifiText = (TextView)this.findViewById(R.id.textWifi);
        eventsText = (TextView)this.findViewById(R.id.eventsText);
        buttonStart = (Button)this.findViewById(R.id.buttonStart);
        buttonDisconnect = (Button)this.findViewById(R.id.buttonDisconnect);
        buttonSend = (Button)this.findViewById(R.id.buttonSend);
        editText = (EditText)this.findViewById(R.id.editText);
        
        server = new HotSpotTCPServer();
        
        updateView();
        
        
        server.RegisterHandler(new HotSpotServerEventHandler(){

		@Override
		public void OnConnected(Socket client) {
			// TODO Auto-generated method stub
			addEvent(client.getInetAddress() +" Connected");
		}

		@Override
		public void OnDisconnected(Socket client) {
			// TODO Auto-generated method stub
			addEvent(client.getInetAddress() +" Disconnected");
		}

		@Override
		public void OnReceiveMessage(Socket client, String message) {
			// TODO Auto-generated method stub
			
			addEvent(client.getInetAddress() +":"+message);
			
		}});
        

     
    }
    
    private void updateView()
    {
    	String eventStr = "";
    	
    	for(String s : events)
    	{
    		eventStr+=s+"\n";
    	}
    	
    	eventsText.setText(eventStr);
    	
    	String wifiState;
    	wifiState = "Listening:"+server.IsListening() + ", Clients:"+server.GetConnectedClients().size();
    	
    	wifiText.setText(wifiState);
    	
    	buttonStart.setEnabled(!server.IsListening());
    	buttonSend.setEnabled(server.IsConnected());
    	buttonDisconnect.setEnabled(server.IsListening());
    }
    
    private void addEvent(String message)
    {
    	events.add(eventCount+++":"+message);
    	if(events.size()>this.MAX_EVENT_COUNT)events.remove(0);
    	
    	updateView();
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
    

	@Override
	protected void onStop() {
		super.onStop();
		
		server.Stop();
		
	}

	public void onDisconnectClick(View view)
	{
		server.Stop();
		addEvent("Server Stop");
	}
	
	public void onStartClick(View view)
	{
		try {
			server.Start(5566);
			addEvent("Server Start at port 5566");
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void onSendClick(View view) 
	{
		String data = editText.getText().toString();
		if(server.IsConnected())
		{
			server.SendMessage(data);
		}
	}

	
}
