package com.senac.maurelio.consumodeenergia.Model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;

/**
 * Created by Aluno on 17/05/2016.
 */

@DatabaseTable (tableName = "aparelho")

public class Aparelho implements Serializable{

    @DatabaseField (generatedId = true)
    private int id;

    @DatabaseField (canBeNull = true, width = 100)
    private String nome;

    @DatabaseField (canBeNull = true)
    private String marca;

    @DatabaseField (canBeNull = true)
    private double potencia;

    @ForeignCollectionField(eager = true)
    private Collection<Historico> historicos;

    public Aparelho(int id, String nome, String marca, double potencia, Collection<Historico> historicos) {
        this.id = id;
        this.nome = nome;
        this.marca = marca;
        this.potencia = potencia;
        this.historicos = historicos;
    }

    public Aparelho(String nome, String marca, double potencia, Collection<Historico> historicos) {
        this.nome = nome;
        this.marca = marca;
        this.potencia = potencia;
        this.historicos = historicos;
    }

    public Aparelho() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public double getPotencia() {
        return potencia;
    }

    public void setPotencia(double potencia) {
        this.potencia = potencia;
    }

    public Collection<Historico> getHistoricos() {
        return historicos;
    }

    public void setHistoricos(Collection<Historico> historicos) {
        this.historicos = historicos;
    }

    @Override
    public String toString() {
        return nome + " | marca: " + marca + " | potencia:" + potencia;
    }

}


