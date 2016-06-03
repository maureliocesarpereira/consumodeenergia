package com.senac.maurelio.consumodeenergia.View;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.j256.ormlite.dao.Dao;
import com.senac.maurelio.consumodeenergia.Dao.MyORMLiteHelper;
import com.senac.maurelio.consumodeenergia.Model.Aparelho;
import com.senac.maurelio.consumodeenergia.R;

import java.sql.SQLException;

public class AparelhoActivity extends AppCompatActivity {

    private EditText editTextNome, editTextMarca, editTextPotencia;
    private Aparelho aparelhoSelecionado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aparelho);

        //resgatar elementos do layout
        editTextNome = (EditText) findViewById(R.id.editTextNome);
        editTextMarca = (EditText) findViewById(R.id.editTextMarca);
        editTextPotencia = (EditText) findViewById(R.id.editTextPotencia);

        //Resgatar Intent
        Bundle p = getIntent().getExtras();
        if(p != null){
            aparelhoSelecionado = (Aparelho) p.getSerializable("paparelho-curto");
        }
    }

    private boolean verificarEditText(EditText editText){

        if (! editText.getText().toString().equalsIgnoreCase("")){
            return true;
        }else{
            return false;
        }
    }

    public void inserirEquipamento (View v) {
        //montar objeto
        Aparelho aparelho = new Aparelho();
        if (verificarEditText(editTextNome)==false) {
            final AlertDialog.Builder alerta = new AlertDialog.Builder(AparelhoActivity.this);
            alerta.setTitle("Ops");
            alerta.setMessage("Favor inserir nome");
            alerta.setNeutralButton("OK", null);
            alerta.create();
            alerta.show();
        }else{
            aparelho.setNome(editTextNome.getText().toString());
            if (verificarEditText(editTextMarca)==false){
                final AlertDialog.Builder alerta = new AlertDialog.Builder(AparelhoActivity.this);
                alerta.setTitle("Ops");
                alerta.setMessage("Favor inserir marca");
                alerta.setNeutralButton("OK", null);
                alerta.create();
                alerta.show();
            } else{
                aparelho.setMarca(editTextMarca.getText().toString());
                if(verificarEditText(editTextPotencia)==false){
                    final AlertDialog.Builder alerta = new AlertDialog.Builder(AparelhoActivity.this);
                    alerta.setTitle("Ops");
                    alerta.setMessage("Favor inserir potência");
                    alerta.setNeutralButton("OK", null);
                    alerta.create();
                    alerta.show();
                }else{
                    aparelho.setPotencia(Double.parseDouble(editTextPotencia.getText().toString()));

                    inserirAparelho(aparelho);
                    Intent it = new Intent();
                    setResult(RESULT_OK, it);
                    //Finalizar a tela
                    finish();
                }
            }
        }
    }

    private void inserirAparelho(Aparelho aparelho){


        try {
            //Inserir o objeto Aparelho no banco com aparelhoDao
            Dao<Aparelho, Integer> aparelhoDao = MyORMLiteHelper.getInstance(this).getAparelhoDao();
            aparelhoDao.create(aparelho);

            Toast.makeText(this, aparelho.toString(), Toast.LENGTH_LONG).show();

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }


}
