package dev.slne.surf.social.api

import dev.slne.surf.api.core.util.requiredService

private val api = requiredService<SurfSocialApi>()

interface SurfSocialApi {
    companion object : SurfSocialApi by api
}