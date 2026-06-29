package net.lmor.extrahnn.data;

import net.lmor.extrahnn.ExtraHostileConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

public enum ExtraModelTier implements StringRepresentable {
    AUTONOMOUS("autonomous", 0, ExtraModelTier.getKillData(0), ChatFormatting.AQUA, 0.1F),
    INTELLIGENT("intelligent", ExtraModelTier.getReqData(0), ExtraModelTier.getKillData(1), ChatFormatting.DARK_AQUA, 0.3F),
    ADAPTIVE("adaptive", ExtraModelTier.getReqData(1), ExtraModelTier.getKillData(2), ChatFormatting.RED, 0.5F),
    SYNTHETIC("synthetic", ExtraModelTier.getReqData(2), ExtraModelTier.getKillData(3), ChatFormatting.DARK_RED, 0.74F),
    OMNIPOTENT("omnipotent", ExtraModelTier.getReqData(3), 0, ChatFormatting.DARK_PURPLE, 1F);

    private static final ExtraModelTier[] VALUES = values();
    public final String name;
    private TierData tierData;
    private final int requiredData;

    ExtraModelTier(String name, int requiredData, int dataPerKill, ChatFormatting color, float accuracy) {
        this(name, requiredData, dataPerKill, TextColor.fromLegacyFormat(color), accuracy, true);
    }

    ExtraModelTier(String name, int requiredData, int dataPerKill, TextColor color, float accuracy, boolean canSim) {
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
        return Arrays.stream(VALUES).mapToInt((t) -> t.data().requiredData).toArray();
    }

    public static int[] defaultDataPerKill() {
        return Arrays.stream(VALUES).mapToInt((t) -> t.data().dataPerKill).toArray();
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

    static int getReqData(int index){
        List<Integer> data = ExtraHostileConfig.requiredDataModel;
        if (index < 0 || index >= data.size()) return 1;
        return ExtraHostileConfig.requiredDataModel.get(index);
    }

    static int getKillData(int index){
        List<Integer> data = ExtraHostileConfig.dataPerKillModel;
        if (index < 0 || index >= data.size()) return 1;
        return ExtraHostileConfig.dataPerKillModel.get(index);
    }

    public static record TierData(int requiredData, int dataPerKill, TextColor color, float accuracy, boolean canSim){}
}
