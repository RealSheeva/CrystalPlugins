package net.runelite.client.plugins.crystalidle;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.Point;
import net.runelite.api.coords.LocalPoint;
import net.runelite.client.plugins.crystalidle.CounterTimer;
import net.runelite.client.plugins.crystalidle.CrystalIdlePlugin;
import net.runelite.client.plugins.crystalidle.CrystalIdleConfig;
import net.runelite.client.ui.overlay.*;
import net.runelite.client.ui.overlay.components.ProgressPieComponent;

import javax.inject.Inject;
import java.awt.*;
import java.time.Instant;

import static net.runelite.api.MenuAction.RUNELITE_OVERLAY;
import static net.runelite.api.MenuAction.RUNELITE_OVERLAY_CONFIG;
import static net.runelite.client.ui.overlay.OverlayManager.OPTION_CONFIGURE;


@Slf4j
public class CrystalIdleOverlay extends OverlayPanel {
    private final CrystalIdlePlugin plugin;
    private final Client client;
    private final ProgressPieComponent progressPieComponent = new ProgressPieComponent();

    boolean var1_changed = false;
    boolean var2_changed = false;


    private static final int LOGOUT_WARNING_MILLIS = (4 * 60 + 50) * 1000; // 4 minutes and 50 seconds
    private static final int COMBAT_WARNING_MILLIS = 19 * 60 * 1000; // 19 minutes
    private static final int LOGOUT_WARNING_CLIENT_TICKS = LOGOUT_WARNING_MILLIS / Constants.CLIENT_TICK_LENGTH;
    private static final int COMBAT_WARNING_CLIENT_TICKS = COMBAT_WARNING_MILLIS / Constants.CLIENT_TICK_LENGTH;


    int var1 = 0;
    int var2 = 0;

    Instant last_poke = Instant.now();

    net.runelite.client.plugins.crystalidle.CounterTimer our_timer = new CounterTimer();

    @Inject
    private CrystalIdleConfig config;

    @Inject
    private CrystalIdleOverlay(CrystalIdlePlugin plugin, Client client)
    {
        super(plugin);
        this.plugin = plugin;
        this.client = client;
        //setPriority(OverlayPriority.LOW);
        getMenuEntries().add(new OverlayMenuEntry(RUNELITE_OVERLAY_CONFIG, OPTION_CONFIGURE, "Fletch -- afa"));
        getMenuEntries().add(new OverlayMenuEntry(RUNELITE_OVERLAY, "Reset", "Fletch -- zrz"));
        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.ABOVE_WIDGETS);
        setPriority(OverlayPriority.HIGHEST);
    }


    @Override
    public Dimension render(Graphics2D graphics){
        //final Item[] allItems = plugin.getAllItems();D
        drawTimerPieOverlay(graphics);
        return super.render(graphics);
    }

    private void drawTimerPieOverlay(Graphics2D graphics){
        if(our_timer != null) {
            final Player local = client.getLocalPlayer();
            LocalPoint our_location = local.getLocalLocation();



            progressPieComponent.setDiameter(config.diameter());

            Point pt = Perspective.localToCanvas(client, our_location, client.getPlane());

            int gameticks_past = plugin.lastInteractionAgo;
            long ms_past = Instant.now().toEpochMilli() - plugin.lastClickAgo;

            double percent;

            if(config.combat()) {
                if (ms_past <= COMBAT_WARNING_MILLIS) {
                    percent = (double) ms_past / COMBAT_WARNING_MILLIS;
                } else {
                    percent = 1;
                }
            }else{
                if (plugin.lastInteractionAgo <= LOGOUT_WARNING_CLIENT_TICKS) {
                    percent = (double) gameticks_past / LOGOUT_WARNING_CLIENT_TICKS;
                } else {
                    percent = 1;
                }
            }


            progressPieComponent.setPosition(pt);
            progressPieComponent.setFill(Color.GREEN);
            progressPieComponent.setBorderColor(Color.GREEN);
            progressPieComponent.setProgress(percent); // inverse so pie drains over time
            progressPieComponent.render(graphics);
        }

    }




}
