package com.shweit.expendablebackpacks.util;

import java.util.List;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

/**
 * Converts the plugin's legacy-formatted text to Adventure components.
 */
public final class TextUtil {

    private static final LegacyComponentSerializer SECTION_SERIALIZER =
        LegacyComponentSerializer.legacySection();
    private static final LegacyComponentSerializer AMPERSAND_SERIALIZER =
        LegacyComponentSerializer.legacyAmpersand();

    private TextUtil() {
    }

    /**
     * Convert text using section-sign color codes to an Adventure component.
     *
     * @param text the legacy-formatted text
     * @return the converted component
     */
    public static Component fromLegacy(String text) {
        return SECTION_SERIALIZER.deserialize(text);
    }

    /**
     * Convert multiple lines using section-sign color codes to Adventure components.
     *
     * @param lines the legacy-formatted lines
     * @return the converted components
     */
    public static List<Component> fromLegacy(List<String> lines) {
        return lines.stream().map(TextUtil::fromLegacy).toList();
    }

    /**
     * Convert configurable text using ampersand color codes to an Adventure component.
     *
     * @param text the configurable legacy-formatted text
     * @return the converted component
     */
    public static Component fromLegacyConfig(String text) {
        return AMPERSAND_SERIALIZER.deserialize(text);
    }
}
