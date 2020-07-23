package com.omerfpekgoz.mypositionproject.utils;

import java.util.Random;

public class RandomName {

    public static String getSlatString(){
        String SALTCHARS="ABCDEFGHIJKLMNOPQRSTUVWYZ1234567890";
        StringBuilder salt=new StringBuilder();
        Random random=new Random();

        while (salt.length()<18){
            int index=(int) (random.nextFloat() * SALTCHARS.length());
            salt.append(SALTCHARS.charAt(index));
        }
        String saltStr=salt.toString();
        return saltStr;
    }

}
