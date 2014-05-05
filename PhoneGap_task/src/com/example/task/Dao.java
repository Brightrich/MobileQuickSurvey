package com.example.task;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


public class Dao {

	//------------------------DB CONFIG------------------------------------//
	private static final String DATABASE_NAME = "quicksurveydb";    
    private static final int DATABASE_VERSION = 1;
    private static final String TAG = "DAO";
			
    //===========================================================================//  
    
    private final Context context; 
    
    private DatabaseHelper DBHelper;
    private SQLiteDatabase db;

    public Dao(Context ctx) 
    {
        this.context = ctx;
        DBHelper = new DatabaseHelper(context);
    }  
 
    private static class DatabaseHelper extends SQLiteOpenHelper 
    {
        DatabaseHelper(Context context) 
        {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) 
        {
            db.execSQL(ConstantParameter.Reservation.TABLE_RESERVATION_CREATE);
            db.execSQL(ConstantParameter.FeedbackResult.TABLE_FEEDBACK_CREATE);
            db.execSQL(ConstantParameter.FeedbackCriteria.TABLE_FEEDBACK_CRITERIA_CREATE);
            db.execSQL(ConstantParameter.FeedbackRate.TABLE_FEEDBACK_RATE_CREATE);
            db.execSQL(ConstantParameter.DeviceBoard.TABLE_DEVICE_BOARD_CREATE);
            
            createInitData(db);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, 
        int newVersion) 
        {
            Log.w(TAG, "Upgrading database from version " + oldVersion 
                    + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS " + ConstantParameter.Reservation.TABLE);
            db.execSQL("DROP TABLE IF EXISTS " + ConstantParameter.FeedbackResult.TABLE);
            db.execSQL("DROP TABLE IF EXISTS " + ConstantParameter.FeedbackCriteria.TABLE);
            db.execSQL("DROP TABLE IF EXISTS " + ConstantParameter.FeedbackRate.TABLE);
            db.execSQL("DROP TABLE IF EXISTS " + ConstantParameter.DeviceBoard.TABLE);
            onCreate(db);
        }
        
        
        public void createInitData(SQLiteDatabase db){
    		int isDeleted=0;
    		HashMap<String,String> mapData = null;
    		HashMap<String,String> mapRate=null;
    		
    		String[] dataC;
    		String[] dataF;
    		
    		
    		//if(appName.equals(ConstantParameter.Application.CAROL)){
    			dataC = new String[]{"Attitude supir?", "Kenyamanan berkendara?", "Kebersihan kendaraan?"};
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
    		
    		
    		isDeleted = db.delete(ConstantParameter.FeedbackCriteria.TABLE,"1",null);
    		isDeleted = db.delete(ConstantParameter.Reservation.TABLE, "1", null);
    		isDeleted = db.delete(ConstantParameter.FeedbackResult.TABLE,"1",null);
    		isDeleted = db.delete(ConstantParameter.FeedbackRate.TABLE, "1", null);
    		Log.e("CLEAR INIT DATA", "status = "+isDeleted);
    		
    		//init questionnaire CAROL data
    		int number = 0;
    		for(int i=0; i < 3; i++){
    			number = i +1;
    			mapData = new HashMap<String, String>();
    			mapData.put(ConstantParameter.FeedbackCriteria.CRITERIA_DESC, dataC[i]);								
    			mapData.put(ConstantParameter.FeedbackCriteria.APP_NAME, ConstantParameter.Application.CAROL);
    			mapData.put(ConstantParameter.FeedbackCriteria.CRITERIA_ORDER_NUMBER, String.valueOf(number));
    			
    			Log.e("INSERT QUESTIONNAIRE", "Q = "+dataC[i]);
    			
    			ContentValues initialValues = new ContentValues();
    	        
    	        Iterator it = mapData.entrySet().iterator();
    	        while (it.hasNext()) {
    	            Map.Entry pairs = (Map.Entry)it.next();
    	            System.out.println(pairs.getKey() + " = " + pairs.getValue());
    	            initialValues.put(pairs.getKey().toString(), pairs.getValue().toString());
    	            it.remove(); // avoids a ConcurrentModificationException
    	        }
    			
    			db.insert(ConstantParameter.FeedbackCriteria.TABLE, null, initialValues);    			
    		}
    		
    		//init questionnaire FM data
    				int numbers = 0;
    				for(int i=0; i < 3; i++){
    					numbers = i +1;
    					mapData = new HashMap<String, String>();
    					mapData.put(ConstantParameter.FeedbackCriteria.CRITERIA_DESC, dataF[i]);								
    					mapData.put(ConstantParameter.FeedbackCriteria.APP_NAME, ConstantParameter.Application.FM);
    					mapData.put(ConstantParameter.FeedbackCriteria.CRITERIA_ORDER_NUMBER, String.valueOf(numbers));
    					
    					ContentValues initialValues = new ContentValues();
    	    	        
    	    	        Iterator it = mapData.entrySet().iterator();
    	    	        while (it.hasNext()) {
    	    	            Map.Entry pairs = (Map.Entry)it.next();
    	    	            System.out.println(pairs.getKey() + " = " + pairs.getValue());
    	    	            initialValues.put(pairs.getKey().toString(), pairs.getValue().toString());
    	    	            it.remove(); // avoids a ConcurrentModificationException
    	    	        }
    					
    	    	        Log.e("INSERT QUESTIONNAIRE", "Q = "+dataF[i]);
    	    	        db.insert(ConstantParameter.FeedbackCriteria.TABLE, null, initialValues);
    					
    					
    				}
    		
    		//init Feedback Rate
    		for(int i=0; i <rateName.length;i++ ){
    			
    			mapRate = new HashMap<String, String>();
    			mapRate.put(ConstantParameter.FeedbackRate.RATE_NAME, rateName[i]);
    			mapRate.put(ConstantParameter.FeedbackRate.RATE_DESCRIPTION, rateDesc[i]);
    			mapRate.put(ConstantParameter.FeedbackRate.IMAGE_PATH, "emotes/"+rateName[i]+".png");
    			
    			Log.e("INSERT FEEDBACK RATE", "data = "+rateDesc[i]);
    			ContentValues initialValues = new ContentValues();
    	        
    	        Iterator it = mapRate.entrySet().iterator();
    	        while (it.hasNext()) {
    	            Map.Entry pairs = (Map.Entry)it.next();
    	            System.out.println(pairs.getKey() + " = " + pairs.getValue());
    	            initialValues.put(pairs.getKey().toString(), pairs.getValue().toString());
    	            it.remove(); // avoids a ConcurrentModificationException
    	        }
    			db.insert(ConstantParameter.FeedbackRate.TABLE, null, initialValues);
    	        
    			
    		}
    		
    	}
        
    }
    
