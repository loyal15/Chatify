package vsppsgv.chatify.im.webapi;



public interface OnAPICompletedListener<T> {
	public void onCompleted(T result);
	public void onCompleted();
	public void onFailed(T result);
	public void onCanceled(T result);
}
