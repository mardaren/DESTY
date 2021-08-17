package com.example.desty;

import android.content.Intent;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import com.example.desty.ui.main.SectionsPagerAdapter;
import com.example.desty.databinding.ActivityMainBinding;

import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Stack;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    Connection db_conn = null;
    int userId = 0;
    String username;
    Image userIm;
    boolean pub = false;
    Stack fall = new Stack();
    Stack top10 = new Stack();
    Stack userList = new Stack();
    Stack published = new Stack();
    Stack point = new Stack();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userId =getIntent().getIntExtra("UserId",1);
        try {
            Connection b = new Connect().execute().get();
            String s = new MainActivity.userInfo().execute().get();
            s = new MainActivity.GetTen().execute().get();
            s = new MainActivity.UserList().execute().get();
            s = new MainActivity.Fallows().execute().get();
            new MainActivity.AddRouteToList(2,2).execute().get();
            rotaPoints(1);
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        ViewPager viewPager = binding.viewPager;
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = binding.tabs;
        tabs.setupWithViewPager(viewPager);

    }

    private class Connect extends AsyncTask<Void, Void, Connection> {
        @Override
        protected Connection doInBackground(Void... urls) {

            Connection connection = null;
            String conn_url;

            try {
                conn_url = BuildConfig.DB_URL;
                Class.forName("net.sourceforge.jtds.jdbc.Driver");
                connection = DriverManager.getConnection(conn_url);
                if (connection != null) {
                    Log.i("Connection Status", "Connected");
                } else
                    Log.i("Connection Status", "Not Connected");

            } catch (SQLException throwable) {
                throwable.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            db_conn = connection;

            return connection;
        }
    }

    public ArrayList<Object[]> search(String keyword, String country, String city) {
        AllSearch search = new AllSearch(keyword, country, city);
        search.execute();
        return search.table;
    }

    private class AllSearch extends AsyncTask<String, String, String> {
        String countryName, cityName, tag;
        ArrayList<Object[]> table = new ArrayList<>();

        @Override
        protected String doInBackground(String... strings) {
            PreparedStatement statement;
            ResultSet resultSet;
            String query;
            try {
                if (countryName != null && cityName != null && tag != null) {
                    query = "SELECT * FROM [dbo].[Route] WHERE country like ? and city like ? and route_name like ?";
                    statement = db_conn.prepareStatement(query);
                    statement.setString(1, countryName + "%");
                    statement.setString(2, cityName + "%");
                    statement.setString(3, tag + "%");
                } else if (countryName != null && cityName != null) {
                    query = "SELECT * FROM [dbo].[Route] WHERE country like ? and city like ? ";
                    statement = db_conn.prepareStatement(query);
                    statement.setString(1, countryName + "%");
                    statement.setString(2, cityName + "%");
                } else if (countryName != null && tag != null) {
                    query = "SELECT * FROM [dbo].[Route] WHERE country like ? and route_name like ?";
                    statement = db_conn.prepareStatement(query);
                    statement.setString(1, countryName + "%");
                    statement.setString(2, tag + "%");
                } else if (cityName != null && tag != null) {
                    query = "SELECT * FROM [dbo].[Route] WHERE  city like ? and route_name like ?";
                    statement = db_conn.prepareStatement(query);
                    statement.setString(1, cityName + "%");
                    statement.setString(2, tag + "%");
                } else if (countryName != null) {
                    query = "SELECT * FROM [dbo].[Route] WHERE country like ? ";
                    statement = db_conn.prepareStatement(query);
                    statement.setString(1, countryName + "%");
                } else if (cityName != null) {
                    query = "SELECT * FROM [dbo].[Route] WHERE city like ? ";
                    statement = db_conn.prepareStatement(query);
                    statement.setString(1, cityName + "%");
                } else {
                    query = "SELECT * FROM [dbo].[Route] WHERE route_name like ?";
                    statement = db_conn.prepareStatement(query);
                    statement.setString(1, tag + "%");
                }
                resultSet = statement.executeQuery();
                Object[] tuple = new Object[8];
                while (resultSet.next()) {
                    tuple[0] = resultSet.getInt(1);
                    tuple[1] = resultSet.getInt(2);
                    tuple[2] = resultSet.getString(3);
                    tuple[3] = resultSet.getString(4);
                    tuple[4] = resultSet.getFloat(5);
                    tuple[5] = resultSet.getBigDecimal(6);
                    tuple[6] = resultSet.getString(7);
                    tuple[7] = resultSet.getString(8);
                    table.add(tuple);
                }
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }

            return null;
        }

        public AllSearch(String countryName, String cityName, String tag) {
            this.countryName = countryName;
            this.cityName = cityName;
            this.tag = tag;
        }
    }


    private class userInfo extends AsyncTask<String, String, String> {


        @Override
        public String doInBackground(String... strings) {

            PreparedStatement statement;

            try {
                statement = db_conn.prepareStatement("SELECT * FROM [dbo].[User] WHERE id = ?");
                statement.setInt(1, userId);
                ResultSet resultSet = statement.executeQuery();
                while (resultSet.next()) {


                    username = resultSet.getString(2);
                }

            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
            return null;
        }


    }

    private class GetTen extends AsyncTask<String, String, String> {


        @Override
        public String doInBackground(String... strings) {

            PreparedStatement statement;


            try {


                statement = db_conn.prepareStatement("select top 10 * from Route order by views desc");

                ResultSet resultSet = statement.executeQuery();
                while (resultSet.next()) {
                    Object[] columns = new Object[8];
                    columns[0] = resultSet.getInt(1); // rota id
                    columns[1] = resultSet.getInt(2); //publisher id
                    columns[2] = resultSet.getString(3); // rota ismi
                    columns[3] = resultSet.getString(4); // tanımı
                    columns[4] = resultSet.getInt(5); // ratingi
                    columns[5] = resultSet.getInt(6); // izlenmesi
                    columns[6] = resultSet.getString(7); // ülke
                    columns[7] = resultSet.getString(8); // şehir
                    top10.add(columns);
                }

            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
            return null;
        }


    }
    //eldeki id ye sahip userın takip ettiği publisherların idsini döner
    private class Fallows extends AsyncTask<String, String, String> {


        @Override
        public String doInBackground(String... strings) {

            PreparedStatement statement;


            try {


                statement = db_conn.prepareStatement("select  * from [dbo].[Followlists] inner join [dbo].[User] on publisher_id = id where user_id = ?");
                statement.setInt(1, userId);
                ResultSet resultSet = statement.executeQuery();
                while (resultSet.next()) {
                    Object[] columns = new Object[3];
                    columns[0] = resultSet.getInt(1); // user_id
                    columns[1] = resultSet.getInt(2); //publisher id
                    columns[2] = resultSet.getString(4); //publisher name

                    fall.add(columns);
                }

            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
            return null;
        }


    }

    //eldeki kullanıcının takip ettiği listelerin idleri isimleri publisherlarının idlerini ve userın idsini döner
    private class UserList extends AsyncTask<String, String, String> {


        @Override
        public String doInBackground(String... strings) {

            PreparedStatement statement;


            try {


                statement = db_conn.prepareStatement("select  * from [dbo].[list] as l left  join [dbo].[UsersLists] as ul on ul.list_id = l.list_id where user_id = ?");
                statement.setInt(1, userId);
                ResultSet resultSet = statement.executeQuery();
                while (resultSet.next()) {
                    Object[] columns = new Object[4];
                    columns[0] = resultSet.getInt(1); // list id
                    columns[1] = resultSet.getInt(2); //publisher id
                    columns[2] = resultSet.getString(3); // list name
                    columns[3] = resultSet.getInt(5); //user id

                    userList.add(columns);
                }

            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
            return null;
        }


    }

    // eğer kullanıc publisher değil ise null doldurucak publisher ise publisher id ve list id döncek
    private class PublishedLists extends AsyncTask<String, String, String> {

        @Override
        public String doInBackground(String... strings) {

            PreparedStatement statement;


            try {


                statement = db_conn.prepareStatement("SELECT * FROM [dbo].[route] WHERE publisher_id = ?");
                statement.setInt(1, userId);
                ResultSet resultSet = statement.executeQuery();
                while (resultSet.next()) {
                    Object[] columns = new Object[8];
                    columns[0] = resultSet.getInt(1); // rota id
                    columns[1] = resultSet.getInt(2); //publisher id
                    columns[2] = resultSet.getString(3); // rota ismi
                    columns[3] = resultSet.getString(4); // tanımı
                    columns[4] = resultSet.getInt(5); // ratingi
                    columns[5] = resultSet.getInt(6); // izlenmesi
                    columns[6] = resultSet.getString(7); // ülke
                    columns[7] = resultSet.getString(8); // şehir

                    published.add(columns);
                }

            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
            return null;
        }


    }

    // rota id si alıp pointler dönücek
    private class RouteToPoint extends AsyncTask<String, String, String> {

        int routeId;
        public RouteToPoint(int routeid){
            routeId = routeid;

        }
        @Override
        public String doInBackground(String... strings) {

            PreparedStatement statement;


            try {


                statement = db_conn.prepareStatement("select  * from [dbo].[point] where route_id = ?");
                statement.setInt(1, routeId);
                ResultSet resultSet = statement.executeQuery();
                while (resultSet.next()) {
                    Object[] columns = new Object[6];
                    columns[0] = resultSet.getInt(1); // point id
                    columns[1] = resultSet.getInt(2); //rota  id
                    columns[2] = resultSet.getString(3); //isim
                    columns[3] = resultSet.getString(4); //tanım
                    columns[4] = resultSet.getBigDecimal(5); //latitude
                    columns[5] = resultSet.getBigDecimal(6); //longitude
                    point.add(columns);
                }

            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
            return null;
        }


    }

    public void rotaPoints(int routeId){
        new MainActivity.RouteToPoint(routeId).execute();
    }

    //gelen id ye sahip kişinin ismini döner
    private class IdToName extends AsyncTask<String, String, String> {

        int inputId;
        public IdToName(int inputId){
            this.inputId = inputId;

        }
        @Override
        public String doInBackground(String... strings) {

            PreparedStatement statement;
            String cevap = null;

            try {


                statement = db_conn.prepareStatement("select  username from [dbo].[user] where id = ?");
                statement.setInt(1, inputId);
                ResultSet resultSet = statement.executeQuery();
                while (resultSet.next()) {
                    Object[] columns = new Object[6];
                    cevap = resultSet.getString(1); // user name


                }

            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
            return cevap;
        }


    }

    public void idtoname(int usid){
        new MainActivity.IdToName(usid).execute();
    }

    //kullanıcı bilgilerini editler
    private class Edit extends AsyncTask<String, String, String> {
        String name ,foto;
        public Edit(String name, String foto) {
            this.name = name;
            this.foto = foto;
        }
        @Override
        public String doInBackground(String... strings) {

            PreparedStatement statement;


            try {
                if (name != null && foto != null){
                    statement = db_conn.prepareStatement("UPDATE [dbo].[User] SET username = ?, photo_url = ? WHERE id  = ?" );
                    statement.setString(1, this.name);
                    statement.setString(2, this.foto);
                    statement.setInt(3, userId);

                }
                else if (name ==null && foto != null){
                    statement = db_conn.prepareStatement("UPDATE [dbo].[User] SET  photo_url = ? WHERE id  = ?" );
                    statement.setString(1, this.foto);
                    statement.setInt(2, userId);

                }
                else {
                    statement = db_conn.prepareStatement("UPDATE [dbo].[User] SET username = ? WHERE id  = ?");
                    statement.setString(1, this.name);
                    statement.setInt(2, userId);
                }
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
            return null;
        }
    }
    public void editUser(String newname,String newfoto){
        new MainActivity.Edit(newname,newfoto).execute();
    }

    //rota bilgilerin döner rota idsi ile
    private class RotaInfo extends AsyncTask<String, String, Object[]> {
        int rotaId;
        public RotaInfo(int rotaId) {
            this.rotaId = rotaId;
        }
        @Override
        public Object[] doInBackground(String... strings) {

            PreparedStatement statement;
            Object[] columns = new Object[8];

            try {
                statement = db_conn.prepareStatement("select  * from [dbo].[Route] where route_id = ?" );
                statement.setInt(1, rotaId);
                ResultSet resultSet = statement.executeQuery();

                while (resultSet.next()) {

                    columns[0] = resultSet.getInt(1); // rota id
                    columns[1] = resultSet.getInt(2); // publisher id
                    columns[2] = resultSet.getString(3); // rota name
                    columns[3] = resultSet.getString(4); // tanım
                    columns[4] = resultSet.getInt(5); // rating
                    columns[5] = resultSet.getInt(6); // wievs
                    columns[6] = resultSet.getString(7); // ülke
                    columns[7] = resultSet.getString(8); // şehir


                }
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }

            return columns;
        }
    }
    //listin içinde bulunan rotaların idsini döner
    private class ListsRoutes extends AsyncTask<String, String, Stack> {
        int listId;
        public ListsRoutes(int listId) {
            this.listId = listId;
        }
        @Override
        public Stack doInBackground(String... strings) {

            PreparedStatement statement;

            Stack rotalist = new Stack();
            try {
                statement = db_conn.prepareStatement("select  route_id from [dbo].[ListsRoutes] where list_id = ?" );
                statement.setInt(1, listId);
                ResultSet resultSet = statement.executeQuery();

                while (resultSet.next()) {
                    Object[] columns = new Object[1];
                    columns[0] = resultSet.getInt(1); // rota id
                    rotalist.add(columns[0]);

                }
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }

            return rotalist;
        }
    }

    // listeye rota ekleme
    private class AddRouteToList extends AsyncTask<String, String, Integer> {
        int listId,rota_id;
        public AddRouteToList(int listId,int rota_id ){
            this.rota_id = rota_id;
            this.listId = listId;
        }

        @Override
        public Integer doInBackground(String... strings) {
            PreparedStatement statement;
            try {
                    // eğer bu varsa 1 dönüyor
                    //SELECT CASE WHEN EXISTS (SELECT * FROM [dbo].[ListsRoutes] WHERE listid = ? AND route_id = ?) THEN CAST(1 AS BIT) ELSE CAST(0 AS BIT) END
                    statement = db_conn.prepareStatement("INSERT INTO [dbo].[ListsRoutes] values(?,?)");
                    statement.setInt(1, listId);
                    statement.setInt(2, rota_id);
                    ResultSet resultSet = statement.executeQuery();


            } catch (SQLException throwables) {
                throwables.printStackTrace();
                return 0;
            }


            return 1;
        }
    }

    // id ile publisher bilgileri dönüyor
    private class PublisherInfo extends AsyncTask<String, String, Object[]> {
        int pubId;
        public PublisherInfo(int pubId) {
            this.pubId = pubId;
        }
        @Override
        public Object[] doInBackground(String... strings) {

            PreparedStatement statement;
            Object[] columns = new Object[8];

            try {
                statement = db_conn.prepareStatement("select  * from [dbo].[user] join [dbo].[publisher] on publisher_id = id where id = ?" );
                statement.setInt(1, pubId);
                ResultSet resultSet = statement.executeQuery();

                while (resultSet.next()) {

                    columns[0] = resultSet.getInt(1); // publisher id
                    columns[1] = resultSet.getString(2); // publisher name
                    columns[2] = resultSet.getString(3); // user mail
                    columns[3] = resultSet.getString(6); // foto urls
                    columns[4] = resultSet.getString(8); // bio
                    columns[5] = resultSet.getInt(9); // raiting

                }
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }

            return columns;
        }
    }

    // fallowa publisher ekleme
    private class AddUserToFallow extends AsyncTask<String, String, Integer> {
        int user_Id,publisher_Id;
        public AddUserToFallow(int user_Id,int publisher_Id ){
            this.publisher_Id = publisher_Id;
            this.user_Id = user_Id;
        }

        @Override
        public Integer doInBackground(String... strings) {
            PreparedStatement statement;
            try {
                // eğer bu varsa 1 dönüyor
                //SELECT CASE WHEN EXISTS (SELECT * FROM [dbo].[Followlists] WHERE user_id = ? AND publisher_id = ?) THEN CAST(1 AS BIT) ELSE CAST(0 AS BIT) END

                statement = db_conn.prepareStatement("INSERT INTO [dbo].[Followlists] values(?,?)");
                statement.setInt(1, user_Id);
                statement.setInt(2, publisher_Id);
                ResultSet resultSet = statement.executeQuery();


            } catch (SQLException throwables) {
                throwables.printStackTrace();
                return 0;
            }


            return 1;
        }
    }
    // rota tanımlarını döner
    private class RouteDcription extends AsyncTask<String, String, String> {
        int route_id;
        String cevap ="";
        public RouteDcription(int route_id ){
            this.route_id = route_id;
        }

        @Override
        public String doInBackground(String... strings) {
            PreparedStatement statement;
            try {

                statement = db_conn.prepareStatement("SELECT description FROM [dbo].[Route] where route_id = 1");
                statement.setInt(1, route_id);
                ResultSet resultSet = statement.executeQuery();
                while (resultSet.next()) {

                    cevap= resultSet.getString(1); // publisher name

                }

            } catch (SQLException throwables) {
                throwables.printStackTrace();
                return null;
            }


            return cevap;
        }
    }
}
