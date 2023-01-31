package cz.stepes.ancientcitiescontrol.obj;

public class GenerationSettings {

    private final int spacing;
    private final int separation;
    private final String spreadType;
    private final int salt;

    public GenerationSettings(int spacing, int separation, String spreadType, int salt) {
        this.spacing = spacing;
        this.separation = separation;
        this.spreadType = spreadType;
        this.salt = salt;
    }

    public int getSpacing() {
        return spacing;
    }

    public int getSeparation() {
        return separation;
    }

    public String getSpreadType() {
        return spreadType;
    }

    public int getSalt() {
        return salt;
    }
}
