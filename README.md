# Multi-Cloud Heterogeneous Simulation with CloudSim Plus

This project demonstrates a comprehensive multi-cloud simulation using CloudSim Plus, featuring multiple heterogeneous cloud environments that represent real-world cloud providers like AWS, Azure, GCP, and Edge computing infrastructure.

## 🌟 Features

### Multi-Cloud Environment
- **AWS-like Infrastructure**: High-performance computing (16 cores, 3500 MIPS/core, premium pricing)
- **Azure-like Infrastructure**: Balanced performance (12 cores, 3000 MIPS/core, moderate pricing)  
- **GCP-like Infrastructure**: Cost-effective (8 cores, 2800 MIPS/core, budget pricing)
- **Edge Computing**: Low-latency (4 cores, 2200 MIPS/core, premium pricing for latency)

### Intelligent Workload Distribution
- **Enterprise Workloads**: High-performance VMs for CPU-intensive tasks
- **Balanced Workloads**: Standard VMs for general-purpose applications
- **Batch Processing**: Budget VMs for cost-effective bulk processing
- **Edge Computing**: Specialized VMs for latency-sensitive applications

### Realistic Simulation Parameters
- **Infrastructure**: 4 datacenters, 12 hosts total (48 cores AWS, 36 cores Azure, 32 cores GCP, 8 cores Edge)
- **Virtual Resources**: 14 VMs with varied configurations
- **Workloads**: 28 cloudlets representing different application types
- **Cost Models**: Provider-specific pricing reflecting real-world costs

## 🏗️ Architecture

```
Multi-Cloud Infrastructure
├── AWS-like Datacenter
│   ├── 3 hosts (16 cores each, 3500 MIPS/core)
│   ├── 32GB RAM per host, 25Gbps bandwidth
│   └── Premium pricing ($0.15/sec)
├── Azure-like Datacenter  
│   ├── 3 hosts (12 cores each, 3000 MIPS/core)
│   ├── 24GB RAM per host, 20Gbps bandwidth
│   └── Balanced pricing ($0.12/sec)
├── GCP-like Datacenter
│   ├── 4 hosts (8 cores each, 2800 MIPS/core)
│   ├── 16GB RAM per host, 15Gbps bandwidth
│   └── Cost-effective pricing ($0.08/sec)
└── Edge Datacenter
    ├── 2 hosts (4 cores each, 2200 MIPS/core)
    ├── 8GB RAM per host, 5Gbps bandwidth
    └── Premium edge pricing ($0.25/sec)
```

## 🚀 Quick Start

### Prerequisites
- Java 11 or higher
- Maven 3.6 or higher

### Running the Simulation

1. **Clone and navigate to the project:**
   ```bash
   cd trymaven
   ```

2. **Compile the project:**
   ```bash
   mvn compile
   ```

3. **Run the simulation:**
   ```bash
   mvn exec:java
   ```

4. **Or use VS Code task:**
   - Press `Ctrl+Shift+P`
   - Type "Tasks: Run Task"
   - Select "Run CloudSim Plus Example"

## About CloudSim Plus

CloudSim Plus is a modern, full-featured, highly extensible, easy-to-use and state-of-the-art Java 8+ simulation framework for cloud computing environments. It's a complete redesign and re-engineering of CloudSim 3.x.

### Key Features:
- Modern Java 8+ syntax and features
- Functional programming support
- Better documentation and examples
- Improved performance
- Extensive test coverage
- Active development and support

## Project Structure

```
src/
├── main/
│   ├── java/
│   │   └── CloudSimPlusExample.java    # Main example class
│   └── resources/
└── test/
    └── java/
```

## Dependencies

This project includes:
- **CloudSim Plus 8.0.0** - The main simulation framework
- **JUnit 5** - For unit testing
- **Logback** - For logging

## Next Steps

You can now:
1. Modify the `CloudSimPlusExample.java` to experiment with different simulation scenarios
2. Create additional classes for more complex simulations
3. Add tests in the `src/test/java` directory
4. Explore CloudSim Plus documentation at: https://cloudsimplus.org/

## Troubleshooting

If you encounter any issues:
1. Ensure you have Java 11+ installed
2. Check that Maven is properly configured
3. Verify internet connection for downloading dependencies
