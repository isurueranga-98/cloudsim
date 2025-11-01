package com.example;

import com.example.multicloud.CloudProvider;
import com.example.multicloud.MultiCloudBroker;
import com.example.multicloud.MultiCloudResourceManager;
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
import org.cloudsimplus.schedulers.cloudlet.CloudletSchedulerSpaceShared;
import org.cloudsimplus.schedulers.cloudlet.CloudletSchedulerTimeShared;
import org.cloudsimplus.schedulers.vm.VmSchedulerTimeShared;
import org.cloudsimplus.utilizationmodels.UtilizationModelDynamic;
import org.cloudsimplus.vms.Vm;
import org.cloudsimplus.vms.VmSimple;
import org.cloudsimplus.allocationpolicies.VmAllocationPolicySimple;

import java.util.*;
import java.util.stream.IntStream;

/**
 * Advanced Multi-Cloud Heterogeneous Simulation
 * 
 * This comprehensive simulation demonstrates:
 * 1. Multiple heterogeneous cloud providers (AWS, Azure, GCP, Edge, Alibaba, IBM)
 * 2. Intelligent resource allocation strategies
 * 3. Cost optimization and performance analysis
 * 4. Different workload types and VM configurations
 * 5. Real-world cloud provider characteristics
 */
public class AdvancedMultiCloudSimulation {
    
    // Simulation configuration
    private static final int TOTAL_VMS = 24;
    private static final int TOTAL_CLOUDLETS = 48;
    private static final double SIMULATION_TIME = 7200; // 2 hours in seconds
    
    // Cloud provider host configurations
    private static final Map<CloudProvider.ProviderType, Integer> HOST_COUNTS = Map.of(
        CloudProvider.ProviderType.AWS, 4,
        CloudProvider.ProviderType.AZURE, 3,
        CloudProvider.ProviderType.GCP, 5,
        CloudProvider.ProviderType.EDGE, 2,
        CloudProvider.ProviderType.ALIBABA, 3,
        CloudProvider.ProviderType.IBM, 3
    );
    
    // Simulation components
    private final CloudSimPlus simulation;
    private List<CloudProvider> cloudProviders;
    
    public static void main(String[] args) {
        new AdvancedMultiCloudSimulation();
    }
    
    private AdvancedMultiCloudSimulation() {
        printHeader();
        
        // Initialize simulation
        simulation = new CloudSimPlus();
        
        // Create heterogeneous cloud infrastructure
        createCloudInfrastructure();
        
        // Run multiple optimization scenarios
        runOptimizationScenarios();
        
        System.out.println("\n🎉 Advanced Multi-Cloud Simulation completed successfully!");
    }
    
    private void printHeader() {
        System.out.println("╔" + "═".repeat(78) + "╗");
        System.out.println("║" + " ".repeat(15) + "ADVANCED MULTI-CLOUD HETEROGENEOUS SIMULATION" + " ".repeat(15) + "║");
        System.out.println("║" + " ".repeat(78) + "║");
        System.out.println("║  Simulating: AWS, Azure, GCP, Edge, Alibaba Cloud, IBM Cloud" + " ".repeat(14) + "║");
        System.out.println("║  Features: Intelligent allocation, Cost optimization, Performance analysis  ║");
        System.out.println("╚" + "═".repeat(78) + "╝");
        System.out.println();
    }
    
    /**
     * Creates comprehensive cloud infrastructure with multiple providers
     */
    private void createCloudInfrastructure() {
        cloudProviders = new ArrayList<>();
        
        System.out.println("🏗️  Creating Cloud Infrastructure...");
        
        // Create each cloud provider
        for (CloudProvider.ProviderType type : CloudProvider.ProviderType.values()) {
            Datacenter datacenter = createProviderDatacenter(type);
            CloudProvider provider = new CloudProvider(type, datacenter);
            cloudProviders.add(provider);
            
            System.out.printf("   ✓ %s: %d hosts, Latency: %.1fms, Performance: %d/100%n",
                    type.getFullName(),
                    HOST_COUNTS.get(type),
                    provider.getNetworkLatency(),
                    provider.getPerformanceScore());
        }
        
        System.out.printf("📊 Total Infrastructure: %d providers, %d hosts%n%n", 
                cloudProviders.size(), 
                HOST_COUNTS.values().stream().mapToInt(Integer::intValue).sum());
    }
    
