
package net.java.otr4j.session;

import java.util.logging.Logger;

import net.java.otr4j.OtrException;

/**
 * A simple utility class for getting the {@link AuthContext} in the tests. This
 * should not be used in real code.
 *
 * @author hans
 */
public class AuthContextGetter {

    public static final Logger logger = Logger.getLogger(AuthContextGetter.class.getName());

    public static AuthContext getAuthContext(Session session) {
        logger.finest("");
        return session.getAuthContext();
    }

    public static byte[] getM2(Session session) throws OtrException {
        logger.finest("");
        return session.getAuthContext().getM2();
    }

    public static byte[] getM2p(Session session) throws OtrException {
        logger.finest("");
        return session.getAuthContext().getM2p();
    }
}
