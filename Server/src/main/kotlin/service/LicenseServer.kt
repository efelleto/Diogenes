package dev.efelleto.diogenes.server.service

import dev.efelleto.diogenes.core.model.License
import dev.efelleto.diogenes.core.model.LicenseRequest
import dev.efelleto.diogenes.core.model.LicenseResponse
import dev.efelleto.diogenes.server.database.MongoService
import org.litote.kmongo.*

object LicenseService {

    /**
     * Validates a license request against the database and hardware/IP authentication.
     */

    fun validate(request: LicenseRequest, clientIp: String): LicenseResponse {
        // Database lookup
        val license = MongoService.licenses.findOne(
            License::key eq request.key,
            License::productId eq request.productId
        )

        // Existence
        if (license == null) {
            return LicenseResponse(isAuthorized = false, message = "Invalid license key for this product.")
        }

        // Active Status
        if (!license.active) {
            return LicenseResponse(isAuthorized = false, message = "This license has been suspended.")
        }

        // First-time activation (HWID & IP Lock)
        if (license.hwid == null) {
            MongoService.licenses.updateOne(
                License::key eq request.key,
                combine(
                    setValue(License::hwid, request.hwid),
                    setValue(License::lastIp, clientIp)
                )
            )
            return LicenseResponse(isAuthorized = true, message = "First-time activation successful. Locked to hardware.")
        }

        // Hardware ID Check
        if (license.hwid != request.hwid) {
            return LicenseResponse(isAuthorized = false, message = "Hardware mismatch detected.")
        }

        // IP/Network Check
        if (license.lastIp != clientIp) {
            return LicenseResponse(isAuthorized = false, message = "Network mismatch. License locked to another IP.")
        }

        return LicenseResponse(isAuthorized = true, message = "Authorized. Welcome back!")
    }
}