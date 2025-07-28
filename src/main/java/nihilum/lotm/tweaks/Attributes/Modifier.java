package nihilum.lotm.tweaks.Attributes;

import net.minecraft.world.entity.ai.attributes.AttributeInstance;

import java.util.List;
import java.util.UUID;

public class Modifier <T> {
    private final UUID ID;
    private final List<T> list;

    @SafeVarargs
    Modifier(T... c){
        this.ID = UUID.randomUUID();
        this.list = List.of(c);
    }

    public T getValue(int index){
        return list.get(index);
    }

    public UUID getID(){
        return ID;
    }

    public void clean(AttributeInstance attr){
        if(attr.getModifier(ID) == null) return;
        attr.removeModifier(ID);
    }
}
