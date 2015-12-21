package com.gpit.android.database;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
	protected static DatabaseHelper databaseHelper;
	
	protected Context mContext;

	private static String DB_NAME = "";// the extension may be .sqlite or .db

	// Defines db error
	public enum DBError {
		DB_ERROR_NONE, DB_ERROR_FAILED, DB_ERROR_ALREADY_EXIST, DB_ERROR_NOT_EXIST,
	};

	private String DB_PATH;

	protected SQLiteDatabase mDatabase;
	
	protected DatabaseHelper(Context context, String dbName, int versionNo) {
		super(context, dbName, null, versionNo);
		
		DB_NAME = dbName;
		mContext = context;

		DB_PATH = context.getFilesDir().getPath() + "/";
		
		boolean dbexist = checkDatabase();
		if (dbexist) {
			openDatabase();
		} else {
			createDatabase();
			openDatabase();
		}
	}

	public String getDatabaseName() {
		return DB_NAME;
	}
	
	public void createDatabase() {
		boolean dbexist = checkDatabase();

		if (dbexist) {
			System.out.println(" Database exists.");
		} else {
			this.getWritableDatabase();
			try {
				copyDatabase();
			} catch (IOException e) {
				// throw new Error("Error copying mDatabase");
			}
		}
	}

	public SQLiteDatabase getDatabase() {
		return mDatabase;
	}
	
	public void closeDatabase() {
		mDatabase.close();
	}

	private boolean checkDatabase() {
		boolean checkdb = false;
		try {
			String myPath = DB_PATH + DB_NAME;
			File dbfile = new File(myPath);
			checkdb = dbfile.exists();
		} catch (SQLiteException e) {
			System.out.println("Database doesn't exist");
		}

		return checkdb;
	}

	public void copyDatabase(String orgPath) throws IOException {
		// Open your local db as the input stream
		FileInputStream input = new FileInputStream(orgPath);

		// Open the empty db as the output stream
		String myDBPath = DB_PATH + DB_NAME;
		(new File(myDBPath)).delete();

		OutputStream output = new FileOutputStream(myDBPath);

		// transfer byte to inputfile to outputfile
		byte[] buffer = new byte[1024];
		int length;
		while ((length = input.read(buffer)) > 0) {
			output.write(buffer, 0, length);
		}

		// Close the streams
		output.flush();
		output.close();
		input.close();
	}

	private void copyDatabase() throws IOException {
		// Open your local db as the input stream
		InputStream input = mContext.getAssets().open(DB_NAME);

		// Open the empty db as the output stream
		String myDBPath = DB_PATH + DB_NAME;

		OutputStream output = new FileOutputStream(myDBPath);

		// transfer byte to inputfile to outputfile
		byte[] buffer = new byte[1024];
		int length;
		while ((length = input.read(buffer)) > 0) {
			output.write(buffer, 0, length);
		}

		// Close the streams
		output.flush();
		output.close();
		input.close();
	}

	public void openDatabase() throws SQLException {
		// Open the mDatabase
		String mypath = DB_PATH + DB_NAME;
		mDatabase = SQLiteDatabase.openDatabase(mypath, null,
				SQLiteDatabase.OPEN_READWRITE);
	}

	@Override
	public void onCreate(SQLiteDatabase arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub
	}
}