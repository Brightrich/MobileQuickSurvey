package com.example.task;

import java.text.ParseException;
import java.util.Date;
import java.util.Locale;

import org.apache.cordova.DroidGap;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.sax.StartElementListener;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.Engine;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.webkit.WebView;
import android.widget.Toast;

public class QuickSurveyPageAttribute {

	  private WebView mAppView;
	  private DroidGap mGap;
	  private long orderId;
	  private String appName;
	  private String voteData;
	  private String suggestion;
	  private String feedbackRate;
	  private String waitIntervalOnSurveyPage;
	 

	  public QuickSurveyPageAttribute(DroidGap gap, WebView view, long orderId, String appName)
	  {
	    mAppView = view;
	    mGap = gap;
	    this.orderId = orderId;
	    this.appName = appName;
	  }	  	
	  
	  
	  
	  public String getOrderData(){
		    String[] fields = null;
		    Cursor c = null;
		    String orderData="";
			Dao dao = new Dao(mGap);
			dao.open();
			int endMinute=0, startMinute=0; 
						
			fields = appName.equalsIgnoreCase(ConstantParameter.Application.CAROL)?ConstantParameter.Reservation.carolFieldsOnQuery:ConstantParameter.Reservation.fmFieldsOnQuery;							
			c =dao.getSpecificDataById(ConstantParameter.Reservation.TABLE, fields, ConstantParameter.Reservation.KEY_ROWID, orderId);
									
			//CAROL = {KEY_ROWID, RESERVATION_ID ,APP_NAME, RESERVED_BY, PLATE_NUMBER, DRIVER_NAME, DESTINATION, PURPOSE}
			//FM = KEY_ROWID, RESERVATION_ID ,APP_NAME, RESERVED_BY, START_TIME, END_TIME, PURPOSE, REMARKS, DESTINATION
			//		    
			if(c.moveToFirst()){
				do{
					
					//orderData = c.getString(0) + "@" + c.getString(1)+ "@"+c.getString(2) + "@" + c.getString(3)+ "@"+c.getString(4) + "@" + c.getString(5)+ "@"+c.getString(6)+ "@"+c.getString(7);
					for(int x=0; x < fields.length; x++){
						if(x < 1){
							orderData = c.getString(x);
						} else {
							if(appName.equals(ConstantParameter.Application.FM) && (x == 4||x==5)){
								
								try {
									Log.e("Parsed Date", "Start index " + x);
									Date date = ConstantParameter.Application.formatSQLiteDate.parse(c.getString(x));
									String shortDate = ConstantParameter.Application.shortTimeFormat.format(date);
									Log.e("Parsed Date End", "Short Date = " + shortDate);
									
									if(x==4){
										String[] timeArr = shortDate.split(":");
										startMinute = (Integer.parseInt(timeArr[0])*60) + Integer.parseInt(timeArr[1]);										
									} else if(x==5){
										String[] timeArr = shortDate.split(":");
										endMinute = (Integer.parseInt(timeArr[0])*60) + Integer.parseInt(timeArr[1]);
									}
									
									setWaitIntervalOnSurveyPage(String.valueOf((endMinute - startMinute)*60*1000));
									orderData = orderData + "@" + shortDate;
								} catch (ParseException e) {
									Log.e("Parsed Date ERROR", e.getMessage());
									orderData = orderData + "@" + c.getString(x);
								}								
								
							} else {
							   orderData = orderData + "@" + c.getString(x);
							}
						}						
					}
						
					
				} while(c.moveToNext());
			}
			dao.close();
			
			return orderData;
	  }
	  
