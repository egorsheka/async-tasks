package com.async.balancer;

import java.util.Set;

public interface LoadBalancerRegistry {

    //todo replace String for Service class - override equals and hashCode, make it immutable
    boolean register(String service);
    boolean unRegister(String service);

    Set<String> getAllServices();
}
