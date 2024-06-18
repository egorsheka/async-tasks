package com.async.balancer;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Logger;

public class LoadBalancerRegistryImpl implements LoadBalancerRegistry {

    private final static int MAX_SERVICE_CAPACITY = 10;
    private final Set<String> services;
    private final Lock lock = new ReentrantLock();


    private static final Logger logger = Logger.getLogger(LoadBalancerRegistryImpl.class.getName());


    public LoadBalancerRegistryImpl(Set<String> services) {
        if (services == null) {
            this.services = new HashSet<>(MAX_SERVICE_CAPACITY);
        } else {
            this.services = services;
        }
    }

    @Override
    public boolean register(String service) {
        if (service == null){
            System.out.println("Cannot register service: service is null");
            return false;
        }
        String log = null;
        lock.lock();
        try {
            if (services.size() >= MAX_SERVICE_CAPACITY) {
                log = String.format("Cannot register service %s: maximum capacity reached", service);
                return false;
            }
            boolean added = services.add(service);
            if (!added){
                log = String.format("Cannot register service %s: service already exist", service);
                return false;
            }
            log = String.format("service %s has been registered", service);
            return true;
        }finally {
            lock.unlock();
            logger.info(log);
        }

    }

    @Override
    public boolean unRegister(String service) {
        String log = null;
        lock.lock();
        try {
            boolean removed = services.remove(service);
            if (removed){
                log = "...1";
                return true;
            }else {
                log = "...2";
                return false;
            }
        }finally {
            lock.unlock();
            logger.info(log);
        }
    }

    @Override
    public Set<String> getAllServices() {
        lock.lock();
        try {
            return new HashSet<>(services);
        }finally {
            lock.unlock();
        }
    }
}
