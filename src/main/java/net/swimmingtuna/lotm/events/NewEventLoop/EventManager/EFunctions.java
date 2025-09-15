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
    FACTION_DATA_TICK(new FactionDataTickLayer()),
    MATTER_ACCELERATION_BLOCKS(new MatterAccelerationBlocksLayer()),
    DAWN_ARMOR_TICK(new DawnArmorTickEventLayer()),
    CONCEAL_TIMER(new ConcealTickLayer()),
    DOOR_LAYERING_TICK(new DoorLayeringTickLayer()),

    CONCEPTUALIZATION_TICK(new ConceptualizationTickLayer()),
    STARFALL_TICK(new StarfallTickLayer()),
    CALAMITY_EXPLOSION(new CalamityExplosionLayer()),
    TWILIGHT_MANIFESTATION_TICK(new TwilightManifestationTickLayer()),
    MERCURY_LIQUEFICATION_TICK(new MercuryLiqueficationTickLayer()),
    INVISIBLE_HAND_TICK(new InvisibleHandTickLayer()),
    GIGANTIFICATION_SCALE(new GigantificationScaleLayer()),
    WARRIOR_PROTECTION_TICK(new WarriorProtectionTickLayer()),
    DECREMENT_GUARDIAN_TIMER(new DecrementGuardianTimerLayer()),
    EYE_TICK(new EyeTickLayer()),
    WINTRY_BLADE_TICK(new WintryBladeTickLayer()),
    DEATH_KNELL_NEGATIVE_TICK(new DeathKnellNegativeTickLayer()),
    PROBABILITY_MANIPULATION_INFINITE(new ProbabilityManipulationInfiniteLayer()),
    CYCLE_OF_FATE(new CycleOfFateTickLayer()),
    DREAM_WALKING_TICK(new DreamWalkingTickLayer()),
    MISFORTUNE_MANIPULATION(new MisfortuneManipulationTickLayer()),
    FALSE_PROPHECY(new FalseProphecyTickLayer()),
    AURA_OF_CHAOS(new AuraOfChaosLayer()),
    PSYCHE_STORM(new PsycheStormTickLayer()),
    AURA_OF_GLORY(new AuraOfGloryTickLayer()),
    MISFORTUNE_LIGHTNING_STORM(new MisfortuneLightningStorm()),
    GIGANTIFICATION_DESTROY_BLOCKS(new GigantificationDestroyBlocksLayer()),
    LIGHT_OF_DAWN(new LightOfDawnLayer()),
    FALSE_PROPHECY_DOUBLE_DAMAGE(new FalseProphecyDoubleDamageLayer()),
    LUCK_DENIAL(new LuckDenialLayer()),
    MONSTER_CALAMITY_INCARNATION(new MonsterCalamityIncarnationLayer()),
    DREAM_WEAVING(new DreamWeavingLayer()),
    LIGHT_CONCEALMENT_TICK(new LightConcealmentTickLayer()),
    PROPHESISE_TICK(new ProphesizeDemiseTickLayer()),
    AQUEOUS_LIGHT_DROWN_TICK(new AqueousLightDrownTickLayer()),
    EXTREME_COLDNESS_TICK(new ExtremeColdnessTickLayer()),
    STORM_SEAL_TICK(new StormSealTickLayer()),
    SPATIAL_MAZE_TICK(new SpatialMazeTickLayer()),
    TSUNAMI_SEAL_TICK(new TsunamiSealTickLayer());

    private final IFunction func;

    EFunctions(IFunction obj) {
        func = obj;
    }

    public IFunction get() {
        return func;
    }
}