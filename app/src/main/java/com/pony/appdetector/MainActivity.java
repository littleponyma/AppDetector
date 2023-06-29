package com.pony.appdetector;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.pony.appdetector.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        StringBuilder sb =new StringBuilder();
        AntiGuard antiGuard = new AntiGuard();
        if (antiGuard.checkIsDebuggerConnected()) {
            sb.append("Debugger connected\n");
        }
        if (antiGuard.isRoot()) {
            sb.append("Rooted\n");
        }
        if (antiGuard.checkXposed()) {
            sb.append("Xposed detected\n");
        }
        binding.tvTips.setText(sb.toString());
    }

}