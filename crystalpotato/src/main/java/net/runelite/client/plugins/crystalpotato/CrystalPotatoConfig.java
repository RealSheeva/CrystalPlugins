package net.runelite.client.plugins.crystalpotato;

// Mandatory imports
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("crystalpotato")
public interface CrystalPotatoConfig extends Config
{

    @ConfigItem(
            position = 0,
            keyName = "joggingMode",
            name = "Enable Jogging",
            description = "Naughty naughty..."
    )
    default boolean joggingMode()
    {
        return false;
    }

    @ConfigItem(
            position = 1,
            keyName = "rangedThreshold",
            name = "# of attacks",
            description = "Threshold for Ranged (0 to disable)"
    )
    default int rangedThreshold()
    {
        return 0;

    }
}