package com.makksi.vodafone;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ServiceActivity extends Activity implements OnClickListener{
	SharedPreferences preferences;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		CheckBox[] ch;
		int chId = 1000;
		setContentView(R.layout.service_settings);
		LinearLayout mainLayout = (LinearLayout)findViewById(R.id.LinearLayout1);
		preferences = PreferenceManager.getDefaultSharedPreferences(this);	
		int ssize = preferences.getInt("service_size",0);
		ch = new CheckBox[ssize];
		for(int i=0;i<ssize;i++){
			String service = preferences.getString("service"+"_"+i+"_2", "n/a");
			String serv_enabled = preferences.getString("service"+"_"+i+"_3", "n/a");
			TextView textView = new TextView(this);
			ch[i] = new CheckBox(this);
			ch[i].setId(chId++);
			ch[i].setOnClickListener(this);
			if (serv_enabled.contains("enabled")){
				ch[i].setChecked(true);
			}
			textView=setTextView(textView);
			textView.setText(service);
			mainLayout.addView(textView);
			mainLayout.addView(ch[i]);
		}
	}
	@Override
	public void onClick(View v) {
		Editor edit = preferences.edit();
		if ( ((CheckBox)v).isChecked()){	
			edit.putString("service"+"_"+(v.getId()-1000)+"_3", "enabled");
		}else{
			edit.putString("service"+"_"+(v.getId()-1000)+"_3", "disabled");
		}
		edit.commit();
	}



	private TextView setTextView (TextView textView){
		//		int textAppearance = android.R.attr.textAppearanceLarge;
		//		myTextView.setTextAppearance(context, textAppearance);
		textView.setTextAppearance(this, android.R.style.TextAppearance_Large);
		return textView;
	}



}
