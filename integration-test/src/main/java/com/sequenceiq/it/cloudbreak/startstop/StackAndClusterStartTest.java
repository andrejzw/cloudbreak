package com.sequenceiq.it.cloudbreak.startstop;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.sequenceiq.cloudbreak.api.model.StatusRequest;
import com.sequenceiq.cloudbreak.api.model.UpdateClusterJson;
import com.sequenceiq.cloudbreak.api.model.UpdateStackJson;
import com.sequenceiq.it.IntegrationTestContext;
import com.sequenceiq.it.cloudbreak.AbstractCloudbreakIntegrationTest;
import com.sequenceiq.it.cloudbreak.CloudbreakITContextConstants;
import com.sequenceiq.it.cloudbreak.CloudbreakUtil;



public class StackAndClusterStartTest extends AbstractCloudbreakIntegrationTest {
    private static final String STARTED = "STARTED";

    private static final String NOWAIT = "false";

    @BeforeMethod
    public void setContextParameters() {
        IntegrationTestContext itContext = getItContext();
        Assert.assertNotNull(itContext.getContextParam(CloudbreakITContextConstants.STACK_ID), "Stack id is mandatory.");
        Assert.assertNotNull(itContext.getContextParam(CloudbreakITContextConstants.AMBARI_USER_ID), "Ambari user id is mandatory.");
        Assert.assertNotNull(itContext.getContextParam(CloudbreakITContextConstants.AMBARI_PASSWORD_ID), "Ambari password id is mandatory.");
        Assert.assertNotNull(itContext.getContextParam(CloudbreakITContextConstants.AMBARI_PORT_ID), "Ambari port id is mandatory.");
    }

    @Test
    @Parameters({ "waitOn" })
    public void testStackAndClusterStart(@Optional(NOWAIT) Boolean waitOn) throws Exception {
        // GIVEN
        IntegrationTestContext itContext = getItContext();
        String stackId = itContext.getContextParam(CloudbreakITContextConstants.STACK_ID);
        Integer stackIntId = Integer.valueOf(stackId);
        String ambariUser = itContext.getContextParam(CloudbreakITContextConstants.AMBARI_USER_ID);
        String ambariPassword = itContext.getContextParam(CloudbreakITContextConstants.AMBARI_PASSWORD_ID);
        String ambariPort = itContext.getContextParam(CloudbreakITContextConstants.AMBARI_PORT_ID);
        // WHEN
        UpdateStackJson updateStackJson = new UpdateStackJson();
        updateStackJson.setStatus(StatusRequest.valueOf(STARTED));
        CloudbreakUtil.checkResponse("StartStack", getCloudbreakClient().stackEndpoint().put(Long.valueOf(stackIntId), updateStackJson));

        if (Boolean.TRUE.equals(waitOn)) {
            CloudbreakUtil.waitAndCheckStackStatus(getCloudbreakClient(), stackId, "AVAILABLE");
        }

        UpdateClusterJson updateClusterJson = new UpdateClusterJson();
        updateClusterJson.setStatus(StatusRequest.valueOf(STARTED));
        CloudbreakUtil.checkResponse("StartCluster", getCloudbreakClient().clusterEndpoint().put(Long.valueOf(stackIntId), updateClusterJson));
        CloudbreakUtil.waitAndCheckClusterStatus(getCloudbreakClient(), stackId, "AVAILABLE");
        // THEN
        CloudbreakUtil.checkClusterAvailability(getCloudbreakClient().stackEndpoint(), ambariPort, stackId, ambariUser, ambariPassword, true);
    }
}
