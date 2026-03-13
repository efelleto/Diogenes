package dev.efelleto.diogenes.core.security

import java.net.NetworkInterface
import java.security.MessageDigest
import java.io.File

object SecurityUtils {

    fun calculateSha256(input: String): String {
        return MessageDigest.getInstance("SHA-256")
            .digest(input.toByteArray())
            .joinToString("") { "%02x".format(it) }
    }

    fun getHWID(): String {
        return generateHWID()
    }

    private fun generateHWID(): String {
        val hwData = StringBuilder()
        val macList = mutableListOf<String>()

        try {
            val networkInterfaces = NetworkInterface.getNetworkInterfaces()
            if (networkInterfaces != null) {
                for (netInterface in networkInterfaces) {
                    // Ignore loopback and virtual interfaces that changes on reboots
                    if (netInterface.isLoopback || netInterface.isVirtual || netInterface.name.contains("vboxnet") || netInterface.name.contains("docker")) continue

                    val mac = netInterface.hardwareAddress
                    if (mac != null) {
                        macList.add(mac.joinToString("") { "%02x".format(it) })
                    }
                }
            }
        } catch (e: Exception) {}

        // guarantee that MAC_A + MAC_B don't turn to MAC_B + MAC_A on next reboot
        macList.sort()
        macList.forEach { hwData.append(it) }

        // new hardware information getter (removed user.name and os.version which is volatiles)
        hwData.append(System.getProperty("os.name"))
        hwData.append(System.getProperty("os.arch"))
        hwData.append(Runtime.getRuntime().availableProcessors())

        // unique os idenfitifer (linux/mac)
        if (System.getProperty("os.name").lowercase().contains("linux")) {
            val machineId = File("/etc/machine-id")
            if (machineId.exists()) hwData.append(machineId.readText().trim())
        } else if (System.getProperty("os.name").lowercase().contains("mac")) {
            // in mac, the IOPlatformUUID is the best "anchor"
            try {
                val process = Runtime.getRuntime().exec("ioreg -rd1 -c IOPlatformExpertDevice")
                val uuid = process.inputStream.bufferedReader().readText()
                    .split("IOPlatformUUID")[1]
                    .split("\"")[2]
                hwData.append(uuid)
            } catch (e: Exception) {}
        }

        return calculateSha256(hwData.toString()).take(24).uppercase()
    }
}