    /**
     * Creates a datacenter for a specific cloud provider
     */
    private Datacenter createProviderDatacenter(CloudProvider.ProviderType providerType) {
        List<Host> hostList = new ArrayList<>();
        int hostCount = HOST_COUNTS.get(providerType);
        
        for (int i = 0; i < hostCount; i++) {
            Host host = createProviderSpecificHost(providerType, i);
            hostList.add(host);
        }
        
        // Choose allocation policy based on provider characteristics
        Datacenter datacenter = new DatacenterSimple(simulation, hostList, 
                getOptimalAllocationPolicy(providerType));
        
        // Set provider-specific pricing
        setProviderPricing(datacenter, providerType);
        
        return datacenter;
    }
    
    /**
     * Creates host with provider-specific characteristics
     */
    private Host createProviderSpecificHost(CloudProvider.ProviderType type, int hostId) {
        HostConfiguration config = getHostConfiguration(type);
        
        List<Pe> peList = new ArrayList<>();
        for (int i = 0; i < config.coreCount; i++) {
            peList.add(new PeSimple(config.mipsPerCore, new PeProvisionerSimple()));
        }
        
        return new HostSimple(config.ram, config.bandwidth, config.storage, peList)
                .setRamProvisioner(new ResourceProvisionerSimple())
                .setBwProvisioner(new ResourceProvisionerSimple())
                .setVmScheduler(new VmSchedulerTimeShared());
    }
    
    /**
     * Host configuration for different providers
     */
    private HostConfiguration getHostConfiguration(CloudProvider.ProviderType type) {
        switch (type) {
            case AWS:
                return new HostConfiguration(16, 3500, 65536, 25000, 4000000); // Premium
            case AZURE:
                return new HostConfiguration(12, 3000, 49152, 20000, 3000000); // Balanced
            case GCP:
                return new HostConfiguration(8, 2800, 32768, 15000, 2000000);  // Cost-effective
            case EDGE:
                return new HostConfiguration(4, 2200, 16384, 5000, 1000000);   // Limited
            case ALIBABA:
                return new HostConfiguration(10, 2600, 24576, 12000, 1500000); // Competitive
            case IBM:
                return new HostConfiguration(14, 3200, 57344, 22000, 3500000); // Enterprise
            default:
                return new HostConfiguration(8, 2400, 16384, 10000, 1000000);
        }
    }
    
    private static class HostConfiguration {
        final int coreCount;
        final double mipsPerCore;
        final long ram;
        final long bandwidth;
        final long storage;
        
        HostConfiguration(int coreCount, double mipsPerCore, long ram, long bandwidth, long storage) {
            this.coreCount = coreCount;
            this.mipsPerCore = mipsPerCore;
            this.ram = ram;
            this.bandwidth = bandwidth;
            this.storage = storage;
        }
    }
    
    /**
     * Gets optimal VM allocation policy for each provider
     */
    private VmAllocationPolicySimple getOptimalAllocationPolicy(CloudProvider.ProviderType type) {
        // All providers use the simple allocation policy for compatibility
        return new VmAllocationPolicySimple();
    }
    
