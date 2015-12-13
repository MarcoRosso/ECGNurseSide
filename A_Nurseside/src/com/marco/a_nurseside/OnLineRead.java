package com.marco.a_nurseside;










import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.RandomAccessFile;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.json.JSONObject;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobRealTimeData;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.ValueEventListener;


import com.marco.constant.NurseUser;
import com.marco.constant.PatientIn;
import com.marco.dataprocess.BluetoothChatService;
import com.marco.dataprocess.FilterProcess;
import com.marco.dataprocess.QRSProcess;

import android.app.Activity;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class OnLineRead extends Activity{
	private TextView mTitle;
	private TextView ecgshow;
	private ListView mConversationView;
	private Button button_search;
    PowerManager.WakeLock mWakeLock;
	private SurfaceView sfv;
    private SurfaceHolder sfh;
    private int frenumber=0;
    private int smallfrenumber=0;
    private float frecount=0;
    private float smallfrecount=0;
    private int fre=250;
    private int readamount=0;
    private int totalamount=0;
    private int calamount=0;
    private int calchange=0;
    private float linepos=0;
	private int writeamount=0;
    private int  centerY,hline=0,hline2=0,smallhline=0,smallhline2=0,volnumber=0,smallvolnumber=0,oldY,Y_axis[],y=0;
    private float vline=0;
    private float smallvline=0;
    private float onceplus=0;
    private float drawoldx=0;
    private float drawnextx=0;
    private double readdata[];
    private double ecgcal[];
    private boolean creatfile=false;
    private boolean begincal=true;
    private boolean calenough=false;
    private String filename;
    private String folername;
    private String onlinename;
	private ArrayAdapter<String> mConversationArrayAdapter;
	BmobRealTimeData data = new BmobRealTimeData();

	Handler mHandler2= new Handler(){
    	public void handleMessage(Message msg){
            if(msg.what==0x112){
    	    	ecgshow.setText(msg.getData().getString("HR","XXX"));
    	    }
            if(msg.what==0x123){
            	mTitle.setText("已连接到"+onlinename+"的设备");
            }
    	}
    };
	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.onlineread_layout);
        getActionBar().hide();
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        mWakeLock = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "My Tag");
        Bmob.initialize(this, "540ce211a2e2d4de0350b0b92cef5ebf");
        ecgcal=new double[fre*12];
        double temp=fre;
        double temp1=0.2/(1/temp);
        frecount=(int)(temp1);
        sfv = (SurfaceView)findViewById(R.id.SurfaceView01);       
        sfh = sfv.getHolder();
        sfh.addCallback(new Callback()  {

			@Override
			public void surfaceCreated(SurfaceHolder holder) {
				// TODO Auto-generated method stub
		        DisplayMetrics metric = new DisplayMetrics(); 
		        getWindowManager().getDefaultDisplay().getMetrics(metric); 
		        float xcm = (float) (metric.xdpi / 2.54);
		        double temp=sfv.getWidth()/xcm/0.5;
		        frenumber=(int)temp;
		        double temp2=sfv.getWidth()/xcm/0.1;
		        smallfrenumber=(int)temp2;
		        frecount=(float) (xcm*0.5);
		        smallfrecount=(float)(xcm*0.1);
		        volnumber=sfv.getHeight()/50;
		        smallvolnumber=sfv.getHeight()/10;
		        centerY = sfv.getHeight()/2; 
			    hline=centerY;
			    hline2=centerY;
			    smallhline=centerY;
			    smallhline2=centerY;
		        DrawGrid();
		        double temp3=sfv.getWidth()/xcm/2.5*fre;
		        totalamount=(int)temp3;
		        System.out.println("temp3 "+temp3);
		        System.out.println("totalamount"+totalamount);
		        readdata=new double[totalamount];
		        double temp4=sfv.getWidth()/temp3;
		        onceplus=(float)temp4;
				mWakeLock.acquire(); 
			}

			@Override
			public void surfaceChanged(SurfaceHolder holder, int format,
					int width, int height) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void surfaceDestroyed(SurfaceHolder holder) {
				// TODO Auto-generated method stub
				
			}
        	
        });
        setupChat();
        ecgshow=(TextView)findViewById(R.id.show);
		mTitle = (TextView) findViewById(R.id.title_right_text);

	}
	protected void onStart() {
		super.onStart();
	}
	public synchronized void onResume() {
		super.onResume();

	}
	/*protected void onDestroy() {
		super.onDestroy();
		mWakeLock.release(); 
	}*/
	  void DrawGrid(){
	        Canvas canvas = sfh.lockCanvas(new Rect(0, 0, getWindowManager().getDefaultDisplay().getWidth(),
	                getWindowManager().getDefaultDisplay().getHeight()));
	        canvas.drawColor(Color.BLACK);
	        Paint mPaint = new Paint();
	        mPaint.setColor(Color.GRAY);
	        mPaint.setStrokeWidth(2);
	        for(int j=0;j<=frenumber;j++){
	        	canvas.drawLine(vline, 0, vline, sfv.getHeight(), mPaint);
	        	vline=vline+frecount;
	        }
	        for(int k=0;k<=volnumber/2;k++){
	        	canvas.drawLine(0, hline, sfv.getWidth(),hline, mPaint);
	        	hline=hline+50;
	        }
	        for(int k=0;k<=volnumber/2;k++){
	        	canvas.drawLine(0, hline2, sfv.getWidth(),hline2, mPaint);
	        	hline2=hline2-50;
	        }
	        mPaint.setStrokeWidth(1);
	        for(int j=0;j<=smallfrenumber;j++){
	        	canvas.drawLine(smallvline, 0, smallvline, sfv.getHeight(), mPaint);
	        	smallvline=smallvline+smallfrecount;
	        }
	        for(int k=0;k<=smallvolnumber/2;k++){
	        	canvas.drawLine(0, smallhline, sfv.getWidth(),smallhline, mPaint);
	        	smallhline=smallhline+10;
	        }
	        for(int l=0;l<=smallvolnumber/2;l++){
	        	canvas.drawLine(0, smallhline2, sfv.getWidth(),smallhline2, mPaint);
	        	smallhline2=smallhline2-10;
	        }
	        mPaint.setStrokeWidth(4);
	        canvas.drawLine(0, centerY, sfv.getWidth(), centerY, mPaint);
	        mPaint.setColor(Color.GREEN);
	        mPaint.setStrokeWidth(2);
	        double H = 8; // 箭头高度   
	        double L = 3.5; // 底边的一半   
	        int x3 = 0;
	        int y3 = 0;
	        int x4 = 0;
	        int y4 = 0;
	        float ex=frecount,sx=frecount;
	        int ey=sfv.getHeight()-85,sy=sfv.getHeight()-35;
	        canvas.drawText("0.5mV", frecount+2, sfv.getHeight()-86, mPaint);
	        double awrad = Math.atan(L / H); // 箭头角度   
	        double arraow_len = Math.sqrt(L * L + H * H); // 箭头的长度   
	        double[] arrXY_1 = rotateVec(ex - sx, ey - sy, awrad, true, arraow_len);
	        double[] arrXY_2 = rotateVec(ex - sx, ey - sy, -awrad, true, arraow_len);
	        double x_3 = ex - arrXY_1[0]; // (x3,y3)是第一端点   
	        double y_3 = ey - arrXY_1[1];
	        double x_4 = ex - arrXY_2[0]; // (x4,y4)是第二端点   
	        double y_4 = ey - arrXY_2[1];
	        Double X3 = new Double(x_3);
	        x3 = X3.intValue();
	        Double Y3 = new Double(y_3);
	        y3 = Y3.intValue();
	        Double X4 = new Double(x_4);
	        x4 = X4.intValue();
	        Double Y4 = new Double(y_4);
	        y4 = Y4.intValue();
	        canvas.drawLine(sx, sy, ex, ey,mPaint);
	        Path triangle = new Path();
	        triangle.moveTo(ex, ey);
	        triangle.lineTo(x3, y3);  
	        triangle.lineTo(x4, y4); 
	        triangle.close();
	        canvas.drawPath(triangle,mPaint);
	        x3 = 0;y3 = 0;x4 = 0;y4 = 0;
	        ex=frecount*2;sx=frecount;ey=sfv.getHeight()-36;sy=sfv.getHeight()-36;
	        canvas.drawText("0.2s", frecount*2, sfv.getHeight()-38, mPaint);
	        awrad = Math.atan(L / H); // 箭头角度   
	        arraow_len = Math.sqrt(L * L + H * H); // 箭头的长度   
	        arrXY_1 = rotateVec(ex - sx, ey - sy, awrad, true, arraow_len);
	        arrXY_2 = rotateVec(ex - sx, ey - sy, -awrad, true, arraow_len);
	        x_3 = ex - arrXY_1[0]; // (x3,y3)是第一端点   
	        y_3 = ey - arrXY_1[1];
	        x_4 = ex - arrXY_2[0]; // (x4,y4)是第二端点   
	        y_4 = ey - arrXY_2[1];
	        X3 = new Double(x_3);
	        x3 = X3.intValue();
	        Y3 = new Double(y_3);
	        y3 = Y3.intValue();
	        X4 = new Double(x_4);
	        x4 = X4.intValue();
	        Y4 = new Double(y_4);
	        y4 = Y4.intValue();
	        canvas.drawLine(sx, sy, ex, ey,mPaint);
	        Path triangle2 = new Path();
	        triangle2.moveTo(ex, ey);
	        triangle2.lineTo(x3, y3);  
	        triangle2.lineTo(x4, y4); 
	        triangle2.close();
	        canvas.drawPath(triangle2,mPaint);
	        sfh.unlockCanvasAndPost(canvas);
	    }
	  void SimpleDraw(int length) {
		  Canvas canvas = sfh.lockCanvas(new Rect(0, 0, getWindowManager().getDefaultDisplay().getWidth(),
	                getWindowManager().getDefaultDisplay().getHeight()));
	        canvas.drawColor(Color.BLACK);
	        Paint mPaint = new Paint();
	        mPaint.setColor(Color.GRAY);
	        mPaint.setStrokeWidth(2);
	        for(int j=0;j<=frenumber;j++){
	        	canvas.drawLine(vline, 0, vline, sfv.getHeight(), mPaint);
	        	vline=vline+frecount;
	        }
	        for(int k=0;k<=volnumber/2;k++){
	        	canvas.drawLine(0, hline, sfv.getWidth(),hline, mPaint);
	        	hline=hline+50;
	        }
	        for(int k=0;k<=volnumber/2;k++){
	        	canvas.drawLine(0, hline2, sfv.getWidth(),hline2, mPaint);
	        	hline2=hline2-50;
	        }
	        mPaint.setStrokeWidth(1);
	        for(int i=0;i<=smallfrenumber;i++){
	        	canvas.drawLine(smallvline, 0, smallvline, sfv.getHeight(), mPaint);
	        	smallvline=smallvline+smallfrecount;
	        }
	        for(int k=0;k<=smallvolnumber/2;k++){
	        	canvas.drawLine(0, smallhline, sfv.getWidth(),smallhline, mPaint);
	        	smallhline=smallhline+10;
	        }
	        for(int l=0;l<=smallvolnumber/2;l++){
	        	canvas.drawLine(0, smallhline2, sfv.getWidth(),smallhline2, mPaint);
	        	smallhline2=smallhline2-10;
	        }
	        mPaint.setStrokeWidth(4);
	        canvas.drawLine(0, centerY, sfv.getWidth(), centerY, mPaint);
	    	        mPaint.setColor(Color.GREEN);// 画笔为绿色
	    	        mPaint.setStrokeWidth(2);// 设置画笔粗细
	    	        canvas.drawLine(linepos, 0, linepos, sfv.getHeight(), mPaint);
	    	        int y;
	    	        for (int i = 0; i < length; i++) {// 绘画
	    	        y = Y_axis[i];
	    	        canvas.drawLine(drawoldx, oldY, drawnextx, y, mPaint);
	    	        drawoldx=drawnextx;
	    	        drawnextx=drawoldx+onceplus;
	    	        oldY = y;
	    	        }
	    	  double H = 8; // 箭头高度   
	    	  double L = 3.5; // 底边的一半   
	    	  int x3 = 0;
	    	  int y3 = 0;
	    	  int x4 = 0;
	    	  int y4 = 0;
	    	  float ex=frecount,sx=frecount;
	    	  int ey=sfv.getHeight()-85,sy=sfv.getHeight()-35;
	    	  canvas.drawText("0.5mV", frecount+2, sfv.getHeight()-86, mPaint);
	    	  double awrad = Math.atan(L / H); // 箭头角度   
	    	  double arraow_len = Math.sqrt(L * L + H * H); // 箭头的长度   
	    	  double[] arrXY_1 = rotateVec(ex - sx, ey - sy, awrad, true, arraow_len);
	    	  double[] arrXY_2 = rotateVec(ex - sx, ey - sy, -awrad, true, arraow_len);
	    	  double x_3 = ex - arrXY_1[0]; // (x3,y3)是第一端点   
	    	  double y_3 = ey - arrXY_1[1];
	    	  double x_4 = ex - arrXY_2[0]; // (x4,y4)是第二端点   
	    	  double y_4 = ey - arrXY_2[1];
	    	  Double X3 = new Double(x_3);
	    	  x3 = X3.intValue();
	    	  Double Y3 = new Double(y_3);
	    	  y3 = Y3.intValue();
	    	  Double X4 = new Double(x_4);
	    	  x4 = X4.intValue();
	    	  Double Y4 = new Double(y_4);
	    	  y4 = Y4.intValue();
	    	  canvas.drawLine(sx, sy, ex, ey,mPaint);
	    	  Path triangle = new Path();
	    	  triangle.moveTo(ex, ey);
	    	  triangle.lineTo(x3, y3);  
	    	  triangle.lineTo(x4, y4); 
	    	  triangle.close();
	    	  canvas.drawPath(triangle,mPaint);
	    	  x3 = 0;y3 = 0;x4 = 0;y4 = 0;
	    	  ex=frecount*2;sx=frecount;ey=sfv.getHeight()-36;sy=sfv.getHeight()-36;
	    	  canvas.drawText("0.2s", frecount*2, sfv.getHeight()-38, mPaint);
	    	  awrad = Math.atan(L / H); // 箭头角度   
	    	  arraow_len = Math.sqrt(L * L + H * H); // 箭头的长度   
	    	  arrXY_1 = rotateVec(ex - sx, ey - sy, awrad, true, arraow_len);
	    	  arrXY_2 = rotateVec(ex - sx, ey - sy, -awrad, true, arraow_len);
	    	  x_3 = ex - arrXY_1[0]; // (x3,y3)是第一端点   
	    	  y_3 = ey - arrXY_1[1];
	    	  x_4 = ex - arrXY_2[0]; // (x4,y4)是第二端点   
	    	  y_4 = ey - arrXY_2[1];
	    	  X3 = new Double(x_3);
	    	  x3 = X3.intValue();
	    	  Y3 = new Double(y_3);
	    	  y3 = Y3.intValue();
	    	  X4 = new Double(x_4);
	    	  x4 = X4.intValue();
	    	  Y4 = new Double(y_4);
	    	  y4 = Y4.intValue();
	    	  canvas.drawLine(sx, sy, ex, ey,mPaint);
	    	  Path triangle2 = new Path();
	    	  triangle2.moveTo(ex, ey);
	    	  triangle2.lineTo(x3, y3);  
	    	  triangle2.lineTo(x4, y4); 
	    	  triangle2.close();
	    	  canvas.drawPath(triangle2,mPaint);
	       sfh.unlockCanvasAndPost(canvas);// 解锁画布，提交画好的图像
	  }
      void ClearDraw() {
      Canvas canvas = sfh.lockCanvas(null);
      canvas.drawColor(Color.BLACK);// 清除画布
      sfh.unlockCanvasAndPost(canvas);
      }
	private void setupChat() {

		// Initialize the array adapter for the conversation thread
		mConversationArrayAdapter = new ArrayAdapter<String>(this,
				R.layout.message);
		mConversationView = (ListView) findViewById(R.id.in);
		mConversationView.setAdapter(mConversationArrayAdapter);

		button_search = (Button) findViewById(R.id.button3);
		button_search.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
   	         BmobQuery<PatientIn> query = new BmobQuery<PatientIn>();
   	         query.addWhereEqualTo("Online", 1);
   	         query.findObjects(OnLineRead.this, new FindListener<PatientIn>() {
					@Override
					public void onError(int arg0, String arg1) {
						// TODO Auto-generated method stub	
				    Toast.makeText(OnLineRead.this, "获取在线用户信息错误"+arg0+arg1, Toast.LENGTH_SHORT).show();
					}
					@Override
					public void onSuccess(List<PatientIn> object) {
						// TODO Auto-generated method stub
						final String[] patientname= new String[object.size()];
						int i=0;
						 for (PatientIn patientin : object) {
				            	patientname[i]=patientin.getPatientName();
				            	i++;
				            }
				            Dialog alertDialog = new android.app.AlertDialog.Builder(OnLineRead.this). 
			                	    setTitle("需要连接哪个病人的在线设备？"). 
			                	    setIcon(R.drawable.ic_launcher) 
			                	    .setItems(patientname, new DialogInterface.OnClickListener() { 
			                	     @Override 
			                	     public void onClick(DialogInterface dialog, int which) { 
                                          onlinename=patientname[which];
                                          folername=onlinename;
                                          File file = null;
  		                			    try {
  		                			        file = new File("/sdcard/ECG/"+folername+"/");
  		                			        if (!file.exists()) {
  		                			            file.mkdir();
  		                			        }
  		                			         } catch (Exception e) {
  		                			            Log.i("error:", e+"");
  		                			         }
                                          if(data.isConnected())
                                        	 data.unsubTableUpdate("RealTimeData");
                                          getData();
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
		});
	}
	private void getData(){
         data.start(OnLineRead.this, new ValueEventListener() {			
			@Override
			public void onDataChange(JSONObject arg0) {
				// TODO Auto-generated method stub
	               
				if(BmobRealTimeData.ACTION_UPDATETABLE.equals(arg0.optString("action"))){
					JSONObject data = arg0.optJSONObject("data");
					if(data.optString("Username").equals(onlinename)){
						String onlinedata=data.optString("Data");
						mHandler2.sendEmptyMessage(0x123);
						mConversationArrayAdapter.add(onlinedata);
						if(!creatfile){
							filename=gettime();
							startfile(Integer.toString(fre),filename);
							creatfile=true;
						}
						String[] temp1=onlinedata.split("\n");
						double temp2[]=new double[temp1.length];
						for(int i=0;i<temp1.length;i++){
							if(isDouble(temp1[i]))
				            temp2[i]=Double.parseDouble(temp1[i]);
							if(writeamount==fre*60){
								filename=gettime();
								startfile(Integer.toString(fre),filename);
								creatfile=true;
								writeamount=0;
							}
							writefile(filename,temp2[i]+"\n");
							writeamount=writeamount+1;
					    }
						int readamountonce=temp2.length;
						if(calamount+readamountonce<fre*12)
					    	calenough=false;
					    else
					    	calenough=true;
					    if(!calenough){
						       for(int i=calamount;i<readamountonce+calamount;i++){
							          ecgcal[i]=temp2[i-calamount];
						       } 
						   calamount=calamount+readamountonce;
						}else{
							  if(begincal){
								  int readtemp=0;
							      for(int i=readamountonce;i<fre*12;i++){
									   double trans=ecgcal[i];
									   ecgcal[i-readamountonce]=trans;
									   }
							      for(int i=fre*12-readamountonce;i<fre*12;i++){
										   ecgcal[i]=temp2[readtemp];
										   readtemp++;
									   }
							      calchange=calchange+readamountonce;
							      if(calchange>=fre*2){
								      HRCal();
								      calchange=0;
							      }
							  }else{
								  int readtemp=0;
							      for(int i=readamountonce;i<fre*12;i++){
									   double trans=ecgcal[i];
									   ecgcal[i-readamountonce]=trans;
									   }
							      for(int i=fre*12-readamountonce;i<fre*12;i++){
										   ecgcal[i]=temp2[readtemp];
										   readtemp++;
									   }
							      HRCal();
							      begincal=true;
							  }			      
						}
					    
						 if (readamountonce+readamount>totalamount){
						    	for(int i=readamount;i<totalamount;i++){
											  readdata[i]=temp2[i-readamount];
											  linepos=i*onceplus;
						    	}
						    	int alreadyread=totalamount-readamount;
						    	int leftread=readamountonce+readamount-totalamount;
								for(int i=0;i<leftread;i++){
										  readdata[i]=temp2[alreadyread+i];
										  linepos=i*onceplus;
									 }
						    	readamount=leftread;
						    }else{
								for(int i=readamount;i<readamountonce+readamount;i++){
										  readdata[i]=temp2[i-readamount];
										  linepos=i*onceplus;
									 }
								readamount=readamountonce+readamount;
						    }
								   
						Y_axis=new int[readdata.length];
				        for(int i=0;i<readdata.length;i++){
				        	Y_axis[i]=(int)(readdata[i]*100);
				        	Y_axis[i] = centerY-Y_axis[i];
				        }
				        drawoldx=0;
				        drawnextx=0;
				    	y=0;
				        oldY = centerY;
				        vline=0;
				        smallvline=0;
					    hline=centerY;
					    hline2=centerY;
					    smallhline=centerY;
					    smallhline2=centerY;
				        SimpleDraw(Y_axis.length);	
					}
				}			
			}			
			@Override
			public void onConnectCompleted() {
				// TODO Auto-generated method stub
				if(data.isConnected()){
					data.subTableUpdate("RealTimeData");
					   /*   		*/     
				}
			}
		});		
	}

	public String gettime()
	{
	  Date date=new Date();
	  DateFormat format=new SimpleDateFormat("yyyyMMdd-HHmmss");
	  String time=format.format(date);
	  return time;
	}
	 public void startfile(String s,String name)
    {
	  try 
	  {
	   folername=onlinename;
	   FileOutputStream outStream = new FileOutputStream("/sdcard/ECG/"+folername+"/"+name+".txt",true);
	   OutputStreamWriter writer = new OutputStreamWriter(outStream,"UTF-8");
	   writer.write(s);
	   writer.write("\n");
	   writer.flush();
	   writer.close();//记得关闭
	   outStream.close();
	  } 
	  catch (Exception e)
	  {
	    e.printStackTrace();
	   Toast.makeText(OnLineRead.this, "文件创建错误", Toast.LENGTH_SHORT).show();
	  } 
   }
	 public void writefile(String name,String content){
		 folername=onlinename;
		 File targetFile=new File("/sdcard/ECG/"+folername+"/"+name+".txt");
		 try {
			RandomAccessFile raf=new RandomAccessFile(targetFile,"rw");
			try {
				raf.seek(targetFile.length());
				raf.write(content.getBytes());
				raf.close();
			} catch (IOException e) {
				Toast.makeText(OnLineRead.this, "文件写入错误", Toast.LENGTH_SHORT).show();
				e.printStackTrace();
			}			
		} catch (FileNotFoundException e) {
			Toast.makeText(OnLineRead.this, "文件写入错误", Toast.LENGTH_SHORT).show();
			//e.printStackTrace();
		}
		 
	 }
	public boolean isDouble(String str)
	{
		   try
		   {
		      Double.parseDouble(str);
		      return true;
		   }
		   catch(NumberFormatException ex){}
		   return false;
	}
    public double[] rotateVec(float f, int py, double ang, boolean isChLen, double newLen)
    {
        double mathstr[] = new double[2];
        // 矢量旋转函数，参数含义分别是x分量、y分量、旋转角、是否改变长度、新长度   
        double vx = f * Math.cos(ang) - py * Math.sin(ang);
        double vy = f * Math.sin(ang) + py * Math.cos(ang);
        if (isChLen) {
            double d = Math.sqrt(vx * vx + vy * vy);
            vx = vx / d * newLen;
            vy = vy / d * newLen;
            mathstr[0] = vx;
            mathstr[1] = vy;
        }
        return mathstr;
    }
	public void HRCal(){
        new Thread(){ 
           public void run(){ 
            double Y[]=new double[ecgcal.length + 2];
       		FilterProcess.filtering(ecgcal.length, ecgcal, Y, fre);
       		int Rnum;
       		int[] RIndex = new int[ecgcal.length+1];
       		Rnum = QRSProcess.RPeekDetect(Y, ecgcal.length, fre, RIndex);
       		for (int i = 0; i < Rnum; i++)
       		{
       			RIndex[i] = RIndex[i + 1];
       		}
       		//System.out.println("Rnum"+Rnum);
       		int[] QIndex = new int[ecgcal.length+1];
       		int Qnum;
       		Qnum = QRSProcess.QPeekDetect(Y, ecgcal.length, fre, QIndex, Rnum, RIndex);
       		//System.out.println("Qnum"+Qnum);
       		int[] SIndex = new int[ecgcal.length+1];
       		int Snum;
       		Snum = QRSProcess.SPeekDetect(Y, ecgcal.length, fre, SIndex, Rnum, RIndex);
       		//System.out.println("Snum"+Snum);
       		int[] RPeek = new int[Rnum-1];
       		for (int i = 0; i < Rnum - 1; i++)//这里不知道怎么要减二本来减一
       		{
       			RPeek[i] = RIndex[i + 1] - RIndex[i];
       		}
       		double RPeekAverage = QRSProcess.sumint(RPeek,Rnum - 1) / (Rnum - 1);
       		double TR = RPeekAverage / 250.0;
       		double HR = 60.0 / TR;
       		//System.out.println("HR:"+HR);
       		int x=Integer.parseInt(new java.text.DecimalFormat("0").format(HR));
       		Message msg = new Message();  
               msg.what = 0x112;  
               Bundle bundle = new Bundle();    
               bundle.putString("HR",Integer.toString(x));  //往Bundle中存放数据       
               msg.setData(bundle);//mes利用Bundle传递数据   
               mHandler2.sendMessage(msg);   		        
           } 
       }.start(); 
	}
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN){   
    		mWakeLock.release(); 
               finish(); 
               if(data.isConnected())
             	 data.unsubTableUpdate("RealTimeData");
        }
        return super.onKeyDown(keyCode, event);
    }

}
