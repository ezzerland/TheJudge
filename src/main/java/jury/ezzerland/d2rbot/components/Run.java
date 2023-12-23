package jury.ezzerland.d2rbot.components;

import jury.ezzerland.d2rbot.Environment;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import static jury.ezzerland.d2rbot.TheJudge.BOT;

public class Run {

    private RunType type = RunType.BAAL;
    private RunMode mode = RunMode.NONLADDER;
    private RunFlag flag = RunFlag.NONE;
    private Member host;
    private Set<Member> members;
    private Integer maxMembers = 8;
    private String gameName = "", password = "";
    private boolean rsvp = false;
    private long lastAction;
    private Timer fiveMinuteReminder, fifteenMinuteReminder;
    private TimerTask fiveMinuteReminderTask, fifteenMinuteReminderTask;

    public Run(Member host) {
        members = new HashSet<>();
        setHost(host);
        addMember(host);
    }

    public void broadcastRun(boolean isNew) {
        getChannel().sendMessageEmbeds(Responses.announceNewRun(getHost().getEffectiveName(), getModeAsString(),getTypeAsString(), isNew, isRsvp())).addActionRow(Responses.joinButton(getHost().getId(), isRsvp()), Responses.getInfoButton(getHost().getId()), Responses.listRunsButton(getHost().getId())).queue();
        updateLastAction();
        if (isNew && isRsvp()) { setUpTimers(); }
    }

    public void publishRun() {
        if (!isFull()) { getChannel().sendMessageEmbeds(Responses.publishRun(this, getHost().getEffectiveName(), getModeAsString(), getTypeAsString())).addActionRow(Responses.joinButton(getHost().getId(), isRsvp()), Responses.getInfoButton(getHost().getId()), Responses.listRunsButton(getHost().getId())).queue(); }
        else { getChannel().sendMessageEmbeds(Responses.publishRun(this, getHost().getEffectiveName(), getModeAsString(), getTypeAsString())).addActionRow(Responses.getInfoButton(getHost().getId()), Responses.listRunsButton(getHost().getId())).queue(); }
        updateLastAction();
        setRsvp(false);
        cancelTimers();
    }

