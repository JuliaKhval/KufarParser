package org.example.kufarparser;

import java.util.Arrays;

public enum City {
    GRODNO("grodno", "Гродно"),
    MINSK("minsk", "Минск"),
    BREST("brest", "Брест"),
    VITEBSK("vitebsk", "Витебск"),
    GOMEL("gomel", "Гомель"),
    MOGILEV("mogilev", "Могилёв");

    private final String urlKey;
    private final String displayName;

    City(String urlKey, String displayName) {
        this.urlKey = urlKey;
        this.displayName = displayName;
    }

    public static String fromDisplayName(String name) {
        for (City city : values()) {
            if (city.displayName.equalsIgnoreCase(name)) {
                return city.urlKey;
            }
        }
        throw new IllegalArgumentException("Неизвестный город: " + name);
    }


}