package com.senac.maurelio.consumodeenergia.Dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.senac.maurelio.consumodeenergia.Model.Aparelho;
import com.senac.maurelio.consumodeenergia.Model.Historico;

import java.sql.SQLException;

/**
 * Created by Aluno on 17/05/2016.
 */
public class MyORMLiteHelper extends OrmLiteSqliteOpenHelper {

    //Nome da base de dados
    private static final String NOME_DO_BANCO = "consumoDataBase.db";

    //Versão da base da base de dados
    private static final int DATABASE_VERSION = 5;

    //Caso você queria ter apenas uma instancia da base de dados.
    private static MyORMLiteHelper mInstance = null;

    private Dao<Aparelho, Integer> aparelhoDao = null;
    private Dao<Historico, Integer> historicoDao = null;


    public MyORMLiteHelper(Context context) {
        super(context, NOME_DO_BANCO, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource) {

        try {
            //Criar tabelas do banco
            TableUtils.createTableIfNotExists(connectionSource, Aparelho.class);
            TableUtils.createTableIfNotExists(connectionSource, Historico.class);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource, int i, int i1) {

        try {
            TableUtils.dropTable(connectionSource, Aparelho.class, true);
            TableUtils.dropTable(connectionSource, Historico.class, true);
            onCreate(sqLiteDatabase, connectionSource);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static MyORMLiteHelper getInstance (Context context){
        if (mInstance == null){
            mInstance = new MyORMLiteHelper(context.getApplicationContext());
        }
        return mInstance;
    }

    //Criar DAO de cada tabela

    public Dao<Aparelho, Integer> getAparelhoDao()throws SQLException {
        if (aparelhoDao == null){
            aparelhoDao = getDao(Aparelho.class);
        }
        return aparelhoDao;
    }

    public Dao<Historico, Integer> getHistoricoDao() throws SQLException{
        if (historicoDao == null){
            historicoDao = getDao(Historico.class);
        }
        return historicoDao;
    }

}
