/*
 * otr4j, the open source java otr library.
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package net.java.otr4j;

import java.security.KeyPair;

import net.java.otr4j.io.SerializationConstants;
import net.java.otr4j.session.InstanceTag;
import net.java.otr4j.session.SessionID;

/**
 * This interface should be implemented by the host application. It is required
 * for otr4j to work properly. This provides the core interface between the app
 * and otr4j.
 *
 * @author George Politis
 */
public abstract interface OtrEngineHost {

	public abstract void injectMessage(SessionID sessionID, String msg)
			throws OtrException;

    /**
     * Warn the user that an encrypted message was received that could not be
     * unencrypted, most likely because it was encrypted to a different session,
     * or an old session.
     *
     * @param sessionID
     * @throws OtrException
     */
	public abstract void unreadableMessageReceived(SessionID sessionID)
			throws OtrException;

    /**
     * Display the message to the user, but warn him that the message was
     * received unencrypted.
     *
     * @param sessionID
     * @param msg the body of the received message that was not encrypted
     * @throws OtrException
     */
	public abstract void unencryptedMessageReceived(SessionID sessionID,
			String msg) throws OtrException;

	public abstract void showError(SessionID sessionID, String error)
			throws OtrException;

	public abstract void smpError(SessionID sessionID, int tlvType,
			boolean cheated) throws OtrException;

	public abstract void smpAborted(SessionID sessionID) throws OtrException;

	public abstract void finishedSessionMessage(SessionID sessionID,
			String msgText) throws OtrException;

	public abstract void requireEncryptedMessage(SessionID sessionID,
			String msgText) throws OtrException;

	public abstract OtrPolicy getSessionPolicy(SessionID sessionID);

	/**
	 * Get instructions for the necessary fragmentation operations.
	 *
	 * If no fragmentation is necessary, return {@link Integer#MAX_VALUE} to
	 * indicate the largest possible fragment size. Return any positive
	 * integer to specify a maximum fragment size and enable fragmentation
	 * using that boundary condition. If specified max fragment size is too
	 * small to fit at least the fragmentation overhead + some part of the
	 * message, fragmentation will fail with an IOException when
	 * fragmentation is attempted during message encryption.
	 *
	 * @param sessionID
	 *            the session ID of the session
	 * @return Returns the maximum fragment size allowed. Or return the
	 * maximum value possible, {@link Integer#MAX_VALUE}, if fragmentation
	 * is not necessary.
	 */
	public abstract int getMaxFragmentSize(SessionID sessionID);

	public abstract KeyPair getLocalKeyPair(SessionID sessionID)
			throws OtrException;

	public abstract byte[] getLocalFingerprintRaw(SessionID sessionID);

	public abstract void askForSecret(SessionID sessionID, InstanceTag receiverTag, String question);

    /**
     * When a remote user's key is verified via the Socialist Millionaire's
     * Protocol (SMP) shared passphrase or question/answer, this method will be
     * called upon successful completion of that process.
     *
     * @param sessionID of the session where the SMP happened.
     * @param fingerprint of the key to verify
     * @param approved
     */

	public abstract void verify(SessionID sessionID, String fingerprint, boolean approved);

    /**
     * If the Socialist Millionaire's Protocol (SMP) process fails, then this
     * method will be called to make sure that the session is marked as
     * untrustworthy.
     *
     * @param sessionID of the session where the SMP happened.
     * @param fingerprint of the key to unverify
     */
	public abstract void unverify(SessionID sessionID, String fingerprint);

	public abstract String getReplyForUnreadableMessage(SessionID sessionID);

    /**
     * Return the localized message that explains to the recipient how to get an
     * OTR-enabled client. This is sent as part of the initial OTR Query message
     * that prompts the other side to set up an OTR session. If this returns
     * {@code null} or {@code ""}, then otr4j will use the built-in default
     * message specified in
     * {@link SerializationConstants#DEFAULT_FALLBACK_MESSAGE}
     *
     * @param sessionID
     * @return String the localized message
     */
	public abstract String getFallbackMessage(SessionID sessionID);

	public abstract void messageFromAnotherInstanceReceived(SessionID sessionID);

	public abstract void multipleInstancesDetected(SessionID sessionID);
}
