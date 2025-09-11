package net.swimmingtuna.lotm.events.NewEventLoop.EventManager;


import net.swimmingtuna.lotm.events.NewEventLoop.LayerClasses.*;
import net.swimmingtuna.lotm.events.NewEventLoop.LayerClasses.BeyonderTicks.Apprentice.ApprenticeSPTickLayer;
import net.swimmingtuna.lotm.events.NewEventLoop.LayerClasses.BeyonderTicks.Apprentice.ApprenticeTickLayer;
import net.swimmingtuna.lotm.events.NewEventLoop.LayerClasses.BeyonderTicks.Apprentice.WaterWalkingLayer;
import net.swimmingtuna.lotm.events.NewEventLoop.LayerClasses.BeyonderTicks.Monster.MonsterTickLayer;
import net.swimmingtuna.lotm.events.NewEventLoop.LayerClasses.BeyonderTicks.Sailor.SailorTickLayer;
import net.swimmingtuna.lotm.events.NewEventLoop.LayerClasses.BeyonderTicks.Spectator.SpectatorTickLayer;
import net.swimmingtuna.lotm.events.NewEventLoop.LayerClasses.BeyonderTicks.Warrior.WarriorTickLayer;

public enum EFunctions {
    // Original functions
    APPRENTICE_SP_TICK(new ApprenticeSPTickLayer()),
    APPRENTICE_TICK(new ApprenticeTickLayer()),
    BLINK_STATE(new BlinkStateLayer()),
    DOOR_MIRAGE(new DoorMirageLayer()),
    ENVISION_KINGDOM(new EnvisionKingdomLayer()),
    EXILE(new ExileLayer()),
    GRAVITY_MANIPULATION(new GravityManipulationLayer()),
    LIGHTNINGREDIRECTION(new LightningRedirectionLayer()),
    MONSTER_TICK(new MonsterTickLayer()),
    PSYCHOLOGICAL_INVISIBILITY(new PsychologicalInvisibilityLayer()),
    RAIN_EYES(new RainEyesLayer()),
    REGENERATE_SPIRITUALITY(new RegenerateSpiritualityLayer()),
    SAILOR_TICK(new SailorTickLayer()),
    SEAL(new SealLayer()),
    SPECTATOR_TICK(new SpectatorTickLayer()),
    SPATIAL_CAGE(new SpatialCageLayer()),
    SYMBOLIZATION(new SymbolizationLayer()),
    VOLCANIC_ERUPTION(new VolcanicEruptionLayer()),
    WARRIOR_TICK(new WarriorTickLayer()),
    WATER_WALKING(new WaterWalkingLayer()),

    TRICKMASTERTELEKENESIS(new TrickmasterTelekenisisPassiveLayer()),
    PROPHECY(new ProphecyTickLayer()),
    SPECTATORPROPHECY(new SpectatorClassProphecyTickLayer()),
    DREAM_INTO_REALITY(new DreamIntoRealityLayer()),
    ACIDICRAIN(new AcidicRainTickLayer()),
    LIGHTNING_STORM(new LightningStormLayer()),
    TSUNAMI(new TsunamiLayer()),
    RAGINGBLOWS(new RagingBlowsTickLayer()),
    WATERSPHERE(new WaterSphereCheckLayer()),
    WIND_MANIPULATION_FLIGHT(new WindManipulationFlightLayer()),
    SIRENSONG(new SirenSongsLayer()),
    STAR_OF_LIGHTNING(new StarOfLightningLayer()),
    DIVINE_HAND_COOLDOWN_DECREASE(new DivineHandCooldownDecreaseLayer()),
    EARTHQUAKE(new EarthquakeLayer()),
    HURRICANE(new HurricaneLayer()),
    AFFECTEDBYEXTREMECOLDNESS(new ExtremeColdnessLayer()),
    CALAMITYINCARNATIONTSUNAMI(new CalamityIncarnationTsunamiTickLayer()),
    MANIPULATE_MOVEMENT(new ManipulateMovementLayer()),
    CONSCIOUSNESS_STROLL(new ConsciousnessStrollLayer()),
    CALAMITY_INCARNATION_TORNADO(new CalamityIncarnationTornadoLayer()),
    RAGINGCOMBO(new RagingComboLayer()),
    WIND_MANIPULATION_SENSE(new WindManipulationSenseLayer()),
    LIGHTNINGTRAVEL(new SailorLightningTravelLayer()),
    NIGHTMARE_TICK(new NightmareTickLayer()),
    CALAMITY_UNDEAD_ARMY(new CalamityUndeadArmyLayer()),
    CALAMITY_LIGHTNING_STORM(new CalamityLightningStormLayer()),
    WARRIOR_DANGER_SENSE(new WarriorDangerSenseLayer()),
    DECREMENT_MONSTER_ATTACK_EVENT(new DecrementMonsterAttackEventLayer()),
    CHAOSWALKERCOMBAT(new OnChaosWalkerCombatLayer()),
    MONSTER_DANGER_SENSE(new MonsterDangerSenseLayer()),
    GLOBE_OF_TWILIGHT_TICK(new GlobeOfTwilightTickLayer()),
    MERCURYLIQUEFICATIONTICK(new MercuryTickLayer()),
    FATEREINCARNATION(new MonsterReincarnationCheckerLayer()),
    TWILIGHTACCELERATE(new TwilightAccelerateTickLayer()),
    TWILIGHT_FREEZE_TICK(new TwilightFreezeTickLayer()),
    MISFORTUNEIMPLOSIONLIGHTNING(new MisfortuneImplosionLightningLayer()),
    TWILIGHT_LIGHT_TICK(new TwilightLightTickLayer()),
    MATTER_ACCELERATION_BLOCKS(new MatterAccelerationBlocksLayer());


    // Additional enum values to add to your existing EFunctions enum


    private final IFunction func;

    EFunctions(IFunction obj) {
        func = obj;
    }

    public IFunction get() {
        return func;
    }
}