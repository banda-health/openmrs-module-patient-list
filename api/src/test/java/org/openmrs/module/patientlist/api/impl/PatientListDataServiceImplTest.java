package org.openmrs.module.patientlist.api.impl;

import org.junit.*;
import org.openmrs.api.PatientService;
import org.openmrs.api.VisitService;
import org.openmrs.api.context.Context;
import org.openmrs.module.openhmis.commons.api.PagingInfo;
import org.openmrs.module.patientlist.api.IPatientListDataService;
import org.openmrs.module.patientlist.api.IPatientListDataServiceTest;
import org.openmrs.module.patientlist.api.IPatientListService;
import org.openmrs.module.patientlist.api.model.*;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.agent.PowerMockAgent;
import org.powermock.modules.junit4.rule.PowerMockRule;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

@PrepareForTest(Calendar.class)
public class PatientListDataServiceImplTest extends IPatientListDataServiceTest {

	@Rule
	public PowerMockRule rule = new PowerMockRule();

	@BeforeClass
	public static void beforeClass() throws Exception {
		PowerMockAgent.initializeIfNeeded();
	}

	private IPatientListService patientListService;
	private IPatientListDataService patientListDataService;
	private IPatientListDataServiceTest patientListDataServiceTest;
	private PatientService patientService;
	private VisitService visitService;
	private Calendar calendar;

	@Before
	public void before() throws Exception {
		super.before();

		patientListService = createService();
		patientListDataServiceTest = new IPatientListDataServiceTest();
		patientListDataService = Context.getService(IPatientListDataService.class);
		patientService = Context.getPatientService();
		visitService = Context.getVisitService();

		mockStatic(Calendar.class);
		calendar = mock(Calendar.class);
		when(Calendar.getInstance()).thenReturn(calendar);
	}

	@Override
	protected IPatientListService createService() {
		return Context.getService(IPatientListService.class);
	}

	@Test
	public void patientList_shouldCreateSingleSelectionRuleWithoutSortOrder() throws Exception {
		PatientList patientList = patientListDataServiceTest.createEntity(true);

		PatientListCondition selectionRule = new PatientListCondition();
		selectionRule.setField("p.age");
		selectionRule.setOperator(PatientListOperator.GT);
		selectionRule.setValue("20");
		selectionRule.setConditionOrder(1);

		patientList.addCondition(selectionRule);

		patientListService.save(patientList);
		Context.flushSession();

		Assert.assertNotNull(patientList);
		Assert.assertEquals("p.age", patientList.getPatientListConditions().get(0).getField());
	}

	@Test
	public void patientList_shouldCreateMultipleSelectionRulesWithoutSortOrder() throws Exception {
		PatientList patientList = patientListDataServiceTest.createEntity(true);

		PatientListCondition selectionRule = new PatientListCondition();
		selectionRule.setField("p.age");
		selectionRule.setOperator(PatientListOperator.GT);
		selectionRule.setValue("20");
		selectionRule.setConditionOrder(1);

		patientList.addCondition(selectionRule);

		selectionRule = new PatientListCondition();
		selectionRule.setField("p.gender");
		selectionRule.setOperator(PatientListOperator.EQUALS);
		selectionRule.setValue("M");
		selectionRule.setConditionOrder(2);

		patientList.addCondition(selectionRule);

		patientListService.save(patientList);
		Context.flushSession();

		Assert.assertNotNull(patientList);
		Assert.assertEquals("p.gender", patientList.getPatientListConditions().get(1).getField());
	}

	@Test
	public void patientList_shouldCreateSingleSelectionRuleWithSortOrder() throws Exception {
		PatientList patientList = patientListDataServiceTest.createEntity(true);

		PatientListCondition selectionRule = new PatientListCondition();
		selectionRule.setField("p.age");
		selectionRule.setOperator(PatientListOperator.GT);
		selectionRule.setValue("20");
		selectionRule.setConditionOrder(1);

		patientList.addCondition(selectionRule);

		PatientListOrder order = new PatientListOrder();
		order.setField("p.name");
		order.setSortOrder("asc");
		order.setConditionOrder(1);

		patientList.addSortOrder(order);

		patientListService.save(patientList);
		Context.flushSession();

		Assert.assertNotNull(patientList);
		Assert.assertEquals("asc", patientList.getOrdering().get(0).getSortOrder());
	}

