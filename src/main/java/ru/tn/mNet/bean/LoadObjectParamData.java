package ru.tn.mNet.bean;

import ru.tn.mNet.model.ObjectParamData;

import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Stateless бин, который грузит значения параметров объекта сети
 */
@Stateless
public class LoadObjectParamData {

    private static final String SQL = "select * from table (mnemo.get_net_Mnemo_hist_data(?, sysdate, sysdate))";

    @Resource(name = "OracleDataSource", mappedName = "jdbc/OracleDataSource")
    private DataSource ds;

    /**
     * Загрузка данных по объекту сети
     * @param objectId id объекта сети типа int
     * @return ObjectParamData в котором хронятся значения параметров объекта
     */
    public ObjectParamData load(int objectId) {
        try(Connection connect = ds.getConnection();
                PreparedStatement stm = connect.prepareStatement(SQL)) {
            stm.setInt(1, objectId);

            ResultSet res = stm.executeQuery();
            if(res.next()) {
                return new ObjectParamData(res.getString(1), res.getString(2));
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}