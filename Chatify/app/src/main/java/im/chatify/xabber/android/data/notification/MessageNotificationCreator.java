package im.chatify.xabber.android.data.notification;

import android.app.PendingIntent;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.StyleSpan;

import java.util.List;

import im.chatify.CIApp;
import im.chatify.R;
import im.chatify.xabber.android.data.extension.avatar.AvatarManager;
import im.chatify.xabber.android.data.extension.muc.MUCManager;
import im.chatify.xabber.android.data.message.MessageItem;
import im.chatify.xabber.android.data.message.chat.ChatManager;
import im.chatify.xabber.android.data.roster.RosterManager;
import im.chatify.xabber.android.utils.StringUtils;

public class MessageNotificationCreator {

    private static int UNIQUE_REQUEST_CODE = 0;
    private final CIApp application;
//    private final AccountPainter accountPainter;
    private List<MessageNotification> messageNotifications;

    public MessageNotificationCreator() {
        application = CIApp.getInstance();
//        accountPainter = new AccountPainter(application);

    }

    public android.app.Notification notifyMessageNotification(List<MessageNotification> messageNotifications,
                                                              MessageItem messageItem) {
        this.messageNotifications = messageNotifications;

//        if (messageNotifications.isEmpty()) {
//            return null;
//        }

        return null;

        /*
        int messageCount = 0;

        for (MessageNotification messageNotification : messageNotifications) {
            messageCount += messageNotification.getCount();
        }


        MessageNotification message = messageNotifications.get(messageNotifications.size() - 1);

        boolean showText  = ChatManager.getInstance().isShowText(message.getAccount(), message.getUser());


        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(application);
        notificationBuilder.setContentTitle(getTitle(message, messageCount));
        notificationBuilder.setContentText(getText(message, showText));
        notificationBuilder.setSubText(message.getAccount());

        notificationBuilder.setTicker(getText(message, showText));

        notificationBuilder.setSmallIcon(getSmallIcon());
        notificationBuilder.setLargeIcon(getLargeIcon(message));

        notificationBuilder.setWhen(message.getTimestamp().getTime());
//        notificationBuilder.setColor(accountPainter.getAccountMainColor(message.getAccount()));
        notificationBuilder.setStyle(getStyle(message, messageCount, showText));

        notificationBuilder.setContentIntent(getIntent(message));

        notificationBuilder.setCategory(NotificationCompat.CATEGORY_MESSAGE);
        notificationBuilder.setPriority(NotificationCompat.PRIORITY_HIGH);

        NotificationManager.addEffects(notificationBuilder, messageItem);

        return notificationBuilder.build();
        */
    }

    private CharSequence getTitle(MessageNotification message, int messageCount) {
        if (isFromOneContact()) {
            return getSingleContactTitle(message, messageCount);
        } else {
            return getMultiContactTitle(messageCount);
        }
    }

    private CharSequence getSingleContactTitle(MessageNotification message, int messageCount) {
        if (messageCount > 1) {
            return application.getString(R.string.chat_messages_from_contact,
                    messageCount, getTextForMessages(messageCount), getContactName(message));
        } else {
            return getContactName(message);
        }
    }

    private String getContactName(MessageNotification message) {
        return RosterManager.getInstance().getName(message.getAccount(), message.getUser());
    }

    private CharSequence getMultiContactTitle(int messageCount) {
        String messageText = getTextForMessages(messageCount);
        String contactText = StringUtils.getQuantityString(application.getResources(),
                R.array.chat_contact_quantity, messageNotifications.size());
        return application.getString(R.string.chat_status,
                messageCount, messageText, messageNotifications.size(), contactText);
    }

    private String getTextForMessages(int messageCount) {
        return StringUtils.getQuantityString(
                application.getResources(), R.array.chat_message_quantity, messageCount);
    }

    private CharSequence getText(MessageNotification message, boolean showText) {
        if (isFromOneContact()) {
            if (showText) {
                return message.getText();
            } else {
                return null;
            }
        } else {
            return getContactNameAndMessage(message, showText);
        }
    }

    private int getSmallIcon() {
        return R.drawable.ic_stat_chat;
    }

    private android.graphics.Bitmap getLargeIcon(MessageNotification message) {
        if (isFromOneContact()) {
            if (MUCManager.getInstance().hasRoom(message.getAccount(), message.getUser())) {
                return AvatarManager.getInstance().getRoomBitmap(message.getUser());
            } else {
                return AvatarManager.getInstance().getUserBitmap(message.getUser());
            }
        }
        return null;
    }

    private boolean isFromOneContact() {
        return messageNotifications.size() == 1;
    }

    private NotificationCompat.Style getStyle(MessageNotification message, int messageCount, boolean showText) {
        if (isFromOneContact()) {
            NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle();

            bigTextStyle.setBigContentTitle(getSingleContactTitle(message, messageCount));
            if (showText) {
                bigTextStyle.bigText(message.getText());
            }
            bigTextStyle.setSummaryText(message.getAccount());

            return bigTextStyle;
        } else {
            return getInboxStyle(messageCount, message.getAccount());
        }
    }

    private NotificationCompat.Style getInboxStyle(int messageCount, String accountName) {
        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();

        inboxStyle.setBigContentTitle(getMultiContactTitle(messageCount));

        for (int i = 1; i <= messageNotifications.size(); i++) {
            MessageNotification messageNotification = messageNotifications.get(messageNotifications.size() - i);

            boolean showTextForThisContact
                    = ChatManager.getInstance().isShowText(messageNotification.getAccount(), messageNotification.getUser());

            inboxStyle.addLine(getContactNameAndMessage(messageNotification, showTextForThisContact));
        }

        inboxStyle.setSummaryText(accountName);

        return inboxStyle;
    }

    private Spannable getContactNameAndMessage(MessageNotification messageNotification, boolean showText) {
        String userName = getContactName(messageNotification);

        Spannable spannableString;
        if (showText) {
            String contactAndMessage = application.getString(
                    R.string.chat_contact_and_message, userName, messageNotification.getText());
            spannableString = new SpannableString(contactAndMessage);

        } else {
            spannableString = new SpannableString(userName);
        }

        spannableString.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0, userName.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannableString;
    }

    private PendingIntent getIntent(MessageNotification message) {
        /*
        Intent backIntent = ContactList.createIntent(application);
        backIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        Intent intent = ChatViewer.createClearTopIntent(application, message.getAccount(), message.getUser());
        */
        Intent backIntent = new Intent();
        Intent intent = new Intent();

        return PendingIntent.getActivities(application, UNIQUE_REQUEST_CODE++,
                new Intent[]{backIntent, intent}, PendingIntent.FLAG_ONE_SHOT);
    }

}
