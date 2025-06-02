package net.swimmingtuna.lotm.util.ClientData;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ClientShouldntRenderFlashData {
    private static final Map<UUID, Boolean> invisibilityStates = new HashMap<>();
    private static final int flashTimer = 0;

    public static void setShouldntRender(int value, UUID uuid) {
        if (value == 1) {
            invisibilityStates.put(uuid, true);
        } else {
            invisibilityStates.remove(uuid);
        }
    }

    public static boolean getShouldntRender(UUID uuid) {
        return invisibilityStates.getOrDefault(uuid, false);
    }

    public static void clearAll() {
        invisibilityStates.clear();
    }
}