package com.example.task;

import java.text.SimpleDateFormat;

public class ConstantParameter {

	public static final class Reservation{
		
		public static final String  TABLE = "RESERVATION";
		
		public static final String  KEY_ROWID = "_ID";		
		public static final String  RESERVATION_ID = "RESERVATION_ID";
		public static final String  APP_NAME = "APP_NAME";
		public static final String  RESERVED_BY = "RESERVED_BY";		
		public static final String  PLATE_NUMBER = "PLATE_NUMBER";
		public static final String  DRIVER_NAME = "DRIVER_NAME";	
		public static final String  DESTINATION = "DESTINATION"; //MEETING ROOM IN FM
		public static final String  PURPOSE = "PURPOSE"; //DEPARTMENT ON FM					
		public static final String  START_TIME = "START_TIME";
		public static final String  END_TIME = "END_TIME";		
		
		public static final String REMARKS = "REMARKS";
		
		//FLAG VALUE FOR UPDATE
		public static final String REMARKS_VALUE_UPDATE = "U";
		public static final String REMARKS_VALUE_INSERT = "I";
		public static final String REMARKS_VALUE_DELETE = "D";
		public static final String REMARKS_VALUE_NO_UPDATE = "N";				
		
		public static final String  IS_ALREADY_DISPLAYED = "IS_ALREADY_DISPLAYED";
		public static final String  IS_ALREADY_REPORTED = "IS_ALREADY_REPORTED";
		public static final String  RESERVATION_RECEIVED_DATE = "RESERVATION_RECEIVED_DATE";
		
		//ordered carol  fields for query
	    public static final String[] carolFieldsOnQuery = {KEY_ROWID, RESERVATION_ID ,APP_NAME, RESERVED_BY, PLATE_NUMBER, DRIVER_NAME, DESTINATION, PURPOSE, REMARKS};
	    
	    //ordered FM fields for query
	    public static final String[] fmFieldsOnQuery = {KEY_ROWID, RESERVATION_ID ,APP_NAME, RESERVED_BY, START_TIME, END_TIME, PURPOSE, REMARKS, DESTINATION};
	    
	    //ordered all fields
	    public static final String[] queryAllFields = {KEY_ROWID, APP_NAME, RESERVATION_ID , RESERVED_BY, DESTINATION, PURPOSE, START_TIME, END_TIME, DRIVER_NAME, PLATE_NUMBER, IS_ALREADY_DISPLAYED, IS_ALREADY_REPORTED, RESERVATION_RECEIVED_DATE, REMARKS};
	    
	    //ordered insert statement fields for query
	    public static final String[] insertStatementFields = {APP_NAME, RESERVATION_ID , RESERVED_BY, DESTINATION, PURPOSE, START_TIME, END_TIME, DRIVER_NAME, PLATE_NUMBER, REMARKS};
	    
	  //------------------------TABLE RESERVATION ORDER-----------------------------//
	    public static final String TABLE_RESERVATION_CREATE =
	            "create table "+ TABLE +" ("+ KEY_ROWID +" integer primary key autoincrement, "
	            + RESERVATION_ID + " text not null, "
	            + APP_NAME +" text not null, "
	            + RESERVED_BY +" text not null, " 
	            + PLATE_NUMBER + " text, "
	            + DRIVER_NAME +" text, " 
	            + DESTINATION +" text, " 
	            + PURPOSE + " text,"
	            + REMARKS + " text,"
	            + START_TIME + " text,"
	            + END_TIME + " text,"	    
	            + IS_ALREADY_DISPLAYED + " text, "
	            + IS_ALREADY_REPORTED + " text, "
	            + RESERVATION_RECEIVED_DATE + " text "
	            + ");";	    	    
	}
					
	public static final class FeedbackCriteria{		
		public static final String  TABLE = "FEEDBACK_CRITERIA";
	    public static final String  KEY_ROWID = "_ID";
	    public static final String  APP_NAME = "APP_NAME";
	    public static final String  CRITERIA_ORDER_NUMBER = "CRITERIA_ORDER_NUMBER";
	    public static final String  CRITERIA_DESC = "CRITERIA_DESC";
	    
	    
	  //order fields for query
	    public static final String[] fieldsOnQuery = {KEY_ROWID, APP_NAME, CRITERIA_ORDER_NUMBER, CRITERIA_DESC};
	    
	  //------------------------TABLE FEEDBACK CRITERIA-----------------------------//    
	    public static final String TABLE_FEEDBACK_CRITERIA_CREATE =
	            "create table "+ TABLE +" ("+ KEY_ROWID +" integer primary key autoincrement, "
	            + APP_NAME +" text not null, "
	            + CRITERIA_ORDER_NUMBER +" text, "
	            + CRITERIA_DESC +" text not null);";
	}
	
	public static final class FeedbackResult{
		public static final String  TABLE = "FEEDBACK";
	    public static final String  KEY_ROWID = "_ID";
	    public static final String  RESERVATION_ID = "RESERVATION_ID";	    
		public static final String  FEEDBACK_CRITERIA_ID ="FEEDBACK_CRITERIA_ID";
		public static final String  FEEDBACK_RATE_ID ="FEEDBACK_RATE_ID";
		public static final String  REMARKS ="REMARKS";
		
