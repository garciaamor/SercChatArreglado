package com.example.jgarciaamor.serv;


import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.JsonWriter;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_17;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;


public class MainActivity extends AppCompatActivity
    implements NavigationView.OnNavigationItemSelectedListener
    {

        public static String userName;
    private WebSocketClient mWebSocketClient;
        JSONObject object, client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);




        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                //       .setAction("Action", null).show();
                sendMessage();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

        @Override
        public void onBackPressed() {
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
            } else {
                super.onBackPressed();
            }
        }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
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

        @SuppressWarnings("StatementWithEmptyBody")
        //@Override
        public boolean onNavigationItemSelected(MenuItem item) {
            // Handle navigation view item clicks here.
            int id = item.getItemId();

            if (id == R.id.nav_camera) {
                // Handle the camera action
            } else if (id == R.id.nickUsr) {
                AlertDialog.Builder alert= new AlertDialog.Builder(this);
                final EditText user=new EditText(this);
                user.setSingleLine();
                user.setPadding(50,0,50,0);
                alert.setTitle("Nickname");
                alert.setMessage("Introduzca un nombre ");
                alert.setView(user);
                alert.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        userName=user.getText().toString();
                    }
                });
                alert.setNegativeButton("Cancelar",null);
                alert.create();
                alert.show();

            } else if (id == R.id.cnct) {
                if(userName==null){
                    AlertDialog.Builder alertC=new AlertDialog.Builder(this);
                    alertC.setTitle("Atención");
                    alertC.setMessage("Necesitas un nick para conectarte");
                    alertC.setPositiveButton("Aceptar",null);
                    alertC.create();
                    alertC.show();

                }else {
                    connectWebSocket();
                }
            } else if (id == R.id.nav_manage) {

            } else if (id == R.id.nav_share) {

            } else if (id == R.id.nav_send) {

            }

            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
            return true;
        }




        private void connectWebSocket() {

            URI uri;
            try {
                uri = new URI("ws://serv-garciaamor.c9users.io:8081");
                //uri = new URI("ws://serv-mariomoure.c9users.io:8081");
            } catch (URISyntaxException e) {
                e.printStackTrace();
                return;
            }



            Map<String, String> headers = new HashMap<>();

            mWebSocketClient = new WebSocketClient(uri, new Draft_17(), headers, 0) {

                @Override
                public void onOpen(ServerHandshake serverHandshake) {
                    Log.i("Websocket", "Opened");
                    mWebSocketClient.send("{\"id\":\"" + userName + "\"}");                client = new JSONObject();
                }

                @Override
                public void onMessage(String s) {
                    final String message = s;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            TextView textView = (TextView)findViewById(R.id.messages);

                            String nombre;
                            String msg;
                            int priv;
                            String det;

                            try{
                                object = new JSONObject(message);
                                    nombre=object.getString("id");
                                    msg = object.getString("msg");
                                    priv = object.getInt("priv");
                                    det = object.getString("det");

                                if(priv==1){
                                    if(det.equals(userName)){
                                        textView.setText(textView.getText() + "\n" + nombre+ "\n" + msg);
                                    }

                                }else
                                    textView.setText(textView.getText() + "\n" + nombre+ "\n" + msg);


                            }
                            catch(JSONException e){

                                textView.setText(textView.getText() + "\n" + message);
                            }


                        }
                    });
                }


                @Override
                public void onClose(int i, String s, boolean b) {
                    Log.i("Websocket", "Closed " + s);
                }

                @Override
                public void onError(Exception e) {
                    Log.i("Websocket", "Error " + e.getMessage());
                }
            };

            mWebSocketClient.connect();

        }



        public void sendMessage() {

            EditText editText = (EditText)findViewById(R.id.message);
            CheckBox chk = (CheckBox) findViewById(R.id.chk);
            EditText dest = (EditText) findViewById(R.id.destinatario);
            String mensaje = editText.getText().toString();
            int privado;
            if (chk.isChecked()){
                privado=1;
            }else{
                privado=0;
            }
            String destino = dest.getText().toString();

            client = new JSONObject();
            try {
                client.put("id",userName);
                client.put("msg",mensaje);
                client.put("esPrivado",privado);
                client.put("dst",dest);



            } catch (JSONException e) {
                e.printStackTrace();
            }

            mWebSocketClient.send(client.toString());
            editText.setText("");
            dest.setText("");

        }


    }