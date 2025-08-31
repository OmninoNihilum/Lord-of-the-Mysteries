package net.swimmingtuna.lotm.capabilities.concealed_data;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;

public class ConcealedDataCapability implements IConcealedDataCapability, INBTSerializable<CompoundTag> {
    private HashMap<UUID, UUID> concealmentsCreators = new HashMap<>();
    private HashMap<UUID, Integer> concealmentsSequences = new HashMap<>();
    private HashMap<UUID, Boolean> concealmentsHasTimers = new HashMap<>();
    private HashMap<UUID, Integer> concealmentsTimers = new HashMap<>();
    private HashSet<UUID> concealmentsWithTimers = new HashSet<>();
    private HashMap<UUID, CONCEALMENT_TYPES> concealmentsTypes = new HashMap<>();

    @Override
    public HashMap<UUID, UUID> concealmentsCreators() {
        return this.concealmentsCreators;
    }

    @Override
    public HashMap<UUID, Integer> concealmentsSequences() {
        return this.concealmentsSequences;
    }

    @Override
    public HashMap<UUID, Boolean> concealmentsHasTimers() {
        return this.concealmentsHasTimers;
    }

    @Override
    public HashMap<UUID, Integer> concealmentsTimers() {
        return this.concealmentsTimers;
    }

    @Override
    public HashSet<UUID> concealmentsWithTimers() {
        return this.concealmentsWithTimers;
    }

    @Override
    public HashMap<UUID, CONCEALMENT_TYPES> concealmentsTypes() {
        return this.concealmentsTypes;
    }

    @Override
    public void setCreator(UUID concealmentUUID, UUID creator) {
        this.concealmentsCreators.put(concealmentUUID, creator);
    }

    @Override
    public void setSequence(UUID concealmentUUID, int sequence) {
        this.concealmentsSequences.put(concealmentUUID, sequence);
    }

    @Override
    public void toggleTimer(UUID concealmentUUID) {
        this.concealmentsHasTimers.put(concealmentUUID, true);
        this.concealmentsWithTimers.add(concealmentUUID);
    }

    @Override
    public void setTimer(UUID concealmentsUUID, int counter) {
        this.concealmentsTimers.put(concealmentsUUID, counter);
    }

    @Override
    public void setConcealmentType(UUID concealmentUUID, CONCEALMENT_TYPES concealmentType) {
        this.concealmentsTypes.put(concealmentUUID, concealmentType);
    }

