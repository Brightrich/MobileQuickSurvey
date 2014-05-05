package com.example.task;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.channels.FileChannel;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import org.apache.cordova.DroidGap;

import android.os.Bundle;
import android.os.Environment;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.Engine;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;


@SuppressLint("NewApi")
public class QuickSurveyActivity extends DroidGap {

	QuickSurveyPageAttribute resultAttr = null;
	private static int TTS_DATA_CHECK = 1;
	private TextToSpeech tts;
	private boolean isTTSInitialized = false;
	  
	@Override
	public void onCreate(Bundle savedInstanceState) {			
		Log.e("QuickSurveyActivity", "OnCreate");
		String temp = "NULL";
		if(resultAttr!=null && resultAttr.getOrderData() != null){
			temp = resultAttr.getOrderData();
		}
		Log.e("QuickSurveyActivity", "resultAttr.orderId = " + temp);
		
		super.onCreate(savedInstanceState);
		super.init();		
		
		super.setIntegerProperty("loadUrlTimeoutValue", 60000);
		
		appView.addJavascriptInterface(this, "Controller");
		
		Intent intent = getIntent();
        //String messageSender = intent.getStringExtra(ConstantParameter.Application.MSG_SENDER);
        String messageBody = intent.getStringExtra(ConstantParameter.Application.MSG_BODY);
		String appName = intent.getStringExtra(ConstantParameter.Application.APP_NAME);		
		String rowId = intent.getStringExtra(ConstantParameter.Reservation.KEY_ROWID);
		
		Log.e("QuickSurveyActivity OnCreate", "Message Received = " + messageBody);
		
        //Init Data for Dummy runner
		long orderId;
		try {
			orderId = initQuestionnaireData(this, messageBody, appName,rowId);
			
			//INIT TTS
			confirmTTSData();
			
		//Creating Page Attribute
		final QuickSurveyPageAttribute attr = new QuickSurveyPageAttribute(this, appView, orderId, appName);
		resultAttr = attr;
		
		runOnUiThread(new Runnable() {			
			@Override
			public void run() {
				appView.addJavascriptInterface(attr, "PageAttr");
			}
		});
		
		getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
	    getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
	    WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		//getWindow().addFlags(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
		//getWindow().addFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
		//getWindow().addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);
	    
		//getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
		//getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
		
	    if(appName.equals(ConstantParameter.Application.CAROL)){
	    	super.loadUrl("file:///android_asset/www/carolsurvey.html");
	    } else {
	    	super.loadUrl("file:///android_asset/www/fmsurvey.html");
	    }
	    
	    SpeakOfTheDevil.assistActivity(this);
	    
		
		
	    
	    /*getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
	    		WindowManager.LayoutParams.fla.FLAG_FORCE_NOT_FULLSCREEN);*/
	    
	    
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.quick_survey, menu);
		return true;
	}

	
	public void onWindowFocusChanged(boolean hasFocus)
    {
            
			Log.e("FOCUS CHANGED", "CHANGED FOCUS");
			
			try
            {
               if(!hasFocus)
               {
            	   
            	   Log.e("FOCUS CHANGED", "COLLAPSE STATUS BAR");
            	  /* Intent contentIntent = new Intent(this.getContext(), QuickSurveyActivity.class);
            	   contentIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            	   getContext().startActivity(contentIntent);
            	   this.getIntent().no
            	  */ //this.getCurrentFocus().clearFocus();
            	   
            	   
                    Object service  = getSystemService("statusbar");                    
                    Class<?> statusbarManager = Class.forName("android.app.StatusBarManager");
                    Method collapse = statusbarManager.getMethod("collapsePanels");    
                    collapse .setAccessible(true);
                    collapse .invoke(service);
                    
                   // super.onResume();
                    Log.e("FOCUS CHANGED", "END COLLAPSE STATUS BAR");
                    
               }
            }
            catch(Exception ex)
            {
            	ex.printStackTrace();
            	Log.e("FOCUS CHANGED", "ERROR = " + ex.getMessage());
            }
    }
	
	
	//DUMMY FOR DEMO
	public long initQuestionnaireData(Context ctx, String message, String appName, String rowId) throws ParseException{
		
        //Parse Msg Body
      	String[] parsedOrder = message.split("#");
      	String[] fields = ConstantParameter.Reservation.insertStatementFields;
      	HashMap<String,String> mapDataRsv = new HashMap<String, String>();
      	long reservationId = 0L;
      	
      	Dao dao = new Dao(ctx);
      	
      	//Disable to run without init dummy data
      	//createInitData(dao, appName);
      	
      	Log.e("RSVP DATA ", message);
      	//    0             1              2            3          4          5         6           7            8
      	//{APP_NAME, RESERVATION_ID , RESERVED_BY, DESTINATION, PURPOSE, START_TIME, END_TIME, DRIVER_NAME, PLATE_NUMBER};
      	for (int i = 0; i < parsedOrder.length; i++) {
      		//Convert data for CAROL DATA
      		if((i==5 || i==6) && appName.equalsIgnoreCase(ConstantParameter.Application.CAROL) && !parsedOrder[i].equals("")){      		
			   //Date date = ConstantParameter.Application.transformDateFormat.parse(parsedOrder[i]);
			   //mapDataRsv.put(fields[i], ConstantParameter.Application.formatSQLiteDate.format(date));      			
      			mapDataRsv.put(fields[i], parsedOrder[i]);
      		} else {
      			mapDataRsv.put(fields[i], parsedOrder[i]);
      		}
			Log.e(fields[i], parsedOrder[i]);
		}
      	
        //Directly display the survey page
		mapDataRsv.put(ConstantParameter.Reservation.IS_ALREADY_DISPLAYED, ConstantParameter.Application.FLAG_TRUE);
		Log.e(ConstantParameter.Reservation.IS_ALREADY_DISPLAYED, ConstantParameter.Application.FLAG_TRUE);
		
		//Reconciliation Attribute
		mapDataRsv.put(ConstantParameter.Reservation.IS_ALREADY_REPORTED, ConstantParameter.Application.FLAG_FALSE);
		Log.e(ConstantParameter.Reservation.IS_ALREADY_REPORTED, ConstantParameter.Application.FLAG_FALSE);
		
		//Reconciliation Attribute
		Date receivedDate = new Date();
		mapDataRsv.put(ConstantParameter.Reservation.RESERVATION_RECEIVED_DATE, ConstantParameter.Application.formatSQLiteDate.format(receivedDate));
		Log.e(ConstantParameter.Reservation.RESERVATION_RECEIVED_DATE, ConstantParameter.Application.formatSQLiteDate.format(receivedDate));
			
      	dao.open();
      	
      	//CAROL INSERT RESERVATION DATA TO DB
      	if(appName.equals(ConstantParameter.Application.CAROL)){
      		reservationId = dao.insertData(ConstantParameter.Reservation.TABLE, mapDataRsv);
      	} else {
      		//FM UPDATING EXISTING DATA
      		boolean isUpdated = dao.updateDataById(ConstantParameter.Reservation.TABLE, ConstantParameter.Reservation.KEY_ROWID, 
      				Long.valueOf(rowId), mapDataRsv);
      		if(isUpdated){
      			reservationId = Long.valueOf(rowId);
      		}
      		
      	}
      	
		Log.e("INSERT RSVP", "C = "+reservationId);				
		
		dao.close();
		
		Log.e("INIT SURVEY", "Creating default response");
		createDefaultResponse(String.valueOf(reservationId),appName);
		Log.e("INIT SURVEY", "default response created");
		
		return reservationId;
	} 
	
