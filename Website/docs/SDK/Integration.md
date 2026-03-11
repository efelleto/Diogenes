---
sidebar_position: 1
---

# Integration

Integrating the **Diogenes SDK** into your software is the final step to ensure total protection. Our SDK handles secure communication, hardware fingerprinting (HWID), and real-time validation with your server.

## Dependency Setup

First, ensure you have added the JitPack repository and the SDK dependency to your project as shown in the [Getting Started](../intro.md) section.

---

## Initialization

To start using the licensing system, call `DiogenesSDK.init()` inside your plugin's `onEnable()`. This method manages the entire authentication flow automatically.

### Method Signature

```java
DiogenesSDK.init(Plugin plugin, String productId, String baseUrl, Runnable onSuccess)
```

| Parameter | Type | Description                                                                                    |
| :--- | :--- |:-----------------------------------------------------------------------------------------------|
| `plugin` | `Plugin` | Your plugin instance (`this`). Used to schedule async tasks and disable the plugin on failure. |
| `productId` | `String` | The product identifier registered in your Discord Server. Must match exactly.                  |
| `baseUrl` | `String` | The full URL of your running Diogenes Server (e.g. `http://your-server:8080`).                 |
| `onSuccess` | `Runnable` | Callback executed **only** if the license is valid. Place all your plugin logic here.          |

---

## Code Examples

### Java

```java
@Override
public void onEnable() {
    DiogenesSDK.init(this, "your-product-id", "http://your-server:8080", () -> {
        isAuthorized.set(true);
        getServer().getPluginManager().registerEvents(this, this);
        getLogger().info("Plugin successfully loaded.");
    });
}
```

### Kotlin

```kotlin
override fun onEnable() {
    DiogenesSDK.init(this, "your-product-id", "http://your-server:8080") {
        isAuthorized.set(true)
        server.pluginManager.registerEvents(this, this)
        logger.info("Plugin successfully loaded.")
    }
}
```

---

## Authentication Flow

When `init()` is called, the SDK handles everything automatically:

1. The `license.yml` file is created in your plugin's data folder on first run.
2. The player fills in their license key and restarts the server.
3. The SDK reads the key, generates a **Hardware ID (HWID)**, and sends a validation request to your server.
4. If the license is valid, the `onSuccess` callback is triggered on the main thread.
5. If validation fails for any reason, the plugin is **automatically disabled**.

---

## Error Handling

You don't need to handle errors manually, the SDK does it for you. The table below shows every possible outcome:

| Scenario | SDK Behavior | Console Output |
| :--- | :--- | :--- |
| License valid | Executes `onSuccess` callback | `AUTH: Successfully authenticated.` |
| `license.yml` not found | Creates the file and disables plugin | `License file created. Please fill it and restart.` |
| Key is blank or placeholder | Disables plugin | `Please provide a valid license key in license.yml.` |
| Invalid key / HWID mismatch | Disables plugin | Server-provided error message |
| Server unreachable | Disables plugin | `ERROR: Remote server unreachable.` |

:::caution
Always place your plugin's core logic (event listeners, commands, tasks) **inside the `onSuccess` callback**. Code placed outside of it will run regardless of license validation.
:::