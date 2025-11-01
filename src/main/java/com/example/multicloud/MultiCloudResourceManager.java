package com.example.multicloud;

import org.cloudsimplus.cloudlets.Cloudlet;
import org.cloudsimplus.vms.Vm;
import org.cloudsimplus.datacenters.Datacenter;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Multi-Cloud Resource Manager handles intelligent resource allocation and management
 * across multiple heterogeneous cloud providers. It implements various optimization
 * strategies for different objectives like cost minimization, performance maximization,
 * or latency optimization.
 */
public class MultiCloudResourceManager {
    
    public enum OptimizationStrategy {
        COST_MINIMIZATION("Minimize total cost across all providers"),
        PERFORMANCE_MAXIMIZATION("Maximize overall performance"),
        LATENCY_OPTIMIZATION("Minimize network latency"),
        BALANCED("Balance cost, performance, and latency"),
        GEOGRAPHIC_DISTRIBUTION("Distribute workload geographically"),
        LOAD_BALANCING("Balance load across all providers");
        
        private final String description;
        
        OptimizationStrategy(String description) {
            this.description = description;
        }
        
        public String getDescription() { return description; }
    }
    
    private final List<CloudProvider> cloudProviders;
    private OptimizationStrategy currentStrategy;
    private final Map<String, Double> strategyWeights;
    
    public MultiCloudResourceManager(List<CloudProvider> cloudProviders) {
        this.cloudProviders = new ArrayList<>(cloudProviders);
        this.currentStrategy = OptimizationStrategy.BALANCED;
        this.strategyWeights = initializeStrategyWeights();
    }
    
    private Map<String, Double> initializeStrategyWeights() {
        Map<String, Double> weights = new HashMap<>();
        weights.put("cost", 0.4);
        weights.put("performance", 0.3);
        weights.put("latency", 0.2);
        weights.put("reliability", 0.1);
        return weights;
    }
    
    /**
     * Intelligently allocate VMs to cloud providers based on the current strategy
     */
    public Map<CloudProvider, List<Vm>> allocateVms(List<Vm> vms) {
        Map<CloudProvider, List<Vm>> allocation = new HashMap<>();
        
        // Initialize allocation map
        cloudProviders.forEach(provider -> allocation.put(provider, new ArrayList<>()));

        Map<CloudProvider, Integer> providerCapacity = computeProviderPeCapacity();
        Map<CloudProvider, Integer> usedPes = initializeUsedPes();
        
        switch (currentStrategy) {
            case COST_MINIMIZATION:
                allocateVmsByCost(vms, allocation, providerCapacity, usedPes);
                break;
            case PERFORMANCE_MAXIMIZATION:
                allocateVmsByPerformance(vms, allocation, providerCapacity, usedPes);
                break;
            case LATENCY_OPTIMIZATION:
                allocateVmsByLatency(vms, allocation, providerCapacity, usedPes);
                break;
            case BALANCED:
                allocateVmsBalanced(vms, allocation, providerCapacity, usedPes);
                break;
            case GEOGRAPHIC_DISTRIBUTION:
                allocateVmsGeographically(vms, allocation, providerCapacity, usedPes);
                break;
            case LOAD_BALANCING:
                allocateVmsLoadBalanced(vms, allocation, providerCapacity, usedPes);
                break;
        }
        
        return allocation;
    }
    
    private void allocateVmsByCost(List<Vm> vms,
                                   Map<CloudProvider, List<Vm>> allocation,
                                   Map<CloudProvider, Integer> capacity,
                                   Map<CloudProvider, Integer> usedPes) {
        // Sort providers by cost efficiency (highest first)
        List<CloudProvider> sortedProviders = cloudProviders.stream()
                .sorted((p1, p2) -> Integer.compare(p2.getCostEfficiencyScore(), p1.getCostEfficiencyScore()))
                .collect(Collectors.toList());
        
        distributeVmsByPriority(vms, sortedProviders, allocation, capacity, usedPes);
    }
    
