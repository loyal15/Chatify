package com.gpit.android.profile.addressbook;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;

public class Contact {
	
	private String first_name = null;
	
	private String last_name = null;
	
	private String middle_name = null;
	
	private String organization = null;
	
	private String department = null;
	
	private String notes = null;
	
	private Date birthday = null;
	
	private String nickname = null;
	
	private String display_name = null;
	
	private ArrayList<String> emails = null;
	
	private String home_number = null;
	
	private String mobile_number = null;
	
	private String work_number = null;
	
	private String main_number = null;
	
	private Address homeAddress = null;
	
	private Address workAddress = null;
	
	private Address otherAddress = null;
	
	private Address customAddress = null;
	
	private Uri photoURI = null;

	private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
	
	public Contact() {
		first_name = null;
		last_name = null;
		middle_name = null;
		organization = null;
		department = null;
		notes = null;
		birthday = null;
		nickname = null;
		display_name = null;
		emails = null;
		home_number = null;
		mobile_number = null;
		work_number = null;
		main_number = null;
		homeAddress = null;
		workAddress = null;
		otherAddress = null;
		customAddress = null;
		photoURI = null;
	}

	public Contact(Context context, int contactId) {
		String[] projectionContact = new String[] {
				ContactsContract.Contacts._ID,
	        ContactsContract.Contacts.DISPLAY_NAME,
	        ContactsContract.Contacts.HAS_PHONE_NUMBER,
	        ContactsContract.Contacts.LOOKUP_KEY,
	        ContactsContract.Contacts.PHOTO_ID
		};
		
		String whereOrganization;
		try {
			whereOrganization = ContactsContract.Contacts._ID + " = " + contactId;
			Cursor cursorContact = context.getContentResolver().query(ContactsContract.Contacts.CONTENT_URI,
					projectionContact, whereOrganization, null, ContactsContract.Contacts.DISPLAY_NAME + " ASC");
			if (!cursorContact.moveToFirst()) {
				cursorContact.close();
				return;
			}
			setDisplayName(cursorContact.getString(cursorContact.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME)));
			cursorContact.close();
		} catch (Exception e) {e.printStackTrace();}
		
