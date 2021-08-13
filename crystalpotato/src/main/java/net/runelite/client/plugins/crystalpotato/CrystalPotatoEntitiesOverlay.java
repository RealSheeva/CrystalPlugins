package net.runelite.client.plugins.crystalpotato;

import net.runelite.api.Client;
import net.runelite.api.NPC;
import net.runelite.api.NPCComposition;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.overlay.*;

import javax.inject.Inject;
import java.awt.*;
import java.util.HashMap;
import java.util.List;

import static net.runelite.api.ItemID.ORANGE;
import static net.runelite.api.ItemID.RED;

public class CrystalPotatoEntitiesOverlay extends OverlayPanel {
    private final Client client;


    @Inject
    private CrystalPotatoEntitiesOverlay(CrystalPotatoPlugin plugin, Client client) {
        this.client = client;
        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.ABOVE_WIDGETS);
        setPriority(OverlayPriority.HIGHEST);
    }

    private static final Font FONT = FontManager.getRunescapeFont().deriveFont(Font.BOLD, 16);
    private static final Color RED = new Color(221, 44, 0);
    private static final Color GREEN = new Color(0, 200, 83);
    private static final Color ORANGE = new Color(255, 109, 0);
    private static final Color YELLOW = new Color(255, 214, 0);
    private static final Color CYAN = new Color(0, 184, 212);
    private static final Color BLUE = new Color(41, 98, 255);
    private static final Color DEEP_PURPLE = new Color(98, 0, 234);
    private static final Color PURPLE = new Color(170, 0, 255);
    private static final Color GRAY = new Color(158, 158, 158);


    //Hashmap for Players and their colors
    private HashMap<Integer, String> weaponMap = new HashMap<>();
    private HashMap<Integer, Color> styleMap = new HashMap<>();

    //Hashmap for Projectiles and colors
    private HashMap<Integer, String> projMap = new HashMap<>();
    private HashMap<Integer, Color> projStyleMap = new HashMap<>();

    //Hashmap for Game Objects
    private HashMap<Integer, String> goMap = new HashMap<>();
    private HashMap<Integer, Color> goStyleMap = new HashMap<>();
    private int[] goIgnore = new int[50];

    @Inject
    private CrystalPotatoConfig config;



    private void renderNpcs(Graphics2D graphics)
    {
        List<NPC> npcs = client.getNpcs();
        for (NPC npc : npcs)
        {
            NPCComposition composition = npc.getComposition();
            Color color = composition.getCombatLevel() > 1 ? YELLOW : ORANGE;
            if (composition.getConfigs() != null)
            {
                NPCComposition transformedComposition = composition.transform();
                if (transformedComposition == null)
                {
                    color = GRAY;
                }
                else
                {
                    composition = transformedComposition;
                }
            }

            //IDE acquiescence
            Shape objectClickbox = npc.getConvexHull();
            String text;

            switch(composition.getId()){

                case 10569:
                    text = "JUMPER";
                    OverlayUtil.renderActorOverlay(graphics, npc, text, ORANGE);
                    break;
                case 10568:
                    text = " ";
                    OverlayUtil.renderActorOverlay(graphics, npc, text, GREEN);
                    break;



                //Shamans
                case 8565:
                    if(npc.getAnimation() == 7157){
                        text = "Rise minions... RISE!";
                        OverlayUtil.renderActorOverlay(graphics, npc, text, PURPLE);
                        renderPoly(graphics, PURPLE, objectClickbox);
                    }
                    break;
                //Shamans -- Purple guys
                case 6768:
                    if(npc.getAnimation() == -1) {
                        //Idle
                        color = BLUE;
                    }else if(npc.getAnimation() == 7159){
                        //BOOM
                        color = RED;
                    }else if(npc.getAnimation() == 7160){
                        //RISEEE
                        color = PURPLE;
                    }
                    text = composition.getName();
                    OverlayUtil.renderActorOverlay(graphics, npc, text, color);
                    break;

                //Gauntlet - Crystalline Heffalump
                case 9022:
                case 9023:
                case 9024:
                case 9025:
                    //Gauntlet - Corrupted Heffalump
                case 9035:
                case 9036:
                case 9037:
                case 9038:
                    if(config.joggingMode()) {
                        if (npc.getAnimation() == 8754) {
                            text = "Switching to Mage";
                            OverlayUtil.renderActorOverlay(graphics, npc, text, PURPLE);
                            renderPoly(graphics, PURPLE, objectClickbox);
                        } else if (npc.getAnimation() == 8755) {
                            text = "Switching to Range";
                            OverlayUtil.renderActorOverlay(graphics, npc, text, GREEN);
                            renderPoly(graphics, GREEN, objectClickbox);
                        } else if (npc.getAnimation() == 8418) {
                            text = "NADO";
                            OverlayUtil.renderActorOverlay(graphics, npc, text, ORANGE);
                            renderPoly(graphics, ORANGE, objectClickbox);
                        } else {
                            text = "ID: " + composition.getId() + "  A: " + npc.getAnimation();
                            OverlayUtil.renderActorOverlay(graphics, npc, text, GRAY);
                        }
                    }else{
                        text = "Fatty";
                        OverlayUtil.renderActorOverlay(graphics, npc, text, GRAY);
                    }
                    break;

                case 9039:
                    text = "";
                    OverlayUtil.renderActorOverlay(graphics, npc, text, ORANGE);
                    break;

                //Gauntlet - Animals
                //Tier 1
                //Spider
                case 9027:
                case 9041:
                    text = "t1";
                    OverlayUtil.renderActorOverlay(graphics, npc, text, GREEN);
                    break;
                //Rat
                case 9026:
                case 9040:
                    text = "t1";
                    OverlayUtil.renderActorOverlay(graphics, npc, text, YELLOW);
                    break;
                //Bat
                case 9028:
                case 9042:
                    text = "t1";
                    OverlayUtil.renderActorOverlay(graphics, npc, text, RED);
                    break;
                //Tier 2
                //Scorpion
                case 9030:
                case 9044:
                    text = "t2";
                    OverlayUtil.renderActorOverlay(graphics, npc, text, GREEN);
                    break;
                //Unicorn
                case 9029:
                case 9043:
                    text = "t2";
                    OverlayUtil.renderActorOverlay(graphics, npc, text, YELLOW);
                    break;
                //Wolf
                case 9031:
                case 9045:
                    text = "t2";
                    OverlayUtil.renderActorOverlay(graphics, npc, text, RED);
                    break;

                //Gauntlet - Demi-bosses
                //Drake
                case 9033:
                case 9047:
                    text = "Drake";
                    OverlayUtil.renderActorOverlay(graphics, npc, text, PURPLE);
                    renderPoly(graphics, PURPLE, objectClickbox);
                    break;
                //Bear
                case 9032:
                case 9046:
                    text = "Bear";
                    OverlayUtil.renderActorOverlay(graphics, npc, text, RED);
                    renderPoly(graphics, RED, objectClickbox);
                    break;
                //Dark Beast
                case 9034:
                case 9048:
                    text = "Beast";
                    OverlayUtil.renderActorOverlay(graphics, npc, text, GREEN);
                    renderPoly(graphics, GREEN, objectClickbox);
                    break;

                //Catchall
                default:
                    if(config.joggingMode()) {
                        text = "ID: " + composition.getId() + "  A: " + npc.getAnimation();
                    }
                    //OverlayUtil.renderActorOverlay(graphics, npc, text, GRAY);

            }
        }
    }

    private void renderPoly(Graphics2D graphics, Color color, Shape polygon)
    {
        if (polygon != null)
        {
            graphics.setColor(color);
            graphics.setStroke(new BasicStroke(2));
            graphics.draw(polygon);
            graphics.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 20));
            graphics.fill(polygon);
        }
    }





    @Override
    public Dimension render(Graphics2D graphics){
        renderNpcs(graphics);
        return(super.render(graphics));
    }
}
