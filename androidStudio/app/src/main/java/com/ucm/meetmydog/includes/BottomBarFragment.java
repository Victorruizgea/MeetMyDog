package com.ucm.meetmydog.includes;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.content.Intent;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import nl.joery.animatedbottombar.AnimatedBottomBar;
import nl.joery.animatedbottombar.AnimatedBottomBar.OnTabSelectListener;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.ucm.meetmydog.activities.home.InicialActivity;
import com.ucm.meetmydog.activities.mapa.MapaActivity;
import com.ucm.meetmydog.activities.perfil.PerfilUsuarioActivity;
import com.ucm.meetmydog.R;

public class BottomBarFragment extends Fragment implements OnTabSelectListener {
    private AnimatedBottomBar bottomBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bottom_bar, container, false);
        bottomBar = view.findViewById(R.id.bottom_bar);
        bottomBar.setOnTabSelectListener(this);
        return view;
    }

    public void onTabSelected(int lastSelectedIndex, int newSelectedIndex) {
        NavController navController = Navigation.findNavController(requireActivity(), R.id.bottom_bar);
        switch (newSelectedIndex) {
            case 0:
                Intent intent1 = new Intent(requireActivity(), InicialActivity.class);
                startActivity(intent1);
                break;
            case 1:
                Intent intent2 = new Intent(requireActivity(), MapaActivity.class);
                startActivity(intent2);
                break;
            case 2:
                Intent intent3 = new Intent(requireActivity(), PerfilUsuarioActivity.class);
                startActivity(intent3);
                break;
        }
    }

    @Override
    public void onTabReselected(int i, @NonNull AnimatedBottomBar.Tab tab) {

    }

    @Override
    public void onTabSelected(int i, @Nullable AnimatedBottomBar.Tab tab, int i1, @NonNull AnimatedBottomBar.Tab tab1) {

    }
}