package dev.efelleto.diogenes.server.discord.command

import dev.efelleto.diogenes.core.model.License
import dev.efelleto.diogenes.core.model.ProductModel
import dev.efelleto.diogenes.server.database.MongoService
import dev.efelleto.diogenes.server.database.MongoService.sendAuditLog
import dev.efelleto.diogenes.server.util.KeyGenerator
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import org.litote.kmongo.eq
import org.litote.kmongo.findOne
import java.awt.Color
import java.time.Instant

class LicenseCommand : ListenerAdapter() {

    override fun onSlashCommandInteraction(event: SlashCommandInteractionEvent) {
        if (event.name != "create-license") return

        val productId = event.getOption("product")?.asString ?: return
        val product = MongoService.products.findOne(ProductModel::id eq productId)

        if (product == null) {
            event.reply("❌ **»** The product `$productId` was not found in the database.")
                .setEphemeral(true).queue()
            return
        }

        val generatedKey = KeyGenerator.generate()

        val newLicense = License(
            key = generatedKey,
            productId = productId,
            active = true
        )

        MongoService.licenses.insertOne(newLicense)

        sendAuditLog(
            event.guild!!,
            "Manual License Generated",
            "🔑 **Key:** `${generatedKey}`\n📦 **Product:** ${product.displayName}\n👤 **Staff:** ${event.user.asMention}",
            Color.decode("#34eb52")
        )

        val successEmbed = EmbedBuilder()
            .setTitle("🏺 Diogenes | License Generated")
            .setColor(Color.decode("#3452eb"))
            .setDescription("A new license has been registered in the database.")
            .addField("📦 Product", "`${product.displayName}`", true)
            .addField("🆔 Product ID", "`${product.id}`", true)
            .addField("🔑 License Key", "```$generatedKey```", false)
            .addField("👤 Staff Member", event.user.asMention, true)
            .setThumbnail(event.jda.selfUser.avatarUrl)
            .setFooter("Diogenes Licensing System", event.user.avatarUrl)
            .setTimestamp(Instant.now())
            .build()

        event.replyEmbeds(successEmbed).queue()
    }
}