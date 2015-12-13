package com.marco.a_nurseside;



 



import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class ContactActivity extends Activity{
    private TextView welcome_name;
    private TextView welcome_roomnumber;
    private String name;
	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contact_layout);
        getActionBar().hide(); 
        welcome_name=(TextView)findViewById(R.id.welcome_name);
        welcome_roomnumber=(TextView)findViewById(R.id.welcome_roomnumber);
        Intent intent=getIntent();
        name=intent.getStringExtra("username");
        String roomnumber=intent.getStringExtra("roomnumber");
        welcome_name.setText(name+",欢迎使用心电监测与报警系统");
        welcome_roomnumber.setText("你管理病房号是："+roomnumber);
	}
	 public void menuone(View view){
		    Intent intent= new Intent();
			intent.putExtra("username", name);
			intent.setClass(ContactActivity.this, OffLineRead.class);
			startActivity(intent);
	 }
	 public void menufour(View view){
		    Intent intent= new Intent();
			intent.putExtra("username", name);
			intent.setClass(ContactActivity.this, OtherFunction.class);
			startActivity(intent);
	 }
	 public void menuthree(View view){
		    Intent intent= new Intent();
			intent.putExtra("username", name);
			intent.setClass(ContactActivity.this, PatientInfo.class);
			startActivity(intent);
	 }
	 public void menutwo(View view){
		    Intent intent= new Intent();
			intent.putExtra("username", name);
			intent.setClass(ContactActivity.this, OnLineRead.class);
			startActivity(intent);
	 }

}
