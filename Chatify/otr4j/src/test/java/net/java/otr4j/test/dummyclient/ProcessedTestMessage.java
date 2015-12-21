package net.java.otr4j.test.dummyclient;

/**
 * Created by gp on 2/6/14.
 */
public class ProcessedTestMessage extends TestMessage {

	final TestMessage originalMessage;

	public ProcessedTestMessage(TestMessage originalMessage, String content) {
		super(originalMessage.getSender(), content);
		this.originalMessage = originalMessage;
	}

	public TestMessage getOriginalMessage() {
		return originalMessage;
	}
}
