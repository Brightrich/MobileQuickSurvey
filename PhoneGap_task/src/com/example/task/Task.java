package com.example.task;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.cordova.DroidGap;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.WindowManager;

public class Task extends DroidGap{

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		super.init();						
		
		super.setIntegerProperty("loadUrlTimeoutValue", 60000);
		
		appView.addJavascriptInterface(this, "MainActivity");				

		getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
	    getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
	    WindowManager.LayoutParams.FLAG_FULLSCREEN);	    	    
	    
	    Log.e("OnCREATE", "ACTIVITY CREATED");
		super.loadUrl("file:///android_asset/www/welcome-fm1.html");
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.quick_survey, menu);
		return true;
	}		
	
	@Override
	protected void onResume() {
		Log.e("OnRESUME", "ACTIVITY RESUME");
		//writeToSD();
		sendEmailReconciliation();
	    super.onResume();
	    
	}    
	
	public void sendMessage(String appName){
		Log.e("Custom Function Called", "Custom Function Called");
		
		
		Intent intent = new Intent(this, QuickSurveyActivity.class);
		
		if(appName.equalsIgnoreCase("carol")){
			//DUMMY CAROL 
			intent.putExtra(ConstantParameter.Application.MSG_SENDER, "081286041588"); 
			intent.putExtra(ConstantParameter.Application.MSG_BODY, "CAROL#CAR-001#Andro#Wisma Mulia#antar###Gogon#B811SEL#");
			intent.putExtra(ConstantParameter.Application.APP_NAME, ConstantParameter.Application.CAROL);
			startActivity(intent);
		} else {
			//DUMMY FM
			//{KEY_ROWID, RESERVATION_ID ,APP_NAME, RESERVED_BY, START_TIME, END TIME, DEPARTMENT}
			//Time format "yyyy-MM-dd HH:mm:ss"
			//APP_NAME, RESERVATION_ID , RESERVED_BY, DESTINATION, PURPOSE, START_TIME, END_TIME, DRIVER_NAME, PLATE_NUMBER, REMARKS
			insertFMData("FM#00024/ROOM/III/2015#Niken Susilowati#R. Meeting-Veronica#Data Mining, Reporting and Monitoring#2014-03-26 08:00#2014-03-26 10:30###I");
			insertFMData("FM#00034/ROOM/III/2015#Hendri Timbat#R. Meeting-Veronica#Project tracking#2014-03-26 11:00#2014-03-26 12:00###I");
			insertFMData("FM#00044/ROOM/III/2015#Arga Hasril#R. Meeting-Veronica#Mobile Quick Survey#2014-03-26 13:00#2014-03-26 14:00###I");
			insertFMData("FM#00054/ROOM/III/2015#Andro#R. Meeting-Veronica#Android KITKAT Update#2014-03-26 14:00#2014-03-26 14:30###I");
			insertFMData("FM#00064/ROOM/III/2015#Boss#R. Meeting-Veronica#Kongkow-kongkow#2014-03-26 15:00#2014-03-26 16:00###I");
			insertFMData("FM#00074/ROOM/III/2015#Visitor#R. Meeting-Veronica#project implementation#2014-03-26 16:00#2014-03-26 16:30###I");
			insertFMData("FM#00084/ROOM/III/2015#Office Boy#R. Meeting-Veronica#Demo Gaji#2014-03-26 16:30#2014-03-26 17:00###I");
			
		}
		
    					
	}
	
	private void sendEmailReconciliation() {
		
		String reservationId="";				
		
		String rawQuery = "SELECT a."+ConstantParameter.Reservation.RESERVATION_ID 
						  + ", a." + ConstantParameter.Reservation.APP_NAME 
						  + ", a." + ConstantParameter.Reservation.RESERVED_BY
						  + ", a." + ConstantParameter.Reservation.PLATE_NUMBER
						  + ", a." + ConstantParameter.Reservation.DRIVER_NAME
						  + ", a." + ConstantParameter.Reservation.DESTINATION
						  + ", a." + ConstantParameter.Reservation.PURPOSE
						  + ", a." + ConstantParameter.Reservation.START_TIME
						  + ", a." + ConstantParameter.Reservation.END_TIME
						  + ", b." + ConstantParameter.FeedbackResult.REMARKS
						  + ", c." + ConstantParameter.FeedbackCriteria.CRITERIA_ORDER_NUMBER
						  + ", c." + ConstantParameter.FeedbackCriteria.CRITERIA_DESC
						  + ", d." + ConstantParameter.FeedbackRate.RATE_NAME
						  + ", d." + ConstantParameter.FeedbackRate.RATE_DESCRIPTION
						  + " FROM "+ ConstantParameter.Reservation.TABLE +" a "
						  + " LEFT JOIN " + ConstantParameter.FeedbackResult.TABLE + " b on a."+ ConstantParameter.Reservation.KEY_ROWID + " = b." + ConstantParameter.FeedbackResult.RESERVATION_ID
						  + " LEFT JOIN " + ConstantParameter.FeedbackCriteria.TABLE + " c on c."+ ConstantParameter.FeedbackCriteria.KEY_ROWID + " = b." + ConstantParameter.FeedbackResult.FEEDBACK_CRITERIA_ID
						  + " LEFT JOIN " + ConstantParameter.FeedbackRate.TABLE + " d on d."+ ConstantParameter.FeedbackRate.KEY_ROWID + " = b." + ConstantParameter.FeedbackResult.FEEDBACK_RATE_ID
						  + " WHERE a." + ConstantParameter.Reservation.IS_ALREADY_REPORTED + " =? AND a."+ ConstantParameter.Reservation.START_TIME + "<? AND a."+ConstantParameter.Reservation.IS_ALREADY_DISPLAYED +" =?" 
						  +	" ORDER BY a." +ConstantParameter.Reservation.START_TIME +" ASC" ;
						  
		String messageHeader = "", messageDetail="", message="";
		String prevRevId = "";
		
		Dao dao = new Dao(this);
		dao.open();
		Cursor c = dao.queryWithRawSQL(rawQuery, new String[]{ConstantParameter.Application.FLAG_FALSE, ConstantParameter.Application.shortDateFormat.format(new Date()), ConstantParameter.Application.FLAG_TRUE});
		dao.close();
		//      0                 1             2              3           4                 5            6           7             8          9              10                     11             12            13
		//a.RESERVATION_ID, a.APP_NAME, a.RESERVED_BY, a.PLATE_NUMBER, a.DRIVER_NAME, a.DESTINATION, a.PURPOSE, a.START_TIME, a.END_TIME, b.REMARKS, c.CRITERIA_ORDER_NUMBER, c.CRITERIA_DESC, d.RATE_NAME, d.RATE_DESCRIPTION
		if(c.moveToFirst()){
							
			prevRevId=c.getString(0);
			reservationId = "'"+c.getString(0)+"' ";				
			do{																						
				
				if(prevRevId.equalsIgnoreCase(c.getString(0))){
				
					if(messageHeader.equals("")){
						messageHeader = createMessageHeader(c.getString(1), c);
					} 
					
					if(c.getString(9)==null || c.getString(9).equals("") || c.getString(9).equalsIgnoreCase("null") || c.getString(9).equalsIgnoreCase(ConstantParameter.Application.IS_DEFAULTED)){
						
						if(c.getString(9)!=null && c.getString(9).equalsIgnoreCase(ConstantParameter.Application.IS_DEFAULTED)){
							messageDetail = messageDetail + "<tr><td>"+ c.getString(10) +"</td><td colspan='2'>"+ c.getString(11) +"</td><td>"+ c.getString(12) +"</td><td>"+ c.getString(13) +" ("+ c.getString(9) +")</td></tr>";
						} else {
							messageDetail = messageDetail + "<tr><td>"+ c.getString(10) +"</td><td colspan='2'>"+ c.getString(11) +"</td><td>"+ c.getString(12) +"</td><td>"+ c.getString(13) +"</td></tr>";
						}
						
						
					} else {
						messageDetail = messageDetail + "<tr><td>&nbsp;</td><td colspan='2'>"+ ConstantParameter.Application.RES_SUGGESTION +"</td><td>&nbsp;</td><td>"+ c.getString(9) +"</td></tr>";
					}
				
				} else {
					message = message + messageHeader + messageDetail + "</table><br><br>";
					messageHeader = "";
					messageDetail = "";
					prevRevId=c.getString(0);
					
					messageHeader = createMessageHeader(c.getString(0), c);
					if(c.getString(9)==null || c.getString(9).equals("") || c.getString(9).equalsIgnoreCase("null") || c.getString(9).equalsIgnoreCase(ConstantParameter.Application.IS_DEFAULTED)){
						messageDetail = "<tr><td>"+ c.getString(10) +"</td><td colspan='2'>"+ c.getString(11) +"</td><td>"+ c.getString(12) +"</td><td>"+ c.getString(13) +"</td></tr>";
					} else {
						messageDetail = messageDetail + "<tr><td>&nbsp;</td><td colspan='2'>"+ ConstantParameter.Application.RES_SUGGESTION +"</td><td>&nbsp;</td><td>"+ c.getString(9) +"</td></tr>";
					}
					reservationId = reservationId + ", '"+c.getString(0)+"'";
				}
			
			} while(c.moveToNext());
			
			message = message + messageHeader + messageDetail + "</table><br><br>";     
		}
		
		Log.e("SendMail", "Message generated = " + message);
		
		if(!message.equals("")){
			Log.e("SendMail", "ready to send mail recon");						
			
			EmailFeeder feeder = new EmailFeeder();
			feeder.execute(message, reservationId);
		
		}
	}
	
	public String createMessageHeader(String appName, Cursor c){
		String messageHeader = "";
		if(appName.equalsIgnoreCase(ConstantParameter.Application.CAROL)){
			messageHeader = "<table>" +
				"<tr><td>Reservation Id </td><td>"+ c.getString(0) +"</td><td>&nbsp;</td><td>Application</td><td>"+ c.getString(1) +"</td></tr>" +
				"<tr><td>Reserved By </td><td>"+ c.getString(2) +"</td><td>&nbsp;</td><td>Purpose</td><td>"+ c.getString(6) +"</td></tr>" +
				"<tr><td>Driver Name </td><td>"+ c.getString(4) +"</td><td>&nbsp;</td><td>Plate Number</td><td>"+ c.getString(3) +"</td></tr>" +
				"<tr><td>Destination </td><td colspan='4'>"+ c.getString(5) +"</td></tr>" +
				"<tr><td>Question Number</td><td colspan='2'>Question</td><td>Feedback Rate</td><td>Remarks</td></tr>";
		} else {
			messageHeader = "<table>" +
					"<tr><td>Reservation Id </td><td>"+ c.getString(0) +"</td><td>&nbsp;</td><td>Application</td><td>"+ c.getString(1) +"</td></tr>" +
					"<tr><td>Reserved By </td><td>"+ c.getString(2) +"</td><td>&nbsp;</td><td>Purpose</td><td>"+ c.getString(6) +"</td></tr>" +
					"<tr><td>Start Time </td><td>"+ c.getString(7) +"</td><td>&nbsp;</td><td>End Time</td><td>"+ c.getString(8) +"</td></tr>" +								
					"<tr><td>Question Number</td><td colspan='2'>Question</td><td>Feedback Rate</td><td>Remarks</td></tr>";					
		}
		return messageHeader;
	}
	
		
	
	
	private class EmailFeeder extends AsyncTask<String, String, String>{

	    private Exception exception;

	    protected String doInBackground(String... params) {
	    	String result="";
	        	Log.e("SendMail", "ready to send mail recon");
				try {   
		            EmailSender sender = new EmailSender("bnr.demo1@gmail.com", "brt123456");
		            sender.sendMail("Test Survey Response", params[0],
		                    "bnr.demo1@gmail.com",   
		                    "bnr.demo1@gmail.com");
		            
		            Log.e("SendMail", "done sending email recon");
		            result = "Recon Mail Sent!";
		            
		            
		            Dao dao = new Dao(getApplicationContext());
		            dao.open();
		            
		            HashMap<String,String> dataMap = new HashMap<String,String>();
		            dataMap.put(ConstantParameter.Reservation.IS_ALREADY_REPORTED, ConstantParameter.Application.FLAG_TRUE);
		            
		            Log.e("UpdateDB", "Ready to update Flag");
		            Log.e("UpdateDB", "Rsvp to be updated = " + params[1]);
		            
		            boolean x = dao.updateDataWithINCondition(ConstantParameter.Reservation.TABLE, ConstantParameter.Reservation.RESERVATION_ID, params[1], dataMap);
		            Log.e("RESULT", "X = " + x);
		            dao.close();
		            
		        } catch (Exception e) {   
		            Log.e("SendMailERROR", e.getMessage());
		            result = "Error in sending Recon Mail";
		        }
				return result;
	    }

	    protected void onPostExecute(String feed) {
	        // TODO: check this.exception 
	        // TODO: do something with the feed
	    }
	}
	
	public String queryNextFMOrder(){
		
		String queryTodaySchedule = "SELECT " + ConstantParameter.Reservation.RESERVATION_ID + ", strftime('%H:%M', "+
				ConstantParameter.Reservation.START_TIME + "), strftime('%H:%M', " +
				ConstantParameter.Reservation.END_TIME + "), " +
				ConstantParameter.Reservation.RESERVED_BY + ", " +
				ConstantParameter.Reservation.PURPOSE + ", " +
				ConstantParameter.Reservation.DESTINATION + "" +
				" FROM " + ConstantParameter.Reservation.TABLE + 
				" WHERE " + ConstantParameter.Reservation.START_TIME + " >? " +
				" AND " + ConstantParameter.Reservation.START_TIME + " <? " +
				" AND " + ConstantParameter.Reservation.APP_NAME + "=?" +
				" ORDER BY " +ConstantParameter.Reservation.START_TIME + " ASC";		

		String query = "SELECT " + ConstantParameter.Reservation.KEY_ROWID + " || '#' || "+ ConstantParameter.Reservation.APP_NAME + " || '#' || " +
		   ConstantParameter.Reservation.RESERVATION_ID + "|| '#' || " +
		   ConstantParameter.Reservation.RESERVED_BY + "|| '#' || " +
		   ConstantParameter.Reservation.DESTINATION + "|| '#' || " + 
		   ConstantParameter.Reservation.PURPOSE + " || '#' || " + 
		   ConstantParameter.Reservation.START_TIME + " || '#' || " + 
		   ConstantParameter.Reservation.END_TIME + " || '###' as RSVP " + 
		   " FROM " + ConstantParameter.Reservation.TABLE +
		   " WHERE " + ConstantParameter.Reservation.IS_ALREADY_DISPLAYED + " =? AND " + 
		   ConstantParameter.Reservation.START_TIME + " <=? AND "+ 
		   ConstantParameter.Reservation.END_TIME + " >=? AND " +
		   ConstantParameter.Reservation.APP_NAME + "=?" +
		   " ORDER BY " + ConstantParameter.Reservation.START_TIME + " ASC";
				
		Dao dao = new Dao(this);
		Date date = new Date();
		String result = "";
		String schedule = "";
		
		String dateBegin = ConstantParameter.Application.shortDateFormat.format(date) + " 00:00";
		String dateEnd   = ConstantParameter.Application.shortDateFormat.format(date) + " 23:59";
		String timeCheck = ConstantParameter.Application.MEETING_ROOM_TIME_BEGIN;
		int i = 0;
						
		dao.open();
		Log.e("DATE START =" , dateBegin);
		Log.e("DATE END =" , dateEnd);
		Log.e("QUERY =" , queryTodaySchedule);
		Cursor x = dao.queryWithRawSQL(queryTodaySchedule, new String[]{dateBegin, dateEnd, ConstantParameter.Application.FM});		
		if(x.moveToFirst()){
			do{
			//  0         1        2          3         4        5
			//RES_ID START_TIME END_TIME RESERVED_BY PURPOSE DESTINATION
			Log.e("QUERY GOT DATA =" , x.toString());
			if(x.getString(1).equals(timeCheck)){
				
				if(schedule.equals("")){
					schedule = createSchedule(x, ConstantParameter.Application.MEETING_ROOM_NOT_AVAILABLE, null, null);				
				} else {
					schedule = schedule + "~" + createSchedule(x, ConstantParameter.Application.MEETING_ROOM_NOT_AVAILABLE, null, null);				
				}
				
				timeCheck = x.getString(2);

			} else {
				
				if(i == 0){
					schedule = createSchedule(x, ConstantParameter.Application.MEETING_ROOM_AVAILABLE, 
									ConstantParameter.Application.MEETING_ROOM_TIME_BEGIN, x.getString(1));
					schedule = schedule + "~"+createSchedule(x, ConstantParameter.Application.MEETING_ROOM_NOT_AVAILABLE,null,null);					
					timeCheck = x.getString(2);	
				} else {
					schedule = schedule + "~"+createSchedule(x, ConstantParameter.Application.MEETING_ROOM_AVAILABLE, 
									timeCheck, x.getString(1));
					schedule = schedule + "~"+createSchedule(x, ConstantParameter.Application.MEETING_ROOM_NOT_AVAILABLE,null,null);					
					timeCheck = x.getString(2);	
				}

			}															
			
			i = 1;
			} while(x.moveToNext());
		}
		
		if(i== 0){
			Log.e("QUERY GOT DATA =" , "NONE");
			schedule = createSchedule(null, ConstantParameter.Application.MEETING_ROOM_AVAILABLE, ConstantParameter.Application.MEETING_ROOM_TIME_BEGIN, ConstantParameter.Application.MEETING_ROOM_TIME_END);
			timeCheck = ConstantParameter.Application.MEETING_ROOM_TIME_END;
		}
		
		Log.e("TIME CHECK = ", timeCheck);
		
		if(!timeCheck.equals(ConstantParameter.Application.MEETING_ROOM_TIME_END)){
			schedule = schedule + "~"+createSchedule(null, ConstantParameter.Application.MEETING_ROOM_AVAILABLE, 
							timeCheck, ConstantParameter.Application.MEETING_ROOM_TIME_END);			
		}
		
		//SCHEDULE FORMAT
		//AVAILABLE#TIME#HIGHLIGHTED~
		//N/A#RES_ID#TIME#RES_BY#PURPOSE#DESTINATION#HIGHLIGHTED~
		dao.close();
		
		String dateNow = ConstantParameter.Application.formatSQLiteDate.format(date);
		
		dao.open();
		Cursor c = dao.queryWithRawSQL(query, new String[]{ConstantParameter.Application.FLAG_FALSE, dateNow, dateNow, ConstantParameter.Application.FM});
		if(c.moveToFirst()){
			result = c.getString(0);
		} else {
			result = ConstantParameter.Application.NONE;
		}
		dao.close();
		
		Log.e("MESSAGE", result + "@" + schedule);
		return result + "@" + schedule;
	}
	
	private String createSchedule(Cursor x, String roomAvailability, String startTime, String endTime){
		String result = "";
			//  0         1        2          3         4        5
			//RES_ID START_TIME END_TIME RESERVED_BY PURPOSE DESTINATION
		Date dateNow = new Date();
		int from = 0;
	    int to = 0;
	    
	    Calendar c = Calendar.getInstance();
	    c.setTime(dateNow);
	    
	    int t = c.get(Calendar.HOUR_OF_DAY) * 100 + c.get(Calendar.MINUTE);
	    boolean isBetween = false;	    
		
		if(roomAvailability.equalsIgnoreCase(ConstantParameter.Application.MEETING_ROOM_AVAILABLE)){
			
			from = Integer.parseInt(startTime.replace(":", ""));
			to = Integer.parseInt(endTime.replace(":", ""));		
			isBetween = to > from && t >= from && t <= to || to < from && (t >= from || t <= to);
						
			result = ConstantParameter.Application.MEETING_ROOM_AVAILABLE+"#"+ startTime + " - " + endTime;
			
		} else {
			
			from = Integer.parseInt(x.getString(1).replace(":", ""));
			to = Integer.parseInt(x.getString(2).replace(":", ""));		
			isBetween = to > from && t >= from && t <= to || to < from && (t >= from || t <= to);
			
			result = ConstantParameter.Application.MEETING_ROOM_NOT_AVAILABLE+"#"+
					 x.getString(0)+"#"+x.getString(1) + " - " + x.getString(2) + "#" + x.getString(3) +
					 "#" + x.getString(4) + "#" + x.getString(5);					 
		}				
		
		
			result = result + "#" + (isBetween?ConstantParameter.Application.HIGHLIGHTED:ConstantParameter.Application.NONE);
		
		
		return result;
	}
	
	public void processFMOrder(String order){
		
		Log.e("FMOrderAutoExec", "Proceed with = " + order);				
		Intent intent = new Intent(this, QuickSurveyActivity.class);
		
		String rowId = order.substring(0,order.indexOf("#"));
		String fmOrder = order.substring(order.indexOf("#")+1);
		   
		intent.putExtra(ConstantParameter.Application.MSG_BODY, fmOrder);
		intent.putExtra(ConstantParameter.Application.APP_NAME, ConstantParameter.Application.FM);
		intent.putExtra(ConstantParameter.Reservation.KEY_ROWID, rowId);
		
		
		
    	startActivity(intent);
	}
	
	public String setModeOn(String mode){
		Dao dao = new Dao(this);
		
		
		HashMap<String, String> mapData = new HashMap<String, String>();
		mapData.put(ConstantParameter.DeviceBoard.DEVICE_ID, mode+"001");
		mapData.put(ConstantParameter.DeviceBoard.MSISDN, "081286041588");
		mapData.put(ConstantParameter.DeviceBoard.APK_ID, "Version 0.1");
		
		
		dao.open();
		dao.deleteAllData(ConstantParameter.DeviceBoard.TABLE);
		
		if(mode.equalsIgnoreCase(ConstantParameter.Application.FM)){			
			mapData.put(ConstantParameter.DeviceBoard.MOUNTED_TO, "ROOM 1");
			mapData.put(ConstantParameter.DeviceBoard.APP_NAME, ConstantParameter.Application.FM);			
		} else {
			mapData.put(ConstantParameter.DeviceBoard.MOUNTED_TO, "CAR 1");
			mapData.put(ConstantParameter.DeviceBoard.APP_NAME, ConstantParameter.Application.CAROL);
		}
		
		long id = dao.insertData(ConstantParameter.DeviceBoard.TABLE, mapData);
		dao.close();
		
		return String.valueOf(id);
	}
	
	public void runAngryBirds(){
		String pm = "com.rovio.angrybirds";
		Intent LaunchApp = getPackageManager().getLaunchIntentForPackage(pm);
		startActivity( LaunchApp );
		try {
			Thread.sleep(10000L);
			callHome(pm);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			callHome(pm);
		}
		
	}
	
	@SuppressLint("NewApi")
	private void callHome(String pm){
		Log.e("CALL HOME", "PREPARE CALL");
		//int pid = getAppPidbyName(pm);
		//if(pid > 0){
			//Log.e("TRY KILLING PID", "" +pid);
			ActivityManager am = (ActivityManager)this.getSystemService(ACTIVITY_SERVICE);
			am.moveTaskToFront(this.getTaskId(), ActivityManager.MOVE_TASK_WITH_HOME);			
			//android.os.Process.killProcess(pid);
			Log.e("TRY KILLING PID", "PID KILLED");
		//}
		Log.e("CALL HOME", "HOME RUN");
	}
	
	private int getAppPidbyName(String packnam){
			Log.e("CALL PID", "SEARCH PID");	    
		    ActivityManager am = (ActivityManager)this.getSystemService(ACTIVITY_SERVICE);
		    List l = am.getRunningAppProcesses();
		    Iterator i = l.iterator();
		    PackageManager pm = this.getPackageManager();
		    while(i.hasNext()) 
		    {
		          ActivityManager.RunningAppProcessInfo info = (ActivityManager.RunningAppProcessInfo)(i.next());
		          try 
		          { 
		              if(info.processName.equalsIgnoreCase(packnam))
		              {
		            	 Log.e("CALL PID", "FOUND PID = " + info.pid); 
		                 return info.pid;
		              }
		          }
		          catch(Exception e) 
		          {
		                Log.e("Process", "Error>> :"+ e.toString());
		                e.printStackTrace();
		          }
		   }
		    
		    return 0;
		
	}
	
	
	 public void insertFMData(String message){
			
	        //Parse Msg Body
	      	String[] parsedOrder = message.split("#");
	      	String[] fields = ConstantParameter.Reservation.insertStatementFields;
	      	HashMap<String,String> mapDataRsv = new HashMap<String, String>();
	      	long reservationId = 0L;
	      	
	      	Dao dao = new Dao(this);
	      	
	      	Log.e("RSVP DATA ", message);
	      	
	      	//    0             1              2            3          4          5         6           7           8                9
	      	//{APP_NAME, RESERVATION_ID , RESERVED_BY, DESTINATION, PURPOSE, START_TIME, END_TIME, DRIVER_NAME, PLATE_NUMBER, REMARKS/FLAG};
	      	for (int i = 0; i < parsedOrder.length; i++) {
	      		if(i == 5 || i == 6){
	      			//Date date;
					//try {
						//date = ConstantParameter.Application.transformDateFormat.parse(parsedOrder[i]);
						//mapDataRsv.put(fields[i], ConstantParameter.Application.formatSQLiteDate.format(date));
						mapDataRsv.put(fields[i], parsedOrder[i]);
					//} catch (ParseException e) {
					//	Log.e("PARSE DATE ERROR", e.getMessage());
					//}      			
	      		} else {
	      			mapDataRsv.put(fields[i], parsedOrder[i]);
	      		}	
				Log.e(fields[i], parsedOrder[i]);
				
			}      	        
			
			//Reconciliation Attribute
			Date receivedDate = new Date();
			mapDataRsv.put(ConstantParameter.Reservation.RESERVATION_RECEIVED_DATE, ConstantParameter.Application.formatSQLiteDate.format(receivedDate));
			Log.e(ConstantParameter.Reservation.RESERVATION_RECEIVED_DATE, ConstantParameter.Application.formatSQLiteDate.format(receivedDate));
										
	      	dao.open();
	      	
	      	//Check reservation id
	      	if(mapDataRsv.get(ConstantParameter.Reservation.REMARKS)!=null && mapDataRsv.get(ConstantParameter.Reservation.REMARKS).equals(ConstantParameter.Reservation.REMARKS_VALUE_UPDATE)){
	      		//UPDATE SCHEDULE
	      		boolean isUpdated = dao.updateDataByCondition(ConstantParameter.Reservation.TABLE, ConstantParameter.Reservation.RESERVATION_ID + " = ? ", new String[]{mapDataRsv.get(ConstantParameter.Reservation.RESERVATION_ID)}, mapDataRsv);      				      	          	
	    		Log.e("UPDATE RSVP", "IS UPDATED = "+ isUpdated);      		
	      	} else if (mapDataRsv.get(ConstantParameter.Reservation.REMARKS)!=null && mapDataRsv.get(ConstantParameter.Reservation.REMARKS).equals(ConstantParameter.Reservation.REMARKS_VALUE_INSERT)){
	      		//NEW ORDER
	      		
	      		//NOT Directly display the survey page
	    		mapDataRsv.put(ConstantParameter.Reservation.IS_ALREADY_DISPLAYED, ConstantParameter.Application.FLAG_FALSE);
	    		Log.e(ConstantParameter.Reservation.IS_ALREADY_DISPLAYED, ConstantParameter.Application.FLAG_FALSE);
	    		
	    		//Reconciliation Attribute
	    		mapDataRsv.put(ConstantParameter.Reservation.IS_ALREADY_REPORTED, ConstantParameter.Application.FLAG_FALSE);
	    		Log.e(ConstantParameter.Reservation.IS_ALREADY_REPORTED, ConstantParameter.Application.FLAG_FALSE);
	    		
	      		reservationId = dao.insertData(ConstantParameter.Reservation.TABLE, mapDataRsv);      	          	
	    		Log.e("INSERT RSVP", "C = "+reservationId);
	      	} else if (mapDataRsv.get(ConstantParameter.Reservation.REMARKS)!=null && mapDataRsv.get(ConstantParameter.Reservation.REMARKS).equals(ConstantParameter.Reservation.REMARKS_VALUE_DELETE)){
	      		//DELETE ORDER
	      		int i = dao.deleteDataWithCondition(ConstantParameter.Reservation.TABLE, ConstantParameter.Reservation.RESERVATION_ID + " =?", new String[]{mapDataRsv.get(ConstantParameter.Reservation.RESERVATION_ID)});
	      		Log.e("DELETE RSVP", "N = "+i);
	      	}
	      	
	      	
	      	
	      					
			
			dao.close();								
		}
}
