package com.tim.servercommunicator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends Activity {
	
	private static String messageToSendToServer;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);	

		kickOffMessageReceiver();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	/** Called when the user clicks the Send button */
	public void sendMessage(View view) {
	    // Do something in response to button
		//Intent intent = new Intent(this, DisplayMessageActivity.class);
		EditText editText = (EditText) findViewById(R.id.editText1);
		messageToSendToServer = editText.getText().toString();

		new Thread(new SendMessageToServerTask()).start();
		
	}
	
	private void kickOffMessageReceiver() {
		new ReceiveMessageFromServerTask().execute((String[])null);
	}
	
	class SendMessageToServerTask implements Runnable {

		@Override
		public void run() {
			try {

				Socket socket = new Socket("192.168.1.103", 4444);
				PrintWriter pw = new PrintWriter(socket.getOutputStream());
				
				pw.write(messageToSendToServer);
				pw.flush();
				pw.close();
				socket.close();
				
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}			
		}
		
	}
	



	class ReceiveMessageFromServerTask extends AsyncTask<String, Void, String> {

		
		@Override
		protected String doInBackground(String... params) {
			String message = "";
			try {
				ServerSocket serverSocket = new ServerSocket(15555);

				Socket client = serverSocket.accept();
				
				InputStreamReader inReader = new InputStreamReader(client.getInputStream());
				BufferedReader buffReader = new BufferedReader(inReader);
				
				message = buffReader.readLine();
				
				buffReader.close();
				inReader.close();
				client.close();
				serverSocket.close();  
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return message;
		}
		
		@Override
		protected void onPostExecute(String params) {
			
			TextView text = (TextView) findViewById(R.id.textView2);
			text.setText(params);
			
			kickOffMessageReceiver();
			
		}
	}
}
