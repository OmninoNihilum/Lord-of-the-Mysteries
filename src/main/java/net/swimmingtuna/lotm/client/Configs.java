package net.swimmingtuna.lotm.client;

import com.google.common.collect.ImmutableList;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.swimmingtuna.lotm.util.PlayerMobs.ItemManager;
import net.swimmingtuna.lotm.util.PlayerMobs.NameManager;
import net.swimmingtuna.lotm.util.PlayerMobs.ThreadUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

public class Configs {

    public static final Common COMMON;
    public static final ForgeConfigSpec commonSpec;

    static {
        Pair<Common, ForgeConfigSpec> pair = new ForgeConfigSpec.Builder().configure(Common::new);
        commonSpec = pair.getRight();
        COMMON = pair.getKey();
    }

    public static class Common {

        //LOTM CONFIGS
        public ForgeConfigSpec.IntValue damageMultiplier;
        public ForgeConfigSpec.BooleanValue shouldDestroyBlocks;
        public ForgeConfigSpec.BooleanValue shouldNpcSpawn;
        public ForgeConfigSpec.BooleanValue shouldDropCharacteristic;
        public ForgeConfigSpec.BooleanValue shouldResetSequence;
        public ForgeConfigSpec.BooleanValue pathwaySafetyNet;
        public ForgeConfigSpec.BooleanValue mobsShouldActivateCalamities;
        public ForgeConfigSpec.BooleanValue mobsShouldOnlyUseAbilitiesOnPlayers;
        public ForgeConfigSpec.BooleanValue shouldUseAbilitiesNearSpawn;
        public ForgeConfigSpec.BooleanValue factionsEnabled;

        //PLAYER MOB CONFIGS
        public ForgeConfigSpec.ConfigValue<List<? extends String>> mainItems;
        public ForgeConfigSpec.ConfigValue<List<? extends String>> offhandItems;
        public ForgeConfigSpec.ConfigValue<List<? extends String>> tippedArrowBlocklistStrings;
        public final List<ResourceLocation> tippedArrowBlocklist = new CopyOnWriteArrayList<>();
        public ForgeConfigSpec.ConfigValue<List<? extends String>> mobNames;
        public ForgeConfigSpec.ConfigValue<List<? extends String>> nameLinks;


        public Common(ForgeConfigSpec.Builder builder) {
            builder.push("LOTMC Configs");
            damageMultiplier = builder
                    .comment("The amount that damage should be multiplied by in the mod, damage doesn't mean damage dealt, but just how much stronger the ability will be in one way or another.",
                            "The ability can either be made to have a larger range, longer effect duration, more damage, or something else. Very case by case.",
                            "Max of 10, Min of 1.")
                    .defineInRange("Damage Multiplier", 1, 1, 10);

            shouldDestroyBlocks = builder
                    .comment("If disabled, most abilities that don't rely on block breaking/altering to work properly won't destroy/alter blocks")
                    .define("Abilities Should Break Blocks", true);

            shouldNpcSpawn = builder
                    .comment("Disables or enables whether NPCs should naturally spawn in the world.")
                    .define("NPC Should Spawn", true);

            shouldDropCharacteristic = builder
                    .comment("If enabled, when killed by a player, you will drop a beyonder characteristic which can be used to make potions, substituting main ingredients.")
                    .define("Should Drop Characteristic", false);

            shouldResetSequence = builder
                    .comment("If you have the config option to drop characteristics on death, this will control whether you reset a sequence (if true) or simply go down a sequence (if false).")
                    .define("Should Reset Sequence", false);

            pathwaySafetyNet = builder
                    .comment("If you have the config option to drop characteristics on death, this will control if at sequences 8 and 4, you don't decrement a sequence or drop characteristics.")
                    .define("Should Have Pathway Safety Net", false);

            mobsShouldActivateCalamities = builder
                    .comment("If enabled, mobs from Monster Sequence 6 or higher will have calamities activate passively")
                    .define("Mobs Should Activate Calamities", true);

            mobsShouldOnlyUseAbilitiesOnPlayers = builder
                    .comment("If enabled, mobs will only use abilities on players and not other mobs.")
                    .define("Mobs Should Only Use Abilities On Players", true);

            shouldUseAbilitiesNearSpawn = builder
                    .comment("If disabled, abilities won't be able to be used near spawn.")
                    .define("Can Use Abilities Near Spawn", true);

            factionsEnabled = builder
                    .comment("If enabled, you can use commands to make factions and play with them as you would with a plugin.")
                    .define("Should have factions", false);


            builder.pop()
                    .comment("NPC Config")
                    .push("spawning");

            mainItems = builder
                    .comment("A list of items that the player mobs can spawn with.",
                            "Default is 40% for a bow, 10% for a crossbow and 50% for a sword, then the swords are distributed after that.",
                            "There is a separated chance to spawn with an item at all, this is to pick what to spawn when it does",
                            "Syntax is \"namespace:id-weight\"")
                    .defineList("Spawn Items", DEFAULT_MAIN_HAND_ITEMS, Common::validString);

            offhandItems = builder
                    .comment("What item to be able to spawn in the offhand",
                            "Offhand items can only spawn when on hard difficulty",
                            "It won't spawn an item in the offhand if it spawns with a bow like item.",
                            "There is a separated chance to spawn with an item at all, this is to pick what it to spawn when it does",
                            "Syntax is \"namespace:id-weight\"")
                    .defineList("Spawn Items Offhand", DEFAULT_OFFHAND_ITEMS, Common::validString);

            tippedArrowBlocklistStrings = builder
                    .comment("A list of potion \"namespace:id\" to block from getting applied to tipped arrows")
                    .defineList("Tipped Arrow Blocklist", DEFAULT_BLOCKED_POTIONS, Common::validResourceLocation);

            builder.pop()
                    .comment("Configs related to the names of the mobs.")
                    .push("names");

            nameLinks = builder
                    .comment("A list of links to get names that the player mobs can have.",
                            "The names need to be separated by a newline.",
                            "Names from these links are combined with the named from below",
                            "As an example you have Twitch subs in the game by using https://whitelist.gorymoon.se")
                    .defineList("Name Links", ImmutableList.of(), Common::validString);

            mobNames = builder
                    .comment("A list of names that the player mobs can have.")
                    .defineList("Mob Names", DEFAULT_NAMES, Common::validString);


            builder.pop();
        }

