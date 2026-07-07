package zonepracoldcombat.config;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public final class ConfigManager {

    private static final String LADDER_MODES_PATH = "ladder-modes";
    private static final String MODULES_PATH = "modules";

    private final JavaPlugin plugin;
    private Map<String, String> ladderModes = Collections.emptyMap();
    private Map<String, List<String>> modeModules = Collections.emptyMap();

    public ConfigManager(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void load() {
        final FileConfiguration config = plugin.getConfig();

        ConfigurationSection section = config.getConfigurationSection(LADDER_MODES_PATH);
        if (section == null) {
            ladderModes = Collections.emptyMap();
            plugin.getLogger().warning("Missing '" + LADDER_MODES_PATH + "' section in config.yml.");
        } else {
            final Map<String, String> mappings = new HashMap<>();
            for (String ladder : section.getKeys(false)) {
                String mode = section.getString(ladder);
                if (mode != null) {
                    mappings.put(ladder.toLowerCase(Locale.ROOT), mode);
                }
            }
            ladderModes = Collections.unmodifiableMap(mappings);
            plugin.getLogger().info(() -> "Loaded " + ladderModes.size() + " ladder mode mappings.");
        }

        ConfigurationSection modulesSection = config.getConfigurationSection(MODULES_PATH);
        if (modulesSection == null) {
            modeModules = Collections.emptyMap();
            plugin.getLogger().warning("Missing '" + MODULES_PATH + "' section in config.yml.");
        } else {
            final Map<String, List<String>> modules = new HashMap<>();
            for (String mode : modulesSection.getKeys(false)) {
                List<String> moduleList = modulesSection.getStringList(mode);
                modules.put(mode, Collections.unmodifiableList(moduleList));
            }
            modeModules = Collections.unmodifiableMap(modules);
            plugin.getLogger().info(() -> "Loaded " + modeModules.size() + " mode definitions.");
        }
    }

    public String getMode(String ladderName) {
        return ladderModes.get(ladderName.toLowerCase(Locale.ROOT));
    }

    public List<String> getModeModules(String mode) {
        return modeModules.get(mode);
    }
}
