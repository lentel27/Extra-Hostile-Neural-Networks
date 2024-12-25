package net.lmor.extrahnn.data;

import com.mojang.serialization.Codec;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.util.StringRepresentable;

import java.util.Arrays;

public enum ExtraModelTier implements StringRepresentable {
    AUTONOMOUS("autonomous", 0, 20, ChatFormatting.AQUA, 0.1F),
    INTELLIGENT("intelligent", 1500, 30, ChatFormatting.DARK_AQUA, 0.3F),
    ADAPTIVE("adaptive", 4500, 50, ChatFormatting.RED, 0.5F),
    SYNTHETIC("synthetic", 10750, 75, ChatFormatting.DARK_RED, 0.74F),
    OMNIPOTENT("omnipotent", 22375, 0, ChatFormatting.DARK_PURPLE, 1F);

    private static final ExtraModelTier[] VALUES = values();
    public final String name;
    private TierData tierData;
    private int requiredData;
    public static final Codec<ExtraModelTier> CODEC = StringRepresentable.fromEnum(() -> {
        return VALUES;
    });

    private ExtraModelTier(String name, int requiredData, int dataPerKill, ChatFormatting color, float accuracy) {
        this(name, requiredData, dataPerKill, TextColor.fromLegacyFormat(color), accuracy);
    }

    private ExtraModelTier(String name, int requiredData, int dataPerKill, TextColor color, float accuracy) {
        this.name = name;
        this.tierData = new TierData(requiredData, dataPerKill, color, accuracy);
        this.requiredData = requiredData;
    }

    public ExtraModelTier previous() {
        return this == AUTONOMOUS ? this : VALUES[this.ordinal() - 1];
    }

    public ExtraModelTier next() {
        return this == OMNIPOTENT ? this : VALUES[this.ordinal() + 1];
    }

    public Component getComponent() {
        return Component.translatable("extrahnn.tier." + this.name).withStyle(Style.EMPTY.withColor(this.data().color));
    }

    public static ExtraModelTier getByData(int data){
        for(int i = 4; i >= 0; --i) {
            if (data >= VALUES[i].requiredData){
                return VALUES[i];
            }
        }
        return AUTONOMOUS;
    }

    public static int[] defaultData() {
        return Arrays.stream(VALUES).mapToInt((t) -> {
            return t.data().requiredData;
        }).toArray();
    }

    public static int[] defaultDataPerKill() {
        return Arrays.stream(VALUES).mapToInt((t) -> {
            return t.data().dataPerKill;
        }).toArray();
    }

    public String getSerializedName() {
        return this.name;
    }

    public TierData data() {
        return this.tierData;
    }

    public int color() {
        return this.data().color.getValue();
    }

    public float accuracy() {
        return this.data().accuracy;
    }

    void updateData(TierData data) {
        this.tierData = data;
    }

    public static record TierData(int requiredData, int dataPerKill, TextColor color, float accuracy){
        public TierData(int requiredData, int dataPerKill, TextColor color, float accuracy) {
            this.requiredData = requiredData;
            this.dataPerKill = dataPerKill;
            this.color = color;
            this.accuracy = accuracy;
        }

        public int requiredData() {
            return this.requiredData;
        }

        public int dataPerKill() {
            return this.dataPerKill;
        }

        public TextColor color() {
            return this.color;
        }

        public float accuracy() {
            return this.accuracy;
        }
    }
}