	@Test
	public void patientList_shouldCreateSingleSelectionRuleWithMultipleSortOrders() throws Exception {
		PatientList patientList = patientListDataServiceTest.createEntity(true);

		PatientListCondition selectionRule = new PatientListCondition();
		selectionRule.setField("p.age");
		selectionRule.setOperator(PatientListOperator.GT);
		selectionRule.setValue("20");
		selectionRule.setConditionOrder(1);

		patientList.addCondition(selectionRule);

		PatientListOrder order = new PatientListOrder();
		order.setField("p.name");
		order.setSortOrder("asc");
		order.setConditionOrder(1);

		patientList.addSortOrder(order);

		order = new PatientListOrder();
		order.setField("p.id");
		order.setSortOrder("asc");
		order.setConditionOrder(2);

		patientList.addSortOrder(order);

		patientListService.save(patientList);
		Context.flushSession();

		Assert.assertNotNull(patientList);
		Assert.assertEquals("p.id", patientList.getOrdering().get(1).getField());
	}

	@Test
	public void patientList_shouldCreateListWithSinglePatientDetail() throws Exception {
		PatientList patientList = patientListService.getById(0);

		List<PatientListCondition> conditions = patientList.getPatientListConditions();
		PatientListCondition givenNameCondition = conditions.get(0);

		Assert.assertEquals("p.givenName", givenNameCondition.getField());

		patientList.getPatientListConditions().clear();
		patientList.getPatientListConditions().add(givenNameCondition);

		PagingInfo pagingInfo = new PagingInfo();
		List<PatientListData> patientListDataSet = patientListDataService.getPatientListData(patientList, pagingInfo);

		Assert.assertNotNull(patientListDataSet);
		Assert.assertEquals(2, patientListDataSet.size());
	}

	@Test
	public void patientList_shouldCreateListWithMultiplePatientDetails() throws Exception {
		PatientList patientList = patientListService.getById(0);

		List<PatientListCondition> conditions = patientList.getPatientListConditions();
		PatientListCondition givenNameCondition = conditions.get(0);
		PatientListCondition ageCondition = conditions.get(8);

		Assert.assertEquals("p.givenName", givenNameCondition.getField());
		Assert.assertEquals("p.age", ageCondition.getField());

		patientList.getPatientListConditions().clear();
		patientList.getPatientListConditions().add(givenNameCondition);
		patientList.getPatientListConditions().add(ageCondition);

		PagingInfo pagingInfo = new PagingInfo();
		List<PatientListData> patientListDataSet = patientListDataService.getPatientListData(patientList, pagingInfo);

		Assert.assertNotNull(patientListDataSet);
		Assert.assertEquals(1, patientListDataSet.size());
	}

	@Test
	public void patientList_shouldCreateListWithSinglePersonAttribute() throws Exception {
		PatientList patientList = patientListService.getById(0);

		List<PatientListCondition> conditions = patientList.getPatientListConditions();
		PatientListCondition condition = conditions.get(9);

		Assert.assertEquals("p.attr.Phone", condition.getField());

		patientList.getPatientListConditions().clear();
		patientList.getPatientListConditions().add(condition);

		PagingInfo pagingInfo = new PagingInfo();
		List<PatientListData> patientListDataSet = patientListDataService.getPatientListData(patientList, pagingInfo);

		Assert.assertNotNull(patientListDataSet);
		Assert.assertEquals(1, patientListDataSet.size());
	}

