package jury.ezzerland.d2rbot.components;

public enum RunFlag {
    NONE(0),
    FULLCLEAR(1),
    PRETELE(2),
    SEALPOP(3),
    ELITEHUNT(4),
    BOSSKILL(5);

    private final int number;

    RunFlag(int number) { this.number = number; }
    public int getNumber() { return number; }

    public String getFlagAsString(RunFlag flag) {
        String asString = "";
        switch (flag) {
            case FULLCLEAR:
                asString = "Full-Clear ";
                break;
            case SEALPOP:
                asString = "Seal Pop ";
                break;
            case PRETELE:
                asString = "Pre-Tele ";
                break;
            case ELITEHUNT:
                asString = "Elite Hunt ";
                break;
            case BOSSKILL:
                asString = "Boss Kill ";
                break;
            default:
                asString = "";
                break;
        }
        return asString;
    }
}
