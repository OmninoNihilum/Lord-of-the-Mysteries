package net.swimmingtuna.lotm.events.NewEventLoop.EventManager;

import java.util.Objects;

public class FunctionsProvider {
    public static IFunction getByID(String id) {

        for (var obj : EFunctions.values()) {
            if (Objects.equals(id, obj.get().getID()))
                return obj.get();
        }

        return null;
    }
}
