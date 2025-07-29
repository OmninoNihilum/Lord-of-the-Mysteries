package nihilum.lotm.tweaks.Attributes;

import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;

import java.util.Objects;
import java.util.UUID;

public class BaseAttributes {
    //protected static final UUID healthBoostID = UUID.fromString("a3a90fac-39d0-4b75-9990-8211f70e0a0f");
    protected static final UUID speedID = UUID.fromString("1cd27c58-f3e8-46d9-8990-44a9f14dfc28");
    protected static final UUID attackID = UUID.fromString("b29d4a1d-20c5-42aa-a984-1c4bc77ccdad");

    protected static void clean(AttributeInstance attr, UUID id){
        if(attr.getModifier(id) != null)
            attr.removeModifier(id);
    }

    protected static <T> void apply(AttributeInstance attr, UUID id, Double value, String name){
        clean(attr, id);

        attr.addPermanentModifier(new AttributeModifier(
                id, name, value, AttributeModifier.Operation.ADDITION));
    }

    public static void cleanAll(Player player){
        //clean(Objects.requireNonNull(player.getAttribute(Attributes.MAX_HEALTH)), healthBoostID);

        clean(Objects.requireNonNull(player.getAttribute(Attributes.MOVEMENT_SPEED)), speedID);
        clean(Objects.requireNonNull(player.getAttribute(Attributes.ATTACK_DAMAGE)), attackID);
    }
}