	@Test
	public void patientList_shouldCreateListWithMultiplePersonAttributes() throws Exception {
		PatientList patientList = patientListService.getById(0);

		List<PatientListCondition> conditions = patientList.getPatientListConditions();
		PatientListCondition condition = conditions.get(10);
		Assert.assertEquals("p.attr.State", condition.getField());

		PatientListCondition condition2 = conditions.get(11);
		Assert.assertEquals("p.attr.City", condition2.getField());

		patientList.getPatientListConditions().clear();
		patientList.getPatientListConditions().add(condition);
		patientList.getPatientListConditions().add(condition2);

		Assert.assertEquals(2, patientList.getPatientListConditions().size());

		PagingInfo pagingInfo = new PagingInfo();
		List<PatientListData> patientListDataSet =
		        patientListDataService.getPatientListData(patientList, pagingInfo);

		Assert.assertNotNull(patientListDataSet);
		Assert.assertEquals(3, patientListDataSet.size());
	}

	@Test
	public void patientList_shouldCreateListWithSingleVisitDetail() throws Exception {
		PatientList patientList = patientListService.getById(0);

		List<PatientListCondition> conditions = patientList.getPatientListConditions();
		PatientListCondition condition = conditions.get(12);

		Assert.assertEquals("v.startDate", condition.getField());

		patientList.getPatientListConditions().clear();
		patientList.getPatientListConditions().add(condition);

		PagingInfo pagingInfo = new PagingInfo();
		List<PatientListData> patientListDataSet = patientListDataService.getPatientListData(patientList, pagingInfo);

		Assert.assertNotNull(patientListDataSet);
		Assert.assertEquals(5, patientListDataSet.size());
	}

	@Test
	public void patientList_shouldCreateListWithSingleVisitAttribute() throws Exception {
		PatientList patientList = patientListService.getById(0);

		List<PatientListCondition> conditions = patientList.getPatientListConditions();
		PatientListCondition condition = conditions.get(13);
		Assert.assertEquals("v.attr.First Visit", condition.getField());

		patientList.getPatientListConditions().clear();
		patientList.getPatientListConditions().add(condition);

		PagingInfo pagingInfo = new PagingInfo();
		List<PatientListData> patientListDataSet = patientListDataService.getPatientListData(patientList, pagingInfo);

		Assert.assertNotNull(patientListDataSet);
		Assert.assertEquals(1, patientListDataSet.size());
	}

	@Test
	public void patientList_shouldCreateListWithMultipleVisitAttributes() throws Exception {
		PatientList patientList = patientListService.getById(0);

		List<PatientListCondition> conditions = patientList.getPatientListConditions();
		PatientListCondition condition = conditions.get(13);
		Assert.assertEquals("v.attr.First Visit", condition.getField());

		PatientListCondition condition2 = conditions.get(14);
		Assert.assertEquals("v.attr.Admission Date", condition2.getField());

		patientList.getPatientListConditions().clear();
		patientList.getPatientListConditions().add(condition);
		patientList.getPatientListConditions().add(condition2);

		PagingInfo pagingInfo = new PagingInfo();
		List<PatientListData> patientListDataSet = patientListDataService.getPatientListData(patientList, pagingInfo);

		Assert.assertNotNull(patientListDataSet);
		Assert.assertEquals(2, patientListDataSet.size());
	}

