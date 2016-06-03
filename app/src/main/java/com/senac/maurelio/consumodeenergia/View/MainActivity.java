package com.senac.maurelio.consumodeenergia.View;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
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
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;

public class MainActivity extends AppCompatActivity {

    private ListView listViewAparelhos;
    private ArrayList<Aparelho> listaDeAparelhos;
    private ArrayAdapter<Aparelho> adapterAparelhos;
    private Aparelho aparelhoSelecionado, a;
    private TextView textViewTotal;
    private Historico h;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listViewAparelhos = (ListView) findViewById(R.id.listViewEquipamentos);
        textViewTotal = (TextView) findViewById(R.id.editTextValorFinal);

        atualizarLista();
        atualizarValor();

        //Clique Longo (Adicionar Histórico)

        listViewAparelhos.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                aparelhoSelecionado = new Aparelho();
                aparelhoSelecionado = adapterAparelhos.getItem(position);

                Intent it = new Intent(MainActivity.this, AdicionarHistoricoActivity.class);
                it.putExtra("paparelho", aparelhoSelecionado);
                startActivity(it);
                return false;
            }
        });

        //Clique Curto (Editar, excluir, ver histórico do aparelho)
        listViewAparelhos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                aparelhoSelecionado = adapterAparelhos.getItem(position);
                mostrarDadosAparelho(aparelhoSelecionado);

            }

        });
    }

    private void mostrarDadosAparelho(final Aparelho aparelhoSelecionado) {
        final AlertDialog.Builder alerta = new AlertDialog.Builder(MainActivity.this);
        alerta.setTitle("Detalhes Aparelho");
        String msg;
        msg = "Aparelho: " + aparelhoSelecionado.getNome() + "\n";
        msg += "Marca: " + aparelhoSelecionado.getMarca() + "\n";
        msg += "Potência: " + aparelhoSelecionado.getPotencia() + "\n";
        alerta.setCancelable(true);
        alerta.setNegativeButton("Fechar", null);

        if (identificarHistorico(aparelhoSelecionado) == true) {
            alerta.setNeutralButton("Histórico", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent it = new Intent(getApplicationContext(), HistoricoActivity.class);
                    it.putExtra("paparelho", aparelhoSelecionado);
                    startActivityForResult(it, 2);

                }
            });
        } else {
            msg += "Sem histórico de utilização";
            alerta.setNeutralButton("Fechar", null);
        }
        alerta.setMessage(msg);
        alerta.setNegativeButton("Editar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent it = new Intent(getApplicationContext(), EditarAparelhoActivity.class);
                it.putExtra("paparelho", aparelhoSelecionado);
                startActivityForResult(it, 1);
            }
        });
        alerta.setPositiveButton("Excluir", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    //fazer o dao
                    Dao<Aparelho, Integer> aparelhoDao = MyORMLiteHelper.getInstance(MainActivity.this).getAparelhoDao();
                    excluirHistoricoPorAparelho(aparelhoSelecionado);
                    aparelhoDao.deleteById(aparelhoSelecionado.getId());

                } catch (SQLException e) {
                    e.printStackTrace();
                }

                Toast.makeText(MainActivity.this, "Aparelho deletado", Toast.LENGTH_LONG).show();
                atualizarLista();
            }
        });

        alerta.create();
        alerta.show();
    }

    public boolean identificarHistorico(Aparelho aparelho) {
        Iterator<Historico> i = aparelho.getHistoricos().iterator();
        if (i.hasNext() == true) {
            return true;
        } else {
            return false;
        }
    }

    public void atualizarLista() {
        ArrayList<Aparelho> listaDeAparelhos = new ArrayList<>();
        try {
            Dao<Aparelho, Integer> aparelhoDao = MyORMLiteHelper.getInstance(this).getAparelhoDao();
            listaDeAparelhos = (ArrayList<Aparelho>) aparelhoDao.queryForAll();
            this.adapterAparelhos = new ArrayAdapter<>(this,
                    android.R.layout.simple_list_item_1,
                    listaDeAparelhos);
            listViewAparelhos.setAdapter(adapterAparelhos);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void atualizarValor() {
        textViewTotal.setText("Consumo total: " + DecimalFormat.getCurrencyInstance().format(calcularConsumo()));
    }

    public void atualizarConsumo(View v) {
        atualizarValor();
        atualizarLista();
    }

    public Double calcularConsumo() {
        listaDeAparelhos = new ArrayList<>();
        Double valorFinal = 0.00;
        Double valor, tempo, potencia;
        try {
            Dao<Aparelho, Integer> aparelhoDao = MyORMLiteHelper.getInstance(this).getAparelhoDao();
            listaDeAparelhos = (ArrayList<Aparelho>) aparelhoDao.queryForAll();
            //For
            for (Aparelho aparelho : listaDeAparelhos) {
                for (Historico historico : aparelho.getHistoricos()) {
                    potencia = historico.getAparelho().getPotencia();
                    valor = historico.getValorKwh();
                    tempo = Double.valueOf(historico.getTempoDeUso());
                    valorFinal += ((valor * tempo * potencia) / 60000);
                }
            }
            return valorFinal;

        } catch (SQLException e) {
            e.printStackTrace();
            return 0.00;
        }
    }

    public void cadastrarAparelho(View v) {
        Intent it = new Intent(this, AparelhoActivity.class);
        startActivityForResult(it, 1);
    }

    //Subscrever método onActivityResult
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0 && resultCode == RESULT_OK){
            String resultado = data.getStringExtra("SCAN_RESULT");
            String format = data.getStringExtra("SCAN_RESULT_FORMAT");
            Toast.makeText(MainActivity.this, "Resultado: "+ resultado, Toast.LENGTH_SHORT).show();

            try {
                a = MyORMLiteHelper.getInstance(this).getAparelhoDao().queryForId(Integer.valueOf(resultado));
                mostrarDadosAparelho(a);
            } catch (SQLException e) {
                e.printStackTrace();
            }

        }else {
            atualizarLista();
        }
    }

    public void cadastrarHistorico(View v) {
        Intent it = new Intent(this, AdicionarHistoricoActivity.class);
        startActivity(it);
    }

    public void excluirHistoricoPorAparelho(Aparelho aparelho) {

        Iterator<Historico> iterator = aparelho.getHistoricos().iterator();
        while (iterator.hasNext()) {
            Historico h = iterator.next();
            try {
                Dao<Historico, Integer> historicoDao = MyORMLiteHelper.getInstance(this).getHistoricoDao();
                historicoDao.deleteById(h.getId());
                Toast.makeText(MainActivity.this, "Histórico " + h.getId() + " Excluído", Toast.LENGTH_LONG).show();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
//https://www.youtube.com/watch?v=iT6EaIwtonY
    public void escanearQR(View v){
        try {
            Intent intent = new Intent("com.google.zxing.client.android.SCAN");
            intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
            startActivityForResult(intent, 0);
        } catch (ActivityNotFoundException anfe) {
            Toast.makeText(this, "Você precisa baixar o Barcode", Toast.LENGTH_LONG).show();
            Uri uri = Uri.parse("market://search?q=pname:" + "com.google.zxing.client.android");
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        }
    }

    public void scanQR() {

    }

    @Override
    protected void onResume() {
        super.onResume();
        atualizarValor();
    }
}