	  public String getQuestionnaireData(){
			String[] fields;
			Dao dao = new Dao(mGap);
			dao.open();
			
			fields = ConstantParameter.FeedbackCriteria.fieldsOnQuery;						
			Cursor c =dao.getAllDataByCondition(ConstantParameter.FeedbackCriteria.TABLE, fields, ConstantParameter.FeedbackCriteria.APP_NAME + "=?", new String[] {appName}, ConstantParameter.FeedbackCriteria.CRITERIA_ORDER_NUMBER + " ASC");
			
			String questionnaireResult="";
			
			if(c.moveToFirst()){
				do{
					if(questionnaireResult.equals("")){						
						//{KEY_ROWID, APP_NAME, CRITERIA_ORDER_NUMBER, CRITERIA_DESC}
						questionnaireResult = c.getString(0)+"@"+c.getString(1)+"@"+c.getString(2)+"@"+c.getString(3);
					} else {
						questionnaireResult = questionnaireResult + "#" + c.getString(0)+"@"+c.getString(1)+"@"+c.getString(2)+"@"+c.getString(3);
					}
				} while(c.moveToNext());
			}
			
			dao.close();
			
			return questionnaireResult;
		}
	  
	  
	  	//SETTERS & GETTERS
		public void setVoteData(String data){		  
			this.voteData = data;
		}

		public long getOrderId() {
			return orderId;
		}
	
		public void setOrderId(long orderId) {
			this.orderId = orderId;
		}
	
		public String getAppName() {
			return appName;
		}
	
		public void setAppName(String appName) {
			this.appName = appName;
		}
	
		public String getSuggestion() {
			return suggestion;
		}
	
		public void setSuggestion(String suggestion) {
			this.suggestion = suggestion;
		}
	
		public String getVoteData() {
			return voteData;
		}
						
		public String getFeedbackRate() {
			return feedbackRate;
		}

		public void setFeedbackRate(String feedbackRate) {
			this.feedbackRate = feedbackRate;
		}				

		public String getWaitIntervalOnSurveyPage() {
			return waitIntervalOnSurveyPage;
		}

		public void setWaitIntervalOnSurveyPage(String waitIntervalOnSurveyPage) {
			this.waitIntervalOnSurveyPage = waitIntervalOnSurveyPage;
		}

		public String queryFeedbackRate(){
			String[] fields;
			Dao dao = new Dao(mGap);
			dao.open();
			
			fields = ConstantParameter.FeedbackRate.fieldsOnQuery;						
			Cursor c =dao.getAllData(ConstantParameter.FeedbackRate.TABLE, fields, ConstantParameter.FeedbackRate.RATE_NAME + " ASC");
			
			String rateResult="";
			
			if(c.moveToFirst()){
				do{
					if(rateResult.equals("")){						
						//{KEY_ROWID, RATE_NAME, RATE_DESCRIPTION, IMAGE_PATH}
						rateResult = c.getString(0)+"@"+c.getString(1)+"@"+c.getString(2)+"@"+c.getString(3);
					} else {
						rateResult = rateResult + "#" + c.getString(0)+"@"+c.getString(1)+"@"+c.getString(2)+"@"+c.getString(3);
					}
				} while(c.moveToNext());
			}
			
			dao.close();
			
			
			setFeedbackRate(rateResult);
			return rateResult;
		}				
	  
