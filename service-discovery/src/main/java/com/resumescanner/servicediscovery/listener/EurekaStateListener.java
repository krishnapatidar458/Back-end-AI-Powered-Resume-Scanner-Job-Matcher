package com.resumescanner.servicediscovery.listener;

import com.netflix.appinfo.InstanceInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.netflix.eureka.server.event.EurekaInstanceCanceledEvent;
import org.springframework.cloud.netflix.eureka.server.event.EurekaInstanceRegisteredEvent;
import org.springframework.cloud.netflix.eureka.server.event.EurekaInstanceRenewedEvent;
import org.springframework.cloud.netflix.eureka.server.event.EurekaRegistryAvailableEvent;
import org.springframework.cloud.netflix.eureka.server.event.EurekaServerStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class EurekaStateListener {

    @EventListener
    public void listen(EurekaInstanceRegisteredEvent event) {
        InstanceInfo instanceInfo = event.getInstanceInfo();
        log.info("Service {} with instance {} registered", 
                instanceInfo.getAppName(), instanceInfo.getInstanceId());
        log.debug("Instance details: {}:{}", instanceInfo.getIPAddr(), instanceInfo.getPort());
    }

    @EventListener
    public void listen(EurekaInstanceCanceledEvent event) {
        log.warn("Service {} with instance {} canceled/deregistered", 
                event.getAppName(), event.getServerId());
    }

    @EventListener
    public void listen(EurekaInstanceRenewedEvent event) {
        log.debug("Service {} with instance {} renewed lease", 
                event.getAppName(), event.getServerId());
    }

    @EventListener
    public void listen(EurekaRegistryAvailableEvent event) {
        log.info("Eureka registry is now available for service registrations");
    }

    @EventListener
    public void listen(EurekaServerStartedEvent event) {
        log.info("Eureka Server has started and is ready to accept service registrations");
    }
}
