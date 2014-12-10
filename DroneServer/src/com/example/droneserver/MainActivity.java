package com.example.droneserver;

import java.net.Socket;

import HotSpotCommander.HotSpotServerEventHandler;
import HotSpotCommander.HotSpotServerInterface;
import HotSpotCommander.HotSpotTCPServer;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;


public class MainActivity extends ActionBarActivity {
	
    TextView wifiText;
    HotSpotServerInterface server;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_main);
        
        
        wifiText = (TextView)this.findViewById(R.id.wifiText);
        
        server = new HotSpotTCPServer();
        
        
        
        server.RegisterHandler(new HotSpotServerEventHandler(){

		@Override
		public void OnConnected(Socket client) {
			// TODO Auto-generated method stub
			wifiText.append("\n"+client.getInetAddress() +" Connected");
		}

		@Override
		public void OnDisconnected(Socket client) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void OnReceiveMessage(Socket client, String message) {
			// TODO Auto-generated method stub
			
			wifiText.append("\n"+client.getInetAddress() +":"+message);
			
		}});
        
			try {
				server.Start(5566);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
     
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

	
	public void onClick(View view) 
	{
		
		if(server.IsConnected())
		{
			server.SendMessage("wa ha ha");
		}
	}

	
}