    private void allocateVmsByPerformance(List<Vm> vms,
                                          Map<CloudProvider, List<Vm>> allocation,
                                          Map<CloudProvider, Integer> capacity,
                                          Map<CloudProvider, Integer> usedPes) {
        // Sort providers by performance score (highest first)
        List<CloudProvider> sortedProviders = cloudProviders.stream()
                .sorted((p1, p2) -> Integer.compare(p2.getPerformanceScore(), p1.getPerformanceScore()))
                .collect(Collectors.toList());
        
        // Allocate high-performance VMs to high-performance providers
        List<Vm> sortedVms = vms.stream()
                .sorted((v1, v2) -> Double.compare(v2.getMips(), v1.getMips()))
                .collect(Collectors.toList());

        distributeVmsByPriority(sortedVms, sortedProviders, allocation, capacity, usedPes);
    }
    
    private void allocateVmsByLatency(List<Vm> vms,
                                      Map<CloudProvider, List<Vm>> allocation,
                                      Map<CloudProvider, Integer> capacity,
                                      Map<CloudProvider, Integer> usedPes) {
        // Sort providers by latency (lowest first)
        List<CloudProvider> sortedProviders = cloudProviders.stream()
                .sorted(Comparator.comparingDouble(CloudProvider::getNetworkLatency))
                .collect(Collectors.toList());

        // Sort VMs so lighter workloads are allocated to low-capacity providers first
        List<Vm> sortedVms = vms.stream()
                .sorted(Comparator.comparingDouble(Vm::getMips))
                .collect(Collectors.toList());

        distributeVmsByPriority(sortedVms, sortedProviders, allocation, capacity, usedPes);
    }
    
    private void allocateVmsBalanced(List<Vm> vms,
                                     Map<CloudProvider, List<Vm>> allocation,
                                     Map<CloudProvider, Integer> capacity,
                                     Map<CloudProvider, Integer> usedPes) {
        // Calculate composite score for each provider
        Map<CloudProvider, Double> providerScores = new HashMap<>();
        
        for (CloudProvider provider : cloudProviders) {
            double score = 
                (provider.getCostEfficiencyScore() * strategyWeights.get("cost")) +
                (provider.getPerformanceScore() * strategyWeights.get("performance")) +
                ((100 - provider.getNetworkLatency()) * strategyWeights.get("latency")) +
                (provider.getSlaUptime() * strategyWeights.get("reliability"));
            
            providerScores.put(provider, score);
        }
        
        // Sort providers by composite score
        List<CloudProvider> sortedProviders = cloudProviders.stream()
                .sorted((p1, p2) -> Double.compare(providerScores.get(p2), providerScores.get(p1)))
                .collect(Collectors.toList());
        
        // Distribute VMs proportionally based on scores
        double totalScore = providerScores.values().stream().mapToDouble(Double::doubleValue).sum();
        int vmIndex = 0;

        for (CloudProvider provider : sortedProviders) {
            double providerShare = providerScores.get(provider) / totalScore;
            int vmCount = Math.max(1, (int) (vms.size() * providerShare));
            
            for (int i = 0; i < vmCount && vmIndex < vms.size(); i++) {
                Vm vm = vms.get(vmIndex++);
                if (!addVmWithCapacity(provider, vm, allocation, capacity, usedPes)) {
                    CloudProvider fallback = findProviderWithCapacity(sortedProviders, vm, capacity, usedPes, provider);
                    if (fallback != null) {
                        addVmWithCapacity(fallback, vm, allocation, capacity, usedPes);
                    }
                }
            }
        }
        
        // Allocate remaining VMs to best provider
        while (vmIndex < vms.size()) {
            Vm vm = vms.get(vmIndex++);
            CloudProvider bestProvider = findProviderWithCapacity(sortedProviders, vm, capacity, usedPes, null);
            if (bestProvider != null) {
                addVmWithCapacity(bestProvider, vm, allocation, capacity, usedPes);
            }
        }
    }
    
    private void allocateVmsGeographically(List<Vm> vms,
                                           Map<CloudProvider, List<Vm>> allocation,
                                           Map<CloudProvider, Integer> capacity,
                                           Map<CloudProvider, Integer> usedPes) {
        // Distribute evenly across all providers for geographic diversity
        distributeVmsByPriority(vms, cloudProviders, allocation, capacity, usedPes);
    }
    
    private void allocateVmsLoadBalanced(List<Vm> vms,
                                         Map<CloudProvider, List<Vm>> allocation,
                                         Map<CloudProvider, Integer> capacity,
                                         Map<CloudProvider, Integer> usedPes) {
        // Simple round-robin allocation for load balancing
        distributeVmsByPriority(vms, cloudProviders, allocation, capacity, usedPes);
    }

