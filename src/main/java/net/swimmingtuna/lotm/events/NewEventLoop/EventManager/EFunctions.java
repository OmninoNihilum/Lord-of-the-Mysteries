package net.swimmingtuna.lotm.events.NewEventLoop.EventManager;


import net.swimmingtuna.lotm.events.NewEventLoop.LayerClasses.*;
import net.swimmingtuna.lotm.events.NewEventLoop.LayerClasses.BeyonderTicks.Apprentice.ApprenticeSPTickLayer;
import net.swimmingtuna.lotm.events.NewEventLoop.LayerClasses.BeyonderTicks.Apprentice.ApprenticeTickLayer;
import net.swimmingtuna.lotm.events.NewEventLoop.LayerClasses.BeyonderTicks.Apprentice.WaterWalkingLayer;
import net.swimmingtuna.lotm.events.NewEventLoop.LayerClasses.MisfortuneLightningStorm;
import net.swimmingtuna.lotm.events.NewEventLoop.LayerClasses.CalamityExplosionLayer;
import net.swimmingtuna.lotm.events.NewEventLoop.LayerClasses.BeyonderTicks.Monster.MonsterTickLayer;
import net.swimmingtuna.lotm.events.NewEventLoop.LayerClasses.BeyonderTicks.Sailor.SailorTickLayer;
import net.swimmingtuna.lotm.events.NewEventLoop.LayerClasses.BeyonderTicks.Spectator.SpectatorTickLayer;
import net.swimmingtuna.lotm.events.NewEventLoop.LayerClasses.BeyonderTicks.Warrior.WarriorTickLayer;

public enum EFunctions {
    // Original functions

    //APPRENTICE
    APPRENTICE_SP_TICK(new ApprenticeSPTickLayer()),
    APPRENTICE_TICK(new ApprenticeTickLayer()),
    BLINK_STATE(new BlinkStateLayer()),
    DOOR_MIRAGE(new DoorMirageLayer()),
    EXILE(new ExileLayer()),
    GRAVITY_MANIPULATION(new GravityManipulationLayer()),
    SPATIAL_CAGE(new SpatialCageLayer()),
    SYMBOLIZATION(new SymbolizationLayer()),
    WATER_WALKING(new WaterWalkingLayer()),
    DOOR_LAYERING_TICK(new DoorLayeringTickLayer()),
    CONCEPTUALIZATION_TICK(new ConceptualizationTickLayer()),
    STARFALL_TICK(new StarfallTickLayer()),
    INVISIBLE_HAND_TICK(new InvisibleHandTickLayer()),
    SPATIAL_MAZE_TICK(new SpatialMazeTickLayer()),
    TRICKMASTERTELEKENESIS(new TrickmasterTelekenisisPassiveLayer()), //TEST AGAIN

    //SAILOR
    LIGHTNINGREDIRECTION(new LightningRedirectionLayer()),
    RAIN_EYES(new RainEyesLayer()),
    SAILOR_TICK(new SailorTickLayer()),
    VOLCANIC_ERUPTION(new VolcanicEruptionLayer()),
    ACIDICRAIN(new AcidicRainTickLayer()),
    LIGHTNING_STORM(new LightningStormLayer()),
    TSUNAMI(new TsunamiLayer()),
    RAGINGBLOWS(new RagingBlowsTickLayer()),
    WATERSPHERE(new WaterSphereCheckLayer()),
    WIND_MANIPULATION_FLIGHT(new WindManipulationFlightLayer()),
    SIRENSONG(new SirenSongsLayer()),
    STAR_OF_LIGHTNING(new StarOfLightningLayer()),
    EARTHQUAKE(new EarthquakeLayer()),
    HURRICANE(new HurricaneLayer()),
    AFFECTEDBYEXTREMECOLDNESS(new ExtremeColdnessLayer()),
    CALAMITYINCARNATIONTSUNAMI(new CalamityIncarnationTsunamiTickLayer()),
    CALAMITY_INCARNATION_TORNADO(new CalamityIncarnationTornadoLayer()),
    RAGINGCOMBO(new RagingComboLayer()),
    WIND_MANIPULATION_SENSE(new WindManipulationSenseLayer()),
    LIGHTNINGTRAVEL(new SailorLightningTravelLayer()),
    MATTER_ACCELERATION_BLOCKS(new MatterAccelerationBlocksLayer()),
    AQUEOUS_LIGHT_DROWN_TICK(new AqueousLightDrownTickLayer()),
    EXTREME_COLDNESS_TICK(new ExtremeColdnessTickLayer()),
    STORM_SEAL_TICK(new StormSealTickLayer()),
    TSUNAMI_SEAL_TICK(new TsunamiSealTickLayer()),