	@Test
	public void patientList_shouldCreatePatientListWithSingleConditionSingleSortOrder() throws Exception {
		PatientList patientList = patientListService.getById(0);

		List<PatientListCondition> conditions = patientList.getPatientListConditions();
		PatientListCondition condition = conditions.get(15);
		Assert.assertEquals("v.attr.First Visit", condition.getField());

		patientList.getPatientListConditions().clear();
		patientList.getPatientListConditions().add(condition);

		PatientListOrder order = new PatientListOrder();
		order.setId(1);
		order.setSortOrder("asc");
		order.setField("v.startDate");
		order.setPatientList(patientList);
		patientList.getOrdering().add(order);

		PagingInfo pagingInfo = new PagingInfo();
		List<PatientListData> patientListDataSet = patientListDataService.getPatientListData(patientList, pagingInfo);

		Assert.assertNotNull(patientListDataSet);
		Assert.assertEquals(2, patientListDataSet.size());
		String startDate =
		        new SimpleDateFormat("yyyy-MM-dd").format(patientListDataSet.get(0).getVisit().getStartDatetime());
		Assert.assertEquals("2016-06-27", startDate);

		// order by desc
		patientList.getOrdering().get(0).setSortOrder("desc");
		patientListDataSet = patientListDataService.getPatientListData(patientList, pagingInfo);

		Assert.assertNotNull(patientListDataSet);
		Assert.assertEquals(2, patientListDataSet.size());
		startDate =
		        new SimpleDateFormat("yyyy-MM-dd").format(patientListDataSet.get(0).getVisit().getStartDatetime());
		Assert.assertEquals("2016-11-01", startDate);
	}

	@Test
	public void patientList_shouldCreatePatientListWithMultipleConditionsSingleSortOrder() throws Exception {
		PatientList patientList = patientListService.getById(0);

		List<PatientListCondition> conditions = patientList.getPatientListConditions();
		PatientListCondition givenNameCondition = conditions.get(0);
		PatientListCondition ageCondition = conditions.get(16);

		Assert.assertEquals("p.givenName", givenNameCondition.getField());
		Assert.assertEquals("p.age", ageCondition.getField());

		patientList.getPatientListConditions().clear();
		patientList.getPatientListConditions().add(givenNameCondition);
		patientList.getPatientListConditions().add(ageCondition);

		PatientListOrder order = new PatientListOrder();
		order.setId(1);
		order.setSortOrder("asc");
		order.setField("p.age");
		order.setPatientList(patientList);
		patientList.getOrdering().add(order);

		PagingInfo pagingInfo = new PagingInfo();
		List<PatientListData> patientListDataSet = patientListDataService.getPatientListData(patientList, pagingInfo);

		Assert.assertNotNull(patientListDataSet);
		Assert.assertEquals(2, patientListDataSet.size());

		Assert.assertEquals("46", patientListDataSet.get(0).getPatient().getAge().toString());

		// change ordering
		patientList.getOrdering().get(0).setSortOrder("desc");
		patientListDataSet = patientListDataService.getPatientListData(patientList, pagingInfo);

		Assert.assertNotNull(patientListDataSet);
		Assert.assertEquals(2, patientListDataSet.size());

		Assert.assertEquals("66", patientListDataSet.get(0).getPatient().getAge().toString());

	}

	@Test
	public void patientList_shouldCreatePatientListWithSingleConditionMultipleSortOrder() throws Exception {
		PatientList patientList = patientListService.getById(0);

		List<PatientListCondition> conditions = patientList.getPatientListConditions();
		PatientListCondition condition = conditions.get(12);

		Assert.assertEquals("v.startDate", condition.getField());

		patientList.getPatientListConditions().clear();
		patientList.getPatientListConditions().add(condition);

		PatientListOrder order = new PatientListOrder();
		order.setId(1);
		order.setSortOrder("desc");
		order.setField("p.age");
		order.setPatientList(patientList);
		patientList.getOrdering().add(order);

		order = new PatientListOrder();
		order.setId(2);
		order.setSortOrder("desc");
		order.setField("p.identifier");
		order.setPatientList(patientList);
		patientList.getOrdering().add(order);

		PagingInfo pagingInfo = new PagingInfo();
		List<PatientListData> patientListDataSet = patientListDataService.getPatientListData(patientList, pagingInfo);

		Assert.assertNotNull(patientListDataSet);
		Assert.assertEquals(3, patientListDataSet.size());

		Assert.assertEquals("66", patientListDataSet.get(0).getPatient().getAge().toString());

		patientList.getOrdering().get(1).setSortOrder("asc");
		patientListDataSet = patientListDataService.getPatientListData(patientList, pagingInfo);

		Assert.assertNotNull(patientListDataSet);
		Assert.assertEquals(3, patientListDataSet.size());

		Assert.assertEquals("46", patientListDataSet.get(0).getPatient().getAge().toString());

	}

