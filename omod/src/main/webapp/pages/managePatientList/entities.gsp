<%
    ui.decorateWith("appui", "standardEmrPage", [ title: ui.message("patientlist.admin.patientList") ])

   
    /* load stylesheets */
    ui.includeCss("openhmis.commons", "bootstrap.css")
    ui.includeCss("openhmis.commons", "entities2x.css")
    ui.includeCss("uicommons", "ngDialog/ngDialog.min.css")
    ui.includeCss("uicommons", "datetimepicker.css")
    ui.includeCss("patientlist", "entity.css")

    /* load angular libraries */
    ui.includeJavascript("uicommons", "angular.min.js")
    ui.includeJavascript("uicommons", "angular-ui/angular-ui-router.min.js")
    ui.includeJavascript("uicommons", "angular-ui/ui-bootstrap-tpls-0.11.2.min.js")
    ui.includeJavascript("uicommons", "angular-common.js")
    ui.includeJavascript("uicommons", "ngDialog/ngDialog.js")
    ui.includeJavascript("uicommons", "datetimepicker/bootstrap-datetimepicker.min.js")

    /* load re-usables/common modules */
    ui.includeFragment("openhmis.commons", "load.reusable.modules")

    /* load managePatientList modules */
    ui.includeJavascript("patientlist", "managePatientList/models/entity.model.js")
    ui.includeJavascript("patientlist", "managePatientList/models/patientlist.condition.model.js")
    ui.includeJavascript("patientlist", "managePatientList/models/patientlist.ordering.model.js")
    ui.includeJavascript("patientlist", "managePatientList/services/entity.functions.js")
    ui.includeJavascript("patientlist", "managePatientList/services/entity.restful.services.js")
    ui.includeJavascript("patientlist", "managePatientList/controllers/entity.controller.js")
    ui.includeJavascript("patientlist", "managePatientList/controllers/manage-entity.controller.js")
    ui.includeJavascript("patientlist", "constants.js")
%>

<script data-main="managePatientList/configs/entity.main" src="/${ ui.contextPath() }/moduleResources/uicommons/scripts/require/require.js"></script>

<div id="entitiesApp">
    <div ui-view></div>
</div>
