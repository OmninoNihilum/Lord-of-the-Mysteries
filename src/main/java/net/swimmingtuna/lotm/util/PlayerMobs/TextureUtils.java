package net.swimmingtuna.lotm.util.PlayerMobs;

import com.google.common.hash.Hashing;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.blaze3d.systems.RenderSystem;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.client.resources.SkinManager;
import net.minecraft.resources.ResourceLocation;
import net.swimmingtuna.lotm.entity.PlayerMobEntity;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class TextureUtils {

    private static final Map<UUID, SkinType> SKIN_TYPE_CACHE = new Object2ObjectOpenHashMap<>();
    private static final Map<UUID, Long> FAILED_REQUESTS_COOLDOWN = new ConcurrentHashMap<>();
    private static final long COOLDOWN_TIME = 30000;

    public static SkinType getPlayerSkinType(@Nullable GameProfile profile) {
        SkinType type = SkinType.DEFAULT;
        if (profile != null && profile.isComplete()) {
            if (SKIN_TYPE_CACHE.containsKey(profile.getId())) {
                type = SKIN_TYPE_CACHE.get(profile.getId());
            } else {
                if (isOnCooldown(profile.getId())) {
                    return getType(DefaultPlayerSkin.getSkinModelName(profile.getId()));
                }
                try {
                    Minecraft mc = Minecraft.getInstance();
                    Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> map = mc.getSkinManager().getInsecureSkinInformation(profile);
                    if (map.containsKey(MinecraftProfileTexture.Type.SKIN)) {
                        String stringType = map.get(MinecraftProfileTexture.Type.SKIN).getMetadata("model");
                        SKIN_TYPE_CACHE.put(profile.getId(), type = getType(stringType));
                        FAILED_REQUESTS_COOLDOWN.remove(profile.getId());
                    } else {
                        type = getType(DefaultPlayerSkin.getSkinModelName(profile.getId()));
                    }
                } catch (Exception e) {
                    FAILED_REQUESTS_COOLDOWN.put(profile.getId(), System.currentTimeMillis());
                    type = getType(DefaultPlayerSkin.getSkinModelName(profile.getId()));
                }
            }
        }
        return type;
    }

    private static SkinType getType(@Nullable String stringType) {
        return "slim".equals(stringType) ? SkinType.SLIM : SkinType.DEFAULT;
    }

    public static ResourceLocation getPlayerSkin(PlayerMobEntity entity) {
        return getTexture(entity, MinecraftProfileTexture.Type.SKIN).orElse(DefaultPlayerSkin.getDefaultSkin());
    }

    public static Optional<ResourceLocation> getPlayerCape(PlayerMobEntity entity) {
        return getTexture(entity, MinecraftProfileTexture.Type.CAPE);
    }

    @SuppressWarnings("deprecation")
    private static Optional<ResourceLocation> getTexture(PlayerMobEntity entity, MinecraftProfileTexture.Type type) {
        if (entity.isTextureAvailable(type)) {
            return Optional.of(entity.getTexture(type));
        }

        GameProfile profile = entity.getProfile();
        if (profile != null && !profile.isComplete()) {
            return getDefault(profile, type);
        }

        if (profile != null && profile.getName() != null) {
            if (isOnCooldown(profile.getId())) {
                return getDefault(profile, type);
            }

            try {
                Minecraft mc = Minecraft.getInstance();
                Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> map = mc.getSkinManager().getInsecureSkinInformation(profile);
                if (map.containsKey(type)) {
                    MinecraftProfileTexture profileTexture = map.get(type);
                    String s = Hashing.sha1().hashUnencodedChars(profileTexture.getHash()).toString();
                    ResourceLocation location = SkinManager.getTextureLocation(type, s);
                    if (mc.textureManager.getTexture(location, MissingTextureAtlasSprite.getTexture()) != MissingTextureAtlasSprite.getTexture()) {
                        FAILED_REQUESTS_COOLDOWN.remove(profile.getId());
                        return Optional.of(location);
                    } else {
                        RenderSystem.recordRenderCall(() -> {
                            mc.getSkinManager().registerTexture(profileTexture, type, entity.getSkinCallback());
                        });
                        FAILED_REQUESTS_COOLDOWN.remove(profile.getId());
                    }
                }
            } catch (Exception e) {
                FAILED_REQUESTS_COOLDOWN.put(profile.getId(), System.currentTimeMillis());
                return getDefault(profile, type);
            }
        }
        return getDefault(profile, type);
    }

    private static boolean isOnCooldown(UUID profileId) {
        Long lastFailTime = FAILED_REQUESTS_COOLDOWN.get(profileId);
        if (lastFailTime == null) {
            return false;
        }

        boolean onCooldown = System.currentTimeMillis() - lastFailTime < COOLDOWN_TIME;
        if (!onCooldown) {
            FAILED_REQUESTS_COOLDOWN.remove(profileId);
        }
        return onCooldown;
    }

    private static Optional<ResourceLocation> getDefault(@Nullable GameProfile profile, MinecraftProfileTexture.Type type) {
        if (type == MinecraftProfileTexture.Type.CAPE || type == MinecraftProfileTexture.Type.ELYTRA) {
            return Optional.empty();
        } else {
            return Optional.of(profile != null && profile.isComplete() ? DefaultPlayerSkin.getDefaultSkin(profile.getId()) : DefaultPlayerSkin.getDefaultSkin());
        }
    }

    public static void clearCooldowns() {
        FAILED_REQUESTS_COOLDOWN.clear();
    }

    public enum SkinType {
        DEFAULT,
        SLIM
    }
}