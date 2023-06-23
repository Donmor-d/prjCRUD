package com.example.prjcrud;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.os.StrictMode;
import android.view.View;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prjcrud.databinding.ActivityMainBinding;
import com.mysql.jdbc.PreparedStatement;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;

    RecyclerView recyclerView;
    DbAmigosAdapter adapter;

    DbAmigo amigoAlterado = null;

    FloatingActionButton fabRedo;

    //Outside Database MySQL
    private static final String url = "jdbc:mysql://108.179.253.78:3306/dreco836_DbAmigos?characterEncoding=latin1";
    private static final String usuario = "dreco836_aluno";
    private static final String senha = "Amigos2022";
    private Connection connection = null;
    //================================

    private int getIndex(Spinner spinner, String myString) {
        int index = 0;
        for (int i=0;(i<spinner.getCount())&&!(spinner.getItemAtPosition(i).toString().equalsIgnoreCase(myString));i++);
        return index;
    }


    private void configurarRecycler() {
        // Ativando o layou para uma lista tipo RecyclerView e configurando-a

        recyclerView = (RecyclerView)findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        // Preparando o adapter para associar os objetos à lista.

        DbAmigosDAO dao = new DbAmigosDAO(this);
        adapter = new DbAmigosAdapter(dao.listarAmigos());
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        for(int i = 0; i < recyclerView.getChildCount(); i++){
            System.out.println("CONFIGURAR RECYCLE | child("+i+"): "+ recyclerView.getChildAt(i));
        }

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        binding = ActivityMainBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);

        Intent intent = getIntent();

        //Update Amigo?
        if(intent.hasExtra("amigo")){
            System.out.println("id includecadastro: " + R.id.include_cadastro);
            System.out.println("View: " + (View) findViewById(R.id.include_cadastro));


            findViewById(R.id.include_listagem).setVisibility(View.INVISIBLE);
            findViewById(R.id.include_cadastro).setVisibility(View.VISIBLE);
            findViewById(R.id.fab).setVisibility(View.INVISIBLE);

            amigoAlterado = (DbAmigo) intent.getSerializableExtra("amigo");
            EditText edtNome    = (EditText)findViewById(R.id.edtNome);
            EditText edtCelular = (EditText)findViewById(R.id.edtCelular);

            edtNome.setText(amigoAlterado.getNome());
            edtCelular.setText(amigoAlterado.getCelular());
            int status = 20;
        }

        //Create DB connection
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitNetwork().build();
        StrictMode.setThreadPolicy(policy);

        try {
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection(url, usuario, senha);
            Toast.makeText(this, "Conectado em DB externo", Toast.LENGTH_SHORT).show();
        }
        catch (SQLException e) {
            Toast.makeText(this, "Não pode conectar em DB externo", Toast.LENGTH_SHORT).show();
            throw new RuntimeException(e);
        }
        catch (ClassNotFoundException e) {
            Toast.makeText(this, "Classe não encotnrada", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

        //=======================
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("id includecadastro: " + R.id.include_cadastro);
                System.out.println("View: " + (View) findViewById(R.id.include_cadastro));

                findViewById(R.id.include_listagem).setVisibility(View.INVISIBLE);
                findViewById(R.id.include_cadastro).setVisibility(View.VISIBLE);
                findViewById(R.id.fab).setVisibility(View.INVISIBLE);
            }
        });

        /*
        fabRedo = (FloatingActionButton) findViewById(R.id.fabRedo);
        fabRedo.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                //Set the right page to show to guarantee calls form other views as well
                View includeListagem = findViewById(R.id.include_listagem);
                includeListagem.setVisibility(View.VISIBLE);
                findViewById(R.id.include_cadastro).setVisibility(View.INVISIBLE);

                List<View> buttonsRedo = includeListagem.findViewWithTag("buttonRedo");
                System.out.println("Buttons:" + buttonsRedo);

                buttonsRedo.forEach((View button) -> {
                    button.setVisibility(View.GONE);
                });

                //findViewById(R.id.btnEditar).setVisibility(View.GONE);
                //findViewById(R.id.btnRecuperar).setVisibility(View.VISIBLE);
            }
        });
        */
        Button btnCancelar = (Button)findViewById(R.id.btnCancelar);
        btnCancelar.setOnClickListener(new Button.OnClickListener() {
        @Override
            public void onClick(View view) {
                Snackbar.make(view, "Cancelando...", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                findViewById(R.id.include_listagem).setVisibility(View.VISIBLE);
                findViewById(R.id.include_cadastro).setVisibility(View.INVISIBLE);
                findViewById(R.id.fab).setVisibility(View.VISIBLE);
            }
        });



        Button btnSalvar = (Button)findViewById(R.id.btnSalvar);
        btnSalvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Sincronizando os campos com o contexto
                EditText edtNome = (EditText) findViewById (R.id.edtNome);
                EditText edtCelular = (EditText) findViewById (R.id.edtCelular);

                // Adaptando atributos
                String nome = edtNome.getText().toString();
                String celular = edtCelular.getText().toString();
                int status = 10;

                // Gravando no banco de dados
                DbAmigosDAO dao = new DbAmigosDAO(getBaseContext());

                boolean sucesso;

                if(amigoAlterado != null) {
                    //Update amigo
                    status = 20;
                    sucesso = dao.salvar(amigoAlterado.getId(), nome, celular, status);
                }
                else {
                    //Insert amigo
                    sucesso = dao.salvar(nome, celular, status);

                    //Try add amigo into external MySQL
                    try {
                        //Insert
                        /*
                        Finding out database name and table names.
                        PreparedStatement pTable = (PreparedStatement) connection.prepareStatement("SHOW TABLES");
                        PreparedStatement pDb = (PreparedStatement) connection.prepareStatement("SHOW DATABASES");
                        ResultSet rsTable = pTable.executeQuery();
                        ResultSet rsDb = pDb.executeQuery();
                        while(rsTable.next()){
                            System.out.println("Tables: " + rsTable.getString(1));
                        }
                        while(rsDb.next()){
                            System.out.println("DBs: " + rsDb.getString(1));
                        }
                        */

                        String sql = String.format("INSERT INTO Amigos(RA, ID, nome, celular, status) VALUES(6110574, %d, '%s', '%s', %d)",dao.ultimoAmigo().getId(), nome, celular, status);
                        PreparedStatement prepStatement = (PreparedStatement) connection.prepareStatement(sql);

                        //Select (for checking)
                        String sql2 = String.format("SELECT * FROM Amigos");
                        //String sql2 = String.format("DESCRIBE Amigos");
                        PreparedStatement prepStatement2 = (PreparedStatement) connection.prepareStatement(sql2);
                        ResultSet selectAmigos = prepStatement2.executeQuery();

                        //Execute queries
                        prepStatement.executeUpdate();
                        while(selectAmigos.next()){
                            System.out.println("Row: " + selectAmigos.getRow() + "Amigos: " + selectAmigos.getString(1));
                        }

                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }


                }

                if (sucesso) {
                    DbAmigo amigo = dao.ultimoAmigo();

                    if (amigoAlterado != null) {
                        adapter.atualizarAmigo(amigo);
                        amigoAlterado = null;
                        configurarRecycler();
                    }
                    else {
                        adapter.inserirAmigo(amigo);
                    }

                    //Give feedback it worked
                    Snackbar.make(view, "Amigo: " + amigo.getNome() + "  foi salvo!", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();

                    //Load listagem view back for better UX
                    findViewById(R.id.include_listagem).setVisibility(View.VISIBLE);
                    findViewById(R.id.include_cadastro).setVisibility(View.INVISIBLE);
                    findViewById(R.id.fab).setVisibility(View.VISIBLE);
                }

            }
        });

            configurarRecycler();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
/*
    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }
*/

}