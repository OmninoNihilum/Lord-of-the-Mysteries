package net.swimmingtuna.lotm.item.BeyonderAbilities;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.swimmingtuna.lotm.init.BeyonderClassInit;
import nihilum.lotm.tweaks.LeftClickhandlers.LeftClickHandlerSkill;
import nihilum.lotm.tweaks.LeftClickhandlers.LeftClickType;
import net.swimmingtuna.lotm.networking.packet.LeftClickC2S;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

public class BeyonderAbilityUser extends LeftClickHandlerSkill {
    public BeyonderAbilityUser(Properties properties) {
        super(properties, BeyonderClassInit.SPECTATOR, 9, 0, 0);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.literal("Used to use abilities more efficiently, with a combo of 5 Left and Right clicks\n" +
                "Use /abilityput (Combination of L and R 5 time's) (ability)\n" +
                "Example: /abilityput LLRLR lotm:mindreading").withStyle(ChatFormatting.AQUA));
        super.baseHoverText(stack, level, tooltipComponents, tooltipFlag);
    }

    @Override
    public LeftClickType getleftClickEmpty() {
        return new LeftClickC2S();
    }
}