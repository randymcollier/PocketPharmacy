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

public class SearchResultsView extends Activity {

	ArrayAdapter<String> myAdapter;
	DBAdapter db;
	String[] names;
	int[] ids;
	ListView lv;
	TextView tv;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search_results_view);
		
		tv = (TextView) findViewById(R.id.searchTitle2);
		tv.setText("All Drugs");
		
		Bundle data = getIntent().getExtras();
		String keyword = data.getString("keyword");
		
		long selectionId = data.getLong("selectionId");
		
		db = new DBAdapter(this);
		db.open();
        
		Cursor c = null;
		if (selectionId == 1) {
			c = db.searchAllByBrand(keyword);
		}
		else if (selectionId == 2) {
			c = db.searchAllByClass(keyword);
		}
		else {
			c = db.searchAllByGeneric(keyword);
		}
		names = new String[c.getCount()];
		ids = new int[c.getCount()];
		
		for (int pos=0; pos<c.getCount(); pos++) {
	        c.moveToPosition(pos);
	        ids[pos] = c.getInt(0);
    	    names[pos] = c.getString(1);
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
				data.putInt("id", ids[pos]);
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
	  inflater.inflate(R.menu.context_menu, menu);
	  AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
	  menu.setHeaderTitle(names[info.position]);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
	  AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
	  int pos = info.position;
	  switch (item.getItemId()) {
	  case R.id.more:
		Intent i = new Intent("edu.olemiss.rcollier.pocketpharmacy.Web");
		i.putExtra("drug", names[pos]);
        startActivityForResult(i, 1);
		return true;
	  case R.id.bookmark:
		  this.addBookmark(pos);
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
	
	public void addBookmark(int pos) {
		Cursor c = db.getBookmarks();
		boolean isBookmark = false;
		for (int i=0; i<c.getCount(); i++) {
	        c.moveToPosition(i);
	        if (c.getInt(2) == ids[pos]) {
	        	isBookmark = true;
	        }
		}
		if (isBookmark) {
			showToast(names[pos] + " is already favorited.");
		}
		else {
			db.addBookmark(ids[pos]);
			showToast("Favorited " + names[pos] + ".");
		}
	}

}
