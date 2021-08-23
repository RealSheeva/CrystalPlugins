//Created by Sheeva

package net.runelite.client.plugins.crystalpotato;

import com.google.common.base.MoreObjects;
import com.google.inject.Binder;
import com.google.inject.Provides;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Area;
import java.awt.image.BufferedImage;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.*;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetID;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.Notifier;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.events.OverlayMenuClicked;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.cluescrolls.clues.ClueScroll;
import net.runelite.client.plugins.crystalpotato.item.AnyRequirementCollection;
import net.runelite.client.plugins.crystalpotato.AttackCounter;
import net.runelite.client.plugins.crystalpotato.SpecialWeapon;
import net.runelite.client.plugins.crystalpotato.Boss;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.ui.overlay.infobox.InfoBoxManager;

import static net.runelite.api.ItemID.*;
import static net.runelite.client.plugins.crystalpotato.item.ItemRequirements.*;
import  net.runelite.client.plugins.crystalpotato.item.ItemRequirement;


import lombok.extern.slf4j.Slf4j;
import org.pf4j.Extension;


@Slf4j
@Extension
@PluginDescriptor(
        name = "A Crystal Potato",
        description = "I am de captain now.",
        tags = {"sheeva"}
)

public class CrystalPotatoPlugin extends Plugin{
    @Getter
    private Item[] equippedItems;

    @Getter
    private Item[] inventoryItems;

    private SpecialWeapon specialWeapon;

    private final int GAUNTLET_CAST = 1167;
    private final int GAUNTLET_SHOOT = 426;
    private final int SPIN_EMOTE = 2107;
    private final int GAUNTLET_SWIPE = 440;
    private final int GAUNTLET_STAB = 428;

    private static final ItemRequirement HAS_LIGHT = new AnyRequirementCollection("Light Source",
            item(LIT_TORCH),
            item(LIT_CANDLE),
            item(LIT_BLACK_CANDLE),
            item(CANDLE_LANTERN_4531),
            item(CANDLE_LANTERN_4534), // lit black candle lantern
            item(OIL_LAMP_4524),
            item(OIL_LANTERN_4539),
            item(BULLSEYE_LANTERN_4550),
            item(CRYSTAL_PICKAXE_23863),
            item(EMERALD_LANTERN_9065),
            item(MINING_HELMET),
            item(FIREMAKING_CAPE),
            item(FIREMAKING_CAPET),
            item(KANDARIN_HEADGEAR_1),
            item(KANDARIN_HEADGEAR_2),
            item(KANDARIN_HEADGEAR_3),
            item(KANDARIN_HEADGEAR_4),
            item(BRUMA_TORCH),
            item(MAX_CAPE),
            item(MAX_CAPE_13342));

    public int collected_shards = 0;
    public Item[] our_inventory;
    public Item[] our_equipment;
    public Item[] all_items;
    public boolean trip_two = false;
    public boolean boss_fight = false;
    public int previous_weapon = 0;

    private double last_mage_xp = 0;

    private final Set<Integer> interactedNpcIds = new HashSet<>();
    private final AttackCounter[] attackCounter = new AttackCounter[SpecialWeapon.values().length];



    @Inject
    @Getter
    private Client client;

    @Inject
    private OverlayManager overlayManager;

    @Inject
    private CrystalPotatoOverlay overlay;

    @Inject
    private CrystalPotatoEntitiesOverlay overlay2;

    @Override
    protected void startUp() throws Exception
    {
        overlayManager.add(overlay);
        overlayManager.add(overlay2);
    }

    @Inject
    private ItemManager itemManager;

    @Inject
    private Notifier notifier;

    @Inject
    private InfoBoxManager infoBoxManager;

    @Inject
    private CrystalPotatoConfig config;

    @Override
    protected void shutDown() throws Exception
    {
        removeCounters();
        overlayManager.remove(overlay);
        overlayManager.remove(overlay2);
    }

    private void sendNotification(SpecialWeapon weapon, AttackCounter counter)
    {
        int threshold = weapon.getThreshold().apply(config);
        if (threshold > 0 && counter.getCount() == threshold)
        {
            notifier.notify(weapon.getName() + " is ineffective. Switch weapons!");
        }
    }

    private int getHit(SpecialWeapon specialWeapon, Hitsplat hitsplat)
    {
        if(specialWeapon != null) {
            return specialWeapon.isDamage() ? hitsplat.getAmount() : 1;
        }else{
            return(0);
        }
    }

