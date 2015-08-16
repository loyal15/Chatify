package vsppsgv.chatify.im.common.phoneno;

import java.util.Stack;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public abstract class XmlHandler extends DefaultHandler {
	private Stack<String> mPath = new Stack<String>();
	private String mValue = "";

	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		mValue += new String(ch, start, length);
	}

	public abstract void endElement(String localName, String elementValue);

	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		endElement(localName, mValue);
		mPath.pop();
		mValue = "";
	}

	public String getParentLocalName() {
		String result = "";
		if (mPath.size() > 1)
			result = mPath.get(mPath.size() - 2);
		return result;
	}

	public abstract void startElement(String localName, Attributes attributes);

	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		mPath.push(localName);
		mValue = "";
		startElement(localName, attributes);
	}
}
