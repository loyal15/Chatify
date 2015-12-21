package im.chatify.page.chat;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import im.chatify.R;

/**
 * Created by administrator on 9/7/15.
 */
public class CIBusinessChatListView extends LinearLayout {

    public CIBusinessChatListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {

        ViewGroup view = null;
        view = (ViewGroup) LayoutInflater.from(context).inflate(R.layout.view_chathistory_business, (ViewGroup) null);
        view.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        addView(view);

        initUI(context, view);
    }

    private void initUI(Context context, ViewGroup view) {


    }
}
