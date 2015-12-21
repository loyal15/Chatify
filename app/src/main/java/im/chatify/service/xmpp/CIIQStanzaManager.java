package im.chatify.service.xmpp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Base64;
import android.util.Log;

import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smack.packet.UnparsedIQ;
import org.jivesoftware.smackx.iqregister.packet.Registration;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import im.chatify.CIApp;
import im.chatify.model.CICategory;
import im.chatify.model.CIConst;
import im.chatify.xabber.android.data.NetworkException;
import im.chatify.xabber.android.data.connection.ConnectionManager;
import im.chatify.xabber.android.data.connection.OnResponseListener;

/**
 * Created by administrator on 9/22/15.
 */
public class CIIQStanzaManager {

    private static CIIQStanzaManager mInstance;
    //    public final static String USERNAME = "358414755120";
    public final static String USERNAME = "8618202429484";
    //    public final static String USERNAME = "8617077826327";
    public final static String PASSWORD = "abcd1234";

    private Context mContext;

    public static CIIQStanzaManager getInstance(Context context) {

        if (mInstance == null)
            mInstance = new CIIQStanzaManager(context);

        return mInstance;
    }

    public CIIQStanzaManager(Context context) {

        mContext = context;
    }

    public IQ createIQStanzaForCategories(String account) {

        IQ iq = new IQ("query") {
            @Override
            protected IQChildElementXmlStringBuilder getIQChildElementBuilder(IQChildElementXmlStringBuilder xml) {

                xml.attribute("xmlns", "sw:cat");
                xml.attribute("act", "list");
                xml.rightAngleBracket();

                return xml;
            }
        };

        iq.setFrom(account);
        iq.setType(IQ.Type.get);
        iq.setTo("chatify.im");

        return iq;
    }

    public static Stanza createIQStanzaForRegister(String serviceName, final String username, final String password, IQ.Type type) {

        Map<String,String> attributes=new HashMap<String,String>();
        attributes.put("username", USERNAME);
        attributes.put("password", PASSWORD);
        attributes.put("code", "1234");

        Registration stanza = new Registration(attributes);
        stanza.setTo(serviceName);

        stanza.setType(type);

        return stanza;
    }

    public static void sendIQStanzaForRegisterDeviceInfo(final String account, final String token, final String udid, final long timezoneOffset, final int devType, final int enabled) {

        IQ iq = new IQ("query") {
            @Override
            protected IQChildElementXmlStringBuilder getIQChildElementBuilder(IQChildElementXmlStringBuilder xml) {

                xml.attribute("xmlns", "swr");
                xml.rightAngleBracket();
                xml.element("token", token);
                xml.element("lang", "en");
                xml.element("hwid", udid);
                xml.element("pns", "gcm");
                xml.element("timezone", String.valueOf(timezoneOffset));
                xml.element("devtype", String.valueOf(devType));
//                xml.element("enabled", String.valueOf(enabled));

                return xml;
            }
        };

        iq.setType(IQ.Type.set);
        iq.setTo("chatify.im");

        try {
            ConnectionManager.getInstance().sendRequest(account, iq, new OnResponseListener() {
                @Override
                public void onReceived(String account, String packetId, IQ iq) {

                }

                @Override
                public void onError(String account, String packetId, IQ iq) {

                }

                @Override
                public void onTimeout(String account, String packetId) {

                }

                @Override
                public void onDisconnect(String account, String packetId) {

                }
            });
        } catch (NetworkException e) {
            e.printStackTrace();
        }
    }

