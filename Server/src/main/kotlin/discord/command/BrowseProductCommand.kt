package dev.efelleto.diogenes.server.discord.command

import dev.efelleto.diogenes.core.model.ProductModel
import dev.efelleto.diogenes.core.model.License
import dev.efelleto.diogenes.server.database.MongoService
import dev.efelleto.diogenes.server.database.MongoService.sendAuditLog
import dev.efelleto.diogenes.server.util.KeyGenerator
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.components.actionrow.ActionRow
import net.dv8tion.jda.api.components.buttons.Button
import net.dv8tion.jda.api.components.selections.EntitySelectMenu
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent
import net.dv8tion.jda.api.events.interaction.component.EntitySelectInteractionEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import org.litote.kmongo.*
import java.awt.Color

class BrowserProductCommand : ListenerAdapter() {

    override fun onSlashCommandInteraction(event: SlashCommandInteractionEvent) {
        if (event.name != "browse-products") return

        // acknowledge the interaction immediately to prevent timeout
        event.deferReply(true).queue()

        // render the initial product list page
        renderPage(event.hook, 0)
    }

    override fun onButtonInteraction(event: ButtonInteractionEvent) {
        val id = event.componentId

        when {
            id.startsWith("nav_page_") -> {
                event.deferEdit().queue()
                val page = id.removePrefix("nav_page_").toInt()
                renderPage(event.hook, page)
            }

            id.startsWith("view_prod_") -> {
                event.deferEdit().queue()
                val productId = id.removePrefix("view_prod_")
                showProductDetails(event, productId)
            }

            id.startsWith("gen_key_") -> {
                event.deferReply(true).queue()
                val productId = id.removePrefix("gen_key_")
                val key = KeyGenerator.generate()

                MongoService.licenses.insertOne(License(key = key, productId = productId, active = true))
                sendAuditLog(event.guild!!, "Key Generated (Panel)", "📦 **Product:** `$productId`\n👤 **Staff:** ${event.user.asMention}", Color.decode("#3452eb"))

                event.hook.sendMessage("✅ **»** New License Key (`$productId`):\n```$key```").setEphemeral(true).queue()
            }

            id.startsWith("assign_role_") -> {
                val productId = id.removePrefix("assign_role_")
                val menu = EntitySelectMenu.create("select_role_$productId", EntitySelectMenu.SelectTarget.ROLE)
                    .setPlaceholder("Select the role for this product")
                    .setRequiredRange(1, 1)
                    .build()

                event.reply("Please select the role for product `$productId`:")
                    .setComponents(ActionRow.of(menu))
                    .setEphemeral(true)
                    .queue()
            }

            id.startsWith("delete_prod_") -> {
                event.deferReply(true).queue()
                val productId = id.removePrefix("delete_prod_")

                MongoService.products.deleteOne(ProductModel::id eq productId)
                MongoService.licenses.deleteMany(License::productId eq productId)
                sendAuditLog(event.guild!!, "Product Deleted", "🆔 **Product ID:** `$productId`\n👤 **Staff:** ${event.user.asMention}", Color.decode("#eb3434"))

                event.hook.sendMessage("✅ **»** Product `$productId` and all its keys have been deleted successfully.")
                    .setEphemeral(true).queue()
            }
        }
    }

    override fun onEntitySelectInteraction(event: EntitySelectInteractionEvent) {
        if (event.componentId.startsWith("select_role_")) {
            event.deferReply(true).queue()
            val productId = event.componentId.removePrefix("select_role_")
            val selectedRole = event.mentions.roles[0]

            MongoService.products.updateOne(
                ProductModel::id eq productId,
                setValue(ProductModel::roleId, selectedRole.id)
            )
            sendAuditLog(event.guild!!, "Role Updated", "📦 **Product:** `$productId`\n🎭 **New Role:** ${selectedRole.asMention}", Color.decode("#34eb52"))

            event.hook.sendMessage("✅ **»** Role ${selectedRole.asMention} is now linked to product `$productId`.")
                .setEphemeral(true).queue()
        }
    }

    private fun renderPage(hook: net.dv8tion.jda.api.interactions.InteractionHook, page: Int) {
        val products = MongoService.products.find().toList()
        val pageSize = 5
        val totalPages = if (products.isEmpty()) 1 else (products.size + pageSize - 1) / pageSize
        val start = page * pageSize
        val end = minOf(start + pageSize, products.size)

        val embed = EmbedBuilder()
            .setTitle("🏺 Diogenes Product Panel")
            .setDescription("Manage your software ecosystem.\nPage ${page + 1} of $totalPages")
            .setColor(Color.decode("#3452eb"))
            .setThumbnail(hook.jda.selfUser.avatarUrl)

        val buttons = mutableListOf<Button>()
        for (i in start until end) {
            val product = products[i]
            embed.addField("${i + 1}️⃣ ${product.displayName}", "ID: `${product.id}`", true)
            buttons.add(Button.secondary("view_prod_${product.id}", "${i + 1}"))
        }

        val rows = mutableListOf<ActionRow>()
        if (buttons.isNotEmpty()) {
            rows.add(ActionRow.of(buttons))
        }

        val nav = mutableListOf<Button>()
        if (page > 0) nav.add(Button.primary("nav_page_${page - 1}", "⬅️ Previous"))
        if (end < products.size) nav.add(Button.primary("nav_page_${page + 1}", "Next ➡️"))
        if (nav.isNotEmpty()) rows.add(ActionRow.of(nav))

        hook.editOriginalEmbeds(embed.build()).setComponents(rows).queue()
    }

    private fun showProductDetails(event: ButtonInteractionEvent, productId: String) {
        val product = MongoService.products.findOne(ProductModel::id eq productId) ?: return
        val totalKeys = MongoService.licenses.countDocuments(License::productId eq productId)
        val activeUsers = MongoService.licenses.countDocuments(and(License::productId eq productId, License::discordId ne null))

        val embed = EmbedBuilder()
            .setTitle("📦 Managing: ${product.displayName}")
            .setColor(Color.decode("#3452eb"))
            .addField("Linked Role", if (product.roleId != null) "<@&${product.roleId}>" else "`None`", true)
            .addField("Statistics", "️**»** Total Keys: `$totalKeys`\n**»** Active Users: `$activeUsers`", true)
            .setFooter("Product ID: $productId")
            .build()

        val actionRow = ActionRow.of(
            Button.secondary("assign_role_$productId", "🙋‍♂️ Set Role"),
            Button.secondary("gen_key_$productId", "⭐ Generate Key"),
            Button.danger("delete_prod_$productId", "❌ Delete Product"),
            Button.secondary("nav_page_0", "⬅️ Back")
        )

        event.hook.editOriginalEmbeds(embed).setComponents(actionRow).queue()
    }
}