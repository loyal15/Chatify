
package net.java.otr4j.io.messages;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Arrays;
import java.util.Random;

import net.java.otr4j.OtrException;
import net.java.otr4j.io.SerializationConstants;
import net.java.otr4j.io.SerializationUtils;
import net.java.otr4j.session.AuthContextGetter;
import net.java.otr4j.session.Session;
import net.java.otr4j.session.Session.OTRv;
import net.java.otr4j.test.dummyclient.DummyClient;
import net.java.otr4j.test.dummyclient.TestMessage;

import org.junit.Test;

public class SignatureMessageTest {

    @Test
    public void testVerify() throws OtrException, IOException {
        DummyClient[] convo = DummyClient.getConversation();
        DummyClient alice = convo[0];
        DummyClient bob = convo[1];

        bob.secureSession(alice.getAccount());

        alice.pollReceivedMessage(); // Query
        bob.pollReceivedMessage(); // DH-Commit
        alice.pollReceivedMessage(); // DH-Key
        // stop alice from processing bob's next
        alice.stopBeforeProcessingNextMessage();
        bob.pollReceivedMessage(); // Reveal signature

        Session session = alice.getSession();
        TestMessage tm = alice.getNextTestMessage(); // Signature
        AbstractMessage am = SerializationUtils.toMessage(tm.getContent());
        assertTrue("This should be a SignatureMessage",
                am.getClass() == SignatureMessage.class);
        SignatureMessage sm = (SignatureMessage) am;
        byte[] M2 = AuthContextGetter.getM2(session);
        assertFalse("This should NOT verify", sm.verify(M2));
        byte[] M2p = AuthContextGetter.getM2p(session);
        assertTrue("This should verify", sm.verify(M2p));
        byte[] corruptKey = Arrays.copyOf(M2p, M2p.length);
        int byteToCorrupt = M2p.length - 10;
        corruptKey[byteToCorrupt] = (byte) (M2p[byteToCorrupt] - 10);
        assertFalse("This should NOT verify", sm.verify(corruptKey));
    }

    @Test
    /** since this test is based on randomly generated data,
     * there is a very small chance of false positives. */
    public void testHashCode() {
        Random r = new Random();
        byte[] fakeEncryptedMAC = new byte[SerializationConstants.TYPE_LEN_MAC];
        SignatureMessage current = null;
        SignatureMessage previous = null;
        for (int i = 1; i <= 10000000; i *= 10) {
            byte[] fakeEncrypted = new byte[i];
            r.nextBytes(fakeEncrypted);
            r.nextBytes(fakeEncryptedMAC);
            current = new SignatureMessage(OTRv.THREE, fakeEncrypted,
                    fakeEncryptedMAC);
            assertNotNull(current);
            assertFalse(current.equals(null));
            assertFalse(current.equals(previous));
            if (previous != null)
                assertFalse(current.hashCode() == previous.hashCode());
            previous = current;
        }
        for (int i = -128; i < 128; i++) {
            byte[] fakeEncrypted = new byte[100];
            Arrays.fill(fakeEncrypted, (byte) i);
            Arrays.fill(fakeEncryptedMAC, (byte) i);
            current = new SignatureMessage(OTRv.THREE, fakeEncrypted,
                    fakeEncryptedMAC);
            assertNotNull(current);
            assertFalse(current.hashCode() == previous.hashCode());
            previous = current;
        }
    }

    @Test
    /** since this test is based on randomly generated data,
     * there is a very small chance of false positives. */
    public void testEqualsObject() {
        Random r = new Random();
        byte[] fakeEncryptedMAC = new byte[SerializationConstants.TYPE_LEN_MAC];
        SignatureMessage previous = null;
        for (int i = 1; i <= 10000000; i *= 10) {
            byte[] fakeEncrypted = new byte[i];
            r.nextBytes(fakeEncrypted);
            r.nextBytes(fakeEncryptedMAC);
            SignatureMessage sm = new SignatureMessage(OTRv.THREE,
                    fakeEncrypted, fakeEncryptedMAC);
            assertNotNull(sm);
            assertFalse(sm.equals(null));
            SignatureMessage sm2 = new SignatureMessage(OTRv.THREE,
                    fakeEncrypted, fakeEncryptedMAC);
            assertNotNull(sm2);
            assertTrue(sm.equals(sm2));
            assertFalse(sm.equals(previous));
            previous = sm;
        }
        for (int i = -128; i < 128; i++) {
            byte[] fakeEncrypted = new byte[1000];
            Arrays.fill(fakeEncrypted, (byte) i);
            Arrays.fill(fakeEncryptedMAC, (byte) i);
            SignatureMessage current = new SignatureMessage(OTRv.THREE,
                    fakeEncrypted, fakeEncryptedMAC);
            assertNotNull(current);
            assertFalse(current.equals(null));
            assertFalse(current.equals(previous));
            previous = current;
        }
    }
}
