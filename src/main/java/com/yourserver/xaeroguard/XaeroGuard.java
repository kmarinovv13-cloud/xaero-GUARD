package com.yourserver.xaeroguard;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRegisterChannelEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class XaeroGuard extends JavaPlugin implements Listener {

    // Official Xaero control codes:
    // §n§o§m§i§n§i§m§a§p -> Completely disables Xaero's Minimap HUD
    // §f§a§i§r§x§a§e§r§o -> Enables Fair-Play mode (disables cave mode & entity radar on Minimap + World Map)
    private static final String DISABLE_MINIMAP_CODE = "§n§o§m§i§n§i§m§a§p";
    private static final String FAIR_PLAY_CODE = "§f§a§i§r§x§a§e§r§o";

    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(this, this);
        getLogger().info("XaeroGuard enabled.");
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (player.hasPermission("xaeroguard.bypass")) return;

        // Delay 10 ticks (0.5s) to ensure the client finishes loading into the world before receiving codes
        Bukkit.getScheduler().runTaskLater(this, () -> applyXaeroRestrictions(player), 10L);
    }

    @EventHandler
    public void onWorldChange(PlayerChangedWorldEvent event) {
        Player player = event.getPlayer();
        if (player.hasPermission("xaeroguard.bypass")) return;

        Bukkit.getScheduler().runTaskLater(this, () -> applyXaeroRestrictions(player), 10L);
    }

    @EventHandler
    public void onChannelRegister(PlayerRegisterChannelEvent event) {
        Player player = event.getPlayer();
        if (player.hasPermission("xaeroguard.bypass")) return;

        String channel = event.getChannel().toLowerCase();
        // Kick immediately if the client announces a Xaero mod plugin channel
        if (channel.contains("xaero")) {
            Bukkit.getScheduler().runTask(this, () ->
                player.kick(Component.text("Xaero's Minimap / World Map is not allowed on this server.", NamedTextColor.RED))
            );
        }
    }

    private void applyXaeroRestrictions(Player player) {
        if (!player.isOnline()) return;

        Component disableMinimap = LegacyComponentSerializer.legacySection().deserialize(DISABLE_MINIMAP_CODE);
        Component fairPlay = LegacyComponentSerializer.legacySection().deserialize(FAIR_PLAY_CODE);

        player.sendMessage(disableMinimap);
        player.sendMessage(fairPlay);
    }
}
