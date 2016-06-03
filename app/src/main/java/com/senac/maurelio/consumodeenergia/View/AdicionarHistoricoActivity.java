package com.senac.maurelio.consumodeenergia.View;

import android.app.AlertDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.j256.ormlite.dao.Dao;
import com.senac.maurelio.consumodeenergia.Dao.MyORMLiteHelper;
import com.senac.maurelio.consumodeenergia.Model.Aparelho;
import com.senac.maurelio.consumodeenergia.Model.Historico;
import com.senac.maurelio.consumodeenergia.R;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;

public class AdicionarHistoricoActivity extends AppCompatActivity {

    public Historico historico;

    private Spinner spinnerAparelho;
    private EditText editTextTempo, editTextValor;
    private ArrayAdapter<Aparelho> adapterAparelhos;
    private Aparelho aparelhoIntent, aparelhoSelecionado, a;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adicionar_historico);

        //recuperar elementos do layout
        editTextTempo = (EditText) findViewById(R.id.editTextTempo);
        spinnerAparelho = (Spinner) findViewById(R.id.spinnerAparelhos);
        editTextValor = (EditText) findViewById(R.id.editTextValorKwh);

        aparelhoIntent = new Aparelho();

        //receber Intent
        Bundle p = getIntent().getExtras();
        if (p!=null){
            aparelhoIntent = (Aparelho) p.getSerializable("paparelho");
        }

        //Popular Spinner de Aparelhos com aparelhos do banco
        aparelhoSelecionado = new Aparelho();
        Dao<Aparelho, Integer> aparelhoDao = null;
        try {
            aparelhoDao = MyORMLiteHelper.getInstance(this).getAparelhoDao();
            ArrayList<Aparelho> listaDeAparelhos = (ArrayList<Aparelho>) aparelhoDao.queryForAll();
            if (p!=null) {
                //Procurar aparelho vindo na Intent
                Iterator<Aparelho> i = listaDeAparelhos.iterator();
                while (i.hasNext()) {
                    a = new Aparelho();
                    a = i.next();
                    if (a.getId() == aparelhoIntent.getId()) {
                        aparelhoSelecionado = a;
                    }
                }
            }
            //Spinner
            this.adapterAparelhos = new ArrayAdapter<Aparelho>(this, android.R.layout.simple_spinner_dropdown_item,listaDeAparelhos);
            spinnerAparelho.setAdapter(adapterAparelhos);
            spinnerAparelho.setSelection(adapterAparelhos.getPosition(aparelhoSelecionado));
        } catch (SQLException e) {
            e.printStackTrace();
        }

        //Verificar se já existe valor do KW/h cadastrado no histórico
        Dao<Historico, Integer> historicoDao = null;
        try {
            historicoDao = MyORMLiteHelper.getInstance(this).getHistoricoDao();
            ArrayList<Historico> listaDeHistoricos = (ArrayList<Historico>) historicoDao.queryForAll();
            Iterator<Historico> iterator = listaDeHistoricos.iterator();
            if(iterator.hasNext()) {
                historico = new Historico();
                    historico = iterator.next();
                    editTextValor.setText(String.valueOf(historico.getValorKwh()));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void adicionarHistorico(View v){

        //Instanciar objeto Aparelho para pegar o aparelho selecionado
        Aparelho aparelhoSelecionado = (Aparelho) spinnerAparelho.getSelectedItem();

        //Instanciar Histórico para gravar no banco
        Historico historico = new Historico();

        if(editTextTempo.getText().toString().equalsIgnoreCase("")){
            final AlertDialog.Builder alerta = new AlertDialog.Builder(AdicionarHistoricoActivity.this);
            alerta.setTitle(R.string.ops);
            alerta.setMessage("Favor inserir tempo de uso");
            alerta.setNeutralButton("OK", null);
            alerta.create();
            alerta.show();
        }else{
            historico.setTempoDeUso((Integer.parseInt(editTextTempo.getText().toString())));
            if(editTextValor.getText().toString().equalsIgnoreCase("")){
                final AlertDialog.Builder alerta = new AlertDialog.Builder(AdicionarHistoricoActivity.this);
                alerta.setTitle("Ops");
                alerta.setMessage("Favor inserir valor do Kwh");
                alerta.setNeutralButton("OK", null);
                alerta.create();
                alerta.show();
            }else{
                historico.setValorKwh(Double.parseDouble(editTextValor.getText().toString()));
                historico.setAparelho(aparelhoSelecionado);
            }
        }
        //Inserir o objeto Histórico no banco com historicoDao
        try {
            Dao<Historico, Integer> historicoDao = MyORMLiteHelper.getInstance(this).getHistoricoDao();
            historicoDao.create(historico);

            Toast.makeText(this, historico.toString(), Toast.LENGTH_LONG).show();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        Intent it = new Intent(getApplicationContext(), HistoricoActivity.class);
        it.putExtra("paparelho", aparelhoSelecionado);
        startActivity(it);
        finish();

    }

}
