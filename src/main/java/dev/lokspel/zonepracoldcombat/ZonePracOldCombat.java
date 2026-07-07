package dev.lokspel.zonepracoldcombat;

import dev.nandi0813.api.ZonePracticeApi;
import kernitus.plugin.OldCombatMechanics.api.OldCombatMechanicsAPI;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import dev.lokspel.zonepracoldcombat.config.ConfigManager;
import dev.lokspel.zonepracoldcombat.listener.MatchListener;

public final class ZonePracOldCombat extends JavaPlugin {

    @Override
    public void onEnable() {
        if (ZonePracticeApi.getInstance() == null) {
            getLogger().severe("ZonePracticePro API not found! Disabling.");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        RegisteredServiceProvider<OldCombatMechanicsAPI> registration =
                Bukkit.getServicesManager().getRegistration(OldCombatMechanicsAPI.class);
        if (registration == null) {
            getLogger().severe("OldCombatMechanics API not found! Disabling.");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }
        OldCombatMechanicsAPI ocmApi = registration.getProvider();

        saveDefaultConfig();
        saveResource("example-ocm-config.yml", false);

        ConfigManager configManager = new ConfigManager(this);
        configManager.load();

        Bukkit.getPluginManager().registerEvents(new MatchListener(ocmApi, configManager, getLogger()), this);

        getLogger().info("ZonePracOldCombat enabled.");
    }
}
