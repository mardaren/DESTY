package com.example.desty;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class HomepageFragment extends Fragment {

    private Button mapButton;
    private ListView listHomepage;
    private ArrayList<Object[]> result;
    private int user_id = -1;
    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_homepage, container, false);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // MAP BUTTON / ROUTE ACTIVITY
        mapButton = view.findViewById(R.id.button_add);
        mapButton.setOnClickListener(v -> {
            user_id = MainActivity.getInstance().userId;
            Intent i = new Intent(getActivity(), AddRouteActivity.class);
            i.putExtra("User_ID",user_id);
            startActivity(i);
        });

        // LIST
        ArrayList<String> headers = sendQuery();
        ArrayAdapter adapter = new ArrayAdapter(getContext(), android.R.layout.simple_list_item_1,headers);

        listHomepage = view.findViewById(R.id.list_homepage);
        listHomepage.setAdapter(adapter);
        listHomepage.setOnItemClickListener((parent, view1, position, id) -> {
            //Toast.makeText(getContext(),"clicked item "+position+ " "+ headers.get(position), Toast.LENGTH_SHORT).show();
            Intent i = new Intent(getActivity(), RouteActivity.class);
            i.putExtra("id", result.get(position));
            startActivity(i);
        });
    }

    private ArrayList<String> sendQuery(){
        result = ((MainActivity) requireActivity()).getHomepageList();
        ArrayList<String> headers = new ArrayList<>();
        for (Object[] a: result)
            headers.add(a[2].toString());
        return headers;
    }

}
