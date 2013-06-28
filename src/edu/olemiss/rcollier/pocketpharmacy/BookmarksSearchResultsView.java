package edu.olemiss.rcollier.pocketpharmacy;

import java.util.ArrayList;
import java.util.Arrays;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class BookmarksSearchResultsView extends Activity {

	ArrayAdapter<String> myAdapter;
	DBAdapter db;
	String[] names;
	int[] ids, drugIds;
	ListView lv;
	TextView tv;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search_results_view);
		
		tv = (TextView) findViewById(R.id.searchTitle2);
		tv.setText("Favorites");
		
		db = new DBAdapter(this);
		db.open();
        
		Bundle data = getIntent().getExtras();
		String keyword = data.getString("keyword");
		
		long selectionId = data.getLong("selectionId");
		
		Cursor c = null;
		if (selectionId == 1) {
			c = db.searchBookmarksByBrand(keyword);
		}
		else if (selectionId == 2) {
			c = db.searchBookmarksByClass(keyword);
		}
		else {
			c = db.searchBookmarksByGeneric(keyword);
		}
		names = new String[c.getCount()];
		ids = new int[c.getCount()];
		drugIds = new int[c.getCount()];
		
		for (int pos=0; pos<c.getCount(); pos++) {
	        c.moveToPosition(pos);
	        ids[pos] = c.getInt(0);
    	    names[pos] = c.getString(1);
    	    drugIds[pos] = c.getInt(2);
		}
		
		ArrayList<String> drugNames = new ArrayList<String>();
		drugNames.addAll(Arrays.asList(names));
		myAdapter = new ArrayAdapter<String>(this, R.layout.one_row, drugNames);
			
		lv = (ListView) findViewById(R.id.searchList);
		lv.setAdapter(myAdapter);	
		registerForContextMenu(lv);
		
		lv.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
				Bundle data = new Bundle();
				data.putInt("id", drugIds[pos]);
				Intent i = new Intent("edu.olemiss.rcollier.pocketpharmacy.IndividualView");
				i.putExtras(data);
				startActivityForResult(i, 1);
			}
		});
		lv.setOnItemLongClickListener(new OnItemLongClickListener() {
			public boolean onItemLongClick(AdapterView<?> parent, View view, int pos, long id) {
				return false;
			}
		});
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
	  super.onCreateContextMenu(menu, v, menuInfo);		  
	  MenuInflater inflater = getMenuInflater();
	  inflater.inflate(R.menu.context_menu_bookmarks, menu);
	  AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
	  menu.setHeaderTitle(names[info.position]);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
	  AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
	  int pos = info.position;
	  switch (item.getItemId()) {
	  case R.id.delete:
		db.removeBookmark(ids[pos]);
		showToast("Removed " + names[pos] + " from favorites.");
		this.recreate();
	    return true; 
	  case R.id.more:
		Intent i = new Intent("edu.olemiss.rcollier.pocketpharmacy.Web");
		i.putExtra("drug", names[pos]);
        startActivityForResult(i, 1);
		return true; 
	  default:
	    return super.onContextItemSelected(item);
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
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        this.recreate();
    }
	
	public void showToast(String message) {
	    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
    }
}