	//UNUSED
	public void createInitData(Dao dao, String appName){
		int isDeleted=0;
		HashMap<String,String> mapData = null;
		HashMap<String,String> mapRate=null;
		
		String[] dataC;
		String[] dataF;
		
		
		//if(appName.equals(ConstantParameter.Application.CAROL)){
			dataC = new String[]{"Kebersihan kendaraan?", "Pengetahuan Rute?", "Ketepatan waktu?"};
		//} else {
			dataF = new String[]{"Kebersihan ruangan?", "Kelengkapan ruangan?", "Kenyamanan ruangan?"};
		//}
		
		//Feedback criteria	
		String[] rateDesc = {ConstantParameter.Application.VERY_DISAPPOINTED, ConstantParameter.Application.DISAPPOINTED, 
							ConstantParameter.Application.NEUTRAL, ConstantParameter.Application.SATISFIED,
							ConstantParameter.Application.VERY_SATISFIED};
		
		String[] rateName = {ConstantParameter.Application.VERY_DISAPPOINTED_RATE, ConstantParameter.Application.DISAPPOINTED_RATE, 
				ConstantParameter.Application.NEUTRAL_RATE, ConstantParameter.Application.SATISFIED_RATE,
				ConstantParameter.Application.VERY_SATISFIED_RATE};
		
		dao.open();

		isDeleted = dao.deleteAllData(ConstantParameter.FeedbackCriteria.TABLE);
		isDeleted = dao.deleteAllData(ConstantParameter.Reservation.TABLE);
		isDeleted = dao.deleteAllData(ConstantParameter.FeedbackResult.TABLE);
		isDeleted = dao.deleteAllData(ConstantParameter.FeedbackRate.TABLE);
		Log.e("CLEAR INIT DATA", "status = "+isDeleted);
		
		dao.close();
		
		//init questionnaire CAROL data
		int number = 0;
		for(int i=0; i < 3; i++){
			number = i +1;
			mapData = new HashMap<String, String>();
			mapData.put(ConstantParameter.FeedbackCriteria.CRITERIA_DESC, dataC[i]);								
			mapData.put(ConstantParameter.FeedbackCriteria.APP_NAME, ConstantParameter.Application.CAROL);
			mapData.put(ConstantParameter.FeedbackCriteria.CRITERIA_ORDER_NUMBER, String.valueOf(number));
			
			Log.e("INSERT QUESTIONNAIRE", "Q = "+dataC[i]);
			dao.open();
			dao.insertData(ConstantParameter.FeedbackCriteria.TABLE, mapData);
			dao.close();
		}
		
		//init questionnaire FM data
				int numbers = 0;
				for(int i=0; i < 3; i++){
					numbers = i +1;
					mapData = new HashMap<String, String>();
					mapData.put(ConstantParameter.FeedbackCriteria.CRITERIA_DESC, dataF[i]);								
					mapData.put(ConstantParameter.FeedbackCriteria.APP_NAME, ConstantParameter.Application.FM);
					mapData.put(ConstantParameter.FeedbackCriteria.CRITERIA_ORDER_NUMBER, String.valueOf(numbers));
					
					Log.e("INSERT QUESTIONNAIRE", "Q = "+dataF[i]);
					dao.open();
					dao.insertData(ConstantParameter.FeedbackCriteria.TABLE, mapData);
					dao.close();
				}
		
		//init Feedback Rate
		for(int i=0; i <rateName.length;i++ ){
			
			mapRate = new HashMap<String, String>();
			mapRate.put(ConstantParameter.FeedbackRate.RATE_NAME, rateName[i]);
			mapRate.put(ConstantParameter.FeedbackRate.RATE_DESCRIPTION, rateDesc[i]);
			mapRate.put(ConstantParameter.FeedbackRate.IMAGE_PATH, "emotes/"+rateName[i]+".png");
			
			Log.e("INSERT FEEDBACK RATE", "data = "+rateDesc[i]);
			dao.open();
			dao.insertData(ConstantParameter.FeedbackRate.TABLE, mapRate);
			dao.close();
		}
		
	}
	
