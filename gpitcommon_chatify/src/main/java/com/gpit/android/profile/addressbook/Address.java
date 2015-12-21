package com.gpit.android.profile.addressbook;

public class Address {

	private String type = null;

	private String country = null;

	private String street = null;

	private String zip = null;

	private String city = null;

	private String state = null;

	public Address() {
		type = null;
		country = null;
		street = null;
		zip = null;
		city = null;
		state = null;
	}

	public Address(String type, String country, String street, String zip,
			String city, String state) {
		this.type = type;
		this.country = country;
		this.street = street;
		this.zip = zip;
		this.city = city;
		this.state = state;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getStreet() {
		return street;
	}

	public void setStreet(String street) {
		this.street = street;
	}

	public String getZip() {
		return zip;
	}

	public void setZip(String zip) {
		this.zip = zip;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

}
