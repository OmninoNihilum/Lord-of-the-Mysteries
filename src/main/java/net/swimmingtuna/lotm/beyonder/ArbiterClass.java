package net.swimmingtuna.lotm.beyonder;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.ChatFormatting;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.swimmingtuna.lotm.beyonder.api.BeyonderClass;
import net.swimmingtuna.lotm.init.ItemInit;

import java.util.List;

public class ArbiterClass implements BeyonderClass {
    @Override
    public List<String> sequenceNames() {
        return List.of(
                "Justiciar",
                "Hand of Order",
                "Balancer",
                "Chaos Hunter",
                "Imperative Mage",
                "Disciplinary Paladin",
                "Judge",
                "Interrogator",
                "Sheriff",
                "Arbiter"
        );
    }

    @Override
    public List<Integer> antiDivination() {
        return List.of(20, 15, 13, 9, 5, 2, 1, 1, 1, 1);
    }

    @Override
    public List<Integer> divination() {
        return List.of(20, 15, 13, 9, 5, 2, 1, 1, 1, 1);
    }

    @Override
    public void applyAllModifiers(LivingEntity entity, int seq) {

    }

    @Override
    public List<Integer> spiritualityLevels() {
        return List.of(10000, 5000, 3000, 1800, 1200, 700, 450, 300, 175, 125);
    }

    @Override
    public List<Integer> mentalStrength() {
        return List.of(480, 335, 230, 187, 163, 120, 88, 72, 47, 35);
    }

    @Override
    public List<Integer> spiritualityRegen() {
        return List.of(34, 22, 16, 12, 10, 8, 6, 5, 3, 2);
    }


    @Override
    public void tick(LivingEntity player, int sequenceLevel) {
        if (player.level().getGameTime() % 50 == 0) {
            if (sequenceLevel == 9) {
            }
            if (sequenceLevel == 8) {
            }
            if (sequenceLevel == 7) {
            }
            if (sequenceLevel == 6) {
            }
            if (sequenceLevel == 5) {

            }
            if (sequenceLevel == 4) {
            }
            if (sequenceLevel == 3) {
            }
            if (sequenceLevel == 2) {
            }
            if (sequenceLevel == 1) {

            }
            if (sequenceLevel == 0) {

            }
        }
    }

    @Override
    public Multimap<Integer, Item> getItems() {
        HashMultimap<Integer, Item> items = HashMultimap.create();
        items.put(9, ItemInit.BEYONDER_ABILITY_USER.get());
        items.put(9, ItemInit.ALLY_MAKER.get());
        return items;
    }

    @Override
    public ChatFormatting getColorFormatting() {
        return ChatFormatting.GOLD;
    }


}
