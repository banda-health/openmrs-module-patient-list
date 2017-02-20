<script type="text/javascript">
    var breadcrumbs = [
        {icon: "icon-home", link: '/' + OPENMRS_CONTEXT_PATH + '/index.htm'},
        {label: "${ui.message("patientlist.title")}"}
    ];

    jQuery('#breadcrumbs').html(emr.generateBreadcrumbHtml(breadcrumbs));

</script>
<span class="h1-substitue-left">
    ${ui.message('patientlist.page')}
    <br /><br />
</span>
<div>
    <ul id="patient-list" class="left-menu">
        <li class="menu-item viewPatientListDetails"
            ng-class="{'selected': patientList.selected}"
            ng-repeat="patientList in patientLists track by patientList.uuid"
            ng-click="getPatientListData(patientList, patientList.currentPage, limit, true)">
            <i ng-show="patientList.showSpinner === true" class="icon-spinner icon-spin icon-2x pull-left"></i>
            <span class="menu-title capitalize">{{patientList.name}}</span>
            <span class="arrow-border"></span>
            <span class="arrow"></span>
        </li>
    </ul>

    <div class="main-content" >

        <div ng-show="patientLists.length === 0" class="empty-patient-list">
            ${ui.message('patientlist.list.none')}
        </div>

        <div ng-show="fetchedEntities.length == 0 && loaded == true" class="empty-patient-list">
            ${ui.message('patientlist.list.empty')}
        </div>

        <table class="manage-entities-table" ng-show="fetchedEntities.length > 0">
            <tr class="clickable-tr" pagination-id="__patientListData"
                dir-paginate="entity in fetchedEntities | itemsPerPage: limit"
                total-items="totalNumOfResults" current-page="patientList.currentPage">

                <td>
                    <div class="row" >
                        <span ng-bind-html="renderTemplate(entity.headerContent)"></span>
                    </div>

                    <div class="row" >
                        <span ng-bind-html="renderTemplate(entity.bodyContent)"></span>
                    </div>

                    <div class="row">
                        <div class="status-container">
                            <span ng-show="entity.visit.stopDatetime === null">
                                <span class="status active"></span>
                                ${ ui.message("coreapps.activeVisit") }
                            </span>

                            <a class="right"
                               href="/${ ui.contextPath() }/coreapps/clinicianfacing/patient.page?patientId={{entity.patient.uuid}}">
                                ${ui.message('patientlist.view.patientDetails')}</a>
                            <span class="right"> | </span>

                            <a class="right" ng-show="entity.visit.stopDatetime === null"
                               href="/${ ui.contextPath() }/coreapps/patientdashboard/patientDashboard.page?patientId={{entity.patient.uuid}}">
                                ${ui.message('coreapps.patientDashBoard.visitDetails')}</a>
                            <span ng-show="entity.visit.stopDatetime === null" class="right"> | </span>

                            <a class="right"
                               ng-show="entity.visit.stopDatetime !== null"
                               href="/${ ui.contextPath() }/coreapps/clinicianfacing/patient.page?patientId={{entity.patient.uuid}}">
                                ${ ui.message("coreapps.task.startVisit.label") }
                            </a>
                            <a class="right" ng-show="entity.visit.stopDatetime === null"
                               ng-click="endVisitDialog(entity.visit.uuid)">${ ui.message("coreapps.task.endVisit.label") }</a>
                        </div>
                    </div>
                </td>
            </tr>
        </table>
        ${ui.includeFragment("openhmis.commons", "paginationFragment", [
                paginationId      : "__patientListData",
                onPageChange      : "getPatientListData(patientList, patientList.currentPage, limit)",
                onChange          : "getPatientListData(patientList, patientList.currentPage, limit)",
                model             : "limit",
                pagingFrom        : "pagingFrom(patientList.currentPage || 1, limit)",
                pagingTo          : "pagingTo(patientList.currentPage || 1, limit, totalNumOfResults)",
                showRetiredSection: "false"
        ])}

    </div>

    <div id="entities" class="detail-section-border-top">
        <br/>
        <input type="button" class="cancel" value="${ui.message('general.back')}" ng-click="cancel()" />
    </div>

    <div id="end-visit-dialog" class="dialog hide-dialog">
        <div class="dialog-header">
            <span>
                <i class="icon-warning-sign"></i>
                <h3>${ ui.message("coreapps.task.endVisit.label") }</h3>
            </span>
            <i class="icon-remove cancel show-cursor align-right" ng-click="closeThisDialog()"></i>
        </div>
        <div class="dialog-content form">
            <span>${ ui.message("coreapps.task.endVisit.message") }</span>
            <br /><br />
            <div class="ngdialog-buttons detail-section-border-top">
                <br />
                <input type="button" class="cancel" value="${ui.message('general.no')}" ng-click="closeThisDialog('Cancel')" />
                <input type="button" class="confirm right" value="${ui.message('general.yes')}"
                       ng-click="confirm('OK'); getPatientListData(patientList, patientList.currentPage, limit)" />
            </div>
        </div>
    </div>
</div>



