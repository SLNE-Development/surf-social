package dev.slne.surf.social.microservice.config

import org.slf4j.LoggerFactory
import java.nio.file.Path
import java.util.*
import kotlin.io.path.*

object SocialConfig {
    private val logger = LoggerFactory.getLogger(SocialConfig::class.java)

    lateinit var discordBotToken: String
        private set

    fun load(dataPath: Path) {
        val configFile = dataPath.resolve("social.properties")

        if (!configFile.exists()) {
            dataPath.createDirectories()
            configFile.writeText(
                """
                |# Discord Bot Token for fetching user information
                |discord-bot-token=YOUR_TOKEN_HERE
                """.trimMargin()
            )
            logger.warn("Config file created at {}. Please configure your Discord bot token.", configFile)
        }

        val properties = Properties()
        configFile.inputStream().use { properties.load(it) }

        discordBotToken = properties.getProperty("discord-bot-token", "")

        if (discordBotToken.isBlank() || discordBotToken == "YOUR_TOKEN_HERE") {
            logger.error("Discord bot token is not configured! Please set it in {}", configFile)
        }
    }
}

