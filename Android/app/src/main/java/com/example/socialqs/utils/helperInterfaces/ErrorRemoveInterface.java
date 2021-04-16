package com.example.socialqs.utils.helperInterfaces;

import android.text.Editable;
import android.text.TextWatcher;

/**
 * This interface has been created so that we can clear the errors whenever text changes in the input text fields.
 * It acts as a layer with predefined methods to consume when handling user input and helps limit the lines of code
 */
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
