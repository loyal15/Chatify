package im.chatify.model;

import java.util.Date;

/**
 * Created by administrator on 10/5/15.
 */
public class CIChat {

    public static final int TYPE_MESSAGE_FROMME = 1;
    public static final int TYPE_MESSAGE_TOME = 2;

    public String   toJid;
    public String   fromJid;
    public Date     createdDate;
    public String   chatMessage;

    public int checkMessageType() {

        return TYPE_MESSAGE_FROMME;
    }
}
