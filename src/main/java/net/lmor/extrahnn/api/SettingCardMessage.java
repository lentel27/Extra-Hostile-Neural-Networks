package net.lmor.extrahnn.api;

import net.minecraft.network.chat.Component;

public enum SettingCardMessage {
    SETTINGS_LOADED(Component.translatable("extrahnn.player_message.setting_load")),
    SETTINGS_SAVED(Component.translatable("extrahnn.player_message.setting_save")),
    SETTINGS_CLEARED(Component.translatable("extrahnn.player_message.setting_clear")),
    SETTING_INVALID(Component.translatable("extrahnn.player_message.setting_invalid"));

    final Component translate;

    SettingCardMessage(Component translate) {
        this.translate = translate;
    }

    public Component getTranslate() {
        return translate;
    }
}
