/**
 * Copyright (c) 2013, Redsolution LTD. All rights reserved.
 *
 * This file is part of Xabber project; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License, Version 3.
 *
 * Xabber is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License,
 * along with this program. If not, see http://www.gnu.org/licenses/.
 */
package im.chatify.xabber.android.data.message;

import android.text.Spannable;
import android.text.SpannableString;

import java.util.Date;

/**
 * Message item.
 *
 * @author alexander.ivanov
 */
public class MessageItem implements Comparable<MessageItem> {

    private final AbstractChat chat;
    /**
     * Contact's resource.
     */
    private final String resource;
    /**
     * Text representation.
     */
    private final String text;
    /**
     * Optional action. If set message represent not an actual message but some
     * action in the chat.
     */
    private final ChatAction action;
    private final boolean incoming;
    private final boolean unencypted;
    /**
     * Message was received from server side offline storage.
     */
    private final boolean offline;
    /**
     * Tag used to identify collection in server side message archive. Equals to
     * collection's start attribute.
     */
    private String tag;
    /**
     * Cached text populated with smiles and link.
     */
    private Spannable spannable;
    /**
     * Time when message was received or sent by Xabber.
     */
    private Date timestamp;
    /**
     * Time when message was created.
     */
    private Date delayTimestamp;
    /**
     * ID in database.
     */
    private Long id;
    /**
     * Error response received on send request.
     */
    private boolean error;
    /**
     * Receipt was received for sent message.
     */
    private boolean delivered;
    /**
     * Message was sent.
     */
    private boolean sent;
    /**
     * Message was shown to the user.
     */
    private boolean read;
    /**
     * Outgoing packet id.
     */
    private String packetID;

    /**
     * isImage
     */
    private boolean image;

    //Added by Loyal 12/21/2015
    /**
     *
     * @param latitude
     * @param longitude
     * @param Country
     * @param locality
     */
    private String latitude;
    private String longitude;
    private String country;
    private String locality;

    public MessageItem(AbstractChat chat, String tag, String resource,
                       String text, ChatAction action, Date timestamp,
                       Date delayTimestamp, boolean incoming, boolean read, boolean sent,
                       boolean error, boolean delivered, boolean unencypted,
                       boolean offline) {
        this.chat = chat;
        this.tag = tag;
        this.resource = resource;
        this.text = text;
        this.action = action;
        this.timestamp = timestamp;
        this.delayTimestamp = delayTimestamp;
        this.incoming = incoming;
        this.read = read;
        this.sent = sent;
        this.error = error;
        this.delivered = delivered;
        this.unencypted = unencypted;
        this.offline = offline;
        this.id = null;
        this.packetID = null;
        //this.image=image;

        this.latitude = null;
        this.longitude = null;
        this.country = null;
        this.locality = null;
    }

    public AbstractChat getChat() {
        return chat;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getResource() {
        return resource;
    }

    public String getText() {
        return text;
    }

    public Spannable getSpannable() {
        if (spannable == null) {
            spannable = new SpannableString(text);
        }
        return spannable;
    }

    public ChatAction getAction() {
        return action;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public Date getDelayTimestamp() {
        return delayTimestamp;
    }

    public boolean isIncoming() {
        return incoming;
    }

    public boolean isError() {
        return error;
    }

    public boolean isDelivered() {
        return delivered;
    }

    public boolean isUnencypted() {
        return unencypted;
    }

    public boolean isOffline() {
        return offline;
    }

    public boolean isSent() {
        return sent;
    }

    public boolean isRead() {
        return read;
    }

    public Long getId() {
        return id;
    }

    void setId(Long id) {
        this.id = id;
    }

    public String getPacketID() {
        return packetID;
    }

    public void setPacketID(String packetID) {
        this.packetID = packetID;
    }

    void markAsError() {
        error = true;
    }

    void markAsSent() {
        sent = true;
    }

    void setSentTimeStamp(Date timestamp) {
        this.delayTimestamp = this.timestamp;
        this.timestamp = timestamp;
    }

    void markAsRead() {
        read = true;
    }

    void markAsDelivered() {
        delivered = true;
    }

    public boolean isImage() {
        return image;
    }

    //Added by Loyal
    public String getLatitude(){
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude(){
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getCountry(){
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getLocality(){
        return locality;
    }

    public void setLocality(String locality) {
        this.locality = locality;
    }

    @Override
    public int compareTo(MessageItem another) {
        return timestamp.compareTo(another.timestamp);
    }

}
