package com.niftyhybrid.pharmarays.comparator;

import com.niftyhybrid.pharmarays.data.Drugs;

public class DrugComparator implements Comparable {
	private Drugs drug;

	public DrugComparator(Drugs drug) {
		this.drug = drug;
	}

	public Drugs getDrug() {
		return drug;
	}

	public void setDrug(Drugs drug) {
		setDrug(drug);
	}

	@Override
	public int compareTo(Object obj) {
		DrugComparator drugComparator = (DrugComparator) obj;
		// TODO Auto-generated method stub
		return drug.getName().toLowerCase().trim()
				.compareTo(drugComparator.drug.getName().toLowerCase());
	}

}
