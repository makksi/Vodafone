package com.makksi.vodafone;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class SettingsActivity extends Activity {
	SharedPreferences preferences;	
//	public static String userValue ="";
//	public static String passValue ="";
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);
		preferences = PreferenceManager.getDefaultSharedPreferences(this);		
		Button button = (Button) findViewById(R.id.buttonOk);
		button.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				EditText userEdit = (EditText) findViewById(R.id.editTextUser);
				EditText passEdit = (EditText) findViewById(R.id.editTextPass);				
				String userValue = userEdit.getText().toString();
				String passValue = passEdit.getText().toString();				
				updatePreferenceValue(userValue,passValue);
				finish();
			}	
		});
	}
	public void updatePreferenceValue(String user, String pass){
		Editor edit = preferences.edit();
		edit.putString("username", user);
		edit.putString("password", pass);
		edit.commit();
		String username = preferences.getString("username", "n/a");
		String password = preferences.getString("password", "n/a");
	} 		

}	