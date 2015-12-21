package im.chatify.xabber.xmpp;

import org.jivesoftware.smack.packet.ExtensionElement;

/**
 * Created by lion on 12/19/15.
 */
public class LocationPacketExtention implements ExtensionElement {

    public static final String NAMESPACE = "http://jabber.org/protocol/geoloc";
    public static final String ELEMENT = "geoloc";

    private String country;
    private String latitude;
    private String locality;
    private String longitude;

    public LocationPacketExtention(String latitude, String longitude, String country, String locality){
        this.country = country;
        this.locality = locality;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getCountry() {
        return country;
    }

    public String getLocality() {
        return locality;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    @Override
    public String getNamespace() {
        return NAMESPACE;
    }

    @Override
    public String getElementName() {
        return ELEMENT;
    }

    @Override
    public CharSequence toXML() {
        StringBuffer buf = new StringBuffer();
        buf.append("<" + getElementName());
        buf.append(" xmlns=\"").append(getNamespace()).append("\">");
        buf.append("<country>").append(getCountry()).append("</country>");
        buf.append("<lat>").append(getLatitude()).append("</lat>");
        buf.append("<locality>").append(getLocality()).append("</locality>");
        buf.append("<lon>").append(getLongitude()).append("</lon>");
        buf.append("</"+getElementName()+">");
        return buf.toString();
    }
}
