package com.example.socialqs.utils.helperInterfaces;

import android.text.Editable;
import android.text.TextWatcher;

public abstract class ErrorRemoveInterface implements TextWatcher {

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after){
        //do nothing
    };

    @Override
    public void afterTextChanged(Editable s){
        //do nothing
    };
}
