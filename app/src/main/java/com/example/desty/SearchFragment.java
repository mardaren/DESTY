package com.example.desty;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentMap;

public class SearchFragment extends Fragment implements View.OnClickListener {

    private String Keyword="", Country="", City="";
    private HashMap<String,String[]> citiesCountries = new HashMap<>();
    private Button buttonSearch;
    private EditText editText_keyword;
    private ArrayList<Object[]> result;

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // keyword
        editText_keyword = view.findViewById(R.id.editKeyword);
        editText_keyword.setOnClickListener(this);

        // fill **** GELISTIRILEBILIR ****
        String[] countries = {"Country", "United States", "Turkey"};
        fillMap();

        // spinners
        PSpinner spinnerCity = new PSpinner(null, view, (String[]) citiesCountries.get(""), R.id.spinner_city,
                R.layout.support_simple_spinner_dropdown_item, android.R.layout.simple_spinner_dropdown_item);
        PSpinner spinnerCountry = new PSpinner(spinnerCity, view, countries, R.id.spinner_country,
                R.layout.support_simple_spinner_dropdown_item, android.R.layout.simple_spinner_dropdown_item);

        // SEARCH BUTTON
        buttonSearch = view.findViewById(R.id.button_search);
        buttonSearch.setOnClickListener(v -> {
            //query phase
            ArrayList<Object[]> result = sendQuery();

            // page phase
            Intent i = new Intent(getActivity(), SearchResultsActivity.class);
            i.putExtra("result", result);
            startActivity(i);
        });
    }

    private void fillMap(){
        String[] cities_turkey = {"City","Ankara","Istanbul","Izmir"};
        String[] cities_usa = {"City","New York","Los Angeles","Washington"};
        String[] cities_empty = {"City - Select Country"};

        citiesCountries.put("", cities_empty);
        citiesCountries.put("Turkey", cities_turkey);
        citiesCountries.put("United States", cities_usa);
    }

    private ArrayList<Object[]> sendQuery(){
        String key,countryName,cityName;
        key = "" + editText_keyword.getText();
        key = (key.equals("Keyword")||key.equals(""))?null:key;
        countryName = (Country.equals("")||Country.equals("Country"))?null:Country;
        cityName = (City.equals("") || City.equals("City") || City.equals("City - Select Country"))?null:City;

        System.out.println(key+" "+countryName+" "+cityName);
        result = ((MainActivity) requireActivity()).search(countryName,cityName,key);
        return result;
    }

    @Override
    public void onClick(View v) {
        editText_keyword.setText("");
    }

    private class PSpinner {

        private View view;
        private int spinner_id, layout_id, a_layout_id;
        private String[] array;
        private PSpinner child;

        private Spinner spinner;
        private ArrayAdapter adapter;

        public String result;


        public PSpinner(PSpinner child, View view, String[] array, int spinner_id, int layout_id, int a_layout_id){
            this.view = view;
            this.spinner_id = spinner_id;
            this.layout_id = layout_id;
            this.a_layout_id = a_layout_id;
            this.array = array;
            this.child = child;

            spinner = view.findViewById(spinner_id);
            fillAdapter();
            setAdapter();
            setOnItemSelectedListener();
        }

        private void fillAdapter(){
            adapter = new ArrayAdapter<String>(getContext(),layout_id, array){
                @Override
                public boolean isEnabled(int position){
                    return true;
                }
                @Override
                public View getDropDownView(int position, View convertView,
                                            ViewGroup parent) {
                    View view = super.getDropDownView(position, convertView, parent);
                    TextView tv = (TextView) view;
                    if(position == 0){
                        // Set the hint text color gray
                        tv.setTextColor(Color.GRAY);
                    }
                    else {
                        tv.setTextColor(Color.BLACK);
                    }
                    return view;
                }
            };
        }

        private void setAdapter(){
            adapter.setDropDownViewResource(a_layout_id);
            spinner.setAdapter(adapter);
        }

        private void setOnItemSelectedListener(){
            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    String selectedItemText = (String) parent.getItemAtPosition(position);
                    // If user change the default selection
                    // First item is disable and it is used for hint
                    if(position > 0){
                        // Notify the selected item text
                        result = parent.getItemAtPosition(position).toString();
                        if(child != null){
                            child.array = (String[]) citiesCountries.get(result);
                            child.fillAdapter();
                            child.setAdapter();
                            child.setOnItemSelectedListener();
                            Country = result;
                        }
                        else{
                            City = result;
                        }
                    }
                }
                @Override
                public void onNothingSelected(AdapterView<?> parent) {}
            });
        }

    }
}
