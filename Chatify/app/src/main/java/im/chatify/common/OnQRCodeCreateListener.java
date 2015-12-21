package photofoto.gpit.com.photofoto.common;

import android.graphics.Bitmap;

public interface OnQRCodeCreateListener {
	// Sync data based on updated database
	public void onCreatedBitmap(Bitmap bitmap);
}
