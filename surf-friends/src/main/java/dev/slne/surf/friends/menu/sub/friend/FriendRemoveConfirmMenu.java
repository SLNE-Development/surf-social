package dev.slne.surf.friends.menu.sub.friend;

import com.github.stefvanschie.inventoryframework.pane.OutlinePane;
import com.github.stefvanschie.inventoryframework.pane.Pane.Priority;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import dev.slne.surf.friends.FriendManager;
import dev.slne.surf.friends.menu.FriendMenu;
import dev.slne.surf.friends.util.ItemBuilder;
import dev.slne.surf.friends.util.PluginColor;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;

public class FriendRemoveConfirmMenu extends FriendMenu {

  public FriendRemoveConfirmMenu(String name) {
    super(5, "Bitte bestätige.");

    OutlinePane header = new OutlinePane(0, 0, 9, 1, Priority.LOW);
    OutlinePane footer = new OutlinePane(0, 4, 9, 1, Priority.LOW);
    StaticPane navigation = new StaticPane(0, 4, 9, 1, Priority.HIGH);
    OutlinePane left = new OutlinePane(1, 2, 1, 1);
    OutlinePane mid = new OutlinePane(4, 2, 1, 1);

    OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(name);

    header.addItem(build(new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).setName("")));
    header.setRepeat(true);

    footer.addItem(build(new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).setName("")));
    footer.setRepeat(true);

    mid.addItem(build(new ItemBuilder(Material.PLAYER_HEAD).setName(Component.text("Möchtest du " + name + " wirklich enfernen?")).setSkullOwner(offlinePlayer.getName())));

    left.addItem(build(new ItemBuilder(Material.LIME_DYE).setName(Component.text("Bestätigen", PluginColor.LIGHT_GREEN)), event -> {
      FriendManager.instance().removeFriend(event.getWhoClicked().getUniqueId(), offlinePlayer.getUniqueId());
      FriendManager.instance().removeFriend(offlinePlayer.getUniqueId(), event.getWhoClicked().getUniqueId());

      new FriendFriendsMenu(event.getWhoClicked().getUniqueId()).show(event.getWhoClicked());
    }));

    navigation.addItem(build(new ItemBuilder(Material.BARRIER).setName(Component.text("Zurück", PluginColor.RED)), event -> {
      if(offlinePlayer.getName() == null) {
        new FriendFriendsMenu(event.getWhoClicked().getUniqueId()).show(event.getWhoClicked());
      } else {
        new FriendSingleMenu(offlinePlayer.getName()).show(event.getWhoClicked());
      }
    }), 4, 0);


    addPane(header);
    addPane(footer);
    addPane(navigation);
    addPane(mid);
    addPane(left);

    setOnGlobalClick(event -> event.setCancelled(true));
    setOnGlobalDrag(event -> event.setCancelled(true));
  }
}
