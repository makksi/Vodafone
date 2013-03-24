package com.makksi.vodafone;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;


public class SettingsActivity extends Activity {
	SharedPreferences preferences;	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);
		preferences = PreferenceManager.getDefaultSharedPreferences(this);		
		Button button = (Button) findViewById(R.id.buttonOk);
		String username = preferences.getString("username", "n/a");
		String password = preferences.getString("password", "n/a");	
		String phone = preferences.getString("phone", "n/a");			
		EditText userEdit = (EditText) findViewById(R.id.editTextUser);
		EditText passEdit = (EditText) findViewById(R.id.editTextPass);	
		EditText phoneEdit = (EditText) findViewById(R.id.editTextPhone);			
		userEdit.setText(username);
		passEdit.setText(password);
		phoneEdit.setText(phone);

		button.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				EditText userEdit = (EditText) findViewById(R.id.editTextUser);
				EditText passEdit = (EditText) findViewById(R.id.editTextPass);		
				EditText phoneEdit = (EditText) findViewById(R.id.editTextPhone);					
				String userValue = userEdit.getText().toString();
				String passValue = passEdit.getText().toString();	
				String phoneValue = phoneEdit.getText().toString();	
				updatePreferenceValue(userValue,passValue,phoneValue);
				finish();
			}	
		});
	}

	public void onStart(){
		super.onStart();
		new Thread(new Runnable() {
			@Override
			public void run() {
				httpreq();			
			}
		}).start();	
	}

	public void updatePreferenceValue(String user, String pass, String phone){
		Editor edit = preferences.edit();
		edit.putString("username", user);
		edit.putString("password", pass);
		edit.putString("phone", phone);		
		edit.commit();

	} 	

	public List<String[]> readStream2(InputStream in) {
		BufferedReader reader = null;
		String line= "";
		String[] sstrings;
		List<String> lstrings = new ArrayList<String>();
		List<String[]> plans = new ArrayList<String[]>();
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
					//					for(int j=0;j<tstrings.length;j++){
					//						lstrings.add(tstrings[j]);
					//					}
					//					plans.add(lstrings);	
					plans.add(tstrings);						
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

	public void httpreq(){
		URL url;
		HttpURLConnection con;
		List<String[]> SwidgetList=null;
		String username = preferences.getString("username", "n/a");
		String password = preferences.getString("password", "n/a");	
		String phone = preferences.getString("phone", "n/a");
		try{
			//---------------Widget List--------------------				
			url = new URL ("https://my190.vodafone.it/190mobile/endpoint/Android/ws_widget_list.php?installation_id=5309921AB593664EF5B066471A0991C0&bundle=3.2.1&user="+username+"&pass="+password+"&id=c_P_1221_2&msisdn="+phone);
			con = (HttpURLConnection) url.openConnection();
			SwidgetList=readStream2 (con.getInputStream());				
		}catch(Exception e){
			e.printStackTrace();
		}
		for(int i=0;i<SwidgetList.size();i++){
			for(int j=0;j<=3;j++){
				Editor edit = preferences.edit();
				if (j==3){
					edit.putString("service"+"_"+i+"_"+j, "enabled");	
				}else{
					edit.putString("service"+"_"+i+"_"+j, SwidgetList.get(i)[j].toString());
				}
				edit.commit();
			}
			Editor edit = preferences.edit();
			edit.putInt("service_size",SwidgetList.size());
			edit.commit();
		}	
	}
}	