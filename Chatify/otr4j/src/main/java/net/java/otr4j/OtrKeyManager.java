
package net.java.otr4j;

import java.security.KeyPair;
import java.security.PublicKey;

import net.java.otr4j.session.SessionID;

/**
 * An interface to implement in order to handle the storing of the local
 * account's private/public key pairs, the remote users' publicKeys, and the
 * verification status of the remote users' keys. otr4j includes a working
 * implementation of this interface as {@link OtrKeyManagerImpl} which can be
 * used in an app.
 */
public abstract interface OtrKeyManager {

    public abstract void addListener(OtrKeyManagerListener l);

    public abstract void removeListener(OtrKeyManagerListener l);

    /**
     * Mark the remote user's key as verified in the local key store.
     *
     * @param sessionID
     */
    public abstract void verify(SessionID sessionID);

    /**
     * Remove verification mark of the remote user's key from local key store.
     *
     * @param sessionID
     */
    public abstract void unverify(SessionID sessionID);

    /**
     * Check the local key store if the remote user's key is marked as verified.
     *
     * @param sessionID
     * @return boolean true if stored key is verified
     */
    public abstract boolean isVerified(SessionID sessionID);

    /**
     * Get the fingerprint of the remote user's key
     *
     * @param sessionID
     * @return String the hash of the key
     */
    public abstract String getRemoteFingerprint(SessionID sessionID);

    /**
     * Get the fingerprint of the local account's key.
     *
     * @param sessionID
     * @return String the hash of the key
     */
    public abstract String getLocalFingerprint(SessionID sessionID);

    public abstract byte[] getLocalFingerprintRaw(SessionID sessionID);

    public abstract void savePublicKey(SessionID sessionID, PublicKey pubKey);

    public abstract PublicKey loadRemotePublicKey(SessionID sessionID);

    public abstract KeyPair loadLocalKeyPair(SessionID sessionID);

    /**
     * Generate a new public/private key pair for the local account.
     *
     * @param sessionID
     */
    public abstract void generateLocalKeyPair(SessionID sessionID);
}
