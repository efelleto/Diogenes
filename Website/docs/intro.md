---
sidebar_position: 1
---

# Getting Started

Welcome to the official **Diogenes** documentation. Diogenes is a high-performance licensing and security ecosystem designed for developers who demand total control and robust protection.

:::tip[Pro Tip]
Always keep your **Diogenes Server** updated to the latest version to ensure you have the most recent security patches and features.
:::

## Quick Start

To integrate Diogenes into your project, you'll need to use our SDK provided via [Jitpack](https://jitpack.io/#efelleto/diogenes).

### 1. Requirements
- **Java 17** or higher.
- A running instance of **MongoDB**.
- A **Discord Bot Token** (for notifications and management).

### 2. Dependency Setup

### Gradle
Add the JitPack repository and the Diogenes dependency to your `build.gradle.kts`:
```kotlin
repositories {
    maven("[https://jitpack.io](https://jitpack.io)")
}

dependencies {
    implementation("com.github.efelleto:diogenes:{version}")
}
```

### Maven
Add the JitPack repository and the Diogenes dependency to your `pom.xml`:
```xml
<repositories>
		<repository>
		    <id>jitpack.io</id>
		    <url>https://jitpack.io</url>
		</repository>
	</repositories>

  <dependency>
	    <groupId>com.github.efelleto.diogenes</groupId>
	    <artifactId>SDK</artifactId>
	    <version>{version}</version>
	</dependency>
  ```

