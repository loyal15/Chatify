package com.gpit.android.db;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import junit.framework.Assert;

import com.gpit.android.util.Utils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

public class BackupDB {
	private final static String BACKUP_RES_PATH = "backup/";
	
	private Context mContext;
	private ContentResolver mCR;
	private String dirPath;
	
	public BackupDB(Context context) {
		mContext = context;
		mCR = mContext.getContentResolver();
		
		dirPath = Utils.ensureDir(mContext, BACKUP_RES_PATH);
	}
	
	public StringBuffer runBackup(Uri uri, String[] selection, boolean saveToSDCard) {
		File backupFile = null;
		
	    StringBuffer csvBuffer;

	    if (saveToSDCard) {
	    	// backup csv to sd card
	    	String filePath = getBackupPath(uri.toString());
	    	backupFile = new File(filePath);
	    	try {
				backupFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
	    }
	    
	    Cursor cursor = mCR.query(uri, selection, null, null, null);
	    csvBuffer = Utils.cursorToCSV(cursor);
	    cursor.close();
	    
	    if (saveToSDCard) {
	    	Assert.assertTrue(backupFile != null);
	    	if (backupFile.exists()) {
	    		try {
					FileOutputStream fout = new FileOutputStream(backupFile);
					DataOutputStream dout = new DataOutputStream(fout);
					dout.writeBytes(csvBuffer.toString());
					dout.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
	    	}
	    }
	    return csvBuffer;
	}
	
	public String getBackupPath(String filename) {
		StringBuffer pathBuffer;
		String path;
		
		pathBuffer = new StringBuffer(filename);
		Utils.normalizePath(pathBuffer);
		path = dirPath + File.separatorChar + pathBuffer.toString();
		
		return path;
	}
	
}
