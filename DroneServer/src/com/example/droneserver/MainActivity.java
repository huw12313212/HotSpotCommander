package com.example.droneserver;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
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
import android.widget.TextView;


public class MainActivity extends ActionBarActivity {

	
    public String   s_dns1 ;
    public String   s_dns2;     
    public String   s_gateway;  
    public String   s_ipAddress;    
    public String   s_leaseDuration;    
    public String   s_netmask;  
    public String   s_serverAddress;
    DhcpInfo d;
    WifiManager wifii;
    
    private ServerSocket serverSocket;
    Handler updateConversationHandler;
    Thread serverThread = null;
    
    public static final int SERVERPORT = 5566;

	
    TextView wifiText;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        wifiText = (TextView)this.findViewById(R.id.wifiText);
        
        wifii= (WifiManager) getSystemService(Context.WIFI_SERVICE);
        d=wifii.getDhcpInfo();

        s_dns1="DNS 1: "+intToIp(d.dns1);
        s_dns2="DNS 2: "+intToIp(d.dns2);    
        s_gateway="Default Gateway: "+intToIp(d.gateway);    
        s_ipAddress="IP Address: "+intToIp(d.ipAddress); 
        s_leaseDuration="Lease Time: "+String.valueOf(d.leaseDuration);     
        s_netmask="Subnet Mask: "+intToIp(d.netmask);    
        s_serverAddress="Server IP: "+intToIp(d.serverAddress);

        //dispaly them
        wifiText.setText("Network Info\n"+s_dns1+"\n"+s_dns2+"\n"+s_gateway+"\n"+s_ipAddress+"\n"+s_leaseDuration+"\n"+s_netmask+"\n"+s_serverAddress);
        
        
    	updateConversationHandler = new Handler();

		this.serverThread = new Thread(new ServerThread());
		this.serverThread.start();
    }

    public String intToIp(int i) {

 	   return ((i) & 0xFF ) + "." +
 	               ((i >> 8 ) & 0xFF) + "." +
 	               ((i >> 16 ) & 0xFF) + "." +
 	               ( i >> 24 & 0xFF) ;
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
		try {
			serverSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	class ServerThread implements Runnable {

		public void run() {
			Socket socket = null;
			try {
				serverSocket = new ServerSocket(SERVERPORT);
			} catch (IOException e) {
				e.printStackTrace();
			}
			while (!Thread.currentThread().isInterrupted()) {

				try {

					socket = serverSocket.accept();

					CommunicationThread commThread = new CommunicationThread(socket);
					new Thread(commThread).start();

				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	class CommunicationThread implements Runnable {

		private Socket clientSocket;

		private BufferedReader input;

		public CommunicationThread(Socket clientSocket) {

			socket = this.clientSocket = clientSocket;

			try {

				this.input = new BufferedReader(new InputStreamReader(this.clientSocket.getInputStream()));

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
	
	//The Last Connected Socket
	public Socket socket;
	
	  public void onClick(View view) {
			try {
				//EditText et = (EditText) findViewById(R.id.EditText01);
				String str = "fuck you";
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
}
