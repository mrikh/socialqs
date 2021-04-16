package com.example.socialqs.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class created to validate user input.
 */
public class InputValidator {

    /**
     * Checks if the input string is correctly formatted to represent an email
     * @param s Argument containing the email string
     * @return True if it is valid, false otherwise
     */
    public boolean isValidEmail(String s){

        if (s == null) {return false;}

        final String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(s);
        return matcher.matches();
    }

    /**
     * Checks if the input string is correctly formatted to represent a password
     * @param s Argument containing the password string
     * @return True if it is valid, false otherwise
     */
    public boolean isValidPassword(String s){

        if (s == null) {return false;}

        String stripped = s.trim();
        if (stripped.length() < 8){
            return false;
        }

        return true;
    }

    /**
     * Checks if the input string is correctly formatted to represent a name
     * @param s Argument containing the name string
     * @return True if it is valid, false otherwise
     */
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