    public TextChannel getChannel() {
        switch (mode) {
            case HCLADDER:
                return BOT.getShardManager().getTextChannelById(Environment.HARDCORE_LADDER_CHANNEL);
            case HCNONLADDER:
                return BOT.getShardManager().getTextChannelById(Environment.HARDCORE_NONLADDER_CHANNEL);
            case LADDER:
                if (getType().equals(RunType.PVP)) { return BOT.getShardManager().getTextChannelById(Environment.PVP_LADDER_CHANNEL); }
                return BOT.getShardManager().getTextChannelById(Environment.LADDER_CHANNEL);
            case NONLADDER:
                if (getType().equals(RunType.PVP)) { return BOT.getShardManager().getTextChannelById(Environment.PVP_NONLADDER_CHANNEL); }
                return BOT.getShardManager().getTextChannelById(Environment.NONLADDER_CHANNEL);
        }
        return BOT.getShardManager().getTextChannelById(Environment.NONLADDER_CHANNEL);
    }
    public String getLadderAsString() {
        if (isLadder()) { return "Ladder"; }
        else { return "Non-Ladder"; }
    }
    public String getHardcoreAsString() {
        if (isHardcore()) { return "Hardcore "; }
        else { return ""; }
    }
    public String getModeAsString() {
        return getHardcoreAsString() + "" + getLadderAsString();
    }
    public String getTypeAsString() {
        if (getsFlagged()) {return getFlagTypeCombined(); }
        return type.getTypeAsString(type);
    }
    public void setHost(Member host) { this.host = host; }
    public Member getHost() { return this.host; }
    public void setType(RunType type) { this.type = type; }
    public RunType getType() { return this.type; }
    public void setFlag(RunFlag flag) {this.flag = flag; }
    //public RunFlag getFlag() { return this.flag; }
    private boolean getsFlagged () {
        switch (type) {
            case CHAOS:
            case BAAL:
                return true;
            default:
                return false;
        }
    }
    private String getFlagTypeCombined() { return flag.getFlagAsString(flag) + "" + type.getTypeAsString(type); }
    public boolean addMember(Member member) {
        if (members.size() >= maxMembers) { return false; }
        members.add(member);
        return true;
    }
    public void removeMember(Member member) {
        members.remove(member);
        BOT.getParticipants().remove(member);
    }
    public void kickAllMembers() {
        for (Member member : members) {
            if (!member.equals(getHost())) {
                BOT.getParticipants().remove(member);
            }
        }
        members.clear();
        members.add(host);
    }
    public Integer getMemberCount() { return members.size(); }
    public Integer getSpotsOpen() { return maxMembers - getMemberCount(); }
    public boolean isFull() {
        if (getMemberCount() >= maxMembers) { return true; }
        return false;
    }
    public String isFullAsString() {
        if (getSpotsOpen() <= 0) { return "is full!"; }
        if (getSpotsOpen() == 1) { return "has " + getSpotsOpen() + " spot available!"; }
        return "has " + getSpotsOpen() + " spots available!";
    }
    public Set<Member> getMembers() { return members; }
    public void setGameName (String gameName) {
        if (renameIsOnCooldown()) { return; }
        this.gameName = gameName;
        updateLastAction();
    }
    public String getGameName() { return gameName; }
    public void setPassword (String password) { this.password = password; }
    public String getPassword() { return password; }
    //public void setLadder(boolean ladder) { this.ladder = ladder; }
    //public boolean isLadder() { return ladder; }
    public void setMode(RunMode mode) {this.mode = mode; }
    public boolean isLadder() {
        if (mode.equals(RunMode.LADDER) || mode.equals(RunMode.HCLADDER)) { return true; }
        return false;
    }
    public boolean isHardcore() {
        if (mode.equals(RunMode.HCNONLADDER) || mode.equals(RunMode.HCLADDER)) { return true; }
        return false;
    }
    public void setRsvp(boolean rsvp) { this.rsvp = rsvp; }
    public boolean isRsvp() { return rsvp; }
    public void endRun() {
        if (isRsvp()) { cancelTimers(); }
        kickAllMembers();
        BOT.getParticipants().remove(host);
        if (isLadder()) {
            BOT.getLadder().get(getType()).remove(this);
        } else {
            BOT.getNonLadder().get(getType()).remove(this);
        }
    }
    public void updateLastAction() { lastAction = System.nanoTime(); }
    public boolean hasExpired() {
        if (isRsvp()) { return false; }
        if (getType().equals(RunType.PVP) && lastAction() >= 120) { return true; }
        if (!getType().equals(RunType.PVP) && lastAction() >= 60) { return true; }
        return false;
    }
    public long lastAction() { return TimeUnit.NANOSECONDS.toMinutes(System.nanoTime()-lastAction); }
    public boolean renameIsOnCooldown() {
        if (TimeUnit.NANOSECONDS.toSeconds(System.nanoTime()-lastAction) > 15) { return false; }
        return true;
    }

    public String timeTilStart() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime nextHour = now.plusHours(1).truncatedTo(ChronoUnit.HOURS);
        return Duration.between(now, nextHour.minusMinutes(5)).toMinutes() + " Minutes";
    }

    private void setUpTimers() {
        fiveMinuteReminder = new Timer();
        fifteenMinuteReminder = new Timer();
        fiveMinuteReminderTask = new TimerTask() {
            public void run() {
                publishRun();
                if (fiveMinuteReminder != null) { fiveMinuteReminder.cancel(); }
            }
        };
        fifteenMinuteReminderTask = new TimerTask() {
            public void run() {
                if (!isFull()) { broadcastRun(false); }
                if (fifteenMinuteReminder != null) { fifteenMinuteReminder.cancel(); }
            }
        };
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime nextHour = now.plusHours(1).truncatedTo(ChronoUnit.HOURS);
        if (now.plusMinutes(15).isBefore(nextHour)) {
            fifteenMinuteReminder.schedule(fifteenMinuteReminderTask, Duration.between(now, nextHour.minusMinutes(15)).toMillis());
        }
        if (now.plusMinutes(5).isBefore(nextHour)) {
            fiveMinuteReminder.schedule(fiveMinuteReminderTask, Duration.between(now, nextHour.minusMinutes(5)).toMillis());
        }
        else {
            publishRun();
        }
    }
    private void cancelTimers() {
        if (fiveMinuteReminder != null) { fiveMinuteReminder.cancel(); }
        if (fifteenMinuteReminder != null) { fifteenMinuteReminder.cancel(); }
        if (fiveMinuteReminderTask != null) { fiveMinuteReminderTask.cancel(); }
        if (fifteenMinuteReminderTask != null) { fifteenMinuteReminderTask.cancel(); }
    }
}
