package com.example.task;

import android.os.AsyncTask;
import android.util.Log;

class RetreiveFeedTask extends AsyncTask<String, String, String>{

    private Exception exception;

    /*protected String doInBackground(String... urls) {

        	Log.e("SendMail", "ready to send mail recon");
			try {   
	            EmailSender sender = new EmailSender("bnr.demo1@gmail.com", "brt123456");
	            sender.sendMail("Test Survey Response","test",
	                    "bnr.demo1@gmail.com",   
	                    "a.hasril@brightrich.com");
	            
	            Log.e("SendMail", "done sending email recon");
	        } catch (Exception e) {   
	            Log.e("SendMailERROR", e.getMessage());   
	        }
        
    }*/

    protected void onPostExecute(String feed) {
        // TODO: check this.exception 
        // TODO: do something with the feed
    }

	@Override
	protected String doInBackground(String... params) {
		String result = "";
		Log.e("SendMail", "ready to send mail recon");
		try {   
            EmailSender sender = new EmailSender("bnr.demo1@gmail.com", "brt123456");
            sender.sendMail("Test Survey Response",params[0],
                    "bnr.demo1@gmail.com",   
                    "a.hasril@brightrich.com");
            
            Log.e("SendMail", "done sending email recon");
            result = "Recon Email Sent!!!";
        } catch (Exception e) {   
            Log.e("SendMailERROR", e.getMessage());
            result = "Recon Email Error!!!";
        }
		return result;
	}

	
}
