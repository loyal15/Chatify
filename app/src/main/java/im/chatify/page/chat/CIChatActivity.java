package im.chatify.page.chat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.hb.views.PinnedSectionListView;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

import im.chatify.CIApp;
import im.chatify.R;
import im.chatify.common.CIGPSClient;
import im.chatify.common.CINotificationCenter;
import im.chatify.common.CIUtils;
import im.chatify.common.ui.CICommonActivity;
import im.chatify.common.widgets.Emoji;
import im.chatify.common.widgets.EmojiView;
import im.chatify.common.widgets.SizeNotifierRelativeLayout;
import im.chatify.model.CIConst;
import im.chatify.service.xmpp.CIIQStanzaManager;
import im.chatify.xabber.android.data.LogManager;
import im.chatify.xabber.android.data.account.AccountManager;
import im.chatify.xabber.android.data.account.OnAccountChangedListener;
import im.chatify.xabber.android.data.entity.BaseEntity;
import im.chatify.xabber.android.data.extension.cs.ChatStateManager;
import im.chatify.xabber.android.data.message.MessageManager;
import im.chatify.xabber.android.data.message.OnChatChangedListener;
import im.chatify.xabber.android.data.roster.AbstractContact;
import im.chatify.xabber.android.data.roster.OnContactChangedListener;
import im.chatify.xabber.android.data.roster.RosterManager;
import in.co.madhur.chatbubblesdemo.AndroidUtilities;
import io.codetail.animation.SupportAnimator;
import io.codetail.animation.ViewAnimationUtils;
import io.codetail.widget.RevealFrameLayout;


/**
 * Created by administrator on 10/5/15.
 */
public class CIChatActivity extends CICommonActivity implements OnChatChangedListener, OnContactChangedListener, OnAccountChangedListener, SizeNotifierRelativeLayout.SizeNotifierRelativeLayoutDelegate, CINotificationCenter.NotificationCenterDelegate {

    public static final String KEY_USER = "KEY_USER";
    public static final String KEY_ACCOUNT = "KEY_ACCOUNT";
    private static final int MINIMUM_MESSAGES_TO_LOAD = 10;

    private PinnedSectionListView   mLVMessage;
    private AppCompatEditText       mETMessage;
    private TextView                mTVUserName;
    private ImageView               mIVOnlineStatus;
    private Toolbar                 mToolbar;
    private ImageView               mIVInfo;
//    private ImageView               mIVSendMessage;
    private ImageView               mIVEmoji;
    private ImageView               mIVAttachment;
    private RevealFrameLayout       mVAttachment;

    private CIChatAdapter           mChatAdapter;
    private BaseEntity              mInitialChat = null;
    private String                  mAccount;
    private String                  mUser;
    private SupportAnimator         mShowAttachmentAnimator;
    private WindowManager.LayoutParams mWindowLayoutParams;
    private EmojiView               mEmojiView;
    private boolean                 mShowingEmoji;
    private boolean                 mShowAttachmentView;
    private int                     mKeyboardHeight;
    private boolean                 mKeyboardVisible;
    private SizeNotifierRelativeLayout mSizeNotifierRelativeLayout;
    private int                     mMessageType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cichat);
    }

    @Override
    protected void initUI() {

        AndroidUtilities.statusBarHeight = getStatusBarHeight();

        initData();

        mToolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationIcon(R.mipmap.ic_back_arrow);
        mToolbar.setNavigationOnClickListener(mNavigationClickListener);

        mIVEmoji = (ImageView)findViewById(R.id.ivEmoji);
        mIVEmoji.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (mShowAttachmentView)
                    showAttachmentView(false);

                showEmojiPopup(!mShowingEmoji);
            }
        });

        mLVMessage = (PinnedSectionListView)findViewById(R.id.lvMessage);
        mETMessage = (AppCompatEditText)findViewById(R.id.etMessage);

        /*
        mETMessage.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {

                if ((keyEvent != null && (keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (i == EditorInfo.IME_ACTION_DONE)) {
                    sendMessage();
                }

                return false;
            }
        });
        */


        mETMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if (charSequence.toString().length() > 0) {

                    if (mMessageType != 1) {
                        mMessageType = 1;
                        CIUtils.imageViewAnimatedChange(CIChatActivity.this, mIVAttachment, BitmapFactory.decodeResource(getResources(), R.mipmap.ic_chat_sendmessage), 100);
                    }

                } else {

                    if (mMessageType != 0) {
                        mMessageType = 0;
                        CIUtils.imageViewAnimatedChange(CIChatActivity.this, mIVAttachment, BitmapFactory.decodeResource(getResources(), R.mipmap.ic_chat_attachment), 100);
                    }
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        mETMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (mShowingEmoji)
                    hideEmojiPopup();
            }
        });

        mTVUserName = (TextView)findViewById(R.id.tvToUserName);
        mIVOnlineStatus = (ImageView)findViewById(R.id.ivOnlineStatus);
        mIVInfo = (ImageView)findViewById(R.id.ivInfo);

        mIVAttachment = (ImageView)findViewById(R.id.ivAttachment);
        mIVAttachment.setOnClickListener(mIVAttachmentClickListener);

