package springmvc.db.repositories.impl;

import com.kzn.itis.db.config.DatabaseConfiguration;
import com.kzn.itis.db.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by yasina on 13.03.15.
 */
@Repository
public class UserRepositoryImpl  implements UserRepository{

    private static final Logger logger = LoggerFactory.getLogger(UserRepositoryImpl.class);

    private NamedParameterJdbcTemplate namedJDBCTemplate;

    @Autowired
    public void setNamedParameterJdbcTemplate(DataSource dataSource) {
        this.namedJDBCTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

    @Autowired
    public UserRepositoryImpl() {
    }

    public UserRepositoryImpl (DataSource dataSource) {
        //setNamedParameterJdbcTemplate(dataSource);
    }

    @Override
    public void add(User user) {

        String sql = "INSERT INTO USERS(lastname,firstname,age) VALUES (:lastname,:firstname,:age)";
        Map parametrs = new HashMap();
        parametrs.put("lastname", user.getLastName());
        parametrs.put("firstname", user.getFirstName());
        parametrs.put("age", user.getAge());
        namedJDBCTemplate.update(sql,parametrs);

    }

    @Override
    public void remove() {
        int num = countOfUsers();
        String sql = "DELETE FROM USERS WHERE id=:id";
        SqlParameterSource namedParameters = new MapSqlParameterSource("id", Integer.valueOf(num));
        namedJDBCTemplate.update(sql, namedParameters);
    }

    @Override
    public void update(String newUser, int age) {
        int num = countOfUsers();
        String sql = "UPDATE USERS SET firstname=:firstname ,age=:age WHERE id=:id";
        MapSqlParameterSource parametrs = new MapSqlParameterSource();
        parametrs.addValue("firstname",newUser);
        parametrs.addValue("age", age);
        parametrs.addValue("id", num);
        namedJDBCTemplate.update(sql,parametrs);

    }

    @Override
    public List<User> getAllUsers() {
        String sqlQuery = "SELECT * FROM USERS";
        List<User> users = namedJDBCTemplate.query(sqlQuery, new UserMapper());
        return users;
    }

    @Override
    public int countOfUsers() {
        int count = 0;
        String sqlQuery = "SELECT COUNT(*) FROM USERS";
        count = namedJDBCTemplate.getJdbcOperations().queryForInt(sqlQuery);
        return count;
    }
}


