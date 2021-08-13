package net.runelite.client.plugins.crystalarrow;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.Point;
import net.runelite.api.coords.LocalPoint;
import net.runelite.client.ui.overlay.*;
import net.runelite.client.ui.overlay.components.ProgressPieComponent;

import javax.inject.Inject;
import java.awt.*;
import java.time.Instant;

import static net.runelite.api.MenuAction.RUNELITE_OVERLAY;
import static net.runelite.api.MenuAction.RUNELITE_OVERLAY_CONFIG;
import static net.runelite.client.ui.overlay.OverlayManager.OPTION_CONFIGURE;


@Slf4j
public class CrystalArrowOverlay extends OverlayPanel {
    private final CrystalArrowPlugin plugin;
    private final Client client;
    private final ProgressPieComponent progressPieComponent = new ProgressPieComponent();

    boolean var1_changed = false;
    boolean var2_changed = false;

    int var1 = 0;
    int var2 = 0;

    Instant last_fletch = Instant.now();

    CounterTimer our_timer = new CounterTimer();



    @Inject
    private CrystalArrowOverlay(CrystalArrowPlugin plugin, Client client)
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
        int i = 4;
        return super.render(graphics);
    }

    public void onCall(){
        if(this.our_timer != null) {
            this.our_timer.poke();

            this.last_fletch = Instant.now();
        }
    }

    private void drawTimerPieOverlay(Graphics2D graphics){
        if(our_timer != null) {
            final Player local = client.getLocalPlayer();
            LocalPoint our_location = local.getLocalLocation();

            progressPieComponent.setDiameter(300);

            Point pt = Perspective.localToCanvas(client, our_location, client.getPlane());

            Instant current_time = Instant.now();

            long seconds_passed = our_timer.seconds_passed();
            double percent;
             if (seconds_passed <= 11) {
                percent = (double) seconds_passed / 11;
            } else {
                percent = 1;
            }


            progressPieComponent.setPosition(pt);
            progressPieComponent.setFill(Color.GREEN);
            progressPieComponent.setBorderColor(Color.GREEN);
            progressPieComponent.setProgress(percent); // inverse so pie drains over time
            progressPieComponent.render(graphics);
        }

    }




}
