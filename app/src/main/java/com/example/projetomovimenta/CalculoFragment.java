package com.example.projetomovimenta;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.snackbar.Snackbar;


public class CalculoFragment extends Fragment {
    private EditText edtCampoPeso, edtCampoAltura;
    private Button btnCalcular, btnLimpar;
    private TextView txtSeuImc, txtInterpretacaoImc, txtInformacao;
    private Toast toast;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.calculofragment, container, false);

        edtCampoPeso = view.findViewById(R.id.idCampoPeso);
        edtCampoAltura = view.findViewById(R.id.idCampoAltura);
        btnCalcular = view.findViewById(R.id.idBtnCalcular);
        btnLimpar = view.findViewById(R.id.idBtnLimpar);
        txtSeuImc = view.findViewById(R.id.idTxtSeuImc);
        txtInformacao = view.findViewById(R.id.idTxtInformacao);
        txtInterpretacaoImc = view.findViewById(R.id.idTabelaImc);

        txtSeuImc.setVisibility(View.GONE);
        txtInformacao.setVisibility(View.GONE);
        txtInterpretacaoImc.setVisibility(View.GONE);

        btnCalcular.setOnClickListener(this::calcularImc);
        btnLimpar.setOnClickListener(v -> limparCampos());

        return view;
    }

    public void calcularImc(View v) {
        String strCampoPeso, strCampoAltura;
        strCampoPeso = edtCampoPeso.getText().toString();
        strCampoAltura = edtCampoAltura.getText().toString();
        //verificar campos e valida
        if (!strCampoPeso.isEmpty()) {
            if (!strCampoAltura.isEmpty()) {
                double peso, altura, calculo;
                peso = Double.parseDouble(strCampoPeso);
                altura = Double.parseDouble(strCampoAltura);
                altura = altura * altura;
                calculo = peso / altura;
                if (calculo > 0) {
                    txtSeuImc.setVisibility(View.VISIBLE);
                    txtSeuImc.setText("Seu Imc: " + Math.round(calculo));
                    txtInformacao.setVisibility(View.VISIBLE);
                    txtInterpretacaoImc.setVisibility(View.VISIBLE);
                    interpretacaoImc(calculo);
                }
            } else {
                Snackbar snackbar = Snackbar.make(v, "Preencha o campo Altura:!!", Snackbar.LENGTH_SHORT);
                snackbar.setBackgroundTint(Color.WHITE);
                snackbar.setTextColor(Color.BLACK);
                snackbar.show();
                edtCampoAltura.requestFocus();
            }
        } else {
            Snackbar snackbar = Snackbar.make(v, "Preencha o campo Peso:!!", Snackbar.LENGTH_SHORT);
            snackbar.setBackgroundTint(Color.WHITE);
            snackbar.setTextColor(Color.BLACK);
            snackbar.show();
            //retornar p/ campo
            edtCampoPeso.requestFocus();
        }
    }

    public void limparCampos() {
        txtSeuImc.setVisibility(View.GONE);
        txtInformacao.setVisibility(View.GONE);
        txtInterpretacaoImc.setVisibility(View.GONE);
        edtCampoPeso.setText("");
        edtCampoAltura.setText("");
        txtSeuImc.setText("");
        edtCampoPeso.requestFocus();
    }

    public void interpretacaoImc(Double imc) {
        if (imc < 18.5) {
            txtInterpretacaoImc.setText("MENOR QUE 18,5 -> Baixo peso");
        } else {
            if (imc >= 18.5 && imc < 24.9) {
                txtInterpretacaoImc.setText("ENTRE 18,5 E 24,9 Peso Adequado");
            } else {
                if (imc == 25) {
                    txtInterpretacaoImc.setText("Igual a 25 Risco de SobrePeso");
                } else {
                    if (imc > 25 & imc <= 29.9) {
                        txtInterpretacaoImc.setText("ENTRE 25,0 E 29,9 Pré-Obeso");
                    } else {
                        if (imc >= 30 && imc <= 34.9) {
                            txtInterpretacaoImc.setText("ENTRE 30,0 E 34,9 Obesidade GRAU I");
                        } else {
                            if (imc >= 35 && imc <= 39.9) {
                                txtInterpretacaoImc.setText("ENTRE 35,0 E 39,9 Obesidade GRAU II");
                            } else {
                                if (imc > 40) {
                                    txtInterpretacaoImc.setText("Igual ou Maior 40 Obesidade GRAU III");
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}