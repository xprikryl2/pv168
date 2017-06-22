package dragonrental.backend;

/**
 * TODO: create  javadoc
 *
 * @author Zuzana Wolfová
 */
public enum DragonElement {
    WATER, FIRE, WIND, EARTH, MAGIC;
    
    public double getElementCostMult() {
        switch(this) {
            case FIRE: return 1.0;
            case WATER: return 0.9;
            case WIND: return 0.8;
            case EARTH: return 1.1;
            case MAGIC: return 1.3;
        }
        return 1.0;
    }
}
