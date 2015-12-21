package im.chatify.xabber.xmpp.archive;

import org.xmlpull.v1.XmlPullParser;

import im.chatify.xabber.xmpp.AbstractIQProvider;
import im.chatify.xabber.xmpp.IQ;

/**
 * Created by administrator on 11/22/15.
 */
public class MessageArchiveProvider extends AbstractIQProvider {
    @Override
    protected IQ createInstance(XmlPullParser parser) {
        return null;
    }
}