	@Test
	public void patientList_shouldCreatePatientListWithMultipleConditionsMultipleSortOrder() throws Exception {
		PatientList patientList = patientListService.getById(0);

		List<PatientListCondition> conditions = patientList.getPatientListConditions();
		PatientListCondition givenNameCondition = conditions.get(0);
		PatientListCondition ageCondition = conditions.get(16);

		Assert.assertEquals("p.givenName", givenNameCondition.getField());
		Assert.assertEquals("p.age", ageCondition.getField());

		patientList.getPatientListConditions().clear();
		patientList.getPatientListConditions().add(givenNameCondition);
		patientList.getPatientListConditions().add(ageCondition);

		PatientListOrder order = new PatientListOrder();
		order.setId(1);
		order.setSortOrder("desc");
		order.setField("p.age");
		order.setPatientList(patientList);
		patientList.getOrdering().add(order);

		order = new PatientListOrder();
		order.setId(2);
		order.setSortOrder("asc");
		order.setField("p.givenName");
		order.setPatientList(patientList);
		patientList.getOrdering().add(order);

		PagingInfo pagingInfo = new PagingInfo();
		List<PatientListData> patientListDataSet = patientListDataService.getPatientListData(patientList, pagingInfo);

		Assert.assertNotNull(patientListDataSet);
		Assert.assertEquals(2, patientListDataSet.size());

		Assert.assertEquals("66", patientListDataSet.get(0).getPatient().getAge().toString());
		Assert.assertEquals("Mike", patientListDataSet.get(0).getPatient().getGivenName());

		// change ordering
		patientList.getOrdering().get(1).setSortOrder("desc");
		patientListDataSet = patientListDataService.getPatientListData(patientList, pagingInfo);

		Assert.assertNotNull(patientListDataSet);
		Assert.assertEquals(2, patientListDataSet.size());

		Assert.assertEquals("46", patientListDataSet.get(0).getPatient().getAge().toString());
		Assert.assertEquals("Jennifer", patientListDataSet.get(0).getPatient().getGivenName());

	}

	@Test
	public void patientList_shouldCheckActiveVisits() throws Exception {
		PatientList patientList = patientListService.getById(0);

		List<PatientListCondition> conditions = patientList.getPatientListConditions();
		PatientListCondition condition = conditions.get(17);

		Assert.assertEquals("p.hasActiveVisit", condition.getField());

		patientList.getPatientListConditions().clear();
		patientList.getPatientListConditions().add(condition);

		PagingInfo pagingInfo = new PagingInfo();
		List<PatientListData> patientListDataSet = patientListDataService.getPatientListData(patientList, pagingInfo);

		Assert.assertNotNull(patientListDataSet);
		Assert.assertEquals(8, patientListDataSet.size());
	}

	@Test
	public void patientList_shouldSearchCodedDiagnosis() throws Exception {
		PatientList patientList = patientListService.getById(0);

		List<PatientListCondition> conditions = patientList.getPatientListConditions();
		PatientListCondition condition = conditions.get(18);

		Assert.assertEquals("v.diagnosis", condition.getField());

		patientList.getPatientListConditions().clear();
		patientList.getPatientListConditions().add(condition);

		PagingInfo pagingInfo = new PagingInfo();
		List<PatientListData> patientListDataSet = patientListDataService.getPatientListData(patientList, pagingInfo);

		Assert.assertNotNull(patientListDataSet);
		Assert.assertEquals(3, patientListDataSet.size());
	}