    public void sendIQStanzaForRequestHistory(String account, final String opponent) {

//        <iq type='get' id='query1'>
//        <query xmlns='urn:xmpp:mam:tmp' queryid='x01'>
//        <set xmlns='http://jabber.org/protocol/rsm'>
//        <max>20</max>
//        </set>
//        </query>
//        </iq>

        IQ reqChatHistoryIQ = new IQ("query") {
            @Override
            protected IQChildElementXmlStringBuilder getIQChildElementBuilder(IQChildElementXmlStringBuilder xml) {

                xml.attribute("xmlns", "urn:xmpp:mam:tmp");
                xml.rightAngleBracket();
                xml.element("with", opponent);
                xml.element("start", "2015-10-06T00:00:00Z");
                xml.element("end", "2015-10-24T00:00:00Z");
                /*
                xml.halfOpenElement("field");
                xml.attribute("var", "end");
                xml.rightAngleBracket();
                xml.element("value", "2015-10-24T00:00:00Z");
                xml.closeElement("field");
*/
                /*
                xml.halfOpenElement("set");
                xml.attribute("xmlns", "http://jabber.org/protocol/rsm");
                xml.rightAngleBracket();
                xml.element("max", "20");
                xml.closeElement("set");
                */

                return xml;
            }
        };

        reqChatHistoryIQ.setType(IQ.Type.get);

        try {
            ConnectionManager.getInstance().sendRequest(account, reqChatHistoryIQ, new OnResponseListener() {
                @Override
                public void onReceived(String account, String packetId, IQ iq) {

                    UnparsedIQ unparsedIQ = (UnparsedIQ)iq;

                    Log.e("onReceived", unparsedIQ.getContent().toString());
                }

                @Override
                public void onError(String account, String packetId, IQ iq) {

                }

                @Override
                public void onTimeout(String account, String packetId) {

                }

                @Override
                public void onDisconnect(String account, String packetId) {

                }
            });
        } catch (NetworkException e) {
            e.printStackTrace();
        }

        /*
        IQ reqChatHistoryIQ = new IQ("query") {

            @Override
            protected IQChildElementXmlStringBuilder getIQChildElementBuilder(IQChildElementXmlStringBuilder xml) {

                xml.attribute("xmlns", "urn:xmpp:mam:0");
                xml.rightAngleBracket();
                xml.element("value", "urn:xmpp:mam:0");
                xml.element("with", opponent);

                return xml;
            }
        };

        reqChatHistoryIQ.setType(IQ.Type.get);
        reqChatHistoryIQ.setFrom(account);

        try {
            ConnectionManager.getInstance().sendRequest(account, reqChatHistoryIQ, new OnResponseListener() {
                @Override
                public void onReceived(String account, String packetId, IQ iq) {

                }

                @Override
                public void onError(String account, String packetId, IQ iq) {

                }

                @Override
                public void onTimeout(String account, String packetId) {

                }

                @Override
                public void onDisconnect(String account, String packetId) {

                }
            });
        } catch (NetworkException e) {
            e.printStackTrace();
        }
        */
    }

    public void sendIQStanzaForLoadCategories(final String account, final boolean needPlaceHolder, final OnResponseListener responseListener) {

        final ArrayList<CICategory> categories = CICategory.getCategoryList(mContext, true);

        if (categories.size() == 1) {

            try {

                final IQ reqCategoryIQ = createIQStanzaForCategories(account);

                ConnectionManager.getInstance().sendRequest(account, reqCategoryIQ, new OnResponseListener() {
                    @Override
                    public void onReceived(String iqAccount, String packetId, IQ iq) {

                        if (!iqAccount.equals(account))
                            return;

                        if (!iq.getStanzaId().equals(reqCategoryIQ.getStanzaId()))
                            return;

                        UnparsedIQ unparsedIQ = (UnparsedIQ)iq;

                        String content = unparsedIQ.getContent().toString();

                        SharedPreferences.Editor prefEditor = CIApp.getPreferenceEditor(mContext);
                        prefEditor.putString(CIConst.KEY_CATEGORY_BUSINESS, content);
                        prefEditor.commit();

                        Document doc = Jsoup.parse(content);

                        if (doc != null) {

                            Elements catElements = doc.getElementsByTag("cat");

                            for (int i = 0; i < catElements.size(); i ++) {

                                Element element = catElements.get(i);

                                if (element != null) {

                                    CICategory category = new CICategory(mContext);
                                    category.categoryName = element.attr("name");
                                    category.categoryId = Integer.parseInt(element.attr("id"));
                                    category.categoryLogo = element.text();

                                    byte[] imageAsBytes = Base64.decode(category.categoryLogo.getBytes(), Base64.DEFAULT);

                                    category.categoryBitmap = BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length);
                                    categories.add(category);

                                }
                            }

                            if (responseListener != null)
                                responseListener.onReceived(account, packetId, iq);

                            Intent intent = new Intent("KEY_UPDATE_BUSINESSLIST");
                            LocalBroadcastManager mgr = LocalBroadcastManager.getInstance(mContext);
                            mgr.sendBroadcast(intent);
                        }
                    }

                    @Override
                    public void onError(String account, String packetId, IQ iq) {

                    }

                    @Override
                    public void onTimeout(String account, String packetId) {

                    }

                    @Override
                    public void onDisconnect(String account, String packetId) {

                    }

                });
            } catch (NetworkException e) {
                e.printStackTrace();
            }

        } else {

            if (responseListener != null)
                responseListener.onReceived(account, null, null);
        }
    }


}
