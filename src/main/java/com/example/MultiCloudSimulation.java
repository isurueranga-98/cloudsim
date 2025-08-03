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
import org.cloudsimplus.provisioners.PeProvisionerSimple;
import org.cloudsimplus.provisioners.ResourceProvisionerSimple;
import org.cloudsimplus.resources.Pe;
import org.cloudsimplus.resources.PeSimple;
import org.cloudsimplus.schedulers.cloudlet.CloudletSchedulerTimeShared;
import org.cloudsimplus.schedulers.vm.VmSchedulerTimeShared;
import org.cloudsimplus.schedulers.vm.VmSchedulerSpaceShared;
import org.cloudsimplus.utilizationmodels.UtilizationModelDynamic;
import org.cloudsimplus.vms.Vm;
import org.cloudsimplus.vms.VmSimple;
import org.cloudsimplus.allocationpolicies.VmAllocationPolicySimple;
import org.cloudsimplus.allocationpolicies.VmAllocationPolicyBestFit;
import org.cloudsimplus.allocationpolicies.VmAllocationPolicyFirstFit;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

/**
 * Advanced Multi-Cloud Simulation demonstrating heterogeneous cloud environments.
 * This simulation creates multiple cloud providers with different characteristics:
 * - AWS-like datacenter: High performance, expensive
 * - Azure-like datacenter: Balanced performance and cost
 * - GCP-like datacenter: Cost-effective, good for batch processing
 * - Edge datacenter: Low latency, limited resources
 */
public class MultiCloudSimulation {
    
    // Cloud provider configurations
    private static final int AWS_HOSTS = 3;
    private static final int AZURE_HOSTS = 4;
    private static final int GCP_HOSTS = 5;
    private static final int EDGE_HOSTS = 2;
    
    private static final int TOTAL_VMS = 16;
    private static final int TOTAL_CLOUDLETS = 32;
    
    // Simulation components
    private final CloudSimPlus simulation;
    private List<Datacenter> datacenterList;
    private List<DatacenterBroker> brokerList;
    private List<Vm> vmList;
    private List<Cloudlet> cloudletList;
    
    public static void main(String[] args) {
        new MultiCloudSimulation();
    }
    
    private MultiCloudSimulation() {
        System.out.println("=".repeat(60));
        System.out.println("Starting Multi-Cloud Heterogeneous Simulation...");
        System.out.println("=".repeat(60));
        
        simulation = new CloudSimPlus();
        
        // Create heterogeneous cloud environments
        datacenterList = createHeterogeneousDatacenters();
        
        // Create brokers for different cloud strategies
        brokerList = createBrokers();
        
        // Create diverse VM configurations
        vmList = createDiverseVms();
        
        // Create varied workloads
        cloudletList = createVariedCloudlets();
        
        // Distribute resources across brokers
        distributeResources();
        
        // Start simulation
        simulation.start();
        
        // Display results
        displayResults();
        
        System.out.println("Multi-Cloud simulation completed successfully!");
    }
    
    /**
     * Creates heterogeneous datacenters representing different cloud providers
     */
    private List<Datacenter> createHeterogeneousDatacenters() {
        List<Datacenter> datacenters = new ArrayList<>();
        
        // AWS-like Datacenter: High-performance, premium pricing
        Datacenter awsDatacenter = createAWSLikeDatacenter();
        datacenters.add(awsDatacenter);
        System.out.println("✓ Created AWS-like datacenter with " + AWS_HOSTS + " high-performance hosts");
        
        // Azure-like Datacenter: Balanced performance and cost
        Datacenter azureDatacenter = createAzureLikeDatacenter();
        datacenters.add(azureDatacenter);
        System.out.println("✓ Created Azure-like datacenter with " + AZURE_HOSTS + " balanced hosts");
        
        // GCP-like Datacenter: Cost-effective, good for batch processing
        Datacenter gcpDatacenter = createGCPLikeDatacenter();
        datacenters.add(gcpDatacenter);
        System.out.println("✓ Created GCP-like datacenter with " + GCP_HOSTS + " cost-effective hosts");
        
        // Edge Datacenter: Low latency, limited resources
        Datacenter edgeDatacenter = createEdgeDatacenter();
        datacenters.add(edgeDatacenter);
        System.out.println("✓ Created Edge datacenter with " + EDGE_HOSTS + " edge hosts");
        
        return datacenters;
    }
    
