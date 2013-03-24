package com.makksi.vodafone;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.text.method.ScrollingMovementMethod;
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
	String phone="";
	List<List<String>> SwidgetList;

	// Si occupa di aggiornare il thread principale ossia la UI quando viene invocato ad esempio in un thread secondario	
	private final Handler handler = new Handler();
	final Runnable updateUI = new Runnable() {
		public void run() {
			//call the activity method that updates the UI
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
			text = (TextView) findViewById(R.id.textView1);
			text.setMovementMethod(new ScrollingMovementMethod());
			int ssize = preferences.getInt("service_size",0);
			for(int i=0;i<ssize;i++){
				for(int j=0;j<=3;j++){
					String service = preferences.getString("service"+"_"+i+"_"+j, "n/a");
					text.append(service+"\n");
			}
		}
	}		
			

		};	

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);		
		//		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		//		StrictMode.setThreadPolicy(policy); 
		setContentView(R.layout.activity_main);	
		preferences = PreferenceManager.getDefaultSharedPreferences(this);		
	}

	// run update UI at startup and  also if username,password,phone changed in Settings.Activity
	public void onStart(){
		super.onStart();
		new Thread(new Runnable() {
			@Override
			public void run() {
				httpreq();			
			}
		}).start();	
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
			Intent intentSettings = new Intent (this,SettingsActivity.class);
			this.startActivity(intentSettings);
			break;
		case R.id.menu_services:
			Intent intentService = new Intent (this,ServiceActivity.class);
			this.startActivity(intentService);
			break;			
		default:
			return super.onOptionsItemSelected(item);
		}
		return true;
	}        	

	public List<List<String>> readStream2(InputStream in) {
		BufferedReader reader = null;
		String line= "";
		String[] sstrings;
		List<String> lstrings = new ArrayList<String>();
		List<List<String>> plans = new ArrayList<List<String>>();
		try{
			reader = new BufferedReader(new InputStreamReader(in));
			while ((line = reader.readLine()) != null) {
				line=line.replace("{\"code\":0,\"result\":[", "");
				line=line.replace("]}", "");	
				line=line.replace("\"", "");
				line=line.replace(",{", "");
				line=line.replace("}", ";");
				line=line.replace("id:", "");
				line=line.replace("type:", "");
				line=line.replace("title:", "");
				line=line.replaceAll("[\\]\\{\\[]", "");
				sstrings=line.split(";");
				for(int i=0;i<sstrings.length;i++){
					String[] tstrings=sstrings[i].split(",");
					for(int j=0;j<tstrings.length;j++){
						lstrings.add(tstrings[j]);
					}
					plans.add(lstrings);	
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
		return plans;
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

	private class httpreq1 extends AsyncTask<Void, Void, Void> {

		protected Void doInBackground(Void... params) {
			try{
			}catch(Exception e){
				e.printStackTrace();
			} 				
			return null;
		}	
		protected void onProgressUpdate(Integer... progress) {
		}
		protected void onPostExecute(Long result) {
		}
	}	
	// retrieve data from web and send updateUI runnable to UI handler to update UI.
	public void httpreq(){
		URL url;
		HttpURLConnection con;
		username = preferences.getString("username", "n/a");
		password = preferences.getString("password", "n/a");	
		phone = preferences.getString("phone", "n/a");
		try{
			//---------------Widget List--------------------				
			url = new URL ("https://my190.vodafone.it/190mobile/endpoint/Android/ws_widget_list.php?installation_id=5309921AB593664EF5B066471A0991C0&bundle=3.2.1&user="+username+"&pass="+password+"&id=c_P_1221_2&msisdn="+phone);
			con = (HttpURLConnection) url.openConnection();
			SwidgetList=readStream2 (con.getInputStream());		
			//---------------Credit--------------------				
			url = new URL ("https://my190.vodafone.it/190mobile/endpoint/Android/wv_widget.php?installation_id=5309921AB593664EF5B066471A0991C0&bundle=3.2.1&user="+username+"&pass="+password+"&id=i_credito&msisdn="+phone);
			con = (HttpURLConnection) url.openConnection();
			Scredit=readStream1 (con.getInputStream(),"big red\">","&nbsp");
			//---------------200Plus--------------------				
			url = new URL ("https://my190.vodafone.it/190mobile/endpoint/Android/wv_widget.php?installation_id=5309921AB593664EF5B066471A0991C0&bundle=3.2.1&user="+username+"&pass="+password+"&id=c_P_1221_2&msisdn="+phone);
			con = (HttpURLConnection) url.openConnection();
			S200PlusUsed=readStream1 (con.getInputStream(),"Utilizzati <span class=\"red\">"," MIN");
			con = (HttpURLConnection) url.openConnection();						
			S200PlusFrom=readStream1 (con.getInputStream(),"Dal <span class=\"red\">","</span> al <span class");				
			con = (HttpURLConnection) url.openConnection();							
			S200PlusTo=readStream1 (con.getInputStream(),"</span> al <span class=\"red\">","</span>");				
			//---------------Internet 1 anno----------------------				
			url = new URL ("https://my190.vodafone.it/190mobile/endpoint/Android/wv_widget.php?installation_id=5309921AB593664EF5B066471A0991C0&bundle=3.2.1&user="+username+"&pass="+password+"&id=c_P_1256_2&msisdn="+phone);
			con = (HttpURLConnection) url.openConnection();
			SInternet1AnnoUsed=readStream1 (con.getInputStream(),"Utilizzati <span class=\"red\">"," MB");
			con = (HttpURLConnection) url.openConnection();						
			SInternet1AnnoFrom=readStream1 (con.getInputStream(),"Dal <span class=\"red\">","</span> al <span class");
			con = (HttpURLConnection) url.openConnection();						
			SInternet1AnnoTo=readStream1 (con.getInputStream(),"</span> al <span class=\"red\">","</span>");						
			handler.post(updateUI);				
		}catch(Exception e){
			e.printStackTrace();
		} 		
	}	
}

/*---------------------------------------------------------------------------------
				Toast toast2=Toast.makeText(this,"Start while",Toast.LENGTH_LONG);
				toast2.show();
------------------------------------------------------------------------------------*/