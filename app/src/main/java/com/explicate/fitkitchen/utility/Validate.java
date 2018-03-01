package com.explicate.fitkitchen.utility;

/**
 * Created by Mahesh on 09/08/16.
 */
public class Validate {

    public static String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    public static String passwordPattern = "((?=.*\\d)(?=.*[a-z])(?=.*[@#$%]).{6,15})";
    //public static String phoneNo = "^[+]?[0-9]{10,13}$";
    public static String phoneNo = "^[789]\\d{9}$";

    //EMAIL VALIDATION
    public static boolean isValidEmail(String email){

        if(email.matches(emailPattern))
        {
            return  true;
        }

        return false;
    }

    //PASSWORD VALIDATION
    public static boolean isValidPassword(String pass)
    {
        if(pass.matches(passwordPattern))
        {
            return true;
        }
        return false;
    }

    //PHONE NO. VALIDATION
    public static boolean isValidPhone(String phone)
    {
        if(phone.matches(phoneNo))
        {
            return true;
        }
        return false;
    }




}
