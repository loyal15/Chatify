package com.gpit.android.util;

/**
 * simple uri encoder, made from the spec at:
 * http://www.ietf.org/rfc/rfc2396.txt
 * Feel free to copy this. I take no responsibility for anything, ever.
 * @author Daniel Murphy
 */
public class URIEncoder {
	private static String mark = "-_.!~*'()\"/:";

	public static String encodeURI(String argString) {
		StringBuilder uri = new StringBuilder(); // Encoded URL
		// thanks Marco!

		char[] chars = argString.toCharArray();
		for(int i = 0; i<chars.length; i++) {
			char c = chars[i];
			if((c >= '0' && c <= '9') || (c >= 'a' && c <= 'z') ||
			   (c >= 'A' && c <= 'Z') || mark.indexOf(c) != -1) {
				uri.append(c);
			}
			else {
				uri.append("%");
				uri.append(Integer.toHexString((int)c));
			}
		}
		return uri.toString();
	}
}