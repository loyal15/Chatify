package com.gpit.android.util.zip;

import android.content.Context;
import android.util.Log;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import com.gpit.android.util.Utils;

public class ZipHandler {
	private static final int BUFFER_SIZE = 2048;

	private static ZipHandler zipHandler;
	
	private Context mContext;
	public static ZipHandler getInstance(Context context) {
		if (zipHandler == null)
			zipHandler = new ZipHandler(context);
		
		return zipHandler;
	}
	
	public ZipHandler(Context context) {
		mContext = context;
	}
	
	/******************************************************************************
	 * COMPRESS
	 ******************************************************************************/
	public void compress(String basePath, String[] files, String zipFile) {
		try {
			new File(zipFile).createNewFile();

			FileOutputStream dest = new FileOutputStream(zipFile);
			ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(
					dest));

			compress(basePath, files, out);

			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void compress(String basePath, String[] files, ZipOutputStream out) {
		try {
			BufferedInputStream origin = null;

			for (int i = 0; i < files.length; i++) {
				Log.v("Compress", "Adding: " + files[i]);
				
				File file = new File(files[i]);
				if (file.isDirectory()) {
					File[] subFiles = file.listFiles();
					if (subFiles == null || subFiles.length == 0) {
						String subPath = files[i].substring(basePath.length() + 1);
						ZipEntry entry = new ZipEntry(subPath + File.separatorChar + ".");
						out.putNextEntry(entry);
						out.closeEntry();
					} else {
						String[] subFilesPath = new String[subFiles.length];
						for (int j = 0 ; j < subFiles.length ; j++) {
							subFilesPath[j] = subFiles[j].getAbsolutePath();
						}
						
						// Directory
						compress(basePath, subFilesPath, out);
					}
				} else {
					FileInputStream fi = new FileInputStream(files[i]);
					origin = new BufferedInputStream(fi, BUFFER_SIZE);
					
					String subPath = files[i].substring(basePath.length());
					ZipEntry entry = new ZipEntry(subPath);
					out.putNextEntry(entry);
					
					// File
					int count;
					byte data[] = new byte[BUFFER_SIZE];
					while ((count = origin.read(data, 0, BUFFER_SIZE)) != -1) {
						out.write(data, 0, count);
					}
					origin.close();
					fi.close();
					
					out.closeEntry();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/******************************************************************************
	 * DECOMPRESS
	 ******************************************************************************/
	public boolean decompress(String zipPath, String destPath) {
		InputStream is;
		ZipInputStream zis;
		try {
			is = new FileInputStream(zipPath);
			zis = new ZipInputStream(new BufferedInputStream(is));
			ZipEntry ze;

			while ((ze = zis.getNextEntry()) != null) {
				try {
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					byte[] buffer = new byte[BUFFER_SIZE];
					int count;
	
					// zapis do souboru
					String filename = ze.getName();
					File newFile = new File(destPath + File.separatorChar + filename);
					Utils.ensureDir(mContext, newFile.getParent(), false);
					FileOutputStream fout = new FileOutputStream(newFile);
	
					// cteni zipu a zapis
					while ((count = zis.read(buffer)) != -1) {
						baos.write(buffer, 0, count);
						byte[] bytes = baos.toByteArray();
						fout.write(bytes);
						baos.reset();
					}
	
					fout.close();
					zis.closeEntry();
				} catch (IOException e) {
				}
			}

			zis.close();
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}
}
