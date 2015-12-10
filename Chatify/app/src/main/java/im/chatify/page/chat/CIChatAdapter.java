package im.chatify.page.chat;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.hb.views.PinnedSectionListView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import im.chatify.R;
import im.chatify.common.date.CIDateTimeUtils;
import im.chatify.xabber.android.data.extension.muc.MUCManager;
import im.chatify.xabber.android.data.message.MessageItem;
import im.chatify.xabber.android.data.message.MessageManager;

/**
 * Created by administrator on 9/23/15.
 */
public class CIChatAdapter extends ArrayAdapter<CIChatAdapter.CIItem> implements PinnedSectionListView.PinnedSectionListAdapter, UpdatableAdapter {

    public static final int VIEW_TYPE_INCOMING_MESSAGE = 1;
    public static final int VIEW_TYPE_OUTGOING_MESSAGE = 2;


    private Context                     mContext;
    private int                         mListItemId;
    private String                      mAccount;
    private String                      mUser;
    private String                      mNickName;
    private boolean                     mIsMuc;
    private List<CIItem>                mMessages = new ArrayList<CIItem>();

    public CIChatAdapter(Context context, int resource, String account, String user) {
        super(context, resource);

        mListItemId = resource;
        mContext = context;
        mAccount = account;
        mUser = user;

        mIsMuc = MUCManager.getInstance().hasRoom(account, user);

        if (mIsMuc) {
            mNickName = MUCManager.getInstance().getNickname(account, user);
        }
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        View row = convertView;

        CIChatHolder holder;

        CIItem chat = (CIItem) mMessages.get(position);

        if (chat.type == CIItem.SECTION) {

            LayoutInflater inflater = ((Activity)mContext).getLayoutInflater();
            row = inflater.inflate(R.layout.listitem_chat_date, parent, false);
            TextView tvDate = (TextView)row.findViewById(R.id.tvDate);
            tvDate.setText(CIDateTimeUtils.toChatHistoryDate(chat.keyDate));

        } else if (chat.type == CIItem.ITEM) {

            if ( row == null )
            {
                LayoutInflater inflater = ((Activity)mContext).getLayoutInflater();

                if (getItemType(chat.messageItem) == VIEW_TYPE_OUTGOING_MESSAGE)
                    row = inflater.inflate(R.layout.listitem_chat_you, parent, false);
                else if (getItemType(chat.messageItem) == VIEW_TYPE_INCOMING_MESSAGE)
                    row = inflater.inflate(R.layout.listitem_chat_friend, parent, false);

                holder = new CIChatHolder(row);
                holder.holderType = getItemType(chat.messageItem);

                row.setTag(holder);

            } else {

                holder = (CIChatHolder)row.getTag();

                if (holder.holderType != getItemType(chat.messageItem)) {

                    LayoutInflater inflater = ((Activity)mContext).getLayoutInflater();

                    if (getItemType(chat.messageItem) == VIEW_TYPE_OUTGOING_MESSAGE)
                        row = inflater.inflate(R.layout.listitem_chat_you, parent, false);
                    else if (getItemType(chat.messageItem) == VIEW_TYPE_INCOMING_MESSAGE)
                        row = inflater.inflate(R.layout.listitem_chat_friend, parent, false);

                    holder = new CIChatHolder(row);
                    holder.holderType = getItemType(chat.messageItem);

                    row.setTag(holder);
                }
            }

            int msgType = getItemType(chat.messageItem);
            holder.setChatMessage(mContext, chat.messageItem, msgType);
        }

        return row;
    }

    @Override
    public void onChange() {

        ArrayList<MessageItem> msgItems = new ArrayList<>(MessageManager.getInstance().getMessages(mAccount, mUser));

        for (int i = msgItems.size() - 1; i > -1; i --) {
            MessageItem messageItem = msgItems.get(i);

            if (messageItem.getAction() != null)
                msgItems.remove(messageItem);
        }

        Map<String, ArrayList<MessageItem>> maps = new TreeMap<String, ArrayList<MessageItem>>();

        for (int i = 0; i < msgItems.size(); i ++) {

            MessageItem item = msgItems.get(i);
            Date msgDate = item.getTimestamp();
            Date startDayOfMsgDate = CIDateTimeUtils.startDateOfDay(msgDate);

            ArrayList<MessageItem> items = maps.get(String.valueOf(startDayOfMsgDate.getTime()));

            if (items == null) {
                items = new ArrayList<MessageItem>();
                items.add(item);
                maps.put(String.valueOf(startDayOfMsgDate.getTime()), items);
            } else {
                items.add(item);
            }

        }

        /*
        Comparator<Date> comparator = new Comparator<Date>() {
            public int compare(Date date1, Date date2) {
                return date1.compareTo(date2);
            }
        };

        SortedSet<Date> keys = new TreeSet<Date>(comparator);
        keys.addAll(maps.keySet());
        */
        List sortedKeys=new ArrayList(maps.keySet());
        Collections.sort(sortedKeys);

        mMessages.clear();

        int listPosition = 0;
        int sectionPosition = 0;

        for (int i = 0; i < sortedKeys.size(); i ++) {

            String keyDateStr = (String)sortedKeys.get(i);

            CIItem section = new CIItem(CIItem.SECTION, CIDateTimeUtils.getDateFromInterval(keyDateStr));
            section.sectionPosition = sectionPosition;
            section.listPosition = listPosition++;

            mMessages.add(section);

            ArrayList<MessageItem> messageItems = (ArrayList<MessageItem>) maps.get(keyDateStr);

            for (int j = 0; j < messageItems.size(); j ++) {

                CIItem rowItem = new CIItem(CIItem.ITEM, messageItems.get(j));
                rowItem.sectionPosition = sectionPosition;
                rowItem.listPosition = listPosition++;
                mMessages.add(rowItem);
            }

            sectionPosition++;
        }

        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mMessages.size();
    }

    private int getItemType(MessageItem messageItem) {

        if (messageItem.isIncoming()) {
            if (mIsMuc&& messageItem.getResource().equals(mNickName)) {
                return VIEW_TYPE_OUTGOING_MESSAGE;
            }
            return VIEW_TYPE_INCOMING_MESSAGE;
        } else {
            return VIEW_TYPE_OUTGOING_MESSAGE;
        }
    }

    @Override public int getViewTypeCount() {
        return 2;
    }

    @Override public int getItemViewType(int position) {
        return mMessages.get(position).type;
    }

    @Override
    public boolean isItemViewTypePinned(int viewType) {
        return false;
    }


    static class CIItem {

        public static final int ITEM = 0;
        public static final int SECTION = 1;

        public final int            type;
        public MessageItem          messageItem;
        public Date                 keyDate;

        public int sectionPosition;
        public int listPosition;

        public CIItem(int type, Date date) {
            this.type = type;
            this.keyDate = date;
        }

        public CIItem(int type, MessageItem msg) {
            this.type = type;
            this.messageItem = msg;
        }

    }


}
