package net.swimmingtuna.lotm.util.ClientData;

public class ClientColorOverlayData {
    public enum OverlayColor {
        NONE(0, 0, 0, 0),
        GRAY(128, 128, 128, 255),
        BLACK(0, 0, 0, 255),
        RED(255, 0, 0, 255),
        BLUE(0, 0, 255, 255),
        YELLOW(255, 255, 0, 255),
        PURPLE(128, 0, 128, 255),
        GREEN(0, 255, 0, 255),
        ORANGE(255, 165, 0, 255),
        AQUA(0, 255, 255, 255),
        MAGENTA(255, 0, 255, 255),
        PINK(255, 192, 203, 255);

        private final int red;
        private final int green;
        private final int blue;
        private final int alpha;

        OverlayColor(int r, int g, int b, int a) {
            this.red = r;
            this.green = g;
            this.blue = b;
            this.alpha = a;
        }

        public int getRed() { return red; }
        public int getGreen() { return green; }
        public int getBlue() { return blue; }
        public int getAlpha() { return alpha; }

        public float getRedNormalized() { return red / 255.0f; }
        public float getGreenNormalized() { return green / 255.0f; }
        public float getBlueNormalized() { return blue / 255.0f; }
        public float getAlphaNormalized() { return alpha / 255.0f; }
    }

    private static OverlayColor currentColor = OverlayColor.NONE;
    private static int duration = 0;
    private static int maxDuration = 0;
    private static float intensityMultiplier = 1.0f;

    public static void setColorOverlay(OverlayColor color, int dur, float intensity) {
        currentColor = color;
        duration = dur;
        maxDuration = dur;
        intensityMultiplier = Math.max(0.0f, Math.min(1.0f, intensity));
    }

    public static void setColorOverlay(OverlayColor color, int dur) {
        setColorOverlay(color, dur, 1.0f);
    }

    public static void clearColorOverlay() {
        currentColor = OverlayColor.NONE;
        duration = 0;
        maxDuration = 0;
        intensityMultiplier = 1.0f;
    }

    public static OverlayColor getCurrentColor() {
        return currentColor;
    }

    public static int getDuration() {
        return duration;
    }

    public static int getMaxDuration() {
        return maxDuration;
    }

    public static float getIntensityMultiplier() {
        return intensityMultiplier;
    }

    public static float getRedNormalized() {
        return currentColor.getRedNormalized();
    }

    public static float getGreenNormalized() {
        return currentColor.getGreenNormalized();
    }

    public static float getBlueNormalized() {
        return currentColor.getBlueNormalized();
    }

    public static float getAlphaNormalized() {
        if (duration <= 0 || maxDuration <= 0 || currentColor == OverlayColor.NONE) {
            return 0.0f;
        }
        float fadeProgress = (float) duration / maxDuration;
        return currentColor.getAlphaNormalized() * fadeProgress * intensityMultiplier;
    }

    public static void decrementDuration() {
        if (duration > 0) {
            duration--;
        }
        if (duration <= 0) {
            currentColor = OverlayColor.NONE;
        }
    }

    public static boolean isActive() {
        return duration > 0 && currentColor != OverlayColor.NONE;
    }

    // Utility method to get color by ordinal (useful for networking)
    public static OverlayColor getColorByOrdinal(int ordinal) {
        OverlayColor[] colors = OverlayColor.values();
        if (ordinal >= 0 && ordinal < colors.length) {
            return colors[ordinal];
        }
        return OverlayColor.NONE;
    }
}
