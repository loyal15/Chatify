package im.chatify.page.chat;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import im.chatify.R;
import im.chatify.common.widgets.Emoji;
import im.chatify.model.CIBusiness;
import im.chatify.model.CICategory;
import im.chatify.xabber.android.data.extension.capability.ClientSoftware;
import im.chatify.xabber.android.data.message.AbstractChat;
import im.chatify.xabber.android.data.message.MessageManager;
import im.chatify.xabber.android.data.roster.AbstractContact;
import im.chatify.xabber.android.data.roster.GroupManager;
import im.chatify.xabber.android.utils.StringUtils;
import in.co.madhur.chatbubblesdemo.AndroidUtilities;

/**
 * Created by administrator on 9/23/15.
 */
public class CIChatHistoryHolder {

    private ImageView       mIVProfile;
    private TextView        mTVUserName;
    private TextView        mTVLastMessage;
    private TextView        mTVCreatedDate;

    public CIChatHistoryHolder(View view) {
        mIVProfile = (ImageView)view.findViewById(R.id.ivProfile);
        mTVUserName = (TextView)view.findViewById(R.id.tvUserName);
        mTVLastMessage = (TextView)view.findViewById(R.id.tvLastMessage);
        mTVCreatedDate = (TextView)view.findViewById(R.id.tvCreatedDate);
    }

    public void setChatHistory(Context context, AbstractContact contact) {

        mTVCreatedDate.setVisibility(View.INVISIBLE);

        mTVUserName.setText(contact.getName());

        ClientSoftware clientSoftware = contact.getClientSoftware();

        MessageManager messageManager = MessageManager.getInstance();

        String statusText = "";

        if (messageManager.hasActiveChat(contact.getAccount(), contact.getUser())) {

            AbstractChat chat = messageManager.getChat(contact.getAccount(), contact.getUser());

            statusText = chat.getLastText().trim();

            if (!statusText.isEmpty()) {

                mTVCreatedDate.setText(StringUtils.getSmartTimeText(context, chat.getLastTime()));
                mTVCreatedDate.setVisibility(View.VISIBLE);

//                if (!chat.isLastMessageIncoming()) {
//                    viewHolder.outgoingMessageIndicator.setText(context.getString(R.string.sender_is_you) + ": ");
//                    viewHolder.outgoingMessageIndicator.setVisibility(View.VISIBLE);
//                    viewHolder.outgoingMessageIndicator.setTextColor(accountMainColors[colorLevel]);
//                }

//                viewHolder.smallRightIcon.setImageResource(R.drawable.ic_client_small);
//                viewHolder.smallRightIcon.setVisibility(View.VISIBLE);
//                viewHolder.smallRightIcon.setImageLevel(clientSoftware.ordinal());
//                viewHolder.largeClientIcon.setVisibility(View.GONE);
            } else {
                mTVCreatedDate.setVisibility(View.INVISIBLE);
            }
        } else {
            statusText = contact.getStatusText().trim();
        }

        if (statusText.isEmpty()) {
            mTVLastMessage.setVisibility(View.GONE);
        } else {
            mTVLastMessage.setVisibility(View.VISIBLE);
            mTVLastMessage.setText(Emoji.replaceEmoji(statusText, mTVLastMessage.getPaint().getFontMetricsInt(), AndroidUtilities.dp(16)));
        }


        ArrayList<CICategory> categories = CICategory.getCategoryList(context, true);

        if (contact.getUser().equals("4915735983515@chatify.im"))
            mIVProfile.setImageResource(CIBusiness.getBusinessDrawableIdFromType(CIBusiness.TYPE_BUSINESS_SHOP));
        else if (contact.getUser().equals("alice@chatify.im"))
            mIVProfile.setImageResource(CIBusiness.getBusinessDrawableIdFromType(CIBusiness.TYPE_BUSINESS_SHOP));
        else if (contact.getUser().equals("4915735983514@chatify.im"))
            mIVProfile.setImageResource(CIBusiness.getBusinessDrawableIdFromType(CIBusiness.TYPE_BUSINESS_BUSINESS));
    }

    public void setAccountConfiguration(AccountConfiguration configuration) {

        mTVUserName.setText(GroupManager.getInstance().getGroupName(configuration.getAccount(), configuration.getUser()));
    }
}
