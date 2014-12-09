package com.example.phoneconnect;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;


import android.content.Context;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


public class MainActivity extends ActionBarActivity {
	
 
    public String   s_gateway;  
    public String   s_ipAddress;    
    DhcpInfo d;
    WifiManager wifii;
    
	private Socket socket;
	private static final int SERVERPORT = 5566;
	private static String SERVER_IP = "";
	TextView wifiText;
	Handler updateConversationHandler;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        wifiText = (TextView)this.findViewById(R.id.wifiText);
        
        
        
        wifii= (WifiManager) getSystemService(Context.WIFI_SERVICE);
        d=wifii.getDhcpInfo();
        s_gateway="Default Gateway: "+intToIp(d.gateway);    
        s_ipAddress="IP Address: "+intToIp(d.ipAddress); 
        
        SERVER_IP = intToIp(d.gateway);

        //dispaly them
        wifiText.setText("\n"+s_ipAddress+"\n" +s_gateway);
        
        Button b = (Button)this.findViewById(R.id.button1);
       // b.on
        
        updateConversationHandler = new Handler();
        
        new Thread(new ClientThread()).start();
    }

    public String intToIp(int i) {

    	   return ((i) & 0xFF ) + "." +
    	               ((i >> 8 ) & 0xFF) + "." +
    	               ((i >> 16 ) & 0xFF) + "." +
    	               ( i >> 24 & 0xFF) ;
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
					updateConversationHandler.post(new updateUIThread(read));

				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	class updateUIThread implements Runnable {
		private String msg;

		public updateUIThread(String str) {
			this.msg = str;
		}

		@Override
		public void run() {
			 wifiText.setText(wifiText.getText()+"\n"+"Client Says: "+ msg);
		}
	}
    
    public void onClick(View view) {
		try {
			//EditText et = (EditText) findViewById(R.id.EditText01);
			String str = "test";
			PrintWriter out = new PrintWriter(new BufferedWriter(
					new OutputStreamWriter(socket.getOutputStream())),
					true);
			out.println(str);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
    
    class ClientThread implements Runnable {

		@Override
		public void run() {

			try {

				socket = new Socket(SERVER_IP, SERVERPORT);
				
				CommunicationThread commThread = new CommunicationThread(socket);
				new Thread(commThread).start();

			} catch (UnknownHostException e1) {
				e1.printStackTrace();
			} catch (IOException e1) {
				e1.printStackTrace();
			}

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
}
