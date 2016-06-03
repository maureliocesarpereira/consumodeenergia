package com.senac.maurelio.consumodeenergia.View;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.j256.ormlite.dao.Dao;
import com.senac.maurelio.consumodeenergia.Dao.MyORMLiteHelper;
import com.senac.maurelio.consumodeenergia.Model.Aparelho;
import com.senac.maurelio.consumodeenergia.Model.Historico;
import com.senac.maurelio.consumodeenergia.R;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class HistoricoActivity extends AppCompatActivity {

    private TextView textViewNomeAparelho, textViewTotal;
    private ListView listViewHistoricos;
    private ArrayAdapter<Historico> arrayAdapterHistoricos;
    private ArrayList<Historico> arrayHistoricos;
    public Aparelho aparelhoSelecionado, aparelhoTemp;
    private Historico h, historicoSelecionado;
    boolean cliqueLongo = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historico);

        textViewNomeAparelho = (TextView) findViewById(R.id.textViewNome);
        textViewTotal = (TextView) findViewById(R.id.textViewTotal);
        listViewHistoricos = (ListView) findViewById(R.id.listViewHistorico);

        //Carregar Intent
        Bundle parametro = getIntent().getExtras();

        //Extrair objeto Aparelho
        aparelhoSelecionado = (Aparelho) parametro.getSerializable("paparelho");

        //Publicar o nome do aparelho
        textViewNomeAparelho.setText(aparelhoSelecionado.getNome().toString() + " - " + aparelhoSelecionado.getPotencia());

        //Início Clique Curto
        listViewHistoricos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (cliqueLongo == false) {
                    historicoSelecionado = arrayAdapterHistoricos.getItem(position);
                    Intent it = new Intent(getApplicationContext(), EditarHistoricoActivity.class);
                    it.putExtra("phistorico", historicoSelecionado);
                    startActivity(it);
                    finish();
                } else {
                    cliqueLongo = false;
                }
            }
        });
        //Início Clique Longo
        listViewHistoricos.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                historicoSelecionado = arrayAdapterHistoricos.getItem(position);
                final AlertDialog.Builder alerta = new AlertDialog.Builder(HistoricoActivity.this);
                alerta.setTitle(R.string.ops);
                alerta.setMessage("Deseja deletar esse histórico?");
                alerta.setNeutralButton("Cancelar", null);
                alerta.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deletarHistorico(historicoSelecionado);
                        //Listar históricos do aparelho selecionado
                        atualizarLista(historicoSelecionado.getAparelho());
                    }
                });
                alerta.create();
                alerta.show();
                cliqueLongo = true;
                return false;
            }
        });
        //Listar históricos do aparelho selecionado
        atualizarLista(aparelhoSelecionado);
        Intent it = new Intent();
        setResult(RESULT_OK, it);

    }

    public Double calcularConsumo(Aparelho aparelho) {
        aparelhoTemp = new Aparelho();

        try {
            Dao<Aparelho, Integer> daoAparelho = MyORMLiteHelper.getInstance(this).getAparelhoDao();
            aparelhoTemp = daoAparelho.queryForSameId(aparelho);
            Iterator<Historico> iterator = aparelhoTemp.getHistoricos().iterator();
            Double tempo = 0.00;
            Double valorFinal = 0.00;
            Double valor = 0.00;
            while (iterator.hasNext()) {
                h = new Historico();
                h = iterator.next();
                valor = h.getValorKwh();
                tempo = Double.valueOf(h.getTempoDeUso());
                Double potencia = h.getAparelho().getPotencia();
                valorFinal += (valor * tempo * potencia) / 60000;
            }
            return valorFinal;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.00;
    }

    public void deletarHistorico(Historico historico) {
        try {
            Dao<Historico, Integer> daoHistorico = MyORMLiteHelper.getInstance(this).getHistoricoDao();
            daoHistorico.deleteById(historico.getId());
            Toast.makeText(HistoricoActivity.this, "Histórico deletado", Toast.LENGTH_SHORT).show();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void atualizarLista(Aparelho aparelho) {

        arrayHistoricos = new ArrayList<Historico>();
        aparelhoTemp = new Aparelho();
        try {
            Dao<Aparelho, Integer> daoAparelho = MyORMLiteHelper.getInstance(this).getAparelhoDao();
            aparelhoTemp = daoAparelho.queryForSameId(aparelho);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        Iterator<Historico> iterator = aparelhoTemp.getHistoricos().iterator();
        while (iterator.hasNext()) {
            h = new Historico();
            h = iterator.next();
            arrayHistoricos.add(h);
        }
        //Publicar lista atualizada
        this.arrayAdapterHistoricos = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1,
                arrayHistoricos);
        listViewHistoricos.setAdapter(arrayAdapterHistoricos);

        textViewTotal.setText("Consumo total: " + calcularConsumo(aparelhoTemp));

    }
}