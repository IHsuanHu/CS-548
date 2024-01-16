package edu.stevens.cs548.clinic.service.dto;

import com.google.gson.annotations.SerializedName;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class RadiologyTreatmentDto extends TreatmentDto {
	
	@SerializedName("treatment-dates")
	private List<LocalDate> treatmentDates;

	public List<LocalDate> getTreatmentDates() {
		return treatmentDates;
	}

	public void setTreatmentDates(List<LocalDate> treatmentDates) {
		this.treatmentDates = treatmentDates;
	}
	
	public RadiologyTreatmentDto() {
		this.treatmentDates = new ArrayList<>();
	}

}
