package com.badlyac.morefeaturemod.handler.NoHurtShake;

import net.minecraftforge.common.ForgeConfigSpec;

public class NoHurtShakeConfig {
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;

    public static final ForgeConfigSpec.BooleanValue ENABLED;
    public static final ForgeConfigSpec.DoubleValue INTENSITY;

    static {
        BUILDER.push("NoHurtShake");
        ENABLED = BUILDER
                .comment("是否啟用 NoHurtShake（可用熱鍵切換）")
                .define("enabled", true);
        INTENSITY = BUILDER
                .comment("受傷視角晃動強度縮放（0.0 = 完全無晃動；1.0 = 原版）")
                .defineInRange("intensity", 0.25D, 0.0D, 1.0D);
        BUILDER.pop();
        SPEC = BUILDER.build();
    }

    public static boolean enabled() {
        return ENABLED.get();
    }

    public static void setEnabled(boolean v) {
        ENABLED.set(v);
        SPEC.save();
    }

    public static float intensity() {
        return INTENSITY.get().floatValue();
    }

    public static void setIntensity(double v) {
        INTENSITY.set(v);
        SPEC.save();
    }
}