package springmvc.db.dao.manager;

import com.springapp.mvc.config.DatabaseConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Created by boro on 23.03.15.
 */
@Repository
public class OperationManager {
    @Autowired
    private Connection connection;

    @Autowired
    private DatabaseConfig config;

    public Connection setConnection() throws IOException, SQLException, ClassNotFoundException {
        if (connection.isClosed()){
            connection = DriverManager.getConnection(config.getDbUrl(),"postgres","yasina");
        }
        return connection;
    }

    public void closeConnection() throws SQLException {
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