	public void sendMessage(){
		Log.e("Called Interface class", "Submission called");		 		
		
		//GUIDELINE of Result
	    //    0         1                 2               3 
	    //CRITERIA_ID#APP_NAME#CRITERIA_ORDER_NUMBER#RATE_ID@CRITERIA_ID#APP_NAME#CRITERIA_ORDER_NUMBER#RATE_ID
	    //delimited by # and for each new row marked by @
		String voteData = resultAttr.getVoteData();
		
		String[] voteRow = voteData.split("@");
		Dao dao = new Dao(this);
		dao.open();
		
		for(int i =0 ; i < voteRow.length; i ++){			
			String[] voteRes = voteRow[i].split("#");
			
			HashMap<String,String> resMap = new HashMap<String, String>();
			resMap.put(ConstantParameter.FeedbackResult.RESERVATION_ID, String.valueOf(resultAttr.getOrderId()));			
			resMap.put(ConstantParameter.FeedbackResult.FEEDBACK_CRITERIA_ID, voteRes[0]);
			resMap.put(ConstantParameter.FeedbackResult.FEEDBACK_RATE_ID, voteRes[3]);
			resMap.put(ConstantParameter.FeedbackResult.REMARKS, "");
			
			boolean state = dao.updateDataByCondition(ConstantParameter.FeedbackResult.TABLE, 
					ConstantParameter.FeedbackResult.RESERVATION_ID + " =? AND " 
					+ ConstantParameter.FeedbackResult.FEEDBACK_CRITERIA_ID + "=? AND " 
					+ ConstantParameter.FeedbackResult.REMARKS + " =?", 
					new String[]{String.valueOf(resultAttr.getOrderId()), voteRes[0], ConstantParameter.Application.IS_DEFAULTED}
					, resMap);
			
			Log.e("SendMessage", "UPDATE DATA = " + state);
			//dao.insertData(ConstantParameter.FeedbackResult.TABLE, resMap);							
			
		}
		
		//add Suggestion if any
		if(resultAttr.getSuggestion()!= null && !resultAttr.getSuggestion().equals("")){
		
		    HashMap<String,String> advMap = new HashMap<String, String>();
			advMap.put(ConstantParameter.FeedbackResult.RESERVATION_ID, String.valueOf(resultAttr.getOrderId()));
			advMap.put(ConstantParameter.FeedbackResult.FEEDBACK_CRITERIA_ID, ConstantParameter.Application.RES_SUGGESTION);
			advMap.put(ConstantParameter.FeedbackResult.REMARKS, resultAttr.getSuggestion());			
			
			dao.insertData(ConstantParameter.FeedbackResult.TABLE, advMap);
		
		}
		
		
		dao.close();
		
		//CALL THANKS PAGE
		super.loadUrl("file:///android_asset/www/thanks.html");
		//finish();
	}
	
	
	public void finishActivity(){
		writeToSD();
		finish();
		if (tts != null) {
    		tts.stop();
    		tts.shutdown();
    	}
	}
	
	
	
