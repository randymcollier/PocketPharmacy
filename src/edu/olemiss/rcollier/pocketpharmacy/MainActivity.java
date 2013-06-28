package edu.olemiss.rcollier.pocketpharmacy;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

public class MainActivity extends Activity {
	
	public static final String PREFS_NAME = "user_conditions";
	
	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
	    boolean accepted = settings.getBoolean("accepted", false);
	    if( accepted ){
	    	startActivity(new Intent("edu.olemiss.rcollier.pocketpharmacy.HomeActivity"));
	    	finish();
	    }else{
	        showDialog(0);
	    }
	}
	
	protected Dialog onCreateDialog(int id){
	    
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
	    builder.setTitle(R.string.legal_title)
	    	   .setMessage(R.string.legal_message)
	           .setCancelable(false)
	           .setPositiveButton("Agree", new DialogInterface.OnClickListener() {
	               public void onClick(DialogInterface dialog, int id) {
	                   
	            	   SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
	                   SharedPreferences.Editor editor = settings.edit();
	                   editor.putBoolean("accepted", true);
	                   editor.commit(); 
	                   startActivity(new Intent("edu.olemiss.rcollier.pocketpharmacy.HomeActivity"));
	                   finish();
	               }
	           })
	           .setNegativeButton("Disagree", new DialogInterface.OnClickListener() {
	               public void onClick(DialogInterface dialog, int id) {
	            	   System.exit(0);
	               }
	           });
	    return builder.create();
	}

}