	@Test
	public void patientList_shouldSearchNoneCodedDiagnosis() throws Exception {
		PatientList patientList = patientListService.getById(0);

		List<PatientListCondition> conditions = patientList.getPatientListConditions();
		PatientListCondition condition = conditions.get(20);

		Assert.assertEquals("v.diagnosis", condition.getField());

		patientList.getPatientListConditions().clear();
		patientList.getPatientListConditions().add(condition);

		PagingInfo pagingInfo = new PagingInfo();
		List<PatientListData> patientListDataSet = patientListDataService.getPatientListData(patientList, pagingInfo);

		Assert.assertNotNull(patientListDataSet);
		Assert.assertEquals(1, patientListDataSet.size());
	}

	@Test
	public void patientList_shouldCheckHasDiagnosis() throws Exception {
		PatientList patientList = patientListService.getById(0);

		List<PatientListCondition> conditions = patientList.getPatientListConditions();
		PatientListCondition condition = conditions.get(19);

		Assert.assertEquals("v.hasDiagnosis", condition.getField());

		patientList.getPatientListConditions().clear();
		patientList.getPatientListConditions().add(condition);

		PagingInfo pagingInfo = new PagingInfo();
		List<PatientListData> patientListDataSet = patientListDataService.getPatientListData(patientList, pagingInfo);

		Assert.assertNotNull(patientListDataSet);
		Assert.assertEquals(3, patientListDataSet.size());
	}

	@Test
	public void patientList_shouldCheckActiveVisitsHasDiagnosis() throws Exception {
		PatientList patientList = patientListService.getById(0);

		List<PatientListCondition> conditions = patientList.getPatientListConditions();
		PatientListCondition condition = conditions.get(19);
		Assert.assertEquals("v.hasDiagnosis", condition.getField());

		PatientListCondition condition2 = conditions.get(17);
		Assert.assertEquals("p.hasActiveVisit", condition2.getField());

		patientList.getPatientListConditions().clear();
		patientList.getPatientListConditions().add(condition);
		patientList.getPatientListConditions().add(condition2);

		PagingInfo pagingInfo = new PagingInfo();
		List<PatientListData> patientListDataSet = patientListDataService.getPatientListData(patientList, pagingInfo);

		Assert.assertNotNull(patientListDataSet);
		Assert.assertEquals(1, patientListDataSet.size());
	}

	@Test
	public void patientList_shouldCheckActiveVisitsWithADiagnosis() throws Exception {
		PatientList patientList = patientListService.getById(0);

		List<PatientListCondition> conditions = patientList.getPatientListConditions();
		PatientListCondition condition = conditions.get(18);

		Assert.assertEquals("v.diagnosis", condition.getField());

		PatientListCondition condition2 = conditions.get(17);
		Assert.assertEquals("p.hasActiveVisit", condition2.getField());

		patientList.getPatientListConditions().clear();
		patientList.getPatientListConditions().add(condition);
		patientList.getPatientListConditions().add(condition2);

		PagingInfo pagingInfo = new PagingInfo();
		List<PatientListData> patientListDataSet = patientListDataService.getPatientListData(patientList, pagingInfo);

		Assert.assertNotNull(patientListDataSet);
		Assert.assertEquals(1, patientListDataSet.size());
	}

	@Test
	public void patientList_shouldCheckRelativeOperators() throws Exception {
		PatientList patientList = patientListService.getById(0);

		List<PatientListCondition> conditions = patientList.getPatientListConditions();
		PatientListCondition condition = conditions.get(21);

		Assert.assertEquals("RELATIVE", condition.getOperator().toString());

		patientList.getPatientListConditions().clear();
		patientList.getPatientListConditions().add(condition);

		Date date = new Date(2016, 0, 1);
		when(calendar.getTimeInMillis()).thenReturn(date.getTime());

		PagingInfo pagingInfo = new PagingInfo();
		List<PatientListData> patientListDataSet = patientListDataService.getPatientListData(patientList, pagingInfo);

		Assert.assertNotNull(patientListDataSet);
		Assert.assertEquals(3, patientListDataSet.size());

	}

