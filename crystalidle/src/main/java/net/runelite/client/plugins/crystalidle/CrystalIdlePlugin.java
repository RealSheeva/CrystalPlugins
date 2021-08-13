//Created by Sheeva

package net.runelite.client.plugins.crystalidle;

import com.google.inject.Provides;
import lombok.Getter;
import net.runelite.api.Client;
import net.runelite.api.Constants;
import net.runelite.api.Skill;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.StatChanged;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;


@PluginDescriptor(
        name = "A Crystal Idle",
        description = "r u afk?",
        tags = {"sheeva"}
)

public class CrystalIdlePlugin extends Plugin{

    private int last_var = 0;

    private static final int LOGOUT_WARNING_MILLIS = (4 * 60 + 40) * 1000; // 4 minutes and 40 seconds
    private static final int COMBAT_WARNING_MILLIS = 19 * 60 * 1000; // 19 minutes
    private static final int LOGOUT_WARNING_CLIENT_TICKS = LOGOUT_WARNING_MILLIS / Constants.CLIENT_TICK_LENGTH;
    private static final int COMBAT_WARNING_CLIENT_TICKS = COMBAT_WARNING_MILLIS / Constants.CLIENT_TICK_LENGTH;

    @Inject
    @Getter
    private Client client;


    @Inject
    private OverlayManager overlayManager;

    @Inject
    private CrystalIdleOverlay overlay;


    public int lastInteractionAgo = 0;
    public long lastClickAgo = 0;


    @Override
    protected void startUp()
    {
        overlayManager.add(overlay);
    }

    @Override
    protected void shutDown() throws Exception
    {
        overlayManager.remove(overlay);
    }


    @Subscribe
    public void onGameTick(GameTick event){
        this.lastInteractionAgo = checkIdleLogout();
        this.lastClickAgo = checkCombatIdle();
    }

    private int checkIdleLogout()
    {
        // Check clientside AFK first, because this is required for the server to disconnect you for being first
        int idleClientTicks = client.getKeyboardIdleTicks();
        if (client.getMouseIdleTicks() < idleClientTicks)
        {
            idleClientTicks = client.getMouseIdleTicks();
        }

        return(idleClientTicks);
    }

    private long checkCombatIdle(){
        long our_ms = client.getMouseLastPressedMillis();
        return(our_ms);
    }

    @Provides
    CrystalIdleConfig provideConfig(ConfigManager configManager){
        return configManager.getConfig(CrystalIdleConfig.class);
    }
}
