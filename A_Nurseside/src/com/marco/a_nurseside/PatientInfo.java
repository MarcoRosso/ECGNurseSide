package com.marco.a_nurseside;


import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.GetListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;


import com.marco.constant.Filename;
import com.marco.constant.NurseUser;
import com.marco.constant.PatientIn;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;


public class PatientInfo extends Activity{
	private EditText newpatient_name;
	private EditText newpatient_password;
	private EditText newpatient_room;
	private EditText newpatient_bed;
	private EditText newpatient_ssid;
	private EditText changepatient_name;
	private EditText changepatient_password;
	private EditText changepatient_room;
	private EditText changepatient_bed;
	private EditText changepatient_ssid;
	private String patientID;
    private String deletename;
    private String deletepassword;
	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.patientifo_layout);
        getActionBar().hide();
        newpatient_name=(EditText)findViewById(R.id.newpatient_name);
        newpatient_password=(EditText)findViewById(R.id.newpatient_password);
        newpatient_room=(EditText)findViewById(R.id.newpatient_room);
        newpatient_bed=(EditText)findViewById(R.id.newpatient_bed);
        newpatient_ssid=(EditText)findViewById(R.id.newpatient_ssid);
        changepatient_name=(EditText)findViewById(R.id.changepatient_name);
        changepatient_password=(EditText)findViewById(R.id.changepatient_password);
        changepatient_room=(EditText)findViewById(R.id.changepatient_room);
        changepatient_bed=(EditText)findViewById(R.id.changepatient_bed);
        changepatient_ssid=(EditText)findViewById(R.id.changepatient_ssid);
        changepatient_name.setEnabled(false);
        changepatient_password.setEnabled(false);
        changepatient_room.setEnabled(false);
        changepatient_bed.setEnabled(false);
        changepatient_ssid.setEnabled(false);
        
	}
	public void newpatientconfirm(View view){
		if(newpatient_name.getText().toString().equals("")||
			newpatient_password.getText().toString().equals("")||
			  newpatient_room.getText().toString().equals("")||
			  newpatient_bed.getText().toString().equals("")){
			Toast.makeText(PatientInfo.this, "请将病人信息填写完整！", Toast.LENGTH_SHORT).show();
			return;
		}
		NurseUser nurseuser=new NurseUser();
		nurseuser.setUsername(newpatient_name.getText().toString());
		nurseuser.setPassword(newpatient_password.getText().toString());
		nurseuser.setRoomnumber(newpatient_room.getText().toString()+"房"
		                           +newpatient_bed.getText().toString()+"床");
		nurseuser.setInUse(true);
		nurseuser.setNurse(true);
	    nurseuser.setSsid(newpatient_ssid.getText().toString());
		nurseuser.signUp(this, new SaveListener() {
		    @Override
		    public void onSuccess() {
		        // TODO Auto-generated method stub
		        PatientIn patientin = new PatientIn();
		        patientin.setPatientName(newpatient_name.getText().toString());
		        patientin.setPatientPassword(newpatient_password.getText().toString());
		        patientin.save(getApplicationContext(), new SaveListener() {
		            @Override
		            public void onSuccess() {
		                // TODO Auto-generated method stub
		            	 Toast.makeText(PatientInfo.this, "病人信息添加成功！", Toast.LENGTH_SHORT).show();
		            }

		            @Override
		            public void onFailure(int code, String arg0) {
		                // TODO Auto-generated method stub
				    	Toast.makeText(PatientInfo.this, "注册失败:"+arg0, Toast.LENGTH_SHORT).show();
		            }
		        });
		    }
		    @Override
		    public void onFailure(int code, String msg) {
		        // TODO Auto-generated method stub
		    	Toast.makeText(PatientInfo.this, "注册失败:"+msg, Toast.LENGTH_SHORT).show();
		    }
		});
	}
	public void changpatient_choose(View view){
		BmobQuery<NurseUser> query = new BmobQuery<NurseUser>();
		query.addWhereEqualTo("Nurse", true);
		query.addWhereEqualTo("inUse", true);
		query.findObjects(this, new FindListener<NurseUser>() {
		    @Override
		    public void onSuccess(List<NurseUser> object) {
		        // TODO Auto-generated method stub
            	final String[] patientname= new String[object.size()];
            	final String[] patientroomnumber= new String[object.size()]; 
            	final String[] patientssid= new String[object.size()]; 
            	final String[] patientid=new String[object.size()];
            	int i=0;
	            for (NurseUser nurseuser : object) {
	            	patientname[i]=nurseuser.getUsername();
	            	patientroomnumber[i]=nurseuser.getRoomnumber();
	            	patientssid[i]=nurseuser.getSsid();
	            	patientid[i]=nurseuser.getObjectId();
	            	i++;
	                Log.d("bmob", "UserName:"+nurseuser.getUsername());
	            }
	            Dialog alertDialog = new android.app.AlertDialog.Builder(PatientInfo.this). 
                	    setTitle("需要编辑哪个病人信息？"). 
                	    setIcon(R.drawable.ic_launcher) 
                	    .setItems(patientname, new DialogInterface.OnClickListener() { 
                	     @Override 
                	     public void onClick(DialogInterface dialog, int which) { 
                	    	 changepatient_name.setText(patientname[which]);
                	    	 changepatient_ssid.setText(patientssid[which]);
                	    	 String temp[]=patientroomnumber[which].split("房");
                	    	 changepatient_room.setText(temp[0]);
                	    	 String temp2[]=temp[1].split("床");
                	    	 changepatient_bed.setText(temp2[0]);
                	         changepatient_room.setEnabled(true);
                	         changepatient_bed.setEnabled(true);
                	         changepatient_ssid.setEnabled(true);
                	         BmobQuery<PatientIn> query = new BmobQuery<PatientIn>();
                	         query.addWhereEqualTo("PatientName", patientname[which]);
                	         query.findObjects(PatientInfo.this, new FindListener<PatientIn>() {
								@Override
								public void onError(int arg0, String arg1) {
									// TODO Auto-generated method stub	
							    Toast.makeText(PatientInfo.this, "获取用户信息错误"+arg0+arg1, Toast.LENGTH_SHORT).show();
								}
								@Override
								public void onSuccess(List<PatientIn> arg0) {
									// TODO Auto-generated method stub
									for(PatientIn patientin:arg0){
						           changepatient_password.setText(patientin.getPatientPassword());
						           patientID=patientin.getObjectId();
									}
								   changepatient_password.setEnabled(true);
							    	final BmobUser user= new BmobUser();
							    	user.setUsername(changepatient_name.getText().toString());
							    	user.setPassword(changepatient_password.getText().toString());
							    	user.login(PatientInfo.this, new SaveListener(){
										@Override
										public void onFailure(int arg0,
												String arg1) {
											// TODO Auto-generated method stub
											Toast.makeText(PatientInfo.this, "获取用户信息错误"+arg0+arg1, Toast.LENGTH_SHORT).show();	
										}
										@Override
										public void onSuccess() {
											// TODO Auto-generated method stub
											Toast.makeText(PatientInfo.this, "获取用户信息成功!", Toast.LENGTH_SHORT).show();
										}							    		
							    	});
								}

                	         });
                	     } 
                	    }). 
                	    setNegativeButton("取消", new DialogInterface.OnClickListener() {

                	     @Override 
                	     public void onClick(DialogInterface dialog, int which) { 
                	      // TODO Auto-generated method stub
                	    	dialog.dismiss();
                	     } 
                	    }). 
                	    create(); 
                alertDialog.show();
		    }
		    @Override
		    public void onError(int code, String msg) {
		        // TODO Auto-generated method stub
		    	Toast.makeText(PatientInfo.this, "查询用户失败："+code+msg, Toast.LENGTH_SHORT).show();
		    }
		});
	}
	public void changepatientconfirm(View view){
		if(changepatient_name.getText().toString().equals("")||
				changepatient_password.getText().toString().equals("")||
				  changepatient_room.getText().toString().equals("")||
				  changepatient_bed.getText().toString().equals("")){
				Toast.makeText(PatientInfo.this, "请将病人信息填写完整！", Toast.LENGTH_SHORT).show();
				return;
			}
		NurseUser nurseuser = new NurseUser();
		nurseuser.setRoomnumber(changepatient_room.getText().toString()+"房"
		                           +changepatient_bed.getText().toString()+"床");
			nurseuser.setSsid(changepatient_ssid.getText().toString());
			nurseuser.setPassword(changepatient_password.getText().toString());
		nurseuser.setInUse(true);
		nurseuser.setNurse(true);
		BmobUser bmobUser = BmobUser.getCurrentUser(this);
		nurseuser.update(this,bmobUser.getObjectId(),new UpdateListener() {
		    @Override
		    public void onSuccess() {
		        // TODO Auto-generated method stub
		    	PatientIn patientin = new PatientIn();
		    	patientin.setPatientPassword(changepatient_password.getText().toString());
		    	patientin.update(PatientInfo.this, patientID, new UpdateListener() {

		    	    @Override
		    	    public void onSuccess() {
		    	        // TODO Auto-generated method stub
		    	    	Toast.makeText(PatientInfo.this, "更新用户信息成功!", Toast.LENGTH_SHORT).show();
		    	    }

		    	    @Override
		    	    public void onFailure(int code, String msg) {
		    	        // TODO Auto-generated method stub
		    	    	 Toast.makeText(PatientInfo.this, "更新用户信息失败:"+code+msg, Toast.LENGTH_SHORT).show();
		    	    }
		    	});

		    }
		    @Override
		    public void onFailure(int code, String msg) {
		        // TODO Auto-generated method stub
		        Toast.makeText(PatientInfo.this, "更新用户信息失败:" +code+msg, Toast.LENGTH_SHORT).show();
		    }
		});
	}
	public void deletepatient_choose(View view){
		BmobQuery<NurseUser> query = new BmobQuery<NurseUser>();
		query.addWhereEqualTo("Nurse", true);
		query.addWhereEqualTo("inUse", true);
		query.findObjects(this, new FindListener<NurseUser>() {
		    @Override
		    public void onSuccess(List<NurseUser> object) {
		        // TODO Auto-generated method stub
            	final String[] patientname= new String[object.size()];
            	int i=0;
	            for (NurseUser nurseuser : object) {
	            	patientname[i]=nurseuser.getUsername();
	            	i++;
	                Log.d("bmob", "UserName:"+nurseuser.getUsername());
	            }
	            Dialog alertDialog = new android.app.AlertDialog.Builder(PatientInfo.this). 
                	    setTitle("需要删除哪个病人信息？"). 
                	    setIcon(R.drawable.ic_launcher) 
                	    .setItems(patientname, new DialogInterface.OnClickListener() { 
                	     @Override 
                	     public void onClick(DialogInterface dialog, int which) { 
                	         BmobQuery<PatientIn> query = new BmobQuery<PatientIn>();
                	         query.addWhereEqualTo("PatientName", patientname[which]);
                	         deletename=patientname[which];
                	         query.findObjects(PatientInfo.this, new FindListener<PatientIn>() {
								@Override
								public void onError(int arg0, String arg1) {
									// TODO Auto-generated method stub	
							    Toast.makeText(PatientInfo.this, "获取用户信息错误"+arg0+arg1, Toast.LENGTH_SHORT).show();
								}
								@Override
								public void onSuccess(List<PatientIn> arg0) {
									// TODO Auto-generated method stub
									for(PatientIn patientin:arg0){
								   deletepassword=patientin.getPatientPassword();
									}
							    	final BmobUser user= new BmobUser();
							    	user.setUsername(deletename);
							    	user.setPassword(deletepassword);
							    	user.login(PatientInfo.this, new SaveListener(){
										@Override
										public void onFailure(int arg0,
												String arg1) {
											// TODO Auto-generated method stub
											Toast.makeText(PatientInfo.this, "获取用户信息错误"+arg0+arg1, Toast.LENGTH_SHORT).show();					
										}
										@Override
										public void onSuccess() {
											// TODO Auto-generated method stub
											Toast.makeText(PatientInfo.this, "获取用户信息成功!", Toast.LENGTH_SHORT).show();										
											showconfirmdialog();
										}							    		
							    	});
								}

                	         });
                	     } 
                	    }). 
                	    setNegativeButton("取消", new DialogInterface.OnClickListener() {

                	     @Override 
                	     public void onClick(DialogInterface dialog, int which) { 
                	      // TODO Auto-generated method stub
                	    	dialog.dismiss();
                	     } 
                	    }). 
                	    create(); 
                alertDialog.show();
		    }
		    @Override
		    public void onError(int code, String msg) {
		        // TODO Auto-generated method stub
		    	Toast.makeText(PatientInfo.this, "查询用户失败："+msg, Toast.LENGTH_SHORT).show();
		    }
		});
	}
	public void showconfirmdialog(){
		final BmobUser bmobUser = BmobUser.getCurrentUser(this);
				  Dialog alertDialog = new AlertDialog.Builder(this). 
				            setTitle("提示").
				            setMessage("你确定要删除"+deletename+"的病人信息吗?").
				            	setPositiveButton("确定", new DialogInterface.OnClickListener() { 
				                @Override 
				                public void onClick(DialogInterface dialog, int which) { 
				                	NurseUser nurseuser = new NurseUser();
				                	nurseuser.setInUse(false);
									nurseuser.update(PatientInfo.this,bmobUser.getObjectId(),new UpdateListener() {
									    @Override
									    public void onSuccess() {
									        // TODO Auto-generated method stub
									    	 Toast.makeText(PatientInfo.this, "删除用户成功！", Toast.LENGTH_SHORT).show();
									    }
									    @Override
									    public void onFailure(int code, String msg) {
									        // TODO Auto-generated method stub
									        Toast.makeText(PatientInfo.this, "删除用户失败:" + msg, Toast.LENGTH_SHORT).show();
									    }
									});
				                } 
				            }).setNegativeButton("取消", new DialogInterface.OnClickListener() {								
								@Override
								public void onClick(DialogInterface arg0, int arg1) {
									// TODO Auto-generated method stub									
								}
							}).create();
							alertDialog.show();
	}
}
