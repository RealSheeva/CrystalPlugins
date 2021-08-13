package net.runelite.client.plugins.crystalpotato;

import lombok.AccessLevel;
import lombok.Getter;
import net.runelite.client.plugins.crystalpotato.CrystalPotatoPlugin;
import net.runelite.client.plugins.crystalpotato.CrystalPotatoConfig;
import net.runelite.client.plugins.crystalpotato.SpecialWeapon;
import net.runelite.client.ui.overlay.infobox.Counter;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

public class AttackCounter extends Counter{
    private final SpecialWeapon weapon;
    private final CrystalPotatoConfig config;
    @Getter(AccessLevel.PACKAGE)
    private final Map<String, Integer> partySpecs = new HashMap<>();

    AttackCounter(BufferedImage image, CrystalPotatoPlugin plugin, CrystalPotatoConfig config, int hitValue, SpecialWeapon weapon){
        super(image, plugin, hitValue);
        this.weapon = weapon;
        this.config = config;
    }

    void addHits(double hit){
        int count = getCount();
        setCount(count + (int) hit);
    }

    @Override
    public String getTooltip()
    {
        int hitValue = getCount();

        if (partySpecs.isEmpty())
        {
            return buildTooltip(hitValue);
        }

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(buildTooltip(hitValue));

        for (Map.Entry<String, Integer> entry : partySpecs.entrySet())
        {
            stringBuilder.append("</br>")
                    .append(entry.getKey() == null ? "You" : entry.getKey()).append(": ")
                    .append(buildTooltip(entry.getValue()));
        }

        return stringBuilder.toString();
    }

    private String buildTooltip(int hitValue)
    {
        if (!weapon.isDamage())
        {
            if (hitValue == 1)
            {
                return weapon.getName() + " special has hit " + hitValue + " time.";
            }
            else
            {
                return weapon.getName() + " special has hit " + hitValue + " times.";
            }
        }
        else
        {
            return weapon.getName() + " special has hit " + hitValue + " total.";
        }
    }

    @Override
    public Color getTextColor()
    {
        int threshold = weapon.getThreshold().apply(config);
        if (threshold > 0)
        {
            int count = getCount();
            return count >= threshold ? Color.RED : Color.GREEN;
        }
        return super.getTextColor();
    }
}
