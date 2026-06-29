package net.lmor.extrahnn.config;

import dev.shadowsoffire.placebo.config.Configuration;
import dev.shadowsoffire.placebo.config.Property;
import net.neoforged.fml.loading.FMLPaths;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
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
        return getList(name, category, defaultValues, comment, s -> s, s -> s, true);
    }

    public List<Integer> getIntList(String name, String category, List<Integer> defaultValues, String comment) {
        return getList(name, category, defaultValues, comment, String::valueOf, Integer::parseInt, false);
    }

    private <T> List<T> getList(String name, String category, List<T> defaultValues, String comment,
                                Function<T, String> serializer, Function<String, T> parser, boolean quoted) {
        String defaults = toJsonListGeneric(defaultValues, serializer, quoted);
        Property prop = this.get(category, name, defaults);
        prop.setComment(comment + "\nDefault: " + defaults);
        return parseJsonListGeneric(prop.getString(), parser, quoted);
    }

    private static <T> String toJsonListGeneric(List<T> list, Function<T, String> serializer, boolean quoted) {
        if (list.isEmpty()) return "[]";
        String q = quoted ? "\"" : "";
        return "[" + list.stream()
                .map(v -> q + serializer.apply(v) + q)
                .collect(Collectors.joining(", ")) + "]";
    }

    private static <T> List<T> parseJsonListGeneric(String value, Function<String, T> parser, boolean quoted) {
        value = value.trim();
        if (value.equals("[]") || value.isBlank()) return new ArrayList<>();
        value = value.replaceAll("^\\[|]$", "").trim();
        return Arrays.stream(value.split(","))
                .map(String::trim)
                .map(s -> quoted ? s.replaceAll("^\"|\"$", "") : s)
                .filter(s -> !s.isBlank())
                .map(parser)
                .collect(Collectors.toList());
    }
}