    /**
     * Sets provider-specific pricing models
     */
    private void setProviderPricing(Datacenter datacenter, CloudProvider.ProviderType type) {
        switch (type) {
            case AWS:
                datacenter.getCharacteristics().setCostPerSecond(0.15);
                datacenter.getCharacteristics().setCostPerMem(0.05);
                break;
            case AZURE:
                datacenter.getCharacteristics().setCostPerSecond(0.12);
                datacenter.getCharacteristics().setCostPerMem(0.04);
                break;
            case GCP:
                datacenter.getCharacteristics().setCostPerSecond(0.08);
                datacenter.getCharacteristics().setCostPerMem(0.03);
                break;
            case EDGE:
                datacenter.getCharacteristics().setCostPerSecond(0.25);
                datacenter.getCharacteristics().setCostPerMem(0.08);
                break;
            case ALIBABA:
                datacenter.getCharacteristics().setCostPerSecond(0.07);
                datacenter.getCharacteristics().setCostPerMem(0.025);
                break;
            case IBM:
                datacenter.getCharacteristics().setCostPerSecond(0.14);
                datacenter.getCharacteristics().setCostPerMem(0.045);
                break;
        }
    }
    
    /**
     * Creates diverse VM fleet with different configurations
     */
    private List<Vm> createDiverseVmFleet() {
        List<Vm> vms = new ArrayList<>();
        int vmId = 0;
        
        System.out.println("🖥️  Creating Diverse VM Fleet...");
        
        // High-performance VMs (25% of fleet)
        vms.addAll(createVmCategory(vmId, 6, "High-Performance", 
                4000, 4, 16384, 5000, 200000));
        vmId += 6;
        
        // Standard VMs (35% of fleet)
        vms.addAll(createVmCategory(vmId, 8, "Standard", 
                2500, 2, 8192, 3000, 100000));
        vmId += 8;
        
        // Burstable VMs (25% of fleet)
        vms.addAll(createVmCategory(vmId, 6, "Burstable", 
                1800, 2, 4096, 2000, 50000));
        vmId += 6;
        
        // Micro VMs for edge (15% of fleet)
        vms.addAll(createVmCategory(vmId, 4, "Micro-Edge", 
                1200, 1, 2048, 1000, 25000));
        
        System.out.printf("   ✓ Created %d/%d VMs across 4 categories%n%n", vms.size(), TOTAL_VMS);
        return vms;
    }
    
    private List<Vm> createVmCategory(int startId, int count, String category,
                                    double mips, int pesNumber, long ram, 
                                    long bw, long storage) {
        return IntStream.range(startId, startId + count)
                .mapToObj(id -> new VmSimple(id, mips, pesNumber)
                        .setRam(ram)
                        .setBw(bw)
                        .setSize(storage)
                        .setCloudletScheduler(pesNumber > 2 ? 
                                new CloudletSchedulerTimeShared() : 
                                new CloudletSchedulerSpaceShared())
                        .setDescription(category + "-VM"))
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
    }
    
    /**
     * Creates varied workloads representing different application types
     */
    private List<Cloudlet> createVariedWorkloads() {
        List<Cloudlet> cloudlets = new ArrayList<>();
        int cloudletId = 0;
        
        System.out.println("📋 Creating Varied Workloads...");
        
        // Web application workloads (30%)
        cloudlets.addAll(createWorkloadCategory(cloudletId, 14, "Web-Application",
                15000, 1, 1024, new UtilizationModelDynamic(0.4)));
        cloudletId += 14;
        
        // Data processing workloads (25%)
        cloudlets.addAll(createWorkloadCategory(cloudletId, 12, "Data-Processing",
                80000, 4, 4096, new UtilizationModelDynamic(0.9)));
        cloudletId += 12;
        
        // Machine learning workloads (20%)
        cloudlets.addAll(createWorkloadCategory(cloudletId, 10, "Machine-Learning",
                120000, 8, 8192, new UtilizationModelDynamic(0.8)));
        cloudletId += 10;
        
        // IoT/Edge workloads (15%)
        cloudlets.addAll(createWorkloadCategory(cloudletId, 7, "IoT-Edge",
                5000, 1, 512, new UtilizationModelDynamic(0.2)));
        cloudletId += 7;
        
        // Batch processing workloads (10%)
        cloudlets.addAll(createWorkloadCategory(cloudletId, 5, "Batch-Processing",
                200000, 2, 2048, new UtilizationModelDynamic(0.7)));
        
        System.out.printf("   ✓ Created %d/%d cloudlets across 5 workload types%n%n", cloudlets.size(), TOTAL_CLOUDLETS);
        return cloudlets;
    }
    
