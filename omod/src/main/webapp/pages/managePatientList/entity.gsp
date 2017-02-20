<script type="text/javascript">
	var breadcrumbs = [
		{icon: "icon-home", link: '/' + OPENMRS_CONTEXT_PATH + '/index.htm'},
		{
			label: "${ ui.message("coreapps.app.systemAdministration.label")}",
			link: '${ui.pageLink("coreapps", "systemadministration/systemAdministration")}'
		},
		{
			label: "${ ui.message("patientlist.manage.label")}",
			link: '/' + OPENMRS_CONTEXT_PATH + '/patientlist/managePatientList/entities.page##/'
		},
		{label: "${ ui.message("patientlist.define.label")}"}
	];
	
	jQuery('#breadcrumbs').html(emr.generateBreadcrumbHtml(breadcrumbs));
	jQuery(function () {
		jQuery('body').on('focus', ".date", function () {
			jQuery(this).datetimepicker({
				minView: 2,
				autoclose: true,
				pickerPosition: "bottom-left",
				todayHighlight: false,
				format: "dd M yyyy"
			});
		});
	});
</script>

<div ng-show="loading" class="loading-msg">
	<span>${ui.message("openhmis.commons.general.processingPage")}</span>
	<br/>
	<span class="loading-img">
		<img src="${ui.resourceLink("uicommons", "images/spinner.gif")}"/>
	</span>
</div>