	@Test
	public void patientList_shouldCreatePatientListWithNotEqualOperator() throws Exception {
		PatientList patientList = patientListService.getById(0);

		List<PatientListCondition> conditions = patientList.getPatientListConditions();
		PatientListCondition condition = conditions.get(22);

		Assert.assertEquals("NOT_EQUALS", condition.getOperator().toString());

		patientList.getPatientListConditions().clear();
		patientList.getPatientListConditions().add(condition);

		PagingInfo pagingInfo = new PagingInfo();
		List<PatientListData> patientListDataSet = patientListDataService.getPatientListData(patientList, pagingInfo);

		Assert.assertNotNull(patientListDataSet);
		Assert.assertEquals(6, patientListDataSet.size());
	}

	@Test
	public void patientList_shouldCreatePatientListWithGreaterThanOperator() throws Exception {
		PatientList patientList = patientListService.getById(0);

		List<PatientListCondition> conditions = patientList.getPatientListConditions();
		PatientListCondition condition = conditions.get(23);

		Assert.assertEquals("GT", condition.getOperator().toString());

		patientList.getPatientListConditions().clear();
		patientList.getPatientListConditions().add(condition);

		PagingInfo pagingInfo = new PagingInfo();
		List<PatientListData> patientListDataSet = patientListDataService.getPatientListData(patientList, pagingInfo);

		Assert.assertNotNull(patientListDataSet);
		Assert.assertEquals(3, patientListDataSet.size());

	}

	@Test
	public void patientList_shouldCreatePatientListWithGreaterThanEqualOperator() throws Exception {
		PatientList patientList = patientListService.getById(0);

		List<PatientListCondition> conditions = patientList.getPatientListConditions();
		PatientListCondition condition = conditions.get(16);

		Assert.assertEquals("GTE", condition.getOperator().toString());

		patientList.getPatientListConditions().clear();
		patientList.getPatientListConditions().add(condition);

		PagingInfo pagingInfo = new PagingInfo();
		List<PatientListData> patientListDataSet = patientListDataService.getPatientListData(patientList, pagingInfo);

		Assert.assertNotNull(patientListDataSet);
		Assert.assertEquals(3, patientListDataSet.size());
	}

	@Test
	public void patientList_shouldCreatePatientListWithLesserThanOperator() throws Exception {
		PatientList patientList = patientListService.getById(0);

		List<PatientListCondition> conditions = patientList.getPatientListConditions();
		PatientListCondition condition = conditions.get(25);

		Assert.assertEquals("LT", condition.getOperator().toString());

		patientList.getPatientListConditions().clear();
		patientList.getPatientListConditions().add(condition);

		PagingInfo pagingInfo = new PagingInfo();
		List<PatientListData> patientListDataSet = patientListDataService.getPatientListData(patientList, pagingInfo);

		Assert.assertNotNull(patientListDataSet);
		Assert.assertEquals(2, patientListDataSet.size());
	}

	@Test
	public void patientList_shouldCreatePatientListWithLesserThanEqualOperator() throws Exception {
		PatientList patientList = patientListService.getById(0);

		List<PatientListCondition> conditions = patientList.getPatientListConditions();
		PatientListCondition condition = conditions.get(24);

		Assert.assertEquals("LTE", condition.getOperator().toString());

		patientList.getPatientListConditions().clear();
		patientList.getPatientListConditions().add(condition);

		PagingInfo pagingInfo = new PagingInfo();
		List<PatientListData> patientListDataSet = patientListDataService.getPatientListData(patientList, pagingInfo);

		Assert.assertNotNull(patientListDataSet);
		Assert.assertEquals(0, patientListDataSet.size());
	}

	@Test
	public void patientList_shouldCreatePatientListWithLikeOperator() throws Exception {
		PatientList patientList = patientListService.getById(0);

		List<PatientListCondition> conditions = patientList.getPatientListConditions();
		PatientListCondition condition = conditions.get(0);

		Assert.assertEquals("LIKE", condition.getOperator().toString());

		patientList.getPatientListConditions().clear();
		patientList.getPatientListConditions().add(condition);

		PagingInfo pagingInfo = new PagingInfo();
		List<PatientListData> patientListDataSet = patientListDataService.getPatientListData(patientList, pagingInfo);

		Assert.assertNotNull(patientListDataSet);
		Assert.assertEquals(2, patientListDataSet.size());
	}

