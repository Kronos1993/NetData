package com.kronos.netdata.DB;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicInteger;

import com.kronos.netdata.Domain.Historial;
import com.kronos.netdata.R;

/**
 * Created by Marcos Octavio on 19/10/2016.
 */
public class Connection extends OrmLiteSqliteOpenHelper{

    private static final int DATABASE_VERSION=1;

    private static Connection connection=null;
    private static final String DB_NAME="NetDataApp";
    private static final AtomicInteger usageCounter = new AtomicInteger(0);
    // the DAO object we use to access the SimpleData table
    private Dao<Historial, Integer> historialDao = null;

    private RuntimeExceptionDao<Historial, Integer> historialRuntimeExceptionDao= null;

/*********************************************************************************************/
    public Connection(Context context) {
        super(context, DB_NAME, null, DATABASE_VERSION, R.raw.ormlite_config);
    }

    /**
     * Get the connection, possibly constructing it if necessary. For each call to this method, there should be 1 and only 1
     * call to {@link #close()}.
     */
    public static synchronized Connection getConnection(Context context) {
        if (connection == null) {
            connection = new Connection(context);
        }
        usageCounter.incrementAndGet();
        return connection;
    }



    /**
     * This is called when the database is first created. Usually you should call createTable statements here to create
     * the tables that will store your data.
     */
    @Override
    public void onCreate(SQLiteDatabase db, ConnectionSource connectionSource) {

        try {
            Log.i(Connection.class.getName(), "onCreate");
            TableUtils.createTableIfNotExists(connectionSource,Historial.class);

            Dao<Historial,Integer>  historialDao=getHistorialDao();
            long millis = System.currentTimeMillis();
            // create some entries in the onCreate

        } catch (SQLException e) {
            Log.e(Connection.class.getName(), "Can't create database", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * This is called when your application is upgraded and it has a higher version number. This allows you to adjust
     * the various data to match the new version number.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db,ConnectionSource connectionSource,int oldVersion, int newVersion)
    {
      try{
          Log.i(Connection.class.getName(), "onUpgrade");
          TableUtils.dropTable(connectionSource,Historial.class, true);
        // after we drop the old databases, we create the new ones
        onCreate(db, connectionSource);
      } catch (SQLException e) {
        Log.e(Connection.class.getName(), "Can't drop databases", e);
        throw new RuntimeException(e);
      }
    }


    /**
     * Returns the Database Access Object (DAO) for our SimpleData class. It will create it or just give the cached
     * value.
     */
    public Dao<Historial,Integer> getHistorialDao() throws SQLException {
        if (historialDao == null) {
            historialDao= getDao(Historial.class);
        }
        return historialDao;
    }

    /**
     * Returns the RuntimeExceptionDao (Database Access Object) version of a Dao for our Categories class. It will
     * create it or just give the cached value. RuntimeExceptionDao only through RuntimeExceptions.
     */
    public RuntimeExceptionDao<Historial, Integer> gethistorialRuntimeExceptionDao() {
        if (historialRuntimeExceptionDao == null) {
            historialRuntimeExceptionDao = getRuntimeExceptionDao(Historial.class);
        }
        return historialRuntimeExceptionDao;
    }

    /**
     * Close the database connections and clear any cached DAOs.
     */
    @Override
    public void close() {
        super.close();
        historialDao = null;
        historialRuntimeExceptionDao = null;
    }

}
