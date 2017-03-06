/*
 * The contents of this file are subject to the OpenMRS Public License
 * Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See
 * the License for the specific language governing rights and
 * limitations under the License.
 *
 * Copyright (C) OpenHMIS.  All Rights Reserved.
 */
package org.openmrs.module.patientlist.api.util;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Visit;
import org.openmrs.PersonAttributeType;
import org.openmrs.Encounter;
import org.openmrs.Patient;
import org.openmrs.OpenmrsData;
import org.openmrs.Concept;
import org.openmrs.VisitAttributeType;
import org.openmrs.ConceptAnswer;
import org.openmrs.PersonAttribute;
import org.openmrs.Obs;

import org.openmrs.api.ConceptService;
import org.openmrs.api.context.Context;
import org.openmrs.attribute.Attribute;
import org.openmrs.attribute.AttributeType;
import org.openmrs.customdatatype.Customizable;
import org.openmrs.module.openhmis.commons.api.f.Func1;
import org.openmrs.module.patientlist.api.model.AbstractPatientListField;
import org.openmrs.module.patientlist.api.model.PatientInformationField;
import org.openmrs.module.patientlist.api.model.custom.AgePatientInformationField;
import org.openmrs.module.patientlist.api.model.custom.BirthDatePatientInformationField;
import org.openmrs.module.patientlist.api.model.custom.DiagnosisPatientInformationField;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import java.util.Date;
import java.util.Collections;
import java.util.Collection;
import java.util.HashMap;
import java.util.Set;

/**
 * Patient information loader class.
 */
@Component
public class PatientInformation {
	public static final String PATIENT_PREFIX = "p";
	public static final String VISIT_PREFIX = "v";
	public static final String ATTRIBUTE_PREFIX = "attr";
	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm a");

	protected final Log LOG = LogFactory.getLog(this.getClass());

	private Map<String, IPatientInformationField<?>> fields =
	        new HashMap<String, IPatientInformationField<?>>();

	private ConceptService conceptService;

	private PatientInformation() {}

	public static PatientInformation getInstance() {
		return Holder.INSTANCE;
	}

	public PatientInformation refresh() {
		loadFields();

		return this;
	}

	public Map<String, IPatientInformationField<?>> getFields() {
		return Collections.unmodifiableMap(fields);
	}

	public IPatientInformationField<?> getField(String key) {
		return fields.get(key);
	}

	private void loadFields() {
		Map<String, IPatientInformationField<?>> tempFields = new HashMap<String, IPatientInformationField<?>>();

		addCustomField(tempFields,
		    new BirthDatePatientInformationField(
		            PATIENT_PREFIX, "birthdate", Date.class, new Func1<Patient, Object>() {
			            @Override
			            public Object apply(Patient patient) {
				            return patient.getGender();
			            }
		            }, PATIENT_PREFIX + ".birthdate"));

		addField(tempFields, PATIENT_PREFIX, "gender", boolean.class, new Func1<Patient, Object>() {
			@Override
			public Object apply(Patient patient) {
				return patient.getGender();
			}
		}, PATIENT_PREFIX + ".gender");

		addCustomField(tempFields, new AgePatientInformationField(
		        PATIENT_PREFIX, "age", Integer.class,
		        new Func1<Patient, Object>() {
			        @Override
			        public Object apply(Patient patient) {
				        return patient.getAge();
			        }
		        },
		        PATIENT_PREFIX + ".age"));

		addField(tempFields, PATIENT_PREFIX, "givenName", String.class, new Func1<Patient, Object>() {
			@Override
			public Object apply(Patient patient) {
				return patient.getGivenName();
			}
		}, PATIENT_PREFIX + ".names.givenName");

		addField(tempFields, PATIENT_PREFIX, "middleName", String.class, new Func1<Patient, Object>() {
			@Override
			public Object apply(Patient patient) {
				return patient.getMiddleName();
			}
		}, PATIENT_PREFIX + ".names.middleName");

		addField(tempFields, PATIENT_PREFIX, "familyName", String.class, new Func1<Patient, Object>() {
			@Override
			public Object apply(Patient patient) {
				return patient.getFamilyName();
			}
		}, PATIENT_PREFIX + ".names.familyName");

		addField(tempFields, PATIENT_PREFIX, "fullName", String.class, new Func1<Patient, Object>() {
			@Override
			public Object apply(Patient patient) {
				return (patient.getGivenName() + " " + patient.getFamilyName()).trim();
			}
		}, PATIENT_PREFIX + ".names.fullName");

		addField(tempFields, PATIENT_PREFIX, "identifier", String.class, new Func1<Patient, Object>() {
			@Override
			public Object apply(Patient patient) {
				String identifier = "";
				if (patient.getPatientIdentifier() != null) {
					identifier = patient.getPatientIdentifier().toString();
				}

				return identifier;
			}
		}, PATIENT_PREFIX + ".identifiers.identifier");

		addField(tempFields, PATIENT_PREFIX, "hasActiveVisit", String.class, new Func1<Visit, Object>() {
			@Override
			public Object apply(Visit visit) {
				return visit.getStartDatetime() != null && visit.getStopDatetime() == null;
			}
		}, null);

		// And so on for each patient field

		List<PersonAttributeType> personAttributeTypes = Context.getPersonService().getAllPersonAttributeTypes();
		for (PersonAttributeType attributeType : personAttributeTypes) {
			addPatientAttributeField(tempFields, PATIENT_PREFIX + "." + ATTRIBUTE_PREFIX, attributeType, null);
		}

		addField(tempFields, VISIT_PREFIX, "startDate", Date.class, new Func1<Visit, Object>() {
			@Override
			public Object apply(Visit visit) {
				Date startDate = visit.getStartDatetime();
				if (startDate != null) {
					return sdf.format(startDate);
				}

				return null;
			}
		}, VISIT_PREFIX + ".startDatetime");

		addField(tempFields, VISIT_PREFIX, "endDate", Date.class, new Func1<Visit, Object>() {
			@Override
			public Object apply(Visit visit) {
				return visit.getStopDatetime();
			}
		}, VISIT_PREFIX + ".stopDatetime");

		addField(tempFields, VISIT_PREFIX, "visitType", String.class, new Func1<Visit, Object>() {
			@Override
			public Object apply(Visit visit) {
				return visit.getVisitType().getName();
			}
		}, VISIT_PREFIX + ".visitType.name");

		addCustomField(tempFields,
		    new DiagnosisPatientInformationField(
		            VISIT_PREFIX, "diagnosis", String.class, new Func1<Visit, Object>() {
			            @Override
			            public Object apply(Visit visit) {
				            String diagnosis = "";
				            Set<Encounter> encounters = visit.getEncounters();
				            if (encounters != null) {
					            for (Encounter encounter : encounters) {
						            if (encounter != null) {
							            Set<Obs> obs = encounter.getAllObs(false);
							            for (Obs observation : obs) {
								            if (observation != null) {
									            if (observation.getValueCoded() != null) {
										            diagnosis += observation.getValueCoded().getDisplayString() + ",";
									            } else if (StringUtils.isNotEmpty(observation.getValueText())) {
										            diagnosis += observation.getValueText() + ",";
									            }
								            }
							            }
						            }
					            }
				            }

				            return StringUtils.removeEnd(diagnosis, ",");
			            }
		            }, VISIT_PREFIX + ".diagnosis"));

		addField(tempFields, VISIT_PREFIX, "hasDiagnosis", String.class, new Func1<Visit, Object>() {
			@Override
			public Object apply(Visit visit) {
				boolean hasDiagnosis = false;
				Set<Encounter> encounters = visit.getEncounters();
				if (encounters != null) {
					for (Encounter encounter : encounters) {
						if (encounter != null) {
							Set<Obs> obs = encounter.getAllObs(false);
							for (Obs observation : obs) {
								if (observation != null) {
									if (observation.getValueCoded() != null) {
										hasDiagnosis = true;
									} else if (StringUtils.isNotEmpty(observation.getValueText())) {
										hasDiagnosis = true;
									}
								}
							}
						}
					}
				}

				return hasDiagnosis;
			}
		}, null);

		// And so on for each visit field

		List<VisitAttributeType> visitAttributeTypes = Context.getVisitService().getAllVisitAttributeTypes();
		for (VisitAttributeType attributeType : visitAttributeTypes) {
			addAttributeField(tempFields, VISIT_PREFIX + "." + ATTRIBUTE_PREFIX, attributeType, null);
		}

		fields = tempFields;
	}

