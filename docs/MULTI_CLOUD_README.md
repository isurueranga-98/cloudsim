# Multi-Cloud Heterogeneous Simulation

This project demonstrates a comprehensive multi-cloud simulation using CloudSim Plus, featuring multiple heterogeneous cloud environments with intelligent resource allocation and optimization strategies.

## 🌟 Features

### Multi-Cloud Providers
- **AWS-like Environment**: High-performance computing with premium pricing
- **Azure-like Environment**: Balanced performance and enterprise integration  
- **GCP-like Environment**: Cost-effective with advanced capabilities
- **Edge Computing**: Ultra-low latency with limited resources
- **Alibaba Cloud**: Asia-Pacific focused with competitive pricing
- **IBM Cloud**: Enterprise-grade hybrid cloud solutions

### Intelligent Resource Management
- **Cost Minimization**: Optimize for lowest total cost
- **Performance Maximization**: Prioritize computational performance
- **Latency Optimization**: Minimize network delays
- **Balanced Strategy**: Optimize across multiple metrics
- **Geographic Distribution**: Distribute workload globally
- **Load Balancing**: Even distribution across providers

### Diverse Workload Types
- **Web Applications**: Moderate resource requirements
- **Data Processing**: High CPU and memory intensive
- **Machine Learning**: GPU-accelerated computing
- **IoT/Edge**: Low-latency, lightweight processing
- **Batch Processing**: Long-running, cost-optimized tasks

## 🏗️ Architecture

```
Multi-Cloud Infrastructure
├── Cloud Providers
│   ├── AWS (4 hosts, 16 cores each, 65GB RAM)
│   ├── Azure (3 hosts, 12 cores each, 48GB RAM)
│   ├── GCP (5 hosts, 8 cores each, 32GB RAM)
│   ├── Edge (2 hosts, 4 cores each, 16GB RAM)
│   ├── Alibaba (3 hosts, 10 cores each, 24GB RAM)
│   └── IBM (3 hosts, 14 cores each, 56GB RAM)
├── Resource Manager
│   ├── Optimization Strategies
│   ├── Cost Analysis
│   └── Performance Monitoring
├── VM Fleet (24 VMs total)
│   ├── High-Performance (6 VMs)
│   ├── Standard (8 VMs)
│   ├── Burstable (6 VMs)
│   └── Micro-Edge (4 VMs)
└── Workloads (48 Cloudlets total)
    ├── Web Applications (14)
    ├── Data Processing (12)
    ├── Machine Learning (10)
    ├── IoT/Edge (7)
    └── Batch Processing (5)
```

## 🚀 Quick Start

### Prerequisites
- Java 11 or higher
- Maven 3.6 or higher

### Running the Simulation

1. **Compile the project:**
   ```bash
   mvn compile
   ```

2. **Run the basic multi-cloud simulation:**
   ```bash
   mvn exec:java -Dexec.mainClass="com.example.MultiCloudSimulation"
   ```

3. **Run the advanced simulation with optimization scenarios:**
   ```bash
   mvn exec:java -Dexec.mainClass="com.example.AdvancedMultiCloudSimulation"
   ```

4. **Using VS Code task:**
   ```bash
   # Press Ctrl+Shift+P, then type "Tasks: Run Task"
   # Select "Run CloudSim Plus Example"
   ```

## 📊 Simulation Scenarios

The advanced simulation runs four optimization scenarios:

1. **Cost Minimization Scenario**
   - Prioritizes cheapest cloud providers
   - Uses cost-effective VM types
   - Optimizes for total cost reduction

2. **Performance Maximization Scenario**
   - Allocates to highest-performance providers
   - Uses premium VM configurations
   - Optimizes for fastest execution

3. **Balanced Scenario**
   - Weighs cost, performance, and latency
   - Distributes workload intelligently
   - Provides optimal trade-offs

4. **Latency Optimization Scenario**
   - Prioritizes edge computing providers
   - Minimizes network delays
   - Optimizes for real-time applications

## 📈 Output Analysis

The simulation provides comprehensive analysis including:

- **Resource Allocation**: How VMs and cloudlets are distributed
- **Cost Analysis**: Total costs per provider and strategy
- **Performance Metrics**: Execution times and throughput
- **Provider Utilization**: Resource usage across cloud providers
- **Optimization Reports**: Strategy effectiveness comparison

### Sample Output
```
╔══════════════════════════════════════════════════════════════════════════════╗
║               ADVANCED MULTI-CLOUD HETEROGENEOUS SIMULATION                 ║
║                                                                              ║
║  Simulating: AWS, Azure, GCP, Edge, Alibaba Cloud, IBM Cloud                ║
║  Features: Intelligent allocation, Cost optimization, Performance analysis  ║
╚══════════════════════════════════════════════════════════════════════════════╝

🏗️  Creating Cloud Infrastructure...
   ✓ Amazon Web Services: 4 hosts, Latency: 5.0ms, Performance: 95/100
   ✓ Microsoft Azure: 3 hosts, Latency: 7.0ms, Performance: 90/100
   ✓ Google Cloud Platform: 5 hosts, Latency: 8.0ms, Performance: 85/100
   ✓ Edge Computing: 2 hosts, Latency: 1.0ms, Performance: 70/100
   ✓ Alibaba Cloud: 3 hosts, Latency: 12.0ms, Performance: 80/100
   ✓ IBM Cloud: 3 hosts, Latency: 6.0ms, Performance: 88/100
```

## 🛠️ Customization

### Adding New Cloud Providers
1. Extend the `CloudProvider.ProviderType` enum
2. Add configuration in `HostConfiguration`
3. Define pricing model in `setProviderPricing`

### Creating Custom Optimization Strategies
1. Add new strategy to `OptimizationStrategy` enum
2. Implement allocation logic in `MultiCloudResourceManager`
3. Define strategy-specific weights and criteria

### Modifying Workload Types
1. Adjust workload categories in `createVariedWorkloads`
2. Modify VM configurations in `createDiverseVmFleet`
3. Customize resource requirements and utilization models

## 📚 Key Classes

- **`AdvancedMultiCloudSimulation`**: Main simulation orchestrator
- **`MultiCloudSimulation`**: Basic multi-cloud demonstration
- **`CloudProvider`**: Cloud provider abstraction with characteristics
- **`MultiCloudResourceManager`**: Intelligent resource allocation engine

## 🔧 Configuration Options

### Simulation Parameters
```java
// In AdvancedMultiCloudSimulation.java
private static final int TOTAL_VMS = 24;
private static final int TOTAL_CLOUDLETS = 48;
private static final double SIMULATION_TIME = 7200; // 2 hours

// Host counts per provider
private static final Map<CloudProvider.ProviderType, Integer> HOST_COUNTS = Map.of(
    CloudProvider.ProviderType.AWS, 4,
    CloudProvider.ProviderType.AZURE, 3,
    // ... more providers
);
```

### Optimization Weights
```java
// In MultiCloudResourceManager.java
Map<String, Double> weights = new HashMap<>();
weights.put("cost", 0.4);
weights.put("performance", 0.3);
weights.put("latency", 0.2);
weights.put("reliability", 0.1);
```

## 📄 License

This project is part of a research initiative for cloud computing simulation and optimization.

## 🤝 Contributing

Feel free to extend the simulation with:
- Additional cloud providers
- New optimization algorithms
- Enhanced workload models
- Advanced cost models
- Real-world data integration

## 📞 Support

For questions or issues, please refer to the CloudSim Plus documentation or create an issue in this repository.