<form ng-hide="loading" name="entityForm" class="entity-form" ng-class="{'submitted': submitted}" style="font-size:inherit">
	${ui.includeFragment("openhmis.commons", "editEntityHeaderFragment")}
	
	<div class="row">
		<ul class="table-layout">
			<li class="required">
				<span>${ui.message("general.name")}</span>
			</li>
			<li>
				<input name="entityName" type="text" ng-model="entity.name" class="maximized form-control"
				       placeholder="${ui.message("general.name")}" required/>
			</li>
		</ul>
		<ul class="table-layout">
			<li style="vertical-align: top" class="not-required">
				<span>${ui.message("general.description")}</span>
			</li>
			<li>
				<textarea ng-model="entity.description" placeholder="${ui.message("general.description")}" rows="3"
				          cols="40">
				</textarea>
			</li>
		</ul>
	</div>
	<hr/>
	
	<div class="row detail-section-border-top">
		<br/>
		<div class="col-md-12">
			<h3>${ui.message('patientlist.condition.list.header')}</h3>
			<table style="margin-bottom:5px;margin-top: 15px" class="manage-entities-table">
				<thead>
				<tr>
					<th style="width:10% "></th>
					<th style="width:40% ">${ui.message("patientlist.condition.field.label")}</th>
					<th style="width:40% ">${ui.message("patientlist.condition.operator.label")}</th>
					<th>${ui.message("patientlist.condition.value.label")}</th>
				</tr>
				</thead>
				<tbody>
				<tr  ng-repeat="listCondition in listConditions">
					<td style="width:10% ">
						<input type="image" src="/openmrs/images/trash.gif" tabindex="-1" ng-show="listCondition.selected"
						       title="${ui.message('patientlist.list.condition.removeTitle')}"
						       class="remove" ng-click="removeListCondition(listCondition)"></td>
					<td style="width:40% ">
						<select class="form-control" ng-change="inputsValueChange(listCondition)"
						        ng-enter="patientListCondition(listCondition)" ng-model="listCondition.field">
							<option value="">--Select Field--</option>
							<option ng-repeat="field in fields track by field.field" value="{{field.field}}"
							        ng-selected="field.field == listCondition.field">{{field.field}}</option>
						</select>
					</td>
					<td style="width:40% ">
						<select class="form-control" ng-model="listCondition.operator"
								        ng-enter="patientListCondition(listCondition)"
								        ng-disabled="listCondition.field == 'p.hasActiveVisit' || listCondition.field == 'v.hasDiagnosis'" ng-selected="patientListConditionOperator(listCondition)">
							<option value="">--Select Operator--</option>
							<option
									value="EQUALS">=</option>
							<option
									value="NOT_EQUALS">!=</option>
							<option ng-hide="listCondition.inputType == 'textInput' || listCondition.inputType == 'conceptInput'
							|| listCondition.field == 'v.visitType' || listCondition.inputType == 'checkBoxInput'"
							        value="GT">></option>
							<option ng-hide="listCondition.inputType == 'textInput' || listCondition.inputType == 'conceptInput'
							|| listCondition.field == 'v.visitType' || listCondition.inputType == 'checkBoxInput'"
							        value="LT"><</option>
							<option ng-hide="listCondition.inputType == 'textInput' || listCondition.inputType == 'conceptInput'
							|| listCondition.inputType == 'checkBoxInput' || listCondition.field == 'v.visitType'"
							        value="GTE">>=</option>
							<option ng-hide="listCondition.inputType == 'textInput' || listCondition.inputType == 'conceptInput'
							|| listCondition.inputType == 'checkBoxInput' || listCondition.field == 'v.visitType'"
							        value="LTE"><=</option>
							<option ng-show="listCondition.inputType == 'textInput' || listCondition.field == 'v.visitType'"
							        value="LIKE">Like</option>
							<option ng-show="listCondition.inputType == 'numberInput' || listCondition.inputType == 'dateInput'"
							        value="BETWEEN">Between</option>
							<option ng-hide="listCondition.field == 'v.diagnosis'" value="NULL">Null</option>
							<option ng-hide="listCondition.field == 'v.diagnosis' || listCondition.inputType == 'checkBoxInput'"
							        value="NOT_NULL">Not Null</option>
							<option ng-hide="listCondition.field == 'v.diagnosis' || listCondition.inputType == 'checkBoxInput'"
							        value="DEFINED">Defined</option>
							<option ng-show="listCondition.inputType == 'dateInput'" value="RELATIVE">Relative</option>
						</select>
					</td>
					<td ng-show="listCondition.inputType == 'textInput'">
						<input name="conditionValue" placeholder="${ui.message("patientlist.condition.value.label")}"
						       class="form-control input-md" type="text" ng-model="listCondition.value"
						       ng-enter="patientListCondition(listCondition)"
						       ng-blur="patientListCondition(listCondition)"
						       ng-disabled="listCondition.field == 'p.hasActiveVisit' || listCondition.field == 'v.hasDiagnosis' || listCondition.operator == 'NULL'
						        || listCondition.operator == 'NOT_NULL' || listCondition.operator == 'DEFINED'"/>
					</td>
					<td ng-show="listCondition.inputType == 'dropDownInput'">
						<select ng-disabled="listCondition.field == 'p.hasActiveVisit' || listCondition.field == 'v.hasDiagnosis' || listCondition.operator == 'NULL'
						 || listCondition.operator == 'NOT_NULL' || listCondition.operator == 'DEFINED'" class="form-control" ng-model="listCondition.value"
						        ng-enter="patientListCondition(listCondition)"
						        ng-change="patientListCondition(listCondition)">
							<option value="">--Select Value--</option>
							<option ng-repeat="dropDownEntry in dropDownEntries" value="{{dropDownEntry.value}}"
							        ng-selected="dropDownEntry.value == listCondition.value">{{dropDownEntry.display}}</option>
						</select>
					</td>
					<td ng-show="listCondition.inputType =='dateInput' && listCondition.operator != 'BETWEEN'"
					    ng-disabled="listCondition.field == 'p.hasActiveVisit' || listCondition.field == 'v.hasDiagnosis' || listCondition.operator == 'NULL'
					     || listCondition.operator == 'NOT_NULL' || listCondition.operator == 'DEFINED'">
						${ui.includeFragment("uicommons", "field/datetimepicker", [
								label        : "",
								useTime      : false,
								formFieldName: 'patientConditionDatePicker',
								model        : 'listConditionValue'
								])}
					</td>
					<td ng-show="listCondition.inputType =='dateInput' && listCondition.operator == 'BETWEEN'"
					    ng-disabled="listCondition.field == 'p.hasActiveVisit' || listCondition.field == 'v.hasDiagnosis' || listCondition.operator == 'NULL'
					     || listCondition.operator == 'NOT_NULL' || listCondition.operator == 'DEFINED'">
						<div class="row">
							<div class="col-md-6">
								${ui.includeFragment("uicommons", "field/datetimepicker", [
										id           : "betweenDateOne",
										label        : "betweenDateOne",
										useTime      : false,
										formFieldName: 'patientConditionDatePicker',
										model        : 'dateOne'
								])}
							</div>
							
							<div class="col-md-6">
								${ui.includeFragment("uicommons", "field/datetimepicker", [
										id           : "betweenDateTwo",
										label        : "betweenDateTwo",
										useTime      : false,
										formFieldName: 'patientConditionDatePicker',
										model        : 'dateTwo'
								])}
							</div>
						</div>
					</td>
					<td ng-show="listCondition.inputType == 'numberInput'">
						<input ng-disabled="listCondition.field == 'p.hasActiveVisit' || listCondition.field == 'v.hasDiagnosis' || listCondition.operator == 'NULL'
						 || listCondition.operator == 'NOT_NULL' || listCondition.operator == 'DEFINED'"
						       name="conditionValue" placeholder="${ui.message("patientlist.condition.value.label")}"
						       class="form-control input-md" type="text" ng-model="listCondition.value" ng-enter="patientListCondition(listCondition)"
						       ng-blur="patientListCondition(listCondition)"/>
					</td>
					<td ng-show="listCondition.inputType == 'checkBoxInput'">
						<label class="checkbox-inline">
							<input type="checkbox" ng-model="listCondition.value"
							       ng-change="patientListCondition(listCondition)" ng-enter="patientListCondition(listCondition)"
							       ng-checked="listCondition.value"
							       ng-disabled="listCondition.field == 'p.hasActiveVisit' || listCondition.field == 'v.hasDiagnosis' || listCondition.operator == 'NULL'
							        || listCondition.operator == 'NOT_NULL' || listCondition.operator == 'DEFINED'"/>&nbsp;Check / Uncheck
						</label>
					</td>
					<td ng-show="listCondition.inputType == 'conceptInput'"
					    ng-class="{'not-valid': listCondition.invalidEntry === true}">
						${ui.includeFragment("openhmis.commons", "searchFragment", [
								typeahead        : ["concept.display for concept in searchConcepts(\$viewValue)"],
								model            : "listCondition.value",
								typeaheadOnSelect: "selectConcept(\$item, listCondition)",
								typeaheadEditable: "true",
								class            : ["form-control conceptSearch"],
								placeholder      : [ui.message('patientlist.list.enterConceptName')],
								ngEnterEvent     : "patientListCondition(listCondition)",
								onChangeEvent    : "patientListCondition(listCondition)",
						])}
					</td>
				</tr>
				
				</tbody>
			</table>
		</div>
	</div>
	<hr/>
	
	<div class="row detail-section-border-top">
		<br/>
		<div class="col-md-12">
			<h3>${ui.message('patientlist.sort.order.header')}</h3>
			<table style="margin-bottom:5px;margin-top: 15px" class="manage-entities-table">
				<thead>
				<tr>
					<th style="width:10% "></th>
					<th>${ui.message("patientlist.sort.order.field.label")}</th>
					<th>${ui.message("patientlist.sort.order.sortOrder.label")}</th>
				</tr>
				</thead>
				<tbody>
				<tr ng-repeat="listOrdering in listOrderings">
					<td class="item-actions" style="width:10% ">
						<input type="image" src="/openmrs/images/trash.gif" tabindex="-1" ng-show="listOrdering.selected"
						       title="${ui.message('patientlist.list.ordering.removeTitle')}"
						       class="remove" ng-click="removeListOrdering(listOrdering)"></td>
					<td>
						<select class="form-control" ng-model="listOrdering.field"
						        ng-enter="patientListSortOrder(listOrdering)"
						        ng-change="patientListSortOrder(listOrdering)">
							<option value="">--Select Field--</option>
							<option ng-repeat="field in fields track by field.field" value="{{field.field}}"
							        ng-selected="field.field == listOrdering.field">{{field.field}}</option>
						</select>
					</td>
					<td>
						<select class="form-control" ng-model="listOrdering.sortOrder"
						        ng-change="patientListSortOrder(listOrdering)" ng-enter="patientListSortOrder(listOrdering)">
							<option ng-selected="listOrdering.sortOrder == 'asc'" value="">Ascending</option>
							<option value="desc">Descending</option>
						</select>
					</td>
				</tr>
				</tbody>
			</table>
		</div>
	</div>
	<hr/>
	
	<div class="row detail-section-border-top">
		<br/>
		
		<div class="row">
			<div class="col-md-12">
				<div class="col-md-4">
					<h3>${ui.message('patientlist.display.template.header')}</h3>
				</div>
			</div>
			
			<div style="margin-bottom:5px;margin-top: 15px" class="col-md-12">
				<div class="col-md-4">
					<span>${ui.message("patientlist.display.template.header.label")}</span>
				</div>
				
				<div class="col-md-7">
					<textarea name="entityHeaderTemplate" type="text" ng-model="entity.headerTemplate"
					          class="form-control"
					          ng-model-options="{ debounce: 1000 }"
					          ng-change="livePreview(entity.headerTemplate, entity.bodyTemplate)"
					          placeholder="${ui.message("patientlist.display.template.header.label")}" rows="5"
					          cols="50"></textarea>
				</div>
			</div>
		</div>
		<br/>
		
		<div class="row">
				<div class="col-md-12">
					<div class="col-md-4">
						<span>${ui.message("patientlist.display.template.body.label")}</span>
					</div>
					<div class="col-md-7">
						<textarea ng-model="entity.bodyTemplate"
								  placeholder="${ui.message("patientlist.display.template.body.label")}" rows="10"
						          cols="50" class="form-control"
								  ng-model-options="{ debounce: 1000 }"
								  ng-change="livePreview(entity.headerTemplate, entity.bodyTemplate)">
						</textarea>
					</div>
				</div>
		</div>
		<br/>

		<span class="live-preview" ng-show="loadPreview">
			<img src="${ui.resourceLink("uicommons", "images/spinner.gif")}"/>
		</span>

		<!-- live preview section -->
		<div  ng-show="headerContent != null && bodyContent != null" class="col-md-12">
			<h3>${ui.message('patientlist.live.preview.header')}</h3>
			<table style="margin-bottom:5px;margin-top: 15px" class="manage-entities-table">
				<thead>
				<tr>
					<th><span ng-bind-html="renderTemplate(headerContent)"></span></th>
				</tr>
				</thead>
				<tbody>
				<tr>
					<td><span ng-bind-html="renderTemplate(bodyContent)"></span></td>
				</tr>
				</tbody>
			</table>
		</div>
	</div>
	<hr/>
	<div class="row detail-section-border-top">
		<br/>
		<div class="col-md-6">
			<input type="button" class="cancel" value="${ui.message("general.cancel")}" ng-click="cancel()"/>
		</div>
		<div class="col-md-6">
			<input type="button" class="confirm right" value="${ui.message("general.save")}" ng-click="saveOrUpdate()"/>
		</div>
	</div>
</form>
${ui.includeFragment("openhmis.commons", "retireUnretireDeleteFragment")}