	private <T extends OpenmrsData> void addField(
	        Map<String, IPatientInformationField<?>> map, String prefix, String name,
	        Class<?> dataType, Func1<T, Object> getValueFunc, String mappingFieldName) {
		PatientInformationField field = new PatientInformationField(
		        prefix, name, dataType, getValueFunc, mappingFieldName);

		map.put(prefix + "." + name, field);
	}

	private <T extends AbstractPatientListField> void addCustomField(
	        Map<String, IPatientInformationField<?>> map, T instance) {
		map.put(instance.getPrefix() + "." + instance.getName(), instance);
	}

	private <T extends OpenmrsData> void addPatientAttributeField(
	        Map<String, IPatientInformationField<?>> map, String prefix,
	        final PersonAttributeType attributeType, String mappingFieldName) {
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
			}, mappingFieldName);
		}
	}

	private <T extends OpenmrsData & Customizable<?>> void addAttributeField(Map<String, IPatientInformationField<?>> map,
	        String prefix, final AttributeType<T> attributeType, String mappingFieldName) {
		Class<?> cls = null;
		try {
			cls = Class.forName(attributeType.getDatatypeClassname());
		} catch (ClassNotFoundException cnfe) {
			LOG.warn("Could not convert attribute type '" + attributeType.getName() + "' datatype ("
			        + attributeType.getDatatypeClassname() + ") to a class.");
		}

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
					Object result = foundAttribute.getValue();
					if (result == null && foundAttribute.getValueReference() != null) {
						return retrieveValueFromReference(foundAttribute);
					}

					return result;
				}
			}
		}, mappingFieldName);
	}

	/**
	 * Retrieve the answer concept name for the given uuid (value_reference)
	 * @param attribute
	 * @return
	 */
	private Object retrieveValueFromReference(Attribute attribute) {
		if (conceptService == null) {
			conceptService = Context.getConceptService();
		}

		Concept concept = conceptService.getConcept(
		        attribute.getAttributeType().getDatatypeConfig());

		if (concept != null) {
			for (ConceptAnswer conceptAnswer : concept.getAnswers()) {
				if (conceptAnswer.getUuid().equalsIgnoreCase(attribute.getValueReference())) {
					Concept answerConcept = conceptAnswer.getAnswerConcept();
					return answerConcept.getName().getName();
				}
			}
		}

		return null;
	}

	private static class Holder {
		private static final PatientInformation INSTANCE = new PatientInformation().refresh();
	}
}
