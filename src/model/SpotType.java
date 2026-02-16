package model;

/**
 * Enum representing the different types of parking spots.
 * Includes standard types and future-proof types like Electric Charging.
 */
public enum SpotType {
    COMPACT("Compact", 2.0),
    REGULAR("Regular", 5.0),
    HANDICAPPED("Handicapped", 2.0),
    RESERVED("Reserved", 10.0),
    ELECTRIC("Electric Charging", 8.0); // Added for Future-Proofing
    
    private final String displayName;
    private final double hourlyRate;
    
    /**
     * Constructor for SpotType
     * @param displayName Human-readable name
     * @param hourlyRate Rate per hour in RM
     */
    SpotType(String displayName, double hourlyRate) {
        this.displayName = displayName;
        this.hourlyRate = hourlyRate;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public double getRate() {
        return hourlyRate;
    }
    
    /**
     * Converts a string to SpotType enum.
     * Useful for parsing dropdown selections from the UI.
     * @param typeStr String representation (e.g., "Compact")
     * @return Corresponding SpotType, or null if not found
     */
    public static SpotType fromString(String typeStr) {
        for (SpotType type : SpotType.values()) {
            if (type.displayName.equalsIgnoreCase(typeStr)) {
                return type;
            }
        }
        return null;
    }
}