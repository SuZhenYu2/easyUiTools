package com.easyuitools.util.factory;

/**
 * Created by suzy2 on 2016/12/12.
 */
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 *
 */
public final class SystemPropertyAction implements PrivilegedAction<String> {
    private static final Logger LOG = Logger.getLogger(SystemPropertyAction.class.getName());
    private final String property;
    private final String def;
    private SystemPropertyAction(String name) {
        property = name;
        def = null;
    }
    private SystemPropertyAction(String name, String d) {
        property = name;
        def = d;
    }

    /* (non-Javadoc)
     * @see java.security.PrivilegedAction#run()
     */
    public String run() {
        if (def != null) {
            return System.getProperty(property, def);
        }
        return System.getProperty(property);
    }

    public static String getProperty(String name) {
        return AccessController.doPrivileged(new SystemPropertyAction(name));
    }

    public static String getProperty(String name, String def) {
        try {
            return AccessController.doPrivileged(new SystemPropertyAction(name, def));
        } catch (SecurityException ex) {
            LOG.log(Level.FINE, "SecurityException raised getting property " + name, ex);
            return def;
        }
    }

    /**
     * Get the system property via the AccessController, but if a SecurityException is
     * raised, just return null;
     * @param name
     */
    public static String getPropertyOrNull(String name) {
        try {
            return AccessController.doPrivileged(new SystemPropertyAction(name));
        } catch (SecurityException ex) {
            LOG.log(Level.FINE, "SecurityException raised getting property " + name, ex);
            return null;
        }
    }
}
