package dev.slne.surf.social.chat.service

import com.google.gson.Gson
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import dev.slne.surf.social.chat.SurfChat
import dev.slne.surf.social.chat.history.SentMessage
import dev.slne.surf.social.chat.`object`.ChatUser
import dev.slne.surf.surfapi.core.api.util.logger
import dev.slne.surf.surfapi.core.api.util.mutableObjectSetOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.sql.SQLException
import java.util.*

object DatabaseService {
    private val log = logger()
    private val config = SurfChat.instance.config
    private lateinit var dataSource: HikariDataSource

    suspend fun connect() {
        val config = HikariConfig().apply {
            jdbcUrl =
                "jdbc:mysql://" + config.getString("database.host") + ":" + config.getInt("database.port") + "/" + config.getString(
                    "database.database"
                )
            username = config.getString("database.username")
            password = config.getString("database.password")

            addDataSourceProperty("cachePrepStmts", "true")
            addDataSourceProperty("prepStmtCacheSize", "250")
            addDataSourceProperty("prepStmtCacheSqlLimit", "2048")
        }


        dataSource = HikariDataSource(config)
        createTable()
    }

    fun disconnect() {
        dataSource.close()
    }

    private suspend fun createTable() = withContext(Dispatchers.IO) {
        val sql = "CREATE TABLE IF NOT EXISTS surf_chat_user (" +
                "uuid VARCHAR(36) PRIMARY KEY," +
                "pm_enabled BOOLEAN NOT NULL," +
                "ignore_list TEXT," +
                "sent_messages TEXT" +
                ");"

        try {
            dataSource.connection.use { conn ->
                conn.createStatement().use { stmt ->
                    stmt.execute(sql)
                }
            }
        } catch (e: SQLException) {
            log.atSevere()
                .withCause(e)
                .log("Failed to create table")
        }
    }

    suspend fun loadUser(uuid: UUID): ChatUser = withContext(Dispatchers.IO) {
        val sql = "SELECT uuid, pm_enabled, ignore_list, sent_messages FROM surf_chat_user WHERE uuid = ?"

            try {
                dataSource.connection.use { conn ->
                    conn.prepareStatement(sql).use { pstmt ->
                        pstmt.setString(1, uuid.toString())
                        val rs = pstmt.executeQuery()
                        if (rs.next()) {
                            val ignoreList = mutableObjectSetOf<UUID>()
                            val ignoreListStr = rs.getString("ignore_list")
                            val sentMessages = ChatUser.deserializeSentMessagesList(rs.getString("sent_messages"))

                            if (ignoreListStr.isNotEmpty()) {
                                for (id in ignoreListStr.split(",").filter { it.isNotBlank() }) {
                                    ignoreList.add(UUID.fromString(id))
                                }
                            }

                            return@withContext ChatUser(UUID.fromString(rs.getString("uuid")), rs.getBoolean("pm_enabled"), ignoreList, sentMessages)
                        }
                    }
                }
            } catch (e: SQLException) {
                log.atSevere()
                    .withCause(e)
                    .log("Failed to load user")
            }

        return@withContext ChatUser(uuid)
        }


    suspend fun saveUser(user: ChatUser) = withContext(Dispatchers.IO) {
        val sql =
            "INSERT INTO surf_chat_user (uuid, pm_enabled, ignore_list, sent_messages) VALUES (?, ?, ?, ?) ON DUPLICATE KEY UPDATE pm_enabled = ?, ignore_list = ?, sent_messages = ?"

        try {
            dataSource.connection.use { conn ->
                conn.prepareStatement(sql).use { pstmt ->
                    pstmt.setString(1, user.uuid.toString())
                    pstmt.setBoolean(2, user.toggledPM)

                    val ignoreListStr = user.ignoreList.joinToString(",") { it.toString() }
                    val sentMessages = user.serializeSentMessages()

                    pstmt.setString(3, ignoreListStr)
                    pstmt.setString(4, sentMessages)
                    pstmt.setBoolean(5, user.toggledPM)
                    pstmt.setString(6, ignoreListStr)
                    pstmt.setString(7, sentMessages)
                    pstmt.executeUpdate()
                }
            }
        } catch (e: SQLException) {
            log.atSevere()
                .withCause(e)
                .log("Failed to save user")
        }
    }

    suspend fun saveAll() {
        ChatUser.cache.asMap().values.forEach { user ->
            this.saveUser(user)
        }
    }
}