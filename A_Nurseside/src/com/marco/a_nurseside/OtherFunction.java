package com.marco.a_nurseside;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.datatype.BmobRelation;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

import com.bmob.BTPFileResponse;
import com.bmob.BmobProFile;
import com.bmob.btp.callback.DownloadListener;
import com.bmob.btp.callback.UploadListener;
import com.marco.constant.Filename;
import com.marco.constant.NurseUser;
import com.marco.constant.PatientIn;
import com.marco.getfilepath.CallbackBundle;
import com.marco.getfilepath.OpenFileDialog;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class OtherFunction extends Activity{
	static private int openfileDialogId = 0; 


	private TextView uploadname;
	private TextView readname;
	private TextView downloadname;
	private String uploadpath="";
	private String readpath="";
	private String username;
	private String uploadnametext;
	private String uploadpasswordtext;
	private String downfilename;
	private String realfilename;
	
	private int buttonnumber=1;
	ProgressDialog pd;
	NurseUser user= new NurseUser();
    Filename filename = new Filename();
	SharedPreferences preferences;
	SharedPreferences.Editor editor;
	private static final int PICK_CONTACT_SUBACTIVITY = 2;
	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.other_layout);
        getActionBar().hide();
        uploadname=(TextView)findViewById(R.id.upload_name);
        readname=(TextView)findViewById(R.id.read_name);
        downloadname=(TextView)findViewById(R.id.download_name);
		        
        Intent intent=getIntent();
        username=intent.getStringExtra("username");
    	
	}
	
	public void choosefile(View view){
		showDialog(openfileDialogId);
		buttonnumber=1;
	}
	public void choosefile_read(View view){
		showDialog(openfileDialogId);
		buttonnumber=2;
	}
	public void downloadfile_choose(View view){
		BmobQuery<NurseUser> query = new BmobQuery<NurseUser>();
		query.addWhereEqualTo("Nurse", true);
		query.addWhereEqualTo("inUse", true);
		query.findObjects(this, new FindListener<NurseUser>() {

			@Override
			public void onError(int arg0, String arg1) {
				// TODO Auto-generated method stub				
			}
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
	            Dialog alertDialog = new android.app.AlertDialog.Builder(OtherFunction.this). 
                	    setTitle("需要下载哪个病人的文件？"). 
                	    setIcon(R.drawable.ic_launcher) 
                	    .setItems(patientname, new DialogInterface.OnClickListener() { 
                	     @Override 
                	     public void onClick(DialogInterface dialog, int which) { 
                	         BmobQuery<PatientIn> query = new BmobQuery<PatientIn>();
                	         query.addWhereEqualTo("PatientName", patientname[which]);
                	         uploadnametext=patientname[which];
                	         query.findObjects(OtherFunction.this, new FindListener<PatientIn>() {
								@Override
								public void onError(int arg0, String arg1) {
									// TODO Auto-generated method stub	
							    Toast.makeText(OtherFunction.this, "获取用户信息错误"+arg0+arg1, Toast.LENGTH_SHORT).show();
								}
								@Override
								public void onSuccess(List<PatientIn> arg0) {
									// TODO Auto-generated method stub
									for(PatientIn patientin:arg0){
								   uploadpasswordtext=patientin.getPatientPassword();
									}
							    	user.setUsername(uploadnametext);
							    	user.setPassword(uploadpasswordtext);
							    	user.login(OtherFunction.this, new SaveListener(){
										@Override
										public void onFailure(int arg0,
												String arg1) {
											// TODO Auto-generated method stub
											Toast.makeText(OtherFunction.this, "获取用户信息错误"+arg0+arg1, Toast.LENGTH_SHORT).show();											
										}
										@Override
										public void onSuccess() {
											// TODO Auto-generated method stub
											BmobQuery<NurseUser> query = new BmobQuery<NurseUser>();
									    	query.addWhereEqualTo("username", uploadnametext);
									    	query.findObjects(OtherFunction.this, new FindListener<NurseUser>() {
									    	    @Override
									    	    public void onError(int code, String msg) {
									    	        // TODO Auto-generated method stub
									    	    	Toast.makeText(OtherFunction.this, R.string.usergetfailed, Toast.LENGTH_SHORT).show();
									    	    	finish();
									    	    }
												@Override
												public void onSuccess(List<NurseUser> arg0) {
													// TODO Auto-generated method stub
									                for(NurseUser NurseUser:arg0){
									                	String objectid=NurseUser.getObjectId();
									               Toast.makeText(OtherFunction.this, R.string.usergetsuccess, Toast.LENGTH_SHORT).show();
									                user.setObjectId(objectid);
									                downloadchoose();
												}}
									    	});													
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
		});
	}
	public void readconfirm(View view){
		if(readpath.equals("")){
			Toast.makeText(OtherFunction.this, R.string.emptypath, Toast.LENGTH_SHORT).show();
			return;
		}
		Intent intent= new Intent();
		intent.putExtra("readpath", readpath);
		intent.setClass(OtherFunction.this, ECGFileRead.class);
		startActivity(intent);	
	}
	public void uploadconfirm(View view){
		if(uploadpath.equals("")){
			Toast.makeText(OtherFunction.this, R.string.emptypath, Toast.LENGTH_SHORT).show();
			return;
		}
		BmobQuery<NurseUser> query = new BmobQuery<NurseUser>();
		query.addWhereEqualTo("Nurse", true);
		query.addWhereEqualTo("inUse", true);
		query.findObjects(this, new FindListener<NurseUser>() {

			@Override
			public void onError(int arg0, String arg1) {
				// TODO Auto-generated method stub				
			}
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
	            Dialog alertDialog = new android.app.AlertDialog.Builder(OtherFunction.this). 
                	    setTitle("上传的是哪个病人的文件？"). 
                	    setIcon(R.drawable.ic_launcher) 
                	    .setItems(patientname, new DialogInterface.OnClickListener() { 
                	     @Override 
                	     public void onClick(DialogInterface dialog, int which) { 
                	         BmobQuery<PatientIn> query = new BmobQuery<PatientIn>();
                	         query.addWhereEqualTo("PatientName", patientname[which]);
                	         uploadnametext=patientname[which];
                	         query.findObjects(OtherFunction.this, new FindListener<PatientIn>() {
								@Override
								public void onError(int arg0, String arg1) {
									// TODO Auto-generated method stub	
							    Toast.makeText(OtherFunction.this, "获取用户信息错误"+arg0+arg1, Toast.LENGTH_SHORT).show();
								}
								@Override
								public void onSuccess(List<PatientIn> arg0) {
									// TODO Auto-generated method stub
									for(PatientIn patientin:arg0){
								   uploadpasswordtext=patientin.getPatientPassword();
									}
							    	user.setUsername(uploadnametext);
							    	user.setPassword(uploadpasswordtext);
							    	user.login(OtherFunction.this, new SaveListener(){
										@Override
										public void onFailure(int arg0,
												String arg1) {
											// TODO Auto-generated method stub
											Toast.makeText(OtherFunction.this, "获取用户信息错误"+arg0+arg1, Toast.LENGTH_SHORT).show();											
										}
										@Override
										public void onSuccess() {
											// TODO Auto-generated method stub
											BmobQuery<NurseUser> query = new BmobQuery<NurseUser>();
									    	query.addWhereEqualTo("username", uploadnametext);
									    	query.findObjects(OtherFunction.this, new FindListener<NurseUser>() {
									    	    @Override
									    	    public void onError(int code, String msg) {
									    	        // TODO Auto-generated method stub
									    	    	Toast.makeText(OtherFunction.this, R.string.usergetfailed, Toast.LENGTH_SHORT).show();
									    	    	finish();
									    	    }
												@Override
												public void onSuccess(List<NurseUser> arg0) {
													// TODO Auto-generated method stub
									                for(NurseUser NurseUser:arg0){
									                	String objectid=NurseUser.getObjectId();
									               Toast.makeText(OtherFunction.this, R.string.usergetsuccess, Toast.LENGTH_SHORT).show();
									               uploadfile();
									                user.setObjectId(objectid);
												}}
									    	});													
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
		});
	}
	public void downloadfile_confirm(View view){
		if(realfilename.equals("")||downfilename.equals("")){
			Toast.makeText(OtherFunction.this, "未选择下载文件", Toast.LENGTH_SHORT).show();
			return;
		}
		downloadfile(downfilename,realfilename);
	}
	private void downloadchoose(){
		BmobQuery<Filename> cards = new BmobQuery<Filename>();
	    cards.addWhereRelatedTo("filename", new BmobPointer(user));
	    cards.findObjects(this, new FindListener<Filename>() {

	        @Override
	        public void onSuccess(List<Filename> arg0) {
	            // TODO Auto-generated method stub
            	final String[] realnamearray= new String[arg0.size()];
            	final String[] filenamearray= new String[arg0.size()];
            	int i=0;
	            for (Filename filename : arg0) {
	            	realnamearray[i]=filename.getRealname();
	            	filenamearray[i]=filename.getFilename();
	            	i++;
	                Log.d("bmob", "objectId:"+filename.getObjectId()+",下载名称："+filename.getFilename()+",真实名称："+filename.getRealname());
	            }
                Dialog alertDialog = new android.app.AlertDialog.Builder(OtherFunction.this). 
                	    setTitle("需要下载哪个文件？"). 
                	    setIcon(R.drawable.ic_launcher) 
                	    .setItems(realnamearray, new DialogInterface.OnClickListener() { 
                	     @Override 
                	     public void onClick(DialogInterface dialog, int which) { 
                	    	 downloadname.setText(realnamearray[which]);
                	    	 realfilename=realnamearray[which];
                	    	 downfilename=filenamearray[which];
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
	        public void onError(int arg0, String arg1) {
	            // TODO Auto-generated method stub
	        	Toast.makeText(OtherFunction.this,"文件查询失败！"+arg0+arg1,Toast.LENGTH_SHORT).show();
	        }
	    });		
	}
	private void downloadfile(String downloadpath,final String filerealname){
		pd = new ProgressDialog(OtherFunction.this);
		pd.setTitle("正在下载文件");
		pd.setCancelable(false);
		pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		pd.setIndeterminate(false);
		pd.show();
		BmobProFile.getInstance(OtherFunction.this).download(downloadpath, new DownloadListener() {

            @Override
            public void onSuccess(String fullPath) {
            	pd.dismiss();
                // TODO Auto-generated method stub
                Toast.makeText(OtherFunction.this, "下载成功!", Toast.LENGTH_SHORT).show();
                System.out.println("fullpath:"+fullPath);
                copyandchangname(fullPath,filerealname);
            }

            @Override
            public void onProgress(String localPath, int percent) {
                // TODO Auto-generated method stub
            	pd.setProgress(percent);
            	System.out.println("localpath:"+localPath);
            }

            @Override
            public void onError(int statuscode, String errormsg) {
                // TODO Auto-generated method stub
                pd.dismiss();
                Toast.makeText(OtherFunction.this,"下载出错："+statuscode +"--"+errormsg, Toast.LENGTH_SHORT).show();
            }
        });
	}
	private void copyandchangname(String oldPath,String filerealname){
		 File file = null;
		    try {
		        file = new File("/sdcard/ECG/Download/");
		        if (!file.exists()) {
		            file.mkdir();
		        }
		    } catch (Exception e) {
		        Log.i("error:", e+"");
		    }

         try {   
             int bytesum = 0;   
             int byteread = 0;   
             File oldfile = new File(oldPath);   
             if (oldfile.exists()) { //文件存在时   
                 InputStream inStream = new FileInputStream(oldPath); //读入原文件   
                 FileOutputStream fs = new FileOutputStream("/sdcard/ECG/Download/"+filerealname);   
                 byte[] buffer = new byte[1444];   
                 while ( (byteread = inStream.read(buffer)) != -1) {   
                     bytesum += byteread; //字节数 文件大小   
                     System.out.println(bytesum);   
                     fs.write(buffer, 0, byteread);   
                 }   
                 inStream.close();   
             }   
         }   
         catch (Exception e) {   
             System.out.println("复制单个文件操作出错");   
             e.printStackTrace();    
         } 
         Intent intent= new Intent();
         intent.setClass(OtherFunction.this, OtherFunction.class);
         intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
         finish();
         startActivity(intent);
         overridePendingTransition(0, 0);
	}
	private void uploadfile(){
		pd = new ProgressDialog(OtherFunction.this);
		pd.setTitle("正在上传文件");
		pd.setCancelable(false);
		pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		pd.setIndeterminate(false);
		pd.show();
		
		BTPFileResponse response = BmobProFile.getInstance(OtherFunction.this).upload(uploadpath, 
				new UploadListener() {

            @Override
            public void onSuccess(String fileName,String url) {
                // TODO Auto-generated method stub
    			Toast.makeText(OtherFunction.this, R.string.fileuploadsuccess, Toast.LENGTH_SHORT).show();
    			pd.dismiss();
    			saveFileNameInfo(fileName);
            }

            @Override
            public void onProgress(int ratio) {
                // TODO Auto-generated method stub
            	pd.setProgress(ratio);

            }
            @Override
            public void onError(int statuscode, String errormsg) {
                // TODO Auto-generated method stub
            	Toast.makeText(OtherFunction.this, R.string.fileuploadfailed, Toast.LENGTH_SHORT).show();
            	pd.dismiss();
            }
        });
	}
	private void saveFileNameInfo(String name){
	    if(TextUtils.isEmpty(user.getObjectId())){
	    	Toast.makeText(OtherFunction.this, R.string.usernameempty, Toast.LENGTH_SHORT).show();
	        return;
	    }
	    filename.setFilename(name);        // 设置银行名称 
	    filename.setUser(user);     
	    filename.setRealname(uploadname.getText().toString());// 设置银行卡户主
	    filename.save(this, new SaveListener() {
	        @Override
	        public void onSuccess() {
	            // TODO Auto-generated method stub
		    	Toast.makeText(OtherFunction.this, R.string.fileinfosuccess, Toast.LENGTH_SHORT).show();
	            addFileToUser();
	        }

	        @Override
	        public void onFailure(int arg0, String arg1) {
	            // TODO Auto-generated method stub
		    	Toast.makeText(OtherFunction.this, R.string.fileinfofailed, Toast.LENGTH_SHORT).show();
	        }
	    });
	}
	private void addFileToUser(){
	    if(TextUtils.isEmpty(user.getObjectId()) || 
	            TextUtils.isEmpty(filename.getObjectId())){
	    	Toast.makeText(OtherFunction.this, R.string.usernameempty, Toast.LENGTH_SHORT).show();
	        return;
	    }

	    BmobRelation filenames = new BmobRelation();
	    filenames.add(filename);
	    user.setInUse(true);
	    user.setFilename(filenames);
	    user.update(this, new UpdateListener() {

	        @Override
	        public void onSuccess() {
	            // TODO Auto-generated method stub
	        	Toast.makeText(OtherFunction.this, R.string.attachusersuccess, Toast.LENGTH_SHORT).show();
	        }

	        @Override
	        public void onFailure(int arg0, String arg1) {
	            // TODO Auto-generated method stub
	        	Toast.makeText(OtherFunction.this, R.string.attachuserfailed+arg0+arg1, Toast.LENGTH_SHORT).show();
	        }
	    });
	}
	protected Dialog onCreateDialog(int id) {  
        if(id==openfileDialogId){   
            Map<String, Integer> images = new HashMap<String, Integer>();  
            // 下面几句设置各文件类型的图标， 需要你先把图标添加到资源文件夹  
            images.put(OpenFileDialog.sRoot, R.drawable.filedialog_root);    // 根目录图标  
            images.put(OpenFileDialog.sParent, R.drawable.filedialog_folder_up);    //返回上一层的图标  
            images.put(OpenFileDialog.sFolder, R.drawable.filedialog_folder);   //文件夹图标  
            images.put("txt", R.drawable.filedialog_txtfile);   //文件图标  
            images.put(OpenFileDialog.sEmpty, R.drawable.filedialog_root);  
            Dialog dialog = OpenFileDialog.createDialog(id, this, "打开文件", new CallbackBundle() {  
                public void callback(Bundle bundle) {  
                    String filepath = bundle.getString("path");
                    setTitle(filepath); // 把文件路径显示在标题上  
                    String filename[]=filepath.split("/");
                    switch(buttonnumber){
                    case 1:uploadname.setText(filename[filename.length-1]);uploadpath=filepath;break;
                    case 2:readname.setText(filename[filename.length-1]);readpath=filepath;break;
                    }
                    
                }  
            },   
            ".txt;",  
            images);  
            return dialog;  
        }  
        return null;  
    }


}
