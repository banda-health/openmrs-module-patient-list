package org.openmrs.module.patientlist.api;

import org.openmrs.module.openhmis.commons.api.entity.IMetadataDataService;
import org.openmrs.module.patientlist.api.model.PatientListOrder;
import org.springframework.transaction.annotation.Transactional;

/**
 * Interface that represents classes which perform data operations for {@link PatientListOrder}s.
 */
@Transactional
public interface IPatientListOrderService extends IMetadataDataService<PatientListOrder> {}
