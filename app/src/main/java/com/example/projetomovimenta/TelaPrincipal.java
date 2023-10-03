package com.example.projetomovimenta;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.projetomovimenta.databinding.ActivityFormLoginBinding;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.android.gms.auth.api.signin.GoogleSignIn;

import java.util.Objects;

public class TelaPrincipal extends AppCompatActivity {

    private TextView nomeUsuario, emailUsuario;
    private Button bt_deslogar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_principal);

        //getSupportActionBar().hide();
        initComponents();

        bt_deslogar.setOnClickListener(this::onClick);
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        String email = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getEmail();
        String usuarioID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        if (FirebaseAuth.getInstance().getCurrentUser().getDisplayName() != null) {
            nomeUsuario.setText(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
            emailUsuario.setText(email);
        } else {

            DocumentReference documentReference = db.collection("Usuários").document(usuarioID);
            documentReference.addSnapshotListener((documentSnapshot, error) -> {
                if (documentSnapshot != null) {
                    nomeUsuario.setText(documentSnapshot.getString("nome"));
                    emailUsuario.setText(email);
                }
            });
        }
    }

    private void initComponents() {
        nomeUsuario = findViewById(R.id.textNomeUsuario);
        emailUsuario = findViewById(R.id.textEmailUsuario);
        bt_deslogar = findViewById(R.id.bt_deslogar);
    }

    private void onClick(View v) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();

        assert user != null;
        for (UserInfo profile : user.getProviderData()) {
            String providerId = profile.getProviderId();

            if (providerId.equals("google.com")) {
                GoogleSignIn.getClient(this, GoogleSignInOptions.DEFAULT_SIGN_IN).revokeAccess()
                        .addOnCompleteListener(this, task -> {
                            if (task.isSuccessful()) {
                                Snackbar snackbar = Snackbar.make(v, "Deslogado com sucesso", Snackbar.LENGTH_SHORT);
                                snackbar.setBackgroundTint(Color.WHITE);
                                snackbar.setTextColor(Color.BLACK);
                                snackbar.show();
                            } else {
                                Snackbar snackbar = Snackbar.make(v, "Erro ao deslogar usuário", Snackbar.LENGTH_SHORT);
                                snackbar.setBackgroundTint(Color.WHITE);
                                snackbar.setTextColor(Color.BLACK);
                                snackbar.show();
                            }
                        });

            }
        }

        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(TelaPrincipal.this, FormLogin.class);
        startActivity(intent);
        finish();
    }
}