    private List<Cloudlet> createWorkloadCategory(int startId, int count, String category,
                                                long length, int pesNumber, long fileSize,
                                                UtilizationModelDynamic utilizationModel) {
        return IntStream.range(startId, startId + count)
                .mapToObj(id -> {
                    Cloudlet cloudlet = new CloudletSimple(id, length, pesNumber);
                    cloudlet.setFileSize(fileSize);
                    cloudlet.setOutputSize(fileSize / 2);
                    cloudlet.setUtilizationModel(utilizationModel);
                    return cloudlet;
                })
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
    }
    
    /**
     * Runs multiple optimization scenarios to compare strategies
     */
    private void runOptimizationScenarios() {
        MultiCloudResourceManager.OptimizationStrategy[] strategies = {
            MultiCloudResourceManager.OptimizationStrategy.COST_MINIMIZATION,
            MultiCloudResourceManager.OptimizationStrategy.PERFORMANCE_MAXIMIZATION,
            MultiCloudResourceManager.OptimizationStrategy.BALANCED,
            MultiCloudResourceManager.OptimizationStrategy.LATENCY_OPTIMIZATION
        };
        
        System.out.println("🔄 Running Optimization Scenarios...");
        
        for (MultiCloudResourceManager.OptimizationStrategy strategy : strategies) {
            System.out.printf("%n--- Scenario: %s ---%n", strategy.name());
            runSingleScenario(strategy);
        }
    }
    
    /**
     * Runs a single optimization scenario
     */
    private void runSingleScenario(MultiCloudResourceManager.OptimizationStrategy strategy) {
        CloudSimPlus scenarioSimulation = new CloudSimPlus();
        List<CloudProvider> scenarioProviders = recreateProvidersForScenario(scenarioSimulation);

        MultiCloudResourceManager scenarioResourceManager = new MultiCloudResourceManager(scenarioProviders);
        scenarioResourceManager.setCurrentStrategy(strategy);

        MultiCloudBroker broker = new MultiCloudBroker(scenarioSimulation);
        broker.registerProviders(scenarioProviders);

        List<Vm> scenarioVms = createFreshVmCopies();
        List<Cloudlet> scenarioCloudlets = createFreshCloudletCopies();

        Map<CloudProvider, List<Vm>> vmAllocation = scenarioResourceManager.allocateVms(scenarioVms);
        Map<CloudProvider, List<Cloudlet>> cloudletAllocation = scenarioResourceManager.allocateCloudlets(scenarioCloudlets);

        broker.prepareVmAllocations(vmAllocation);
        broker.submitVmList(scenarioVms);
        broker.submitCloudletList(scenarioCloudlets);
        broker.bindCloudlets(cloudletAllocation);

        scenarioSimulation.start();

        analyzeScenarioResults(strategy, scenarioResourceManager, broker, vmAllocation, cloudletAllocation);
    }
    
    /**
     * Recreates cloud providers for a new scenario simulation
     */
    private List<CloudProvider> recreateProvidersForScenario(CloudSimPlus scenarioSimulation) {
        List<CloudProvider> scenarioProviders = new ArrayList<>();
        
        // Create each cloud provider for the scenario
        for (CloudProvider.ProviderType type : CloudProvider.ProviderType.values()) {
            Datacenter datacenter = createProviderDatacenterForScenario(type, scenarioSimulation);
            CloudProvider provider = new CloudProvider(type, datacenter);
            scenarioProviders.add(provider);
        }
        
        return scenarioProviders;
    }
    
