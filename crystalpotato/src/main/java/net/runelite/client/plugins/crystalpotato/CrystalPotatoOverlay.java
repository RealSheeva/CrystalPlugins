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

        if(this.plugin.trip_two){
            renderTwo();
        }else if(this.plugin.boss_fight){
            renderBoss();
        }else{
            renderOne();
        }
        return super.render(graphics);
    }


    private void renderOne(){
        Color color;
        String addendum;
        int shards = plugin.collected_shards;

        //Weapon check (First prio)
        if(shards >= 0) {
            if (shards <= 80) {
                color = Color.RED;
                addendum = " - " + Integer.toString(shards) + "/80";
            } else {
                color = Color.GREEN;
                addendum = " - 80/80";
            }
            panelComponent.getChildren().add(LineComponent.builder().left("T2 Weapon" + addendum).leftColor(color).build());
        }
        shards -= 80;

        //Armor check(s) - 120 total
        if(shards > 0) {
            if (shards <= 120) {
                color = Color.RED;
                addendum = " - " + Integer.toString(shards) + "/120";
            } else {
                color = Color.GREEN;
                addendum = " - 120/120";
            }
            panelComponent.getChildren().add(LineComponent.builder().left("Armor " + addendum).leftColor(color).build());
        }
        shards -= 120;

        if(shards > 0){
            addendum = Integer.toString(shards);
            panelComponent.getChildren().add(LineComponent.builder().left("Extra - " + addendum).leftColor(Color.GREEN).build());
        }

    }

    private void renderTwo(){
        Color color;
        String addendum;
        int shards = plugin.collected_shards;

        if(shards > 0) {
            if (shards <= 80) {
                color = Color.RED;
                addendum = " - " + Integer.toString(shards) + "/80";
            } else {
                color = Color.GREEN;
                addendum = " - 80/80";
            }
            panelComponent.getChildren().add(LineComponent.builder().left("Weapon Upgrades - " + addendum).leftColor(color).build());
        }
        shards -= 80;

        if(shards > 0){
            addendum = Integer.toString(shards);
            panelComponent.getChildren().add(LineComponent.builder().left("Extra - " + addendum).leftColor(Color.GREEN).build());
        }

    }

    private void renderBoss(){
            panelComponent.getChildren().add(LineComponent.builder().left("Good Luck! :)").leftColor(Color.GREEN).build());
    }



}
