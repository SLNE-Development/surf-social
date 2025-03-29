package dev.slne.surf.social.chat.provider

import dev.slne.surf.social.chat.`object`.ChatPunishment
import dev.slne.surf.surfapi.core.api.util.char2ObjectMapOf
import java.nio.file.Path
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.io.path.*

class WordBlacklistProvider(file: Path) {

    private val blockedWords = HashMap<String, ChatPunishment?>()
    private val wordStorage = HashMap<Regex, String>()

    init {
        if (!file.exists()) file.createFile()

        file.toFile().readLines().forEach {
            if (it.isNotEmpty()){
                if(it.contains("{")){
                    val word = it.trim().substring(0, it.indexOf("{")).lowercase()
                    addPunishment(word, ChatPunishment.buildFromArgs(it.trim().substring(it.indexOf("{"), it.lastIndexOf("}")+1)))
                }
                else addPunishment(it.trim(), null)
            }
        }
    }

    fun addPunishment(word:String, punishment:ChatPunishment?){
        blockedWords[word] = punishment
        if (!wordStorage.containsValue(word)) wordStorage.put(getRegex(word), word)
    }

    fun save(file:Path){
        file.deleteIfExists()
        file.createFile()
        val list = ArrayList<String>()
        for (set in wordStorage.values){
            list.add(set + (blockedWords[set]?.asString() ?: ""))
        }
        file.writeLines(list)
    }



    companion object {
        lateinit var instance:WordBlacklistProvider
        fun getPunishment(word:String):ChatPunishment? {
            for (regex in instance.wordStorage.keys){
                if (regex.containsMatchIn(word)){
                    return instance.blockedWords[instance.wordStorage.get(regex)] ?: ConfigurationProvider.getDefaultPunishment(ChatPunishment.Punishment.BLOCK)
                }
            }
            return null
        }

        fun getExactBlockedWord(text:String):String?{
            for (regex in instance.wordStorage.keys){
                if (regex.containsMatchIn(text)){
                    return instance.wordStorage.get(regex);
                }
            }
            return null
        }

        fun addPunishment(word:String, punishment:ChatPunishment?){
            instance.addPunishment(word, punishment)
        }

        fun removePunishment(word:String):Boolean {
            for (set in instance.wordStorage){
                if (set.value == word.lowercase()){
                    instance.wordStorage.remove(set.key)
                    break
                }
            }
            val b = instance.blockedWords.containsKey(word)
            instance.blockedWords.remove(word)
            return b
        }

        fun getWords(): MutableCollection<String> {
            return instance.wordStorage.values
        }

        private val regexReplacements = char2ObjectMapOf(
            'a' to "[a@4]", 'b' to "[b8]", 'c' to "c", 'd' to "d",
            'e' to "[e3]", 'f' to "f", 'g' to "[g9]", 'h' to "h",
            'i' to "[i1!]", 'j' to "j", 'k' to "k", 'l' to "[l1]",
            'm' to "m", 'n' to "n", 'o' to "[o0]", 'p' to "p",
            'q' to "q", 'r' to "r", 's' to "[s5]", 't' to "[t7]",
            'u' to "u", 'v' to "v", 'w' to "w", 'x' to "x",
            'y' to "y", 'z' to "[z2]"
        )
        private fun getRegex(word: String): Regex {
            val word = word.trim()
            val regexBuilder = StringBuilder(word.length)
            for (c in word.toCharArray()) {
                regexBuilder.append(regexReplacements.getOrDefault(c, c.toString()))
            }
            return Regex(regexBuilder.toString(), RegexOption.IGNORE_CASE)
        }
    }
}