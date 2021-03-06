package ru.tn.mNet.bean;

import ru.tn.mNet.model.NetModel;

import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Stateless бин, который грузит данные по графу для формирования svg рисунка
 */
@Stateless
public class LoadNetData {

    private static final String SQL_TYPE_1 = "select n1, n2, n3, n4, n5, rownum from table(mnemo.get_net_mnemo_graph(?)) " +
            "where n5 is not null or n3 = 'Труба' order by rownum desc";
    private static final String SQL_TYPE_0 = "select n1, n2, n3, n4, n5, rownum from table(mnemo.get_net_mnemo_graph(?)) " +
            "order by rownum desc";
    private static final String SQL_ALTER = "alter session set NLS_NUMERIC_CHARACTERS='.,'";

    @Resource(name = "OracleDataSource", mappedName = "jdbc/OracleDataSource")
    private DataSource ds;

    /**
     * Метод возвращает список элементов графа сети
     * @param object id объекта бд ввиде String
     * @return список объектов NetModel представляющий собой элементы сети
     */
    public List<NetModel> loadData(String object, String type) {
        List<NetModel> result = new ArrayList<>();
        try(Connection connect = ds.getConnection();
                PreparedStatement stmAlter = connect.prepareStatement(SQL_ALTER)) {
            String sql;

            switch (type) {
                case "0": {
                    sql = SQL_TYPE_0;
                    break;
                }
                case "1": {
                    sql = SQL_TYPE_1;
                    break;
                }
                default: {
                    sql = SQL_TYPE_0;
                }
            }

            try (PreparedStatement stm = connect.prepareStatement(sql)) {
                stmAlter.executeQuery();

                stm.setString(1, object);

                ResultSet res = stm.executeQuery();
                while(res.next()) {
                    result.add(new NetModel(res.getString(1),
                            res.getString(3),
                            res.getInt(2),
                            res.getInt(5),
                            res.getDouble(4)));
                }

                System.out.println(result);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return result;
    }
}
