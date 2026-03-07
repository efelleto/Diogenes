package dev.efelleto.diogenes.server.discord.command

import dev.efelleto.diogenes.core.model.License
import dev.efelleto.diogenes.server.database.MongoService
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.components.actionrow.ActionRow
import net.dv8tion.jda.api.components.buttons.Button
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.dv8tion.jda.api.interactions.InteractionHook
import org.litote.kmongo.eq
import org.litote.kmongo.ne
import java.awt.Color

class AdminCommands : ListenerAdapter() {

    private val pageSize = 5

    override fun onSlashCommandInteraction(event: SlashCommandInteractionEvent) {
        if (event.name != "browse-customers" && event.name != "browse-licenses") return

        if (event.member == null || !event.member!!.hasPermission(Permission.ADMINISTRATOR)) {
            event.reply("❌ **»** No permission.").setEphemeral(true).queue()
            return
        }

        event.deferReply(true).queue()

        when (event.name) {
            "browse-customers" -> renderCustomers(event.hook, 0)
            "browse-licenses" -> renderLicenses(event.hook, 0, "all")
        }
    }

    override fun onButtonInteraction(event: ButtonInteractionEvent) {
        val parts = event.componentId.split(":")
        val action = parts[0]

        when (action) {
            "cust_page" -> {
                event.deferEdit().queue()
                renderCustomers(event.hook, parts[1].toInt())
            }
            "lic_page" -> {
                event.deferEdit().queue()
                renderLicenses(event.hook, parts[1].toInt(), parts[2])
            }
            "lic_filter" -> {
                event.deferEdit().queue()
                renderLicenses(event.hook, 0, parts[1])
            }
        }
    }

    // browse-customers
    private fun renderCustomers(hook: InteractionHook, page: Int) {
        val allActive = MongoService.licenses.find(License::discordId ne null).toList()

        val grouped = allActive.groupBy { it.discordId!! }
        val userIds = grouped.keys.toList()

        val totalPages = if (userIds.isEmpty()) 1 else (userIds.size + pageSize - 1) / pageSize
        val start = page * pageSize
        val end = minOf(start + pageSize, userIds.size)

        val embed = EmbedBuilder()
            .setTitle("👥 Diogenes Customers")
            .setColor(Color.decode("#3452eb"))
            .setFooter("Page ${page + 1} of $totalPages")

        if (userIds.isEmpty()) {
            embed.setDescription("No customers found.")
        } else {
            for (i in start until end) {
                val userId = userIds[i]
                val licenses = grouped[userId] ?: emptyList()

                val userMention = "<@$userId>"
                val details = licenses.joinToString("\n") { "📦 __${it.productId}__ | 🔑 `${it.key}`" }

                embed.addField("👤 Customer Data", "User: $userMention\n$details", false)
            }
        }

        val buttons = mutableListOf<Button>()
        if (page > 0) buttons.add(Button.primary("cust_page:${page - 1}", "⬅️ Previous"))
        if (end < userIds.size) buttons.add(Button.primary("cust_page:${page + 1}", "Next ➡️"))

        val row = if (buttons.isNotEmpty()) ActionRow.of(buttons) else null
        hook.editOriginalEmbeds(embed.build()).apply {
            if (row != null) setComponents(row) else setComponents(emptyList())
        }.queue()
    }

    // browse-licenses
    private fun renderLicenses(hook: InteractionHook, page: Int, filter: String) {
        val licenses = when (filter) {
            "used" -> MongoService.licenses.find(License::discordId ne null).toList()
            "unused" -> MongoService.licenses.find(License::discordId eq null).toList()
            else -> MongoService.licenses.find().toList()
        }

        val totalPages = if (licenses.isEmpty()) 1 else (licenses.size + pageSize - 1) / pageSize
        val start = page * pageSize
        val end = minOf(start + pageSize, licenses.size)

        val embed = EmbedBuilder()
            .setTitle("🗝️ License Manager")
            .setColor(if (filter == "unused") Color.decode("#3452eb") else Color.decode("#3452eb"))
            .setDescription("Filter: **${filter.uppercase()}**")
            .setFooter("Page ${page + 1} of $totalPages")

        for (i in start until end) {
            val lic = licenses[i]
            val status = if (lic.discordId != null) "👤 Owner: <@${lic.discordId}>\n" else "🟢 **Unused**\n"
            embed.addField("Key: `${lic.key}`", "${status}📦 Product: `${lic.productId}`", false)
        }

        val filterRow = ActionRow.of(
            Button.secondary("lic_filter:all", "📘 Show All").withDisabled(filter == "all"),
            Button.secondary("lic_filter:used", "✅ Used").withDisabled(filter == "used"),
            Button.secondary("lic_filter:unused", "📪 Unused").withDisabled(filter == "unused")
        )

        val navButtons = mutableListOf<Button>()
        if (page > 0) navButtons.add(Button.primary("lic_page:${page - 1}:$filter", "⬅️ Previous"))
        if (end < licenses.size) navButtons.add(Button.primary("lic_page:${page + 1}:$filter", "Next ➡️"))

        val rows = mutableListOf(filterRow)
        if (navButtons.isNotEmpty()) rows.add(ActionRow.of(navButtons))

        hook.editOriginalEmbeds(embed.build()).setComponents(rows).queue()
    }
}