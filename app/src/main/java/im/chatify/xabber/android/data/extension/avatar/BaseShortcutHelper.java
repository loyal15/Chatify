package im.chatify.xabber.android.data.extension.avatar;

import android.content.res.Resources;

import im.chatify.CIApp;

/**
 * Helper class to create shortcuts under Android < 2.3.
 *
 * @author alexander.ivanov
 */
public class BaseShortcutHelper {

    /**
     * Get the preferred launcher icon size. This is used when custom drawables
     * are created (e.g., for shortcuts).
     * <p/>
     * Based on {@link android.app.ActivityManager#getLauncherLargeIconSize()}
     * for Android 3+.
     *
     * @return dimensions of square icons in terms of pixels
     */
    static int getLauncherLargeIconSize() {
        final Resources res = CIApp.getInstance().getResources();
        return res.getDimensionPixelSize(android.R.dimen.app_icon_size);
    }

}
