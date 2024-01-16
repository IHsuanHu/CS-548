package edu.stevens.cs548.clinic.service.dto;

import com.google.gson.annotations.SerializedName;

import java.time.LocalDate;

public class SurgeryTreatmentDto extends TreatmentDto {

	/*
	 * TODO add two fields:
	 *  surgeryDate (type LocalDate)
	 *  dischargeInstructions (type String)
	 * Annotate with @SerializedName (surgery-date and discharge-instructions)
	 * Also add getter and setter methods for these properties.
	 */
	@SerializedName("surgery-date")
	private LocalDate surgeryDate;
	public LocalDate getSurgeryDate(){	return surgeryDate;}

	public void setSurgeryDate(LocalDate surgeryDate){this.surgeryDate = surgeryDate;}

	@SerializedName("discharge-instructions")
	private String dischargeInstructions;

	public String getDischargeInstructions() {
		return dischargeInstructions;
	}

	public void setDischargeInstructions(String dischargeInstructions) {this.dischargeInstructions = dischargeInstructions;}

}
