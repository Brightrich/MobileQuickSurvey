package com.example.task;



import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

public class SmsListener extends BroadcastReceiver{

    private SharedPreferences preferences;

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub
    	String msgBody="", msg_from = "";
    	
        if(intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")){
            Bundle bundle = intent.getExtras();           //---get the SMS message passed in---
            SmsMessage[] msgs = null;            
            if (bundle != null){
                //---retrieve the SMS message received---
                try{
                    Object[] pdus = (Object[]) bundle.get("pdus");
                    msgs = new SmsMessage[pdus.length];
                    for(int i=0; i<msgs.length; i++){
                        msgs[i] = SmsMessage.createFromPdu((byte[])pdus[i]);
                        msg_from = msgs[i].getOriginatingAddress();
                        
                        msgBody = msgs[i].getMessageBody();
                    }
                }catch(Exception e){
//                            Log.d("Exception caught",e.getMessage());
                }
            }
        }
        
        
        /*if(!msg_from.equals("+6281286041588")){
        	abortBroadcast();
        	deleteSMS(context, "+6281286041588");
        } else {*/
            
            //abortBroadcast();
            //deleteSMS(context, "+6281286041588");
            
        	//CHECK SENDER ANGELINA OR ELSE ABORT BROADCAST
        
	        Intent msgIntent = new Intent(context,QuickSurveyActivity.class); 
	        
	        Log.e("SMS LISTENER", "SENDER = "+ msg_from);
	        Log.e("SMS LISTENER", "MESSAGE = "+ msgBody);
	        
	        
	        if(msgBody.toLowerCase().startsWith(ConstantParameter.Application.CAROL.toLowerCase())){
	        	msgIntent.putExtra(ConstantParameter.Application.APP_NAME, ConstantParameter.Application.CAROL);
	        	msgIntent.putExtra(ConstantParameter.Application.MSG_SENDER, msg_from); 
		        msgIntent.putExtra(ConstantParameter.Application.MSG_BODY, msgBody);  
		        msgIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		        msgIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		        context.startActivity(msgIntent);		        
			} else if(msgBody.toLowerCase().startsWith(ConstantParameter.Application.FM.toLowerCase())){
				insertFMData(msgBody, context);
			} 
				
	        //abortBroadcast();					        	      
    }
    
    public void insertFMData(String message, Context ctx){
		
        //Parse Msg Body
      	String[] parsedOrder = message.split("#");
      	String[] fields = ConstantParameter.Reservation.insertStatementFields;
      	HashMap<String,String> mapDataRsv = new HashMap<String, String>();
      	long reservationId = 0L;
      	
      	Dao dao = new Dao(ctx);
      	
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
    
    public void deleteSMS(Context context, String number) {
        try {
            //mLogger.logInfo("Deleting SMS from inbox");
            Uri uriSms = Uri.parse("content://sms/inbox");
            Cursor c = context.getContentResolver().query(uriSms,
                new String[] { "_id", "thread_id", "address",
                    "person", "date", "body" }, null, null, null);

            if (c != null && c.moveToFirst()) {
                do {
                    long id = c.getLong(0);
                    long threadId = c.getLong(1);
                    String address = c.getString(2);
                    String body = c.getString(5);

                    if (!address.equals(number)) {
                      //  mLogger.logInfo("Deleting SMS with id: " + threadId);
                        context.getContentResolver().delete(
                            Uri.parse("content://sms/" + id), null, null);
                    }
                } while (c.moveToNext());
            }
        } catch (Exception e) {
           // mLogger.logError("Could not delete SMS from inbox: " + e.getMessage());
        }
    }
}
