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
import com.senac.maurelio.consumodeenergia.View.HistoricoActivity;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;

public class EditarHistoricoActivity extends AppCompatActivity {

    private Historico historicoIntent, historico;
    private Spinner spinnerAparelho;
    private EditText editTextTempo, editTextValor;
    private ArrayAdapter<Aparelho> adapterAparelhos;
    private Aparelho aparelhoSelecionado, a, aparelho;
    private Integer idH;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_historico);

        //recuperar elementos do layout
        editTextTempo = (EditText) findViewById(R.id.editTextTempo2);
        spinnerAparelho = (Spinner) findViewById(R.id.spinnerAparelhos2);
        editTextValor = (EditText) findViewById(R.id.editTextValorKwh2);

        //receber Intent
        Bundle p = getIntent().getExtras();
        if (p!=null){
            historicoIntent = (Historico) p.getSerializable("phistorico");
            //Carregar valores nos elementos do layout
            editTextTempo.setText(String.valueOf(historicoIntent.getTempoDeUso()));
            editTextValor.setText(String.valueOf(historicoIntent.getValorKwh()));

            //Coletar id do histórico vindo na intent
            idH = historicoIntent.getId();
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
                    if (a.getId() == historicoIntent.getAparelho().getId()) {
                        aparelhoSelecionado = a;
                    }
                }
            }
            //Spinner
            this.adapterAparelhos = new ArrayAdapter<Aparelho>(this, android.R.layout.simple_spinner_dropdown_item,listaDeAparelhos);
            spinnerAparelho.setAdapter(adapterAparelhos);
            spinnerAparelho.setSelection(adapterAparelhos.getPosition(aparelhoSelecionado));
            editTextValor.setText(String.valueOf(historicoIntent.getValorKwh()));
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
    public void update(Historico historico) {

        try {
            Dao<Historico, Integer> historicoDao = MyORMLiteHelper.getInstance(getApplicationContext()).getHistoricoDao();
            if (historicoDao.update(historico)>0) {
                Toast.makeText(this, "Histórico atualizado: " + historico.getTempoDeUso(), Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Histórico não atualizado: ", Toast.LENGTH_LONG).show();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void salvarHistorico(View v){

        //Instanciar Histórico para gravar no banco
        historico = new Historico();

        //Instanciar objeto Aparelho para pegar o aparelho selecionado
        Aparelho aparelho = (Aparelho) spinnerAparelho.getSelectedItem();
        historico.setAparelho(aparelho);

        if(editTextTempo.getText().toString().equalsIgnoreCase("")){
            final AlertDialog.Builder alerta = new AlertDialog.Builder(EditarHistoricoActivity.this);
            alerta.setTitle(R.string.ops);
            alerta.setMessage("Favor inserir tempo de uso");
            alerta.setNeutralButton("OK", null);
            alerta.create();
            alerta.show();
        }else{
            historico.setTempoDeUso((Integer.parseInt(editTextTempo.getText().toString())));
            if(editTextValor.getText().toString().equalsIgnoreCase("")){
                final AlertDialog.Builder alerta = new AlertDialog.Builder(EditarHistoricoActivity.this);
                alerta.setTitle("Ops");
                alerta.setMessage("Favor inserir valor do Kwh");
                alerta.setNeutralButton("OK", null);
                alerta.create();
                alerta.show();
            }else{
                historico.setValorKwh(Double.parseDouble(editTextValor.getText().toString()));
                //Setar id do histórico com o id vindo na intent (idH)
                historico.setId(idH);
            }
        }
        //Salvar o objeto Histórico no banco com historicoDao
        update(historico);

        Intent it = new Intent(this, HistoricoActivity.class);
        it.putExtra("paparelho", aparelho);
        startActivity(it);
        finish();
    }


}
