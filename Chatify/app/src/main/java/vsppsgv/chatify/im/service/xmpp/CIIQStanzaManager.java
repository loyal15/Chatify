package vsppsgv.chatify.im.service.xmpp;

import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smackx.iqregister.packet.Registration;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by administrator on 9/22/15.
 */
public class CIIQStanzaManager {

    public final static String USERNAME = "4915735983515";
    public final static String PASSWORD = "abcd1234";

    public static Stanza createIQStanzaForRegister(String serviceName, final String username, final String password, IQ.Type type) {

        Map<String,String> attributes=new HashMap<String,String>();
        attributes.put("username", USERNAME);
        attributes.put("password", PASSWORD);
        attributes.put("code", "1234");

        Registration stanza = new Registration(attributes);
        stanza.setTo(serviceName);

        stanza.setType(type);

        /*
        stanza.addExtension(new PacketExtension() {
            @Override
            public String getNamespace() {
                return null;
            }

            @Override
            public String getElementName() {
                return null;
            }

            @Override
            public CharSequence toXML() {
                return "<code hint='sinch'>12345</code>";
            }
        });

        String xmlStr = stanza.toString();
        */

        return stanza;
    }

    public static Stanza createIQStanzaForRegisterDeviceInfo(final String token, final String udid, final long timezoneOffset, final int devType, final int enabled) {

        return new Stanza() {
            @Override
            public CharSequence toXML() {
                return String.format("<iq type='set' id='reg2'>\n" +
                        " <query xmlns='swr'>\n" +
                        "       <token>%s</token>\n" +
                        "       <lang>%s</lang>\n" +
                        "       <hwid>%s</hwid>\n" +
                        "       <pns>gcm</pns>\n" +
                        "       <timezone>%d</timezone>\n" +
                        "       <devtype>%d</devtype>\n" +
                        "       <enabled>%d</enabled>\n" +
                        "   </query>\n" +
                        "</iq>", token, "en", udid, timezoneOffset, devType, enabled);
            }
        };
    }
}
