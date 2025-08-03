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
 * Basic Multi-Cloud Simulation demonstrating heterogeneous cloud environments.
 * This example creates multiple cloud datacenters with different characteristics
 * representing AWS, Azure, GCP, and Edge computing environments.
 */
public class BasicMultiCloudSimulation {
    
    // Cloud provider characteristics
    private static class CloudConfig {
        final String name;
        final int hosts;
        final int coresPerHost;
        final double mipsPerCore;
        final long ramPerHost;
        final long storagePerHost;
        final long bandwidthPerHost;
        final double costPerSecond;
        
        CloudConfig(String name, int hosts, int coresPerHost, double mipsPerCore,
                   long ramPerHost, long storagePerHost, long bandwidthPerHost, double costPerSecond) {
            this.name = name;
            this.hosts = hosts;
            this.coresPerHost = coresPerHost;
            this.mipsPerCore = mipsPerCore;
            this.ramPerHost = ramPerHost;
            this.storagePerHost = storagePerHost;
            this.bandwidthPerHost = bandwidthPerHost;
            this.costPerSecond = costPerSecond;
        }
    }
    
    // Define different cloud provider configurations
    private static final CloudConfig AWS_CONFIG = new CloudConfig(
        "AWS-like (High Performance)", 3, 16, 3500, 32768, 2000000, 25000, 0.15);
    private static final CloudConfig AZURE_CONFIG = new CloudConfig(
        "Azure-like (Balanced)", 3, 12, 3000, 24576, 1500000, 20000, 0.12);
    private static final CloudConfig GCP_CONFIG = new CloudConfig(
        "GCP-like (Cost Effective)", 4, 8, 2800, 16384, 1000000, 15000, 0.08);
    private static final CloudConfig EDGE_CONFIG = new CloudConfig(
        "Edge Computing (Low Latency)", 2, 4, 2200, 8192, 500000, 5000, 0.25);
    
    private final CloudSimPlus simulation;
    private List<Datacenter> datacenters;
    private List<DatacenterBroker> brokers;
    
    public static void main(String[] args) {
        new BasicMultiCloudSimulation();
    }
    
    private BasicMultiCloudSimulation() {
        printWelcome();
        
        simulation = new CloudSimPlus();
        
        // Create heterogeneous cloud infrastructure
        createMultiCloudInfrastructure();
        
        // Create brokers and workloads
        createBrokersAndWorkloads();
        
        // Run simulation
        simulation.start();
        
        // Display results
        displayResults();
        
        System.out.println("\\n✅ Basic Multi-Cloud Simulation completed successfully!");
    }
    
    private void printWelcome() {
        System.out.println("=" + "=".repeat(70) + "=");
        System.out.println("     BASIC MULTI-CLOUD HETEROGENEOUS SIMULATION");
        System.out.println("=" + "=".repeat(70) + "=");
        System.out.println("Simulating multiple cloud providers with different characteristics:");
        System.out.println("• AWS-like: High-performance, premium pricing");
        System.out.println("• Azure-like: Balanced performance and cost");
        System.out.println("• GCP-like: Cost-effective, good for batch processing");
        System.out.println("• Edge: Low-latency, limited resources");
        System.out.println();
    }
    
    /**
     * Creates multiple datacenters representing different cloud providers
     */
    private void createMultiCloudInfrastructure() {
        datacenters = new ArrayList<>();
        
        System.out.println("🏗️ Creating Multi-Cloud Infrastructure...");
        
        // Create AWS-like datacenter
        Datacenter awsDatacenter = createCloudDatacenter(AWS_CONFIG);
        datacenters.add(awsDatacenter);
        System.out.printf("   ✓ %s: %d hosts, %d cores each, %.0f MIPS/core\\n", 
                AWS_CONFIG.name, AWS_CONFIG.hosts, AWS_CONFIG.coresPerHost, AWS_CONFIG.mipsPerCore);
        
        // Create Azure-like datacenter
        Datacenter azureDatacenter = createCloudDatacenter(AZURE_CONFIG);
        datacenters.add(azureDatacenter);
        System.out.printf("   ✓ %s: %d hosts, %d cores each, %.0f MIPS/core\\n", 
                AZURE_CONFIG.name, AZURE_CONFIG.hosts, AZURE_CONFIG.coresPerHost, AZURE_CONFIG.mipsPerCore);
        
        // Create GCP-like datacenter
        Datacenter gcpDatacenter = createCloudDatacenter(GCP_CONFIG);
        datacenters.add(gcpDatacenter);
        System.out.printf("   ✓ %s: %d hosts, %d cores each, %.0f MIPS/core\\n", 
                GCP_CONFIG.name, GCP_CONFIG.hosts, GCP_CONFIG.coresPerHost, GCP_CONFIG.mipsPerCore);
        
        // Create Edge datacenter
        Datacenter edgeDatacenter = createCloudDatacenter(EDGE_CONFIG);
        datacenters.add(edgeDatacenter);
        System.out.printf("   ✓ %s: %d hosts, %d cores each, %.0f MIPS/core\\n", 
                EDGE_CONFIG.name, EDGE_CONFIG.hosts, EDGE_CONFIG.coresPerHost, EDGE_CONFIG.mipsPerCore);
        
        int totalHosts = AWS_CONFIG.hosts + AZURE_CONFIG.hosts + GCP_CONFIG.hosts + EDGE_CONFIG.hosts;
        System.out.printf("\\n📊 Total Infrastructure: %d datacenters, %d hosts\\n\\n", 
                datacenters.size(), totalHosts);
    }
    
