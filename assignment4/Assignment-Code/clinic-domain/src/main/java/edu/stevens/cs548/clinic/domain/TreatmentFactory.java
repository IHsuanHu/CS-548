package edu.stevens.cs548.clinic.domain;

import java.security.PublicKey;

public class TreatmentFactory implements ITreatmentFactory {
	
	/*
	 * Patient and provider fields are set when the treatment is added (see Provider).
	 * Id field is set when the treatment entity is synced with the database (see TreatmentDAO).
	 */

	@Override
	public DrugTreatment createDrugTreatment() {
		return new DrugTreatment();
	}

	/*
	 * TODO define other factory methods
	 */
	@Override
	public SurgeryTreatment createSurgeryTreatment() { return new SurgeryTreatment();}
	@Override
	public RadiologyTreatment createRadiologyTreatment() { return new RadiologyTreatment();}
	@Override
	public PhysiotherapyTreatment createPhysiotherapyTreatment() { return  new PhysiotherapyTreatment();}

	

}
