package com.async.balancer;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

public class LoadBalancerRegistryTest {


    @Test
    public void register_withValidServer_shouldReturnTrue() {}

    @Test
    public void register_withNullServer_shouldReturnFalse() {}

    @Test
    public void register_withNotUniqueServer_shouldReturnFalse() {}

    @Test
    public void register_withMaxServersExceeded_shouldReturnFalse() {}

    @Test
    public void unRegister_withValidServer_shouldReturnTrue() {}

    @Test
    public void unRegister_withNullServer_shouldReturnFalse() {}

    @Test
    public void unRegister_withNotExistServer_shouldReturnFalse() {}

    @Test
    public void registerAndUnRegister_concurrently_shouldHandleConcurrentRegistrationAndUnRegisterCorrectly() {}

    //todo
    // register
    // add service
    // add more than 10 services
    // add not unique service
    // add null service

    // todo
    // unregister
    // remove null
    //
    // check under concurrent pressure

    @Test
    public void testLoadBalancerCapacity(){
        LoadBalancerRegistry loadBalancerRegistry = new LoadBalancerRegistryImpl(new HashSet<>(10));

        for (int i = 0; i < 20; i++) {
            loadBalancerRegistry.register(String.format("service %s", i));
        }
        Assertions.assertEquals(10, loadBalancerRegistry.getAllServices().size());
    }

    @Test
    public void testLoadBalancerUniquenessLogic(){
        List<String> services = List.of("service 1",
                "service 2",
                "service 2",
                "service 3",
                "service 4",
                "service 4");


        LoadBalancerRegistry loadBalancerRegistry = new LoadBalancerRegistryImpl(new HashSet<>(10));

        for (String service : services) {
            loadBalancerRegistry.register(service);
        }

        List<String> expectedServices = List.of("service 1",
                "service 2",
                "service 3",
                "service 4");

        Assertions.assertEquals(4, loadBalancerRegistry.getAllServices().size());
        List<String> servicesList = new ArrayList<>(loadBalancerRegistry.getAllServices());
        Collections.sort(servicesList);
        Assertions.assertEquals(expectedServices, servicesList);
    }
}
