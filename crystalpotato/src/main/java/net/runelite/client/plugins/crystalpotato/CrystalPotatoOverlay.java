package net.runelite.client.plugins.crystalpotato;

import java.awt.*;
import java.util.HashMap;
import java.util.List;
import javax.inject.Inject;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.Item;
import static net.runelite.api.ItemID.*;
import static net.runelite.api.MenuAction.RUNELITE_OVERLAY;
import static net.runelite.api.MenuAction.RUNELITE_OVERLAY_CONFIG;
import static net.runelite.client.plugins.crystalpotato.item.ItemRequirements.item;
import static net.runelite.client.ui.overlay.OverlayManager.OPTION_CONFIGURE;

import net.runelite.api.NPC;
import net.runelite.api.NPCComposition;
import net.runelite.api.events.GameTick;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.cluescrolls.ClueScrollPlugin;
import net.runelite.client.plugins.crystalpotato.item.AnyRequirementCollection;
import net.runelite.client.plugins.crystalpotato.item.ItemRequirement;
import net.runelite.client.plugins.crystalpotato.CrystalPotatoPlugin;
import net.runelite.client.plugins.crystalpotato.item.ItemRequirements;
import net.runelite.client.plugins.crystalpotato.item.SingleItemRequirement;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.overlay.*;
import net.runelite.client.ui.overlay.components.LineComponent;


@Slf4j
public class CrystalPotatoOverlay extends OverlayPanel {
    private final CrystalPotatoPlugin plugin;
    private final Client client;


    //Hashmap for NPC's and their colors
    private HashMap<Integer, String> npcMap = new HashMap<>();
    private HashMap<Integer, Color> npcStyleMap = new HashMap<>();

    @Inject
    private CrystalPotatoConfig config;




    @Inject
    private CrystalPotatoOverlay(CrystalPotatoPlugin plugin, Client client)
    {
        super(plugin);
        this.plugin = plugin;
        this.client = client;
        getMenuEntries().add(new OverlayMenuEntry(RUNELITE_OVERLAY_CONFIG, OPTION_CONFIGURE, "Gauntlet -- afa"));
        getMenuEntries().add(new OverlayMenuEntry(RUNELITE_OVERLAY, "Reset", "Gauntlet -- zrz"));
        setPosition(OverlayPosition.TOP_LEFT);
        setLayer(OverlayLayer.ABOVE_WIDGETS);
        setPriority(OverlayPriority.HIGHEST);
    }

    @Override
    public Dimension render(Graphics2D graphics){
        Item[] inventoryItems = plugin.getInventoryItems();
        //final Item[] allItems = plugin.getAllItems();

        panelComponent.getChildren().add(LineComponent.builder().left("Gauntlet Plugin").leftColor(Color.CYAN).build());

        //T2 weapon x1 -- 80 shards
        //T3 weapon x2 -- 160 shards
        //T1 full armor -- 120 shards
        //T2 full additional -- 120 shards
        Color color;
        if(!this.plugin.boss_fight) {
            if (!this.plugin.trip_two) {
                //First back needs 220 shards for 1xT1 weapon, T1 full armor, +2 vials
                if (plugin.collected_shards <= 220) {
                    color = Color.RED;
                } else {
                    color = Color.GREEN;
                }
                panelComponent.getChildren().add(LineComponent.builder().left("Crystals (t1): " + Integer.toString(plugin.collected_shards - 220)).leftColor(color).build());
            } else {
                if (plugin.collected_shards <= 200) {
                    color = Color.RED;
                } else {
                    color = Color.GREEN;
                }
                //Second back needs 80 for second weapon, plus additional 120 for our armor
                panelComponent.getChildren().add(LineComponent.builder().left("Crystals (t2): " + Integer.toString(plugin.collected_shards - 200)).leftColor(color).build());
            }
        }else{
            panelComponent.getChildren().add(LineComponent.builder().left("Good luck!").leftColor(Color.GREEN).build());
        }
        return super.render(graphics);
    }



}
