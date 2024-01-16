package edu.stevens.cs548.clinic.json.schema;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.stream.JsonReader;
import edu.stevens.cs548.clinic.service.dto.PatientDto;
import edu.stevens.cs548.clinic.service.dto.ProviderDto;
import edu.stevens.cs548.clinic.service.dto.TreatmentDto;
import edu.stevens.cs548.clinic.service.dto.util.GsonFactory;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.text.ParseException;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import com.networknt.schema.ValidationMessage;

public class App {

	public static final String PATIENTS = "patients";

	public static final String PROVIDERS = "providers";

	public static final String TREATMENTS = "treatments";

	public static final String BASE_URI = "https://cs548.stevens.edu/clinic";

	public static final String PATIENT_URI = BASE_URI + "/patient";

	public static final String PROVIDER_URI = BASE_URI + "/provider";

	public static final String TREATMENT_URI = BASE_URI + "/treatment";

	public static final String PATIENT_SCHEMA = "/schema/patient-schema.json";

	public static final String PROVIDER_SCHEMA = "/schema/provider-schema.json";

	public static final String TREATMENT_SCHEMA = "/schema/treatment-schema.json";

	private static final Logger logger = Logger.getLogger(App.class.getCanonicalName());

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

	static void msg(String m) {
		System.out.print(m);
	}

	static void msgln(String m) {
		System.out.println(m);
	}

	static void err(String s) {
		System.err.println("** " + s);
	}

	/**
	 * @param args the command line arguments
	 */
	public static void main(String[] args) {
		try {
			new App(args);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public App(String[] args) throws IOException, ParseException {

		JsonSchema patientSchema = getSchema(PATIENT_SCHEMA);

		JsonSchema providerSchema = getSchema(PROVIDER_SCHEMA);

		JsonSchema treatmentSchema = getSchema(TREATMENT_SCHEMA);

		Gson gson = GsonFactory.createGson();

		try (JsonReader rd = gson.newJsonReader(new BufferedReader(new InputStreamReader(System.in)))) {

			rd.beginObject();

			/*
			 * Parse the list of patients.
			 */
			if (!PATIENTS.equals(rd.nextName())) {
				throw new ParseException("Expected field: " + PATIENTS, 0);
			}
			rd.beginArray();
			while (rd.hasNext()) {
				JsonElement patient = gson.fromJson(rd, JsonElement.class);
				msgln("Validating patient: ");
				String patientJson = gson.toJson(patient);
				msgln(patientJson);
				validate(patientSchema, patientJson);
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
				JsonElement provider = gson.fromJson(rd, JsonElement.class);
				msgln("Validating provider: ");
				String providerJson = gson.toJson(provider);
				msgln(providerJson);
				validate(providerSchema, providerJson);
			}
			rd.endArray();

			/*
			 * Parse the list of treatments.
			 */
			if (!TREATMENTS.equals(rd.nextName())) {
				throw new ParseException("Expected field: " + TREATMENTS, 0);
			}
			rd.beginArray();
			while (rd.hasNext()) {
				JsonElement treatment = gson.fromJson(rd, JsonElement.class);
				msgln("Validating treatment: ");
				String treatmentJson = gson.toJson(treatment);
				msgln(treatmentJson);
				validate(treatmentSchema, treatmentJson);
			}
			rd.endArray();

			rd.endObject();

		}
		
	}

	private void validate(JsonSchema schema, String json) {
		JsonNode jsonNode = getJson(json);

		Set<ValidationMessage> validationResult = schema.validate(jsonNode);

		if (validationResult.isEmpty()) {
			msgln("Validation succeeded!");
		} else {
			msgln("Validation failed!");
			for (ValidationMessage message : validationResult) {
				msgln(message.getMessage());
			}
		}

		msgln("");

	}

	private JsonSchema getSchema(String filename) {
		JsonSchemaFactory schemaFactory = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V202012);
		try (InputStream in = getClass().getResourceAsStream(filename)) {
			return schemaFactory.getSchema(in);
		} catch (IOException e) {
			throw new IllegalStateException("Error reading schema file: " + filename, e);
		}
	}

	private JsonNode getJson(String json) {
		Reader rd = new StringReader(json);
		ObjectMapper mapper = new ObjectMapper();
		try {
			return mapper.readTree(rd);
		} catch (IOException e) {
			throw new IllegalStateException("Error reading JSON string: " + json, e);

		}
	}
}
