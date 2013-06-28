// This class is the communication medium to the database.

package edu.olemiss.rcollier.pocketpharmacy;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBAdapter {
	private static final String TAG = "DBAdapter";
	private static final String DATABASE_NAME = "PocketPharmacy";
	private static final int DATABASE_VERSION = 18;
	private static final String DATABASE_CREATE_DRUGS = 
			"create table drugs(drugid integer primary key autoincrement, " +
			     "name text, " +
			     "class text);";
	private static final String DATABASE_CREATE_BOOKMARKS =
			"create table bookmarks(bookmarkid integer primary key autoincrement, " +
		             "drugid integer);";
	private static final String DATABASE_CREATE_BRANDS = 
			"create table brands(brandsid integer primary key autoincrement, " +
					"name text, drugid int);";
	private static final String DATABASE_CREATE_INTERACTIONS =
			"create table interactions(interactionsid integer primary key autoincrement, " +
					"interaction text, drugid int);";
	private static final String DATABASE_CREATE_DOSAGES =
			"create table dosages(dosagesid integer primary key autoincrement, " +
					"dosage text, drugid int);";
	private final Context context;
	
	protected DatabaseHelper DBHelper;
	protected SQLiteDatabase db;
	
	public DBAdapter(Context ctx) {
		this.context = ctx;
		DBHelper = new DatabaseHelper(context);
	}
	
	private static class DatabaseHelper extends SQLiteOpenHelper {
		DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}
		
		@Override
		public void onCreate(SQLiteDatabase db) {			
			try {
				db.execSQL(DATABASE_CREATE_DRUGS);
				db.execSQL(DATABASE_CREATE_BOOKMARKS);
				db.execSQL(DATABASE_CREATE_BRANDS);
				db.execSQL(DATABASE_CREATE_DOSAGES);
				db.execSQL(DATABASE_CREATE_INTERACTIONS);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.w(TAG, "Upgrading database from version " + oldVersion + " to " +
					newVersion + ", which will destroy all old data");
			db.execSQL("DROP TABLE IF EXISTS drugs;");
			db.execSQL("DROP TABLE IF EXISTS bookmarks;");
			db.execSQL("DROP TABLE IF EXISTS brands;");
			db.execSQL("DROP TABLE IF EXISTS dosages;");
			db.execSQL("DROP TABLE IF EXISTS interactions;");
			onCreate(db);
		}
	}
		
	public DBAdapter open() throws SQLException {
		db = DBHelper.getWritableDatabase();
		return this;
	}
		
	public void close() {
		DBHelper.close();
	}
	
	public Cursor getAllDrugs() {
		return db.query("drugs", new String[] {"drugid", "name"}, 
				null, null, null, null, "name asc");
	}
	
	public Cursor getSingleDrug(int drugid) {
		String query = "SELECT name, class, drugid FROM drugs WHERE drugid='" + drugid + "';";
		return db.rawQuery(query, null);
	}
	
	public Cursor getSingleBrands(int drugid) {
		String query = "SELECT name, drugid FROM brands WHERE drugid='" + drugid + "';";
		return db.rawQuery(query, null);
	}
	
	public Cursor getSingleInteractions(int drugid) {
		String query = "SELECT interaction, drugid FROM interactions WHERE drugid='" + drugid + "';";
		return db.rawQuery(query, null);
	}
	
	public Cursor getSingleDosages(int drugid) {
		String query = "SELECT dosage, drugid FROM dosages WHERE drugid='" + drugid + "';";
		return db.rawQuery(query, null);
	}
	
	public Cursor getBookmarks() {
		String query = "SELECT bookmarks.bookmarkid, drugs.name, drugs.drugid from bookmarks " +
				"INNER JOIN drugs USING (drugid) ORDER BY drugs.name ASC;";
		return db.rawQuery(query, null);
	}
	
	public long addBookmark(long id) {
		ContentValues args = new ContentValues();
		args.put("drugid", id);
		return db.insert("bookmarks", null, args);
	}
	
	public void removeBookmark(long rowId) {
		db.delete("bookmarks", "bookmarkid="+rowId, null);
	}
	
	public void checkUpgrade() {
		if (db.needUpgrade(DATABASE_VERSION)) {
			DBHelper.onUpgrade(db, (db.getVersion()), DATABASE_VERSION);
		}
	}
	
	public void deleteDrug(long rowId) {
		db.delete("drugs", "id="+rowId, null);
	}
	
	public Cursor searchAllByGeneric(String keyword) {
		String query = "SELECT distinct drugid, name FROM drugs WHERE name LIKE '%" + keyword + "%' ORDER BY name ASC;";
		return db.rawQuery(query, null);
	}
	
	public Cursor searchAllByBrand(String keyword) {
		String query = "SELECT distinct drugs.drugid, drugs.name FROM drugs INNER JOIN brands USING (drugid) "
				+ "WHERE brands.name LIKE '%" + keyword + "%' ORDER BY drugs.name ASC;";
		return db.rawQuery(query, null);
	}
	
	public Cursor searchAllByClass(String keyword) {
		String query = "SELECT distinct drugid, name FROM drugs WHERE class LIKE '%" + keyword + "%' ORDER BY name ASC;";
		return db.rawQuery(query, null);
	}
	
	public Cursor searchBookmarksByGeneric(String keyword) {
		String query = "SELECT distinct bookmarks.bookmarkid, drugs.name, drugs.drugid FROM bookmarks " +
				"INNER JOIN drugs USING (drugid) WHERE drugs.name LIKE '%" + keyword + "%' ORDER BY drugs.name ASC;";
		return db.rawQuery(query, null);
	}
	
	public Cursor searchBookmarksByBrand(String keyword) {
		String query = "SELECT distinct bookmarks.bookmarkid, drugs.name, drugs.drugid FROM bookmarks " +
				"INNER JOIN drugs USING (drugid) INNER JOIN brands USING (drugid) " +
				"WHERE brands.name LIKE '%" + keyword + "%' ORDER BY drugs.name ASC;";
		return db.rawQuery(query, null);
	}
	
	public Cursor searchBookmarksByClass(String keyword) {
		String query = "SELECT distinct bookmarks.bookmarkid, drugs.name, drugs.drugid from bookmarks " +
				"INNER JOIN drugs USING (drugid) WHERE drugs.class LIKE '%" + keyword + "%' ORDER BY drugs.name ASC;";
		return db.rawQuery(query, null);
	}
	
	public void InsertCSVFile() {
	    try {
	        BufferedReader br = new BufferedReader(new InputStreamReader(this.context.getAssets().open("database.txt")));
	        String query = "";
	        
	        while ((query = br.readLine()) != null) {
	            db.execSQL(query);
	        }
	    } catch (Exception e) {}
	}
}
