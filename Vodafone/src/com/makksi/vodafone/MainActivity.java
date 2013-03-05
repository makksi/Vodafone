package com.makksi.vodafone;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	SharedPreferences preferences;
	String Scredit="";
	String S200PlusUsed="";
	String S200PlusFrom="";
	String S200PlusTo="";	
	String SInternet1AnnoUsed="";
	String SInternet1AnnoFrom="";
	String SInternet1AnnoTo="";
	String username="";
	String password="";
	// Si occupa di aggiornare il thread principale ossia la UI quando viene invocato ad esempio in un thread secondario	
	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			//			setContentView(R.layout.activity_main);
			TextView text = (TextView) findViewById(R.id.textCredit);  
			text.setText(Scredit);
			text = (TextView) findViewById(R.id.text200PlusUsed);
			text.setText(S200PlusUsed);
			text = (TextView) findViewById(R.id.text200PlusFrom);
			text.setText(S200PlusFrom);
			text = (TextView) findViewById(R.id.text200PlusTo);
			text.setText(S200PlusTo);	
			text = (TextView) findViewById(R.id.textInternet1AnnoUsed);
			text.setText(SInternet1AnnoUsed);
			text = (TextView) findViewById(R.id.textInternet1AnnoFrom);
			text.setText(SInternet1AnnoFrom);
			text = (TextView) findViewById(R.id.textInternet1AnnoTo);
			text.setText(SInternet1AnnoTo);	 
		}
	};
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		//		StrictMode.setThreadPolicy(policy); 
		setContentView(R.layout.activity_main);
		Button button1 = (Button) findViewById(R.id.button1);	
		preferences = PreferenceManager.getDefaultSharedPreferences(this);
		username = preferences.getString("username", "n/a");
		password = preferences.getString("password", "n/a");		
		httpreq.start();
		button1.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
//				username = preferences.getString("username", "n/a");
//				password = preferences.getString("password", "n/a");
				showPrefs(username, password);
			}	
		});	
	}

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.layout.menu, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
        case R.id.menu_settings:
            Intent intent = new Intent (this,SettingsActivity.class);
            this.startActivity(intent);
            break;
         default:
            return super.onOptionsItemSelected(item);
        }
        return true;
    }        
	

	private void showPrefs(String username, String password){
		Toast.makeText(MainActivity.this,"User: "+username+" password: "+password, Toast.LENGTH_LONG).show();
	}	

	private String readStream(InputStream in, String word, int start, int end) {
		BufferedReader reader = null;
		String line= "";
		int stCr = -1;
		try{
			reader = new BufferedReader(new InputStreamReader(in));
			while ((line = reader.readLine()) != null) {
				stCr = line.indexOf(word);
				if (stCr != -1) {
					break;
				}	
			}
		}catch(IOException e){
			e.printStackTrace();
		}finally{
			if (reader != null) {
				try{
					reader.close();
				}catch(IOException e){
					e.printStackTrace();
				}
			}
		}
		return line.substring(stCr+start, stCr+end);
	};	

	private String readStream1(InputStream in, String startString, String stopString) {
		BufferedReader reader = null;
		String line= "";
		String slineTemp= "";
		try{
			reader = new BufferedReader(new InputStreamReader(in));
			while ((line = reader.readLine()) != null) {
				if (line.contains(startString.subSequence(0, startString.length()))) {
					String[] sline=line.split(startString);
					slineTemp = sline[1];
					sline = slineTemp.split(stopString);
					slineTemp = sline[0];
					break;
				}	
			}
		}catch(IOException e){
			e.printStackTrace();
		}finally{
			if (reader != null) {
				try{
					reader.close();
				}catch(IOException e){
					e.printStackTrace();
				}
			}
		}
		return slineTemp;
	};		

	private Thread httpreq = new Thread(){
		// thread secondario; serve per fare in modo da non lockare il thread principale ossia la UI in operazioni particolarmente lunghe		
		public void run(){
			URL url;
			HttpURLConnection con;
			try{
				url = new URL ("https://my190.vodafone.it/190mobile/endpoint/Android/wv_widget.php?installation_id=5309921AB593664EF5B066471A0991C0&bundle=3.2.1&user="+username+"&pass="+password+"&id=i_credito&msisdn=3483734228");
				con = (HttpURLConnection) url.openConnection();
				Scredit=readStream1 (con.getInputStream(),"big red\">","&nbsp");
				//---------------200Plus--------------------				
				url = new URL ("https://my190.vodafone.it/190mobile/endpoint/Android/wv_widget.php?installation_id=5309921AB593664EF5B066471A0991C0&bundle=3.2.1&user="+username+"&pass="+password+"&id=c_P_1221_2&msisdn=3483734228");
				con = (HttpURLConnection) url.openConnection();
				S200PlusUsed=readStream1 (con.getInputStream(),"Utilizzati <span class=\"red\">"," MIN");
				con = (HttpURLConnection) url.openConnection();						
				S200PlusFrom=readStream1 (con.getInputStream(),"Dal <span class=\"red\">","</span> al <span class");				
				con = (HttpURLConnection) url.openConnection();							
				S200PlusTo=readStream1 (con.getInputStream(),"</span> al <span class=\"red\">","</span>");				
				//---------------Internet 1 anno----------------------				
				url = new URL ("https://my190.vodafone.it/190mobile/endpoint/Android/wv_widget.php?installation_id=5309921AB593664EF5B066471A0991C0&bundle=3.2.1&user="+username+"&pass="+password+"&id=c_P_1256_2&msisdn=3483734228");
				con = (HttpURLConnection) url.openConnection();
				SInternet1AnnoUsed=readStream1 (con.getInputStream(),"Utilizzati <span class=\"red\">"," MB");
				con = (HttpURLConnection) url.openConnection();						
				SInternet1AnnoFrom=readStream1 (con.getInputStream(),"Dal <span class=\"red\">","</span> al <span class");
				con = (HttpURLConnection) url.openConnection();						
				SInternet1AnnoTo=readStream1 (con.getInputStream(),"</span> al <span class=\"red\">","</span>");						
				handler.sendEmptyMessage(0);				
			}catch(Exception e){
				e.printStackTrace();
			} 		
		}
	};	
}	



/*---------------------------------------------------------------------------------
				Toast toast2=Toast.makeText(this,"Start while",Toast.LENGTH_LONG);
				toast2.show();
------------------------------------------------------------------------------------*/