    //SPECTATOR
    ENVISION_KINGDOM(new EnvisionKingdomLayer()),
    PSYCHOLOGICAL_INVISIBILITY(new PsychologicalInvisibilityLayer()),
    SPECTATOR_TICK(new SpectatorTickLayer()),
    PROPHECY(new ProphecyTickLayer()),
    SPECTATORPROPHECY(new SpectatorClassProphecyTickLayer()),
    DREAM_INTO_REALITY(new DreamIntoRealityLayer()),
    MANIPULATE_MOVEMENT(new ManipulateMovementLayer()),
    CONSCIOUSNESS_STROLL(new ConsciousnessStrollLayer()),
    NIGHTMARE_TICK(new NightmareTickLayer()),
    DREAM_WALKING_TICK(new DreamWalkingTickLayer()),
    PSYCHE_STORM(new PsycheStormTickLayer()),
    PROPHESISE_TICK(new ProphesizeDemiseTickLayer()),
    DREAM_WEAVING(new DreamWeavingLayer()),

    //WARRIOR
    WARRIOR_TICK(new WarriorTickLayer()),
    DIVINE_HAND_COOLDOWN_DECREASE(new DivineHandCooldownDecreaseLayer()),
    WARRIOR_DANGER_SENSE(new WarriorDangerSenseLayer()),
    GLOBE_OF_TWILIGHT_TICK(new GlobeOfTwilightTickLayer()),
    MERCURYLIQUEFICATIONTICK(new MercuryTickLayer()),
    TWILIGHTACCELERATE(new TwilightAccelerateTickLayer()),
    TWILIGHT_FREEZE_TICK(new TwilightFreezeTickLayer()),
    TWILIGHT_LIGHT_TICK(new TwilightLightTickLayer()),
    DAWN_ARMOR_TICK(new DawnArmorTickEventLayer()),
    TWILIGHT_MANIFESTATION_TICK(new TwilightManifestationTickLayer()),
    MERCURY_LIQUEFICATION_TICK(new MercuryLiqueficationTickLayer()),
    GIGANTIFICATION_SCALE(new GigantificationScaleLayer()),
    WARRIOR_PROTECTION_TICK(new WarriorProtectionTickLayer()),
    DECREMENT_GUARDIAN_TIMER(new DecrementGuardianTimerLayer()),
    EYE_TICK(new EyeTickLayer()),
    AURA_OF_GLORY(new AuraOfGloryTickLayer()),
    GIGANTIFICATION_DESTROY_BLOCKS(new GigantificationDestroyBlocksLayer()),
    LIGHT_OF_DAWN(new LightOfDawnLayer()),
    LIGHT_CONCEALMENT_TICK(new LightConcealmentTickLayer()),

    //MONSTER
    MONSTER_TICK(new MonsterTickLayer()),
    CALAMITY_UNDEAD_ARMY(new CalamityUndeadArmyLayer()),
    CALAMITY_LIGHTNING_STORM(new CalamityLightningStormLayer()),
    DECREMENT_MONSTER_ATTACK_EVENT(new DecrementMonsterAttackEventLayer()),
    CHAOSWALKERCOMBAT(new OnChaosWalkerCombatLayer()),
    MONSTER_DANGER_SENSE(new MonsterDangerSenseLayer()),
    FATEREINCARNATION(new MonsterReincarnationCheckerLayer()),
    MISFORTUNEIMPLOSIONLIGHTNING(new MisfortuneImplosionLightningLayer()),
    CALAMITY_EXPLOSION(new CalamityExplosionLayer()),
    PROBABILITY_MANIPULATION_INFINITE(new ProbabilityManipulationInfiniteLayer()),
    CYCLE_OF_FATE(new CycleOfFateTickLayer()),
    MISFORTUNE_MANIPULATION(new MisfortuneManipulationTickLayer()),
    FALSE_PROPHECY(new FalseProphecyTickLayer()),
    AURA_OF_CHAOS(new AuraOfChaosLayer()),
    MISFORTUNE_LIGHTNING_STORM(new MisfortuneLightningStorm()),
    FALSE_PROPHECY_DOUBLE_DAMAGE(new FalseProphecyDoubleDamageLayer()),
    LUCK_DENIAL(new LuckDenialLayer()),
    MONSTER_CALAMITY_INCARNATION(new MonsterCalamityIncarnationLayer()),

    //OTHER
    SEAL(new SealLayer()),
    REGENERATE_SPIRITUALITY(new RegenerateSpiritualityLayer()),
    CONCEAL_TIMER(new ConcealTickLayer()),
    WINTRY_BLADE_TICK(new WintryBladeTickLayer()),
    DEATH_KNELL_NEGATIVE_TICK(new DeathKnellNegativeTickLayer()),
    FACTION_DATA_TICK(new FactionDataTickLayer());

    private final IFunction func;

    EFunctions(IFunction obj) {
        func = obj;
    }

    public IFunction get() {
        return func;
    }
}