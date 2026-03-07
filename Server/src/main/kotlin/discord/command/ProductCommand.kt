package dev.efelleto.diogenes.server.discord.command

import dev.efelleto.diogenes.core.model.ProductModel
import dev.efelleto.diogenes.server.database.MongoService
import dev.efelleto.diogenes.server.database.MongoService.sendAuditLog
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import org.litote.kmongo.save
import java.awt.Color
import java.time.Instant

class ProductCommands : ListenerAdapter() {

    override fun onSlashCommandInteraction(event: SlashCommandInteractionEvent) {
        if (event.name == "create-product") {
            val id = event.getOption("id")?.asString ?: return
            val name = event.getOption("name")?.asString ?: return

            val newProduct = ProductModel(id, name, roleId = "0000000000")
            MongoService.products.save(newProduct)

            val successEmbed = EmbedBuilder()
                .setTitle("📦 Diogenes | New Product Registered")
                .setColor(Color.decode("#3452eb"))
                .setDescription("A new product has been successfully added to the catalog.")
                .addField("🏷️ Display Name", "**$name**", true)
                .addField("🆔 Product ID", "`$id`", true)
                .addField("🎭 Role Link", "None (Edit via /browse-products)", false)
                .setThumbnail(event.jda.selfUser.avatarUrl)
                .setFooter("Diogenes Licensing System", event.user.avatarUrl)
                .setTimestamp(Instant.now())
                .build()

            event.replyEmbeds(successEmbed).setEphemeral(true).queue()

            sendAuditLog(
                event.guild!!,
                "Product Created",
                "🛠️ **Staff:** ${event.user.asMention}\n🆔 **ID:** `$id`\n🏷️ **Name:** `$name`",
                Color.decode("#34eb52")
            )
        }
    }
}