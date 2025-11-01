# Advanced Multi-Cloud Simulation (CloudSim Plus)

This project demonstrates an end-to-end multi-cloud optimisation workflow using CloudSim Plus. It focuses on a single, feature-rich driver class (`AdvancedMultiCloudSimulation`) that collaborates with a custom broker (`MultiCloudBroker`) and a reusable resource manager (`MultiCloudResourceManager`) to explore allocation strategies across heterogeneous providers (AWS, Azure, GCP, Edge, Alibaba, IBM).

## 📦 Key Components at a Glance

| Layer | Class | Responsibility |
|-------|-------|----------------|
| Simulation Orchestrator | `com.example.AdvancedMultiCloudSimulation` | Boots CloudSim, creates datacenters, runs optimisation scenarios, and produces reports. |
| Strategy-Aware Broker | `com.example.multicloud.MultiCloudBroker` | Extends `DatacenterBrokerSimple` to map VMs and Cloudlets to providers using the resource manager. |
| Decision Engine | `com.example.multicloud.MultiCloudResourceManager` | Scores providers/VMs under multiple optimisation strategies and supplies allocation plans. |
| Provider Model | `com.example.multicloud.CloudProvider` | Wraps `Datacenter` instances with pricing, latency, and capability metadata. |

## 🔄 How Everything Works Together

1. **Infrastructure provisioning** – `AdvancedMultiCloudSimulation` instantiates a `CloudSimPlus` simulation and creates one `CloudProvider` per vendor. Each provider builds a `DatacenterSimple` populated with hosts sized to match its profile.
2. **Scenario loop** – For every optimisation strategy (cost, performance, balanced, latency), the simulation spins up a fresh `CloudSimPlus` instance and reconstructs the providers so results stay isolated.
3. **Broker creation** – A new `MultiCloudBroker` is attached to the scenario simulation. During initialisation it registers mapper callbacks:
	- `mapVmToDatacenter` delegates to `MultiCloudResourceManager.selectProviderForVm`, assigning each VM to the best-fit provider that can actually host it.
	- `mapCloudletToVm` asks `MultiCloudResourceManager.selectVmForCloudlet` to pair workloads with the most suitable VM (or falls back to a round-robin choice for geographic/load-balancing strategies).
4. **Workload submission** – Fresh copies of the VM fleet and Cloudlet workloads are created per scenario and submitted to the broker.
5. **Simulation run** – CloudSim processes events. The broker tracks realised VM and Cloudlet assignments so post-run analytics operate on what actually happened, not just the initial plan.
6. **Reporting** – `AdvancedMultiCloudSimulation` gathers results from the broker and resource manager to print execution statistics, cost figures, provider distribution, and an optimisation report for each strategy.

## 🧠 Optimisation Strategies

`MultiCloudResourceManager` exposes six strategies. The simulation currently cycles through cost, performance, balanced, and latency scenarios, but geographic distribution and load balancing are available for extension.

| Strategy | Behaviour |
|----------|-----------|
| COST_MINIMIZATION | Prefers providers with higher cost-efficiency scores and distributes VMs proportionally. |
| PERFORMANCE_MAXIMIZATION | Sends the highest-MIPS VMs to the fastest providers and prioritises capacity. |
| BALANCED | Combines cost, performance, latency, and reliability into a weighted score. |
| LATENCY_OPTIMIZATION | Minimises network latency, heavily favouring low-latency regions (edge/AWS). |
| GEOGRAPHIC_DISTRIBUTION | Round-robins VMs across providers to spread load globally. |
| LOAD_BALANCING | Uses round-robin placement to keep provider utilisation even. |

Broker callbacks use these strategy choices every time a VM or Cloudlet is scheduled, so you get consistent behaviour between the planning phase and runtime execution.

## 🛠️ Extending or Customising

- **Adjust provider capacity** – Modify `getHostConfiguration` in `AdvancedMultiCloudSimulation` to change host MIPS, RAM, or bandwidth per cloud.
- **Add strategies** – Introduce a new enum constant in `OptimizationStrategy`, provide weighting logic in `MultiCloudResourceManager`, and include it in `runOptimizationScenarios`.
- **Tailor workloads** – `createDiverseVmFleet` and `createVariedWorkloads` produce the VM and Cloudlet profiles. Adjust counts or resource demands to simulate different customer mixes.
- **Instrumentation** – Plug additional analytics into `analyzeScenarioResults` to export CSVs, compute SLA breaches, or feed dashboards.

## 🚀 Running the Simulation

```powershell
mvn compile
mvn exec:java "-Dexec.mainClass=com.example.AdvancedMultiCloudSimulation"
```

- The `exec-maven-plugin` in `pom.xml` already targets `AdvancedMultiCloudSimulation`, so `mvn exec:java` is usually sufficient. Quoting the `-Dexec.mainClass` flag keeps PowerShell happy if you override it.
- Expect CloudSim to log warnings if Cloudlets request more RAM or bandwidth than the assigned VM offers. That indicates contention and can be useful when stress-testing strategies.

## 🗂️ Repository Layout

```
src/main/java/com/example/AdvancedMultiCloudSimulation.java
src/main/java/com/example/multicloud/CloudProvider.java
src/main/java/com/example/multicloud/MultiCloudBroker.java
src/main/java/com/example/multicloud/MultiCloudResourceManager.java
```

Non-essential files were removed to keep the project focused on this advanced example.

## ✅ Verification Checklist

- `mvn -q compile` builds successfully (Maven may emit a Guice/Unsafe warning: it is safe to ignore).
- `mvn exec:java` prints optimisation summaries for each strategy.
- Reported allocations align with the provider/VM scoring logic in `MultiCloudResourceManager`.

## 🤔 Troubleshooting

- **“No suitable host found” warnings** – Increase the host MIPS in `getHostConfiguration` or reduce the VM MIPS requirements. The current defaults include extra headroom for high-performance VMs.
- **Cloudlet virtual memory messages** – The broker is exercising VMs beyond their RAM/bandwidth. Either raise VM capacities or reduce Cloudlet demands if the slowdown is undesirable.
- **Different shells** – On macOS/Linux you can drop the quotes: `mvn exec:java -Dexec.mainClass=...`.

## 📚 Further Reading

- [CloudSim Plus Documentation](https://cloudsimplus.org/) – API reference and tutorials.
- CloudSim Plus examples (`org.cloudsimplus.examples`) for ideas on adding power models, network topologies, or container workloads.

Have fun experimenting with strategy-driven multi-cloud optimisation! Adjust the inputs, add new providers, or wire in external data sources to see how the broker reacts under real-world constraints.
