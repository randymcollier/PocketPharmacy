package edu.olemiss.rcollier.pocketpharmacy;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

public class IndividualView extends Activity {

	DBAdapter db;
	String name, drugclass;
	String[] interactions, brands, dosages;
	int id, bookmarkId;
	TextView tv_title, tv_dosage, tv_class, tv_interactions, tv_brands, tv_more;
	CheckBox cb;
	boolean isBookmark;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_individual);
		
		Bundle data = getIntent().getExtras();
		id = data.getInt("id");
		
		db = new DBAdapter(this);
		db.open();
		
		Cursor c = db.getSingleDrug(id);
		c.moveToFirst();
		name = c.getString(0);
		drugclass = c.getString(1) + "\n";
		
		tv_title = (TextView) findViewById(R.id.ind_title);
		tv_title.setText(name);
		
		tv_class = (TextView) findViewById(R.id.ind_class);
		tv_class.setText(drugclass);
		
		c = db.getSingleBrands(id);
		brands = new String[c.getCount()];
		tv_brands = (TextView) findViewById(R.id.ind_brand);
		String text = "";
		for (int i = 0; i < c.getCount(); i++) {
			c.moveToPosition(i);
			text += c.getString(0);
			text += "\n";
		}
		tv_brands.setText(text);
		
		c = db.getSingleDosages(id);
		dosages = new String[c.getCount()];
		tv_dosage = (TextView) findViewById(R.id.ind_dosage);
		text = "";
		for (int i = 0; i < c.getCount(); i++) {
			c.moveToPosition(i);
			text += c.getString(0);
			text += " mg\n";
		}
		tv_dosage.setText(text);
		
		c = db.getSingleInteractions(id);
		interactions = new String[c.getCount()];
		tv_interactions = (TextView) findViewById(R.id.ind_interactions);
		text = "";
		for (int i = 0; i < c.getCount(); i++) {
			c.moveToPosition(i);
			text += c.getString(0);
			text += "\n";
		}
		tv_interactions.setText(text);
		
		cb = (CheckBox) findViewById(R.id.bookmarkCheckBox);
		c = db.getBookmarks();
		isBookmark = false;
		for (int i=0; i<c.getCount(); i++) {
	        c.moveToPosition(i);
	        if (c.getInt(2) == id) {
	        	isBookmark = true;
	        	bookmarkId = c.getInt(0);
	        }
		}
		if (isBookmark) {
			cb.setSelected(true);
			cb.setButtonDrawable(R.drawable.rate_star_big_on_holo_light);
		}
		else {
			cb.setSelected(false);
			cb.setButtonDrawable(R.drawable.rate_star_big_off_holo_light);
		}
		cb.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				v.setSelected(!v.isSelected());
				doBookmark(id);
			}
			
		});
		
		tv_more = (TextView) findViewById(R.id.tv_more);
		tv_more.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent("edu.olemiss.rcollier.pocketpharmacy.Web");
				  i.putExtra("drug", name);
		        startActivityForResult(i, 1);
			}
		});
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

	public void doBookmark(int id) {
		Cursor c = db.getBookmarks();
		boolean isBookmark = false;
		for (int i=0; i<c.getCount(); i++) {
	        c.moveToPosition(i);
	        if (c.getInt(2) == id) {
	        	isBookmark = true;
	        	bookmarkId = c.getInt(0);
	        }
		}
		if (isBookmark) {
			db.removeBookmark(bookmarkId);
			cb.setButtonDrawable(R.drawable.rate_star_big_off_holo_light);
			isBookmark = false;
			showToast("Removed " + name + " from favorites.");
		}
		else {
			db.addBookmark(id);
			cb.setButtonDrawable(R.drawable.rate_star_big_on_holo_light);
			isBookmark = true;
			showToast("Favorited " + name + ".");
		}
	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        this.recreate();
    }
}
