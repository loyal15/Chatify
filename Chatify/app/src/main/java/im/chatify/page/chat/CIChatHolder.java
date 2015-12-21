package im.chatify.page.chat;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Date;

import im.chatify.R;
import im.chatify.common.date.CIDateTimeUtils;
import im.chatify.common.widgets.Emoji;
import im.chatify.xabber.android.data.message.MessageItem;
import in.co.madhur.chatbubblesdemo.AndroidUtilities;

/**
 * Created by administrator on 9/23/15.
 */
public class CIChatHolder {

    private TextView        mTVMessage;
    private TextView        mTVCreatedDate;
    private TextView        mTVMeasureCreatedDate;
    private ImageView       mIVSentFlag;
    public int              holderType;

    public CIChatHolder(View view) {
        mTVMessage = (TextView)view.findViewById(R.id.tvMessage);
        mTVCreatedDate = (TextView)view.findViewById(R.id.tvCreatedDate);
        mIVSentFlag = (ImageView)view.findViewById(R.id.ivSentFlag);
        mTVMeasureCreatedDate = (TextView)view.findViewById(R.id.tvMeasureCreatedDate);
    }

    public void setChatMessage(Context context, MessageItem messageItem, int msgType) {

//        final Spannable spannable = messageItem.getSpannable();
        mTVMessage.setText(Emoji.replaceEmoji(messageItem.getText(), mTVMessage.getPaint().getFontMetricsInt(), AndroidUtilities.dp(16)));


        String time = CIDateTimeUtils.getShortTime(messageItem.getTimestamp());

        Date delayTimestamp = messageItem.getDelayTimestamp();
        if (delayTimestamp != null) {
            time = CIDateTimeUtils.getShortTime(delayTimestamp);
        }

        mTVCreatedDate.setText(time);
        mTVMeasureCreatedDate.setText(time);

        if (msgType == CIChatAdapter.VIEW_TYPE_OUTGOING_MESSAGE) {
            if (messageItem.isSent()) {
                mIVSentFlag.setVisibility(View.VISIBLE);
            } else {
                mIVSentFlag.setVisibility(View.GONE);
            }
        }
    }
}
