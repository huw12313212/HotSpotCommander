package com.example.phoneconnect;

import java.net.Socket;

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
import android.widget.TextView;


public class MainActivity extends ActionBarActivity {
	
  
	HotSpotClientInterface client;
	TextView wifiText;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        wifiText = (TextView)this.findViewById(R.id.wifiText);
        

        String ip = getAccessPointIP();
        wifiText.setText("\n Connect to"+ip);

        
        client = new HotSpotTCPClient();
        client.RegisterHandler(new HotSpotClientEventHandler(){

			@Override
			public void OnConnected(Socket server) {
				// TODO Auto-generated method stub
				
				wifiText.append("\n"+server.getInetAddress()+" server connected");
			}

			@Override
			public void OnDisconnected(Socket server) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void OnReceiveMessage(Socket server, String message) {
				// TODO Auto-generated method stub
				wifiText.append("\n"+server.getInetAddress()+":"+message);
			}});

        
        try {
			client.Connect(ip, 5566);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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

    
    public void onClick(View view) {

    	
    	if(client.IsConnected())
			client.SendMessage("hello world");
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