    @Subscribe
    public void onAnimationChanged(AnimationChanged ani){
        if(config.joggingMode()) {
            //Only react to our animations && ani.getActor().getAnimation() == AnimationID.GAUNTLET_CAST
            if (ani.getActor() == client.getLocalPlayer()) {

                //Only under gauntlet conditions...
                ItemContainer equipment = client.getItemContainer(InventoryID.EQUIPMENT);
                if (equipment != null) {
                    if (equipment.contains(CORRUPTED_STAFF_PERFECTED) ||
                            equipment.contains(CORRUPTED_BOW_PERFECTED) ||
                            equipment.contains(CORRUPTED_HALBERD_PERFECTED) ) {
                        if (ani.getActor().getAnimation() == GAUNTLET_CAST) {
                            specialWeapon = specialWeapon.CORRUPTED_STAFF_PERFECTED;
                            if (specialWeapon.getItemID() != this.previous_weapon) {
                                this.previous_weapon = specialWeapon.getItemID();
                                removeCounters();
                            }
                            AttackCounter counter = attackCounter[specialWeapon.ordinal()];
                            if (counter == null) {
                                counter = new AttackCounter(itemManager.getImage(specialWeapon.getItemID()), this, config,
                                        1, specialWeapon);
                                infoBoxManager.addInfoBox(counter);
                                attackCounter[specialWeapon.ordinal()] = counter;
                            } else {
                                counter.addHits(1);
                                sendNotification(specialWeapon, counter);
                            }
                        } else if (ani.getActor().getAnimation() == GAUNTLET_SHOOT) {
                            specialWeapon = specialWeapon.CORRUPTED_BOW_PERFECTED;
                            if (specialWeapon.getItemID() != this.previous_weapon) {
                                this.previous_weapon = specialWeapon.getItemID();
                                removeCounters();
                            }
                            AttackCounter counter = attackCounter[specialWeapon.ordinal()];
                            if (counter == null) {
                                counter = new AttackCounter(itemManager.getImage(specialWeapon.getItemID()), this, config,
                                        1, specialWeapon);
                                infoBoxManager.addInfoBox(counter);
                                attackCounter[specialWeapon.ordinal()] = counter;
                            } else {
                                counter.addHits(1);
                                sendNotification(specialWeapon, counter);
                            }
                        }else if ((ani.getActor().getAnimation() == GAUNTLET_STAB)
                                || (ani.getActor().getAnimation() == GAUNTLET_SWIPE)) {
                            specialWeapon = specialWeapon.CORRUPTED_HALBERD_PERFECTED;
                            if (specialWeapon.getItemID() != this.previous_weapon) {
                                this.previous_weapon = specialWeapon.getItemID();
                                removeCounters();
                            }
                            AttackCounter counter = attackCounter[specialWeapon.ordinal()];
                            if (counter == null) {
                                counter = new AttackCounter(itemManager.getImage(specialWeapon.getItemID()), this, config,
                                        1, specialWeapon);
                                infoBoxManager.addInfoBox(counter);
                                attackCounter[specialWeapon.ordinal()] = counter;
                            } else {
                                counter.addHits(1);
                                sendNotification(specialWeapon, counter);
                            }
                        }
                    }

                    if (ani.getActor().getAnimation() == SPIN_EMOTE) {
                        this.previous_weapon = specialWeapon.getItemID();
                        removeCounters();
                    }
                }
            }
        }
    }


    @Provides
    CrystalPotatoConfig provideConfig(ConfigManager configManager){
        return configManager.getConfig(CrystalPotatoConfig.class);
    }