    private void distributeVmsByPriority(List<Vm> vms,
                                          List<CloudProvider> priorityProviders,
                                          Map<CloudProvider, List<Vm>> allocation,
                                          Map<CloudProvider, Integer> capacity,
                                          Map<CloudProvider, Integer> usedPes) {
        for (Vm vm : vms) {
            CloudProvider provider = findProviderWithCapacity(priorityProviders, vm, capacity, usedPes, null);
            if (provider != null) {
                addVmWithCapacity(provider, vm, allocation, capacity, usedPes);
            }
        }
    }

    private boolean addVmWithCapacity(CloudProvider provider,
                                      Vm vm,
                                      Map<CloudProvider, List<Vm>> allocation,
                                      Map<CloudProvider, Integer> capacity,
                                      Map<CloudProvider, Integer> usedPes) {
        if (provider == null) {
            return false;
        }

        int available = capacity.getOrDefault(provider, 0) - usedPes.getOrDefault(provider, 0);
        int requiredPes = (int) vm.getPesNumber();
        if (available < requiredPes) {
            return false;
        }

        allocation.get(provider).add(vm);
        usedPes.merge(provider, requiredPes, Integer::sum);
        return true;
    }

    private CloudProvider findProviderWithCapacity(List<CloudProvider> providers,
                                                   Vm vm,
                                                   Map<CloudProvider, Integer> capacity,
                                                   Map<CloudProvider, Integer> usedPes,
                                                   CloudProvider exclude) {
        for (CloudProvider provider : providers) {
            if (exclude != null && provider.equals(exclude)) {
                continue;
            }
            int available = capacity.getOrDefault(provider, 0) - usedPes.getOrDefault(provider, 0);
            int requiredPes = (int) vm.getPesNumber();
            if (available >= requiredPes) {
                return provider;
            }
        }
        return null;
    }

    private Map<CloudProvider, Integer> computeProviderPeCapacity() {
        Map<CloudProvider, Integer> capacity = new HashMap<>();
        for (CloudProvider provider : cloudProviders) {
            Datacenter datacenter = provider.getDatacenter();
        int totalPes = datacenter.getHostList().stream()
            .mapToInt(host -> host.getPeList().size())
                    .sum();
            capacity.put(provider, totalPes);
        }
        return capacity;
    }

    private Map<CloudProvider, Integer> initializeUsedPes() {
        Map<CloudProvider, Integer> usedPes = new HashMap<>();
        cloudProviders.forEach(provider -> usedPes.put(provider, 0));
        return usedPes;
    }
    
    /**
     * Intelligently allocate cloudlets based on their characteristics and requirements
     */
    public Map<CloudProvider, List<Cloudlet>> allocateCloudlets(List<Cloudlet> cloudlets) {
        Map<CloudProvider, List<Cloudlet>> allocation = new HashMap<>();
        cloudProviders.forEach(provider -> allocation.put(provider, new ArrayList<>()));
        
        // Categorize cloudlets by their characteristics
        List<Cloudlet> cpuIntensive = new ArrayList<>();
        List<Cloudlet> ioIntensive = new ArrayList<>();
        List<Cloudlet> latencySensitive = new ArrayList<>();
        List<Cloudlet> batchProcessing = new ArrayList<>();
        
        for (Cloudlet cloudlet : cloudlets) {
            if (cloudlet.getLength() > 40000) {
                cpuIntensive.add(cloudlet);
            } else if (cloudlet.getFileSize() > 2048) {
                ioIntensive.add(cloudlet);
            } else if (cloudlet.getLength() < 15000) {
                latencySensitive.add(cloudlet);
            } else {
                batchProcessing.add(cloudlet);
            }
        }
        
        // Allocate based on provider strengths
        allocateCloudletsByType(cpuIntensive, allocation, "high_performance");
        allocateCloudletsByType(ioIntensive, allocation, "balanced");
        allocateCloudletsByType(latencySensitive, allocation, "low_latency");
        allocateCloudletsByType(batchProcessing, allocation, "cost_effective");
        
        return allocation;
    }
    
