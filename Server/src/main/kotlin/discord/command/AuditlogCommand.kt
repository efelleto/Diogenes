package dev.efelleto.diogenes.server.discord.command

import dev.efelleto.diogenes.server.database.MongoService
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.components.actionrow.ActionRow
import net.dv8tion.jda.api.components.buttons.Button
import net.dv8tion.jda.api.components.selections.EntitySelectMenu
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent
import net.dv8tion.jda.api.events.interaction.component.EntitySelectInteractionEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import org.litote.kmongo.*
import java.awt.Color

// simple data model for settings
data class GuildSettings(val _id: String, var logChannelId: String? = null, var logsEnabled: Boolean = false)

class AuditLogCommand : ListenerAdapter() {

    override fun onSlashCommandInteraction(event: SlashCommandInteractionEvent) {
        if (event.name != "audit-setup") return
        if (!event.member!!.hasPermission(Permission.ADMINISTRATOR)) {
            event.reply("❌ **»** No permission.").setEphemeral(true).queue()
            return
        }
        event.deferReply(true).queue()
        renderAuditPanel(event.hook)
    }

    private fun renderAuditPanel(hook: net.dv8tion.jda.api.interactions.InteractionHook) {
        val settings = MongoService.database.getCollection<GuildSettings>("settings")
            .findOne(GuildSettings::_id eq hook.interaction.guild?.id) ?: GuildSettings(hook.interaction.guild!!.id)

        val statusIcon = if (settings.logsEnabled) "🟢 ON" else "🔴 OFF"
        val channelMention = if (settings.logChannelId != null) "<#${settings.logChannelId}>" else "`Not Set`"

        val embed = EmbedBuilder()
            .setTitle("🛡️ Audit Log Configuration")
            .addField("**»** Current Status", statusIcon, true)
            .addField("**»** Logs Channel", channelMention, true)
            .setDescription("Be aware of everything happening in your licensing system.")
            .setColor(if (settings.logsEnabled) Color.decode("#34eb52") else Color.decode("#eb3434"))
            .build()

        val toggleBtn = if (settings.logsEnabled)
            Button.secondary("audit_toggle_off", "Turn OFF 🔴")
        else
            Button.secondary("audit_toggle_on", "Turn ON 🟢")

        val row1 = ActionRow.of(toggleBtn, Button.primary("audit_set_channel", "#️⃣ Select Channel"))

        hook.editOriginalEmbeds(embed).setComponents(row1).queue()
    }

    override fun onButtonInteraction(event: ButtonInteractionEvent) {
        val guildId = event.guild!!.id
        val collection = MongoService.database.getCollection<GuildSettings>("settings")

        when (event.componentId) {
            "audit_toggle_on", "audit_toggle_off" -> {
                event.deferEdit().queue()
                val enabled = event.componentId == "audit_toggle_on"
                collection.updateOne(GuildSettings::_id eq guildId, setValue(GuildSettings::logsEnabled, enabled), upsert())
                renderAuditPanel(event.hook)
            }
            "audit_set_channel" -> {
                val menu = EntitySelectMenu.create("audit_channel_select", EntitySelectMenu.SelectTarget.CHANNEL)
                    .setPlaceholder("Select the logs channel")
                    .build()
                event.reply("Choose a channel for logs:").setComponents(ActionRow.of(menu)).setEphemeral(true).queue()
            }
        }
    }

    override fun onEntitySelectInteraction(event: EntitySelectInteractionEvent) {
        if (event.componentId == "audit_channel_select") {
            event.deferEdit().queue()
            val channel = event.mentions.channels[0]
            MongoService.database.getCollection<GuildSettings>("settings")
                .updateOne(GuildSettings::_id eq event.guild!!.id, setValue(GuildSettings::logChannelId, channel.id), upsert())

            event.hook.editOriginal("✅ Log channel set to ${channel.asMention}").setComponents(emptyList()).queue()
        }
    }
}