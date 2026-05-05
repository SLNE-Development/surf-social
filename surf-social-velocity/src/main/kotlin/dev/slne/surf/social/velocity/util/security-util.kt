package dev.slne.surf.social.velocity.util

import dev.slne.surf.api.core.util.random
import dev.slne.surf.social.velocity.config
import java.util.*
import javax.crypto.Cipher
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec

private val key by lazy {
    val keyBytes =
        Base64.getDecoder().decode(config.encryptionSecret)
    SecretKeySpec(keyBytes, "AES")
}

private val encoder by lazy {
    Base64.getUrlEncoder().withoutPadding()
}

fun encryptUuid(uuid: UUID): String {
    val nonce = ByteArray(12).also { random.nextBytes(it) }

    val cipher = Cipher.getInstance("AES/GCM/NoPadding")
    cipher.init(Cipher.ENCRYPT_MODE, key, GCMParameterSpec(128, nonce))

    val ciphertext = cipher.doFinal(uuid.toString().toByteArray(Charsets.UTF_8))

    val combined = nonce + ciphertext
    return encoder.encodeToString(combined)
}