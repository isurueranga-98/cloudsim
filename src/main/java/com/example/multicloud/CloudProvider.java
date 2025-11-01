package com.example.multicloud;

import org.cloudsimplus.datacenters.Datacenter;
import org.cloudsimplus.vms.Vm;
import org.cloudsimplus.cloudlets.Cloudlet;

import java.util.Map;
import java.util.HashMap;

/**
 * Cloud Provider represents different cloud service providers with their characteristics
 * and pricing models. This class encapsulates the behavior and properties of major
 * cloud providers like AWS, Azure, GCP, and Edge computing environments.
 */
public class CloudProvider {
    
    public enum ProviderType {
        AWS("Amazon Web Services", "High-performance computing with premium pricing"),
        AZURE("Microsoft Azure", "Balanced performance and enterprise integration"),
        GCP("Google Cloud Platform", "Cost-effective with advanced ML capabilities"),
        EDGE("Edge Computing", "Ultra-low latency with limited resources"),
        ALIBABA("Alibaba Cloud", "Asia-Pacific focused with competitive pricing"),
        IBM("IBM Cloud", "Enterprise-grade hybrid cloud solutions");
        
        private final String fullName;
        private final String description;
        
        ProviderType(String fullName, String description) {
            this.fullName = fullName;
            this.description = description;
        }
        
        public String getFullName() { return fullName; }
        public String getDescription() { return description; }
    }
    
    private final ProviderType type;
    private final Datacenter datacenter;
    private final Map<String, Double> pricingModel;
    private final Map<String, Object> characteristics;
    
    public CloudProvider(ProviderType type, Datacenter datacenter) {
        this.type = type;
        this.datacenter = datacenter;
        this.pricingModel = new HashMap<>();
        this.characteristics = new HashMap<>();
        initializeProviderSpecificSettings();
    }
    
    private void initializeProviderSpecificSettings() {
        switch (type) {
            case AWS:
                setupAWSCharacteristics();
                break;
            case AZURE:
                setupAzureCharacteristics();
                break;
            case GCP:
                setupGCPCharacteristics();
                break;
            case EDGE:
                setupEdgeCharacteristics();
                break;
            case ALIBABA:
                setupAlibabaCharacteristics();
                break;
            case IBM:
                setupIBMCharacteristics();
                break;
        }
    }
    
    private void setupAWSCharacteristics() {
        characteristics.put("region", "us-east-1");
        characteristics.put("availability_zones", 3);
        characteristics.put("sla_uptime", 99.99);
        characteristics.put("network_latency", 5.0); // ms
        characteristics.put("auto_scaling", true);
        characteristics.put("spot_instances", true);
        
        pricingModel.put("compute_per_hour", 0.15);
        pricingModel.put("storage_per_gb", 0.023);
        pricingModel.put("bandwidth_per_gb", 0.09);
        pricingModel.put("memory_per_gb", 0.05);
    }
    
    private void setupAzureCharacteristics() {
        characteristics.put("region", "east-us");
        characteristics.put("availability_zones", 3);
        characteristics.put("sla_uptime", 99.95);
        characteristics.put("network_latency", 7.0); // ms
        characteristics.put("auto_scaling", true);
        characteristics.put("hybrid_integration", true);
        
        pricingModel.put("compute_per_hour", 0.12);
        pricingModel.put("storage_per_gb", 0.020);
        pricingModel.put("bandwidth_per_gb", 0.08);
        pricingModel.put("memory_per_gb", 0.04);
    }
    
    private void setupGCPCharacteristics() {
        characteristics.put("region", "us-central1");
        characteristics.put("availability_zones", 4);
        characteristics.put("sla_uptime", 99.95);
        characteristics.put("network_latency", 8.0); // ms
        characteristics.put("preemptible_instances", true);
        characteristics.put("ml_services", true);
        
        pricingModel.put("compute_per_hour", 0.08);
        pricingModel.put("storage_per_gb", 0.018);
        pricingModel.put("bandwidth_per_gb", 0.06);
        pricingModel.put("memory_per_gb", 0.03);
    }
    
    private void setupEdgeCharacteristics() {
        characteristics.put("region", "edge-local");
        characteristics.put("availability_zones", 1);
        characteristics.put("sla_uptime", 99.0);
        characteristics.put("network_latency", 1.0); // ms - ultra low
        characteristics.put("limited_resources", true);
        characteristics.put("5g_connectivity", true);
        
        pricingModel.put("compute_per_hour", 0.25);
        pricingModel.put("storage_per_gb", 0.050);
        pricingModel.put("bandwidth_per_gb", 0.15);
        pricingModel.put("memory_per_gb", 0.08);
    }
    
