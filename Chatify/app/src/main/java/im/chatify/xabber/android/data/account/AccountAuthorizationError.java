/**
 * Copyright (c) 2013, Redsolution LTD. All rights reserved.
 *
 * This file is part of Xabber project; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License, Version 3.
 *
 * Xabber is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License,
 * along with this program. If not, see http://www.gnu.org/licenses/.
 */
package im.chatify.xabber.android.data.account;

import android.content.Intent;

import im.chatify.CIApp;
import im.chatify.R;
import im.chatify.xabber.android.data.entity.AccountRelated;
import im.chatify.xabber.android.data.notification.AccountNotificationItem;

public class AccountAuthorizationError extends AccountRelated implements
        AccountNotificationItem {

    public AccountAuthorizationError(String account) {
        super(account);
    }

    @Override
    public Intent getIntent() {
        return new Intent();
//        return AccountEditor.createIntent(
//                CIApp.getInstance(), account);
    }

    @Override
    public String getTitle() {
        return CIApp.getInstance().getString(
                R.string.AUTHENTICATION_FAILED);
    }

    @Override
    public String getText() {
        return AccountManager.getInstance().getVerboseName(account);
    }

}
