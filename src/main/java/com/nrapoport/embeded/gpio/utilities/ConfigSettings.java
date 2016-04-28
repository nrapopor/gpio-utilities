package com.nrapoport.embeded.gpio.utilities;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class ConfigSettings {
    private static final String BUNDLE_NAME = "config"; //$NON-NLS-1$

    private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME);

    private ConfigSettings() {
    }

    public static String getString(String key) {
        try {
            return RESOURCE_BUNDLE.getString(key);
        } catch (MissingResourceException e) {
            return '!' + key + '!';
        }
    }
}
