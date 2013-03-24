package com.makksi.vodafone;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ServiceActivity extends Activity {
	SharedPreferences preferences;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.service_settings);
		LinearLayout mainLayout = (LinearLayout)findViewById(R.id.LinearLayout1);
		preferences = PreferenceManager.getDefaultSharedPreferences(this);	
		int ssize = preferences.getInt("service_size",0);
		for(int i=0;i<ssize;i++){
			String service = preferences.getString("service"+"_"+i+"_2", "n/a");
			TextView textView = new TextView(this);
			textView=setTextView(textView);
			textView.setText(service);
			mainLayout.addView(textView);
		}
	}
	
	private TextView setTextView (TextView textView){
//		int textAppearance = android.R.attr.textAppearanceLarge;
//		myTextView.setTextAppearance(context, textAppearance);
		textView.setTextAppearance(this, android.R.style.TextAppearance_Large);
		return textView;
	}
}

