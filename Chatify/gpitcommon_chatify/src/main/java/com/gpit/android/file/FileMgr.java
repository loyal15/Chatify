/* 
 * Copyright (C) 2007-2008 OpenIntents.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.gpit.android.file;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.xmlpull.v1.XmlPullParserException;

import com.gpit.android.library.R;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.res.XmlResourceParser;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Environment;
import android.os.StatFs;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

/**
 * @version 2009-07-03
 * 
 * @author Peli
 *
 */
public class FileMgr {
	private static FileMgr FILE_MGR;
	
	// *************** Size Unit String *****************
	public static final String SIZE_UNIT_BYTE = "Bytes";
	public static final String SIZE_UNIT_KB = "KB";
	public static final String SIZE_UNIT_MB = "MB";
	public static final String SIZE_UNIT_GB = "GB";
	
	// ***********************Mimetype*******************
	public static final int FILETYPE_ALL = -1;
	public static final int FILETYPE_OTHER = 0;
	public static final int FILETYPE_AUDIO = 1;
	public static final int FILETYPE_VIDEO = 2;
	public static final int FILETYPE_PHOTO = 3;
	public static final int FILETYPE_DOC = 4;
	public static final int FILETYPE_XLS = 5;
	public static final int FILETYPE_PDF = 6;
	public static final int FILETYPE_PPT = 7;
	public static final int FILETYPE_TXT = 8;
	
	// *************** Buffer Size for Copying File *****************
	public static final int COPY_BUFFER_SIZE = 4096;
	public static final long MAX_FILE_SIZE = (long)2 * 1024 * 1024 * 1024;
	
	/** TAG for log messages. */
	private static final String TAG = "FileUtils";

	private static MimeTypes mMimeTypes = null;

	public static FileMgr getInstance() {
		if (FILE_MGR == null)
			FILE_MGR = new FileMgr();
		
		return FILE_MGR;
	}
	/**
	 * Gets the extension of a file name, like ".png" or ".jpg".
	 * 
	 * @param uri
	 * @return Extension including the dot("."); "" if there is no extension;
	 *         null if uri was null.
	 */
	public static String getExtension(String uri) {
		if (uri == null) {
			return null;
		}

		int dot = uri.lastIndexOf(".");
		if (dot >= 0) {
			return uri.substring(dot);
		} else {
			// No extension.
			return "";
		}
	}

	/**
	 * Convert File into Uri.
	 * @param file
	 * @return uri
	 */
	public static Uri getUri(File file) {
		if (file != null) {
			return Uri.fromFile(file);
		}
		return null;
	}
	
	public static boolean isExistFile(String absolutePath){
		if (absolutePath == null) return false;
		
		File tmpFile = new File(absolutePath);

		return tmpFile.isFile() && tmpFile.exists();
	}
	
	public static long getFileSize(String fullPath){
		if (fullPath == null) return 0;
		File tmpFile = new File(fullPath);
		if (tmpFile.isDirectory() || !tmpFile.exists())
			return 0;
		return tmpFile.length();
	}

	public static long getFileRealSize(String strFileSize){
		long lFileSize = 0;
		if(strFileSize != null)
		{
			if(strFileSize.contains(SIZE_UNIT_BYTE))
			{
				strFileSize = strFileSize.substring(0, strFileSize.length() - SIZE_UNIT_BYTE.length());
				strFileSize = strFileSize.trim();
				lFileSize = Integer.parseInt(strFileSize);
			}
			else if(strFileSize.contains(SIZE_UNIT_KB))
			{
				strFileSize = strFileSize.substring(0, strFileSize.length() - SIZE_UNIT_KB.length());
				strFileSize = strFileSize.trim();
				lFileSize = Math.round(Float.parseFloat(strFileSize) * 1024);
			}
			else if(strFileSize.contains(SIZE_UNIT_MB))
			{
				strFileSize = strFileSize.substring(0, strFileSize.length() - SIZE_UNIT_MB.length());
				strFileSize = strFileSize.trim();
				lFileSize = Math.round(Float.parseFloat(strFileSize) * 1024 * 1024);
			}
			else if(strFileSize.contains(SIZE_UNIT_GB))
			{
				strFileSize = strFileSize.substring(0, strFileSize.length() - SIZE_UNIT_GB.length());
				strFileSize = strFileSize.trim();
				lFileSize = Math.round(Float.parseFloat(strFileSize) * 1024 * 1024 * 1024);
			}
			else
			{
				//strFileSize穈�?嚙趣�??諤蕭? ?嚙趟� 窶趣 0???嚙趟?旭��
				lFileSize = 0;
			}
		}
		return lFileSize;
		
	}

