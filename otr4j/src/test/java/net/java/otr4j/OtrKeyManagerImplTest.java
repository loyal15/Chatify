
package net.java.otr4j;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;

import net.java.otr4j.session.SessionID;

import org.junit.Test;

public class OtrKeyManagerImplTest {

    private SessionID bobSessionID = new SessionID("Alice@Wonderland",
            "Bob@Wonderland", "Scytale");
    private SessionID eveSessionID = new SessionID("Alice@Wonderland",
            "Eve@Wonderland", "Scytale");
    private File otrProperties = new File("otr.properties");

    @Test
    public void testVerifyUnverify() throws Exception {
        if (otrProperties.exists())
            assertTrue(otrProperties.delete());
        OtrKeyManager keyManager = new OtrKeyManagerImpl(otrProperties.getAbsolutePath());
        keyManager.generateLocalKeyPair(bobSessionID);
        System.out.println(keyManager.isVerified(bobSessionID));
        assertFalse(keyManager.isVerified(bobSessionID));

        keyManager.verify(bobSessionID);
        assertTrue(keyManager.isVerified(bobSessionID));

        keyManager.unverify(bobSessionID);
        assertFalse(keyManager.isVerified(bobSessionID));
    }

    @Test
    public void testReadExistingFile() throws Exception {
        if (otrProperties.exists())
            assertTrue(otrProperties.delete());
        OtrKeyManager keyManager = new OtrKeyManagerImpl(otrProperties.getAbsolutePath());
        keyManager.generateLocalKeyPair(bobSessionID);
        System.out.println(keyManager.isVerified(bobSessionID));
        assertFalse(keyManager.isVerified(bobSessionID));
        assertFalse(keyManager.isVerified(eveSessionID));

        keyManager.verify(bobSessionID);
        assertTrue(keyManager.isVerified(bobSessionID));

        OtrKeyManager newKeyManager = new OtrKeyManagerImpl(otrProperties.getAbsolutePath());
        assertTrue(newKeyManager.isVerified(bobSessionID));
        assertFalse(newKeyManager.isVerified(eveSessionID));
    }
}