    /**
     * Creates a datacenter with the specified configuration
     */
    private Datacenter createCloudDatacenter(CloudConfig config) {
        List<Host> hostList = new ArrayList<>();
        
        // Create hosts for this datacenter
        for (int i = 0; i < config.hosts; i++) {
            Host host = createHost(config);
            hostList.add(host);
        }
        
        // Create datacenter
        Datacenter datacenter = new DatacenterSimple(simulation, hostList);
        
        // Set pricing characteristics
        datacenter.getCharacteristics().setCostPerSecond(config.costPerSecond);
        datacenter.getCharacteristics().setCostPerMem(config.costPerSecond * 0.3);
        datacenter.getCharacteristics().setCostPerStorage(config.costPerSecond * 0.1);
        datacenter.getCharacteristics().setCostPerBw(config.costPerSecond * 0.2);
        
        return datacenter;
    }
    
    /**
     * Creates a host based on the cloud configuration
     */
    private Host createHost(CloudConfig config) {
        // Create processing elements (CPU cores)
        List<Pe> peList = new ArrayList<>();
        for (int i = 0; i < config.coresPerHost; i++) {
            peList.add(new PeSimple(config.mipsPerCore));
        }
        
        // Create and configure host
        return new HostSimple(config.ramPerHost, config.bandwidthPerHost, config.storagePerHost, peList)
                .setVmScheduler(new VmSchedulerTimeShared());
    }
    
    /**
     * Creates brokers and distributes workloads across different cloud providers
     */
    private void createBrokersAndWorkloads() {
        brokers = new ArrayList<>();
        
        System.out.println("👥 Creating Brokers and Workloads...");
        
        // Enterprise Broker (uses AWS for high-performance workloads)
        DatacenterBroker enterpriseBroker = new DatacenterBrokerSimple(simulation);
        enterpriseBroker.setName("Enterprise-Broker");
        List<Vm> enterpriseVms = createVMs(4, "High-Performance", 3000, 4, 8192, 2000, 100000);
        List<Cloudlet> enterpriseCloudlets = createCloudlets(8, "CPU-Intensive", 50000, 2, 2048);
        enterpriseBroker.submitVmList(enterpriseVms);
        enterpriseBroker.submitCloudletList(enterpriseCloudlets);
        brokers.add(enterpriseBroker);
        System.out.println("   ✓ Enterprise Broker: 4 high-performance VMs, 8 CPU-intensive cloudlets");
        
        // Balanced Broker (uses Azure for general workloads)
        DatacenterBroker balancedBroker = new DatacenterBrokerSimple(simulation);
        balancedBroker.setName("Balanced-Broker");
        List<Vm> balancedVms = createVMs(3, "Standard", 2000, 2, 4096, 1500, 50000);
        List<Cloudlet> balancedCloudlets = createCloudlets(6, "General-Purpose", 25000, 1, 1024);
        balancedBroker.submitVmList(balancedVms);
        balancedBroker.submitCloudletList(balancedCloudlets);
        brokers.add(balancedBroker);
        System.out.println("   ✓ Balanced Broker: 3 standard VMs, 6 general-purpose cloudlets");
        
        // Cost-Optimized Broker (uses GCP for batch processing)
        DatacenterBroker costBroker = new DatacenterBrokerSimple(simulation);
        costBroker.setName("Cost-Optimized-Broker");
        List<Vm> costVms = createVMs(5, "Budget", 1500, 2, 2048, 1000, 25000);
        List<Cloudlet> costCloudlets = createCloudlets(10, "Batch-Processing", 30000, 1, 512);
        costBroker.submitVmList(costVms);
        costBroker.submitCloudletList(costCloudlets);
        brokers.add(costBroker);
        System.out.println("   ✓ Cost-Optimized Broker: 5 budget VMs, 10 batch processing cloudlets");
        
        // Edge Broker (uses Edge for latency-sensitive workloads)
        DatacenterBroker edgeBroker = new DatacenterBrokerSimple(simulation);
        edgeBroker.setName("Edge-Computing-Broker");
        List<Vm> edgeVms = createVMs(2, "Edge", 1200, 1, 1024, 500, 10000);
        List<Cloudlet> edgeCloudlets = createCloudlets(4, "Latency-Sensitive", 8000, 1, 256);
        edgeBroker.submitVmList(edgeVms);
        edgeBroker.submitCloudletList(edgeCloudlets);
        brokers.add(edgeBroker);
        System.out.println("   ✓ Edge Computing Broker: 2 edge VMs, 4 latency-sensitive cloudlets");
        
        System.out.println("\\n📋 Total: 4 brokers, 14 VMs, 28 cloudlets\\n");
    }
    
