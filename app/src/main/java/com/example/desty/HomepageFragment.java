package com.example.desty;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class HomepageFragment extends Fragment {

    Button mapButton;
    ListView listHomepage;

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_homepage, container, false);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mapButton = view.findViewById(R.id.button_add);
        mapButton.setOnClickListener(v -> {
            //Intent i = new Intent(getActivity(), LocationActivity.class);
            Intent i = new Intent(getActivity(), RouteActivity.class);
            startActivity(i);
        });

        listHomepage = (ListView) view.findViewById(R.id.list_homepage);

        ArrayList<String> headers = sendQuery();
        ArrayAdapter adapter = new ArrayAdapter(getContext(), android.R.layout.simple_list_item_1,headers);

        listHomepage.setAdapter(adapter);

        listHomepage.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getContext(),"clicked item "+position+ " "+ headers.get(position), Toast.LENGTH_SHORT).show();
                Intent i = new Intent(getActivity(), RouteActivity.class);
                i.putExtra("id", headers.get(position));
                startActivity(i);
            }
        });
    }

    private ArrayList<String> sendQuery(){
        ArrayList<Object[]> result = ((MainActivity) requireActivity()).getHomepageList();
        ArrayList<String> headers = new ArrayList<>();
        for (Object[] a: result){
            headers.add(a[2].toString());
        }

        return headers;
    }

}
