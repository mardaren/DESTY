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
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;

public class SearchFragment extends Fragment{

    private String Keyword="", Country="", City="";
    private HashMap citiesCountries = new HashMap<String,String[]>();
    private Button buttonSearch;

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        buttonSearch = view.findViewById(R.id.button_search);
        buttonSearch.setOnClickListener(v -> {
            Intent i = new Intent(getActivity(), SearchResultsActivity.class);
            startActivity(i);
            getActivity().finish();
        });

        // fill **** GELISTIRILEBILIR ****
        String[] countries = {"Country", "United States", "Turkey"};
        fillMap();

        // spinners
        PSpinner spinnerCity = new PSpinner(null, view, (String[]) citiesCountries.get(""), R.id.spinner_city,
                R.layout.support_simple_spinner_dropdown_item, android.R.layout.simple_spinner_dropdown_item);
        PSpinner spinnerCountry = new PSpinner(spinnerCity, view, countries, R.id.spinner_country,
                R.layout.support_simple_spinner_dropdown_item, android.R.layout.simple_spinner_dropdown_item);

        return view;
    }

    private void fillMap(){
        String[] cities_turkey = {"City","Ankara","Istanbul","Izmir"};
        String[] cities_usa = {"City","New York","Los Angeles","Washington"};
        String[] cities_empty = {"City - Select Country"};

        citiesCountries.put("", cities_empty);
        citiesCountries.put("Turkey", cities_turkey);
        citiesCountries.put("United States", cities_usa);
    }

    private void sendQuery(){

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
                    return position != 0;
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
                            //Toast.makeText(getActivity().getApplicationContext(), child.array[0], Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                @Override
                public void onNothingSelected(AdapterView<?> parent) {}
            });
        }

    }
}
