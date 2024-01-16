package edu.stevens.cs548.clinic.json.bind;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import edu.stevens.cs548.clinic.service.dto.*;
import edu.stevens.cs548.clinic.service.dto.util.GsonFactory;

import java.io.*;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

public class App {

	public static final String PATIENTS = "patients";

	public static final String PROVIDERS = "providers";

	public static final String TREATMENTS = "treatments";

	private static final Logger logger = Logger.getLogger(App.class.getCanonicalName());

	private final BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

	private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");

	private final PatientDtoFactory patientFactory = new PatientDtoFactory();

	private final ProviderDtoFactory providerFactory = new ProviderDtoFactory();

	private final TreatmentDtoFactory treatmentFactory = new TreatmentDtoFactory();

	private final Gson gson;
	
	private final List<PatientDto> patients = new ArrayList<PatientDto>();

	private final List<ProviderDto> providers = new ArrayList<ProviderDto>();

	private final List<TreatmentDto> treatments = new ArrayList<TreatmentDto>();

	public void severe(String s) {
		logger.severe(s);
	}

	public void severe(Exception e) {
		logger.log(Level.SEVERE, "Error during processing!", e);
	}

	public void warning(String s) {
		logger.info(s);
	}

	public void info(String s) {
		logger.info(s);
	}

	/**
	 * @param args the command line arguments
	 */
	public static void main(String[] args) {
		new App(args);
	}

	static void msg(String m) {
		System.out.print(m);
	}

	static void msgln(String m) {
		System.out.println(m);
	}

	static void err(String s) {
		System.err.println("** " + s);
	}

	public App(String[] args) {
		
		gson = GsonFactory.createGson();
		
		// Main command-line interface loop

		while (true) {
			try {
				msg("cs548> ");
				String line = in.readLine();
				if (line == null) {
					return;
				}
				String[] inputs = line.split("\\s+");
				if (inputs.length > 0) {
					String cmd = inputs[0];
					if (cmd.length() == 0)
						;
					else if ("load".equals(cmd))
						load(inputs);
					else if ("save".equals(cmd))
						save(inputs);
					else if ("addpatient".equals(cmd))
						addPatient();
					else if ("addprovider".equals(cmd))
						addProvider();
					else if ("addtreatment".equals(cmd))
						addOneTreatment();
					else if ("list".equals(cmd))
						list(inputs);
					else if ("help".equals(cmd))
						help(inputs);
					else if ("quit".equals(cmd))
						return;
					else
						msgln("Bad input.  Type \"help\" for more information.");
				}
			} catch (Exception e) {
				severe(e);
			}
		}
	}

	public void load(String[] inputs) throws IOException, ParseException {
		if (inputs.length != 2) {
			err("Usage: load <filename>");
		}
		File input = new File(inputs[1]);
		if (!input.equals(input)) {
			err("File " + input + " does not exist!");
		}
		JsonReader rd = gson.newJsonReader(new BufferedReader(new FileReader(input)));
		rd.beginObject();

		/*
		 * Parse the list of patients.
		 */
		if (!PATIENTS.equals(rd.nextName())) {
			throw new ParseException("Expected field: " + PATIENTS, 0);
		}
		rd.beginArray();
		while (rd.hasNext()) {
			PatientDto patient = gson.fromJson(rd, PatientDto.class);
			patients.add(patient);
		}
		rd.endArray();

		/*
		 * Parse the list of providers.
		 */
		if (!PROVIDERS.equals(rd.nextName())) {
			throw new ParseException("Expected field: " + PROVIDERS, 0);
		}
		rd.beginArray();
		while (rd.hasNext()) {
			ProviderDto provider = gson.fromJson(rd, ProviderDto.class);
			providers.add(provider);
		}
		rd.endArray();

		/*
		 * TODO Load the treatment information
		 */
		if (!TREATMENTS.equals(rd.nextName())) {
			throw new ParseException("Expected field: " + TREATMENTS, 0);
		}
		rd.beginArray();
		while (rd.hasNext()){
			TreatmentDto treatment = gson.fromJson(rd, TreatmentDto.class);
			treatments.add(treatment);
		}
		rd.endArray();
		rd.endObject();
	}

