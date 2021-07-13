package com.groceryapp.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.groceryapp.R;
import com.groceryapp.database.MyRoomDatabase;
import com.groceryapp.database.entities.User;
import com.groceryapp.utility.FirebaseUtils;

import org.jetbrains.annotations.NotNull;

public class SettingFragment extends Fragment {


    ClickListener listener;
    LinearLayout editProfile, changeToGrocer, LogOut;

    public SettingFragment(ClickListener listener) {
        this.listener = listener;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setting, container, false);

        view.findViewById(R.id.editProfile)
                .setOnClickListener(v -> listener.onClick(0));

        changeToGrocer = view.findViewById(R.id.changeToGrocer);
        changeToGrocer.setOnClickListener(v -> listener.onClick(1));

        view.findViewById(R.id.logOut)
                .setOnClickListener(v -> listener.onClick(2));


        return view;
    }

    public interface ClickListener {
        public void onClick(int actionId);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        User user = MyRoomDatabase.getUserDao(requireActivity()).getUser(FirebaseUtils.getUserId());

        if (user != null && user.is_shop)
            changeToGrocer.setVisibility(View.GONE);
    }
}