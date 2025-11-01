package com.example.multicloud;

import org.cloudsimplus.brokers.DatacenterBrokerSimple;
import org.cloudsimplus.cloudlets.Cloudlet;
import org.cloudsimplus.core.CloudSimPlus;
import org.cloudsimplus.datacenters.Datacenter;
import org.cloudsimplus.vms.Vm;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * A broker that leverages {@link MultiCloudResourceManager} to allocate VMs and Cloudlets
 * across multiple cloud providers in an intelligent way.
 */
public class MultiCloudBroker extends DatacenterBrokerSimple {

    private final MultiCloudResourceManager resourceManager;
    private final List<CloudProvider> providers;
    private final Map<Vm, CloudProvider> vmProviderAssignments = new HashMap<>();
    private final Map<Cloudlet, Vm> cloudletAssignments = new HashMap<>();

    public MultiCloudBroker(final CloudSimPlus simulation,
                             final String name,
                             final MultiCloudResourceManager resourceManager,
                             final List<CloudProvider> providers) {
        super(simulation);
        setName(name);
        this.resourceManager = Objects.requireNonNull(resourceManager, "resourceManager must not be null");
        this.providers = new ArrayList<>(Objects.requireNonNull(providers, "providers must not be null"));
        setDatacenterMapper(this::mapVmToDatacenter);
        setVmMapper(this::mapCloudletToVm);
    }

    private Datacenter mapVmToDatacenter(final Datacenter originalDc, final Vm vm) {
        final CloudProvider provider = resourceManager.selectProviderForVm(vm, providers);
        vmProviderAssignments.put(vm, provider);
        return provider.getDatacenter();
    }

    private Vm mapCloudletToVm(final Cloudlet cloudlet) {
        final Vm selectedVm = resourceManager.selectVmForCloudlet(cloudlet, getVmCreatedList(), vmProviderAssignments);
        if (!selectedVm.equals(Vm.NULL)) {
            cloudletAssignments.put(cloudlet, selectedVm);
        }
        return selectedVm;
    }

    public Map<Vm, CloudProvider> getVmProviderAssignments() {
        return Collections.unmodifiableMap(vmProviderAssignments);
    }

    public Map<Cloudlet, Vm> getCloudletAssignments() {
        return Collections.unmodifiableMap(cloudletAssignments);
    }

    public Map<CloudProvider, List<Vm>> getRealizedVmAllocation() {
        return vmProviderAssignments.entrySet().stream()
                .collect(Collectors.groupingBy(Map.Entry::getValue,
                        Collectors.mapping(Map.Entry::getKey, Collectors.toList())));
    }

    public Map<CloudProvider, List<Cloudlet>> getRealizedCloudletAllocation() {
        return cloudletAssignments.entrySet().stream()
                .map(entry -> new AbstractMap.SimpleEntry<>(vmProviderAssignments.get(entry.getValue()), entry.getKey()))
                .filter(entry -> entry.getKey() != null)
                .collect(Collectors.groupingBy(Map.Entry::getKey,
                        Collectors.mapping(Map.Entry::getValue, Collectors.toList())));
    }

    public List<CloudProvider> getProviders() {
        return Collections.unmodifiableList(providers);
    }
}