	public static String getStringFileSize(long realFileSize)
	{
		String strFileSize = "";

		if(realFileSize < 0)
		{
			return strFileSize;
		}
		
		Double dRealFileSize = Double.parseDouble(realFileSize + "");
		
    	if(dRealFileSize >= 1024)
    	{
    		dRealFileSize = dRealFileSize / 1024;
    		if(dRealFileSize >= 1024)
    		{
    			dRealFileSize = dRealFileSize / 1024;
        		if(dRealFileSize >= 1024)
        		{
        			dRealFileSize = dRealFileSize / 1024;
        			strFileSize = Math.round(dRealFileSize * 100) / 100.0 + SIZE_UNIT_GB;
        		}
        		else
        		{
        			strFileSize = Math.round(dRealFileSize * 100) / 100.0 + SIZE_UNIT_MB;
        		}
    		}
    		else
    		{
    			strFileSize = Math.round(dRealFileSize * 100) / 100.0 + SIZE_UNIT_KB;
    		}
    	}
    	else
    	{
    		strFileSize = realFileSize + SIZE_UNIT_BYTE; 
    	}
    	
    	return strFileSize;
	}
	
	private static int fileOpen(Context context, File aFile)
	{
		initMimetype(context);
		Intent intent = new Intent(android.content.Intent.ACTION_VIEW);

		Uri data = getUri(aFile);
		
		String type = mMimeTypes.getMimeType(aFile.getName().toLowerCase());
		
		String strMessage = context.getString(R.string.msg_cannot_open_file);
		if(type == null)
		{
			Toast.makeText(context, strMessage, Toast.LENGTH_LONG).show();
			//No matched type
			return 1;
		}
		else
		{
			intent.setDataAndType(data, type);

			try {
				context.startActivity(intent);
			} catch (ActivityNotFoundException e) {
				e.printStackTrace();
				Toast.makeText(context, strMessage, Toast.LENGTH_LONG).show();
			}
			//Success
			return 0;
		}
	}
	
	public static int fileOpen(Context context, String absoluteFilePath)
	{
		if (absoluteFilePath == null)
			return -1;
		File aFile = new File(absoluteFilePath);
		if(aFile != null && aFile.exists() && !aFile.isDirectory())
			return fileOpen(context, aFile);
		else
			//None exist file
			return -1;
	}
	
	private static void initMimetype(Context context)
	{
		if(mMimeTypes == null)
		{
			MimeTypeParser mtp = new MimeTypeParser();
			
			XmlResourceParser in = context.getResources().getXml(R.xml.mimetypes);
			
			try {
				mMimeTypes = mtp.fromXmlResource(in);
			} catch (XmlPullParserException e) {
				e.printStackTrace();
				throw new RuntimeException("PreselectedChannelsActivity: XmlPullParserException");
			} catch (IOException e) {
				e.printStackTrace();
				throw new RuntimeException("PreselectedChannelsActivity: IOException");
			}
		}
	}

