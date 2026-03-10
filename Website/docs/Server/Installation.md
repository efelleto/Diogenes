---
sidebar_position: 1
---

# Installation

The **Diogenes Server** is the central brain of the ecosystem. Built with **Ktor** and **Netty**, it acts as a high-performance API that handles license validation requests, manages the MongoDB database, and powers the Discord Bot integration.

## Getting the Files

You don't need to compile the code yourself. We provide ready-to-use binaries for every stable release.

1. Go to the [Diogenes Releases](https://github.com/efelleto/diogenes/releases/latest) page.
2. Download the `DiogenesServer.jar` file.
3. Place the `.jar` file in a dedicated folder on your VPS or local machine.

## How to Run

Open your terminal or command prompt inside the folder where you placed the file and run:

```bash
java -jar DiogenesServer.jar
```

:::info
On the first run, the server will automatically generate a `config.yml` file and then shut down. You must configure this file before the server can start successfully.
:::

## Configuration

Open the generated `settings.yml` and fill in your credentials:
```
   port: The port where the API will listen (Default: 8080).
   mongo-uri: Your MongoDB connection string.
   bot-token: Your Discord Bot token from the Developer Portal.
```

## Troubleshooting

Running a backend server can lead to a few common "hiccups." Use this guide to identify and fix issues quickly based on the terminal logs.

### 1. Missing Configuration
**Error:** The server starts and immediately closes without showing the ASCII banner.

**Fix:** Ensure `settings.yml` exists in the root folder. Diogenes requires a valid `bot-token` and `mongo-uri` to boot. If these fields are empty, the server will shut down for security reasons.

---

### 2. Port Already in Use
**Error:** `[ERROR] FATAL: Failed to initialize services` followed by an *Address already in use* message.

**Fix:** Another application is already using port **8080**. Open `settings.yml` and change the `port` value to a different one (e.g., `8081` or `8888`), then restart the server.

---

### 3. MongoDB Connection Timeout
**Error:** The console hangs at `[INFO] DATABASE: Connecting to MongoDB...` for a long time.

**Fix:** Check if your MongoDB service is running. If you are using **MongoDB Atlas**, ensure your server's IP is whitelisted in the "Network Access" tab. Also, verify if your `mongoUri` includes the correct credentials.

---

### 4. Invalid Discord Token
**Error:** `[ERROR] JDA: Starting Discord Bot Manager...` followed by an authentication error.

**Fix:** Your Discord token is likely invalid or has been reset. Go to the [Discord Developer Portal](https://discord.com/developers/applications), reset your **Bot Token**, and update your configuration file.

---

## Testing the API

Once the server is running and the **Diogenes Banner** appears, you can verify the connection status manually:

1. **Access:** Open your browser and navigate to `http://your-server-ip:8080/`.
2. **Response:** You should see the message: `[+] Diogenes API is Running!`.
3. **Verification:** This confirms the **Netty** engine is live and ready to process requests from the SDK.

> **Note on VPS:** If you are running on a remote server, make sure the port is open in your firewall (UFW/Iptables), otherwise the SDK will not be able to connect.
