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
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


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

