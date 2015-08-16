package com.gpit.android.facedetect;

import android.graphics.Bitmap;
import android.graphics.PointF;
import android.graphics.Rect;
import android.media.FaceDetector;
import android.media.FaceDetector.Face;

public class FacedetectUtils {

	/**
	 * maybe need system.gc() after this.
	 * 
	 * @param bitmap
	 * @param numFace
	 * @return
	 */
	public static Face[] detectFace(Bitmap bitmap, int numFace) {
		FaceDetector faceDetector = new FaceDetector(bitmap.getWidth(),
				bitmap.getHeight(), numFace);

		Face[] faces = new FaceDetector.Face[numFace];

		faceDetector.findFaces(bitmap, faces);
		faceDetector = null;
		return faces;
	}

	public static Rect[] faceToRect(Face[] faces) {
		PointF[] faceMids = new PointF[faces.length];
		float[] eyedistance = new float[faces.length];
		for (int i = 0; i < faces.length; i++) {
			if (faces[i] != null) {
				PointF pt = new PointF();
				faces[i].getMidPoint(pt);
				eyedistance[i] = faces[i].eyesDistance();
				faceMids[i] = pt;
			} else {
				faceMids[i] = null;
			}
		}

		// these are something magic number
		float r = 3f / 4;
		float faceHeight = 1.9f;
		float faceWidth = faceHeight * r;

		Rect[] rects = new Rect[faces.length];
		for (int i = 0; i < faces.length; i++) {
			if (faces[i] != null) {
				int dw = (int) (eyedistance[0] * 2 * faceWidth);
				int dh = (int) (eyedistance[0] * 2 * faceHeight);
				int dx = (int) (faceMids[i].x - dw / 2);
				int dy = (int) (faceMids[i].y - dh / 2);
				rects[i] = new Rect(dx, dy, dx + dw, dy + dh);
			}
		}
		faceMids = null;
		eyedistance = null;
		return rects;
	}

	/**
	 * dont work yet.
	 * 
	 * @author aki
	 * @deprecated
	 */
	public class FaceDetectControlder {
		public boolean cancel;
	}
}