    /*
    @Subscribe
    public void onHitsplatApplied(HitsplatApplied hitsplatApplied){
        Actor target = hitsplatApplied.getActor();
        Hitsplat hitsplat = hitsplatApplied.getHitsplat();
        // Ignore all hitsplats other than mine
        if (!hitsplat.isMine() || target == client.getLocalPlayer()){
            return;
        }

        NPC npc = (NPC) target;
        int interactingId = npc.getId();

        // If this is a new NPC reset the counters

        if (!interactedNpcIds.contains(interactingId))
        {
            removeCounters();
            addInteracting(interactingId);
        }


        ItemContainer equipment = client.getItemContainer(InventoryID.EQUIPMENT);

        if(equipment.contains(CORRUPTED_BOW_PERFECTED)){
            specialWeapon = specialWeapon.CORRUPTED_BOW_PERFECTED;
        }else if(equipment.contains(CRYSTAL_BOW_PERFECTED)) {
            specialWeapon = specialWeapon.CRYSTAL_BOW_PERFECTED;
        }else if(equipment.contains(CRYSTAL_BOW_BASIC)) {
            specialWeapon = specialWeapon.CRYSTAL_BOW_BASIC;
        }else if(equipment.contains(CRYSTAL_HALBERD_PERFECTED)) {
            specialWeapon = specialWeapon.CRYSTAL_HALBERD_PERFECTED;
        }else if(equipment.contains(CORRUPTED_HALBERD_PERFECTED)) {
            specialWeapon = specialWeapon.CORRUPTED_HALBERD_PERFECTED;
        }else{
            this.previous_weapon = specialWeapon.getItemID();
            removeCounters();
            addInteracting(interactingId);
            return;
        }

        boolean skip = false;
        skip |= (specialWeapon.getItemID() == specialWeapon.CORRUPTED_STAFF_PERFECTED.getItemID());
        skip |= (specialWeapon.getItemID() == specialWeapon.CRYSTAL_STAFF_PERFECTED.getItemID());
        if(skip){
            return;
        }

        if(specialWeapon.getItemID() != this.previous_weapon){
            this.previous_weapon = specialWeapon.getItemID();
            removeCounters();
            addInteracting(interactingId);
        }


        int hit = getHit(specialWeapon, hitsplat);
        AttackCounter counter = attackCounter[specialWeapon.ordinal()];
        if (counter == null){
            counter = new AttackCounter(itemManager.getImage(specialWeapon.getItemID()), this, config,
                    hit, specialWeapon);
            infoBoxManager.addInfoBox(counter);
            attackCounter[specialWeapon.ordinal()] = counter;
        }else{
            counter.addHits(hit);
            sendNotification(specialWeapon, counter);
        }
    }
     */

    private void addInteracting(int npcId)
    {
        interactedNpcIds.add(npcId);

        // Add alternate forms of bosses
        final Boss boss = Boss.getBoss(npcId);
        if (boss != null)
        {
            interactedNpcIds.addAll(boss.getIds());
        }
    }

    private void removeCounters()
    {
        interactedNpcIds.clear();

        for (int i = 0; i < attackCounter.length; ++i)
        {
            AttackCounter counter = attackCounter[i];

            if (counter != null)
            {
                infoBoxManager.removeInfoBox(counter);
                attackCounter[i] = null;
            }
        }
    }

    @Subscribe
    public void onItemContainerChanged(ItemContainerChanged event){
        this.collected_shards = getCurrentCrystals();
        check_gear();
    }

    private void check_gear(){
        ItemContainer equipment = client.getItemContainer(InventoryID.EQUIPMENT);
        ItemContainer inventory = client.getItemContainer(InventoryID.INVENTORY);

        //Check for all 3 pieces
        //this.trip_two = (equipment.contains(CORRUPTED_HELM_BASIC) || equipment.contains(CRYSTAL_HELM_BASIC));
        //this.trip_two &= (equipment.contains(CORRUPTED_BODY_BASIC) || equipment.contains(CRYSTAL_BODY_BASIC));
        //this.trip_two &= (equipment.contains(CORRUPTED_LEGS_BASIC) || equipment.contains(CRYSTAL_LEGS_BASIC));

        //Also check for t2 bow
        this.trip_two &= ((inventory.contains(CORRUPTED_BOW_ATTUNED) || (inventory.contains(CRYSTAL_BOW_ATTUNED)))
                || (equipment.contains(CORRUPTED_BOW_ATTUNED) || (equipment.contains(CRYSTAL_BOW_ATTUNED))));


        this.boss_fight = ((inventory.contains(CORRUPTED_BOW_PERFECTED) || equipment.contains(CORRUPTED_BOW_PERFECTED))
                        || (inventory.contains(CRYSTAL_BOW_PERFECTED) || equipment.contains(CRYSTAL_BOW_PERFECTED)));
    }

    private int getCurrentCrystals()
    {
        final ItemContainer inventory = client.getItemContainer(InventoryID.INVENTORY);

        if (inventory == null){
            return 0;
        }

        return(inventory.count(ItemID.CRYSTAL_SHARDS) + inventory.count(ItemID.CORRUPTED_SHARDS));
    }
}
