package com.example.imagesearch.ui.common;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

public class BaseActivity extends AppCompatActivity {

    private BaseViewModel sharedViewModel; //does not work

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //sharedViewModel = new ViewModelProvider(this).get(BaseViewModel.class);
    }

    private BaseViewModel getSharedViewModel(){
        return sharedViewModel;
    }
}
