package edu.stevens.cs548.clinic.service.init;

import jakarta.annotation.PostConstruct;
import jakarta.ejb.LocalBean;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.Destroyed;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.json.bind.annotation.JsonbSubtype;
import jakarta.servlet.ServletContext;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.*;
import java.util.function.Consumer;
import java.util.logging.Logger;

import edu.stevens.cs548.clinic.service.IPatientService;
import edu.stevens.cs548.clinic.service.IProviderService;
import edu.stevens.cs548.clinic.service.dto.DrugTreatmentDto;
import edu.stevens.cs548.clinic.service.dto.PatientDto;
import edu.stevens.cs548.clinic.service.dto.PatientDtoFactory;
import edu.stevens.cs548.clinic.service.dto.PhysiotherapyTreatmentDto;
import edu.stevens.cs548.clinic.service.dto.ProviderDto;
import edu.stevens.cs548.clinic.service.dto.ProviderDtoFactory;
import edu.stevens.cs548.clinic.service.dto.RadiologyTreatmentDto;
import edu.stevens.cs548.clinic.service.dto.SurgeryTreatmentDto;
import edu.stevens.cs548.clinic.service.dto.TreatmentDto;
import edu.stevens.cs548.clinic.service.dto.TreatmentDtoFactory;

@Singleton
@LocalBean
@Startup
// @ApplicationScoped
// @Transactional
public class InitBean {

	private static final Logger logger = Logger.getLogger(InitBean.class.getCanonicalName());

	private static final ZoneId ZONE_ID = ZoneOffset.UTC;

	private PatientDtoFactory patientFactory = new PatientDtoFactory();

	private ProviderDtoFactory providerFactory = new ProviderDtoFactory();

	private TreatmentDtoFactory treatmentFactory = new TreatmentDtoFactory();

	// TODO
	@Inject
	private IPatientService patientService;

	// TODO
	@Inject
	private IProviderService providerService;

