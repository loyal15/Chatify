package vsppsgv.chatify.im.webapi;

import android.app.Activity;
import android.content.Context;

public abstract class OnCommonAPICompleteListener<T> implements OnAPICompletedListener<T> {
	protected Context mContext;
	protected Context mActivity;
	public OnCommonAPICompleteListener(Context context) {
		mContext = context;
		if (mContext instanceof Activity) {
			mActivity = (Activity) mContext;
		}
	}
	
	
	public void onFailed(T webapi) {
		@SuppressWarnings("rawtypes")
        CIBaseWebAPI baseWebAPI = (CIBaseWebAPI) webapi;

		showLogs(baseWebAPI);
		showToasts(baseWebAPI);
	}
	
	protected void showLogs(@SuppressWarnings("rawtypes") CIBaseWebAPI baseWebAPI) {
        /*
		if (!Utils.isNullOrEmpty(baseWebAPI.getErrorMsg())) {
			RemoteLogger.d(getClass().getSimpleName(), "API calling failed: " + baseWebAPI.getErrorMsg());
		} else {
			RemoteLogger.d(getClass().getSimpleName(), "API calling failed: " + 
					mContext.getResources().getString(R.string.disconnected_server_communication));
		}
		*/
	}

	protected void showToasts(@SuppressWarnings("rawtypes") CIBaseWebAPI baseWebAPI) {
        /*
		if (mActivity != null) {
			if (!Utils.isNullOrEmpty(baseWebAPI.getErrorMsg())) {
				Toast.makeText(mActivity, baseWebAPI.getErrorMsg(), Toast.LENGTH_LONG).show();
			} else {
				Toast.makeText(mActivity, mActivity.getResources().getString(R.string.disconnected_server_communication), Toast.LENGTH_LONG).show();
			}
		}
		*/
	}
	
	public void onCanceled(T webapi) {
//		RemoteLogger.d(getClass().getSimpleName(), "API calling canceled.");
	}
}
