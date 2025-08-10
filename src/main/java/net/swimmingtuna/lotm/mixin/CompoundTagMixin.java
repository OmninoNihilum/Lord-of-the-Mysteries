package net.swimmingtuna.lotm.mixin;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

@Mixin(CompoundTag.class)
public class CompoundTagMixin {

    @Inject(method = "putInt(Ljava/lang/String;I)V", at = @At("HEAD"))
    private void onPutInt(String key, int value, CallbackInfo ci) {
        checkCallingMod("putInt", key);
    }

    @Inject(method = "putString(Ljava/lang/String;Ljava/lang/String;)V", at = @At("HEAD"))
    private void onPutString(String key, String value, CallbackInfo ci) {
        checkCallingMod("putString", key);
    }

    @Inject(method = "putDouble(Ljava/lang/String;D)V", at = @At("HEAD"))
    private void onPutDouble(String key, double value, CallbackInfo ci) {
        checkCallingMod("putDouble", key);
    }

    @Inject(method = "putFloat(Ljava/lang/String;F)V", at = @At("HEAD"))
    private void onPutFloat(String key, float value, CallbackInfo ci) {
        checkCallingMod("putFloat", key);
    }

    @Inject(method = "putUUID(Ljava/lang/String;Ljava/util/UUID;)V", at = @At("HEAD"))
    private void onPutUUID(String key, UUID value, CallbackInfo ci) {
        checkCallingMod("putUUID", key);
    }

    private void checkCallingMod(String method, String key) {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        for (int i = 3; i < stackTrace.length; i++) {
            String className = stackTrace[i].getClassName();
            if (className.startsWith("net.swimmingtuna.lotm.")) {
                System.out.println("test");
                break;
            }
            if (className.startsWith("net.minecraft.") ||
                    className.startsWith("net.minecraftforge.") ||
                    className.startsWith("java.") ||
                    className.startsWith("sun.")) {
                break;
            }
        }
    }
}