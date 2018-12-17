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

    private static final String SQL = "select * from table (mnemo.get_net_Mnemo_hist_data(?, " +
            "to_date(?, 'dd.mm.yyyy HH24:mi:ss'), " +
            "to_date(?, 'dd.mm.yyyy HH24:mi:ss')))";

    @Resource(name = "OracleDataSource", mappedName = "jdbc/OracleDataSource")
    private DataSource ds;

    /**
     * Загрузка данных по объекту сети
     * @param objectId id объекта сети типа int
     * @return ObjectParamData в котором хронятся значения параметров объекта
     */
    public ObjectParamData load(int objectId, String startTime, String endTime) {
        try(Connection connect = ds.getConnection();
                PreparedStatement stm = connect.prepareStatement(SQL)) {
            stm.setInt(1, objectId);
            stm.setString(2, startTime);
            stm.setString(3, endTime);

            System.out.println("LoadObjectParamData.load start: " + startTime);
            System.out.println("LoadObjectParamData.load end: " + endTime);

            ResultSet res = stm.executeQuery();
            if(res.next()) {
                return new ObjectParamData(res.getString(1), res.getString(2), res.getString(3), res.getString(4));
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
