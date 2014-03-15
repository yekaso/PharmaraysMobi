package com.niftyhybrid.pharmarays.data;

public class Pharmacy {
	private Long id;
	private String name;
	private String emailAddress;
	private String address;
	private String phoneNumber;
	private boolean selected;
	private boolean notChecked;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmailAddress() {
		return emailAddress;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	public boolean isNotChecked() {
		return notChecked;
	}

	public void setNotChecked(boolean notChecked) {
		this.notChecked = notChecked;
	}

}
