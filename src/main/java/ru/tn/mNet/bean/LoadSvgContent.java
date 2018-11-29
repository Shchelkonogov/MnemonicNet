package ru.tn.mNet.bean;

import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.sql.DataSource;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Stateless бин, который грузит части svg картинки для формирования графа
 */
@Stateless
public class LoadSvgContent {

    private static final String SQL = "select mnemo from dev_mnemo_type where kind = ?";

    @Resource(name = "OracleDataSource", mappedName = "jdbc/OracleDataSource")
    private DataSource ds;

    public String getSvg(String name) {
        try(Connection connect = ds.getConnection();
                PreparedStatement stm = connect.prepareStatement(SQL)) {
            stm.setString(1, name);

            ResultSet res = stm.executeQuery();
            if(res.next()) {
                return new String(res.getBytes(1), Charset.forName("UTF-8"));
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
