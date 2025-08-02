package com.example;

import org.cloudsimplus.brokers.DatacenterBroker;
import org.cloudsimplus.brokers.DatacenterBrokerSimple;
import org.cloudsimplus.builders.tables.CloudletsTableBuilder;
import org.cloudsimplus.cloudlets.Cloudlet;
import org.cloudsimplus.cloudlets.CloudletSimple;
import org.cloudsimplus.core.CloudSimPlus;
import org.cloudsimplus.datacenters.Datacenter;
import org.cloudsimplus.datacenters.DatacenterSimple;
import org.cloudsimplus.hosts.Host;
import org.cloudsimplus.hosts.HostSimple;
import org.cloudsimplus.resources.Pe;
import org.cloudsimplus.resources.PeSimple;
import org.cloudsimplus.schedulers.cloudlet.CloudletSchedulerTimeShared;
import org.cloudsimplus.schedulers.vm.VmSchedulerTimeShared;
import org.cloudsimplus.utilizationmodels.UtilizationModelDynamic;
import org.cloudsimplus.vms.Vm;
import org.cloudsimplus.vms.VmSimple;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple CloudSim Plus example demonstrating basic cloud simulation.
 * This example creates a datacenter with one host, one VM, and one cloudlet.
 */
public class CloudSimPlusExample {
    private static final int HOSTS = 1;
    private static final int HOST_PES = 8;
    private static final int VMS = 2;
    private static final int CLOUDLETS = 4;

    private final CloudSimPlus simulation;
    private DatacenterBroker broker0;
    private List<Vm> vmList;
    private List<Cloudlet> cloudletList;
    private Datacenter datacenter0;

    public static void main(String[] args) {
        new CloudSimPlusExample();
    }

    private CloudSimPlusExample() {
        System.out.println("Starting CloudSim Plus simulation...");

        simulation = new CloudSimPlus();
        datacenter0 = createDatacenter();

        broker0 = new DatacenterBrokerSimple(simulation);

        vmList = createVms();
        cloudletList = createCloudlets();
        broker0.submitVmList(vmList);
        broker0.submitCloudletList(cloudletList);

        simulation.start();

        final List<Cloudlet> finishedCloudlets = broker0.getCloudletFinishedList();
        new CloudletsTableBuilder(finishedCloudlets).build();

        System.out.println("CloudSim Plus simulation completed successfully!");
    }

    /**
     * Creates a Datacenter and its Hosts.
     */
    private Datacenter createDatacenter() {
        final List<Host> hostList = new ArrayList<>(HOSTS);
        for (int i = 0; i < HOSTS; i++) {
            Host host = createHost();
            hostList.add(host);
        }

        return new DatacenterSimple(simulation, hostList);
    }

    /**
     * Creates a host with its PEs and VMs.
     */
    private Host createHost() {
        final List<Pe> peList = new ArrayList<>(HOST_PES);
        // List of Host's CPUs (Processing Elements, PEs)
        for (int i = 0; i < HOST_PES; i++) {
            peList.add(new PeSimple(1000));
        }

        final long ram = 2048; // in Megabytes
        final long bw = 10000; // in Megabits/s
        final long storage = 1000000; // in Megabytes

        return new HostSimple(ram, bw, storage, peList)
                .setVmScheduler(new VmSchedulerTimeShared());
    }

    /**
     * Creates a list of VMs.
     */
    private List<Vm> createVms() {
        final List<Vm> list = new ArrayList<>(VMS);
        for (int i = 0; i < VMS; i++) {
            Vm vm = new VmSimple(1000, 2)
                    .setRam(512).setBw(1000).setSize(10000)
                    .setCloudletScheduler(new CloudletSchedulerTimeShared());

            list.add(vm);
        }

        return list;
    }

    /**
     * Creates a list of Cloudlets.
     */
    private List<Cloudlet> createCloudlets() {
        final List<Cloudlet> list = new ArrayList<>(CLOUDLETS);
        UtilizationModelDynamic utilizationModel = new UtilizationModelDynamic(0.5);

        for (int i = 0; i < CLOUDLETS; i++) {
            Cloudlet cloudlet = new CloudletSimple(10000, 2, utilizationModel);
            cloudlet.setSizes(1024);
            list.add(cloudlet);
        }

        return list;
    }
}