//        mIVSendMessage = (ImageView)findViewById(R.id.ivSendMessage);
//        mIVSendMessage.setOnClickListener(mIVSendClickListener);

        mChatAdapter = new CIChatAdapter(this, 0, mAccount, mUser);
        mLVMessage.setAdapter(mChatAdapter);

        showHistory(mAccount, mUser);

        mSizeNotifierRelativeLayout = (SizeNotifierRelativeLayout) findViewById(R.id.rlParent);
        mSizeNotifierRelativeLayout.delegate = this;

        CINotificationCenter.getInstance().addObserver(this, CINotificationCenter.emojiDidLoaded);

        //Added by Loyal
        mLVMessage.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mShowAttachmentView)
                    showAttachmentView(false);
            }
        });

        mLVMessage.setOnNoItemClickListener(new PinnedSectionListView.OnNoItemClickListener() {
            @Override
            public void onNoItemClicked() {
                if (mShowAttachmentView)
                    showAttachmentView(false);
            }
        });
        //////////////////
    }

    private void showAttachmentOptionMenu(boolean show) {

//        mShowAttachment = show;

        if (show) {

            int cx = (mVAttachment.getLeft() + mVAttachment.getRight());
            int cy = (mVAttachment.getTop() + mVAttachment.getBottom());

            // get the final radius for the clipping circle
            int dx = Math.max(cx, mVAttachment.getWidth() - cx);
            int dy = Math.max(cy, mVAttachment.getHeight() - cy);
            float finalRadius = (float) Math.hypot(dx, dy);

            mShowAttachmentAnimator =
                    ViewAnimationUtils.createCircularReveal(mVAttachment, cx, cy, 0, finalRadius);
            mVAttachment.setVisibility(View.VISIBLE);

        } else {

            mShowAttachmentAnimator = mShowAttachmentAnimator.reverse();
            mShowAttachmentAnimator.addListener(new SupportAnimator.AnimatorListener() {
                @Override
                public void onAnimationStart() {

                }

                @Override
                public void onAnimationEnd() {
                    mShowAttachmentAnimator = null;
                    mVAttachment.setVisibility(View.INVISIBLE);
                }

                @Override
                public void onAnimationCancel() {

                }

                @Override
                public void onAnimationRepeat() {

                }
            });

        }

        mShowAttachmentAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        mShowAttachmentAnimator.setDuration(500);
        mShowAttachmentAnimator.start();
    }

    private void scrollsToBottom() {
        /*
        mChatAdapter.notifyDataSetChanged();
        View lastView = mLVMessage.getChildAt(mLVMessage.getChildCount() - 1);

        if (lastView == null || lastView.getBottom() <= mLVMessage.getHeight()) {
            mLVMessage.setSelection(mChatAdapter.getCount() - 1);
            mLVMessage.smoothScrollToPosition(mChatAdapter.getCount() - 1);
        }
        */

        mLVMessage.post(new Runnable() {
            @Override
            public void run() {

                int pos = mChatAdapter.getCount() - 1;

                mLVMessage.setSelection(pos);
                View v = mLVMessage.getChildAt(pos);

                if (v != null)
                    v.requestFocus();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        CIApp.getInstance().addUIListener(OnChatChangedListener.class, this);
        CIApp.getInstance().addUIListener(OnContactChangedListener.class, this);
        CIApp.getInstance().addUIListener(OnAccountChangedListener.class, this);

        updateChatList();
    }

    @Override
    public void onPause() {
        super.onPause();

        ChatStateManager.getInstance().onPaused(mInitialChat.getAccount(), mInitialChat.getUser());

        CIApp.getInstance().removeUIListener(OnChatChangedListener.class, this);
        CIApp.getInstance().removeUIListener(OnContactChangedListener.class, this);
        CIApp.getInstance().removeUIListener(OnAccountChangedListener.class, this);

        MessageManager.getInstance().removeVisibleChat();
    }

    @Override
    protected void initData() {

        Intent intent = getIntent();
        mAccount = intent.getStringExtra(KEY_ACCOUNT);
        mUser = intent.getStringExtra(KEY_USER);

        if (mAccount != null && mUser != null) {
            mInitialChat = new BaseEntity(mAccount, mUser);
        }

    }

    @Override
    public boolean supportOffline() {
        return false;
    }

    private void sendMessage() {

        String text = mETMessage.getText().toString().trim();

        if (text.isEmpty()) {
            return;
        }

        clearMessageText();

        sendMessage(text);

    }

    private void clearMessageText() {
        mETMessage.setText("");
    }

    private void sendMessage(String text) {
        MessageManager.getInstance().sendMessage(mAccount, mUser, text);
        updateChatList();
    }

    private void updateChatList() {
        updateChatUserInfo();
        mChatAdapter.onChange();
        scrollDown();
    }

    private void updateChatUserInfo() {

        AbstractContact contact = RosterManager.getInstance().getBestContact(mAccount, mUser);

        mTVUserName.setText(AccountManager.getInstance().getNickName(contact.getName()));

        int statusLevel = contact.getStatusMode().getStatusLevel();

        if (statusLevel == 6) {
            mIVOnlineStatus.setVisibility(View.GONE);
        } else {
            mIVOnlineStatus.setVisibility(View.VISIBLE);
            mIVOnlineStatus.setImageLevel(statusLevel);
        }
    }

    private void showHistory(String account, String user) {
        MessageManager.getInstance().requestToLoadLocalHistory(account, user);
//        MessageArchiveManager.getInstance().requestHistory(account, user, MINIMUM_MESSAGES_TO_LOAD, 0);

        CIIQStanzaManager.getInstance(CIChatActivity.this).sendIQStanzaForRequestHistory(account, user);
    }


    private void scrollChat(int itemCountBeforeUpdate) {
        scrollDown();
    }

    private void scrollDown() {
        mLVMessage.smoothScrollToPosition(mChatAdapter.getCount() - 1);
    }

    /**
     * Show or hide the emoji popup
     *
     * @param show
     */

    private void showAttachmentView(boolean show) {

        mShowAttachmentView = show;

        if (show) {

            //Added by Loyal
            mETMessage.setEnabled(false);
            ////////////////

            if (mVAttachment == null) {

                mVAttachment = (RevealFrameLayout) LayoutInflater.from(getBaseContext()).inflate(R.layout.view_chat_attachment, null);

                mWindowLayoutParams = new WindowManager.LayoutParams();
                mWindowLayoutParams.gravity = Gravity.BOTTOM | Gravity.LEFT;
                if (Build.VERSION.SDK_INT >= 21) {
                    mWindowLayoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ERROR;
                } else {
                    mWindowLayoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_PANEL;
                    mWindowLayoutParams.token = getWindow().getDecorView().getWindowToken();
                }
                mWindowLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;


                //Added by Loyal 12/18/2015
                TextView mTVShareLocation = (TextView)mVAttachment.findViewById(R.id.tvShareLocation);
                mTVShareLocation.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        LogManager.i("ShareLocation", "ShareLocation Tapped ---- ");

                        if(mAccount != null) {
                            LogManager.i("Account Info", mAccount);

                            String verboseName = AccountManager.getInstance().getVerboseName(mAccount);
                            LogManager.i("Account Verbose", verboseName);

                            double latitude = CIGPSClient.getInstance(getApplicationContext()).getLatitude();
                            double longitude = CIGPSClient.getInstance(getApplicationContext()).getLongitude();

                            LogManager.i(latitude, "Account Latitude");
                            LogManager.i(longitude, "Account Longitude");

                            //String text = mETMessage.getText().toString().trim();

                            try {
                                Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
                                List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);

                                String country = addresses.get(0).getCountryName();
                                String locality = addresses.get(0).getLocality();
                                sendMessage(String.valueOf(latitude), String.valueOf(longitude), country, locality, "");

                            }catch (IOException ex){
                                System.err.print(ex);
                                LogManager.i(ex, "Account Address Error");

                                sendMessage(String.valueOf(latitude), String.valueOf(longitude), "", "", "");
                            }

                            clearMessageText();
                            showAttachmentView(false);

                        }
                    }
                });
            }

            final int currentHeight;

            if (mKeyboardHeight <= 0)
                mKeyboardHeight = CIApp.getInstance().getSharedPreferences("emoji", 0).getInt("kbd_height", AndroidUtilities.dp(200));

            currentHeight = mKeyboardHeight;

            WindowManager wm = (WindowManager) CIApp.getInstance().getSystemService(Activity.WINDOW_SERVICE);

            mWindowLayoutParams.height = currentHeight;
            mWindowLayoutParams.width = AndroidUtilities.displaySize.x;

            try {
                if (mVAttachment.getParent() != null) {
                    wm.removeViewImmediate(mVAttachment);
                }
            } catch (Exception e) {
                Log.e(CIConst.TAG, e.getMessage());
            }

            try {
                wm.addView(mVAttachment, mWindowLayoutParams);
            } catch (Exception e) {
                Log.e(CIConst.TAG, e.getMessage());
                return;
            }

            if (!mKeyboardVisible) {
                if (mSizeNotifierRelativeLayout != null) {
                    mSizeNotifierRelativeLayout.setPadding(0, 0, 0, currentHeight);
                }

                return;
            }

            int cx = (mVAttachment.getLeft() + mVAttachment.getRight());
            int cy = (mVAttachment.getTop() + mVAttachment.getBottom());

            // get the final radius for the clipping circle
            int dx = Math.max(cx, mVAttachment.getWidth() - cx);
            int dy = Math.max(cy, mVAttachment.getHeight() - cy);
            float finalRadius = (float) Math.hypot(dx, dy);

//            mShowAttachmentAnimator =
//                    ViewAnimationUtils.createCircularReveal(mVAttachment, cx, cy, 0, finalRadius);
//            mVAttachment.setVisibility(View.VISIBLE);

        } else {

            //Added by Loyal
            mETMessage.setEnabled(true);
            ////////////////

            removeAttachmentView();
            if (mSizeNotifierRelativeLayout != null) {
                mSizeNotifierRelativeLayout.post(new Runnable() {
                    public void run() {
                        if (mSizeNotifierRelativeLayout != null) {
                            mSizeNotifierRelativeLayout.setPadding(0, 0, 0, 0);
                        }
                    }
                });
            }
        }
    }

    private void removeAttachmentView() {

        if (mVAttachment == null) {
            return;
        }
        try {
            if (mVAttachment.getParent() != null) {
                WindowManager wm = (WindowManager) CIApp.getInstance().getSystemService(Context.WINDOW_SERVICE);
                wm.removeViewImmediate(mVAttachment);
            }
        } catch (Exception e) {
            Log.e(CIConst.TAG, e.getMessage());
        }
    }

    private void showEmojiPopup(boolean show) {
        mShowingEmoji = show;

        if (show) {
            if (mEmojiView == null) {

                mEmojiView = new EmojiView(this);

                mEmojiView.setListener(new EmojiView.Listener() {
                    public void onBackspace() {
                        mETMessage.dispatchKeyEvent(new KeyEvent(0, 67));
                    }

                    public void onEmojiSelected(String symbol) {
                        int i = mETMessage.getSelectionEnd();
                        if (i < 0) {
                            i = 0;
                        }
                        try {
                            CharSequence localCharSequence = Emoji.replaceEmoji(symbol, mETMessage.getPaint().getFontMetricsInt(), AndroidUtilities.dp(20));
                            mETMessage.setText(mETMessage.getText().insert(i, localCharSequence));
                            int j = i + localCharSequence.length();
                            mETMessage.setSelection(j, j);
                        } catch (Exception e) {
                            Log.e(CIConst.TAG, "Error showing emoji");
                        }
                    }
                });

                mWindowLayoutParams = new WindowManager.LayoutParams();
                mWindowLayoutParams.gravity = Gravity.BOTTOM | Gravity.LEFT;
                if (Build.VERSION.SDK_INT >= 21) {
                    mWindowLayoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ERROR;
                } else {
                    mWindowLayoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_PANEL;
                    mWindowLayoutParams.token = getWindow().getDecorView().getWindowToken();
                }

                mWindowLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
            }

            final int currentHeight;

            if (mKeyboardHeight <= 0)
                mKeyboardHeight = CIApp.getInstance().getSharedPreferences("emoji", 0).getInt("kbd_height", AndroidUtilities.dp(200));

            currentHeight = mKeyboardHeight;

            WindowManager wm = (WindowManager) CIApp.getInstance().getSystemService(Activity.WINDOW_SERVICE);

            mWindowLayoutParams.height = currentHeight;
            mWindowLayoutParams.width = AndroidUtilities.displaySize.x;

            try {
                if (mEmojiView.getParent() != null) {
                    wm.removeViewImmediate(mEmojiView);
                }
            } catch (Exception e) {
                Log.e(CIConst.TAG, e.getMessage());
            }

            try {
                wm.addView(mEmojiView, mWindowLayoutParams);
            } catch (Exception e) {
                Log.e(CIConst.TAG, e.getMessage());
                return;
            }

            if (!mKeyboardVisible) {
                if (mSizeNotifierRelativeLayout != null) {
                    mSizeNotifierRelativeLayout.setPadding(0, 0, 0, currentHeight);
                }

                return;
            }

        }
        else {
            removeEmojiWindow();
            if (mSizeNotifierRelativeLayout != null) {
                mSizeNotifierRelativeLayout.post(new Runnable() {
                    public void run() {
                        if (mSizeNotifierRelativeLayout != null) {
                            mSizeNotifierRelativeLayout.setPadding(0, 0, 0, 0);
                        }
                    }
                });
            }
        }


    }


    /**
     * Remove emoji window
     */
    private void removeEmojiWindow() {
        if (mEmojiView == null) {
            return;
        }
        try {
            if (mEmojiView.getParent() != null) {
                WindowManager wm = (WindowManager) CIApp.getInstance().getSystemService(Context.WINDOW_SERVICE);
                wm.removeViewImmediate(mEmojiView);
            }
        } catch (Exception e) {
            Log.e(CIConst.TAG, e.getMessage());
        }
    }



    /**
     * Hides the emoji popup
     */
    public void hideEmojiPopup() {

        if (mShowingEmoji) {
            showEmojiPopup(false);
        }
    }

    /**
     * Check if the emoji popup is showing
     *
     * @return
     */
    public boolean isEmojiPopupShowing() {
        return mShowingEmoji;
    }

    /**
     * Get the system status bar height
     * @return
     */
    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    /**
     * Updates emoji views when they are complete loading
     *
     * @param id
     * @param args
     */
    @Override
    public void didReceivedNotification(int id, Object... args) {
        if (id == CINotificationCenter.emojiDidLoaded) {
            if (mEmojiView != null) {
                mEmojiView.invalidateViews();
            }

            if (mLVMessage!= null) {
                mLVMessage.invalidateViews();
            }
        }
    }

    @Override
    public void onSizeChanged(int height) {

        Rect localRect = new Rect();
        getWindow().getDecorView().getWindowVisibleDisplayFrame(localRect);

        WindowManager wm = (WindowManager) CIApp.getInstance().getSystemService(Activity.WINDOW_SERVICE);
        if (wm == null || wm.getDefaultDisplay() == null) {
            return;
        }

        if (height > AndroidUtilities.dp(50) && mKeyboardVisible) {
            mKeyboardHeight = height;
            CIApp.getInstance().getSharedPreferences("emoji", 0).edit().putInt("kbd_height", mKeyboardHeight).commit();
        }


        if (mShowingEmoji) {
            int newHeight = 0;

            newHeight = mKeyboardHeight;

            if (mWindowLayoutParams.width != AndroidUtilities.displaySize.x || mWindowLayoutParams.height != newHeight) {
                mWindowLayoutParams.width = AndroidUtilities.displaySize.x;
                mWindowLayoutParams.height = newHeight;

                wm.updateViewLayout(mEmojiView, mWindowLayoutParams);
                if (!mKeyboardVisible) {
                    mSizeNotifierRelativeLayout.post(new Runnable() {
                        @Override
                        public void run() {
                            if (mSizeNotifierRelativeLayout != null) {
                                mSizeNotifierRelativeLayout.setPadding(0, 0, 0, mWindowLayoutParams.height);
                                mSizeNotifierRelativeLayout.requestLayout();
                            }
                        }
                    });
                }
            }
        }


        boolean oldValue = mKeyboardVisible;
        mKeyboardVisible = height > 0;
        if (mKeyboardVisible && mSizeNotifierRelativeLayout.getPaddingBottom() > 0) {
            showEmojiPopup(false);
        } else if (!mKeyboardVisible && mKeyboardVisible != oldValue && mShowingEmoji) {
            showEmojiPopup(false);
        }

    }
    @Override
    public void onAccountsChanged(Collection<String> accounts) {

        updateChatList();
    }

    @Override
    public void onContactsChanged(Collection<BaseEntity> entities) {

        updateChatList();
    }

    @Override
    public void onChatChanged(String account, String user, boolean incoming) {

        updateChatList();
    }

    private View.OnClickListener mIVSendClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            sendMessage();
        }
    };

    private View.OnClickListener mNavigationClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            finish();
        }
    };

    private View.OnClickListener mIVAttachmentClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            if (mETMessage.getText().toString().length() == 0) {
                if (mShowingEmoji)
                    showEmojiPopup(false);

                showAttachmentView(!mShowAttachmentView);
            } else {
                sendMessage();
            }
        }
    };

    public class ReverseInterpolator implements Interpolator {
        @Override
        public float getInterpolation(float paramFloat) {
            return Math.abs(paramFloat -1f);
        }
    }

    //Added by Loyal 12/18/2015
    private void sendMessage(String latitude, String longitude, String country, String locality, String text) {
        MessageManager.getInstance().sendMessage(mAccount, mUser, latitude, longitude, country, locality, text);
        updateChatList();
    }

}
