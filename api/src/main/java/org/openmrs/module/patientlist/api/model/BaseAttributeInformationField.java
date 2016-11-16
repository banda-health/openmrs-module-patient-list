package org.openmrs.module.patientlist.api.model;

import org.openmrs.OpenmrsData;
import org.openmrs.module.openhmis.commons.api.f.Func1;

import java.util.List;

public abstract class BaseAttributeInformationField<T extends OpenmrsData> extends BaseInformationField<T> {
	public static final String ATTRIBUTE_FIELD_NAME = "attr";
	public static final Object ATTRIBUTE_DATA_TYPE = "whatevs";

	protected abstract List<InformationField<T>> loadAttributes();

	protected BaseAttributeInformationField(String prefix) {
		super(prefix, ATTRIBUTE_FIELD_NAME, ATTRIBUTE_DATA_TYPE, new Func1<T, Object>() {
			@Override
			public Object apply(T source) {
				return getAttributeValue(source);
			}
		});
	}

	protected BaseAttributeInformationField(String prefix, String name, Object dataType, Func1<T, Object> getValueFunc) {
		super(prefix, name, dataType, getValueFunc);

		setHasChildFields(true);
	}

	@Override
	public List<InformationField<T>> getChildFields() {
		List<InformationField<T>> attributeFields = super.getChildFields();
		if (attributeFields == null || attributeFields.size() == 0) {
			loadAttributes();
		}

		return super.getChildFields();
	}

	private static Object getAttributeValue(T source) {
		return null;
	}
}
