package model;// nura

/**
 * Enum representing the different types of parking spots as per Requirement #1.
 * Each type has a specific hourly rate:
 * - Compact: RM 2/hour (for motorcycles, bicycles)
 * - Regular: RM 5/hour (for regular cars)
 * - Handicapped: RM 2/hour (FREE if handicapped card holder)
 * - Reserved: RM 10/hour (for VIP customers)
 */
public enum SpotType {
    COMPACT("Compact", 2.0),
    REGULAR("Regular", 5.0),
    HANDICAPPED("Handicapped", 2.0),
    RESERVED("Reserved", 10.0);
    
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
    
    /**
     * Gets the display name of this spot type
     * @return Display name (e.g., "Compact")
     */
    public String getDisplayName() {
        return displayName;
    }
    
    /**
     * Gets the hourly rate for this spot type
     * @return Hourly rate in RM
     */
    public double getRate() {
        return hourlyRate;
    }
    
    /**
     * Converts a string to SpotType enum
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