	//"resource"/
	public void save(String[] inputs) throws IOException {
		if (inputs.length != 2) {
			err("Usage: save <filename>");
		}
		File output = new File(inputs[1]);
		try (JsonWriter wr = gson.newJsonWriter(new BufferedWriter(new FileWriter(output)))) {
			wr.beginObject();

			wr.name(PATIENTS);
			wr.beginArray();
			for (PatientDto patient : patients) {
				gson.toJson(patient, PatientDto.class, wr);
			}
			wr.endArray();

			wr.name(PROVIDERS);
			wr.beginArray();
			for (ProviderDto provider : providers) {
				gson.toJson(provider, ProviderDto.class, wr);
			}
			wr.endArray();

			/*
			 * TODO Save the treatment information.
			 */
			wr.name(TREATMENTS);
			wr.beginArray();
			for (TreatmentDto treatment : treatments) {
				gson.toJson(treatment, TreatmentDto.class, wr);
			}
			wr.endArray();
			wr.endObject();
		}
	}

	public void addPatient() throws IOException, ParseException {
		PatientDto patient = patientFactory.createPatientDto();
		patient.setId(UUID.randomUUID());
		msg("Name: ");
		patient.setName(in.readLine());
		patient.setDob(readDate("Patient DOB"));
		patients.add(patient);
	}

	public void addProvider() throws IOException, ParseException {
		ProviderDto provider = providerFactory.createProviderDto();
		provider.setId(UUID.randomUUID());
		msg("NPI: ");
		provider.setNpi(in.readLine());
		msg("Name: ");
		provider.setName(in.readLine());
		providers.add(provider);
	}
	
	/* 
	 * Use this to add a list of follow-up treatments.
	 */
	public void addTreatmentList(Collection<TreatmentDto> treatments) throws IOException, ParseException {
		TreatmentDto treatment = addTreatment();
		while (treatment != null) {
			treatments.add(treatment);
			treatment = addTreatment();
		}
	}

	public void addOneTreatment() throws IOException, ParseException {
		TreatmentDto treatment = addTreatment();
		if (treatment != null) {
			treatments.add(treatment);
		}
	}
	
	public TreatmentDto addTreatment() throws IOException, ParseException {
		msg("What form of treatment: [D]rug, [S]urgery, [R]adiology, [P]hysiotherapy? ");
		String line = in.readLine().toUpperCase();
		TreatmentDto treatment = null;
		if ("D".equals(line)) {
			treatment = addDrugTreatment();
		} else if ("S".equals(line)) {
			treatment = addSurgeryTreatment();
		} else if ("R".equals(line)) {
			treatment = addRadiologyTreatment();
		} else if ("P".equals(line)) {
			treatment = addPhysiotherapyTreatment();
		}

		if (treatment != null) {
			msgln("Adding follow-up treatments...");
			addTreatmentList(treatment.getFollowupTreatments());
			msgln("...finished follow-up treatments");

			addPatientTreatment(treatment);
			addPProviderTreatment(treatment);
		}
		return treatment;
	}

	private DrugTreatmentDto addDrugTreatment() throws IOException, ParseException {
		DrugTreatmentDto treatment = treatmentFactory.createDrugTreatmentDto();

		treatment.setId(UUID.randomUUID());
		msg("Patient ID: ");
		treatment.setPatientId(UUID.fromString(in.readLine()));
		msg("Patient Name: ");
		treatment.setPatientName(in.readLine());
		msg("Provider ID: ");
		treatment.setProviderId(UUID.fromString(in.readLine()));
		msg("Provider Name: ");
		treatment.setProviderName(in.readLine());
		msg("Diagnosis: ");
		treatment.setDiagnosis(in.readLine());
		msg("Drug: ");
		treatment.setDrug(in.readLine());
		msg("Dosage: ");
		treatment.setDosage(Float.parseFloat(in.readLine()));
		treatment.setStartDate(readDate("Start date"));
		treatment.setEndDate(readDate("End date"));
		msg("Frequency: ");
		treatment.setFrequency(Integer.parseInt(in.readLine()));

		return treatment;
	}

