package dev.slne.surf.social.chat.service

import dev.slne.surf.social.chat.SurfChat
import dev.slne.surf.surfapi.core.api.util.*
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import java.util.*
import kotlin.io.path.*
import kotlin.system.measureTimeMillis
import kotlin.time.Duration.Companion.seconds


private const val MESSAGE_LIMIT = 5

object ChatFilterService {

    private val log = logger()
    private val blockedWords = mutableObjectSetOf<String>()
    private val allowedDomains = mutableObjectSetOf<String>()

    private val blockedPatterns = mutableObjectSetOf<Regex>()
    private val rateLimit =
        mutableObject2LongMapOf<UUID>().apply { defaultReturnValue(0) }.synchronize()

    private val messageCount =
        mutableObject2IntMapOf<UUID>().apply { defaultReturnValue(0) }.synchronize()

    private val validCharactersRegex = "^[a-zA-Z0-9/.:_,()%&=?!<>|#^\"²³+*~-äöü@ ]*$".toRegex()
    private val urlRegex =
        "((http|https|ftp)://)?([\\w-]+\\.)+[\\w-]+(/[\\w- ./?%&=]*)?".toRegex(RegexOption.IGNORE_CASE)

    private val TIME_FRAME = 10.seconds.inWholeMilliseconds

    private val regexReplacements = char2ObjectMapOf(
        'a' to "[a@4]", 'b' to "[b8]", 'c' to "c", 'd' to "d",
        'e' to "[e3]", 'f' to "f", 'g' to "[g9]", 'h' to "h",
        'i' to "[i1!]", 'j' to "j", 'k' to "k", 'l' to "[l1]",
        'm' to "m", 'n' to "n", 'o' to "[o0]", 'p' to "p",
        'q' to "q", 'r' to "r", 's' to "[s5]", 't' to "[t7]",
        'u' to "u", 'v' to "v", 'w' to "w", 'x' to "x",
        'y' to "y", 'z' to "[z2]"
    )


    fun loadBlockedWords() {
        val duration = measureTimeMillis {
            val path = SurfChat.instance.dataPath / "blocked.txt"
            with(path) {
                createDirectories()
                if (!exists()) createFile()
            }

            blockedWords.clear()
            blockedPatterns.clear()

            path.useLines { lines ->
                lines.map { it.trim() }
                    .filter { it.isNotEmpty() }
                    .forEach {
                        blockedWords.add(it)

                        val regex = getRegex(it)
                        blockedPatterns.add(Regex(regex, RegexOption.IGNORE_CASE))
                    }
            }
        }

        log.atInfo()
            .log("Loaded %s blocked words and their regexes in %sms", blockedWords.size, duration)
    }


    private fun getRegex(word: String): String {
        val regexBuilder = StringBuilder(word.length)
        for (c in word.toCharArray()) {
            regexBuilder.append(regexReplacements.getOrDefault(c, c.toString()))
        }

        return regexBuilder.toString()
    }

    fun containsBlocked(message: Component) = blockedPatterns.any {
        it.containsMatchIn(
            PlainTextComponentSerializer.plainText().serialize(message)
        )
    }


    fun containsLink(message: Component): Boolean {
        val plainMessage = PlainTextComponentSerializer.plainText().serialize(message)

        return urlRegex.findAll(plainMessage).any { result ->
            val domain = result.groupValues.getOrNull(3) ?: return@any false
            allowedDomains.none { domain.endsWith(it) }
        }
    }

    fun isValidInput(input: String): Boolean {
        return validCharactersRegex.matches(input)
    }

    fun isSpamming(uuid: UUID): Boolean {
        val currentTime = System.currentTimeMillis()
        val lastMessageTime = rateLimit.getLong(uuid)
        val count = messageCount.getInt(uuid)

        return if (currentTime - lastMessageTime < TIME_FRAME) {
            (count >= MESSAGE_LIMIT).also { if (!it) messageCount[uuid] = count + 1 }
        } else {
            rateLimit[uuid] = currentTime
            messageCount[uuid] = 1
            false
        }
    }
}