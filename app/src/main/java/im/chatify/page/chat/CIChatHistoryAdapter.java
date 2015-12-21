package im.chatify.page.chat;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import im.chatify.R;
import im.chatify.model.CIChatContact;
import im.chatify.xabber.android.data.SettingsManager;
import im.chatify.xabber.android.data.account.AccountManager;
import im.chatify.xabber.android.data.account.CommonState;
import im.chatify.xabber.android.data.entity.BaseEntity;
import im.chatify.xabber.android.data.extension.muc.RoomChat;
import im.chatify.xabber.android.data.extension.muc.RoomContact;
import im.chatify.xabber.android.data.message.AbstractChat;
import im.chatify.xabber.android.data.message.ChatContact;
import im.chatify.xabber.android.data.message.MessageManager;
import im.chatify.xabber.android.data.roster.AbstractContact;
import im.chatify.xabber.android.data.roster.RosterContact;
import im.chatify.xabber.android.data.roster.RosterManager;

/**
 * Created by administrator on 9/23/15.
 */
public class CIChatHistoryAdapter extends ArrayAdapter<BaseEntity> implements UpdatableAdapter, Runnable, Filterable {

    private Context mContext;
    private ArrayList<CIChatContact> mData = null;
    private int mListItemId;

    /**
     * View type used for contact items.
     */
    static final int TYPE_CONTACT = 0;

    /**
     * View type used for groups and accounts expanders.
     */
    static final int TYPE_GROUP = 1;
    static final int TYPE_ACCOUNT = 2;
    static final int TYPE_ACCOUNT_TOP_SEPARATOR = 3;
    static final int TYPE_ACCOUNT_BOTTOM_SEPARATOR = 4;

    /**
     * Number of milliseconds between lazy refreshes.
     */
    private static final long REFRESH_INTERVAL = 1000;

    /**
     * Handler for deferred refresh.
     */
    private final Handler handler;

    /**
     * Lock for refresh requests.
     */
    private final Object refreshLock;

    /**
     * Whether refresh was requested.
     */
    private boolean refreshRequested;

    /**
     * Whether refresh is in progress.
     */
    private boolean refreshInProgress;

    /**
     * Minimal time when next refresh can be executed.
     */
    private Date nextRefresh;

    /**
     * Contact filter.
     */
    ContactFilter contactFilter;

    /**
     * Filter string. Can be <code>null</code> if filter is disabled.
     */
    String filterString = "";

    private final OnContactListChangedListener listener;
    private boolean hasActiveChats = false;

    protected Locale locale = Locale.getDefault();

    final ArrayList<BaseEntity> baseEntities = new ArrayList<>();

    public CIChatHistoryAdapter(Context context,  int resource, OnContactListChangedListener listener, View.OnClickListener onClickListener) {
        super(context, resource);

        mListItemId = resource;
        mContext = context;

        this.listener = listener;
        handler = new Handler();
        refreshLock = new Object();
        refreshRequested = false;
        refreshInProgress = false;
        nextRefresh = new Date();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        View row = convertView;

        CIChatHistoryHolder holder;

        if (row == null) {
            LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            row = inflater.inflate(R.layout.listitem_chathistory, parent, false);

            holder = new CIChatHistoryHolder(row);

            row.setTag(holder);

        } else {
            holder = (CIChatHistoryHolder) row.getTag();
        }

        switch (getItemViewType(position)) {
            case TYPE_CONTACT:
                AbstractContact contact = (AbstractContact)baseEntities.get(position);
                holder.setChatHistory(mContext, contact);
        }

        return row;
    }

    @Override
    public int getCount() {
        return baseEntities.size();
    }