	    public String getAllCAROLDATA(){
	    	Dao dao = new Dao(mGap);
			dao.open();
			
			Cursor c  = dao.getAllData(ConstantParameter.Reservation.TABLE, ConstantParameter.Reservation.carolFieldsOnQuery, ConstantParameter.Reservation.KEY_ROWID + " ASC");
			
			String carol="";
			
			if(c.moveToFirst()){
				do{
					if(carol.equals("")){
						
						//CAROL = {KEY_ROWID, RESERVATION_ID ,APP_NAME, RESERVED_BY, PLATE_NUMBER, DRIVER_NAME, DESTINATION, PURPOSE}
						carol = c.getString(0)+"@"+c.getString(1)+"@"+c.getString(2)+"@"+c.getString(3)+"@"+c.getString(4)+"@"+c.getString(5)+"@"+c.getString(6)+"@"+c.getString(7);
					} else {
						carol = carol + "#" + c.getString(0)+"@"+c.getString(1)+"@"+c.getString(2)+"@"+c.getString(3)+"@"+c.getString(4)+"@"+c.getString(5)+"@"+c.getString(6)+"@"+c.getString(7);
					}
				} while(c.moveToNext());
			}
			
			Cursor b  = dao.getAllData(ConstantParameter.FeedbackResult.TABLE, ConstantParameter.FeedbackResult.fieldsOnQuery, ConstantParameter.FeedbackResult.KEY_ROWID + " ASC");
			
			String survey="";
			
			if(b.moveToFirst()){
				do{
					if(survey.equals("")){
						
						//{KEY_ROWID, RESERVATION_ID, FEEDBACK_CRITERIA_ID, FEEDBACK_RATE_ID, REMARKS}						
						survey = b.getString(0)+"@"+b.getString(1)+"@"+b.getString(2)+"@"+b.getString(3)+"@"+b.getString(4);
					} else {
						survey = survey + "#" + b.getString(0)+"@"+b.getString(1)+"@"+b.getString(2)+"@"+b.getString(3)+"@"+b.getString(4);
					}
				} while(b.moveToNext());
			}
			
			dao.close();
			
			carol = carol +"%%%%%%%"+ survey;
			return carol;
	    }	    
	  
	    public String queryFMOrderState(String orderId){
	    	Log.e("Query Order by order Id", orderId);
	    	Dao dao = new Dao(mGap);
			dao.open();
			Cursor c = dao.getAllDataByCondition(ConstantParameter.Reservation.TABLE, ConstantParameter.Reservation.fmFieldsOnQuery, ConstantParameter.Reservation.RESERVATION_ID + " =?", new String[]{orderId}, null);
			String order = ConstantParameter.Reservation.REMARKS_VALUE_DELETE;
			int timeLeftInMilis = 0;
			//String flagEnd = ConstantParameter.Application.NONE;
			
			if(c.moveToFirst()){
				do{
						
						try {
							Date endDate = ConstantParameter.Application.formatSQLiteDate.parse(c.getString(5));
							Date dateNow = new Date();
							long diff = endDate.getTime() - dateNow.getTime();
							timeLeftInMilis = (int) diff;
							
							if(timeLeftInMilis < 0) {
								timeLeftInMilis = 0;
							}
							
							/*String endDateStr = ConstantParameter.Application.shortTimeFormat.format(endDate);
							String nowDateStr = ConstantParameter.Application.shortTimeFormat.format(dateNow);
							boolean isTimeout =  endDateStr.equalsIgnoreCase(nowDateStr);
							if(isTimeout){
								flagEnd = ConstantParameter.Application.AUTO_FINISH;
							} else if(diffMinutes <= 10){
								flagEnd = ConstantParameter.Application.ALARM_BEEPING;
							}*/
							
							
						} catch (ParseException e) {							
							e.printStackTrace();
						}
					
						//    0             1           2           3           4          5         6        7         8
						//KEY_ROWID, RESERVATION_ID ,APP_NAME, RESERVED_BY, START_TIME, END_TIME, PURPOSE, REMARKS, DESTINATION
						if(c.getString(7).equalsIgnoreCase(ConstantParameter.Reservation.REMARKS_VALUE_UPDATE)){
							order = c.getString(0)+"@"+c.getString(1)+"@"+c.getString(2)+"@"+c.getString(3)+"@"+c.getString(4)+
									c.getString(5)+"@"+c.getString(6)+"@"+c.getString(7)+"@"+c.getString(8);
						} else {
							order = ConstantParameter.Reservation.REMARKS_VALUE_NO_UPDATE;
						}
												
				} while(c.moveToNext());
			}
			
			order = timeLeftInMilis + "~" + order;
			dao.close();
			Log.e("Query Order by order Id", order);
	    	return order;
	    }
	    
}
