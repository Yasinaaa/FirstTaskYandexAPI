package springmvc.db.repositories.impl;

import com.kzn.itis.db.model.User;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by yasina on 27.03.15.
 */
public class UserMapper  implements RowMapper<User> {
        public User mapRow(ResultSet rs, int rowNum) throws SQLException {
            User user = new User();
            user.setId(rs.getInt("id"));
            user.setLastName(rs.getString("lastname"));
            user.setFirstName(rs.getString("firstname"));
            user.setAge(rs.getInt("age"));
            return user;
        }
    }
