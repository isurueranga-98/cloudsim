package com.example.multicloud;

import org.cloudsimplus.brokers.DatacenterBrokerSimple;
import org.cloudsimplus.cloudlets.Cloudlet;
import org.cloudsimplus.core.CloudSimPlus;
import org.cloudsimplus.datacenters.Datacenter;
import org.cloudsimplus.vms.Vm;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * MultiCloudBroker extends the default DatacenterBroker to ensure VMs and cloudlets
 * are provisioned in the datacenter that matches their allocated cloud provider.
 */
public class MultiCloudBroker extends DatacenterBrokerSimple {
    private final Map<Long, CloudProvider.ProviderType> vmToProvider = new HashMap<>();
    private final Map<Long, CloudProvider.ProviderType> cloudletToProvider = new HashMap<>();
    private final Map<CloudProvider.ProviderType, Datacenter> providerDatacenters =
            new EnumMap<>(CloudProvider.ProviderType.class);
    private final Map<CloudProvider.ProviderType, List<Vm>> providerVmOrder =
            new EnumMap<>(CloudProvider.ProviderType.class);
    private final List<Vm> submittedVms = new ArrayList<>();

    public MultiCloudBroker(CloudSimPlus simulation) {
        super(simulation);
        setDatacenterMapper(this::mapDatacenterForVm);
    }

    public void registerProviders(Collection<CloudProvider> providers) {
        providers.forEach(this::registerProvider);
    }

    public void registerProvider(CloudProvider provider) {
        providerDatacenters.put(provider.getType(), provider.getDatacenter());
    }

    public void prepareVmAllocations(Map<CloudProvider, List<Vm>> vmAllocation) {
        vmToProvider.clear();
        providerVmOrder.clear();
        submittedVms.clear();

        for (Map.Entry<CloudProvider, List<Vm>> entry : vmAllocation.entrySet()) {
            CloudProvider provider = entry.getKey();
            List<Vm> vms = entry.getValue();
            if (vms.isEmpty()) {
                continue;
            }

            CloudProvider.ProviderType type = provider.getType();
            providerVmOrder.put(type, new ArrayList<>(vms));
            for (Vm vm : vms) {
                vmToProvider.put(vm.getId(), type);
                submittedVms.add(vm);
            }
        }
    }

    public void bindCloudlets(Map<CloudProvider, List<Cloudlet>> cloudletAllocation) {
        for (Map.Entry<CloudProvider, List<Cloudlet>> entry : cloudletAllocation.entrySet()) {
            CloudProvider provider = entry.getKey();
            List<Cloudlet> cloudlets = entry.getValue();
            if (cloudlets.isEmpty()) {
                continue;
            }

            CloudProvider.ProviderType type = provider.getType();
            List<Vm> vms = providerVmOrder.get(type);
            if (vms == null || vms.isEmpty()) {
                assignCloudletsToAnyVm(cloudlets);
                continue;
            }

            int index = 0;
            for (Cloudlet cloudlet : cloudlets) {
                Vm targetVm = vms.get(index % vms.size());
                bindCloudletToVm(cloudlet, targetVm);
                cloudletToProvider.put(cloudlet.getId(), type);
                index++;
            }
        }
    }

    public Map<CloudProvider.ProviderType, List<Cloudlet>> getFinishedCloudletsByProvider() {
        Map<CloudProvider.ProviderType, List<Cloudlet>> result =
                new EnumMap<>(CloudProvider.ProviderType.class);
        for (Cloudlet cloudlet : getCloudletFinishedList()) {
            CloudProvider.ProviderType type = cloudletToProvider.get(cloudlet.getId());
            if (type == null) {
                continue;
            }
            result.computeIfAbsent(type, key -> new ArrayList<>()).add(cloudlet);
        }
        return result;
    }

    private Datacenter mapDatacenterForVm(final Datacenter lastDatacenter, final Vm vm) {
        CloudProvider.ProviderType type = vmToProvider.get(vm.getId());
        if (type != null) {
            Datacenter datacenter = providerDatacenters.get(type);
            if (datacenter != null) {
                return datacenter;
            }
        }
        return super.defaultDatacenterMapper(lastDatacenter, vm);
    }

    private void assignCloudletsToAnyVm(List<Cloudlet> cloudlets) {
        if (submittedVms.isEmpty()) {
            return;
        }
        int index = 0;
        for (Cloudlet cloudlet : cloudlets) {
            Vm targetVm = submittedVms.get(index % submittedVms.size());
            bindCloudletToVm(cloudlet, targetVm);
            CloudProvider.ProviderType type = vmToProvider.get(targetVm.getId());
            if (type != null) {
                cloudletToProvider.put(cloudlet.getId(), type);
            }
            index++;
        }
    }
}
