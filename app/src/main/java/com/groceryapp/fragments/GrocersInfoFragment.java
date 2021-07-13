package com.groceryapp.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.groceryapp.R;

public class GrocersInfoFragment extends Fragment {

    public GrocersInfoFragment() {
    }

    public static GrocersInfoFragment newInstance(String param1, String param2) {
        GrocersInfoFragment fragment = new GrocersInfoFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_groccer_info, container, false);
    }
}