    @Override
    public BaseEntity getItem(int position) {
        return baseEntities.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public int getItemViewType(int position) {
        Object object = getItem(position);
        if (object instanceof AbstractContact) {
            return TYPE_CONTACT;
        } else if (object instanceof AccountConfiguration) {
            return TYPE_ACCOUNT;
        } else if (object instanceof GroupConfiguration) {
            return TYPE_GROUP;
        } else {
            throw new IllegalStateException();
        }
    }

    /**
     * Requests refresh in some time in future.
     */
    public void refreshRequest() {
        synchronized (refreshLock) {
            if (refreshRequested) {
                return;
            }
            if (refreshInProgress) {
                refreshRequested = true;
            } else {
                long delay = nextRefresh.getTime() - new Date().getTime();
                handler.postDelayed(this, delay > 0 ? delay : 0);
            }
        }
    }

    /**
     * Remove refresh requests.
     */
    public void removeRefreshRequests() {
        synchronized (refreshLock) {
            refreshRequested = false;
            refreshInProgress = false;
            handler.removeCallbacks(this);
        }
    }

    @Override
    public void onChange() {
        synchronized (refreshLock) {
            refreshRequested = false;
            refreshInProgress = true;
            handler.removeCallbacks(this);
        }

        final Collection<RosterContact> rosterContacts = RosterManager.getInstance().getContacts();
        final boolean showOffline = SettingsManager.contactsShowOffline();
        final boolean showEmptyGroups = SettingsManager.contactsShowEmptyGroups();
        final boolean stayActiveChats = SettingsManager.contactsStayActiveChats();
        final boolean showAccounts = SettingsManager.contactsShowAccounts();
        final Comparator<AbstractContact> comparator = SettingsManager.contactsOrder();
        final CommonState commonState = AccountManager.getInstance().getCommonState();
        final String selectedAccount = AccountManager.getInstance().getSelectedAccount();


        /**
         * Groups.
         */
        final Map<String, GroupConfiguration> groups;

        /**
         * Contacts.
         */
        final List<AbstractContact> contacts;

        /**
         * List of active chats.
         */
        final GroupConfiguration activeChats;

        /**
         * Whether there is at least one contact.
         */
        boolean hasContacts = false;

        /**
         * Whether there is at least one visible contact.
         */
        boolean hasVisibleContacts = false;

        final Map<String, AccountConfiguration> accounts = new TreeMap<>();

        for (String account : AccountManager.getInstance().getAccounts()) {
            accounts.put(account, null);
        }

        /**
         * List of rooms and active chats grouped by users inside accounts.
         */
        final Map<String, Map<String, AbstractChat>> abstractChats = new TreeMap<>();

        for (AbstractChat abstractChat : MessageManager.getInstance().getChats()) {
            if ((abstractChat instanceof RoomChat || abstractChat.isActive())
                    && accounts.containsKey(abstractChat.getAccount())) {
                final String account = abstractChat.getAccount();
                Map<String, AbstractChat> users = abstractChats.get(account);
                if (users == null) {
                    users = new TreeMap<>();
                    abstractChats.put(account, users);
                }
                users.put(abstractChat.getUser(), abstractChat);
            }
        }

        final ArrayList<AbstractContact> baseEntities = getSearchResults(rosterContacts, comparator, abstractChats);
        this.baseEntities.clear();
        this.baseEntities.addAll(baseEntities);
        hasVisibleContacts = baseEntities.size() > 0;

        notifyDataSetChanged();
        listener.onContactListChanged(commonState, hasContacts, hasVisibleContacts, filterString != null);

        synchronized (refreshLock) {
            nextRefresh = new Date(new Date().getTime() + REFRESH_INTERVAL);
            refreshInProgress = false;
            handler.removeCallbacks(this); // Just to be sure.
            if (refreshRequested) {
                handler.postDelayed(this, REFRESH_INTERVAL);
            }
        }
    }

    private ArrayList<AbstractContact> getSearchResults(Collection<RosterContact> rosterContacts,
                                                        Comparator<AbstractContact> comparator,
                                                        Map<String, Map<String, AbstractChat>> abstractChats) {
        final ArrayList<AbstractContact> baseEntities = new ArrayList<>();

        // Build structure.
        for (RosterContact rosterContact : rosterContacts) {
            if (!rosterContact.isEnabled()) {
                continue;
            }

            final String account = rosterContact.getAccount();

            final Map<String, AbstractChat> users = abstractChats.get(account);
            if (users != null) {
                users.remove(rosterContact.getUser());
            }

            if (filterString.length() > 0) {
                if (rosterContact.getName().toLowerCase(locale).contains(filterString)) {
                    baseEntities.add(rosterContact);
                }
            } else {
                baseEntities.add(rosterContact);
            }
        }

        for (Map<String, AbstractChat> users : abstractChats.values()) {
            for (AbstractChat abstractChat : users.values()) {
                final AbstractContact abstractContact;
                if (abstractChat instanceof RoomChat) {
                    abstractContact = new RoomContact((RoomChat) abstractChat);
                } else {
                    abstractContact = new ChatContact(abstractChat);
                }

                if (filterString.length() > 0) {
                    if (abstractContact.getName().toLowerCase(locale).contains(filterString)) {
                        baseEntities.add(abstractContact);
                    }
                } else {
                    baseEntities.add(abstractContact);
                }
            }
        }
        Collections.sort(baseEntities, comparator);
        return baseEntities;
    }

    @Override
    public void run() {
        onChange();
    }

    /**
     * Listener for contact list appearance changes.
     *
     * @author alexander.ivanov
     */
    public interface OnContactListChangedListener {

        void onContactListChanged(CommonState commonState, boolean hasContacts,
                                  boolean hasVisibleContacts, boolean isFilterEnabled);

    }

    @Override
    public Filter getFilter() {
        if (contactFilter == null) {
            contactFilter = new ContactFilter();
        }
        return contactFilter;
    }

    private class ContactFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            return null;
        }

        @Override
        protected void publishResults(CharSequence constraint,
                                      FilterResults results) {
            if (constraint == null || constraint.length() == 0) {
                filterString = null;
            } else {
                filterString = constraint.toString().toLowerCase(locale);
            }
            onChange();
        }

    }

    public boolean isHasActiveChats() {
        return hasActiveChats;
    }
}