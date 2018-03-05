package com.chattylabs.module.base;


public class Tag {

    private static final int CHOP_LENGTH = 24;

    public static String make(Class<?> anyClass){
        final String className = anyClass.getSimpleName();
        return CHOP_LENGTH > className.length() ?  className : className.substring(0, CHOP_LENGTH);
    }
}
