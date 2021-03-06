package edu.olemiss.rcollier.pocketpharmacy;

import java.util.ArrayList;
import java.util.Arrays;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class ViewList extends Activity implements OnItemSelectedListener {

	ArrayAdapter<String> myAdapter;
	DBAdapter db;
	String[] names;
	int[] ids;
	ListView lv;
	TextView tv;
	EditText et;
	Spinner spinner;
	ImageButton btn;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list_view);
		
		tv = (TextView) findViewById(R.id.title);
		tv.setText("All Drugs");
		
		db = new DBAdapter(this);
		db.open();
        
		Cursor c = db.getAllDrugs();
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
			
		lv = (ListView) findViewById(R.id.list);
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
		
		et = (EditText) findViewById(R.id.searchText);
		et.setText("");
		et.setOnEditorActionListener(new TextView.OnEditorActionListener() {
		    @Override
		    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
		        if (actionId == EditorInfo.IME_NULL && event.getAction() == KeyEvent.ACTION_DOWN) {
		        	InputMethodManager imm = (InputMethodManager)getSystemService(
		        			  Context.INPUT_METHOD_SERVICE);
		        			imm.hideSoftInputFromWindow(et.getWindowToken(), 0);
		        	performSearch();
		            return true;
		        }
		        return false;
		    }
		});
		
		spinner = (Spinner) findViewById(R.id.search_spinner);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
				R.array.search_array, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);
		spinner.setOnItemSelectedListener(this);
		
		btn = (ImageButton) findViewById(R.id.imageButton1);
		btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				performSearch();
				InputMethodManager inputManager = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE); 

				inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                           InputMethodManager.HIDE_NOT_ALWAYS);
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
        et.setText("");
        et.requestFocus();
    }
	
	public void showToast(String message) {
	    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
    }
	
	public void performSearch() {
		String keyword = et.getText().toString();
		long selectionId = spinner.getSelectedItemId();
		Bundle b = new Bundle();
		b.putString("keyword", keyword);
		b.putLong("selectionId", selectionId);
		Intent i = new Intent("edu.olemiss.rcollier.pocketpharmacy.SearchResultsView");
		i.putExtras(b);
		startActivityForResult(i, 1);
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
	
	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        // empty method
    }

    @Override
	public void onNothingSelected(AdapterView<?> parent) {
        // empty method
    }

}
