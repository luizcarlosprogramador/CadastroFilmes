/*
*  Teste Android : ZUP
*  Programador : Luiz Carlos de Olveira Souza
*  Data 21/04/2019
* */

package com.luizcarlos.cadastrofilmes;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static android.graphics.Bitmap.CompressFormat.PNG;


public class MainActivity extends AppCompatActivity {


    public  ListView list = null;
    CustomAdapter customAdapter;



     ArrayList<MovieData> arrayList;

    private static int HTTP_COD_SUCESSO = 200;
    public static class HttpConnections {
        //método get
        public static String get(String urlString){
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String resposta = null;
            try {
                URL url = new URL(urlString);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.connect();

                InputStream in = new BufferedInputStream(urlConnection.getInputStream());

                reader = new BufferedReader(new InputStreamReader(in));
                String line = "";
                StringBuffer buffer = new StringBuffer();
                while ((line = reader.readLine()) != null){
                    buffer.append(line);
                }
                resposta = buffer.toString();
            }catch (Exception e){
                e.printStackTrace();
                resposta = e.toString();
            }finally {
                if (urlConnection != null){
                    urlConnection.disconnect();
                }
                try {
                    //  reader.close();
                }catch (Exception e){
                    resposta = "aqui---------------" + e.toString();
                    e.printStackTrace();
                }
            }
            return resposta;
        }
    }
    private Handler h = new Handler();


  /*Todo: Mostra uma listView com as ocorrencias encontradas
  * Método ainda não implementado retorna o primeiro item da lista
  * */
  public JSONObject  SelectMovieFromList(JSONObject json_response){
    JSONObject Result = null;
    try{

       JSONArray jsonArray = new JSONArray(json_response.getString("Search"));
       Result = jsonArray.getJSONObject(0);

   } catch (JSONException e) {
       e.printStackTrace();
   }

   return Result;

  };




    public void LoadListMovies(){



        String str =   getApplicationInfo().dataDir + "/db.json";
        String string = "{}";
        try {

            string  = readFile( new File(str) );
            JSONObject banco;
            banco = new JSONObject(string);
            JSONArray array = new JSONArray(banco.getString("Search"));

            for (int i = 0; i != array.length(); i++  ){
                String titulo =  array.getJSONObject(i).getString("Title");
                String foto = getApplicationInfo().dataDir + '/' + array.getJSONObject(i).getString("imdbID") + ".png";
                arrayList.add(new MovieData(titulo,"",foto));
            }

            if ((arrayList.size() > 0))  list.setAdapter(customAdapter);



        }  catch (IOException e) {

        } catch (JSONException e) {

        }
    };

    public String readFile(File f) throws IOException {
        StringBuilder b = new StringBuilder((int)f.length());
        BufferedReader in = new BufferedReader(new FileReader(f));
        try {
            String line = null;

            while ((line = in.readLine()) != null) {
                b.append(line).append("\n");
            }

        } finally {
            in.close();
        }
        return b.toString();
    }


    public void LoadPosterFromNet(final String Poster, final String imdbID) {
      new Thread() {
          Bitmap img = null;
          String Title = null;
          public void run() {

              try{

                  URL url = new URL(Poster);
                  HttpURLConnection conexao = (HttpURLConnection) url.openConnection();
                  InputStream input = conexao.getInputStream();
                  img = BitmapFactory.decodeStream(input);

                 // final TextView tv = findViewById(R.id.textView);

                  // carrega informções detalhadas do filme

                  try {

                      String FileOnDisk = getApplicationInfo().dataDir + "/db.json";

                      String info = HttpConnections.get( "http://www.omdbapi.com/?i=" + imdbID + "&apikey=f70d8c74");
                      JSONObject  DbMovieOnDisk = new JSONObject("{\"Search\":[]}");

                      try {
                          String jdbDisk = readFile(new File(FileOnDisk));
                          DbMovieOnDisk = new JSONObject(jdbDisk);
                      }catch (IOException e){

                      }

                      JSONArray arrayTmp =  DbMovieOnDisk.getJSONArray("Search");
                      JSONObject obj = new JSONObject(info);
                      Title = obj.getString("Title");

                      arrayTmp.put( obj);

                      JSONObject otmp = new JSONObject("{}").put("Search", arrayTmp);

                      FileWriter writeFile = new FileWriter(FileOnDisk);

                      writeFile.write(otmp.toString());
                      writeFile.close();

                  } catch (IOException e){

                  }catch (JSONException e){


                  }

              } catch (MalformedURLException e) {
                  e.printStackTrace();
              } catch (IOException e) {
                  e.printStackTrace();
              }


              h.post(new Runnable() {

                  @Override
                  public void run() {



                     ByteArrayOutputStream stream = new ByteArrayOutputStream();
                     img.compress(PNG, 100, stream);
                     String  imgFile = getApplicationInfo().dataDir + '/' + imdbID + ".png";

                      byte[] bytes = stream.toByteArray();
                      try {
                          FileOutputStream fos = new FileOutputStream(imgFile);
                          fos.write(bytes);
                      } catch (FileNotFoundException e) {
                      } catch (IOException e) {
                      }


                      arrayList.add( new MovieData(Title, "", imgFile ));

                      // ** Se a Lista não tiver items o Android Trava "Ainda não descobri o porque"
                      if ((arrayList.size() == 1))  list.setAdapter(customAdapter);

                      // força a Atualização da Lista
                      list.setSelection(0);
                 }
              });
          }
      }.start();
  }


   public void LoadMovieFromNet( final String r_Text ){

     new Thread() {
       @Override
       public void run() {

      
         final String response = HttpConnections.get("http://www.omdbapi.com/?s=" + r_Text + "&apikey=f70d8c74");
         // TextView tv =

           h.post(new Runnable() {
             @Override
             public void run() {
               try {
                 JSONObject json_response = new JSONObject(response);

                   if (json_response.getBoolean("Response")){
                   JSONObject MovieSelected = SelectMovieFromList(json_response);
                   String imdbID = MovieSelected.getString("imdbID");
                   String Poster = MovieSelected.getString("Poster");
                   LoadPosterFromNet(Poster, imdbID);


                   }

               } catch( JSONException e) {
                   Toast.makeText( MainActivity.this, "Houve um erro de conexão!", Toast.LENGTH_LONG).show();
               }
             }
           });
         }
       }.start();
    };

    @Override
    public boolean onCreateOptionsMenu(Menu c_menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, c_menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){

        switch (item.getItemId()){
            case R.id.novocadastro : {

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Digite o nome do filme");
                final EditText input = new EditText(this);
                input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS );
                builder.setView(input);

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String r_Text = "";
                        r_Text = input.getText().toString();
                        if (r_Text != ""){
                            LoadMovieFromNet( r_Text );
                        }
                    }
                });

                builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                     }

                });

                builder.show();

            }
        }
      return  true;
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        list =  (ListView) findViewById(R.id.ListMovies);
        arrayList = new ArrayList<MovieData>();

         customAdapter = new CustomAdapter(  MainActivity.this, arrayList);
        LoadListMovies();
       // arrayList.add(new MovieData("Teste da Lista", "", ""));
    }
}





