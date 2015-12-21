package com.gpit.android.profile.addressbook;

import java.util.ArrayList;

import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;

public class ContactHelper {
	private static ContactHelper instance;
	
	public static ContactHelper getInstance(Context context) {
		if (instance == null) {
			instance = new ContactHelper(context);
		}

		return instance;
	}
	
	private Context mContext;
	
	private ContactHelper(Context context) {
		mContext = context;
	}
	
	public ArrayList<Contact> getContacts() {
		ArrayList<Contact> contactList = new ArrayList<Contact>();
		
		try {
			Cursor cursor = mContext.getContentResolver()
					.acquireContentProviderClient(ContactsContract.Contacts.CONTENT_URI)
					.query(ContactsContract.Contacts.CONTENT_URI, new String[]{ContactsContract.Contacts._ID}, null, null, null);
			if (cursor != null && cursor.getCount() > 0) {
				while (cursor.moveToNext()) {
					int contactId = cursor.getInt(cursor.getColumnIndexOrThrow(ContactsContract.Contacts._ID));
					Contact contact = new Contact(mContext, contactId);
					contactList.add(contact);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return contactList;
	}	
}
