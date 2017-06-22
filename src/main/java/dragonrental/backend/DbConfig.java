/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dragonrental.backend;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Clock;
import java.time.ZoneId;
import java.util.Properties;
import javax.sql.DataSource;
import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

/**
 *
 * @author JustMe
 */
public class DbConfig {
    
    private DataSource ds = dataSource();
    
    public DbConfig(){}
    
    public DataSource dataSource() {
            
        Properties conf = new Properties();
        BasicDataSource ds = new BasicDataSource();
        try {
            conf.load(DbConfig.class.getResourceAsStream("/Properties.properties"));
        } catch (IOException ex) {
        //log.error("Loading properties has failed!");
        }
        
        ds.setUrl(conf.getProperty("db.url"));
        ds.setDriverClassName(conf.getProperty("db.driver"));
        ds.setUsername(conf.getProperty("db.user"));
        ds.setPassword(conf.getProperty("db.password"));
        
        DatabaseMetaData metaData;
        ResultSet tables;
        try( Connection connection = ds.getConnection() ) {
            metaData = connection.getMetaData();
            tables = metaData.getTables(null, null, "%", new String[] {"TABLE"});
            
            //checks wheter there is any tables (will not create new tables if it finds ANY table)
            if(!tables.next()) {
                new ResourceDatabasePopulator(
                    new ClassPathResource("schema-javadb.sql"),
                    new ClassPathResource("test-data.sql")
                                            ).execute(ds);
            }
        } catch(SQLException ex) {
            System.out.println("SQL Ex when checking for tables");
            System.out.println(ex.getMessage());
        }
        
        return ds;
    }
    
    public DragonManager dragonManager() {
        DragonManagerImpl dManager = new DragonManagerImpl(Clock.system(ZoneId.systemDefault()));
        dManager.setDataSource(ds);
        return dManager;
    }
        
    public PersonManager personManager() {
        PersonManagerImpl manager = new PersonManagerImpl();
        manager.setDataSource(ds);
        return manager;
    }
    
    public ReservationManager reservationManager() {
        ReservationManagerImpl rManager = new ReservationManagerImpl();
        rManager.setDataSource(ds);
        return rManager;
    }

}
