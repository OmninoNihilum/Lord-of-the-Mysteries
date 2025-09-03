package net.swimmingtuna.lotm.datagen.loot;

import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.minecraftforge.common.data.GlobalLootModifierProvider;
import net.minecraftforge.common.loot.LootTableIdCondition;
import net.swimmingtuna.lotm.LOTM;
import net.swimmingtuna.lotm.init.ItemInit;
import net.swimmingtuna.lotm.world.loot.AddItemModifier;

import java.util.List;

public class ModGlobalLootModifiersProvide extends GlobalLootModifierProvider {

    public ModGlobalLootModifiersProvide(PackOutput output) {
        super(output, LOTM.MOD_ID);
    }

    @Override
    protected void start() {
        add("seq_9_recipes_from_chest", new AddItemModifier(
                new LootItemCondition[]{
                        new LootTableIdCondition.Builder(new ResourceLocation("chests/simple_dungeon"))
                                .or(
                                        new LootTableIdCondition.Builder(new ResourceLocation("chests/spawn_bonus_chest"))
                                )
                                .or(
                                        new LootTableIdCondition.Builder(new ResourceLocation("chests/ruined_portal"))
                                ).build(),
                        LootItemRandomChanceCondition.randomChance(0.5f).build()
                },
                List.of(
                        ItemInit.SPECTATOR_9_RECIPE.get(),
                        ItemInit.WARRIOR_9_RECIPE.get(),
                        ItemInit.MONSTER_9_RECIPE.get(),
                        ItemInit.SAILOR_9_RECIPE.get(),
                        ItemInit.APPRENTICE_9_RECIPE.get()

                )
        ));

        add("seq_8_recipes_from_chest", new AddItemModifier(
                new LootItemCondition[]{
                        new LootTableIdCondition.Builder(new ResourceLocation("chests/shipwreck_treasure"))
                                .or(new LootTableIdCondition.Builder(new ResourceLocation("chests/shipwreck_supply")))
                                .or(new LootTableIdCondition.Builder(new ResourceLocation("chests/shipwreck_map")))
                                .or(new LootTableIdCondition.Builder(new ResourceLocation("chests/ruined_portal")))
                                .or(new LootTableIdCondition.Builder(new ResourceLocation("chests/desert_pyramid")))
                                .build(),
                        LootItemRandomChanceCondition.randomChance(0.36f).build()
                },
                List.of(
                        ItemInit.SPECTATOR_8_RECIPE.get(),
                        ItemInit.WARRIOR_8_RECIPE.get(),
                        ItemInit.MONSTER_8_RECIPE.get(),
                        ItemInit.SAILOR_8_RECIPE.get(),
                        ItemInit.APPRENTICE_8_RECIPE.get()

                )
        ));

        add("seq_7_recipes_from_chest", new AddItemModifier(
                new LootItemCondition[]{
                        new LootTableIdCondition.Builder(new ResourceLocation("chests/desert_pyramid"))
                                .or(new LootTableIdCondition.Builder(new ResourceLocation("chests/jungle_temple")))
                                .or(new LootTableIdCondition.Builder(new ResourceLocation("chests/underwater_ruin_big")))
                                .or(new LootTableIdCondition.Builder(new ResourceLocation("chests/underwater_ruin_small")))
                                .or(new LootTableIdCondition.Builder(new ResourceLocation("chests/pillager_outpost")))
                                .build(),
                        LootItemRandomChanceCondition.randomChance(0.29f).build()
                },
                List.of(
                        ItemInit.SPECTATOR_7_RECIPE.get(),
                        ItemInit.WARRIOR_7_RECIPE.get(),
                        ItemInit.MONSTER_7_RECIPE.get(),
                        ItemInit.SAILOR_7_RECIPE.get(),
                        ItemInit.APPRENTICE_7_RECIPE.get()

                )
        ));

        add("seq_6_recipes_from_chest", new AddItemModifier(
                new LootItemCondition[]{
                        new LootTableIdCondition.Builder(new ResourceLocation("chests/woodland_mansion"))
                                .or(new LootTableIdCondition.Builder(new ResourceLocation("chests/nether_bridge")))
                                .or(new LootTableIdCondition.Builder(new ResourceLocation("chests/abandoned_mineshaft")))
                                .or(new LootTableIdCondition.Builder(new ResourceLocation("chests/stronghold_corridor")))
                                .or(new LootTableIdCondition.Builder(new ResourceLocation("chests/stronghold_library")))
                                .or(new LootTableIdCondition.Builder(new ResourceLocation("chests/stronghold_crossing")))
                                .or(new LootTableIdCondition.Builder(new ResourceLocation("chests/igloo_chest")))
                                .build(),
                        LootItemRandomChanceCondition.randomChance(0.22f).build()
                },
                List.of(
                        ItemInit.SPECTATOR_6_RECIPE.get(),
                        ItemInit.WARRIOR_6_RECIPE.get(),
                        ItemInit.MONSTER_6_RECIPE.get(),
                        ItemInit.SAILOR_6_RECIPE.get(),
                        ItemInit.APPRENTICE_6_RECIPE.get()

                )
        ));

        add("seq_5_recipes_from_chest", new AddItemModifier(
                new LootItemCondition[]{
                        new LootTableIdCondition.Builder(new ResourceLocation("chests/stronghold_corridor"))
                                .or(new LootTableIdCondition.Builder(new ResourceLocation("chests/stronghold_library")))
                                .or(new LootTableIdCondition.Builder(new ResourceLocation("chests/stronghold_crossing")))
                                .or(new LootTableIdCondition.Builder(new ResourceLocation("chests/woodland_mansion")))
                                .or(new LootTableIdCondition.Builder(new ResourceLocation("chests/abandoned_mineshaft")))
                                .or(new LootTableIdCondition.Builder(new ResourceLocation("chests/pillager_outpost")))
                                .build(),
                        LootItemRandomChanceCondition.randomChance(0.18f).build()
                },
                List.of(
                        ItemInit.SPECTATOR_5_RECIPE.get(),
                        ItemInit.WARRIOR_5_RECIPE.get(),
                        ItemInit.MONSTER_5_RECIPE.get(),
                        ItemInit.SAILOR_5_RECIPE.get(),
                        ItemInit.APPRENTICE_5_RECIPE.get()

                )
        ));

        add("seq_4_recipes_from_chest", new AddItemModifier(
                new LootItemCondition[]{
                        new LootTableIdCondition.Builder(new ResourceLocation("entities/ender_dragon"))
                                .or(new LootTableIdCondition.Builder(new ResourceLocation("entities/wither")))
                                .build(),
                        LootItemRandomChanceCondition.randomChance(0.14f).build()
                },
                List.of(
                        ItemInit.SPECTATOR_4_RECIPE.get(),
                        ItemInit.WARRIOR_4_RECIPE.get(),
                        ItemInit.MONSTER_4_RECIPE.get(),
                        ItemInit.SAILOR_4_RECIPE.get(),
                        ItemInit.APPRENTICE_4_RECIPE.get()

                )
        ));

        add("seq_3_recipes_from_chest", new AddItemModifier(
                new LootItemCondition[]{
                        new LootTableIdCondition.Builder(new ResourceLocation("chests/end_city_treasure")).build(),
                        LootItemRandomChanceCondition.randomChance(0.10f).build()
                },
                List.of(
                        ItemInit.SPECTATOR_3_RECIPE.get(),
                        ItemInit.WARRIOR_3_RECIPE.get(),
                        ItemInit.MONSTER_3_RECIPE.get(),
                        ItemInit.SAILOR_3_RECIPE.get(),
                        ItemInit.APPRENTICE_3_RECIPE.get()

                )
        ));

        add("seq_2_recipes_from_chest", new AddItemModifier(
                new LootItemCondition[]{
                        new LootTableIdCondition.Builder(new ResourceLocation("chests/bastion_bridge"))
                                .or(new LootTableIdCondition.Builder(new ResourceLocation("chests/bastion_hoglin_stable")))
                                .or(new LootTableIdCondition.Builder(new ResourceLocation("chests/bastion_other")))
                                .or(new LootTableIdCondition.Builder(new ResourceLocation("chests/bastion_treasure")))
                                .build(),
                        LootItemRandomChanceCondition.randomChance(0.08f).build()
                },
                List.of(
                        ItemInit.SPECTATOR_2_RECIPE.get(),
                        ItemInit.WARRIOR_2_RECIPE.get(),
                        ItemInit.MONSTER_2_RECIPE.get(),
                        ItemInit.SAILOR_2_RECIPE.get(),
                        ItemInit.APPRENTICE_2_RECIPE.get()

                )
        ));

        add("seq_1_recipes_from_chest", new AddItemModifier(
                new LootItemCondition[]{
                        new LootTableIdCondition.Builder(new ResourceLocation("chests/ancient_city"))
                                .or(new LootTableIdCondition.Builder(new ResourceLocation("chests/ancient_city_ice_box")))
                                .build(),
                        LootItemRandomChanceCondition.randomChance(0.05f).build()
                },
                List.of(
                        ItemInit.SPECTATOR_1_RECIPE.get(),
                        ItemInit.WARRIOR_1_RECIPE.get(),
                        ItemInit.MONSTER_1_RECIPE.get(),
                        ItemInit.SAILOR_1_RECIPE.get(),
                        ItemInit.APPRENTICE_1_RECIPE.get()

                )
        ));

        add("seq_0_recipes_from_chest", new AddItemModifier(
                new LootItemCondition[]{
                        new LootTableIdCondition.Builder(new ResourceLocation("entities/warden")).build(),
                        LootItemRandomChanceCondition.randomChance(0.10f).build()
                },
                List.of(
                        ItemInit.SPECTATOR_0_RECIPE.get(),
                        ItemInit.WARRIOR_0_RECIPE.get(),
                        ItemInit.MONSTER_0_RECIPE.get(),
                        ItemInit.SAILOR_0_RECIPE.get(),
                        ItemInit.APPRENTICE_0_RECIPE.get()
                )
        ));
    }
}