    @Override
    public void removeConcealment(UUID concealmentUUID) {
        this.concealmentsCreators.remove(concealmentUUID);
        this.concealmentsSequences.remove(concealmentUUID);
        this.concealmentsHasTimers.remove(concealmentUUID);
        this.concealmentsTimers.remove(concealmentUUID);
        this.concealmentsWithTimers.remove(concealmentUUID);
        this.concealmentsTypes.remove(concealmentUUID);
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();

        ListTag creatorsTag = new ListTag();
        for (Map.Entry<UUID, UUID> entry : this.concealmentsCreators.entrySet()){
            CompoundTag entryTag = new CompoundTag();

            entryTag.putUUID("concealmentUUID", entry.getKey());
            entryTag.putUUID("creatorUUID", entry.getValue());

            creatorsTag.add(entryTag);
        }
        tag.put("concealmentsCreators", creatorsTag);

        ListTag sequencesTag = new ListTag();
        for (Map.Entry<UUID, Integer> entry : this.concealmentsSequences.entrySet()){
            CompoundTag entryTag = new CompoundTag();

            entryTag.putUUID("concealmentUUID", entry.getKey());
            entryTag.putInt("sequence", entry.getValue());

            sequencesTag.add(entryTag);
        }
        tag.put("concealmentsSequences", sequencesTag);

        ListTag hasTimerTag = new ListTag();
        for (Map.Entry<UUID, Boolean> entry : this.concealmentsHasTimers.entrySet()){
            CompoundTag entryTag = new CompoundTag();

            entryTag.putUUID("concealmentUUID", entry.getKey());
            entryTag.putBoolean("hasTimer", entry.getValue());

            hasTimerTag.add(entryTag);
        }
        tag.put("concealmentsHasTimers", hasTimerTag);

        ListTag timerTag = new ListTag();
        for (Map.Entry<UUID, Integer> entry : this.concealmentsTimers.entrySet()){
            CompoundTag entryTag = new CompoundTag();

            entryTag.putUUID("concealmentUUID", entry.getKey());
            entryTag.putInt("timer", entry.getValue());

            timerTag.add(entryTag);
        }
        tag.put("concealmentsTimers", timerTag);

        ListTag concealmentsWithTimersTag = new ListTag();
        for (UUID concealment : this.concealmentsWithTimers){
            CompoundTag concealmentTag = new CompoundTag();
            concealmentTag.putUUID("concealmentUUID", concealment);
            concealmentsWithTimersTag.add(concealmentTag);
        }
        tag.put("concealmentsWithTimers", concealmentsWithTimersTag);

        ListTag concealmentsTypeTag = new ListTag();
        for (Map.Entry<UUID, CONCEALMENT_TYPES> entry : this.concealmentsTypes.entrySet()){
            CompoundTag entryTag = new CompoundTag();

            entryTag.putUUID("concealmentUUID", entry.getKey());
            entryTag.putString("concealmentType", entry.getValue().name());

            concealmentsTypeTag.add(entryTag);
        }
        tag.put("concealmentsTypes", concealmentsTypeTag);

        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag tag) {
        concealmentsCreators.clear();
        concealmentsSequences.clear();
        concealmentsHasTimers.clear();
        concealmentsTimers.clear();
        concealmentsWithTimers.clear();
        concealmentsTypes.clear();

        ListTag creatorsTag = tag.getList("concealmentsCreators", 10);
        for (int i = 0; i < creatorsTag.size(); i++) {
            CompoundTag entry = creatorsTag.getCompound(i);
            UUID concealmentUUID = entry.getUUID("concealmentUUID");
            UUID creatorUUID = entry.getUUID("creatorUUID");
            concealmentsCreators.put(concealmentUUID, creatorUUID);
        }

        ListTag sequencesTag = tag.getList("concealmentsSequences", 10);
        for (int i = 0; i < sequencesTag.size(); i++) {
            CompoundTag entry = sequencesTag.getCompound(i);
            UUID concealmentUUID = entry.getUUID("concealmentUUID");
            int sequence = entry.getInt("sequence");
            concealmentsSequences.put(concealmentUUID, sequence);
        }

        ListTag hasTimerTag = tag.getList("concealmentsHasTimers", 10);
        for (int i = 0; i < hasTimerTag.size(); i++) {
            CompoundTag entry = hasTimerTag.getCompound(i);
            UUID concealmentUUID = entry.getUUID("concealmentUUID");
            boolean hasTimer = entry.getBoolean("hasTimer");
            concealmentsHasTimers.put(concealmentUUID, hasTimer);
        }

        ListTag timerTag = tag.getList("concealmentsTimers", 10);
        for (int i = 0; i < timerTag.size(); i++) {
            CompoundTag entry = timerTag.getCompound(i);
            UUID concealmentUUID = entry.getUUID("concealmentUUID");
            int timer = entry.getInt("timer");
            concealmentsTimers.put(concealmentUUID, timer);
        }

        ListTag concealmentsWithTimersTag = tag.getList("concealmentsWithTimers", 10);
        for (int i = 0; i < concealmentsWithTimersTag.size(); i++) {
            concealmentsWithTimers.add(concealmentsWithTimersTag.getCompound(i).getUUID("concealmentUUID"));
        }

        ListTag concealmentTypeTag = tag.getList("concealmentsTypes", 10);
        for (int i = 0; i < concealmentTypeTag.size(); i++) {
            CompoundTag entry = concealmentTypeTag.getCompound(i);
            UUID sealUUID = entry.getUUID("concealmentUUID");
            CONCEALMENT_TYPES type = CONCEALMENT_TYPES.valueOf(entry.getString("concealmentType"));
            concealmentsTypes.put(sealUUID, type);
        }
    }

    public void copyFrom(ConcealedDataCapability other) {
        this.concealmentsCreators = new HashMap<>(other.concealmentsCreators);
        this.concealmentsSequences = new HashMap<>(other.concealmentsSequences);
        this.concealmentsHasTimers = new HashMap<>(other.concealmentsHasTimers);
        this.concealmentsTimers = new HashMap<>(other.concealmentsTimers);
        this.concealmentsWithTimers = new HashSet<>(other.concealmentsWithTimers);
        this.concealmentsTypes = new HashMap<>(other.concealmentsTypes);
    }
}