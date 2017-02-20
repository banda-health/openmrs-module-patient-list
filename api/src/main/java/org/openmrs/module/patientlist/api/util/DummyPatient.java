package org.openmrs.module.patientlist.api.util;

import org.apache.commons.lang.StringUtils;
import org.openmrs.Patient;
import org.openmrs.PersonAttribute;
import org.openmrs.PersonAttributeType;
import org.openmrs.PersonAddress;
import org.openmrs.PersonName;
import org.openmrs.PatientIdentifier;
import org.openmrs.api.context.Context;
import org.openmrs.module.patientlist.api.model.PatientInformationField;

import java.util.Calendar;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Dummy patient used for live previewing.
 */
public class DummyPatient extends Patient {

	private static final int BIRTH_YEAR = 1990;
	private static final String IDENTIFIER = "4564";
	private static int BIRTH_DATE = 1;

	private DummyPatient() {}

	public static DummyPatient getInstance() {
		return Holder.INSTANCE;
	}

	private DummyPatient loadData() {
		DummyPatient dummyPatient = new DummyPatient();

		// set id
		dummyPatient.setId(1);

		// set address
		PersonAddress address = new PersonAddress();
		address.setId(1);
		address.setAddress1("Address 1");
		address.setAddress2("Address 2");
		address.setAddress3("Address 3");
		Set<PersonAddress> addresses = new HashSet<PersonAddress>();
		addresses.add(address);
		dummyPatient.setAddresses(addresses);

		// set name
		PersonName personName = new PersonName();
		personName.setId(1);
		personName.setFamilyName("Doe");
		personName.setFamilyName2("Moe");
		personName.setGivenName("Joe");
		personName.setMiddleName("Blow");
		Set<PersonName> names = new HashSet<PersonName>();
		names.add(personName);
		dummyPatient.setNames(names);

		// set person attributes
		Map<String, PatientInformationField<?>> fields =
		        PatientInformation.getInstance().getFields();
		int count = 0;
		Set<PersonAttribute> attributes = new HashSet<PersonAttribute>();
		for (Map.Entry<String, PatientInformationField<?>> field : fields.entrySet()) {
			String key = field.getKey();
			if (StringUtils.contains(key, "p.attr")) {
				PersonAttribute personAttribute = new PersonAttribute();
				personAttribute.setId(count++);
				personAttribute.setValue("TEST " + field.getValue().getName().toUpperCase());

				PersonAttributeType type =
				        Context.getPersonService().getPersonAttributeTypeByName(field.getValue().getName());

				personAttribute.setAttributeType(type);
				attributes.add(personAttribute);
			}
		}

		dummyPatient.setAttributes(attributes);

		// set gender
		dummyPatient.setGender("M");

		//set birthdate
		Calendar cal = Calendar.getInstance();
		cal.set(BIRTH_YEAR, Calendar.JANUARY, BIRTH_DATE);
		dummyPatient.setBirthdate(cal.getTime());

		// set identifier
		PatientIdentifier patientIdentifier = new PatientIdentifier();
		patientIdentifier.setId(1);
		patientIdentifier.setIdentifier(IDENTIFIER);
		Set<PatientIdentifier> identifiers = new HashSet<PatientIdentifier>();
		identifiers.add(patientIdentifier);
		dummyPatient.setIdentifiers(identifiers);

		return dummyPatient;
	}

	private static class Holder {
		private static final DummyPatient INSTANCE = new DummyPatient().loadData();
	}
}
