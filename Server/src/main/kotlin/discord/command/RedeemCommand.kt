package dev.efelleto.diogenes.server.discord.command

import dev.efelleto.diogenes.core.model.License
import dev.efelleto.diogenes.core.model.ProductModel
import dev.efelleto.diogenes.server.database.MongoService
import dev.efelleto.diogenes.server.database.MongoService.sendAuditLog
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import org.litote.kmongo.eq
import org.litote.kmongo.findOne
import org.litote.kmongo.setValue
import java.awt.Color

class RedeemCommand : ListenerAdapter() {

    override fun onSlashCommandInteraction(event: SlashCommandInteractionEvent) {
        if (event.name != "redeem") return

        val inputKey = event.getOption("key")?.asString ?: return
        val guild = event.guild ?: return

        // defer reply to handle database and role assignments
        event.deferReply(true).queue()

        val license = MongoService.licenses.findOne(License::key eq inputKey)

        if (license == null) {
            event.hook.sendMessage("❌ **»** This key is invalid or does not exist.")
                .setEphemeral(true).queue()
            return
        }

        if (license.discordId != null) {
//            val ownerStatus = if (license.discordId == event.user.id) "you already own it" else "it belongs to another user"
            event.hook.sendMessage("⚠️ **»** This key has already been redeemed.") // ($ownerStatus)
                .setEphemeral(true).queue()
            return
        }

        MongoService.licenses.updateOne(License::key eq inputKey, setValue(License::discordId, event.user.id))

        val product = MongoService.products.findOne(ProductModel::id eq license.productId)
        val productName = product?.displayName ?: "Unknown Product"
        val roleId = product?.roleId

        sendAuditLog(
            guild,
            "Key Redeemed",
            "👤 **User:** ${event.user.asMention} (`${event.user.id}`)\n📦 **Product:** $productName\n🔑 **Key:** `${inputKey}`",
            Color.decode("#3452eb")
        )

        var roleStatus = "`N/A`"

        // 5. Automatically assign the Discord Role if configured
        if (roleId != null && roleId.isNotEmpty() && roleId != "0000000000") {
            val role = guild.getRoleById(roleId)
            if (role != null) {
                roleStatus = role.asMention
                try {
                    guild.addRoleToMember(event.user, role).queue(
                        { /* Success */ },
                        { error -> println("Failed to add role: ${error.message}") }
                    )
                } catch (e: Exception) {
                    roleStatus += " (⚠️ Failed to assign: missing permissions)"
                }
            }
        }

        // 6. Build the feedback message
        val embed = EmbedBuilder()
            .setTitle("✅ Product redeemed successfully!")
            .setColor(Color.decode("#3452eb"))
            .setThumbnail(event.user.effectiveAvatarUrl)
            .addField("📦 Product", productName, true)
            .addField("👤 Owner", event.user.asMention, true)
            .addField("🛡️ Role Granted", roleStatus, false)
            .setFooter("Diogenes Licensing System", event.jda.selfUser.avatarUrl)
            .build()

        event.hook.sendMessageEmbeds(embed).setEphemeral(true).queue()
    }
}