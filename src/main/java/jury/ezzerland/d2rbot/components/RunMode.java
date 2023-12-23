package jury.ezzerland.d2rbot.components;

public enum RunMode {
    LADDER(0),
    NONLADDER(1),
    HCLADDER(2),
    HCNONLADDER(3);

    private final int number;

    RunMode(int number) { this.number = number; }
    public int getNumber() { return number; }
    public String getModeAsString(RunMode mode) {
        String asString = "";
        switch (mode) {
            case LADDER:
                asString = "Ladder";
                break;
            case NONLADDER:
                asString = "Non-Ladder";
                break;
            case HCLADDER:
                asString = "Hardcore Ladder";
                break;
            case HCNONLADDER:
                asString = "Hardcore Non-Ladder";
                break;
            default:
                asString = "???";
                break;
        }
        return asString;
    }
}
