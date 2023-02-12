package jury.ezzerland.d2rbot.components;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static jury.ezzerland.d2rbot.TheJudge.BOT;

public class Run {

    private RunType type = RunType.BAAL;
    private Member host;
    private Set<Member> members;
    private Integer maxMembers = 8;
    private String gameName = "", password = "";
    private boolean ladder = false;
    private long lastAction;

    public Run(Member host) {
        members = new HashSet<>();
        setHost(host);
        addMember(host);
    }

    public void broadcastRun(boolean isNew) {
        getChannel().sendMessageEmbeds(Responses.announceNewRun(getHost().getEffectiveName(), getLadderAsString(),getTypeAsString(), isNew)).addActionRow(Responses.joinButton(getHost().getId())).queue();
        updateLastAction();
    }

    public TextChannel getChannel() {
        if (ladder) {
            if (getType().equals(RunType.PVP)) { return BOT.getShardManager().getTextChannelById(BOT.getConfig().get("PVP_LADDER_CHANNEL")); }
            return BOT.getShardManager().getTextChannelById(BOT.getConfig().get("LADDER_CHANNEL"));
        } else {
            if (getType().equals(RunType.PVP)) { return BOT.getShardManager().getTextChannelById(BOT.getConfig().get("PVP_NONLADDER_CHANNEL")); }
            return BOT.getShardManager().getTextChannelById(BOT.getConfig().get("NONLADDER_CHANNEL"));
        }
    }
    public String getLadderAsString() {
        if (ladder) { return "Ladder"; }
        else { return "Non-Ladder"; }
    }
    public String getTypeAsString() { return type.getTypeAsString(type); }
    public void setHost(Member host) { this.host = host; }
    public Member getHost() { return this.host; }
    public void setType(RunType type) { this.type = type; }
    public RunType getType() { return this.type; }
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
    public boolean isFull() {
        if (getMemberCount() >= maxMembers) { return true; }
        return false;
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
    public void setLadder(boolean ladder) { this.ladder = ladder; }
    public boolean isLadder() { return ladder; }
    public void endRun() {
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
        if (getType().equals(RunType.PVP) && lastAction() >= 120) { return true; }
        if (!getType().equals(RunType.PVP) && lastAction() >= 60) { return true; }
        return false;
    }
    public long lastAction() { return TimeUnit.NANOSECONDS.toMinutes(System.nanoTime()-lastAction); }
    public boolean renameIsOnCooldown() {
        if (TimeUnit.NANOSECONDS.toSeconds(System.nanoTime()-lastAction) > 30) { return false; }
        return true;
    }
}
