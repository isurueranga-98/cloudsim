# Multi-Cloud Simulation System Overview

This document explains how the core classes in this project collaborate to deliver a strategy-driven multi-cloud simulation using CloudSim Plus. Use it as a companion to the project README when you need a deeper architectural view or want to extend the system.

## 1. High-Level Architecture

```
AdvancedMultiCloudSimulation (driver)
        |
        v
  MultiCloudBroker (strategy-aware broker)
        |
        v
MultiCloudResourceManager (decision engine)
        |
        v
CloudProvider wrappers + CloudSim datacenters
```

1. **`AdvancedMultiCloudSimulation`** orchestrates the entire lifecycle: it boots a `CloudSimPlus` simulation, creates heterogeneous providers, prepares VM and Cloudlet workloads, and runs multiple optimisation scenarios.
2. **`MultiCloudBroker`** extends `DatacenterBrokerSimple` so that every VM or Cloudlet submission is routed through the `MultiCloudResourceManager`, ensuring decisions respect the active optimisation strategy.
3. **`MultiCloudResourceManager`** assigns providers to VMs and VMs to Cloudlets based on strategy-specific scoring, while guarding against capacity mismatches.
4. **`CloudProvider`** wraps a `DatacenterSimple` and stores descriptive metadata (latency, SLA, pricing, etc.) so that the decision engine can reason about trade-offs.

The Maven build (`pom.xml`) ties everything together by compiling Java 11 sources, provisioning CloudSim Plus, and wiring the exec plugin to run `AdvancedMultiCloudSimulation`.

## 2. File-by-File Responsibilities

### `src/main/java/com/example/AdvancedMultiCloudSimulation.java`
- **Purpose:** Main entry point and scenario coordinator.
- **Key roles:**
  - Instantiates the root `CloudSimPlus` environment and constructs one `CloudProvider` per vendor using provider-specific host configurations.
  - Generates a diverse VM fleet and multi-profile Cloudlet workloads.
  - Iterates over optimisation strategies, creating a fresh simulation, providers, and a `MultiCloudBroker` for each scenario.
  - Submits cloned VMs/Cloudlets to the broker, starts the simulation, and gathers realised allocations for reporting.
  - Produces per-strategy analytics (completion counts, execution times, cost calculations, provider distribution) using results returned by the broker and resource manager.

### `src/main/java/com/example/multicloud/MultiCloudBroker.java`
- **Purpose:** Acts as a strategy-aware broker that keeps runtime allocations aligned with optimisation policies.
- **Key roles:**
  - Registers custom `mapVmToDatacenter` and `mapCloudletToVm` callbacks.
  - Delegates placement decisions to the `MultiCloudResourceManager` every time a VM is created or a Cloudlet is bound, capturing the realised provider/VM mappings.
  - Exposes `getRealizedVmAllocation()` and `getRealizedCloudletAllocation()` so the driver can analyse actual placements after each scenario run.

### `src/main/java/com/example/multicloud/MultiCloudResourceManager.java`
- **Purpose:** Central decision engine for all optimisation strategies.
- **Key roles:**
  - Exposes `OptimizationStrategy` enums (cost, performance, latency, balanced, geographic distribution, load balancing).
  - Provides `selectProviderForVm` and `selectVmForCloudlet` to score candidates and avoid assigning VMs to providers that cannot host them.
  - Calculates composite scores (cost/performance/latency/reliability) and tracks round-robin state for geographic/load-balancing strategies.
  - Generates summary reports (`generateOptimizationReport`) and cost calculations used by the driver’s analytics.

### `src/main/java/com/example/multicloud/CloudProvider.java`
- **Purpose:** Lightweight wrapper around a CloudSim datacenter with provider-specific metadata.
- **Key roles:**
  - Stores pricing models, latency figures, SLA uptime, and feature flags for the provider.
  - Offers helper methods such as `calculateVmCost`, `calculateCloudletCost`, and `canHostVm` to support allocation and reporting logic.
  - Provides descriptive identifiers (full name, type) that show up in reports and logs.

### `pom.xml`
- **Purpose:** Maven build configuration.
- **Key roles:**
  - Targets Java 11, pulls in CloudSim Plus 8.0.0, Logback, and JUnit.
  - Configures the `exec-maven-plugin` to run `com.example.AdvancedMultiCloudSimulation`, enabling `mvn exec:java` workflows.
  - Keeps the project lean by excluding unnecessary modules while remaining easy to extend.

### `README.md`
- **Purpose:** Quick-start guide with execution instructions, high-level component summary, strategy descriptions, and troubleshooting tips.
- **Relationship to this document:** The README answers “how do I run this?” while this overview answers “how does it work under the hood?”

## 3. Execution Flow in Detail

1. **Initial setup:** `AdvancedMultiCloudSimulation` creates host configurations per provider and logs the infrastructure summary.
2. **Workload preparation:** VMs and Cloudlets are generated to represent high-performance, standard, burstable, and edge scenarios.
3. **Scenario loop:** For each optimisation strategy:
   - A new `CloudSimPlus` instance and provider fleet are created to keep results isolated.
   - The resource manager is primed with the active strategy.
   - A `MultiCloudBroker` is instantiated with references to the resource manager and providers.
   - Fresh VM and Cloudlet copies are submitted to the broker, which applies mapping callbacks.
   - The simulation runs until all events are processed.
   - Realised allocations are fetched from the broker, then analysed and reported by the driver.

4. **Reporting:** Statistics (total completions, average execution, total cost, allocation breakdown) are printed, accompanied by the resource manager’s optimisation report.

## 4. Extending the System

- **Add a provider:** Create a new `ProviderType` in `CloudProvider`, supply host configuration logic in `AdvancedMultiCloudSimulation`, and update scoring logic in `MultiCloudResourceManager` if needed.
- **Introduce a new strategy:** Add an enum entry, implement provider/VM scoring rules, and include the strategy in the scenario loop.
- **Custom analytics:** Extend `analyzeScenarioResults` to emit CSVs, send data to external dashboards, or compute additional KPIs.
- **Different workloads:** Adjust `createDiverseVmFleet` and `createVariedWorkloads` to model alternative application mixes.

## 5. Known Simulation Messages

- **“No suitable host found” (resolved)** – Previously caused by high-core VMs targeting undersized hosts. Addressed by increasing provider host MIPS and ensuring `selectProviderForVm` filters out incapable providers.
- **Cloudlet `Virtual Memory` warnings** – Indicate that the selected VM lacks enough RAM or bandwidth. They are useful for stress testing; increase VM specs if you need cleaner runs.

## 6. Quick Reference Commands

```powershell
mvn -q compile
mvn exec:java "-Dexec.mainClass=com.example.AdvancedMultiCloudSimulation"
```

Use the first command to ensure the project still compiles after modifications; the second runs the full simulation with the default scenario loop.

---
This overview should make it easier to navigate the codebase, reason about interactions, and plan extensions. Reach out in the README’s troubleshooting section whenever you adjust resource profiles or add new strategies.
