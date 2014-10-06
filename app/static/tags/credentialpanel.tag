<!-- .... CREDENTIALS PANEL ................................................ -->

<div ng-controller="credentialController" class="col-md-12 col-lg-9">

    <div class="panel panel-default">
        <div class="panel-heading panel-heading-nav">
            <a href="" id="credentials-btn" class="btn btn-info btn-fa-2x" role="button" data-toggle="collapse" data-target="#panel-credentials-collapse">
                <i class="fa fa-angle-down fa-2x fa-fw-forced"></i>
            </a>
            <h4>
                <span class="badge pull-right">{{$root.credentials.length}}</span> manage credentials
            </h4>
        </div>

        <div id="panel-credentials-collapse" class="panel-collapse panel-btn-in-header-collapse collapse">
            <div class="panel-body">

                <p class="btn-row-over-panel">
                    <a href="" class="btn btn-success" role="button" data-toggle="collapse" data-target="#panel-create-credential-collapse">
                        <i class="fa fa-plus fa-fw"></i><span> create credential</span>
                    </a>
                </p>

                <!-- ............ CREATE FORM ............................................. -->

                <div class="panel panel-default">
                    <div id="panel-create-credential-collapse" class="panel-under-btn-collapse collapse">
                        <div class="panel-body">
                            <div class="row " style="padding-bottom: 10px">
                                <div class="btn-segmented-control" id="providerSelector1">
                                    <div class="btn-group btn-group-justified">
                                        <a id="awsChange" type="button" class="btn btn-info" ng-click="createAwsCredentialRequest()">Aws</a>
                                        <a id="azureChange" type="button" class="btn btn-default" ng-click="createAzureCredentialRequest()">Azure</a>
                                    </div>
                                </div>
                            </div>
                            <div ng-include src="'tags/credential/azureform.tag'"></div>
                            <div ng-include src="'tags/credential/awsform.tag'"></div>
                        </div>
                    </div>
                </div>
                <!-- .panel -->

                <!-- ............ CREDENTIAL LIST ........................................... -->

                <div class="panel-group" id="credential-list-accordion">

                    <!-- ............. CREDENTIAL ............................................... -->

                      <div class="panel panel-default" ng-repeat="credential in $root.credentials">
                        <div class="panel-heading">
                            <h5><a href="" data-toggle="collapse" data-parent="#credential-list-accordion"
                                   data-target="#panel-credential-collapse{{credential.id}}"><i class="fa fa-tag fa-fw"></i>{{credential.name}}</a></h5>
                        </div>
                        <div id="panel-credential-collapse{{credential.id}}" class="panel-collapse collapse">

                            <p class="btn-row-over-panel">
                                <a href="" class="btn btn-danger" role="button" ng-click="deleteCredential(credential)">
                                    <i class="fa fa-times fa-fw"></i>
                                    <span> delete</span>
                                </a>
                            </p>


                            <div class="panel-body" ng-if="credential.cloudPlatform == 'AZURE' ">
                                <div ng-include src="'tags/credential/azurelist.tag'"></div>
                            </div>

                            <div class="panel-body" ng-if="credential.cloudPlatform == 'AWS' ">
                                <div ng-include src="'tags/credential/awslist.tag'"></div>
                            </div>
                        </div>
                    </div>
                    <!-- .panel -->


                </div>
                <!-- #credential-list-accordion -->

            </div>
            <!-- .panel-body -->

        </div>
        <!-- .panel-collapse -->
    </div>
    <!-- .panel -->

</div>
<!-- .col- -->
