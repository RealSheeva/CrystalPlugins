package net.runelite.client.plugins.crystalidle;

// Mandatory imports
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("crystalidle")
public interface CrystalIdleConfig extends Config
{

    @ConfigItem(
            position = 0,
            keyName = "Diameter",
            name = "Diameter",
            description = "Diameter"
    )
    default int diameter()
    {
        return 0;
    }

    @ConfigItem(
            position = 1,
            keyName = "Combat",
            name = "Combat",
            description = "Combat"
    )
    default boolean combat()
    {
        return false;
    }


}