    private void setupAlibabaCharacteristics() {
        characteristics.put("region", "ap-southeast-1");
        characteristics.put("availability_zones", 3);
        characteristics.put("sla_uptime", 99.9);
        characteristics.put("network_latency", 12.0); // ms
        characteristics.put("asia_optimized", true);
        characteristics.put("compliance_china", true);
        
        pricingModel.put("compute_per_hour", 0.07);
        pricingModel.put("storage_per_gb", 0.015);
        pricingModel.put("bandwidth_per_gb", 0.05);
        pricingModel.put("memory_per_gb", 0.025);
    }
    
    private void setupIBMCharacteristics() {
        characteristics.put("region", "us-south");
        characteristics.put("availability_zones", 3);
        characteristics.put("sla_uptime", 99.99);
        characteristics.put("network_latency", 6.0); // ms
        characteristics.put("enterprise_security", true);
        characteristics.put("hybrid_cloud", true);
        
        pricingModel.put("compute_per_hour", 0.14);
        pricingModel.put("storage_per_gb", 0.025);
        pricingModel.put("bandwidth_per_gb", 0.085);
        pricingModel.put("memory_per_gb", 0.045);
    }
    
    /**
     * Calculate cost for running a VM for specified duration
     */
    public double calculateVmCost(Vm vm, double hours) {
        double computeCost = pricingModel.get("compute_per_hour") * hours;
        double memoryCost = pricingModel.get("memory_per_gb") * (vm.getRam().getCapacity() / 1024.0) * hours;
        double storageCost = pricingModel.get("storage_per_gb") * (vm.getStorage().getCapacity() / 1024.0) * hours;
        
        return computeCost + memoryCost + storageCost;
    }
    
    /**
     * Calculate cost for executing a cloudlet
     */
    public double calculateCloudletCost(Cloudlet cloudlet) {
        double executionHours = cloudlet.getActualCpuTime() / 3600.0; // Convert seconds to hours
        double computeCost = pricingModel.get("compute_per_hour") * executionHours;
        double bandwidthCost = pricingModel.get("bandwidth_per_gb") * (cloudlet.getFileSize() / 1024.0);
        
        return computeCost + bandwidthCost;
    }
    
    /**
     * Get network latency for this provider
     */
    public double getNetworkLatency() {
        return (Double) characteristics.get("network_latency");
    }
    
    /**
     * Get SLA uptime percentage
     */
    public double getSlaUptime() {
        return (Double) characteristics.get("sla_uptime");
    }
    
    /**
     * Check if provider supports a specific feature
     */
    public boolean supportsFeature(String feature) {
        return characteristics.containsKey(feature) && 
               Boolean.TRUE.equals(characteristics.get(feature));
    }
    
    /**
     * Get provider performance score (0-100)
     */
    public int getPerformanceScore() {
        switch (type) {
            case AWS: return 95;
            case AZURE: return 90;
            case GCP: return 85;
            case IBM: return 88;
            case ALIBABA: return 80;
            case EDGE: return 70; // Lower due to resource constraints
            default: return 75;
        }
    }
    
    /**
     * Get cost efficiency score (0-100, higher is more cost-effective)
     */
    public int getCostEfficiencyScore() {
        switch (type) {
            case GCP: return 95;
            case ALIBABA: return 90;
            case AZURE: return 80;
            case IBM: return 75;
            case AWS: return 70;
            case EDGE: return 50; // Expensive but necessary for latency
            default: return 70;
        }
    }
    
    // Getters
    public ProviderType getType() { return type; }
    public Datacenter getDatacenter() { return datacenter; }
    public Map<String, Double> getPricingModel() { return pricingModel; }
    public Map<String, Object> getCharacteristics() { return characteristics; }

    public boolean canHostVm(Vm vm) {
        return datacenter.getHostList().stream().anyMatch(host -> host.isSuitableForVm(vm));
    }
    
    @Override
    public String toString() {
        return String.format("%s (%s) - Performance: %d/100, Cost Efficiency: %d/100", 
                type.getFullName(), type.name(), 
                getPerformanceScore(), getCostEfficiencyScore());
    }
}
