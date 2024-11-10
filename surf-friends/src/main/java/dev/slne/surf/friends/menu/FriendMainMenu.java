package dev.slne.surf.friends.menu;

import com.github.stefvanschie.inventoryframework.pane.OutlinePane;
import com.github.stefvanschie.inventoryframework.pane.Pane.Priority;
import dev.slne.surf.friends.menu.sub.friend.FriendFriendsMenu;
import dev.slne.surf.friends.menu.sub.request.FriendRequestsMenu;
import dev.slne.surf.friends.menu.sub.settings.FriendSettingsMenu;
import dev.slne.surf.friends.util.ItemBuilder;
import dev.slne.surf.friends.util.PluginColor;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class FriendMainMenu extends FriendMenu {

  public FriendMainMenu() {
    super(5, "Freunde");

    OutlinePane header = new OutlinePane(0, 0, 9, 1, Priority.LOW);
    OutlinePane footer = new OutlinePane(0, 4, 9, 1, Priority.LOW);

    OutlinePane settingPane = new OutlinePane(1, 2, 1, 1);
    OutlinePane flPane = new OutlinePane(4, 2, 1, 1);
    OutlinePane frPane = new OutlinePane(7, 2, 1, 1);

    ItemStack settings = new ItemBuilder(Material.DIAMOND_PICKAXE).setName(Component.text("Einstellungen").color(PluginColor.BLUE_LIGHT)).build();
    ItemStack friendList = new ItemBuilder(Material.ENDER_PEARL).setName(Component.text("Freunde").color(PluginColor.BLUE_LIGHT)).build();
    ItemStack friendRequests = new ItemBuilder(Material.PAPER).setName(Component.text("Freundschaftsanfragen").color(PluginColor.BLUE_LIGHT)).build();

    header.addItem(build(new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).setName("")));
    header.setRepeat(true);

    footer.addItem(build(new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).setName("")));
    footer.setRepeat(true);

    settingPane.addItem(build(settings, event -> {
      new FriendSettingsMenu(event.getWhoClicked().getUniqueId()).show(event.getWhoClicked());
    }));

    flPane.addItem(build(friendList, event -> {
      new FriendFriendsMenu(event.getWhoClicked().getUniqueId()).show(event.getWhoClicked());
    }));

    frPane.addItem(build(friendRequests, event -> {
      new FriendRequestsMenu(event.getWhoClicked().getUniqueId()).show(event.getWhoClicked());
    }));


    addPane(header);
    addPane(footer);
    addPane(settingPane);
    addPane(flPane);
    addPane(frPane);


    setOnGlobalClick(event -> event.setCancelled(true));
    setOnGlobalDrag(event -> event.setCancelled(true));
  }
}
