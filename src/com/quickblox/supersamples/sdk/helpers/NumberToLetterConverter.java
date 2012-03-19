package com.quickblox.supersamples.sdk.helpers;

import java.util.ArrayList;

public class NumberToLetterConverter {

    private static ArrayList<String> numbersToLettersMap;

    static {
        numbersToLettersMap = new ArrayList<String>();
        numbersToLettersMap.add("a");
        numbersToLettersMap.add("b");
        numbersToLettersMap.add("c");
        numbersToLettersMap.add("d");
        numbersToLettersMap.add("e");
        numbersToLettersMap.add("f");
        numbersToLettersMap.add("g");
        numbersToLettersMap.add("h");
        numbersToLettersMap.add("i");
        numbersToLettersMap.add("j");
    }

    public static String convertNumbersToLetters(String numbers){
        String result = "";

        int len = 0;
        ArrayList<String> separetedNumbers = new ArrayList<String>();
        while(len < numbers.length()) {
            String num =  numbers.substring(len, len+1);
            separetedNumbers.add(num);
            ++len;
        }

        for(String number : separetedNumbers){
            result += numbersToLettersMap.get(Integer.parseInt(number));
        }

        return result;
    }
}