        private static boolean validString(Object o) {
            return o instanceof String && !StringUtils.isEmpty((String) o);
        }

        private static boolean validResourceLocation(Object o) {
            return validString(o) && ResourceLocation.tryParse((String) o) != null;
        }

        @SubscribeEvent
        void onLoad(ModConfigEvent.Loading event) {
            configReload();
        }

        @SubscribeEvent
        void onReload(ModConfigEvent.Reloading event) {
            configReload();
        }

        private void configReload() {
            ThreadUtils.tryRunOnMain(() -> {
                tippedArrowBlocklist.clear();
                tippedArrowBlocklist.addAll(tippedArrowBlocklistStrings.get().stream()
                        .map(ResourceLocation::tryParse)
                        .filter(Objects::nonNull)
                        .toList());
                NameManager.INSTANCE.configLoad();
                ItemManager.INSTANCE.configLoad();
            });
        }

        public static final List<String> DEFAULT_MAIN_HAND_ITEMS = ImmutableList.of(
                "minecraft:bow-90",
                "minecraft:crossbow-10",
                "minecraft:stone_sword-64",
                "minecraft:iron_sword-20",
                "minecraft:golden_sword-10",
                "minecraft:diamond_sword-5",
                "minecraft:netherite_sword-1"
        );

        public static final List<String> DEFAULT_OFFHAND_ITEMS = ImmutableList.of(
                "minecraft:shield-1",
                "minecraft:air-4"
        );

        private static final List<String> DEFAULT_BLOCKED_POTIONS = ImmutableList.of(
                "minecraft:awkward",
                "minecraft:empty",
                "minecraft:fire_resistance",
                "minecraft:healing",
                "minecraft:invisibility",
                "minecraft:leaping",
                "minecraft:long_fire_resistance",
                "minecraft:long_invisibility",
                "minecraft:long_leaping",
                "minecraft:long_night_vision",
                "minecraft:long_regeneration",
                "minecraft:long_strength",
                "minecraft:long_swiftness",
                "minecraft:long_turtle_master",
                "minecraft:long_water_breathing",
                "minecraft:luck",
                "minecraft:mundane",
                "minecraft:night_vision",
                "minecraft:regeneration",
                "minecraft:strength",
                "minecraft:strong_healing",
                "minecraft:strong_leaping",
                "minecraft:strong_regeneration",
                "minecraft:strong_strength",
                "minecraft:strong_swiftness",
                "minecraft:strong_turtle_master",
                "minecraft:swiftness",
                "minecraft:thick",
                "minecraft:turtle_master",
                "minecraft:water",
                "minecraft:water_breathing"
        );

        public static final List<String> DEFAULT_NAMES = ImmutableList.of(
                "VoidedMC85",
                "1um1",
                "Rosamie",
                "SwimmingTuna",
                "Shineelight",
                "Pandarion05",
                "Expired_Fanta",
                "tristanomg7894",
                "Gory_Moon",
                "Darkosto",
                "016Nojr",
                "BluSunrize",
                "Buuz135",
                "Darkere",
                "Darkhax",
                "Drullkus",
                "Ellpeck",
                "Emberwalker",
                "Gigabit101",
                "Kamefrede",
                "KnightMiner_",
                "Lat",
                "LexManos",
                "Mrbysco",
                "P3pp3rF1y",
                "Ray",
                "Ridanis",
                "SOTMead",
                "ShyNieke",
                "SkySom",
                "Soaryn",
                "TamasHenning",
                "ValkyrieofNight",
                "XCompWiz",
                "cpw11",
                "darkphan",
                "direwolf20",
                "dmodoomsirius",
                "dmodoomsirius",
                "malte0811",
                "nekosune",
                "neptunepink",
                "vadis365",
                "wyld",
                "paulsoaresjr",
                "Mhykol",
                "Vswe",
                "TurkeyDev",
                "Gen_Deathrow",
                "Sevadus",
                "jeb_",
                "Dinnerbone",
                "Grumm",
                "fry_",
                "LightPhoenix132",
                "Rei_Kaneki12",
                "FantasyFan",
                "Adapter",
                "JediShaggy",
                "Isaacwash1122",
                "Gl_Ink",
                "Voiddustts",
                "AlphaMuppet",
                "Althoros",
                "L9kii",
                "thinkfastnow"
        );
    }
}

