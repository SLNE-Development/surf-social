package dev.slne.surf.friends.menu.sub.request;

import com.github.stefvanschie.inventoryframework.font.util.Font;
import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.pane.OutlinePane;
import com.github.stefvanschie.inventoryframework.pane.Pane.Priority;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import com.github.stefvanschie.inventoryframework.pane.component.Label;
import dev.slne.surf.friends.FriendManager;
import dev.slne.surf.friends.menu.FriendMenu;
import dev.slne.surf.friends.util.ItemBuilder;
import dev.slne.surf.friends.util.PluginColor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class FriendRequestManageMenu extends FriendMenu {
  public FriendRequestManageMenu(String name) {
    super(5, "Anfrage von " + name);

    OutlinePane header = new OutlinePane(0, 0, 9, 1, Priority.LOW);
    OutlinePane footer = new OutlinePane(0, 4, 9, 1, Priority.LOW);
    OutlinePane midPane = new OutlinePane(4, 2, 1, 1);
    StaticPane back = new StaticPane(0, 4, 9, 1, Priority.HIGH);

    Label accept = new Label(1, 2, 1, 1, Priority.NORMAL, Font.OAK_PLANKS);
    Label deny = new Label(7, 2, 1, 1, Priority.NORMAL, Font.OAK_PLANKS);

    ItemStack target = new ItemBuilder(Material.PLAYER_HEAD).setSkullOwner(name).setName(Component.text(name).color(PluginColor.BLUE_LIGHT)).build();

    header.addItem(build(new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).setName("")));
    header.setRepeat(true);

    footer.addItem(build(new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).setName("")));
    footer.setRepeat(true);

    accept.setText("+", (c, stack) -> {
      ItemBuilder builder = new ItemBuilder(stack);

      builder.setName(Component.text("Akzeptieren"));
      builder.addLoreLine(Component.text("Freundschaftsanfrage akzeptieren", PluginColor.DARK_GRAY).decoration(TextDecoration.ITALIC, false));
      builder.addLoreLine(Component.text(" ", PluginColor.LIGHT_GRAY).decoration(TextDecoration.ITALIC, false));
      builder.addLoreLine(Component.text("Hier kannst du diese", PluginColor.LIGHT_GRAY).decoration(TextDecoration.ITALIC, false));
      builder.addLoreLine(Component.text("Freundschaftsanfrage akzeptieren.", PluginColor.LIGHT_GRAY).decoration(TextDecoration.ITALIC, false));

      return this.build(builder);
    });

    deny.setText("X", (c, stack) -> {
      ItemBuilder builder = new ItemBuilder(stack);

      builder.setName(Component.text("Ablehnen"));
      builder.addLoreLine(Component.text("Freundschaftsanfrage ablehnen", PluginColor.DARK_GRAY).decoration(TextDecoration.ITALIC, false));
      builder.addLoreLine(Component.text(" ", PluginColor.LIGHT_GRAY).decoration(TextDecoration.ITALIC, false));
      builder.addLoreLine(Component.text("Hier kannst du diese", PluginColor.LIGHT_GRAY).decoration(TextDecoration.ITALIC, false));
      builder.addLoreLine(Component.text("Freundschaftsanfrage ablehnen.", PluginColor.LIGHT_GRAY).decoration(TextDecoration.ITALIC, false));

      return this.build(builder);
    });

    accept.setOnClick(event -> {
      FriendManager.instance().acceptFriendRequests(event.getWhoClicked().getUniqueId(), Bukkit.getOfflinePlayer(name).getUniqueId());
      new FriendRequestsMenu(event.getWhoClicked().getUniqueId()).show(event.getWhoClicked());
    });

    deny.setOnClick(event -> {
      FriendManager.instance().denyFriendRequest(event.getWhoClicked().getUniqueId(), Bukkit.getOfflinePlayer(name).getUniqueId());
      new FriendRequestsMenu(event.getWhoClicked().getUniqueId()).show(event.getWhoClicked());
    });

    midPane.addItem(new GuiItem(target));

    back.addItem(build(new ItemBuilder(Material.BARRIER).setName(Component.text("Zurück").color(PluginColor.RED)), event -> new FriendRequestsMenu(event.getWhoClicked().getUniqueId()).show(event.getWhoClicked())), 4, 0);

    addPane(header);
    addPane(footer);
    addPane(accept);
    addPane(midPane);
    addPane(deny);
    addPane(back);


    setOnGlobalClick(event -> event.setCancelled(true));
    setOnGlobalDrag(event -> event.setCancelled(true));
  }
}
