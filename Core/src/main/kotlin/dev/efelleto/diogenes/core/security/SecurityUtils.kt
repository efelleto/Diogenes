package dev.efelleto.diogenes.core.security

import java.net.NetworkInterface
import java.security.MessageDigest

object SecurityUtils {

    // calculates an SHA-256 hash for the given input string.
    // this is used to ensure data privacy and consistency across platforms.
    fun calculateSha256(input: String): String {
        return MessageDigest.getInstance("SHA-256")
            .digest(input.toByteArray())
            .joinToString("") { "%02x".format(it) }
    }

    // public gateway to retrieve the unique machine identifier (HWID)
    // this is the method called by the SDK to identify the machine
    fun getHWID(): String {
        return generateHWID()
    }

    /**
     * Internal logic to generate a hardware fingerprint.
     * It attempts to collect MAC addresses and falls back to system properties
     * to ensure uniqueness even in restricted environments (Docker/Containers).
     */
    private fun generateHWID(): String {
        val hwData = StringBuilder()

        try {
            // collect MAC Addresses from all active physical network interfaces
            val networkInterfaces = NetworkInterface.getNetworkInterfaces()
            if (networkInterfaces != null) {
                for (netInterface in networkInterfaces) {
                    if (netInterface.isLoopback || netInterface.isVirtual) continue // skip loopback (localhost) and virtual interfaces (VPNs/VMs)

                    val mac = netInterface.hardwareAddress
                    if (mac != null) {
                        hwData.append(mac.joinToString("") { "%02x".format(it) })
                    }
                }
            }
        } catch (e: Exception) {
            // silent fallback if network interface access is denied
        }

        // append system-specific environment variables to increase entropy
        // this ensures different machines have different hashes even without MAC access
        hwData.append(System.getProperty("os.name"))
        hwData.append(System.getProperty("os.arch"))
        hwData.append(System.getProperty("os.version"))
        hwData.append(System.getProperty("user.name"))
        hwData.append(Runtime.getRuntime().availableProcessors())

        //  return a solid 24-character hash for easier database storage and comparison
        return calculateSha256(hwData.toString()).take(24).uppercase()
    }
}