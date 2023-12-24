package jury.ezzerland.d2rbot.components;

import com.jolbox.bonecp.BoneCP;
import com.jolbox.bonecp.BoneCPConfig;

import java.sql.Connection;
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
        if (connectionPool != null) {
            connectionPool.shutdown();
        }
    }
}
