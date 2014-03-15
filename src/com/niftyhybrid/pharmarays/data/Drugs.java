package com.niftyhybrid.pharmarays.data;

public class Drugs {
	private Long id;
	private String name;
	private String description;
	private String brandNames;
	private String drugManufacturer;
	private boolean selected = false;
	private boolean notChecked = false;

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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	public String getBrandNames() {
		return brandNames;
	}

	public void setBrandNames(String brandNames) {
		this.brandNames = brandNames;
	}

	public String getDrugManufacturer() {
		return drugManufacturer;
	}

	public void setDrugManufacturer(String drugManufacturer) {
		this.drugManufacturer = drugManufacturer;
	}

	public boolean isNotChecked() {
		return notChecked;
	}

	public void setNotChecked(boolean notChecked) {
		this.notChecked = notChecked;
	}

}
