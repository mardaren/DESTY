package com.example.desty;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.desty.profile.EditActivity;
import com.example.desty.profile.FollowListActivity;
import com.example.desty.profile.ListsActivity;

import org.jetbrains.annotations.NotNull;

public class ProfileFragment extends Fragment {

    Button buttonLists, buttonFollowList, buttonRoutes, buttonEdit;
    TextView profileName;
    ImageView imageProfile;

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //profile info
        profileName = (TextView) view.findViewById(R.id.text_profilename);
        imageProfile = (ImageView) view.findViewById(R.id.image_profile);
        String[] profileInfo = ((MainActivity) requireActivity()).getUserInfo();
        profileName.setText(profileInfo[0]);
        // eksik*******************************************************************
        //imageProfile.setImage();

        // lists button
        buttonLists = view.findViewById(R.id.button_lists);
        buttonLists.setOnClickListener(v -> {
            Intent i = new Intent(getActivity(), ListsActivity.class);
            startActivity(i);
        });

        // followlist button
        buttonFollowList = view.findViewById(R.id.button_followlist);
        buttonFollowList.setOnClickListener(v -> {
            Intent i = new Intent(getActivity(), FollowListActivity.class);
            startActivity(i);
        });

        // routes button
        buttonRoutes = view.findViewById(R.id.button_routes);
        buttonRoutes.setOnClickListener(v -> {
            Intent i = new Intent(getActivity(), ListActivity.class);
            i.putExtra("mode", "profile");
            startActivity(i);
        });

        // edit button
        buttonEdit = view.findViewById(R.id.button_edit);
        buttonEdit.setOnClickListener(v -> {
            Intent i = new Intent(getActivity(), EditActivity.class);
            startActivity(i);
        });
    }

}
