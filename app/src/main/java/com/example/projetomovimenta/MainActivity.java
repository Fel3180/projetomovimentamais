package com.example.projetomovimenta;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.webkit.WebView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;

import java.util.ArrayList;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawerLayout;
    private TextView nomeUsuario, emailUsuario;
    private WebView mWebView;

    private void initComponents() {
        nomeUsuario = findViewById(R.id.textViewNome);
        emailUsuario = findViewById(R.id.textViewEmail);
        mWebView = findViewById(R.id.webview);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initComponents();

        // Crie a lista de academias e adicione academias a ela
        ArrayList<Academia> academias = new ArrayList<>();
        academias.add(new Academia("Academia 1", "Regional 1", "Endereço 1"));
        academias.add(new Academia("Academia 2", "Regional 2", "Endereço 2"));
        // Adicione quantas academias desejar

        // Para passar a lista de academias para a próxima Activity (MapActivity), você pode usar Intent
        Intent intent = new Intent(this, MapActivity.class);
        intent.putParcelableArrayListExtra("academias", academias);
        startActivity(intent);

        drawerLayout = findViewById(R.id.nav_view);
        Toolbar toolbar = findViewById(R.id.toolbar);
        NavigationView navigationView = findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new LocalizacaoFragment()).commit();
            navigationView.setCheckedItem(R.id.localizacao);
        }

        String text = "<html><body>"
                + "<p align=\"justify\">"
                + getString(R.string.lorem_ipsum)
                + "</p> "
                + "</body></html>";

        mWebView.loadData(text, "text/html", "utf-8");
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        Fragment selectedFragment = null;

        if (menuItem.getItemId() == R.id.academias) {
            selectedFragment = new AcademiasFragment();
        } else if (menuItem.getItemId() == R.id.localizacao) {
            selectedFragment = new LocalizacaoFragment();
        } else if (menuItem.getItemId() == R.id.equipamentos) {
            selectedFragment = new EquipamentosFragment();
        } else if (menuItem.getItemId() == R.id.imc) {
            selectedFragment = new CalculoFragment();
        } else if (menuItem.getItemId() == R.id.saude) {
            selectedFragment = new DicasFragment();
        } else if (menuItem.getItemId() == R.id.sair) {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

            if(user != null) {
                for (UserInfo profile : user.getProviderData()) {
                    String providerId = profile.getProviderId();

                    if (providerId.equals("google.com")) {
                        GoogleSignIn.getClient(this, GoogleSignInOptions.DEFAULT_SIGN_IN).revokeAccess()
                                .addOnCompleteListener(this, task -> {
                                    if (task.isSuccessful()) {
                                        Snackbar snackbar = Snackbar.make(Objects.requireNonNull(menuItem.getActionView()), "Deslogado com sucesso", Snackbar.LENGTH_SHORT);
                                        snackbar.setBackgroundTint(Color.WHITE);
                                        snackbar.setTextColor(Color.BLACK);
                                        snackbar.show();
                                    } else {
                                        Snackbar snackbar = Snackbar.make(Objects.requireNonNull(menuItem.getActionView()), "Erro ao deslogar usuário", Snackbar.LENGTH_SHORT);
                                        snackbar.setBackgroundTint(Color.WHITE);
                                        snackbar.setTextColor(Color.BLACK);
                                        snackbar.show();
                                    }
                                });

                    }
                }
            }

            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(MainActivity.this, FormLogin.class);
            startActivity(intent);
            finish();
        } else if (menuItem.getItemId()==R.id.share) {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_SUBJECT, "Confira este Aplicativo");
            intent.putExtra(Intent.EXTRA_TEXT, "Link do Aplicativo aqui"); // Use EXTRA_TEXT para o corpo do texto
            startActivity(Intent.createChooser(intent, "Compartilhar"));

        }

        if (selectedFragment != null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}





