package ru.vgTrade.Config;

import ru.vgTrade.Util.Log;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;


/**
 * @author Muson (Original code: Oliver Brown (Arkel))
 */
public class LanguageManager {

    public enum Strings {OPTION, ONLINE, BUSY, REQUESTED, TOACCEPT, TODECLINE, CANCELLED, CONFIRMED, NOTYOURS, NOROOM, FINISHED, SURE, SENT, TIMED, DECLINED, YOURSELF, NOTNOW, IGNORING, NOTIGNORING, UNABLE, PLAYERIGNORING, CREATIVE, TOOFAR}

    private static List stringList = null;

    File configurationFile;
    YamlConfiguration config;

    public LanguageManager(Plugin plugin) {
        configurationFile = new File(plugin.getDataFolder(), "language.yml");
        config = YamlConfiguration.loadConfiguration(configurationFile);

        // Look for defaults in the jar
        InputStream defaultConfigStream = plugin.getResource("language.yml");
        if (defaultConfigStream != null) {
            YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defaultConfigStream);
            config.setDefaults(defConfig);

            config.options().copyDefaults(true);
            save();
        }

        stringList = config.getList("Language");
    }

    public void save() {
        try {
            config.save(configurationFile);
        } catch (IOException ex) {
            Log.severe("Could not save config to " + configurationFile + ex);
        }
    }

    public static String getString(Strings type) {
        return (String) stringList.get(type.ordinal());
    }
}
