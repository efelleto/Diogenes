package dev.efelleto.diogenes.server.discord

import dev.efelleto.diogenes.server.discord.command.*
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.Activity
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions
import net.dv8tion.jda.api.interactions.commands.build.Commands
import net.dv8tion.jda.api.interactions.commands.OptionType

object BotManager {
    fun start(token: String) {
        val jda = JDABuilder.createDefault(token)
            .setActivity(Activity.playing("Server running"))
            .addEventListeners(
                ProductCommands(), LicenseCommand(), RedeemCommand(), AdminCommands(), BrowserProductCommand(), AuditLogCommand(),
            )
            .build()

        jda.updateCommands().addCommands(
            Commands.slash("create-product", "「 Admin 」Register a new product")
                .addOption(OptionType.STRING, "id", "Internal ID (ex: plugin1)", true)
                .addOption(OptionType.STRING, "name", "Display name", true)
                .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.ADMINISTRATOR)),

            Commands.slash("create-license", "「 Admin 」 Generate a new license key")
                .addOption(OptionType.STRING, "product", "Product ID (ex: plugin1)", true)
                .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.ADMINISTRATOR)),

            Commands.slash("browse-customers", "「 Admin 」 List all active customers and their products")
                .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.ADMINISTRATOR)),

            Commands.slash("browse-licenses", "「 Admin 」List all license keys")
                .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.ADMINISTRATOR)),

            Commands.slash("audit-setup", "「 Admin 」 Configure the security and action logs")
                .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.ADMINISTRATOR)),

            Commands.slash("browse-products", "「 Public 」 Open the interactive product management panel"),

            Commands.slash("redeem", "「 Public 」Redeem your license key")
                .addOption(OptionType.STRING, "key", "Your license key (XXXXX-XXXXX...)", true)
        ).queue()

        println("[Discord] Bot is online. All commands updated.")
    }
}