	/*
	 * Initialize using EJB logic
	 */
	@PostConstruct
	/*
	 * This should work to initialize with CDI bean, but there is a bug in
	 * Payara.....
	 */
	// public void init(@Observes @Initialized(ApplicationScoped.class)
	// ServletContext init) {
	public void init() {
		/*
		 * Put your testing logic here. Use the logger to display testing output in the
		 * server logs.
		 */
		logger.info("Your name here: ");
		System.err.println("Your name here!");

		try {

			/*
			 * Clear the database and populate with fresh data.
			 * 
			 * Note that the service generates the external ids, when adding the entities.
			 */

			providerService.removeAll();
			patientService.removeAll();

			PatientDto john = patientFactory.createPatientDto();
			john.setName("John Doe");
			john.setDob(LocalDate.parse("1995-08-15"));
			john.setId(patientService.addPatient(john));

			ProviderDto jane = providerFactory.createProviderDto();
			jane.setName("Jane Doe");
			jane.setNpi("1234");
			jane.setId(providerService.addProvider(jane));

			DrugTreatmentDto drug01 = treatmentFactory.createDrugTreatmentDto();
			drug01.setPatientId(john.getId());
			drug01.setPatientName(john.getName());
			drug01.setProviderId(jane.getId());
			drug01.setProviderName(jane.getName());
			drug01.setDiagnosis("Headache");
			drug01.setDrug("Aspirin");
			drug01.setDosage(20);
			drug01.setFrequency(7);
			drug01.setStartDate(LocalDate.ofInstant(Instant.now(), ZONE_ID));
			drug01.setEndDate(LocalDate.ofInstant(Instant.now(), ZONE_ID));
			providerService.addTreatment(drug01);

			// TODO add more testing, including treatments and providers

			RadiologyTreatmentDto radiology02 = treatmentFactory.createRadiologyTreatmentDto();
			radiology02.setPatientId(john.getId());
			radiology02.setPatientName(john.getName());
			radiology02.setProviderId(jane.getId());
			radiology02.setProviderName(jane.getName());
			radiology02.setDiagnosis("Hand pain");
			radiology02.setTreatmentDates(Arrays.asList(LocalDate.of(2010,11,25)));
			providerService.addTreatment(radiology02);

			PatientDto mike = patientFactory.createPatientDto();
			mike.setName("Mike Hu");
			mike.setDob(LocalDate.parse("2000-09-22"));
			mike.setId(patientService.addPatient(mike));

			PatientDto jack = patientFactory.createPatientDto();
			jack.setName("Jack Chou");
			jack.setDob(LocalDate.parse("2010-10-23"));
			jack.setId(patientService.addPatient(jack));

			ProviderDto leo = providerFactory.createProviderDto();
			leo.setName("Leo Lee");
			leo.setNpi("234");
			leo.setId(providerService.addProvider(leo));

			ProviderDto alex = providerFactory.createProviderDto();
			alex.setName("Alex Liu");
			alex.setNpi("224");
			alex.setId(providerService.addProvider(alex));


			SurgeryTreatmentDto surgery01 = treatmentFactory.createSurgeryTreatmentDto();
			surgery01.setPatientId(jack.getId());
			surgery01.setPatientName(jack.getName());
			surgery01.setProviderId(alex.getId());
			surgery01.setProviderName(alex.getName());
			surgery01.setDiagnosis("Headache");
			surgery01.setSurgeryDate(LocalDate.ofInstant(Instant.now(), ZONE_ID));
			surgery01.setDischargeInstructions("take more rest");
			providerService.addTreatment(surgery01);

			DrugTreatmentDto drug02 = treatmentFactory.createDrugTreatmentDto();
			drug02.setPatientId(jack.getId());
			drug02.setPatientName(jack.getName());
			drug02.setProviderId(alex.getId());
			drug02.setProviderName(alex.getName());
			drug02.setDiagnosis("Headache");
			drug02.setDrug("Aspirin");
			drug02.setDosage(10);
			drug02.setFrequency(6);
			drug02.setStartDate(LocalDate.ofInstant(Instant.now(), ZONE_ID));
			drug02.setEndDate(LocalDate.ofInstant(Instant.now(), ZONE_ID));
			providerService.addTreatment(drug02);

			DrugTreatmentDto drug03 = treatmentFactory.createDrugTreatmentDto();
			drug03.setPatientId(mike.getId());
			drug03.setPatientName(mike.getName());
			drug03.setProviderId(leo.getId());
			drug03.setProviderName(leo.getName());
			drug03.setDiagnosis("Headache");
			drug03.setDrug("Aspirin");
			drug03.setDosage(10);
			drug03.setFrequency(6);
			drug03.setStartDate(LocalDate.ofInstant(Instant.now(), ZONE_ID));
			drug03.setEndDate(LocalDate.ofInstant(Instant.now(), ZONE_ID));

			PhysiotherapyTreatmentDto physiotherapy01 = treatmentFactory.createPhysiotherapyTreatmentDto();
			physiotherapy01.setPatientId(mike.getId());
			physiotherapy01.setPatientName(mike.getName());
			physiotherapy01.setProviderId(leo.getId());
			physiotherapy01.setProviderName(leo.getName());
			physiotherapy01.setDiagnosis("Hand pain");
			physiotherapy01.setTreatmentDates(Arrays.asList(LocalDate.of(2010,10,22)));
			physiotherapy01.setFollowupTreatments(Arrays.asList(drug03));
			providerService.addTreatment(physiotherapy01);

			RadiologyTreatmentDto radiology01 = treatmentFactory.createRadiologyTreatmentDto();
			radiology01.setPatientId(mike.getId());
			radiology01.setPatientName(mike.getName());
			radiology01.setProviderId(leo.getId());
			radiology01.setProviderName(leo.getName());
			radiology01.setDiagnosis("Hand pain");
			radiology01.setTreatmentDates(Arrays.asList(LocalDate.of(2010,11,25)));
			providerService.addTreatment(radiology01);

			// Now show in the logs what has been added

			Collection<PatientDto> patients = patientService.getPatients();
			for (PatientDto p : patients) {
				logger.info(String.format("Patient %s, ID %s, DOB %s", p.getName(), p.getId().toString(),
						p.getDob().toString()));
				logTreatments(p.getTreatments());
			}

			Collection<ProviderDto> providers = providerService.getProviders();
			for (ProviderDto p : providers) {
				logger.info(String.format("Provider %s, ID %s, NPI %s", p.getName(), p.getId().toString(), p.getNpi()));
				logTreatments(p.getTreatments());
			}

		} catch (Exception e) {

			throw new IllegalStateException("Failed to add record.", e);

		}
		
	}

	public void shutdown(@Observes @Destroyed(ApplicationScoped.class) ServletContext init) {
		logger.info("App shutting down....");
	}

	private void logTreatments(Collection<TreatmentDto> treatments) {
		for (TreatmentDto treatment : treatments) {
			if (treatment instanceof DrugTreatmentDto) {
				logTreatment((DrugTreatmentDto) treatment);
			} else if (treatment instanceof SurgeryTreatmentDto) {
				logTreatment((SurgeryTreatmentDto) treatment);
			} else if (treatment instanceof RadiologyTreatmentDto) {
				logTreatment((RadiologyTreatmentDto) treatment);
			} else if (treatment instanceof PhysiotherapyTreatmentDto) {
				logTreatment((PhysiotherapyTreatmentDto) treatment);
			}
			if (!treatment.getFollowupTreatments().isEmpty()) {
				logger.info("============= Follow-up Treatments");
				logTreatments(treatment.getFollowupTreatments());
				logger.info("============= End Follow-up Treatments");
			}
		}
	}

	private void logTreatment(DrugTreatmentDto t) {
		logger.info(String.format("...Drug treatment for %s, drug %s", t.getPatientName(), t.getDrug()));
	}

	private void logTreatment(RadiologyTreatmentDto t) {
		logger.info(String.format("...Radiology treatment for %s", t.getPatientName()));
	}

	private void logTreatment(SurgeryTreatmentDto t) {
		logger.info(String.format("...Surgery treatment for %s", t.getPatientName()));
	}

	private void logTreatment(PhysiotherapyTreatmentDto t) {
		logger.info(String.format("...Physiotherapy treatment for %s", t.getPatientName()));
	}

}