    /**
     * Creates AWS-like datacenter with high-performance hosts
     */
    private Datacenter createAWSLikeDatacenter() {
        List<Host> hostList = new ArrayList<>();
        
        for (int i = 0; i < AWS_HOSTS; i++) {
            // High-performance PEs (3.5 GHz equivalent)
            List<Pe> peList = createPeList(16, 3500); // 16 cores, 3.5 GHz
            
            Host host = new HostSimple(32768, 25000, 2000000, peList) // 32GB RAM, 25Gbps, 2TB storage
                    .setRamProvisioner(new ResourceProvisionerSimple())
                    .setBwProvisioner(new ResourceProvisionerSimple())
                    .setVmScheduler(new VmSchedulerTimeShared());
            
            hostList.add(host);
        }
        
        Datacenter datacenter = new DatacenterSimple(simulation, hostList, new VmAllocationPolicyBestFit());
        datacenter.getCharacteristics().setCostPerSecond(0.15); // Premium pricing
        datacenter.getCharacteristics().setCostPerMem(0.05);
        datacenter.getCharacteristics().setCostPerStorage(0.001);
        datacenter.getCharacteristics().setCostPerBw(0.01);
        
        return datacenter;
    }
    
    /**
     * Creates Azure-like datacenter with balanced performance
     */
    private Datacenter createAzureLikeDatacenter() {
        List<Host> hostList = new ArrayList<>();
        
        for (int i = 0; i < AZURE_HOSTS; i++) {
            // Balanced PEs (2.8 GHz equivalent)
            List<Pe> peList = createPeList(12, 2800); // 12 cores, 2.8 GHz
            
            Host host = new HostSimple(24576, 20000, 1500000, peList) // 24GB RAM, 20Gbps, 1.5TB storage
                    .setRamProvisioner(new ResourceProvisionerSimple())
                    .setBwProvisioner(new ResourceProvisionerSimple())
                    .setVmScheduler(new VmSchedulerTimeShared());
            
            hostList.add(host);
        }
        
        Datacenter datacenter = new DatacenterSimple(simulation, hostList, new VmAllocationPolicySimple());
        datacenter.getCharacteristics().setCostPerSecond(0.12); // Moderate pricing
        datacenter.getCharacteristics().setCostPerMem(0.04);
        datacenter.getCharacteristics().setCostPerStorage(0.0008);
        datacenter.getCharacteristics().setCostPerBw(0.008);
        
        return datacenter;
    }
    
    /**
     * Creates GCP-like datacenter optimized for cost-effectiveness
     */
    private Datacenter createGCPLikeDatacenter() {
        List<Host> hostList = new ArrayList<>();
        
        for (int i = 0; i < GCP_HOSTS; i++) {
            // Cost-effective PEs (2.2 GHz equivalent)
            List<Pe> peList = createPeList(8, 2200); // 8 cores, 2.2 GHz
            
            Host host = new HostSimple(16384, 15000, 1000000, peList) // 16GB RAM, 15Gbps, 1TB storage
                    .setRamProvisioner(new ResourceProvisionerSimple())
                    .setBwProvisioner(new ResourceProvisionerSimple())
                    .setVmScheduler(new VmSchedulerSpaceShared());
            
            hostList.add(host);
        }
        
        Datacenter datacenter = new DatacenterSimple(simulation, hostList, new VmAllocationPolicyFirstFit());
        datacenter.getCharacteristics().setCostPerSecond(0.08); // Budget pricing
        datacenter.getCharacteristics().setCostPerMem(0.03);
        datacenter.getCharacteristics().setCostPerStorage(0.0005);
        datacenter.getCharacteristics().setCostPerBw(0.005);
        
        return datacenter;
    }
    
    /**
     * Creates edge datacenter with low-latency, limited resources
     */
    private Datacenter createEdgeDatacenter() {
        List<Host> hostList = new ArrayList<>();
        
        for (int i = 0; i < EDGE_HOSTS; i++) {
            // Edge PEs (optimized for low latency)
            List<Pe> peList = createPeList(4, 2000); // 4 cores, 2.0 GHz
            
            Host host = new HostSimple(8192, 5000, 500000, peList) // 8GB RAM, 5Gbps, 500GB storage
                    .setRamProvisioner(new ResourceProvisionerSimple())
                    .setBwProvisioner(new ResourceProvisionerSimple())
                    .setVmScheduler(new VmSchedulerTimeShared());
            
            hostList.add(host);
        }
        
        Datacenter datacenter = new DatacenterSimple(simulation, hostList, new VmAllocationPolicySimple());
        datacenter.getCharacteristics().setCostPerSecond(0.20); // Premium for edge computing
        datacenter.getCharacteristics().setCostPerMem(0.06);
        datacenter.getCharacteristics().setCostPerStorage(0.002);
        datacenter.getCharacteristics().setCostPerBw(0.015);
        
        return datacenter;
    }
    