  //---opens the database---
    public Dao open() throws SQLException 
    {
        db = DBHelper.getWritableDatabase();
        return this;
    }

    //---closes the database---    
    public void close() 
    {
        DBHelper.close();
    }
    
    
    //=============================================== CRUD =================================================================//
    
    public long insertData(String tableName, HashMap<String,String> mapData) 
    {
        ContentValues initialValues = new ContentValues();
        
        Iterator it = mapData.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pairs = (Map.Entry)it.next();
            System.out.println(pairs.getKey() + " = " + pairs.getValue());
            initialValues.put(pairs.getKey().toString(), pairs.getValue().toString());
            it.remove(); // avoids a ConcurrentModificationException
        }
               
        return db.insert(tableName, null, initialValues);
    }
   
    public boolean deleteData(String tableName, String fieldName, long rowId) 
    {
        return db.delete(tableName, fieldName + 
        		"=" + rowId, null) > 0;
    }
    
    public int deleteAllData(String tableName) 
    {    	
        return db.delete(tableName, "1", null);
    }
    
    public int deleteDataWithCondition(String tableName, String clause, String[] args) 
    {    	
        return db.delete(tableName, clause, args);
    }
    
    public Cursor getAllData(String tableName, String[] fields, String orderBy) 
    {
        return db.query(tableName, fields, 
                null, 
                null, 
                null, 
                null, 
                orderBy);
    }


    public Cursor getSpecificDataById(String tableName, String[] fields, String pkFieldName,long rowId) throws SQLException 
    {
        Cursor mCursor =
                db.query(true, tableName, fields, 
                		pkFieldName + "=" + rowId, 
                		null,
                		null, 
                		null, 
                		null, 
                		null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }
    
    public Cursor getAllDataByCondition(String tableName, String[] fields, String condition,String[] value, String orderBy) throws SQLException 
    {    	
        Cursor mCursor =
                db.query(tableName, fields, 
                		condition, 
                		value,
                		null, 
                		null, 
                		orderBy, 
                		null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }                
        
        return mCursor;
    }
    

    public boolean updateDataById(String tableName, String pkFieldName ,long rowId, HashMap<String,String> mapData) 
    {
        ContentValues args = new ContentValues();
        
        Iterator it = mapData.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pairs = (Map.Entry)it.next();
            Log.e("UPDATE ", pairs.getKey() + " = " + pairs.getValue());
            args.put(pairs.getKey().toString(), pairs.getValue().toString());
            it.remove(); // avoids a ConcurrentModificationException
        }
        
        return db.update(tableName, args, 
                         pkFieldName + "=" + rowId, null) > 0;
    }
    
    public boolean updateDataWithINCondition(String tableName, String fieldName ,String value ,HashMap<String,String> mapData) 
    {
        ContentValues args = new ContentValues();
        
        Iterator it = mapData.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pairs = (Map.Entry)it.next();
            System.out.println(pairs.getKey() + " = " + pairs.getValue());
            args.put(pairs.getKey().toString(), pairs.getValue().toString());
            it.remove(); // avoids a ConcurrentModificationException
        }
                
        return db.update(tableName, args, 
                fieldName + " IN (" + value +")", null) > 0;
    }
    
    public boolean updateDataByCondition(String tableName, String condition ,String[] value, HashMap<String,String> mapData) 
    {
        ContentValues args = new ContentValues();
        
        Iterator it = mapData.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pairs = (Map.Entry)it.next();
            System.out.println(pairs.getKey() + " = " + pairs.getValue());
            args.put(pairs.getKey().toString(), pairs.getValue().toString());
            it.remove(); // avoids a ConcurrentModificationException
        }
                
        return db.update(tableName, args, 
                         condition, value) > 0;
    }
    
    public Cursor queryWithRawSQL(String sql, String[] value){    	 
    	
    	Log.e("RAW QUERY", "SQL = " + sql);
    	Log.e("RAW QUERY", "Value = " + value.toString());
    	
        Cursor crs = db.rawQuery(sql, value);
        if (crs != null) {
        	crs.moveToFirst();
        }                
        
        return crs;
    }
    
    
}
