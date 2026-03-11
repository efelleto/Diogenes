<div align="center">
  <img src="https://cdn.discordapp.com/attachments/1278907608849186816/1480415342609043646/Gemini_Generated_Image_9uh8dt9uh8dt9uh8-removebg-preview.png?ex=69af97ba&is=69ae463a&hm=26962ed14c1e13be561af7fc72600f522c2f7f313e7440e60b8a33345faf8fae&" alt="Diogenes logo" width="220" />
  <br/>
  <h1>Diogenes</h1>

  <p>
    <a href="https://github.com/efelleto/diogenes">
      <img src="https://img.shields.io/github/stars/efelleto/diogenes?style=for-the-badge&logo=github&logoColor=white&color=yellow" alt="Stars" />
    </a>
    <a href="https://jitpack.io/#efelleto/diogenes">
      <img src="https://img.shields.io/jitpack/v/github/efelleto/diogenes?style=for-the-badge&logo=jitpack&logoColor=white&color=2596be" alt="JitPack" />
    </a>
    <a href="https://github.com/efelleto/diogenes/blob/main/LICENSE">
      <img src="https://img.shields.io/badge/License-MIT-0080ff?style=for-the-badge&logoColor=white" alt="License" />
    </a>
    <img src="https://img.shields.io/badge/kotlin-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white" alt="Kotlin" />
    <img src="https://img.shields.io/badge/Java-8%2B-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white" alt="Java 8+" />
  </p>

  <p>
    Diogenes is a sovereign, fast, and lightweight open-source licensing ecosystem designed to secure software and plugins. Built with Kotlin, it empowers creators to take full control of their infrastructure, eliminating third-party fees and hidden telemetry.
  </p>
</div>

<p align="center">
  <a href="https://efelleto.github.io/Diogenes/"><strong>📘 Explore the Documentation »</strong></a>
  <br /><br />
  <a href="https://efelleto.github.io/Diogenes/docs/intro">Getting Started</a>
  ·
  <a href="https://efelleto.github.io/Diogenes/blog">Dev Log</a>
  ·
  <a href="https://github.com/efelleto/Diogenes/issues">Report Bug</a>
</p>

---

## The Philosophy

[Diogenes of Sinope](https://en.wikipedia.org/wiki/Diogenes) was a Greek philosopher who sought **"Autarkeia"** (self-sufficiency). He lived in a barrel, carried a lantern in broad daylight searching for an "honest man," and rejected the corrupt "currency" of his time.

We named this project **Diogenes** because we believe developers should be self-sufficient:

- **Self-Hosting:** You don't need a third-party service to manage your licenses.
- **Sinope Protector:** Like Diogenes' birthplace, this tool protects your code, ensuring that only "honest" users can run your software through advanced protection.

## Features

- **Self-hosted authority:** You own the database, the bot, and the API.
- **Hardware ID (HWID) Binding:** Licenses are locked to a specific machine, preventing unauthorized sharing.
- **Asynchronous Handshake:** All communication is handled via `CompletableFuture`, ensuring zero impact on server performance.
- **Discord Management:** Manage your entire operation via Slash Commands.
- **No Telemetry:** We don't track you. You don't track your users (unless you want to).

## Installation

Add the repository and dependency to your project. Diogenes supports **Gradle (Kotlin DSL)** and **Maven**.
Check our [jitpack.io](https://jitpack.io/#efelleto/Diogenes) page for more information.

### Gradle (Kotlin DSL)

```kotlin
repositories {
    maven("https://jitpack.io")
}

dependencies {
    implementation("com.github.efelleto.Diogenes:SDK:{version}")
}
```

### Maven

```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>

<dependency>
    <groupId>com.github.efelleto.Diogenes</groupId>
    <artifactId>SDK</artifactId>
    <version>{version}</version>
</dependency>
```

## Integration Example

The SDK is designed to be minimal. The following block handles the entire authentication flow, including `license.yml` generation and auto-disabling the plugin on failure.

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

## Setup (Self-hosting)

- **Install:** 📥 [Download the latest server file](https://github.com/efelleto/diogenes/releases/latest) directly from our releases.
- **Requirements:** Java 8+, MongoDB instance, and a Discord Bot token.
- **Run:** Execute the jar via terminal:
  ```bash
  java -jar DiogenesServer-{version}.jar
  ```
- **Configuration:** Edit `settings.yml` with your **Bot Token** and **Mongo URI**.

## Infrastructure

Diogenes is designed to be extremely resource-efficient. You don't need a powerful machine to run the core server — a simple free-tier VPS is more than enough to handle thousands of license validations.

| Provider | Link |
|---|---|
| Oracle Cloud | https://www.oracle.com/br/cloud/free/ |
| Google Cloud (GCP) | https://cloud.google.com/free |
| Amazon AWS | https://aws.amazon.com/free |
| Digital Ocean | https://www.digitalocean.com |
| Linode (Akamai) | https://www.linode.com/lp/free-credit-100-5000 |
| Microsoft Azure | https://azure.microsoft.com/pricing/purchase-options/azure-account |

> For maximum performance and stability, we recommend a Linux-based machine (Ubuntu 22.04 LTS or Debian 11+) with at least 1 vCPU and 1GB RAM.

## Sinope Protector

Status: `Incoming...`

To protect your final JAR against deobfuscation, use the Sinope CLI tool:

```bash
java -jar Sinope.jar --input plugin.jar --output plugin-protected.jar
```

## Contributing

Contributions are always welcome! If you have an idea for a new feature or found a bug, don't hesitate to open a **Pull Request**.

## License

This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.

## Author

**efelleto**

[![GitHub](https://img.shields.io/badge/GitHub-100000?style=for-the-badge&logo=github&logoColor=white)](https://github.com/efelleto)

---

<p align="center">
  <b>If you found this project useful, please consider giving it a ⭐!</b>
  <br/>
  <i>"The most beautiful thing in the world is freedom of speech." — Diogenes</i>
</p>