	private SurgeryTreatmentDto addSurgeryTreatment() throws IOException, ParseException {
		SurgeryTreatmentDto treatment = treatmentFactory.createSurgeryTreatmentDto();

		// TODO finish this
		treatment.setId(UUID.randomUUID());
		msg("Patient ID: ");
		treatment.setPatientId(UUID.fromString(in.readLine()));
		msg("Patient Name: ");
		treatment.setPatientName(in.readLine());
		msg("Provider ID: ");
		treatment.setProviderId(UUID.fromString(in.readLine()));
		msg("Provider Name: ");
		treatment.setProviderName(in.readLine());
		msg("Diagnosis: ");
		treatment.setDiagnosis(in.readLine());
		msg("Discharge instructions: ");
		treatment.setDischargeInstructions(in.readLine());
		treatment.setSurgeryDate(readDate("Surgery date"));

		return treatment;
	}

	private RadiologyTreatmentDto addRadiologyTreatment() throws IOException, ParseException {
		RadiologyTreatmentDto treatment = treatmentFactory.createRadiologyTreatmentDto();

		treatment.setId(UUID.randomUUID());
		msg("Patient ID: ");
		treatment.setPatientId(UUID.fromString(in.readLine()));
		msg("Patient Name: ");
		treatment.setPatientName(in.readLine());
		msg("Provider ID: ");
		treatment.setProviderId(UUID.fromString(in.readLine()));
		msg("Provider Name: ");
		treatment.setProviderName(in.readLine());
		msg("Diagnosis: ");
		treatment.setDiagnosis(in.readLine());

		LocalDate date = readDate("Treatment date");
		while (date != null) {
			treatment.getTreatmentDates().add(date);
			date = readDate("Treatment date");
		}

		return treatment;
	}

	private PhysiotherapyTreatmentDto addPhysiotherapyTreatment() throws IOException, ParseException {
		PhysiotherapyTreatmentDto treatment = treatmentFactory.createPhysiotherapyTreatmentDto();

		treatment.setId(UUID.randomUUID());
		msg("Patient ID: ");
		treatment.setPatientId(UUID.fromString(in.readLine()));
		msg("Patient Name: ");
		treatment.setPatientName(in.readLine());
		msg("Provider ID: ");
		treatment.setProviderId(UUID.fromString(in.readLine()));
		msg("Provider Name: ");
		treatment.setProviderName(in.readLine());
		msg("Diagnosis: ");
		treatment.setDiagnosis(in.readLine());

		LocalDate date = readDate("Treatment date");
		while (date != null) {
			treatment.getTreatmentDates().add(date);
			date = readDate("Treatment date");
		}

		return treatment;
	}

	private LocalDate readDate(String field) throws IOException {
		msg(String.format("%s (MM/dd/yyyy): ", field));
		String line = in.readLine();
		if (line == null || line.isBlank()) {
			return null;
		}
		return LocalDate.parse(line, dateFormatter);
	}

	private void readDates(String field, List<LocalDate> dates) throws IOException {
		msg(String.format("%s (MM/dd/yyyy): ", field));
		String input = in.readLine();
		while (input != null && !input.isBlank()) {
			dates.add(LocalDate.parse(input));
			msg(String.format("%s (MM/dd/yyyy): ", field));
			input = in.readLine();
		}
	}

	private void addPatientTreatment(TreatmentDto treatment) {
		UUID patientId = treatment.getPatientId();
		for (PatientDto patient : patients) {
			if (patient.getId().equals(patientId)) {
				patient.addTreatment(treatment);
				return;
			}
		}
		msgln("No such patient id: " + patientId);
	}

	private void addPProviderTreatment(TreatmentDto treatment) {
		UUID providerId = treatment.getProviderId();
		for (ProviderDto provider : providers) {
			if (provider.getId().equals(providerId)) {
				provider.addTreatment(treatment);
				return;
			}
		}
		msgln("No such provider id: " + providerId);
	}

	public void list(String[] inputs) {
		msgln("Patients:");
		for (PatientDto patient : patients) {
			msgln(gson.toJson(patient));
		}
		
		msgln("Providers:");
		for (ProviderDto provider : providers) {
			msgln(gson.toJson(provider));
		}
		
		msgln("Treatments:");
		for (TreatmentDto treatment : treatments) {
			msgln(gson.toJson(treatment));
		}
	}

	public void help(String[] inputs) {
		if (inputs.length == 1) {
			msgln("Commands are:");
			msgln("  load filename: load database from a file");
			msgln("  save filename: save database to a file");
			msgln("  addpatient: add a patient");
			msgln("  addprovider: add a provider");
			msgln("  addtreatment: add a treatment");
			msgln("  list: display database content");
			msgln("  quit: exit the app");
		}
	}

}
