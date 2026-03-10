---
sidebar_position: 1
---

# Integration

Integrating the **Diogenes SDK** into your software is the final step to ensure total protection. Our SDK handles secure communication, hardware fingerprinting (HWID), and real-time validation with your server.

## Dependency Setup

First, ensure you have added the JitPack repository and the SDK dependency to your project as shown in the [Getting Started](../intro.md) section.

---

## Initialization

To start using the licensing system, you need to initialize the SDK. This instance will manage the connection to your Diogenes Server.

### Java
```kotlin
  @Override
  public void onEnable() {
    DiogenesSDK.init(this, "productid", "http://your-server:8080", () -> {

      isAuthorized.set(true);
      getServer().getPluginManager().registerEvents(this, this);
    });
  }
  ```

  ### Kotlin
  ```kotlin
  override fun onEnable() {
    DiogenesSDK.init(this, "productid", "http://your-server:8080") {
        
        server.pluginManager.registerEvents(this, this)
    }
}
```
