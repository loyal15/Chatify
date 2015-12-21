package net.java.otr4j.test.dummyclient;

/**
 * Created by gp on 2/6/14.
 */
public class TestMessage {

	public TestMessage(String sender, String content){
		this.sender = sender;
		this.content = content;
	}

	private final String sender;
	private final String content;

	public String getSender() {
		return sender;
	}

	public String getContent() {
		return content;
	}
}
