package org.openmrs.module.patientlist.api.impl;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.api.context.Context;
import org.openmrs.logic.op.Operator;
import org.openmrs.module.openhmis.commons.api.entity.IMetadataDataServiceTest;
import org.openmrs.module.patientlist.api.IPatientListDataService;
import org.openmrs.module.patientlist.api.IPatientListDataServiceTest;
import org.openmrs.module.patientlist.api.TestConstants;
import org.openmrs.module.patientlist.api.model.PatientList;
import org.openmrs.module.patientlist.api.model.PatientListCondition;
import org.openmrs.module.patientlist.api.model.PatientListOrder;

import java.util.ArrayList;
import java.util.List;

public class PatientListDataServiceImplTest extends IPatientListDataServiceTest {

	private IPatientListDataService patientListDataService;
	private IPatientListDataServiceTest patientListDataServiceTest;

	@Before
	public void before() throws Exception {
		super.before();

		patientListDataService = createService();
		patientListDataServiceTest = new IPatientListDataServiceTest();
	}

	@Override
	protected IPatientListDataService createService() {
		return Context.getService(IPatientListDataService.class);
	}

	@Test
	public void patientList_shouldCreateSingleSelectionRuleWithoutSortOrder() throws Exception {
		PatientList patientList = patientListDataServiceTest.createEntity(true);

		PatientListCondition selectionRule = new PatientListCondition();
		selectionRule.setField("p.age");
		selectionRule.setOperator(Operator.GT.toString());
		selectionRule.setValue("20");
		selectionRule.setConditionOrder(1);

		patientList.addSelectionRule(selectionRule);

		patientListDataService.save(patientList);
		Context.flushSession();

		Assert.assertNotNull(patientList);
		Assert.assertEquals("p.age", patientList.getPatientListConditions().get(0).getField());
	}

	@Test
	public void patientList_shouldCreateMultipleSelectionRulesWithoutSortOrder() throws Exception {
		PatientList patientList = patientListDataServiceTest.createEntity(true);

		PatientListCondition selectionRule = new PatientListCondition();
		selectionRule.setField("p.age");
		selectionRule.setOperator(Operator.GT.toString());
		selectionRule.setValue("20");
		selectionRule.setConditionOrder(1);

		patientList.addSelectionRule(selectionRule);

		selectionRule = new PatientListCondition();
		selectionRule.setField("p.gender");
		selectionRule.setOperator(Operator.EQUALS.toString());
		selectionRule.setValue("M");
		selectionRule.setConditionOrder(2);

		patientList.addSelectionRule(selectionRule);

		patientListDataService.save(patientList);
		Context.flushSession();

		Assert.assertNotNull(patientList);
		Assert.assertEquals("p.gender", patientList.getPatientListConditions().get(1).getField());
	}

	@Test
	public void patientList_shouldCreateSingleSelectionRuleWithSortOrder() throws Exception {
		PatientList patientList = patientListDataServiceTest.createEntity(true);

		PatientListCondition selectionRule = new PatientListCondition();
		selectionRule.setField("p.age");
		selectionRule.setOperator(Operator.GT.toString());
		selectionRule.setValue("20");
		selectionRule.setConditionOrder(1);

		patientList.addSelectionRule(selectionRule);

		PatientListOrder order = new PatientListOrder();
		order.setField("p.name");
		order.setSortOrder("asc");
		order.setOrder(1);

		patientList.addSortOrder(order);

		patientListDataService.save(patientList);
		Context.flushSession();

		Assert.assertNotNull(patientList);
		Assert.assertEquals("asc", patientList.getPatientListOrders().get(0).getSortOrder());
	}

	@Test
	public void patientList_shouldCreateSingleSelectionRuleWithMultipleSortOrders() throws Exception {
		PatientList patientList = patientListDataServiceTest.createEntity(true);

		PatientListCondition selectionRule = new PatientListCondition();
		selectionRule.setField("p.age");
		selectionRule.setOperator(Operator.GT.toString());
		selectionRule.setValue("20");
		selectionRule.setConditionOrder(1);

		patientList.addSelectionRule(selectionRule);

		PatientListOrder order = new PatientListOrder();
		order.setField("p.name");
		order.setSortOrder("asc");
		order.setOrder(1);

		patientList.addSortOrder(order);

		order = new PatientListOrder();
		order.setField("p.id");
		order.setSortOrder("asc");
		order.setOrder(2);

		patientList.addSortOrder(order);

		patientListDataService.save(patientList);
		Context.flushSession();

		Assert.assertNotNull(patientList);
		Assert.assertEquals("p.id", patientList.getPatientListOrders().get(1).getField());
	}
}