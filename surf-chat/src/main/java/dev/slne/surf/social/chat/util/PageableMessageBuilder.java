package dev.slne.surf.social.chat.util;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;

import lombok.Getter;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.event.ClickEvent;

import org.bukkit.command.CommandSender;

@Getter
public class PageableMessageBuilder {
    private final ObjectList<Component> lines = new ObjectArrayList<>();
    private String pageCommand = "An error occurred while trying to display the page.";

    public PageableMessageBuilder addLine(Component line) {
        lines.add(line);
        return this;
    }

    public PageableMessageBuilder setPageCommand(String command) {
        this.pageCommand = command;
        return this;
    }

    public void send(CommandSender sender, Integer page) {
        int linesPerPage = 10;
        int totalPages = (int) Math.ceil((double) lines.size() / linesPerPage);
        int start = (page - 1) * linesPerPage;
        int end = Math.min(start + linesPerPage, lines.size());
    
        if (page < 1 || page > totalPages) {
            sender.sendMessage(Component.text("Seite " + page + " existiert nicht.", NamedTextColor.RED));
            return;
        }
    
        Component message = Component.text("==== Seite " + page + " von " + totalPages + " ====", Colors.VARIABLE_KEY).decorate(TextDecoration.BOLD).append(Component.newline());
        Component navigation = this.getComponent(page, totalPages);
    
        for (int i = start; i < end; i++) {
            message = message.append(lines.get(i)).append(Component.newline()).decoration(TextDecoration.BOLD, false);
        }

        if (!navigation.equals(Component.empty())) {
            message = message.append(navigation);
        }
    
        sender.sendMessage(Component.newline().append(message));
    }

    private Component getComponent(int page, int totalPages) {
        Component navigation = Component.empty();

        if (page > 1) {
            navigation = navigation.append(Component.text("[<< Zurück] ", Colors.SUCCESS).clickEvent(ClickEvent.runCommand(this.pageCommand.replace("%page%", String.valueOf(page - 1)))));
        }

        if (page < totalPages) {
            navigation = navigation.append(Component.text("[Weiter >>]", Colors.SUCCESS).clickEvent(ClickEvent.runCommand(this.pageCommand.replace("%page%", String.valueOf(page + 1)))));
        }

        return navigation;
    }
}