		//order fields for query
	    public static final String[] fieldsOnQuery = {KEY_ROWID, RESERVATION_ID, FEEDBACK_CRITERIA_ID, FEEDBACK_RATE_ID, REMARKS};
	    
	  //------------------------TABLE FEEDBACK RESULT-----------------------------//        
	    public static final String TABLE_FEEDBACK_CREATE =
	            "create table "+ TABLE +" ("+ KEY_ROWID +" integer primary key autoincrement, "
	            + RESERVATION_ID +" text not null, "
	            + FEEDBACK_CRITERIA_ID +" text, "
	            + FEEDBACK_RATE_ID + " text, "
	            + REMARKS + " text "
	            +");";
	}
	
	public static final class FeedbackRate{
		public static final String  TABLE = "FEEDBACK_RATE";
	    public static final String  KEY_ROWID = "_ID";
	    public static final String  RATE_NAME = "RATE_NAME";	    
		public static final String  RATE_DESCRIPTION ="RATE_DESCRIPTION";
		public static final String  IMAGE_PATH ="IMAGE_PATH";		
		
		//order fields for query
	    public static final String[] fieldsOnQuery = {KEY_ROWID, RATE_NAME, RATE_DESCRIPTION, IMAGE_PATH};
	    
	    //------------------------TABLE FEEDBACK RATE-----------------------------//  	    
		public static final String TABLE_FEEDBACK_RATE_CREATE =
	            "create table "+ TABLE +" ("+ KEY_ROWID +" integer primary key autoincrement, "
	            + RATE_NAME +" text not null, "
	            + RATE_DESCRIPTION +" text not null, "
	            + IMAGE_PATH + " text not null "	            
	            +");";
	}
	
	public static final class DeviceBoard{
		public static final String  TABLE = "DEVICE_BOARD";	    
	    public static final String  DEVICE_ID = "DEVICE_ID";	    
		public static final String  MOUNTED_TO ="MOUNTED_TO";
		public static final String  MSISDN = "MSISDN";
		public static final String  APK_ID = "APK_ID";
		public static final String  APP_NAME = "APP_NAME";
		
		//order fields for query
	    public static final String[] fieldsOnQuery = {DEVICE_ID, MOUNTED_TO, MSISDN, APK_ID, APP_NAME};
	    
	  //------------------------TABLE DEVICE BOARD-----------------------------//  	    
	  public static final String TABLE_DEVICE_BOARD_CREATE =
	  	            "create table "+ TABLE +" ("+ DEVICE_ID +" text, "
	  	            + MOUNTED_TO +" text, "
	  	            + MSISDN +" text, "
	  	            + APK_ID + " text, "	           
	  	            + APP_NAME + " text"	           
	  	            +");";
	}
	
	
	public static final class Application {
		public static final String  CAROL ="CAROLS";
		public static final String  FM ="FM";
		public static final String  MSG_SENDER = "MSG_SENDER";
		public static final String  MSG_BODY = "MSG_BODY";
		public static final String  APP_NAME = "APP_NAME";
		public static final String  RES_SUGGESTION = "SARAN";
		public static final String  FLAG_FALSE = "FALSE";
		public static final String  FLAG_TRUE = "TRUE";
		public static final String IS_DEFAULTED = "DEFAULTED";
		
		public static final SimpleDateFormat formatSQLiteDate = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	    public static final SimpleDateFormat shortTimeFormat = new SimpleDateFormat("HH:mm");
	    public static final SimpleDateFormat shortDateFormat = new SimpleDateFormat("yyyy-MM-dd");
	    public static final SimpleDateFormat transformDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
	    
		
	    public static final String VERY_DISAPPOINTED = "Very Disappointed";
	    public static final String DISAPPOINTED      = "Disappointed";
	    public static final String NEUTRAL           = "Neutral";
	    public static final String SATISFIED = "Satisfied";
	    public static final String VERY_SATISFIED = "Very Satisfied";
	    
	    public static final String VERY_DISAPPOINTED_RATE 	= "1";
	    public static final String DISAPPOINTED_RATE      	= "2";
	    public static final String NEUTRAL_RATE           	= "3";
	    public static final String SATISFIED_RATE 			= "4";
	    public static final String VERY_SATISFIED_RATE 		= "5";
	    
	    public static final String ALARM_BEEPING = "BEEP";
	    public static final String AUTO_FINISH = "FINISH";
	    public static final String NONE = "NONE";
	    
	    public static final String MEETING_ROOM_TIME_BEGIN = "08:00";
	    public static final String MEETING_ROOM_TIME_END = "17:00";
	    public static final String MEETING_ROOM_AVAILABLE = "AVAILABLE";
	    public static final String MEETING_ROOM_NOT_AVAILABLE = "N/A";
	    public static final String HIGHLIGHTED = "HIGHLIGHTED";
	}
	
	
    
  
    
    
    
  
	
}
