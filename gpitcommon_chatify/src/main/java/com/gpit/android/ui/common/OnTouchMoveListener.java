package com.gpit.android.ui.common;

public interface OnTouchMoveListener {
	public void onMove(int deltaX, int deltaY);
	public void onScale(float scale);
	public void onMoveLeft();
	public void onMoveRight();
	public void onMoveTop();
	public void onMoveBottom();
}
