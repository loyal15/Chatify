
package net.java.otr4j.test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import net.java.otr4j.session.SessionStatus;
import net.java.otr4j.test.dummyclient.DummyClient;

import org.junit.Test;

/**
 * Test unicode handling in the message body
 *
 * @author Hans-Christoph Steiner
 */
public class UnicodeMessageTest {

    @Test
    public void testForcedStart() throws Exception {
        DummyClient[] convo = DummyClient.getConversation();
        DummyClient alice = convo[0];
        DummyClient bob = convo[1];

        DummyClient.forceStartOtr(alice, bob);

        String msg;
        for (String test : TestStrings.unicodes) {
            assertEquals("The session is not encrypted.", SessionStatus.ENCRYPTED,
                    bob.getSession().getSessionStatus());
            assertEquals("The session is not encrypted.", SessionStatus.ENCRYPTED,
                    alice.getSession().getSessionStatus());

            alice.send(bob.getAccount(), msg = "A->B: " + test);
            assertThat("Message has been transferred unencrypted.", alice
                    .getConnection().getSentMessage(), not(equalTo(msg)));
            assertEquals("Received message is different from the sent message.",
                    msg, bob.pollReceivedMessage().getContent());

            bob.send(alice.getAccount(), msg = test + "B->A");
            assertThat("Message has been transferred unencrypted.", bob
                    .getConnection().getSentMessage(), not(equalTo(msg)));
            assertEquals("Received message is different from the sent message.",
                    msg, alice.pollReceivedMessage().getContent());
        }

        bob.exit();
        alice.exit();
    }

    @Test
    public void testPlaintext() throws Exception {
        DummyClient[] convo = DummyClient.getConversation();
        DummyClient alice = convo[0];
        DummyClient bob = convo[1];

        String msg;
        for (String test : TestStrings.unicodes) {
            // first round, mark the message with an ASCII tag
            alice.send(bob.getAccount(), msg = "A->B: " + test);
            assertEquals("Message has been altered (but it shouldn't).", msg,
                    alice.getConnection().getSentMessage());
            assertEquals("Received message is different from the sent message.",
                    msg, bob.pollReceivedMessage().getContent());

            bob.send(alice.getAccount(), msg = test + "B->A");
            assertEquals("Message has been altered (but it shouldn't).", msg,
                    bob.getConnection().getSentMessage());
            assertEquals("Received message is different from the sent message.",
                    msg, alice.pollReceivedMessage().getContent());

            // there should definitely be a OTR Session now
            assertEquals("The session should not be encrypted.", SessionStatus.PLAINTEXT,
                    bob.getSession().getSessionStatus());
            assertEquals("The session should not be encrypted.", SessionStatus.PLAINTEXT,
                    alice.getSession().getSessionStatus());

            // second round, just send the same bit of unicode back and forth
            alice.send(bob.getAccount(), msg = test);
            assertEquals("Message has been altered (but it shouldn't).", msg,
                    alice.getConnection().getSentMessage());
            assertEquals("Received message is different from the sent message.",
                    msg, bob.pollReceivedMessage().getContent());

            bob.send(alice.getAccount(), msg = test);
            assertEquals("Message has been altered (but it shouldn't).", msg,
                    bob.getConnection().getSentMessage());
            assertEquals("Received message is different from the sent message.",
                    msg, alice.pollReceivedMessage().getContent());
        }

        bob.exit();
        alice.exit();
    }
}