	public static int getFileTypes(Context context, String fileName) 
	{
		initMimetype(context);
		String strFileMimeType = mMimeTypes.getMimeType(fileName);
		
		if(strFileMimeType == null)
			return FILETYPE_OTHER;
		
		String strFileType[] = strFileMimeType.split("/");
		
		if (strFileType[0].equals("image"))
			return FILETYPE_PHOTO;
		else if (strFileType[0].equals("audio"))
			return FILETYPE_AUDIO;
		else if (strFileType[0].equals("video"))
			return FILETYPE_VIDEO;
		else if (strFileType[0].equals("text"))
			return FILETYPE_TXT;
		else if (strFileMimeType.equals("application/msword"))
			return FILETYPE_DOC;
		else if (strFileMimeType
				.equals("application/vnd.openxmlformats-officedocument.wordprocessingml.document"))
			return FILETYPE_DOC;
		else if (strFileMimeType.equals("application/vnd.ms-excel"))
			return FILETYPE_XLS;
		else if (strFileMimeType
				.equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
			return FILETYPE_XLS;
		else if (strFileMimeType.equals("application/vnd.ms-powerpoint"))
			return FILETYPE_PPT;
		else if (strFileMimeType
				.equals("application/vnd.openxmlformats-officedocument.presentationml.presentation"))
			return FILETYPE_PPT;
		else if (strFileMimeType.equals("application/pdf"))
			return FILETYPE_PDF;
		else
			return FILETYPE_OTHER;
	}
	
	public static Bitmap makeThumb(String pathName, int iHeight, int iWidth)
	{
		Options options = new Options();
		
		options.inJustDecodeBounds = true;
		options.outWidth = 0;
		options.outHeight = 0;
		options.inSampleSize = 1;
		
		BitmapFactory.decodeFile(pathName, options);

		try {
			if (options.outWidth > 0 && options.outHeight > 0) {
				// Now see how much we need to scale it down.
				int widthFactor = (options.outWidth + iWidth - 1) / iWidth;
				int heightFactor = (options.outHeight + iHeight - 1) / iHeight;
				
				widthFactor = Math.max(widthFactor, heightFactor);
				widthFactor = Math.max(widthFactor, 1);
				
				// Now turn it into a power of two.
				if (widthFactor > 1) {
					if ((widthFactor & (widthFactor-1)) != 0) {
						while ((widthFactor & (widthFactor-1)) != 0) {
							widthFactor &= widthFactor-1;
						}
						
						widthFactor <<= 1;
					}
				}
				options.inSampleSize = widthFactor;
				options.inJustDecodeBounds = false;
				Bitmap imageBitmap = BitmapFactory.decodeFile(pathName, options);
				BitmapDrawable drawable = new BitmapDrawable(imageBitmap);
				drawable.setGravity(Gravity.CENTER);
				drawable.setBounds(0, 0, iHeight, iWidth);
				return drawable.getBitmap();
			}
		} catch (java.lang.OutOfMemoryError e) {
			// TODO: handle exception
			e.printStackTrace();
		}

		return null;
	}
	
	//create by altair 2011/03/15
	private static boolean recursiveDelete(File file, boolean toastOnError) {
		// Recursively delete all contents.
		File[] files = file.listFiles();
		File childFile;
		
		if (files != null && files.length > 0)
		{
			for (int x=0; x<files.length; x++) {
				childFile = files[x];
				if (childFile.isDirectory()) {
					if (!recursiveDelete(childFile, toastOnError)) {
						return false;
					}
				} else {
					if (!childFile.delete()) {
						return false;
					}
				}
			}
		}
		
		if (!file.delete()) {
			return false;
		}
		
		return true;
	}
	
	//create by altair 2011/03/15
	public static boolean deleteFileOrFolder(File file) {
		if (file.isDirectory()) {
			if (recursiveDelete(file, true))
				return true;
		} else {
			if (file.delete()) {
				// Delete was successful.
				return true;
			} else
				return false;
		}
		return false;
	}
	
	//create by altair 2011/03/15
	public static boolean deleteAll(String destFileAbsPath)
	{
		if (destFileAbsPath == null)
			return false;
		
		File destFile = new File(destFileAbsPath);
		
		if(!destFile.exists())
			return false;
		
		return deleteFileOrFolder(destFile);
	}
	
/*	Create New folder
	0 : Success
	1 : Exist already
	2 : Fail*/
	public static int createNewFolder(String folderPath)
	{
		String str = Environment.getExternalStorageState();
        if (str.equals(Environment.MEDIA_MOUNTED))
        {
        	File newFolder = new File(folderPath.trim());
    		if(newFolder.exists())
    			return 1;
    		
    		if(!newFolder.mkdirs())
    			return 2;
    		else
    			return 0;	
        }
        return 2;
	}
	
	public static String rename(String sourceFileAbsPath, String destFileAbsPath, boolean bForce) {
		File sourceFile = new File(sourceFileAbsPath);
		if(!sourceFile.exists())
			return null;
		
		File destFile = new File(destFileAbsPath);
		String mFileName = destFile.getName();
		String mFolderPath = destFile.getParent();
		
		String szExt = "";
		String szNamePart = "";
		int iPos = mFileName.indexOf('.');
		if (iPos > 0) {
			szNamePart = mFileName.substring(0, iPos);
			szExt = mFileName.substring(iPos + 1);
		} else {
			szNamePart = mFileName;
			szExt = "";
		}
				
		int i = 1;
		if (bForce) {
			if (destFile.exists())
				destFile.delete();
		} else {
			while (destFile.exists()) {
				String newFilePath = mFolderPath + "/";
				newFilePath += szNamePart;
				newFilePath += String.format("%d", i++);
				if (szExt != null && szExt.length() > 0)
					newFilePath += String.format(".%s", szExt);
				
				destFile = new File(newFilePath);
			}
		}
		
		if (!sourceFile.renameTo(destFile))
			return null;
		
		return destFile.getAbsolutePath();
	}
	
    public static String getFileAbsPathFromUri(Context context, Uri uri)
    {
        String[] projection = {MediaStore.Images.ImageColumns.DATA,
        		MediaStore.Images.ImageColumns.DISPLAY_NAME };
        
        String column0Value = "";
        Cursor c = ((Activity) context).managedQuery(uri, projection, null, null, null);
        if (c == null)
        	return null;
        if (c.moveToFirst())
        	column0Value = c.getString(0);
        c.close();
        return column0Value;
    }
    
	public static boolean createThumbnail(Bitmap bmpData, String aFileName)
	{
		if (bmpData == null || 
				aFileName == null || 
				aFileName.trim().length() == 0)
		{
			Log.e(TAG, "createThumbnail(Bitmap bmpData, String aFileName)");
			return false;
		}
		
		File fImageFile = new File(aFileName);
		String strPath = fImageFile.getParent();
		if (!new File(strPath).exists())
			if (!new File(strPath).mkdirs())
				return false;
		
		// jpg file write
		if (fImageFile.exists())
			if (fImageFile.length() == 0)
				fImageFile.delete();
			
		try {
			if (!fImageFile.createNewFile()) {
				Log.e("FMFileTypeSearchDataHandler", "thumbnail create failed!");
				fImageFile = null;
				bmpData.recycle();
				Log.e("FMFileTypeSearchDataHandler", "createNewFile Failed");
				return false;
			} else {
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				bmpData.compress(Bitmap.CompressFormat.JPEG, 100, baos);
				FileOutputStream outStream = new FileOutputStream(fImageFile);
				outStream.write(baos.toByteArray());
				outStream.flush();
				outStream.close();
				bmpData.recycle();
				outStream = null;
			}
		} catch (Exception e) {
			e.printStackTrace();
			fImageFile = null;
			if (bmpData != null)
				bmpData.recycle();
			Log.e("FMFileTypeSearchDataHandler", "thumb image create fail.");
			return false;
		}
		return true;
	}
    
    public static boolean CopyFile(String aSourcePath, String aDestPath) {
		File fDesFile = new File(aDestPath);
		if (fDesFile.exists())
			fDesFile.delete();

		File fSrcFile = new File(aSourcePath);
		if (!fSrcFile.exists())
			return false;
		
		// Check storage whether space is enough or not.
		long srcFileSize = fSrcFile.length();
		long bytesAvailable = -1;
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state)) {
			StatFs stat = new StatFs(Environment.getExternalStorageDirectory().getPath());
			bytesAvailable = (long) stat.getFreeBlocks() * (long) stat.getBlockSize();
		}
		
		if (bytesAvailable < srcFileSize)
			return false;

		FileInputStream fis = null;
		FileOutputStream fos = null;
		BufferedInputStream bis = null;
		BufferedOutputStream bos = null;

		byte[] buffer = new byte[COPY_BUFFER_SIZE];
		try {
			fis = new FileInputStream(fSrcFile);
			fos = new FileOutputStream(fDesFile);
			bis = new BufferedInputStream(fis);
			bos = new BufferedOutputStream(fos);

			int len = 0;
			while ((len = bis.read(buffer)) != -1) {
				SystemClock.sleep(1);
				bos.write(buffer, 0, len);
			}

			buffer = null;
			bis.close();
			bos.flush();
			bos.close();
			fis.close();
			fos.flush();
			fos.close();
			return true;
		} catch (FileNotFoundException ex) {
			ex.printStackTrace();
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			// Close the BufferedOutputStream
			try {
				if (bos != null) {
					bos.flush();
					bos.close();
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		
		return false;
    }
}
