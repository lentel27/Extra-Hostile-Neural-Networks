package net.lmor.extrahnn.config;

import dev.shadowsoffire.placebo.config.Configuration;
import dev.shadowsoffire.placebo.config.Property;
import net.neoforged.fml.loading.FMLPaths;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ExtendedConfig extends Configuration {
    public ExtendedConfig(File file) {
        super(file);
    }

    public ExtendedConfig(String mod_id) {
        this(new File(FMLPaths.CONFIGDIR.get().toFile(), mod_id + ".cfg"));
    }

    public int getInt(String name, String category, int defaultValue, int minValue, int maxValue) {
        Property prop = this.get(category, name, defaultValue);
        prop.setComment("Default: " + defaultValue + "; Range: [" + minValue + " ~ " + maxValue + "]");
        prop.setMinValue(minValue);
        prop.setMaxValue(maxValue);
        return prop.getInt(defaultValue) < minValue ? minValue : Math.min(prop.getInt(defaultValue), maxValue);
    }

    public List<String> getStringList(String name, String category, List<String> defaultValues, String comment) {
        String defaults = toJsonList(defaultValues);
        Property prop = this.get(category, name, defaults);
        prop.setComment(comment + "\nDefault: " + defaults);
        return parseJsonList(prop.getString());
    }

    private static String toJsonList(List<String> list) {
        if (list.isEmpty()) return "[]";
        return "[\"" + String.join("\", \"", list) + "\"]";
    }

    private static List<String> parseJsonList(String value) {
        value = value.trim();
        if (value.equals("[]") || value.isBlank()) return new ArrayList<>();

        value = value.replaceAll("^\\[|]$", "").trim();

        return Arrays.stream(value.split(","))
                .map(s -> s.trim().replaceAll("^\"|\"$", ""))
                .filter(s -> !s.isBlank())
                .collect(Collectors.toList());
    }
}
