package com.pony.appdetector;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.pony.appdetector.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    static {
        System.loadLibrary("anti_guard");
    }

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        StringBuilder sb = new StringBuilder();
        AntiGuard antiGuard = new AntiGuard();
        if (antiGuard.checkIsDebuggerConnected()) {
            sb.append("debugger detected\n");
        }
        if (antiGuard.checkRoot()) {
            sb.append("Root detected\n");
        }
        if (antiGuard.checkMagisk()) {
            sb.append("Magisk detected\n");
        }
        if (antiGuard.checkXposed()) {
            sb.append("Xposed detected\n");
        }
        if (antiGuard.checkEmulator()) {
            sb.append("Emulator detected\n");
        }
        binding.tvTips.setText(sb.toString());
    }

}