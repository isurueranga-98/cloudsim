# Advanced Multi-Cloud Simulation (CloudSim Plus)

This lean project focuses solely on the `AdvancedMultiCloudSimulation`, a rich CloudSim Plus scenario that models multiple heterogeneous cloud providers (AWS, Azure, GCP, Edge, Alibaba, IBM), intelligent VM/cloudlet allocation, and cost/performance trade-offs.

## 🌟 What You Get
- Single entry point: `com.example.AdvancedMultiCloudSimulation`
- Supporting utilities: `com.example.multicloud.CloudProvider` and `MultiCloudResourceManager`
- Heterogeneous datacenter setup, strategy-aware resource allocation, detailed reporting

## 🚀 Run It

```bash
mvn compile
mvn exec:java
```

Maven executes `AdvancedMultiCloudSimulation` by default (configured in `pom.xml`).

## 🗂️ Project Layout

```
src/main/java/com/example/AdvancedMultiCloudSimulation.java
src/main/java/com/example/multicloud/CloudProvider.java
src/main/java/com/example/multicloud/MultiCloudResourceManager.java
```

Everything else has been removed to keep the project laser-focused on this advanced scenario.