    /**
     * Helper method to create processing elements
     */
    private List<Pe> createPeList(int numberOfPes, double mips) {
        List<Pe> peList = new ArrayList<>();
        for (int i = 0; i < numberOfPes; i++) {
            peList.add(new PeSimple(mips, new PeProvisionerSimple()));
        }
        return peList;
    }
    
    /**
     * Creates multiple brokers with different strategies
     */
    private List<DatacenterBroker> createBrokers() {
        List<DatacenterBroker> brokers = new ArrayList<>();
        
        // Enterprise broker (prefers high-performance AWS-like)
        DatacenterBroker enterpriseBroker = new DatacenterBrokerSimple(simulation);
        enterpriseBroker.setName("Enterprise-Broker");
        brokers.add(enterpriseBroker);
        
        // Cost-optimized broker (prefers GCP-like)
        DatacenterBroker costOptimizedBroker = new DatacenterBrokerSimple(simulation);
        costOptimizedBroker.setName("Cost-Optimized-Broker");
        brokers.add(costOptimizedBroker);
        
        // Edge computing broker (prefers edge datacenter)
        DatacenterBroker edgeBroker = new DatacenterBrokerSimple(simulation);
        edgeBroker.setName("Edge-Computing-Broker");
        brokers.add(edgeBroker);
        
        // Hybrid broker (uses all datacenters)
        DatacenterBroker hybridBroker = new DatacenterBrokerSimple(simulation);
        hybridBroker.setName("Hybrid-Multi-Cloud-Broker");
        brokers.add(hybridBroker);
        
        System.out.println("✓ Created " + brokers.size() + " specialized brokers");
        return brokers;
    }
    
    /**
     * Creates diverse VM configurations for different use cases
     */
    private List<Vm> createDiverseVms() {
        List<Vm> vms = new ArrayList<>();
        int vmId = 0;
        
        // High-performance VMs for enterprise workloads
        vms.addAll(createVmSet(vmId, 4, 3000, 8192, 2000, 100000, 
                new CloudletSchedulerTimeShared(), "High-Performance"));
        vmId += 4;
        
        // Balanced VMs for general purpose
        vms.addAll(createVmSet(vmId, 4, 2000, 4096, 1500, 50000, 
                new CloudletSchedulerTimeShared(), "General-Purpose"));
        vmId += 4;
        
        // Cost-optimized VMs for batch processing
        vms.addAll(createVmSet(vmId, 4, 1500, 2048, 1000, 25000, 
                new CloudletSchedulerTimeShared(), "Cost-Optimized"));
        vmId += 4;
        
        // Edge VMs for low-latency applications
        vms.addAll(createVmSet(vmId, 4, 1800, 1024, 500, 10000, 
                new CloudletSchedulerTimeShared(), "Edge-Computing"));
        
        System.out.println("✓ Created " + vms.size() + " diverse VMs");
        return vms;
    }
    
    /**
     * Helper method to create a set of VMs with specific characteristics
     */
    private List<Vm> createVmSet(int startId, int count, double mips, long ram, 
                                long bw, long storage, CloudletSchedulerTimeShared scheduler, String type) {
        return IntStream.range(startId, startId + count)
                .mapToObj(id -> new VmSimple(id, mips, 2)
                        .setRam(ram)
                        .setBw(bw)
                        .setSize(storage)
                        .setCloudletScheduler(scheduler)
                        .setDescription(type + "-VM-" + id))
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
    }
    
    /**
     * Creates varied cloudlet workloads
     */
    private List<Cloudlet> createVariedCloudlets() {
        List<Cloudlet> cloudlets = new ArrayList<>();
        int cloudletId = 0;
        
        // CPU-intensive workloads
        cloudlets.addAll(createCloudletSet(cloudletId, 8, 50000, 2, 
                new UtilizationModelDynamic(0.9), "CPU-Intensive"));
        cloudletId += 8;
        
        // Memory-intensive workloads
        cloudlets.addAll(createCloudletSet(cloudletId, 8, 30000, 1, 
                new UtilizationModelDynamic(0.7), "Memory-Intensive"));
        cloudletId += 8;
        
        // I/O-intensive workloads
        cloudlets.addAll(createCloudletSet(cloudletId, 8, 20000, 1, 
                new UtilizationModelDynamic(0.6), "IO-Intensive"));
        cloudletId += 8;
        
        // Edge computing workloads (low latency)
        cloudlets.addAll(createCloudletSet(cloudletId, 8, 10000, 1, 
                new UtilizationModelDynamic(0.3), "Edge-Latency-Sensitive"));
        
        System.out.println("✓ Created " + cloudlets.size() + " varied cloudlets");
        return cloudlets;
    }
    
