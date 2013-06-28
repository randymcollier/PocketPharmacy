package edu.olemiss.rcollier.pocketpharmacy;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

public class HomeActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		
		DBAdapter db = new DBAdapter(this);
		db.open();
		db.checkUpgrade();
		Cursor c = db.getAllDrugs();
		if (c.getCount() < 1) {
			db.InsertCSVFile();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {		
		MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.action_bar, menu);
	    return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    Intent i;
		switch (item.getItemId()) {
	        case R.id.bookmarks:
	        	i = new Intent("edu.olemiss.rcollier.pocketpharmacy.BookmarksView");
	    		startActivityForResult(i, 1);
	            return true;
	        case R.id.viewAllDrugs:
	    		i = new Intent("edu.olemiss.rcollier.pocketpharmacy.ViewList");
	    		startActivityForResult(i, 1);
	    		return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	
	public void showToast(String message) {
		Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        this.recreate();
    }

}