	@Test
	public void patientList_shouldCreatePatientListWithBetweenOperator() throws Exception {
		PatientList patientList = patientListService.getById(0);

		List<PatientListCondition> conditions = patientList.getPatientListConditions();
		PatientListCondition condition = conditions.get(6);

		Assert.assertEquals("BETWEEN", condition.getOperator().toString());

		patientList.getPatientListConditions().clear();
		patientList.getPatientListConditions().add(condition);

		PagingInfo pagingInfo = new PagingInfo();
		List<PatientListData> patientListDataSet = patientListDataService.getPatientListData(patientList, pagingInfo);

		Assert.assertNotNull(patientListDataSet);
		Assert.assertEquals(6, patientListDataSet.size());
	}

	@Test
	public void patientList_shouldCreatePatientListWithNullOperator() throws Exception {
		PatientList patientList = patientListService.getById(0);

		List<PatientListCondition> conditions = patientList.getPatientListConditions();
		PatientListCondition condition = conditions.get(26);

		Assert.assertEquals("NULL", condition.getOperator().toString());

		patientList.getPatientListConditions().clear();
		patientList.getPatientListConditions().add(condition);

		PagingInfo pagingInfo = new PagingInfo();
		List<PatientListData> patientListDataSet = patientListDataService.getPatientListData(patientList, pagingInfo);

		Assert.assertNotNull(patientListDataSet);
		Assert.assertEquals(0, patientListDataSet.size()); //check on this doesn't seem right.
	}

	@Test
	public void patientList_shouldCreatePatientListWithNotNullOperator() throws Exception {
		PatientList patientList = patientListService.getById(0);

		List<PatientListCondition> conditions = patientList.getPatientListConditions();
		PatientListCondition condition = conditions.get(27);

		Assert.assertEquals("NOT_NULL", condition.getOperator().toString());

		patientList.getPatientListConditions().clear();
		patientList.getPatientListConditions().add(condition);

		PagingInfo pagingInfo = new PagingInfo();
		List<PatientListData> patientListDataSet = patientListDataService.getPatientListData(patientList, pagingInfo);

		Assert.assertNotNull(patientListDataSet);
		Assert.assertEquals(6, patientListDataSet.size());

	}

	@Test
	public void patientList_shouldCreatePatientListWithDefinedOperator() throws Exception {
		PatientList patientList = patientListService.getById(0);

		List<PatientListCondition> conditions = patientList.getPatientListConditions();
		PatientListCondition condition = conditions.get(28);

		Assert.assertEquals("DEFINED", condition.getOperator().toString());

		patientList.getPatientListConditions().clear();
		patientList.getPatientListConditions().add(condition);

		PagingInfo pagingInfo = new PagingInfo();
		List<PatientListData> patientListDataSet = patientListDataService.getPatientListData(patientList, pagingInfo);

		Assert.assertNotNull(patientListDataSet);
		Assert.assertEquals(3, patientListDataSet.size());

	}

	@Test
	public void patientList_shouldCreatePatientListWithNotDefinedOperator() throws Exception {
		PatientList patientList = patientListService.getById(0);

		List<PatientListCondition> conditions = patientList.getPatientListConditions();
		PatientListCondition condition = conditions.get(29);

		Assert.assertEquals("NOT_DEFINED", condition.getOperator().toString());

		patientList.getPatientListConditions().clear();
		patientList.getPatientListConditions().add(condition);

		PagingInfo pagingInfo = new PagingInfo();
		List<PatientListData> patientListDataSet = patientListDataService.getPatientListData(patientList, pagingInfo);

		Assert.assertNotNull(patientListDataSet);
		Assert.assertEquals(0, patientListDataSet.size());
	}
}
