package com.niftyhybrid.pharmarays.comparator;

import com.niftyhybrid.pharmarays.data.Pharmacy;

public class PharmacyComparator implements Comparable {
	private Pharmacy pharmacy;

	public PharmacyComparator(Pharmacy pharmacy) {
		this.pharmacy = pharmacy;
	}

	public Pharmacy getPharmacy() {
		return pharmacy;
	}

	public void setPharmacy(Pharmacy pharmacy) {
		setPharmacy(pharmacy);
	}

	@Override
	public int compareTo(Object obj) {
		PharmacyComparator pharmacyComparator = (PharmacyComparator) obj;
		// TODO Auto-generated method stub
		return pharmacy.getName().toLowerCase().trim()
				.compareTo(pharmacyComparator.pharmacy.getName().toLowerCase());
	}

}
