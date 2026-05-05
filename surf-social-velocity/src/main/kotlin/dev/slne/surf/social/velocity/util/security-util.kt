package dev.slne.surf.social.velocity.util

import dev.slne.surf.social.velocity.config
import java.security.SecureRandom
import java.util.*
import javax.crypto.Cipher
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec

fun encryptUuid(uuid: UUID): String {
    val keyBytes =
        Base64.getDecoder().decode(config.encryptionSecret)
    val key = SecretKeySpec(keyBytes, "AES")

    val nonce = ByteArray(12).also { SecureRandom().nextBytes(it) }

    val cipher = Cipher.getInstance("AES/GCM/NoPadding")
    cipher.init(Cipher.ENCRYPT_MODE, key, GCMParameterSpec(128, nonce))

    val ciphertext = cipher.doFinal(uuid.toString().toByteArray(Charsets.UTF_8))

    val combined = nonce + ciphertext
    return Base64.getUrlEncoder().withoutPadding().encodeToString(combined)
}