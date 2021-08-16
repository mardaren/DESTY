package com.example.desty;

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

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
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
    //Stack top10 = new Stack();
    Stack userList = new Stack();
    Stack published = new Stack();

    private static MainActivity instance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userId =getIntent().getIntExtra("UserId",1);
        try {
            Connection b = new Connect().execute().get();
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
        instance = this;
    }

    public static MainActivity getInstance() {
        return instance;
    }

    ///////////////////
    // QUERY METHODS //
    ///////////////////

    //*****NULL KONTROLLERI YAPILMALI***************************************************

    public ArrayList<Object[]> getHomepageList(){
        GetTen t = new GetTen();
        try {
            t.execute().get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return t.table;
    }

    public ArrayList<Object[]> search(String country, String city,String keyword){
        AllSearch search = new AllSearch(country, city, keyword);
        try {
            search.execute().get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return search.table;
    }

    public String[] getUserInfo(){
        UserInfo info = new UserInfo();

        try {
            info.execute().get();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return info.result;
    }

    public ArrayList<Object[]> getUserLists(){
        UserLists ul = new UserLists();
        try {
            ul.execute().get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ul.table;
    }

    public ArrayList<Object[]> getFollowlist(){
        Follows f = new Follows();
        try {
            f.execute().get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return f.table;
    }

    public ArrayList<Object[]> getProfileRoutes(){
        PublishedRoutes pl = new PublishedRoutes();
        try {
            pl.execute().get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return pl.table;
    }

    ////////////////////////////////
    /// CONNECTION-QUERY CLASSES ///
    ////////////////////////////////

    private class Connect extends AsyncTask<Void, Void, Connection> {
        @Override
        protected Connection doInBackground(Void... urls) {

            Connection connection = null;
            String conn_url;

            try {
                conn_url = BuildConfig.db_url;
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
            db_conn=connection;

            return connection;
        }
    }

    private class GetTen extends AsyncTask<String, String, String> {

        ArrayList<Object[]> table;

        public GetTen(){
            table = new ArrayList<Object[]>();
        }

        @Override
        public String doInBackground(String... strings) {

            PreparedStatement statement;
            try {
                statement = db_conn.prepareStatement("select top 10 * from [dbo].[Route] order by views desc");

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
                    table.add(columns);
                }

            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
            return null;
        }
    }

    private class  AllSearch extends AsyncTask<String, String, String> {
        String countryName,cityName,tag;
        ArrayList<Object[]> table;

        @Override
        protected String doInBackground(String... strings) {
            PreparedStatement statement;
            ResultSet resultSet;
            String query;

            try {
                if(countryName!=null && cityName!=null && tag!=null){
                    query = "SELECT * FROM [dbo].[Route] WHERE country like ? and city like ? and route_name like ?";
                    statement = db_conn.prepareStatement(query);
                    statement.setString(1, countryName+"%");
                    statement.setString(2, cityName+"%");
                    statement.setString(3, tag+"%");
                }
                else if (countryName != null && cityName != null){
                    query = "SELECT * FROM [dbo].[Route] WHERE country like ? and city like ? ";
                    statement = db_conn.prepareStatement(query);
                    statement.setString(1, countryName+"%");
                    statement.setString(2, cityName+"%");
                }
                else if (countryName != null && tag != null){
                    query = "SELECT * FROM [dbo].[Route] WHERE country like ? and route_name like ?";
                    statement = db_conn.prepareStatement(query);
                    statement.setString(1, countryName+"%");
                    statement.setString(2, tag+"%");
                }
                else if(countryName != null){
                    query = "SELECT * FROM [dbo].[Route] WHERE country like ? ";
                    statement = db_conn.prepareStatement(query);
                    statement.setString(1, countryName+"%");
                }
                else if(tag!=null){
                    query = "SELECT * FROM [dbo].[Route] WHERE route_name like ?";
                    statement = db_conn.prepareStatement(query);
                    statement.setString(1, tag+"%");
                }
                else {
                    return null;
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

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }

        public AllSearch(String countryName,String cityName,String tag){
            this.countryName = countryName;
            this.cityName = cityName;
            this.tag = tag;
            this.table = new ArrayList<Object[]>();
        }
    }

    private class UserInfo extends AsyncTask<String, String, String> {

        String[] result = new String[2];

        @Override
        public String doInBackground(String... strings) {
            PreparedStatement statement;

            try {
                statement = db_conn.prepareStatement("SELECT * FROM [dbo].[User] WHERE id = ?");
                statement.setInt(1, userId);
                ResultSet resultSet = statement.executeQuery();
                while (resultSet.next()) {

                    result[0] = resultSet.getString(2);
                    result[1] = resultSet.getString(6);
                }

            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
            return null;
        }


    }

    private class UserLists extends AsyncTask<String, String, String> {

        ArrayList<Object[]> table;

        public UserLists(){
            table = new ArrayList<>();
        }

        @Override
        public String doInBackground(String... strings) {
            PreparedStatement statement;
            try {
                statement = db_conn.prepareStatement("select  * from [dbo].[list] as l left  join [dbo].[UsersLists] as ul on ul.list_id = l.list_id where user_id = ?");
                statement.setInt(1, userId);
                ResultSet resultSet = statement.executeQuery();
                while (resultSet.next()) {
                    Object[] columns = new Object[3];
                    columns[0] = resultSet.getInt(1); // list id
                    columns[1] = resultSet.getInt(2); //publisher id
                    columns[2] = resultSet.getInt(5); //user id

                    table.add(columns);
                }

            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
            return null;
        }


    }

    private class Follows extends AsyncTask<String, String, String> {

        ArrayList<Object[]> table;

        public Follows(){
            table = new ArrayList<>();
        }

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
                    table.add(columns);
                }

            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
            return null;
        }
    }

    // eğer kullanıc publisher değil ise null doldurucak publisher ise publisher id ve list id döncek
    private class PublishedRoutes extends AsyncTask<String, String, String> {

        ArrayList<Object[]> table;

        public PublishedRoutes(){
            table = new ArrayList<>();
        }

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
                    table.add(columns);
                }

            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
            return null;
        }


    }

    // rota id si alıp pointler dönücek
    private class routeToPoint extends AsyncTask<String, String, String> {

        int routeId;
        public routeToPoint(int routeid){
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
                    //point.add(columns);
                }

            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
            return null;
        }


    }

    public void rotaPoints(int routeId){
        new MainActivity.routeToPoint(routeId).execute();
    }

    private class idToName extends AsyncTask<String, String, String> {

        int inputId;
        public idToName(int inputId){
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
                    cevap = resultSet.getString(1); // point id


                }

            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
            return cevap;
        }


    }

    public void idtoname(int usid){
        new MainActivity.idToName(usid).execute();
    }

    //kullanıcı bilgilerini editler
    private class edit extends AsyncTask<String, String, String> {
        String name ,foto;
        public edit(String name, String foto) {
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
        new MainActivity.edit(newname,newfoto).execute();
    }

}