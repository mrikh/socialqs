package com.example.socialqs.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InputValidator {

    public boolean isValidEmail(String s){

        if (s == null) {return false;}

        final String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(s);
        return matcher.matches();
    }

    public boolean isValidPassword(String s){

        if (s == null) {return false;}

        String stripped = s.trim();
        if (stripped.length() < 8){
            return false;
        }

        return true;
    }

    public boolean isValidName(String s){

        String stripped = s.trim();

        if(stripped.isEmpty() || !stripped.contains(" ") || stripped.contains(".*\\d+.*")){
            return false;
        }
        String firstname = stripped.substring(0, stripped.indexOf(" "));
        String lastname = stripped.substring(stripped.lastIndexOf(" ")+1);

        if(firstname.length() <= 1 || lastname.length() <= 1 ){
            return false;
        }

        return true;
    }
}
