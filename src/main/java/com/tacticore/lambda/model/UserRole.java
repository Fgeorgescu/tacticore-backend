package com.tacticore.lambda.model;

public enum UserRole {
    ENTRY_FRAGGER("Entry Fragger"),
    SNIPER("Francotirador"),
    IGL("Líder en el juego"),
    OBSERVER("Observador"),
    SUPPORT("Soporte"),
    ANCHOR("Ancla");
    
    private final String displayName;
    
    UserRole(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public static UserRole fromDisplayName(String displayName) {
        for (UserRole role : values()) {
            if (role.displayName.equals(displayName)) {
                return role;
            }
        }
        throw new IllegalArgumentException("Unknown role: " + displayName);
    }
    
    public static String[] getAllDisplayNames() {
        UserRole[] roles = values();
        String[] displayNames = new String[roles.length];
        for (int i = 0; i < roles.length; i++) {
            displayNames[i] = roles[i].displayName;
        }
        return displayNames;
    }
}