    /**
     * Creates a datacenter for a specific cloud provider in a scenario
     */
    private Datacenter createProviderDatacenterForScenario(CloudProvider.ProviderType providerType, CloudSimPlus sim) {
        List<Host> hostList = new ArrayList<>();
        int hostCount = HOST_COUNTS.get(providerType);
        
        for (int i = 0; i < hostCount; i++) {
            Host host = createProviderSpecificHost(providerType, i);
            hostList.add(host);
        }
        
        // Choose allocation policy based on provider characteristics
        Datacenter datacenter = new DatacenterSimple(sim, hostList, 
                getOptimalAllocationPolicy(providerType));
        
        // Set provider-specific pricing
        setProviderPricing(datacenter, providerType);
        
        return datacenter;
    }
    
    /**
     * Creates fresh copies of VMs for a scenario
     */
    private List<Vm> createFreshVmCopies() {
        return createDiverseVmFleet();
    }
    
    /**
     * Creates fresh copies of cloudlets for a scenario
     */
    private List<Cloudlet> createFreshCloudletCopies() {
        return createVariedWorkloads();
    }
    
    /**
     * Analyzes and displays results for a scenario
     */
    private void analyzeScenarioResults(MultiCloudResourceManager.OptimizationStrategy strategy,
                                      MultiCloudResourceManager scenarioResourceManager,
                                      MultiCloudBroker broker,
                                      Map<CloudProvider, List<Vm>> vmAllocation,
                                      Map<CloudProvider, List<Cloudlet>> cloudletAllocation) {

        System.out.printf("📈 Analysis for %s Strategy:%n", strategy.name());

        List<Cloudlet> allFinishedCloudlets = broker.getCloudletFinishedList();
        Map<CloudProvider.ProviderType, List<Cloudlet>> cloudletsByProvider = broker.getFinishedCloudletsByProvider();

        Map<CloudProvider.ProviderType, CloudProvider> providerLookup = new EnumMap<>(CloudProvider.ProviderType.class);
        scenarioResourceManager.getCloudProviders()
                .forEach(provider -> providerLookup.put(provider.getType(), provider));

        if (!allFinishedCloudlets.isEmpty()) {
            double totalExecutionTime = allFinishedCloudlets.stream()
                    .mapToDouble(Cloudlet::getActualCpuTime)
                    .sum();

            double avgExecutionTime = totalExecutionTime / allFinishedCloudlets.size();
            double totalCost = scenarioResourceManager.calculateTotalCost(vmAllocation, cloudletAllocation, SIMULATION_TIME);

            System.out.printf("   • Total Cloudlets Completed: %d%n", allFinishedCloudlets.size());
            System.out.printf("   • Average Execution Time: %.2f seconds%n", avgExecutionTime);
            System.out.printf("   • Total Cost: $%.2f%n", totalCost);
            System.out.printf("   • Cost per Cloudlet: $%.4f%n", totalCost / allFinishedCloudlets.size());

            System.out.println("   • Provider Distribution:");
            for (CloudProvider.ProviderType type : CloudProvider.ProviderType.values()) {
                if (!providerLookup.containsKey(type)) {
                    continue;
                }
                int count = cloudletsByProvider.getOrDefault(type, Collections.emptyList()).size();
                System.out.printf("     - %s: %d cloudlets%n", type.name(), count);
            }
        }

        System.out.println(scenarioResourceManager.generateOptimizationReport(vmAllocation, cloudletAllocation));

        if (strategy == MultiCloudResourceManager.OptimizationStrategy.COST_MINIMIZATION &&
            !allFinishedCloudlets.isEmpty()) {
            System.out.println("📊 Detailed Cloudlet Execution Results:");
            new CloudletsTableBuilder(allFinishedCloudlets.subList(0,
                    Math.min(10, allFinishedCloudlets.size()))).build();
        }
    }
}