    /**
     * Helper method to create a set of cloudlets
     */
    private List<Cloudlet> createCloudletSet(int startId, int count, long length, int pesNumber,
                                           UtilizationModelDynamic utilizationModel, String type) {
        return IntStream.range(startId, startId + count)
                .mapToObj(id -> {
                    Cloudlet cloudlet = new CloudletSimple(id, length, pesNumber);
                    cloudlet.setUtilizationModel(utilizationModel);
                    cloudlet.setSizes(2048); // 2GB file size
                    return cloudlet;
                })
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
    }
    
    /**
     * Distributes VMs and cloudlets across brokers based on strategy
     */
    private void distributeResources() {
        // Enterprise broker gets high-performance VMs and CPU-intensive cloudlets
        brokerList.get(0).submitVmList(vmList.subList(0, 4));
        brokerList.get(0).submitCloudletList(cloudletList.subList(0, 8));
        
        // Cost-optimized broker gets cost-optimized VMs and batch cloudlets
        brokerList.get(1).submitVmList(vmList.subList(4, 8));
        brokerList.get(1).submitCloudletList(cloudletList.subList(8, 16));
        
        // Edge broker gets edge VMs and latency-sensitive cloudlets
        brokerList.get(2).submitVmList(vmList.subList(8, 12));
        brokerList.get(2).submitCloudletList(cloudletList.subList(16, 24));
        
        // Hybrid broker gets general-purpose VMs and I/O-intensive cloudlets
        brokerList.get(3).submitVmList(vmList.subList(12, 16));
        brokerList.get(3).submitCloudletList(cloudletList.subList(24, 32));
        
        System.out.println("✓ Distributed resources across " + brokerList.size() + " brokers");
    }
    
    /**
     * Displays comprehensive simulation results
     */
    private void displayResults() {
        System.out.println("\n" + "=".repeat(80));
        System.out.println("MULTI-CLOUD SIMULATION RESULTS");
        System.out.println("=".repeat(80));
        
        for (DatacenterBroker broker : brokerList) {
            System.out.println("\n--- Results for " + broker.getName() + " ---");
            List<Cloudlet> finishedCloudlets = broker.getCloudletFinishedList();
            
            if (!finishedCloudlets.isEmpty()) {
                new CloudletsTableBuilder(finishedCloudlets).build();
                
                // Calculate statistics
                double totalExecutionTime = finishedCloudlets.stream()
                        .mapToDouble(Cloudlet::getActualCpuTime)
                        .sum();
                
                double avgExecutionTime = totalExecutionTime / finishedCloudlets.size();
                
                System.out.printf("Total Execution Time: %.2f seconds%n", totalExecutionTime);
                System.out.printf("Average Execution Time: %.2f seconds%n", avgExecutionTime);
                System.out.printf("Completed Cloudlets: %d%n", finishedCloudlets.size());
            }
        }
        
        // Overall statistics
        System.out.println("\n" + "=".repeat(80));
        System.out.println("OVERALL MULTI-CLOUD STATISTICS");
        System.out.println("=".repeat(80));
        
        long totalCloudlets = brokerList.stream()
                .mapToLong(broker -> broker.getCloudletFinishedList().size())
                .sum();
        
        System.out.printf("Total Datacenters: %d%n", datacenterList.size());
        System.out.printf("Total Brokers: %d%n", brokerList.size());
        System.out.printf("Total VMs: %d%n", vmList.size());
        System.out.printf("Total Completed Cloudlets: %d%n", totalCloudlets);
        
        // Datacenter utilization summary
        System.out.println("\nDatacenter Summary:");
        System.out.println("- AWS-like: High-performance, Premium pricing");
        System.out.println("- Azure-like: Balanced performance and cost");
        System.out.println("- GCP-like: Cost-effective, Batch processing optimized");
        System.out.println("- Edge: Low-latency, Limited resources");
    }
}
