package com.senac.maurelio.consumodeenergia.Model;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.senac.maurelio.consumodeenergia.Dao.MyORMLiteHelper;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by Aluno on 17/05/2016.
 */
@DatabaseTable(tableName = "historico")
public class Historico implements Serializable{

    @DatabaseField (generatedId = true)
    private Integer id;

    @DatabaseField
    private int tempoDeUso;

    @DatabaseField
    private double valorKwh;

    @DatabaseField(foreign = true)
    private Aparelho aparelho;

    public Historico() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public int getTempoDeUso() {
        return tempoDeUso;
    }

    public void setTempoDeUso(int tempoDeUso) {
        this.tempoDeUso = tempoDeUso;
    }

    public double getValorKwh() {
        return valorKwh;
    }

    public void setValorKwh(double valorKwh) {
        this.valorKwh = valorKwh;
    }

    public Aparelho getAparelho() {
        return aparelho;
    }

    public void setAparelho(Aparelho aparelho) {
        this.aparelho = aparelho;
    }

    @Override
    public String toString() {
        return "tempoDeUso=" + tempoDeUso +
                ", valorKwh=" + valorKwh;
    }

    public double getConsumo(int tempoDeUso, double valorKwh){
        return valorKwh*tempoDeUso;
    }


}
