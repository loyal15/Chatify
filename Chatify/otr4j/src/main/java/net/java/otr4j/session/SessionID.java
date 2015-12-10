/*
 * otr4j, the open source java otr library.
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */

package net.java.otr4j.session;

/**
 * @author George Politis
 */
public final class SessionID {

    /**
     * A unique ID for an OTR session between two accounts.
     *
     * @param localAccountID the local account used for this conversation
     * @param remoteUserID the remote user on the other side of the conversation
     * @param protocolName the messaging protocol used for the conversation
     */
    public SessionID(String localAccountID, String remoteUserID, String protocolName) {
        this.localAccountID = localAccountID;
        this.remoteUserID = remoteUserID;
        this.protocolName = protocolName;
    }

    private final String localAccountID;
    private final String remoteUserID;
    private final String protocolName;

    public static final SessionID Empty = new SessionID(null, null, null);

    /**
     *
     * @return the {@code String} representing the local account
     */
    public String getAccountID() {
        return localAccountID;
    }

    /**
     *
     * @return the {@code String} representing the remote user
     */
    public String getUserID() {
        return remoteUserID;
    }

    public String getProtocolName() {
        return protocolName;
    }

    @Override
    public String toString() {
        return localAccountID + '_' + protocolName + '_' + remoteUserID;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((localAccountID == null) ? 0 : localAccountID.hashCode());
        result = prime * result
                + ((protocolName == null) ? 0 : protocolName.hashCode());
        result = prime * result + ((remoteUserID == null) ? 0 : remoteUserID.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        SessionID other = (SessionID) obj;
        if (localAccountID == null) {
            if (other.localAccountID != null)
                return false;
        } else if (!localAccountID.equals(other.localAccountID))
            return false;
        if (protocolName == null) {
            if (other.protocolName != null)
                return false;
        } else if (!protocolName.equals(other.protocolName))
            return false;
        if (remoteUserID == null) {
            if (other.remoteUserID != null)
                return false;
        } else if (!remoteUserID.equals(other.remoteUserID))
            return false;
        return true;
    }

}
