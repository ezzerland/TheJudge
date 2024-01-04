package jury.ezzerland.d2rbot.components;

import jury.ezzerland.d2rbot.Environment;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static jury.ezzerland.d2rbot.TheJudge.BOT;

public class LeaderboardData {
    private int participantsThisMonth, participantsAllTime, hostsThisMonth, hostsAllTime;
    //ResultSet participants;

    public LeaderboardData () {
        setParticipationCount();
    }

    public int getParticipantsThisMonth() { return this.participantsThisMonth; }
    public int getHostsThisMonth() { return this.hostsThisMonth; }
    public int getParticipantsAllTime() { return this.participantsAllTime; }
    public int getHostsAllTime() { return this.hostsAllTime; }
    //public ResultSet getParticipants() { return this.participants; }

    //========== SQL Database GET requests
    private void setParticipationCount() {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            con = BOT.getDatabase().getConnection();
            ps = con.prepareStatement("SELECT COUNT(DISTINCT uuid) AS total_participants," +
                            "COUNT(DISTINCT host_id) AS total_hosts," +
                            "(SELECT COUNT(DISTINCT uuid) FROM `" + Environment.SQL_RUN_TRACKER_TABLE + "` WHERE DATE_FORMAT(date, '%Y-%m-01') = DATE_FORMAT(CURRENT_DATE(), '%Y-%m-01')) AS monthly_participants," +
                            "(SELECT COUNT(DISTINCT host_id) FROM `" + Environment.SQL_RUN_TRACKER_TABLE + "` WHERE DATE_FORMAT(date, '%Y-%m-01') = DATE_FORMAT(CURRENT_DATE(), '%Y-%m-01')) AS monthly_hosts " +
                            "FROM `" + Environment.SQL_RUN_TRACKER_TABLE + "`");
            rs = ps.executeQuery();
            if (rs.next()) {
                this.participantsAllTime = rs.getInt("total_participants");
                this.participantsThisMonth = rs.getInt("monthly_participants");
                this.hostsAllTime = rs.getInt("total_hosts");
                this.hostsThisMonth = rs.getInt("monthly_hosts");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            BOT.getDatabase().closeQuietly(ps, rs, con);
        }

    }
}
