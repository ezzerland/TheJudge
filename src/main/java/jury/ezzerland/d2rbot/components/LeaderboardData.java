package jury.ezzerland.d2rbot.components;

import jury.ezzerland.d2rbot.Environment;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static jury.ezzerland.d2rbot.TheJudge.BOT;

public class LeaderboardData {
    private int participantsThisMonth, participantsAllTime, hostsThisMonth, hostsAllTime;
    private String topHostAllTime = "N/A",topParticipantAllTime = "N/A",hostWithMostAllTime = "N/A",topHostMonthly = "N/A",topParticipantMonthly = "N/A",hostWithMostMonthly = "N/A";
    private SlashCommandInteractionEvent event;

    public LeaderboardData (SlashCommandInteractionEvent event) {
        this.event = event;
        setParticipationCount();
        setTopData();
    }

    public int getParticipantsThisMonth() { return this.participantsThisMonth; }
    public int getHostsThisMonth() { return this.hostsThisMonth; }
    public int getParticipantsAllTime() { return this.participantsAllTime; }
    public int getHostsAllTime() { return this.hostsAllTime; }
    public String getTopHostAllTime() { return topHostAllTime; }
    public String getHostWithMostAllTime() { return hostWithMostAllTime; }
    public String getTopParticipantAllTime() { return topParticipantAllTime; }
    public String getTopHostMonthly() { return topHostMonthly; }
    public String getHostWithMostMonthly() { return hostWithMostMonthly; }
    public String getTopParticipantMonthly() { return topParticipantMonthly; }

    //========== SQL Database GET requests
    private void setParticipationCount() {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            con = BOT.getDatabase().getConnection();
            ps = con.prepareStatement("SELECT COUNT(DISTINCT uuid) AS total_participants, " +
                            "COUNT(DISTINCT host_id) AS total_hosts, " +
                            "(SELECT COUNT(DISTINCT uuid) FROM `" + Environment.SQL_RUN_TRACKER_TABLE + "` WHERE DATE_FORMAT(date, '%Y-%m-01') = DATE_FORMAT(CURRENT_DATE(), '%Y-%m-01')) AS monthly_participants, " +
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

    private void setTopData() { // I need to eventually refactor this to not be 6 different db queries, but I'll worry about that when/if performance is an issue
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Member member;

        try {
            con = BOT.getDatabase().getConnection();
            //=== Top Host All Time
            ps = con.prepareStatement("SELECT host_id, COUNT(uuid) AS runs " +
                    "FROM `" + Environment.SQL_RUN_TRACKER_TABLE + "` " +
                    "WHERE uuid = host_id " +
                    "GROUP BY host_id " +
                    "ORDER BY runs DESC " +
                    "LIMIT 1");
            rs = ps.executeQuery();
            if (rs.next()) {
                member = this.event.getGuild().getMemberById(rs.getString("host_id"));
                if (member != null) { this.topHostAllTime = Responses.memberName(member) + " (" + rs.getString("runs") + ")"; }
            }
            //== Top Host This Month
            ps = con.prepareStatement("SELECT host_id, COUNT(uuid) AS runs " +
                    "FROM `" + Environment.SQL_RUN_TRACKER_TABLE + "` " +
                    "WHERE uuid = host_id " +
                    "AND DATE_FORMAT(date, '%Y-%m-01') = DATE_FORMAT(CURRENT_DATE(), '%Y-%m-01')" +
                    "GROUP BY host_id " +
                    "ORDER BY runs DESC " +
                    "LIMIT 1");
            rs = ps.executeQuery();
            if (rs.next()) {
                member = this.event.getGuild().getMemberById(rs.getString("host_id"));
                if (member != null) { this.topHostMonthly = Responses.memberName(member) + " (" + rs.getString("runs") + ")"; }
            }
            //=== Host With Most Participants All Time
            ps = con.prepareStatement("SELECT host_id, COUNT(DISTINCT uuid) AS runs " +
                    "FROM `" + Environment.SQL_RUN_TRACKER_TABLE + "` " +
                    "GROUP BY host_id " +
                    "ORDER BY runs DESC " +
                    "LIMIT 1");
            rs = ps.executeQuery();
            if (rs.next()) {
                member = this.event.getGuild().getMemberById(rs.getString("host_id"));
                if (member != null) { this.hostWithMostAllTime = Responses.memberName(member) + " (" + rs.getString("runs") + ")"; }
            }
            //== Host with Most Participants This Month
            ps = con.prepareStatement("SELECT host_id, COUNT(DISTINCT uuid) AS runs " +
                    "FROM `" + Environment.SQL_RUN_TRACKER_TABLE + "` " +
                    "WHERE DATE_FORMAT(date, '%Y-%m-01') = DATE_FORMAT(CURRENT_DATE(), '%Y-%m-01')" +
                    "GROUP BY host_id " +
                    "ORDER BY runs DESC " +
                    "LIMIT 1");
            rs = ps.executeQuery();
            if (rs.next()) {
                member = this.event.getGuild().getMemberById(rs.getString("host_id"));
                if (member != null) { this.hostWithMostMonthly = Responses.memberName(member) + " (" + rs.getString("runs") + ")"; }
            }
            //=== Top Participant All Time
            ps = con.prepareStatement("SELECT uuid, COUNT(uuid) AS runs " +
                    "FROM `" + Environment.SQL_RUN_TRACKER_TABLE + "` " +
                    "WHERE uuid != host_id " +
                    "GROUP BY uuid " +
                    "ORDER BY runs DESC " +
                    "LIMIT 1");
            rs = ps.executeQuery();
            if (rs.next()) {
                member = this.event.getGuild().getMemberById(rs.getString("uuid"));
                if (member != null) { this.topParticipantAllTime = Responses.memberName(member) + " (" + rs.getString("runs") + ")"; }
            }
            //== Top Participant This Month
            ps = con.prepareStatement("SELECT uuid, COUNT(uuid) AS runs " +
                    "FROM `" + Environment.SQL_RUN_TRACKER_TABLE + "` " +
                    "WHERE uuid != host_id " +
                    "AND DATE_FORMAT(date, '%Y-%m-01') = DATE_FORMAT(CURRENT_DATE(), '%Y-%m-01')" +
                    "GROUP BY uuid " +
                    "ORDER BY runs DESC " +
                    "LIMIT 1");
            rs = ps.executeQuery();
            if (rs.next()) {
                member = this.event.getGuild().getMemberById(rs.getString("uuid"));
                if (member != null) { this.topParticipantMonthly = Responses.memberName(member) + " (" + rs.getString("runs") + ")"; }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            BOT.getDatabase().closeQuietly(ps, rs, con);
        }

    }
}
