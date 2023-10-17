package com.example.projetomovimenta;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.snackbar.Snackbar;

public class CalculoFragment extends Fragment {
    private EditText edtCampoPeso, edtCampoAltura;
    private Button btnCalcular, btnLimpar;
    private TextView txtSeuImc, txtInterpretacaoImc;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.calculofragment, container, false);

        edtCampoPeso = view.findViewById(R.id.idCampoPeso);
        edtCampoAltura = view.findViewById(R.id.idCampoAltura);
        btnCalcular = view.findViewById(R.id.idBtnCalcular);
        btnLimpar = view.findViewById(R.id.idBtnLimpar);
        txtSeuImc = view.findViewById(R.id.idTxtSeuImc);
        txtInterpretacaoImc = view.findViewById(R.id.idTabelaImc);

        txtSeuImc.setVisibility(View.GONE);
        txtInterpretacaoImc.setVisibility(View.GONE);

        btnCalcular.setOnClickListener(v -> calcularImc());
        btnLimpar.setOnClickListener(v -> limparCampos());

        // Adicione um TextWatcher para o campo de altura
        edtCampoAltura.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                String text = editable.toString();
                if (!isValidDecimal(text)) {
                    edtCampoAltura.setError("Digite um número decimal válido.");
                }
            }
        });

        return view;
    }

    private boolean isValidDecimal(String text) {
        if (text.isEmpty()) return true;

        try {
            float value = Float.parseFloat(text);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private void calcularImc() {
        String strCampoPeso = edtCampoPeso.getText().toString();
        String strCampoAltura = edtCampoAltura.getText().toString();

        if (strCampoPeso.isEmpty() || strCampoAltura.isEmpty()) {
            Snackbar snackbar = Snackbar.make(btnCalcular, "Preencha todos os campos!", Snackbar.LENGTH_SHORT);
            snackbar.show();
            return;
        }

        float peso = Float.parseFloat(strCampoPeso);
        float altura = Float.parseFloat(strCampoAltura);
        float calculo = peso / (altura * altura);

        txtSeuImc.setVisibility(View.VISIBLE);
        txtSeuImc.setText("Seu IMC: " + Math.round(calculo));
        txtInterpretacaoImc.setVisibility(View.VISIBLE);
        interpretacaoImc(calculo);
    }

    private void limparCampos() {
        edtCampoPeso.setText("");
        edtCampoAltura.setText("");
        txtSeuImc.setVisibility(View.GONE);
        txtInterpretacaoImc.setVisibility(View.GONE);
    }

    private void interpretacaoImc(float imc) {
        String interpretacao;

        if (imc < 18.5) {
            interpretacao = "MENOR QUE 18.5 -> Baixo peso";
        } else if (imc < 24.9) {
            interpretacao = "ENTRE 18.5 E 24.9 -> Peso Adequado";
        } else if (imc < 29.9) {
            interpretacao = "ENTRE 25.0 E 29.9 -> Pré-Obeso";
        } else if (imc < 34.9) {
            interpretacao = "ENTRE 30.0 E 34.9 -> Obesidade GRAU I";
        } else if (imc < 39.9) {
            interpretacao = "ENTRE 35.0 E 39.9 -> Obesidade GRAU II";
        } else {
            interpretacao = "IGUAL OU MAIOR QUE 40 -> Obesidade GRAU III";
        }

        txtInterpretacaoImc.setText(interpretacao);
    }
}
