package im.chatify.webapi;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;

import com.afollestad.materialdialogs.MaterialDialog;

import im.chatify.R;

/**
 * Created by administrator on 6/23/15.
 */
public abstract class CIBaseWebAPI {

    protected Context           mContext;
    protected MaterialDialog    mProgressDlg;
    protected boolean           mSynchronous;
    protected boolean           mShowProgress = true;
    protected String            mDialogTitle;
    private boolean             mDialogCancelable;

    public CIBaseWebAPI(Context context, boolean synchronous) {
        mContext = context;
        mSynchronous = synchronous;
        mDialogTitle = context.getResources().getString(R.string.common_progress_title_loading);
    }

    public void showProgress(boolean show) {
        mShowProgress = show;
    }

    protected void showProgress() {
        if ( mShowProgress ) {
            mProgressDlg = new MaterialDialog.Builder(mContext)
                    .content(R.string.common_progress_title_loading)
                    .progress(true, 0)
                    .show();
        }
    }

    protected void dismissProgress() {
        try {
            if (mProgressDlg != null && mProgressDlg.isShowing())
                mProgressDlg.dismiss();
        } catch (Exception e) {}
    }

    private OnCancelListener mDialogCancelListener = new OnCancelListener() {
        @Override
        public void onCancel(DialogInterface dialog) {
            // Cancel network transfer

        }
    };

    public void setProgressTitle(String title) {
        mDialogTitle = title;
    }
}