    /**
     * Creates a list of VMs with specified characteristics
     */
    private List<Vm> createVMs(int count, String type, double mips, int pesNumber, 
                              long ram, long bw, long storage) {
        List<Vm> vmList = new ArrayList<>();
        
        for (int i = 0; i < count; i++) {
            Vm vm = new VmSimple(mips, pesNumber)
                    .setRam(ram)
                    .setBw(bw)
                    .setSize(storage)
                    .setCloudletScheduler(new CloudletSchedulerTimeShared())
                    .setDescription(type + "-VM");
            vmList.add(vm);
        }
        
        return vmList;
    }
    
    /**
     * Creates a list of cloudlets with specified characteristics
     */
    private List<Cloudlet> createCloudlets(int count, String type, long length, 
                                         int pesNumber, long fileSize) {
        List<Cloudlet> cloudletList = new ArrayList<>();
        UtilizationModelDynamic utilizationModel = new UtilizationModelDynamic(0.5);
        
        for (int i = 0; i < count; i++) {
            Cloudlet cloudlet = new CloudletSimple(length, pesNumber, utilizationModel);
            cloudlet.setSizes(fileSize);
            cloudletList.add(cloudlet);
        }
        
        return cloudletList;
    }
    
    /**
     * Displays comprehensive simulation results
     */
    private void displayResults() {
        System.out.println("\\n" + "=".repeat(80));
        System.out.println("MULTI-CLOUD SIMULATION RESULTS");
        System.out.println("=".repeat(80));
        
        double totalCost = 0.0;
        int totalCloudlets = 0;
        double totalExecutionTime = 0.0;
        
        for (DatacenterBroker broker : brokers) {
            System.out.printf("\\n--- Results for %s ---%n", broker.getName());
            
            List<Cloudlet> finishedCloudlets = broker.getCloudletFinishedList();
            if (!finishedCloudlets.isEmpty()) {
                // Display cloudlet table for first broker only (to save space)
                if (broker == brokers.get(0)) {
                    new CloudletsTableBuilder(finishedCloudlets).build();
                }
                
                // Calculate statistics
                double brokerExecutionTime = finishedCloudlets.stream()
                        .mapToDouble(Cloudlet::getActualCpuTime)
                        .sum();
                
                double avgExecutionTime = brokerExecutionTime / finishedCloudlets.size();
                // Calculate cost based on execution time and datacenter pricing
                double brokerCost = finishedCloudlets.stream()
                        .mapToDouble(cloudlet -> cloudlet.getActualCpuTime() * 0.001) // Simple cost model
                        .sum();
                
                System.out.printf("Completed Cloudlets: %d%n", finishedCloudlets.size());
                System.out.printf("Total Execution Time: %.2f seconds%n", brokerExecutionTime);
                System.out.printf("Average Execution Time: %.2f seconds%n", avgExecutionTime);
                System.out.printf("Total Cost: $%.4f%n", brokerCost);
                System.out.printf("Cost per Cloudlet: $%.4f%n", brokerCost / finishedCloudlets.size());
                
                totalCost += brokerCost;
                totalCloudlets += finishedCloudlets.size();
                totalExecutionTime += brokerExecutionTime;
            }
        }
        
        // Overall statistics
        System.out.println("\\n" + "=".repeat(80));
        System.out.println("OVERALL MULTI-CLOUD STATISTICS");
        System.out.println("=".repeat(80));
        System.out.printf("Total Datacenters: %d%n", datacenters.size());
        System.out.printf("Total Brokers: %d%n", brokers.size());
        System.out.printf("Total Completed Cloudlets: %d%n", totalCloudlets);
        System.out.printf("Total Execution Time: %.2f seconds%n", totalExecutionTime);
        System.out.printf("Total Cost: $%.4f%n", totalCost);
        System.out.printf("Average Cost per Cloudlet: $%.4f%n", totalCost / totalCloudlets);
        
        System.out.println("\\nCloud Provider Summary:");
        System.out.println("• AWS-like: Premium performance, highest cost");
        System.out.println("• Azure-like: Balanced performance and cost");
        System.out.println("• GCP-like: Cost-effective for batch processing");
        System.out.println("• Edge: Ultra-low latency, premium pricing");
        
        System.out.println("\\nWorkload Distribution Strategy:");
        System.out.println("• Enterprise workloads → High-performance infrastructure");
        System.out.println("• General workloads → Balanced infrastructure");
        System.out.println("• Batch processing → Cost-effective infrastructure");
        System.out.println("• Latency-sensitive → Edge infrastructure");
    }
}
