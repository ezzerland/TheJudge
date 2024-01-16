package jury.ezzerland.d2rbot.components;

public enum RunType {

    BAAL(0),
    CHAOS(1),
    TERRORZONE(2),
    GRUSH(5),
    CRUSH(6),
    MAGICFIND(3),
    PVP(4);

    private final int number;

    RunType(int number) { this.number = number; }
    public int getNumber() { return number; }
    public String getTypeAsString(RunType type) {
        String asString = "";
        switch (type) {
            case BAAL:
                asString = "Baal Run";
                break;
            case CHAOS:
                asString = "Chaos Run";
                break;
            case TERRORZONE:
                asString = "TZ Run";
                break;
            case PVP:
                asString = "PvP Game";
                break;
            case MAGICFIND:
                asString = "MF Run";
                break;
            case GRUSH:
                asString = "G-RUSH";
                break;
            case CRUSH:
                asString = "C-RUSH";
                break;
            default:
                asString = "???";
                break;
        }
        return asString;
    }
}
