package net.swimmingtuna.lotm.capabilities.concealed_data;

import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

public interface IConcealedDataCapability {

    HashMap<UUID, UUID> concealmentsCreators();                                         //<Concealment UUID, Creator UUID> Concealment Creator
    HashMap<UUID, Integer> concealmentsSequences();                                     //<Concealment UUID, Sequence> Concealment Sequence
    HashMap<UUID, Boolean> concealmentsHasTimers();                                     //<Concealment UUID, Has Timer> Concealment has Timer
    HashMap<UUID, Integer> concealmentsTimers();                                        //<Concealment UUID, Timer> Time in ticks until the corresponding Concealment diminish
    HashSet<UUID> concealmentsWithTimers();                                             //All Concealments that uses a timer, useful for not iterating over Concealments on the tick method
    HashMap<UUID, CONCEALMENT_TYPES> concealmentsTypes();                               //<Concealment UUID, Concealment Type> The type of Concealment, useful when a Concealment needs to play a custom method when its over

    void setCreator(UUID concealmentUUID, UUID creator);                                //Sets the Concealment Creator
    void setSequence(UUID concealmentUUID, int sequence);                               //Sets the Concealment Sequence
    void toggleTimer(UUID concealmentUUID);                                             //Toggles the timer
    void setTimer(UUID concealmentUUID, int counter);                                   //Sets the timer counter
    void setConcealmentType(UUID concealmentUUID, CONCEALMENT_TYPES concealmentType);   //Sets the ConcealmentType
    void removeConcealment(UUID concealmentUUID);
}