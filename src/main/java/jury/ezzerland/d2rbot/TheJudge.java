package jury.ezzerland.d2rbot;

import jury.ezzerland.d2rbot.components.MySQL;
import jury.ezzerland.d2rbot.components.Run;
import jury.ezzerland.d2rbot.components.RunType;
import jury.ezzerland.d2rbot.listeners.ButtonManager;
import jury.ezzerland.d2rbot.listeners.CommandManager;
import jury.ezzerland.d2rbot.listeners.ModalManager;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.api.sharding.ShardManager;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;

import javax.security.auth.login.LoginException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class TheJudge {

    public static TheJudge BOT;
    private final ShardManager shardManager;
    private Map<Member, Run> participants;
    private Map<RunType, Set<Run>> ladder, nonladder, hardcoreladder, hardcorenonladder;
    private Map<Member, Long> participantTimeOut;
    private MySQL database;

    public TheJudge() throws LoginException {
        DefaultShardManagerBuilder builder = DefaultShardManagerBuilder.createDefault(Environment.TOKEN)
                .setStatus(OnlineStatus.ONLINE)
                .setActivity(Activity.playing(Environment.BOT_ACTIVITY))
                .enableIntents(GatewayIntent.GUILD_MEMBERS)
                .setMemberCachePolicy(MemberCachePolicy.ALL)
                .setChunkingFilter(ChunkingFilter.ALL);
        shardManager = builder.build();
        shardManager.addEventListener(new CommandManager(), new ModalManager(), new ButtonManager());

        ladder = new HashMap<>();
        nonladder = new HashMap<>();
        hardcoreladder = new HashMap<>();
        hardcorenonladder = new HashMap<>();
        for (RunType type : RunType.values()) {
            ladder.put(type, new HashSet<>());
            nonladder.put(type, new HashSet<>());
            hardcoreladder.put(type, new HashSet<>());
            hardcorenonladder.put(type, new HashSet<>());
        }
        participants = new HashMap<>();
        participantTimeOut = new HashMap<>();


        Connection con = null;
        Statement st = null;
        try {
            database = new MySQL(Environment.SQL_SERVER, Environment.SQL_DATABASE, Environment.SQL_USERNAME, Environment.SQL_PASSWORD);
            con = database.getConnection();
            st = con.createStatement();
            st.executeUpdate("CREATE TABLE IF NOT EXISTS `" + Environment.SQL_DATABASE + "`.`" + Environment.SQL_RANKING_TABLE + "` (" +
                    " `uuid` VARCHAR(36) NOT NULL ," +
                    " `points` INT DEFAULT 0 ," +
                    " `host_votes` INT DEFAULT 0 ," +
                    " `host_score` INT DEFAULT 0 ," +
                    " `leacher_votes` INT DEFAULT 0 ," +
                    " `leacher_score` INT DEFAULT 0 ," +
                    " PRIMARY KEY (`uuid`))" +
                    "ENGINE = InnoDB;");
            st.executeUpdate("CREATE TABLE IF NOT EXISTS `" + Environment.SQL_DATABASE + "`.`" + Environment.SQL_RUN_TRACKER_TABLE + "` (" +
                    " `id` INT NOT NULL AUTO_INCREMENT ," +
                    " `uuid` VARCHAR(36) NOT NULL ," +
                    " `host_id` VARCHAR(36) NOT NULL ," +
                    " `type` INT DEFAULT 0 ," +
                    " `mode` INT DEFAULT 0 ," +
                    " `flag` INT DEFAULT 0 ," +
                    " `rsvp` BOOLEAN NOT NULL DEFAULT 0 ," +
                    " `game_name` VARCHAR(20) NOT NULL ," +
                    " `current_players` INT DEFAULT 0 ," +
                    " `max_players` INT DEFAULT 0 ," +
                    " `game_description` VARCHAR(300) NOT NULL ," +
                    " `date` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ," +
                    " PRIMARY KEY (`id`))" +
                    "ENGINE = InnoDB;");
            /*st.executeUpdate("CREATE TABLE IF NOT EXISTS `" + Environment.SQL_DATABASE + "`.`" + Environment.SQL_COMMAND_TRACKER_TABLE + "` (" +
                    " `id` INT NOT NULL AUTO_INCREMENT ," +
                    " `uuid` VARCHAR(36) NOT NULL ," +
                    " `host_id` VARCHAR(36) NOT NULL ," +
                    " `type` INT DEFAULT 0 ," +
                    " `mode` INT DEFAULT 0 ," +
                    " `flag` INT DEFAULT 0 ," +
                    " `current_players` INT DEFAULT 0 ," +
                    " `max_players` INT DEFAULT 0 ," +
                    " `rsvp` BOOLEAN NOT NULL DEFAULT 0 ," +
                    " `game_name` VARCHAR(20) NOT NULL ," +
                    " `game_description` VARCHAR(300) NOT NULL ," +
                    " `date` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ," +
                    " PRIMARY KEY (`id`))" +
                    "ENGINE = InnoDB;");*/
            st.executeUpdate("CREATE TABLE IF NOT EXISTS `" + Environment.SQL_DATABASE + "`.`" + Environment.SQL_RATINGS_TABLE + "` (" +
                    " `id` INT NOT NULL AUTO_INCREMENT ," +
                    " `uuid` VARCHAR(36) NOT NULL ," +
                    " `rated_id` VARCHAR(36) NOT NULL ," +
                    " `score` INT DEFAULT 0 ," +
                    " `date` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ," +
                    " PRIMARY KEY (`id`))" +
                    "ENGINE = InnoDB;");
            con.commit();

        } catch (SQLException e) {
            e.printStackTrace();
            return;
        } finally {
            database.closeQuietly(st, con);
        }
    }

    public static void main(String[] args) {
        try {
            BOT = new TheJudge();
        } catch (LoginException e) {
            System.out.println("ERROR: Login Token is Invalid!");
        }
    }

    public ShardManager getShardManager() { return shardManager; }
    public MySQL getDatabase() { return database; }
    public Map<Member, Run> getParticipants() { return participants; }
    public Map<RunType, Set<Run>> getHCLadder() { return hardcoreladder; }
    public Map<RunType, Set<Run>> getLadder() { return ladder; }
    public Map<RunType, Set<Run>> getHCNonLadder() { return hardcorenonladder; }
    public Map<RunType, Set<Run>> getNonLadder() { return nonladder; }
    public Map<Member, Long> getParticipantTimeOut() { return participantTimeOut; }
    public boolean isOnTimeOut(Member member) {
        if (getParticipantTimeOut().containsKey(member)) {
            if (getParticipantTimeOut().get(member) > System.nanoTime()) { return true; }
            getParticipantTimeOut().remove(member);
        }
        return false;
    }
    public String timeOutRemaining(Member member) {
        return TimeUnit.NANOSECONDS.toMinutes(getParticipantTimeOut().get(member)-System.nanoTime()) + " Minutes";
    }

    public void cleanseRuns() {
        if (getParticipants().size() == 0) { return; }
        Set<Run> cleanse = new HashSet<>();
        for (RunType type : RunType.values()) {
            for (Run run : getLadder().get(type)) {
                if (run.hasExpired()) { cleanse.add(run); }
            }
            for (Run run : getNonLadder().get(type)) {
                if (run.hasExpired()) { cleanse.add(run); }
            }
            for (Run run : getHCLadder().get(type)) {
                if (run.hasExpired()) { cleanse.add(run); }
            }
            for (Run run : getHCNonLadder().get(type)) {
                if (run.hasExpired()) { cleanse.add(run); }
            }
        }
        for (Run run : cleanse) { run.endRun(); }
    }
}
