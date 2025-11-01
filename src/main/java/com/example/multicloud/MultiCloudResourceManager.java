package com.example.multicloud;

import org.cloudsimplus.cloudlets.Cloudlet;
import org.cloudsimplus.vms.Vm;

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
    private int nextGeographicProviderIndex = 0;
    private int nextLoadBalancingProviderIndex = 0;
    private int nextCloudletRoundRobinIndex = 0;
    
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
        
        switch (currentStrategy) {
            case COST_MINIMIZATION:
                allocateVmsByCost(vms, allocation);
                break;
            case PERFORMANCE_MAXIMIZATION:
                allocateVmsByPerformance(vms, allocation);
                break;
            case LATENCY_OPTIMIZATION:
                allocateVmsByLatency(vms, allocation);
                break;
            case BALANCED:
                allocateVmsBalanced(vms, allocation);
                break;
            case GEOGRAPHIC_DISTRIBUTION:
                allocateVmsGeographically(vms, allocation);
                break;
            case LOAD_BALANCING:
                allocateVmsLoadBalanced(vms, allocation);
                break;
        }
        
        return allocation;
    }
    
    private void allocateVmsByCost(List<Vm> vms, Map<CloudProvider, List<Vm>> allocation) {
        // Sort providers by cost efficiency (highest first)
        List<CloudProvider> sortedProviders = cloudProviders.stream()
                .sorted((p1, p2) -> Integer.compare(p2.getCostEfficiencyScore(), p1.getCostEfficiencyScore()))
                .collect(Collectors.toList());
        
        // Allocate VMs to most cost-effective providers first
        int providerIndex = 0;
        for (Vm vm : vms) {
            CloudProvider provider = sortedProviders.get(providerIndex % sortedProviders.size());
            allocation.get(provider).add(vm);
            providerIndex++;
        }
    }
    
    private void allocateVmsByPerformance(List<Vm> vms, Map<CloudProvider, List<Vm>> allocation) {
        // Sort providers by performance score (highest first)
        List<CloudProvider> sortedProviders = cloudProviders.stream()
                .sorted((p1, p2) -> Integer.compare(p2.getPerformanceScore(), p1.getPerformanceScore()))
                .collect(Collectors.toList());
        
        // Allocate high-performance VMs to high-performance providers
        List<Vm> sortedVms = vms.stream()
                .sorted((v1, v2) -> Double.compare(v2.getMips(), v1.getMips()))
                .collect(Collectors.toList());
        
        int providerIndex = 0;
        for (Vm vm : sortedVms) {
            CloudProvider provider = sortedProviders.get(providerIndex % sortedProviders.size());
            allocation.get(provider).add(vm);
            providerIndex++;
        }
    }
    
    private void allocateVmsByLatency(List<Vm> vms, Map<CloudProvider, List<Vm>> allocation) {
        // Sort providers by latency (lowest first)
        List<CloudProvider> sortedProviders = cloudProviders.stream()
                .sorted(Comparator.comparingDouble(CloudProvider::getNetworkLatency))
                .collect(Collectors.toList());
        
        // Prioritize edge and low-latency providers
        int providerIndex = 0;
        for (Vm vm : vms) {
            CloudProvider provider = sortedProviders.get(providerIndex % sortedProviders.size());
            allocation.get(provider).add(vm);
            providerIndex++;
        }
    }
    
    private void allocateVmsBalanced(List<Vm> vms, Map<CloudProvider, List<Vm>> allocation) {
        // Calculate composite score for each provider
        Map<CloudProvider, Double> providerScores = new HashMap<>();
        
        for (CloudProvider provider : cloudProviders) {
            providerScores.put(provider, computeBalancedScore(provider));
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
                allocation.get(provider).add(vms.get(vmIndex++));
            }
        }
        
        // Allocate remaining VMs to best provider
        while (vmIndex < vms.size()) {
            allocation.get(sortedProviders.get(0)).add(vms.get(vmIndex++));
        }
    }

    private double computeBalancedScore(CloudProvider provider) {
        return (provider.getCostEfficiencyScore() * strategyWeights.get("cost")) +
               (provider.getPerformanceScore() * strategyWeights.get("performance")) +
               ((100 - provider.getNetworkLatency()) * strategyWeights.get("latency")) +
               (provider.getSlaUptime() * strategyWeights.get("reliability"));
    }
    
    private void allocateVmsGeographically(List<Vm> vms, Map<CloudProvider, List<Vm>> allocation) {
        // Distribute evenly across all providers for geographic diversity
        int providerIndex = 0;
        for (Vm vm : vms) {
            CloudProvider provider = cloudProviders.get(providerIndex % cloudProviders.size());
            allocation.get(provider).add(vm);
            providerIndex++;
        }
    }
    
    private void allocateVmsLoadBalanced(List<Vm> vms, Map<CloudProvider, List<Vm>> allocation) {
        // Simple round-robin allocation for load balancing
        int providerIndex = 0;
        for (Vm vm : vms) {
            CloudProvider provider = cloudProviders.get(providerIndex % cloudProviders.size());
            allocation.get(provider).add(vm);
            providerIndex++;
        }
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

    public CloudProvider selectProviderForVm(Vm vm, List<CloudProvider> providerCandidates) {
        List<CloudProvider> candidates = (providerCandidates == null || providerCandidates.isEmpty())
                ? new ArrayList<>(cloudProviders)
                : new ArrayList<>(providerCandidates);

        if (candidates.isEmpty()) {
            throw new IllegalStateException("No cloud providers available for VM allocation.");
        }

        List<CloudProvider> suitableProviders = candidates.stream()
                .filter(provider -> provider.canHostVm(vm))
                .collect(Collectors.toList());

        List<CloudProvider> providerPool = suitableProviders.isEmpty() ? candidates : suitableProviders;

        switch (currentStrategy) {
            case COST_MINIMIZATION:
                return providerPool.stream()
                        .max(Comparator.comparingInt(CloudProvider::getCostEfficiencyScore))
                        .orElse(providerPool.get(0));
            case PERFORMANCE_MAXIMIZATION:
                return providerPool.stream()
                        .max(Comparator.comparingInt(CloudProvider::getPerformanceScore))
                        .orElse(providerPool.get(0));
            case LATENCY_OPTIMIZATION:
                return providerPool.stream()
                        .min(Comparator.comparingDouble(CloudProvider::getNetworkLatency))
                        .orElse(providerPool.get(0));
            case GEOGRAPHIC_DISTRIBUTION:
                CloudProvider geographicProvider = providerPool.get(nextGeographicProviderIndex % providerPool.size());
                nextGeographicProviderIndex++;
                return geographicProvider;
            case LOAD_BALANCING:
                CloudProvider loadBalancedProvider = providerPool.get(nextLoadBalancingProviderIndex % providerPool.size());
                nextLoadBalancingProviderIndex++;
                return loadBalancedProvider;
            case BALANCED:
            default:
                return providerPool.stream()
                        .max(Comparator.comparingDouble(this::computeBalancedScore))
                        .orElse(providerPool.get(0));
        }
    }

    public Vm selectVmForCloudlet(Cloudlet cloudlet,
                                  List<Vm> candidateVms,
                                  Map<Vm, CloudProvider> vmProviders) {
        if (candidateVms == null || candidateVms.isEmpty()) {
            return Vm.NULL;
        }

        List<Vm> suitableVms = candidateVms.stream()
                .filter(vm -> vm.isSuitableForCloudlet(cloudlet))
                .collect(Collectors.toList());

        if (suitableVms.isEmpty()) {
            suitableVms = new ArrayList<>(candidateVms);
        }

        if (currentStrategy == OptimizationStrategy.GEOGRAPHIC_DISTRIBUTION
                || currentStrategy == OptimizationStrategy.LOAD_BALANCING) {
            Vm roundRobinVm = suitableVms.get(nextCloudletRoundRobinIndex % suitableVms.size());
            nextCloudletRoundRobinIndex++;
            return roundRobinVm;
        }

        Map<Vm, CloudProvider> providersMap = vmProviders == null ? Collections.emptyMap() : vmProviders;

        return suitableVms.stream()
                .max(Comparator.comparingDouble(vm -> calculateVmScore(vm, cloudlet, providersMap)))
                .orElse(suitableVms.get(0));
    }

    private double calculateVmScore(Vm vm,
                                    Cloudlet cloudlet,
                                    Map<Vm, CloudProvider> vmProviders) {
        CloudProvider provider = resolveProviderForVm(vm, vmProviders);
        double providerPerformance = provider != null ? provider.getPerformanceScore() : 0.0;
        double costEfficiency = provider != null ? provider.getCostEfficiencyScore() : 0.0;
        double latencyScore = provider != null ? (100.0 - provider.getNetworkLatency()) : 0.0;
        double vmCpuScore = vm.getMips();
        double vmRamScore = vm.getRam().getCapacity() / 1024.0;
        double workloadWeight = cloudlet.getLength() / 1000.0 + cloudlet.getPesNumber();

        switch (currentStrategy) {
            case COST_MINIMIZATION:
                return (costEfficiency * 1.5) - (vmCpuScore * 0.002) - (vmRamScore * 0.05);
            case PERFORMANCE_MAXIMIZATION:
                return (providerPerformance * 1.2) + (vmCpuScore * 0.01) + (vmRamScore * 0.1);
            case LATENCY_OPTIMIZATION:
                return (latencyScore * 1.5) + (vmCpuScore * 0.005);
            case BALANCED:
            default:
                double balancedProvider = provider != null ? computeBalancedScore(provider) : 0.0;
                return balancedProvider + (vmCpuScore * 0.006) + (vmRamScore * 0.08) - (workloadWeight * 0.02);
        }
    }

    private CloudProvider resolveProviderForVm(Vm vm, Map<Vm, CloudProvider> vmProviders) {
        if (vmProviders != null) {
            CloudProvider provider = vmProviders.get(vm);
            if (provider != null) {
                return provider;
            }
        }
        return cloudProviders.isEmpty() ? null : cloudProviders.get(0);
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
