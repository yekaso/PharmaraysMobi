package com.niftyhybrid.pharmarays.data;

public class LoggedInPharmacy {
	private String memberId = null;
	private String pharmacyId = null;
	private String memberName = null;
	private String loginUserRoleId = null;
	private String pharmacyName = null;
	private String pharmacyAddress = null;
	private String pharmacyEmail = null;
	private String pharmacyPhone = null;

	public String getMemberId() {
		return memberId;
	}

	public void setMemberId(String memberId) {
		this.memberId = memberId;
	}

	public String getPharmacyId() {
		return pharmacyId;
	}

	public void setPharmacyId(String pharmacyId) {
		this.pharmacyId = pharmacyId;
	}

	public String getMemberName() {
		return memberName;
	}

	public void setMemberName(String memberName) {
		this.memberName = memberName;
	}

	public String getLoginUserRoleId() {
		return loginUserRoleId;
	}

	public void setLoginUserRoleId(String loginUserRoleId) {
		this.loginUserRoleId = loginUserRoleId;
	}

	public String getPharmacyName() {
		return pharmacyName;
	}

	public void setPharmacyName(String pharmacyName) {
		this.pharmacyName = pharmacyName;
	}

	public String getPharmacyAddress() {
		return pharmacyAddress;
	}

	public void setPharmacyAddress(String pharmacyAddress) {
		this.pharmacyAddress = pharmacyAddress;
	}

	public String getPharmacyEmail() {
		return pharmacyEmail;
	}

	public void setPharmacyEmail(String pharmacyEmail) {
		this.pharmacyEmail = pharmacyEmail;
	}

	public String getPharmacyPhone() {
		return pharmacyPhone;
	}

	public void setPharmacyPhone(String pharmacyPhone) {
		this.pharmacyPhone = pharmacyPhone;
	}

	@Override
	public String toString() {
		return "Phone:" + this.pharmacyPhone + "| Email:" + this.pharmacyEmail
				+ "| Address:" + this.pharmacyAddress + "| Name:"
				+ this.pharmacyName + "| Member name:" + this.memberName
				+ "| member id:" + this.memberId + "| Pharmacy id:"
				+ this.pharmacyId + "| loginuserrole:" + this.loginUserRoleId;
	}

}
