package net.swimmingtuna.lotm.capabilities.sealed_data;

import java.util.UUID;

public interface ISealedDataCapability {
    boolean isSealed();
    UUID sealCreator();
    int sealSequence();
    boolean sealedAbilities();

    void setSealed(boolean sealed);
    void setCreator(UUID creator);
    void setSequence(int sequence);
    void setSealedAbilities(boolean sealed);
}