package dev.slne.surf.social.chat.object;

import lombok.Builder;
import net.kyori.adventure.text.Component;

@Builder
public record Message(String sender, String receiver, Component message) {}
