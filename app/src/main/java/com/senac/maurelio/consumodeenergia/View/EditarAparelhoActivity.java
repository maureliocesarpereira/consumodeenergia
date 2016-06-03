package com.senac.maurelio.consumodeenergia.View;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.PreparedUpdate;
import com.j256.ormlite.stmt.UpdateBuilder;
import com.senac.maurelio.consumodeenergia.Dao.MyORMLiteHelper;
import com.senac.maurelio.consumodeenergia.Model.Aparelho;
import com.senac.maurelio.consumodeenergia.R;
import com.senac.maurelio.consumodeenergia.View.MainActivity;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class EditarAparelhoActivity extends AppCompatActivity {

    private EditText editTextNome, editTextMarca, editTextPotencia;
    private Aparelho aparelhoSelecionado, aparelho;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_aparelho);
        //resgatar elementos do layout
        editTextNome = (EditText) findViewById(R.id.editTextNome2);
        editTextMarca = (EditText) findViewById(R.id.editTextMarca2);
        editTextPotencia = (EditText) findViewById(R.id.editTextPotencia2);

        //Resgatar Intent
        Bundle p = getIntent().getExtras();

        if (p != null) {

            aparelhoSelecionado = (Aparelho) p.getSerializable("paparelho");

            //Carregar valores nos elementos do layout
            editTextNome.setText(aparelhoSelecionado.getNome().toString());
            editTextMarca.setText(aparelhoSelecionado.getMarca().toString());
            editTextPotencia.setText(String.valueOf(aparelhoSelecionado.getPotencia()));
        }
    }

    public void update(Aparelho obj) {
        try {
            Dao<Aparelho, Integer> aparelhoDao = MyORMLiteHelper.getInstance(this).getAparelhoDao();
            if (aparelhoDao.update(obj)>0) {
                Toast.makeText(this, "Aparelho atualizado: " + obj.getNome(), Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Aparelho não atualizado: ", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void salvarEquipamento (View v) {

        aparelhoSelecionado.setNome(editTextNome.getText().toString());
        aparelhoSelecionado.setMarca(editTextMarca.getText().toString());
        aparelhoSelecionado.setPotencia(Double.parseDouble(editTextPotencia.getText().toString()));

        update(aparelhoSelecionado);

        Intent it = new Intent();
        setResult(RESULT_OK, it);


        //Finalizar a tela
        finish();
    }


}
