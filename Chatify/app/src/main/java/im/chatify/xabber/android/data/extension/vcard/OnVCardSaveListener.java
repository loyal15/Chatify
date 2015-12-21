package im.chatify.xabber.android.data.extension.vcard;


import im.chatify.xabber.android.data.BaseUIListener;

public interface OnVCardSaveListener extends BaseUIListener {
    void onVCardSaveSuccess(String account);
    void onVCardSaveFailed(String account);
}
