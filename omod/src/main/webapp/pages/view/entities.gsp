<%
    ui.decorateWith("appui", "standardEmrPage", [ title: ui.message("patientlist.page") ])

    /* load stylesheets */
    ui.includeCss("openhmis.commons", "bootstrap.css")
    ui.includeCss("openhmis.commons", "entities2x.css")
    ui.includeCss("patientlist", "entity.css")
    ui.includeCss("uicommons", "ngDialog/ngDialog.min.css")

    /* load angular libraries */
    ui.includeJavascript("uicommons", "angular.min.js")
    ui.includeJavascript("uicommons", "angular-ui/angular-ui-router.min.js")
    ui.includeJavascript("uicommons", "angular-ui/ui-bootstrap-tpls-0.11.2.min.js")
    ui.includeJavascript("uicommons", "angular-common.js")
    ui.includeJavascript("uicommons", "ngDialog/ngDialog.js")

    /* load re-usables/common modules */
    ui.includeFragment("openhmis.commons", "loadReusableModules")

    /* load managePatientList modules */
    ui.includeJavascript("patientlist", "view/models/entity.model.js")
    ui.includeJavascript("patientlist", "view/services/entity.restful.services.js")
    ui.includeJavascript("patientlist", "view/controllers/entity.controller.js")
    ui.includeJavascript("patientlist", "constants.js")
%>

<script data-main="view/configs/entity.main" src="/${ ui.contextPath() }/moduleResources/uicommons/scripts/require/require.js"></script>

<div id="entitiesApp">
    <div ui-view></div>
</div>
