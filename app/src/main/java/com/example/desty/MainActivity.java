package com.example.desty;

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
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    Connection db_conn = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new MainActivity.Connect().execute();
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

    public ArrayList<Object[]> search(String country, String city,String keyword){
        AllSearch search = new AllSearch(country, city, keyword);

        try {
            search.execute().get();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return search.table;
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
}