package jury.ezzerland.d2rbot.components;

import com.jolbox.bonecp.BoneCP;
import com.jolbox.bonecp.BoneCPConfig;
import jury.ezzerland.d2rbot.Environment;
import net.dv8tion.jda.api.entities.Member;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class MySQL {
    private BoneCP connectionPool;

    public MySQL(String host, String database, String username, String password) throws SQLException {
        BoneCPConfig config = new BoneCPConfig();
        config.setJdbcUrl("jdbc:mysql://" + host + "/" + database);
        config.setUsername(username);
        config.setPassword(password);
        config.setDefaultAutoCommit(false);
        connectionPool = new BoneCP(config);
    }

    public Connection getConnection() {
        try {
            return connectionPool.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void shutdown() {
        if (connectionPool != null) { connectionPool.shutdown(); }
    }

    public static void closeQuietly(AutoCloseable... closeables) {
        for (AutoCloseable closeable : closeables) {
            try {
                if (closeable != null) {
                    closeable.close();
                }
            } catch (Exception ignored) {
            }
        }
    }

    //========== SQL Database Inserts / Entry / Updates
    //===== Run Tracker
    //--- Table -- uuid | host_id | type | mode | flag | rsvp | name
    public void addRun(Member member, Run run) { //Add for a single user (used for join command)
        Connection con = null;
        PreparedStatement ps = null;
        try {
            con = getConnection();
            ps = con.prepareStatement("INSERT IGNORE INTO " + Environment.SQL_RUN_TRACKER_TABLE + " (uuid, host_id, type, mode, flag, rsvp, name) VALUES (?, ?, ?, ?, ?, ?, ?)");
            ps.setString(1, member.getId());
            ps.setString(2, run.getHost().getId());
            ps.setInt(3, run.getType().getNumber());
            ps.setInt(4, run.getMode().getNumber());
            ps.setInt(5, run.getFlag().getNumber());
            ps.setBoolean(6, run.isRsvp());
            ps.setString(7, run.getGameName());
            ps.executeUpdate();
            con.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeQuietly(ps, con);
        }
    }
    public void addRun(Run run) { //Add for all players in run (used for next game command)
        Connection con = null;
        PreparedStatement ps = null;
        try {
            con = getConnection();
            ps = con.prepareStatement("INSERT IGNORE INTO " + Environment.SQL_RUN_TRACKER_TABLE + " (uuid, host_id, type, mode, flag, rsvp, name) VALUES (?, ?, ?, ?, ?, ?, ?)");
            for (Member member : run.getMembers()) {
                ps.setString(1, member.getId());
                ps.setString(2, run.getHost().getId());
                ps.setInt(3, run.getType().getNumber());
                ps.setInt(4, run.getMode().getNumber());
                ps.setInt(5, run.getFlag().getNumber());
                ps.setBoolean(6, run.isRsvp());
                ps.setString(7, run.getGameName());
                ps.addBatch();
            }
            ps.executeBatch();
            con.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeQuietly(ps, con);
        }
    }

    //===== Rating Tracker
    //--- Table -- uuid | rated_id | score
    public void addRating(Member member, Member rated, int score) {
        Connection con = null;
        PreparedStatement ps = null;
        try {
            con = getConnection();
            ps = con.prepareStatement("INSERT IGNORE INTO " + Environment.SQL_RATINGS_TABLE + " (uuid, rated_id, score) VALUES (?, ?, ?)");
            ps.setString(1, member.getId());
            ps.setString(2, rated.getId());
            ps.setInt(3, score);
            ps.executeUpdate();
            con.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeQuietly(ps, con);
        }
    }

    //===== Ranking Tracker
    //--- Table -- uuid | points | host_votes | host_score | leacher_votes | leacher_score
    public void updateRanking(Member member, int score, boolean isHost) { // used for host/leacher ratings
        Connection con = null;
        PreparedStatement ps = null;
        try {
            con = getConnection();
            if (isHost) {
                ps = con.prepareStatement("INSERT INTO " + Environment.SQL_RANKING_TABLE + " (uuid, host_votes, host_score) VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE username=VALUES(username), host_votes=host_votes+VALUES(host_votes), host_score=host_score+VALUES(host_score)");
            } else {
                ps = con.prepareStatement("INSERT INTO " + Environment.SQL_RANKING_TABLE + " (uuid, leacher_votes, leacher_score) VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE username=VALUES(username), leacher_votes=leacher_votes+VALUES(leacher_votes), leacher_score=leacher_score+VALUES(leacher_score)");
            }
            ps.setString(1, member.getId());
            ps.setInt(2, 1);
            ps.setInt(3, score);
            ps.executeUpdate();
            con.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeQuietly(ps, con);
        }
    }
}
