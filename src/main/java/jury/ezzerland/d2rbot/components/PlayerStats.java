package jury.ezzerland.d2rbot.components;

import jury.ezzerland.d2rbot.Environment;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static jury.ezzerland.d2rbot.TheJudge.BOT;

public class PlayerStats {
    private int runsHostedAllTime = 0, runsHostedThisMonth = 0, participatedAllTime = 0, participatedThisMonth = 0, playersHostedAllTime = 0, playersHostedThisMonth = 0;

    public PlayerStats (String uuid) {
        setData(uuid);
    }

    //========== SQL Database GET requests
    private void setData(String uuid) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            con = BOT.getDatabase().getConnection();
            ps = con.prepareStatement("SELECT COUNT(uuid) AS participatedAllTime, " +
                    "(SELECT COUNT(uuid) FROM `" + Environment.SQL_RUN_TRACKER_TABLE + "` WHERE host_id='" + uuid + "' AND uuid='" + uuid + "') AS runsHostedAllTime, " +
                    "(SELECT COUNT(DISTINCT uuid) FROM `" + Environment.SQL_RUN_TRACKER_TABLE + "` WHERE host_id='" + uuid + "') AS playersHostedAllTime, " +
                    "(SELECT COUNT(uuid) FROM `" + Environment.SQL_RUN_TRACKER_TABLE + "` WHERE uuid='" + uuid + "' AND host_id!='" + uuid + "' AND DATE_FORMAT(date, '%Y-%m-01') = DATE_FORMAT(CURRENT_DATE(), '%Y-%m-01')) AS participatedThisMonth, " +
                    "(SELECT COUNT(uuid) FROM `" + Environment.SQL_RUN_TRACKER_TABLE + "` WHERE host_id='" + uuid + "' AND uuid='" + uuid + "' AND DATE_FORMAT(date, '%Y-%m-01') = DATE_FORMAT(CURRENT_DATE(), '%Y-%m-01')) AS runsHostedThisMonth, " +
                    "(SELECT COUNT(DISTINCT uuid) FROM `" + Environment.SQL_RUN_TRACKER_TABLE + "` WHERE host_id='" + uuid + "' AND DATE_FORMAT(date, '%Y-%m-01') = DATE_FORMAT(CURRENT_DATE(), '%Y-%m-01')) AS playersHostedThisMonth " +
                    "FROM `" + Environment.SQL_RUN_TRACKER_TABLE + "` " +
                    "WHERE uuid='" + uuid + "' AND host_id!='" + uuid + "'");
            rs = ps.executeQuery();
            if (rs.next()) {
                this.runsHostedAllTime = rs.getInt("runsHostedAllTime");
                this.runsHostedThisMonth = rs.getInt("runsHostedThisMonth");
                this.participatedAllTime = rs.getInt("participatedAllTime");
                this.participatedThisMonth = rs.getInt("participatedThisMonth");
                this.playersHostedAllTime = rs.getInt("playersHostedAllTime");
                this.playersHostedThisMonth = rs.getInt("playersHostedThisMonth");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            BOT.getDatabase().closeQuietly(ps, rs, con);
        }
    }

    public int getRunsHostedAllTime() { return this.runsHostedAllTime; }
    public int getRunsHostedThisMonth() { return this.runsHostedThisMonth; }
    public int getParticipatedAllTime() { return this.participatedAllTime; }
    public int getParticipatedThisMonth() { return this.participatedThisMonth; }
    public int getPlayersHostedAllTime() { return this.playersHostedAllTime; }
    public int getPlayersHostedThisMonth() { return this.playersHostedThisMonth; }
}