		try {
			/**
			 * Get the FirstName, LastName and MiddleName from ContactsContract.Data.CONTENT_URI
			 */
			String[] projectionName = new String[] {
					ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME,
					ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME,
					ContactsContract.CommonDataKinds.StructuredName.MIDDLE_NAME };
	
			String whereName = ContactsContract.CommonDataKinds.StructuredName.CONTACT_ID + " = " + contactId 
					+ " AND " + ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME 
					+ " like '" + getDisplayName() + "'";
			
			Cursor cursorName = context.getContentResolver().query( ContactsContract.Data.CONTENT_URI, projectionName, whereName,
					null, null);
			
			if (cursorName.moveToFirst()) {
				setFirstName(cursorName.getString(cursorName.getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME)));
				setLastName(cursorName.getString(cursorName.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME)));
				setMiddleName(cursorName.getString(cursorName.getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.MIDDLE_NAME)));
			}
			cursorName.close();
		} catch (Exception e) {e.printStackTrace();}
		
		try {
			/**
			 * Get the Organization and Department from ContactsContract.Data.CONTENT_URI
			 */
			String[] projectionOrganization = new String[] { ContactsContract.CommonDataKinds.Organization.DATA1, ContactsContract.CommonDataKinds.Organization.DATA4 };
			
			whereOrganization = ContactsContract.Data.RAW_CONTACT_ID + " = " + contactId + " AND " 
					+ ContactsContract.Data.MIMETYPE + " = '" + ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE + "' AND "
					+ ContactsContract.CommonDataKinds.Organization.TYPE + " = " + ContactsContract.CommonDataKinds.Organization.TYPE_WORK;
			
			Cursor cursorOrganization = context.getContentResolver().query(ContactsContract.Data.CONTENT_URI, projectionOrganization, whereOrganization, null, null);
			
			if (cursorOrganization.moveToFirst()) {
				setOrganization(cursorOrganization.getString(cursorOrganization.getColumnIndex(ContactsContract.CommonDataKinds.Organization.DATA1)));
				setDepartment(cursorOrganization.getString(cursorOrganization.getColumnIndex(ContactsContract.CommonDataKinds.Organization.DATA4)));
			}
			
			cursorOrganization.close();
		} catch (Exception e) {e.printStackTrace();}
		
		try {
			/**
			 * Get the Note from ContactsContract.Data.CONTENT_URI
			 */
			String[] projectionNote = new String[] { ContactsContract.CommonDataKinds.Note.NOTE };
			
			String whereNote = ContactsContract.Data.RAW_CONTACT_ID + " = " + contactId + " AND "
					+ ContactsContract.Data.MIMETYPE + " = '" + ContactsContract.CommonDataKinds.Note.CONTENT_ITEM_TYPE + "'";
			
			Cursor cursorNote = context.getContentResolver().query(ContactsContract.Data.CONTENT_URI, projectionNote, whereNote, null, null);
			
			if (cursorNote.moveToFirst()) {
				setNotes(cursorNote.getString(cursorNote.getColumnIndex(ContactsContract.CommonDataKinds.Note.NOTE)));
			}
			
			cursorNote.close();
		} catch (Exception e) {e.printStackTrace();}
		
		try {
			/**
			 * Get the Birthday from ContactsContract.Data.CONTENT_URI
			 */
			String[] projectionBirthday = new String[] { ContactsContract.CommonDataKinds.Event.START_DATE };
			
			String whereBirthday = ContactsContract.Data.RAW_CONTACT_ID + " = " + contactId + " AND "
					+ ContactsContract.Data.MIMETYPE + " = '" + ContactsContract.CommonDataKinds.Event.CONTENT_ITEM_TYPE + "'";
			
			Cursor cursorBirthday = context.getContentResolver().query(ContactsContract.Data.CONTENT_URI, projectionBirthday, whereBirthday, null, null);
			
			if (cursorBirthday.moveToFirst()) {
				setFormatStringBirthday(cursorBirthday.getString(cursorBirthday.getColumnIndex(ContactsContract.CommonDataKinds.Event.START_DATE)));
			}
			
			cursorBirthday.close();
		} catch (Exception e) {e.printStackTrace();}
		
		try {
			/**
			 * Get the Nickname from ContactsContract.Data.CONTENT_URI
			 */
			String[] projectionNickname = new String[] { ContactsContract.CommonDataKinds.Nickname.NAME };
			
			String whereNickname = ContactsContract.Data.RAW_CONTACT_ID + " = " + contactId + " AND "
					+ ContactsContract.Data.MIMETYPE + " = '" + ContactsContract.CommonDataKinds.Nickname.CONTENT_ITEM_TYPE + "'";
	
			Cursor cursorNickname = context.getContentResolver().query(ContactsContract.Data.CONTENT_URI, projectionNickname, whereNickname, null, null);
			
			if (cursorNickname.moveToFirst()) {
				setNickname(cursorNickname.getString(cursorNickname.getColumnIndex(ContactsContract.CommonDataKinds.Nickname.NAME)));
			}
			
			cursorNickname.close();
		} catch (Exception e) {e.printStackTrace();}
		
		try {
			/**
			 * Get the Email from ContactsContract.Data.CONTENT_URI
			 */
			String[] projectionEmail = new String[] { ContactsContract.CommonDataKinds.Email.DATA };
			
			String whereEmail = ContactsContract.Data.RAW_CONTACT_ID + " = " + contactId + " AND "
				+ ContactsContract.Data.MIMETYPE + " = '" + ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE + "'";
			
			Cursor cursorEmail = context.getContentResolver().query(ContactsContract.Data.CONTENT_URI, projectionEmail, whereEmail, null, ContactsContract.Data._ID + " ASC");
			
			if (cursorEmail.moveToFirst()) {
				do {
					addEmail(cursorEmail.getString(cursorEmail.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA)));
				} while (cursorEmail.moveToNext());
			}
			
			cursorEmail.close();
		} catch (Exception e) {e.printStackTrace();}
		
		try {
			/**
			 * Get the Home Number from ContactsContract.Data.CONTENT_URI
			 */
			String[] projectionHomeNumber = new String[] { ContactsContract.CommonDataKinds.Phone.NUMBER };
			
			String whereHomeNumber = ContactsContract.Data.RAW_CONTACT_ID + " = " + contactId + " AND "
					+ ContactsContract.Data.MIMETYPE + " = '" + ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE + "'" + " AND "
					+ ContactsContract.CommonDataKinds.Phone.TYPE + " = " + ContactsContract.CommonDataKinds.Phone.TYPE_HOME;
			
			Cursor cursorHomeNumber = context.getContentResolver().query(ContactsContract.Data.CONTENT_URI, projectionHomeNumber, whereHomeNumber, null, null);
			
			if (cursorHomeNumber.moveToFirst()) {
				setHomeNumber(cursorHomeNumber.getString(cursorHomeNumber.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)));
			}
			
			cursorHomeNumber.close();
		} catch (Exception e) {e.printStackTrace();}
		
		try {
			/**
			 * Get the Mobile Number from ContactsContract.Data.CONTENT_URI
			 */
			String[] projectionMobileNumber = new String[] { ContactsContract.CommonDataKinds.Phone.NUMBER };
			
			String whereMobileNumber = ContactsContract.Data.RAW_CONTACT_ID + " = " + contactId + " AND "
					+ ContactsContract.Data.MIMETYPE + " = '" + ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE + "'" + " AND "
					+ ContactsContract.CommonDataKinds.Phone.TYPE + " = " + ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE;
			
			Cursor cursorMobileNumber = context.getContentResolver().query(ContactsContract.Data.CONTENT_URI, projectionMobileNumber, whereMobileNumber, null, null);
			
			if (cursorMobileNumber.moveToFirst()) {
				setMobileNumber(cursorMobileNumber.getString(cursorMobileNumber.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)));
			}
			
			cursorMobileNumber.close();
		} catch (Exception e) {e.printStackTrace();}
		
		try {
			/**
			 * Get the Work Number from ContactsContract.Data.CONTENT_URI
			 */
			String[] projectionWorkNumber = new String[] { ContactsContract.CommonDataKinds.Phone.NUMBER };
			
			String whereWorkNumber = ContactsContract.Data.RAW_CONTACT_ID + " = " + contactId + " AND "
					+ ContactsContract.Data.MIMETYPE + " = '" + ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE + "'" + " AND "
					+ ContactsContract.CommonDataKinds.Phone.TYPE + " = " + ContactsContract.CommonDataKinds.Phone.TYPE_WORK;
			
			Cursor cursorWorkNumber = context.getContentResolver().query(ContactsContract.Data.CONTENT_URI, projectionWorkNumber, whereWorkNumber, null, null);
			
			if (cursorWorkNumber.moveToFirst()) {
				setWorkNumber(cursorWorkNumber.getString(cursorWorkNumber.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)));
			}
			
			cursorWorkNumber.close();
		} catch (Exception e) {e.printStackTrace();}
		
		try {
			/**
			 * Get the Main Number from ContactsContract.Data.CONTENT_URI
			 */
			String[] projectionMainNumber = new String[] { ContactsContract.CommonDataKinds.Phone.NUMBER };
			
			String whereMainNumber = ContactsContract.Data.RAW_CONTACT_ID + " = " + contactId + " AND "
					+ ContactsContract.Data.MIMETYPE + " = '" + ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE + "'" + " AND "
					+ ContactsContract.CommonDataKinds.Phone.TYPE + " = " + ContactsContract.CommonDataKinds.Phone.TYPE_MAIN;
			
			Cursor cursorMainNumber = context.getContentResolver().query(ContactsContract.Data.CONTENT_URI, projectionMainNumber, whereMainNumber, null, null);
			
			if (cursorMainNumber.moveToFirst()) {
				setMainNumber(cursorMainNumber.getString(cursorMainNumber.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)));
			}
			
			cursorMainNumber.close();
		} catch (Exception e) {e.printStackTrace();}
		
		try {
			/**
			 * Get the Home Address from ContactsContract.Data.CONTENT_URI
			 */
			String[] projectionHomeAddress = new String[] { ContactsContract.CommonDataKinds.StructuredPostal.COUNTRY, 
															ContactsContract.CommonDataKinds.StructuredPostal.STREET, 
															ContactsContract.CommonDataKinds.StructuredPostal.POSTCODE, 
															ContactsContract.CommonDataKinds.StructuredPostal.CITY, 
															ContactsContract.CommonDataKinds.StructuredPostal.REGION };
			
			String whereHomeAddress = ContactsContract.Data.RAW_CONTACT_ID + " = " + contactId + " AND "
			+ ContactsContract.Data.MIMETYPE + " = '" + ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE + "'" + " AND "
			+ ContactsContract.CommonDataKinds.StructuredPostal.TYPE + " = " + ContactsContract.CommonDataKinds.StructuredPostal.TYPE_HOME;
			
			Cursor cursorHomeAddress = context.getContentResolver().query(ContactsContract.Data.CONTENT_URI, projectionHomeAddress, whereHomeAddress, null, null);
			
			if (cursorHomeAddress.moveToFirst()) {
				setHomeAddress(new Address("Home", 
						cursorHomeAddress.getString(cursorHomeAddress.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.COUNTRY)), 
						cursorHomeAddress.getString(cursorHomeAddress.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.STREET)), 
						cursorHomeAddress.getString(cursorHomeAddress.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.POSTCODE)),
						cursorHomeAddress.getString(cursorHomeAddress.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.CITY)),
						cursorHomeAddress.getString(cursorHomeAddress.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.REGION))));
			}
			
			cursorHomeAddress.close();
		} catch (Exception e) {e.printStackTrace();}
		
		try {
			/**
			 * Get the Work Address from ContactsContract.Data.CONTENT_URI
			 */
			String[] projectionWorkAddress = new String[] { ContactsContract.CommonDataKinds.StructuredPostal.COUNTRY, 
															ContactsContract.CommonDataKinds.StructuredPostal.STREET, 
															ContactsContract.CommonDataKinds.StructuredPostal.POSTCODE, 
															ContactsContract.CommonDataKinds.StructuredPostal.CITY, 
															ContactsContract.CommonDataKinds.StructuredPostal.REGION };
			
			String whereWorkAddress = ContactsContract.Data.RAW_CONTACT_ID + " = " + contactId + " AND "
			+ ContactsContract.Data.MIMETYPE + " = '" + ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE + "'" + " AND "
			+ ContactsContract.CommonDataKinds.StructuredPostal.TYPE + " = " + ContactsContract.CommonDataKinds.StructuredPostal.TYPE_WORK;
			
			Cursor cursorWorkAddress = context.getContentResolver().query(ContactsContract.Data.CONTENT_URI, projectionWorkAddress, whereWorkAddress, null, null);
			
			if (cursorWorkAddress.moveToFirst()) {
				setWorkAddress(new Address("Work", 
						cursorWorkAddress.getString(cursorWorkAddress.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.COUNTRY)), 
						cursorWorkAddress.getString(cursorWorkAddress.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.STREET)), 
						cursorWorkAddress.getString(cursorWorkAddress.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.POSTCODE)),
						cursorWorkAddress.getString(cursorWorkAddress.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.CITY)),
						cursorWorkAddress.getString(cursorWorkAddress.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.REGION))));
			}
			
			cursorWorkAddress.close();
		} catch (Exception e) {e.printStackTrace();}
		
		try {
			/**
			 * Get the Other Address from ContactsContract.Data.CONTENT_URI
			 */
			String[] projectionOtherAddress = new String[] { ContactsContract.CommonDataKinds.StructuredPostal.COUNTRY, 
															ContactsContract.CommonDataKinds.StructuredPostal.STREET, 
															ContactsContract.CommonDataKinds.StructuredPostal.POSTCODE, 
															ContactsContract.CommonDataKinds.StructuredPostal.CITY, 
															ContactsContract.CommonDataKinds.StructuredPostal.REGION };
			
			String whereOtherAddress = ContactsContract.Data.RAW_CONTACT_ID + " = " + contactId + " AND "
			+ ContactsContract.Data.MIMETYPE + " = '" + ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE + "'" + " AND "
			+ ContactsContract.CommonDataKinds.StructuredPostal.TYPE + " = " + ContactsContract.CommonDataKinds.StructuredPostal.TYPE_OTHER;
			
			Cursor cursorOtherAddress = context.getContentResolver().query(ContactsContract.Data.CONTENT_URI, projectionOtherAddress, whereOtherAddress, null, null);
			
			if (cursorOtherAddress.moveToFirst()) {
				setOtherAddress(new Address("Other", 
						cursorOtherAddress.getString(cursorOtherAddress.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.COUNTRY)), 
						cursorOtherAddress.getString(cursorOtherAddress.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.STREET)), 
						cursorOtherAddress.getString(cursorOtherAddress.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.POSTCODE)),
						cursorOtherAddress.getString(cursorOtherAddress.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.CITY)),
						cursorOtherAddress.getString(cursorOtherAddress.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.REGION))));
			}
			
			cursorOtherAddress.close();
		} catch (Exception e) {e.printStackTrace();}
		
		try {
			/**
			 * Get the Custom Address from ContactsContract.Data.CONTENT_URI
			 */
			String[] projectionCustomAddress = new String[] { ContactsContract.CommonDataKinds.StructuredPostal.COUNTRY, 
															ContactsContract.CommonDataKinds.StructuredPostal.STREET, 
															ContactsContract.CommonDataKinds.StructuredPostal.POSTCODE, 
															ContactsContract.CommonDataKinds.StructuredPostal.CITY, 
															ContactsContract.CommonDataKinds.StructuredPostal.REGION };
			
			String whereCustomAddress = ContactsContract.Data.RAW_CONTACT_ID + " = " + contactId + " AND "
			+ ContactsContract.Data.MIMETYPE + " = '" + ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE + "'" + " AND "
			+ ContactsContract.CommonDataKinds.StructuredPostal.TYPE + " = " + ContactsContract.CommonDataKinds.StructuredPostal.TYPE_CUSTOM;
			
			Cursor cursorCustomAddress = context.getContentResolver().query(ContactsContract.Data.CONTENT_URI, projectionCustomAddress, whereCustomAddress, null, null);
			
			if (cursorCustomAddress.moveToFirst()) {
				setCustomAddress(new Address("Custom", 
						cursorCustomAddress.getString(cursorCustomAddress.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.COUNTRY)), 
						cursorCustomAddress.getString(cursorCustomAddress.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.STREET)), 
						cursorCustomAddress.getString(cursorCustomAddress.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.POSTCODE)),
						cursorCustomAddress.getString(cursorCustomAddress.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.CITY)),
						cursorCustomAddress.getString(cursorCustomAddress.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.REGION))));
			}
			
			cursorCustomAddress.close();
		} catch (Exception e) {e.printStackTrace();}
		
		try {
			/**
			 * Get the Photo Uri from ContactsContract.Data.CONTENT_URI
			 */
			String[] projectionPhoto = new String[] { ContactsContract.CommonDataKinds.Photo.PHOTO_ID };
			
			String wherePhoto = ContactsContract.Data.RAW_CONTACT_ID + " = " + contactId + " AND "
					+ ContactsContract.Data.MIMETYPE + " = '" + ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE + "'";
			
			Cursor cursorPhoto = context.getContentResolver().query(ContactsContract.Data.CONTENT_URI, projectionPhoto, wherePhoto, null, null);
			
			if (cursorPhoto.moveToFirst()) {
				Long photoId = cursorPhoto.getLong(cursorPhoto.getColumnIndex(ContactsContract.CommonDataKinds.Photo.PHOTO_ID));
				if (photoId > 0) {
					setPhotoURI(ContentUris.withAppendedId(ContactsContract.Data.CONTENT_URI, photoId));
				}
			}
			
			cursorPhoto.close();
		} catch (Exception e) {e.printStackTrace();}
	}
	
	public Contact(String first_name, String last_name, String middle_name,
			String organization, String department, String notes,
			Date birthday, String job_title, String nickname,
			String display_name, ArrayList<String> emails, String home_number,
			String mobile_number, String work_number, String main_number,
			Address homeAddress, Address workAddress, Address otherAddress,
			Address customAddress, Uri photoURI) {
		this.first_name = first_name;
		this.last_name = last_name;
		this.middle_name = middle_name;
		this.organization = organization;
		this.department = department;
		this.notes = notes;
		this.birthday = birthday;
		this.nickname = nickname;
		this.display_name = display_name;
		this.emails = emails;
		this.home_number = home_number;
		this.mobile_number = mobile_number;
		this.work_number = work_number;
		this.main_number = main_number;
		this.homeAddress = homeAddress;
		this.workAddress = workAddress;
		this.otherAddress = otherAddress;
		this.customAddress = customAddress;
		this.photoURI = photoURI;
	}
	
	public String getFirstName() {
		return first_name;
	}

	public void setFirstName(String first_name) {
		this.first_name = first_name;
	}

	public String getLastName() {
		return last_name;
	}

	public void setLastName(String last_name) {
		this.last_name = last_name;
	}

	public String getMiddleName() {
		return middle_name;
	}

	public void setMiddleName(String middle_name) {
		this.middle_name = middle_name;
	}

	public String getOrganization() {
		return organization;
	}

	public void setOrganization(String organization) {
		this.organization = organization;
	}

	public String getDepartment() {
		return department;
	}

	public void setDepartment(String department) {
		this.department = department;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public Date getBirthday() {
		return birthday;
	}
	
	public String getFormatStringBirthday() {
		if (birthday != null) {
			return sdf.format(birthday);
		}
		else {
			return "";
		}
	}

	public void setBirthday(Date birthday) {
		this.birthday = birthday;
	}

	public void setFormatStringBirthday(String strBirthday) {
		try {
			birthday = sdf.parse(strBirthday);
		} catch (Exception e) {
			birthday = null;
		}
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getDisplayName() {
		return display_name;
	}

	public void setDisplayName(String display_name) {
		this.display_name = display_name;
	}
	
	public String getFirstEmail() {
		if (emails != null && emails.size() > 0)
			return emails.get(0);
		
		return "";
	}
	
	public ArrayList<String> getEmails() {
		return emails;
	}
	
	public void addEmail(String email) {
		if (emails == null) {
			emails = new ArrayList<String>();
		}
		emails.add(email);
	}
	
	public void setEmails(ArrayList<String> emails) {
		this.emails = emails;
	}
	
	public void setEmails(String[] emails) {
		this.emails = new ArrayList<String>();
		for (String email : emails) {
			this.emails.add(email);
		}
	}
	
	public String getPhoneNumber() {
		String number = getMainNumber();
		if (number != null && !number.trim().equals(""))
			return number;
		
		number = getMobileNumber();
		if (number != null && !number.trim().equals(""))
			return number;
		
		number = getWorkNumber();
		if (number != null && !number.trim().equals(""))
			return number;
		
		number = getHomeNumber();
		if (number != null && !number.trim().equals(""))
			return number;
		
		return number;
	}
	
	public String getHomeNumber() {
		return home_number;
	}

	public void setHomeNumber(String home_number) {
		this.home_number = home_number;
	}

	public String getMobileNumber() {
		return mobile_number;
	}

	public void setMobileNumber(String mobile_number) {
		this.mobile_number = mobile_number;
	}

	public String getWorkNumber() {
		return work_number;
	}

	public void setWorkNumber(String work_number) {
		this.work_number = work_number;
	}

	public String getMainNumber() {
		return main_number;
	}

	public void setMainNumber(String main_number) {
		this.main_number = main_number;
	}

	public Address getHomeAddress() {
		return homeAddress;
	}

	public void setHomeAddress(Address homeAddress) {
		this.homeAddress = homeAddress;
	}

	public Address getWorkAddress() {
		return workAddress;
	}

	public void setWorkAddress(Address workAddress) {
		this.workAddress = workAddress;
	}
	
	public Address getOtherAddress() {
		return otherAddress;
	}
	
	public void setOtherAddress(Address otherAddress) {
		this.otherAddress = otherAddress;
	}
	
	public Address getCustomAddress() {
		return customAddress;
	}
	
	public void setCustomAddress(Address customAddress) {
		this.customAddress = customAddress;
	}
	
	public Uri getPhotoURI() {
		return photoURI;
	}
	
	public void setPhotoURI(Uri photoURI) {
		this.photoURI = photoURI;
	}
}
