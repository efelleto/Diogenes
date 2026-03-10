---
sidebar_position: 2
---

# Security Flow

Understanding how Diogenes protects your software is crucial. Our security model relies on a multi-stage validation process that binds a license to specific hardware and ensures the integrity of every request.

---

## The Handshake Process

When `DiogenesSDK.init()` is called, the following sequence occurs instantly:



1. **Hardware Fingerprinting:** The SDK scans unique hardware identifiers (CPU, Motherboard, Disk UUID) to generate a persistent **Hardware ID (HWID)**.
2. **Encrypted Payload:** The License Key and HWID are bundled into an encrypted request sent to your Diogenes Server.
3. **Database Validation:** The server queries the MongoDB cluster to verify:
    * Is the key active?
    * Does the product ID match?
    * Is the HWID either empty (first use) or a perfect match with the stored one?
4. **Authorization Callback:** If all checks pass, the server sends a signed success signal. Only then does the SDK trigger the code inside your initialization callback.

---

## Protection Layers

The **Security Flow** is designed to prevent common cracking techniques:

### 1. HWID Locking
A license key is "locked" to the first machine that redeems it. 
* **Attempt:** If a user shares their key with a friend.
* **Result:** The server detects a HWID mismatch and returns an error, preventing the plugin from enabling on the second machine.

### 2. Callback Encapsulation
By placing your core logic (events, commands, tasks) inside the SDK callback, you create a "Locked Room" scenario.
* **Manual Bypass:** Even if a cracker tries to force the `isAuthorized` variable to `true`, the code inside the callback was never actually triggered by the SDK engine, leaving the software in a "hollow" state.

### 3. Server-Side Authority
The SDK is a messenger; the **Server** is the judge. No sensitive validation logic happens purely on the client side, making it significantly harder to manipulate via local memory editing.

---

## Security States

| State | SDK Action | Software Result |
| :--- | :--- | :--- |
| **Authorized** | Executes Callback | Full Access |
| **Invalid Key** | Ignores Callback | Plugin remains disabled |
| **HWID Mismatch** | Ignores Callback | Error log sent to Admin Audit |
| **Server Offline** | Failsafe (Denied) | Plugin remains disabled |

---

:::caution [**Note on Sinope:**]
For even higher security, we recommend using the **Sinope Layer** to obfuscate the strings and methods mentioned in this flow, preventing static analysis of your compiled JAR.
:::