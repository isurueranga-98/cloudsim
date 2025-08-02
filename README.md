# CloudSim Plus Maven Project

This project demonstrates how to use CloudSim Plus for cloud computing simulation.

## Prerequisites

- Java 11 or higher
- Maven 3.6 or higher

## Getting Started

1. **Install Dependencies**
   ```bash
   mvn clean install
   ```

2. **Compile the Project**
   ```bash
   mvn compile
   ```

3. **Run the Example**
   ```bash
   mvn exec:java -Dexec.mainClass="CloudSimPlusExample"
   ```

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
