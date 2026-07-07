package zonepracoldcombat.listener;

import dev.nandi0813.api.Event.Match.MatchEndEvent;
import dev.nandi0813.api.Event.Match.MatchRoundStartEvent;
import dev.nandi0813.api.Event.Match.MatchStartEvent;
import dev.nandi0813.api.Interface.Match;
import kernitus.plugin.OldCombatMechanics.api.OldCombatMechanicsAPI;
import kernitus.plugin.OldCombatMechanics.api.PlayerModuleOverride;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import zonepracoldcombat.config.ConfigManager;
import zonepracoldcombat.util.LadderResolver;

import java.util.*;
import java.util.logging.Logger;

public class MatchListener implements Listener {

    private final OldCombatMechanicsAPI ocmApi;
    private final ConfigManager configManager;
    private final LadderResolver ladderResolver;
    private final Logger logger;

    public MatchListener(OldCombatMechanicsAPI ocmApi, ConfigManager configManager, Logger logger) {
        this.ocmApi = ocmApi;
        this.configManager = configManager;
        this.ladderResolver = new LadderResolver();
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
        for (Player player : event.getMatch().getPlayers()) {
            ocmApi.clearAllModuleOverridesForPlayer(player);
        }
    }

    private void applyMode(Match match) {
        if (match.getPlayers().isEmpty()) return;

        String ladderName = ladderResolver.resolve(match);
        if (ladderName == null) {
            logger.warning("Could not resolve ladder name from " + match.getClass().getName());
            return;
        }

        String mode = configManager.getMode(ladderName);
        if (mode == null) return;

        List<String> modules = configManager.getModeModules(mode);
        if (modules == null) {
            logger.warning("Unknown mode '" + mode + "' for ladder '" + ladderName + "'");
            return;
        }

        for (Player player : match.getPlayers()) {
            if (modules.isEmpty()) {
                ocmApi.clearAllModuleOverridesForPlayer(player);
            } else {
                Map<String, PlayerModuleOverride> overrides = new HashMap<>();
                for (String module : modules) {
                    overrides.put(module, PlayerModuleOverride.FORCE_ENABLED);
                }
                ocmApi.setModuleOverridesForPlayer(player, overrides);
            }
        }
    }
}
