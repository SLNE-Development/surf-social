package dev.slne.surf.social.chat.object;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class HistoryPair {
  private final int messageID;
  private final long sendTime;
}
