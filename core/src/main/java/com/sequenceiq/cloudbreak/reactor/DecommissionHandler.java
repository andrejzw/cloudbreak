package com.sequenceiq.cloudbreak.reactor;

import java.util.Set;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import com.sequenceiq.cloudbreak.domain.Stack;
import com.sequenceiq.cloudbreak.logger.MDCBuilder;
import com.sequenceiq.cloudbreak.reactor.api.event.resource.DecommissionRequest;
import com.sequenceiq.cloudbreak.reactor.api.event.resource.DecommissionResult;
import com.sequenceiq.cloudbreak.service.cluster.flow.AmbariDecommissioner;
import com.sequenceiq.cloudbreak.service.stack.StackService;

import reactor.bus.Event;
import reactor.bus.EventBus;

@Component
public class DecommissionHandler implements ClusterEventHandler<DecommissionRequest> {

    @Inject
    private EventBus eventBus;

    @Inject
    private StackService stackService;

    @Inject
    private AmbariDecommissioner ambariDecommissioner;

    @Override
    public Class<DecommissionRequest> type() {
        return DecommissionRequest.class;
    }

    @Override
    public void accept(Event<DecommissionRequest> event) {
        DecommissionRequest request = event.getData();
        DecommissionResult result;
        try {
            Stack stack = stackService.getById(request.getStackId());
            MDCBuilder.buildMdcContext(stack);
            Set<String> hostNames;
            if (request.getHostNames() == null) {
                hostNames = ambariDecommissioner.decommissionAmbariNodes(stack, request.getHostGroupName(), request.getScalingAdjustment());
            } else {
                hostNames = ambariDecommissioner.decommissionAmbariNodes(stack, request.getHostGroupName(), request.getHostNames());
            }
            result = new DecommissionResult(request, hostNames);
        } catch (Exception e) {
            result = new DecommissionResult(e.getMessage(), e, request);
        }
        eventBus.notify(result.selector(), new Event<>(event.getHeaders(), result));
    }
}
