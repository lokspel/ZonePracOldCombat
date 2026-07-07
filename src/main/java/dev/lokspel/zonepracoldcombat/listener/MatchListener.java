package dev.lokspel.zonepracoldcombat.listener;

import dev.lokspel.zonepracoldcombat.config.ConfigManager;
import dev.lokspel.zonepracoldcombat.util.LadderResolver;
import dev.nandi0813.api.Event.Match.MatchEndEvent;
import dev.nandi0813.api.Event.Match.MatchRoundStartEvent;
import dev.nandi0813.api.Event.Match.MatchStartEvent;
import dev.nandi0813.api.Interface.Match;
import kernitus.plugin.OldCombatMechanics.api.OldCombatMechanicsAPI;
import kernitus.plugin.OldCombatMechanics.api.PlayerModuleOverride;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public final class MatchListener implements Listener {

    private final OldCombatMechanicsAPI ocmApi;
    private final ConfigManager configManager;
    private final LadderResolver ladderResolver;
    private final Logger logger;

    public MatchListener(
            OldCombatMechanicsAPI ocmApi,
            ConfigManager configManager,
            LadderResolver ladderResolver,
            Logger logger
    ) {
        this.ocmApi = ocmApi;
        this.configManager = configManager;
        this.ladderResolver = ladderResolver;
        this.logger = logger;
    }

    @EventHandler
    public void onMatchStart(MatchStartEvent event) {
        applyMode(event.getMatch());
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

    private void applyMode(Match match) {
        List<Player> players = match.getPlayers();

        if (players.isEmpty()) {
            return;
        }

        String ladder = ladderResolver.resolve(match);
        if (ladder == null) {
            logger.warning(() ->
                    "Could not resolve ladder for " + match.getClass().getName());
            return;
        }

        String mode = configManager.getMode(ladder);
        if (mode == null) {
            return;
        }

        List<String> modules = configManager.getModeModules(mode);
        if (modules == null) {
            logger.warning(() ->
                    "Unknown mode '" + mode + "' for ladder '" + ladder + "'");
            return;
        }

        if (modules.isEmpty()) {
            players.forEach(ocmApi::clearAllModuleOverridesForPlayer);
            return;
        }

        Map<String, PlayerModuleOverride> overrides = createOverrides(modules);

        players.forEach(player ->
                ocmApi.setModuleOverridesForPlayer(player, overrides));
    }

    private Map<String, PlayerModuleOverride> createOverrides(List<String> modules) {
        Map<String, PlayerModuleOverride> overrides = new HashMap<>(modules.size());

        for (String module : modules) {
            overrides.put(module, PlayerModuleOverride.FORCE_ENABLED);
        }

        return overrides;
    }
}