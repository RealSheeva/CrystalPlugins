//Created by Sheeva
package net.runelite.client.plugins.crystalarrow;

import lombok.Getter;
import net.runelite.api.*;
import net.runelite.api.events.StatChanged;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;


@PluginDescriptor(
        name = "A Crystal Arrow",
        description = "Why fletch?",
        tags = {"sheeva"}
)

public class CrystalArrowPlugin extends Plugin{

    private int last_var = 0;


    @Inject
    @Getter
    private Client client;


    @Inject
    private OverlayManager overlayManager;

    @Inject
    private CrystalArrowOverlay overlay;



    private int last_xp = 0;


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
    public void onStatChanged(StatChanged statChanged){
        // StatChanged event occurs when stats drain/boost; check we have an change to actual xp
        if(statChanged.getSkill() == Skill.FLETCHING){
            if(this.last_xp != statChanged.getXp()){
                //Gained fletch Xp
                this.last_xp = statChanged.getXp();
                overlay.onCall();
            }
        }

    }




}