	public void surveyTimeout(){
			finishActivity();		
	}
	
	private void createDefaultResponse(String orderId, String appName){
		String rateId = "";
		
		Dao dao = new Dao(this);
		dao.open();
		
		Cursor x = dao.getAllDataByCondition(ConstantParameter.FeedbackRate.TABLE, new String[]{ConstantParameter.FeedbackRate.KEY_ROWID}, 
				   ConstantParameter.FeedbackRate.RATE_NAME + " =?", new String[]{ConstantParameter.Application.NEUTRAL_RATE}, 
				   ConstantParameter.FeedbackRate.KEY_ROWID);
		
		if(x.moveToFirst()){
			do {
				rateId = x.getString(0);
			} while (x.moveToNext());
		}
		
		//{KEY_ROWID, APP_NAME, CRITERIA_ORDER_NUMBER, CRITERIA_DESC};
		Cursor c = dao.getAllDataByCondition(ConstantParameter.FeedbackCriteria.TABLE, 
				   ConstantParameter.FeedbackCriteria.fieldsOnQuery, 
				   ConstantParameter.FeedbackCriteria.APP_NAME +" =?", new String[]{appName}, 
				   ConstantParameter.FeedbackCriteria.CRITERIA_ORDER_NUMBER);
		
		if(c.moveToFirst()){
			do{
				HashMap<String,String> mapData = new HashMap<String, String>();
				mapData.put(ConstantParameter.FeedbackResult.FEEDBACK_CRITERIA_ID, c.getString(0));
				mapData.put(ConstantParameter.FeedbackResult.FEEDBACK_RATE_ID, rateId);
				mapData.put(ConstantParameter.FeedbackResult.RESERVATION_ID, orderId);
				mapData.put(ConstantParameter.FeedbackResult.REMARKS, ConstantParameter.Application.IS_DEFAULTED);
								
				dao.insertData(ConstantParameter.FeedbackResult.TABLE, mapData);
				
			}while(c.moveToNext());
		}
						
		dao.close();
	}
	
	
	private void writeToSD() {
		try {
			
		Log.e("COPY DB", "NO ERROR");
		
	    String dbPath = this.getDatabasePath("quicksurverydb").toString();
		Log.e("COPY DB", "Current DB PATH = " + dbPath);
	    File sd = Environment.getExternalStorageDirectory();

	    if (sd.canWrite()) {
	    	Log.e("COPY DB", "SD CAN BE WRITTEN");
	        String currentDBPath = "/data/data/com.example.task/databases/quicksurveydb";
	        String backupDBPath = "backupname.db";
	        File currentDB = new File(currentDBPath);
	        File backupDB = new File(sd,backupDBPath);

	        if (currentDB.exists()) {
	        	Log.e("COPY DB", "Current DB Exists");
	            FileChannel src = new FileInputStream(currentDB).getChannel();
	            FileChannel dst = new FileOutputStream(backupDB).getChannel();
	            Log.e("COPY DB", "DESTINATION FOLDER = " + backupDB.getAbsolutePath());
	            dst.transferFrom(src, 0, src.size());
	            src.close();
	            dst.close();
	        }
	    }
	    
		} catch (Exception e) {
		
			Log.e("ERROR",e.getMessage());
		}
	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		
		Log.e("ON ACTIVITY RESULT", "RETURN ON ACTIVITY RESULT");
		
    	if (requestCode == TTS_DATA_CHECK) {
    		Log.e("ON ACTIVITY RESULT", "TTS_DATA_CHECK");
    		if (resultCode == Engine.CHECK_VOICE_DATA_PASS) {
    			//Voice data exists		
    			Log.e("ON ACTIVITY RESULT", "CHECK INIT");
    			initializeTTS();
    		}
    		else {
    			Log.e("ON ACTIVITY RESULT", "NEW INSTALL");
    			Intent installIntent = new Intent(Engine.ACTION_INSTALL_TTS_DATA);
    			startActivity(installIntent);
    		}
    	}
    }
    
    private void initializeTTS() {
		Log.e("INIT TTS", "SET INIT");
    	
    	tts = new TextToSpeech(this, new OnInitListener() {
    		public void onInit(int status) {
    			
    			if (status == TextToSpeech.SUCCESS) {
    				isTTSInitialized = true;
    				Log.e("INIT TTS", "SET INIT");
    			}
    			else {
    				//Handle initialization error here
    				isTTSInitialized = false;
    			}
    		}
    	});
    }
	
    private void confirmTTSData()  {
    	Intent intent = new Intent(Engine.ACTION_CHECK_TTS_DATA);
    	if (tts != null) {
    		tts.stop();
    		tts.shutdown();
    	}    	    	
    	startActivityForResult(intent, TTS_DATA_CHECK);
    }	    	    
    
    public void textToSpeech(String name){		  		 		  	
		  
		  Log.e("TTS", "TTS STARTED");
		  
		  tts.setLanguage(Locale.US);
		  tts.speak("WELCOME " + name, TextToSpeech.QUEUE_ADD, null);
		  Log.e("TTS", "TTS SPEECH");		  		  
	  }
    
}
