package com.example.projetomovimenta;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.projetomovimenta.databinding.ActivityFormLoginBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class FormCadastro extends AppCompatActivity {

    private EditText edit_nome, edit_email, edit_senha;
    private Button bt_cadastrar;
    String[] mensagens = {"Preencha todos os campos", "Cadastro realizado com sucesso"};
    String usuarioId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_cadastro);

        //getSupportActionBar().hide();
        IniciarComponentes();

        bt_cadastrar.setOnClickListener(v -> {

            String nome = edit_nome.getText().toString();
            String email = edit_email.getText().toString();
            String senha = edit_senha.getText().toString();

            if (nome.isEmpty() || email.isEmpty() || senha.isEmpty()) {
                Snackbar snackbar = Snackbar.make(v, mensagens[0], Snackbar.LENGTH_SHORT);
                snackbar.setBackgroundTint(Color.WHITE);
                snackbar.setTextColor(Color.BLACK);
                snackbar.show();
            } else {
                CadastrarUsuario(v);
            }
        });
    }

    private void CadastrarUsuario(View v) {

        String email = edit_email.getText().toString();
        String senha = edit_senha.getText().toString();

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, senha).addOnCompleteListener(task -> {

            if (task.isSuccessful()) {

                SalvarDadosUsuario();

                Snackbar snackbar = Snackbar.make(v, mensagens[1], Snackbar.LENGTH_SHORT);
                snackbar.setBackgroundTint(Color.WHITE);
                snackbar.setTextColor(Color.BLACK);
                snackbar.show();

                Intent intent = new Intent(FormCadastro.this, FormLogin.class);
                startActivity(intent);

            } else {
                String erro;
                try {
                    throw task.getException();

                } catch (FirebaseAuthWeakPasswordException e) {

                    erro = "Digite uma senha com no mínimo 6 caracteres";
                } catch (FirebaseAuthUserCollisionException e) {
                    erro = "Esta conta já foi cadastrada";
                } catch (FirebaseAuthInvalidCredentialsException e) {
                    erro = "E-mail Inválido";
                } catch (Exception e) {
                    erro = "Erro ao cadastrar usuário";
                }

                Snackbar snackbar = Snackbar.make(v, erro, Snackbar.LENGTH_SHORT);
                snackbar.setBackgroundTint(Color.WHITE);
                snackbar.setTextColor(Color.BLACK);
                snackbar.show();
            }
        });
    }

    private void SalvarDadosUsuario() {
        String nome = edit_nome.getText().toString();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Map<String, Object> usuarios = new HashMap<>();
        usuarios.put("nome", nome);

        usuarioId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DocumentReference documentReference = db.collection("Usuários").document(usuarioId);
        documentReference.set(usuarios).addOnSuccessListener(aVoid -> Log.d("db", "Sucesso ao salvar os dados"))
                .addOnFailureListener(e -> Log.d("db_error", "Erro ao salvar os dados" + e));
    }


    private void IniciarComponentes() {

        edit_nome = findViewById(R.id.edit_nome);
        edit_email = findViewById(R.id.edit_email);
        edit_senha = findViewById(R.id.edit_senha);
        bt_cadastrar = findViewById(R.id.bt_cadastrar);
    }
}