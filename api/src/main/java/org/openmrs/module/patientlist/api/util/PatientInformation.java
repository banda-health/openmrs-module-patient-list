package org.openmrs.module.patientlist.api.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.OpenmrsData;
import org.openmrs.Patient;
import org.openmrs.PersonAttribute;
import org.openmrs.PersonAttributeType;
import org.openmrs.Visit;
import org.openmrs.VisitAttributeType;
import org.openmrs.api.context.Context;
import org.openmrs.attribute.Attribute;
import org.openmrs.attribute.AttributeType;
import org.openmrs.customdatatype.Customizable;
import org.openmrs.module.openhmis.commons.api.f.Func1;
import org.openmrs.module.patientlist.api.model.PatientInformationField;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Patient information loader class.
 */
public class PatientInformation {
	public static final String PATIENT_PREFIX = "p";
	public static final String VISIT_PREFIX = "v";
	public static final String ATTRIBUTE_PREFIX = "attr";

	protected final Log LOG = LogFactory.getLog(this.getClass());

	private Map<String, PatientInformationField<?>> fields = new HashMap<String, PatientInformationField<?>>();

	private PatientInformation() {}

	private static class Holder {
		private static final PatientInformation INSTANCE = new PatientInformation().refresh();
	}

	public static PatientInformation getInstance() {
		return Holder.INSTANCE;
	}

	public PatientInformation refresh() {
		loadFields();

		return this;
	}

	public Map<String, PatientInformationField<?>> getFields() {
		return Collections.unmodifiableMap(fields);
	}

	public PatientInformationField<?> getField(String key) {
		return fields.get(key);
	}

	private void loadFields() {
		Map<String, PatientInformationField<?>> tempFields = new HashMap<String, PatientInformationField<?>>();

		addField(tempFields, PATIENT_PREFIX, "gender", boolean.class, new Func1<Patient, Object>() {
			@Override
			public Object apply(Patient patient) {
				return patient.getGender();
			}
		});
		addField(tempFields, PATIENT_PREFIX, "givenName", String.class, new Func1<Patient, Object>() {
			@Override
			public Object apply(Patient patient) {
				return patient.getGivenName();
			}
		});
		addField(tempFields, PATIENT_PREFIX, "familyName", String.class, new Func1<Patient, Object>() {
			@Override
			public Object apply(Patient patient) {
				return patient.getFamilyName();
			}
		});
		addField(tempFields, PATIENT_PREFIX, "fullName", String.class, new Func1<Patient, Object>() {
			@Override
			public Object apply(Patient patient) {
				return (patient.getGivenName() + " " + patient.getFamilyName()).trim();
			}
		});
		// And so on for each patient field

		List<PersonAttributeType> personAttributeTypes = Context.getPersonService().getAllPersonAttributeTypes();
		for (PersonAttributeType attributeType : personAttributeTypes) {
			addPatientAttributeField(tempFields, "p." + ATTRIBUTE_PREFIX, attributeType);
		}

		addField(tempFields, VISIT_PREFIX, "startDate", Date.class, new Func1<Visit, Object>() {
			@Override
			public Object apply(Visit visit) {
				return visit.getStartDatetime();
			}
		});
		addField(tempFields, VISIT_PREFIX, "endDate", Date.class, new Func1<Visit, Object>() {
			@Override
			public Object apply(Visit visit) {
				return visit.getStopDatetime();
			}
		});
		// And so on for each visit field

		List<VisitAttributeType> visitAttributeTypes = Context.getVisitService().getAllVisitAttributeTypes();
		for (VisitAttributeType attributeType : visitAttributeTypes) {
			addAttributeField(tempFields, VISIT_PREFIX + "." + ATTRIBUTE_PREFIX, attributeType);
		}

		fields = tempFields;
	}

	private <T extends OpenmrsData> void addField(Map<String, PatientInformationField<?>> map, String prefix, String name,
	        Class<?> dataType, Func1<T, Object> getValueFunc) {
		PatientInformationField field = new PatientInformationField<T>(prefix, name, dataType, getValueFunc);

		map.put(prefix + "." + name, field);
	}

	private <T extends OpenmrsData> void addPatientAttributeField(Map<String, PatientInformationField<?>> map,
	        String prefix,
	        final PersonAttributeType attributeType) {
		Class<?> cls = null;
		try {
			cls = Class.forName(attributeType.getFormat());
		} catch (ClassNotFoundException cnfe) {
			LOG.warn("Could not convert person attribute type '" + attributeType.getName() + "' format ("
			        + attributeType.getFormat() + ") to a class.");
		}

		if (cls != null) {
			addField(map, prefix, attributeType.getName(), cls, new Func1<Patient, Object>() {
				@Override
				public Object apply(Patient patient) {
					PersonAttribute attribute = patient.getAttribute(attributeType);
					if (attribute == null) {
						return null;
					} else {
						return attribute.getHydratedObject();
					}
				}
			});
		}
	}

	private <T extends OpenmrsData & Customizable<?>> void addAttributeField(Map<String, PatientInformationField<?>> map,
	        String prefix, final AttributeType<T> attributeType) {
		Class<?> cls = null;
		try {
			cls = Class.forName(attributeType.getDatatypeClassname());
		} catch (ClassNotFoundException cnfe) {
			LOG.warn("Could not convert attribute type '" + attributeType.getName() + "' datatype ("
			        + attributeType.getDatatypeClassname() + ") to a class.");
		}

		if (cls != null) {
			addField(map, prefix, attributeType.getName(), cls, new Func1<T, Object>() {
				@Override
				public Object apply(T source) {
					Attribute<?, T> foundAttribute = null;
					Collection<Attribute<?, T>> attributes = (Collection<Attribute<?, T>>)source.getAttributes();
					for (Attribute<?, T> attribute : attributes) {
						if (attribute.getAttributeType().getId() == attributeType.getId()) {
							foundAttribute = attribute;
							break;
						}
					}

					if (foundAttribute == null) {
						return null;
					} else {
						return foundAttribute.getValue();
					}
				}
			});
		}
	}
}
