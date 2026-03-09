package dev.efelleto.diogenes.server.database

import com.mongodb.client.MongoClient
import com.mongodb.client.MongoDatabase
import org.litote.kmongo.* import dev.efelleto.diogenes.core.model.License
import dev.efelleto.diogenes.core.model.ProductModel
import dev.efelleto.diogenes.server.Logger
import dev.efelleto.diogenes.server.discord.command.GuildSettings
import org.litote.kmongo.eq

object MongoService {
     lateinit var client: MongoClient
     lateinit var database: MongoDatabase

    fun sendAuditLog(guild: net.dv8tion.jda.api.entities.Guild, title: String, message: String, color: java.awt.Color) {
        val collection = MongoService.database.getCollection<GuildSettings>("settings")
        val settings = collection.findOne(GuildSettings::_id eq guild.id) ?: return

        if (!settings.logsEnabled || settings.logChannelId == null) return

        val channel = guild.getTextChannelById(settings.logChannelId!!)
        val logEmbed = net.dv8tion.jda.api.EmbedBuilder()
            .setTitle("🛡️ Audit Log: $title")
            .setDescription(message)
            .setTimestamp(java.time.Instant.now())
            .setColor(color)
            .setFooter("Diogenes Security System")
            .build()

        channel?.sendMessageEmbeds(logEmbed)?.queue()
    }

    fun init(uri: String) {
        client = KMongo.createClient(uri)
        database = client.getDatabase("diogenes")

        Logger.info("DATABASE", "Connection established via KMongo.")
    }

    val licenses get() = database.getCollection<License>("licenses")
    val products get() = database.getCollection<ProductModel>("products")
}