package dev.slne.surf.social.chat.history;


import com.google.gson.Gson;
import com.google.gson.stream.JsonWriter;
import dev.slne.surf.social.chat.object.ChatUser;
import kotlinx.serialization.encoding.Encoder;
import kotlinx.serialization.json.Json;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;

import java.io.IOException;
import java.io.Writer;
import java.util.Date;
import java.util.UUID;

public class SentMessage {
    private String message;
    private long timestamp;
    private MessageType type;

    public SentMessage(String message, long timestamp, MessageType type) {
        this.message = message;
        this.timestamp = timestamp;
        this.type = type;
    }

    public static SentMessage deserialize(String json){
        return new Gson().fromJson(json, SentMessage.class);
    }

    public String getMessage() {
        return message;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public MessageType getType() {
        return type;
    }

    public String serialize(){
        return new Gson().toJson(this, SentMessage.class);
    }
    public Date getDate(){
        return new Date(getTimestamp()*1000);
    }

}
