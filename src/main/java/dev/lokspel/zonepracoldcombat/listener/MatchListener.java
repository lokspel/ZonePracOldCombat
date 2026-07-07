package dev.lokspel.zonepracoldcombat.listener;

import dev.lokspel.zonepracoldcombat.config.ConfigManager;
import dev.lokspel.zonepracoldcombat.util.LadderResolver;
import dev.nandi0813.api.Event.Match.MatchEndEvent;
import dev.nandi0813.api.Event.Match.MatchRoundStartEvent;
import dev.nandi0813.api.Event.Match.MatchStartEvent;
import dev.nandi0813.api.Interface.Match;
import dev.nandi0813.practice.manager.fight.match.MatchManager;
import kernitus.plugin.OldCombatMechanics.api.OldCombatMechanicsAPI;
import kernitus.plugin.OldCombatMechanics.api.PlayerModuleOverride;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class MatchListener implements Listener {

    private final OldCombatMechanicsAPI ocmApi;
    private final ConfigManager configManager;
    private final LadderResolver ladderResolver;
    private final JavaPlugin plugin;

    public MatchListener(
            OldCombatMechanicsAPI ocmApi,
            ConfigManager configManager,
            LadderResolver ladderResolver,
            JavaPlugin plugin
    ) {
        this.ocmApi = ocmApi;
        this.configManager = configManager;
        this.ladderResolver = ladderResolver;
        this.plugin = plugin;
    }

    @EventHandler
    public void onMatchStart(MatchStartEvent event) {
        Match match = event.getMatch();
        Bukkit.getScheduler().runTaskLater(plugin, () -> applyMode(match), 1L);
    }

    @EventHandler
    public void onMatchRoundStart(MatchRoundStartEvent event) {
        applyMode(event.getMatch());
    }

    @EventHandler
    public void onMatchEnd(MatchEndEvent event) {
        event.getMatch().getPlayers()
                .forEach(ocmApi::clearAllModuleOverridesForPlayer);
    }

    @EventHandler
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        if (!event.getMessage().equalsIgnoreCase("/leave")) {
            return;
        }

        Player player = event.getPlayer();

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if (MatchManager.getInstance().getLiveMatchByPlayer(player) == null) {
                ocmApi.clearAllModuleOverridesForPlayer(player);
            }
        }, 1L);
    }

    private void applyMode(Match match) {
        List<Player> players = match.getPlayers();

        if (players.isEmpty()) {
            return;
        }

        players.forEach(ocmApi::clearAllModuleOverridesForPlayer);

        String ladder = ladderResolver.resolve(match);
        if (ladder == null) {
            return;
        }

        String mode = configManager.getMode(ladder);
        if (mode == null) {
            return;
        }

        List<String> modules = configManager.getModeModules(mode);
        if (modules == null) {
            return;
        }

        if (modules.isEmpty()) {
            return;
        }

        players.forEach(player ->
                ocmApi.setModuleOverridesForPlayer(player, createOverrides(modules)));
    }

    private Map<String, PlayerModuleOverride> createOverrides(List<String> modules) {
        Map<String, PlayerModuleOverride> overrides = new HashMap<>(modules.size());

        for (String module : modules) {
            overrides.put(module, PlayerModuleOverride.FORCE_ENABLED);
        }

        return overrides;
    }
}