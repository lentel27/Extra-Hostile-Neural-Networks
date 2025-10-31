package net.lmor.extrahnn.data;

import com.mojang.serialization.Codec;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

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
    private final int requiredData;
    public static final Codec<ExtraModelTier> CODEC = StringRepresentable.fromEnum(() -> VALUES);

    private ExtraModelTier(String name, int requiredData, int dataPerKill, ChatFormatting color, float accuracy) {
        this(name, requiredData, dataPerKill, TextColor.fromLegacyFormat(color), accuracy, true);
    }

    private ExtraModelTier(String name, int requiredData, int dataPerKill, TextColor color, float accuracy, boolean canSim) {
        this.name = name;
        this.tierData = new TierData(requiredData, dataPerKill, color, accuracy, canSim);
        this.requiredData = requiredData;
    }

    public ExtraModelTier previous() {
        return this == AUTONOMOUS ? this : VALUES[this.ordinal() - 1];
    }

    public ExtraModelTier next() {
        return this == OMNIPOTENT ? this : VALUES[this.ordinal() + 1];
    }

    public static ExtraModelTier next(ExtraModelTier tier) {
        return tier.next();
    }

    public boolean isMax(){
        return this == OMNIPOTENT;
    }

    public boolean isMin(){
        return this == AUTONOMOUS;
    }

    public static ExtraModelTier getMaxTier() {
        return OMNIPOTENT;
    }

    public static ExtraModelTier getMinTier() {
        return AUTONOMOUS;
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

    public @NotNull String getSerializedName() {
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

    public static record TierData(int requiredData, int dataPerKill, TextColor color, float accuracy, boolean canSim){}
}