    private void allocateCloudletsByType(List<Cloudlet> cloudlets, 
                                       Map<CloudProvider, List<Cloudlet>> allocation, 
                                       String preferredType) {
        List<CloudProvider> preferredProviders = getProvidersForType(preferredType);
        
        int providerIndex = 0;
        for (Cloudlet cloudlet : cloudlets) {
            CloudProvider provider = preferredProviders.get(providerIndex % preferredProviders.size());
            allocation.get(provider).add(cloudlet);
            providerIndex++;
        }
    }
    
    private List<CloudProvider> getProvidersForType(String type) {
        switch (type) {
            case "high_performance":
                return cloudProviders.stream()
                        .filter(p -> p.getType() == CloudProvider.ProviderType.AWS || 
                                   p.getType() == CloudProvider.ProviderType.IBM)
                        .collect(Collectors.toList());
            case "low_latency":
                return cloudProviders.stream()
                        .filter(p -> p.getType() == CloudProvider.ProviderType.EDGE)
                        .collect(Collectors.toList());
            case "cost_effective":
                return cloudProviders.stream()
                        .filter(p -> p.getType() == CloudProvider.ProviderType.GCP || 
                                   p.getType() == CloudProvider.ProviderType.ALIBABA)
                        .collect(Collectors.toList());
            case "balanced":
            default:
                return new ArrayList<>(cloudProviders);
        }
    }
    
    /**
     * Calculate total cost across all providers
     */
    public double calculateTotalCost(Map<CloudProvider, List<Vm>> vmAllocation,
                                   Map<CloudProvider, List<Cloudlet>> cloudletAllocation,
                                   double simulationTime) {
        double totalCost = 0.0;
        
        for (CloudProvider provider : cloudProviders) {
            List<Vm> vms = vmAllocation.getOrDefault(provider, new ArrayList<>());
            List<Cloudlet> cloudlets = cloudletAllocation.getOrDefault(provider, new ArrayList<>());
            
            // VM costs
            for (Vm vm : vms) {
                totalCost += provider.calculateVmCost(vm, simulationTime / 3600.0);
            }
            
            // Cloudlet execution costs
            for (Cloudlet cloudlet : cloudlets) {
                totalCost += provider.calculateCloudletCost(cloudlet);
            }
        }
        
        return totalCost;
    }
    
    /**
     * Generate optimization report
     */
    public String generateOptimizationReport(Map<CloudProvider, List<Vm>> vmAllocation,
                                           Map<CloudProvider, List<Cloudlet>> cloudletAllocation) {
        StringBuilder report = new StringBuilder();
        report.append("\n" + "=".repeat(60)).append("\n");
        report.append("MULTI-CLOUD OPTIMIZATION REPORT\n");
        report.append("=".repeat(60)).append("\n");
        report.append("Strategy: ").append(currentStrategy.name()).append("\n");
        report.append("Description: ").append(currentStrategy.getDescription()).append("\n\n");
        
        for (CloudProvider provider : cloudProviders) {
            List<Vm> vms = vmAllocation.getOrDefault(provider, new ArrayList<>());
            List<Cloudlet> cloudlets = cloudletAllocation.getOrDefault(provider, new ArrayList<>());
            
            report.append(String.format("--- %s ---\n", provider.getType().getFullName()));
            report.append(String.format("Allocated VMs: %d\n", vms.size()));
            report.append(String.format("Allocated Cloudlets: %d\n", cloudlets.size()));
            report.append(String.format("Performance Score: %d/100\n", provider.getPerformanceScore()));
            report.append(String.format("Cost Efficiency: %d/100\n", provider.getCostEfficiencyScore()));
            report.append(String.format("Network Latency: %.1f ms\n", provider.getNetworkLatency()));
            report.append(String.format("SLA Uptime: %.2f%%\n\n", provider.getSlaUptime()));
        }
        
        return report.toString();
    }
    
    // Getters and setters
    public OptimizationStrategy getCurrentStrategy() { return currentStrategy; }
    public void setCurrentStrategy(OptimizationStrategy strategy) { this.currentStrategy = strategy; }
    public void setStrategyWeight(String metric, double weight) { strategyWeights.put(metric, weight); }
    public List<CloudProvider> getCloudProviders() { return new ArrayList<>(cloudProviders); }
}
