# Advanced Multi-Cloud Simulation (CloudSim Plus)

This project now focuses exclusively on the advanced multi-cloud scenario. It models six heterogeneous providers (AWS, Azure, GCP, Edge, Alibaba, IBM) and uses a custom `MultiCloudBroker` to keep VMs and cloudlets aligned with the chosen provider during each optimisation strategy run.

## Included Modules

- `AdvancedMultiCloudSimulation`: entry point that builds providers, generates workloads, and evaluates several optimisation strategies.
- `multicloud/CloudProvider`: encapsulates provider characteristics and pricing.
- `multicloud/MultiCloudResourceManager`: allocates VMs/cloudlets according to the selected optimisation strategy and reports costs.
- `multicloud/MultiCloudBroker`: extends `DatacenterBrokerSimple` to honour provider-specific allocations.

## Prerequisites

- Java 11+
- Maven 3.6+

## Build & Run

```powershell
# from the project root
mvn clean compile
mvn exec:java
```

The Maven exec plugin is preconfigured to launch `com.example.AdvancedMultiCloudSimulation`, so no additional arguments are required. VS Code’s “Run CloudSim Plus Example” task uses the same command sequence.

## Project Layout

```
src/main/java/com/example/
├── AdvancedMultiCloudSimulation.java
└── multicloud/
    ├── CloudProvider.java
    ├── MultiCloudBroker.java
    └── MultiCloudResourceManager.java
```

`target/` is generated during the build and can be removed with `mvn clean` when desired.
