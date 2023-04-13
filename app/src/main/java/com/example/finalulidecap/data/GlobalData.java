package com.example.finalulidecap.data;

import java.util.Dictionary;
import java.util.Enumeration;

public class GlobalData {
    // create a protected dictonary
    protected static Dictionary<String, Object> data;

    // constructor
    public GlobalData() {
        data = new Dictionary<String, Object>() {
            @Override
            public int size() {
                return 0;
            }

            @Override
            public boolean isEmpty() {
                return false;
            }

            @Override
            public Enumeration<String> keys() {
                return null;
            }

            @Override
            public Enumeration<Object> elements() {
                return null;
            }

            @Override
            public Object get(Object o) {
                return null;
            }

            @Override
            public Object put(String s, Object o) {
                return null;
            }

            @Override
            public Object remove(Object o) {
                return null;
            }
        };
    }

    // add a key value pair to the dictionary
    public static void add(String key, Object value) {
        data.put(key, value);
    }

    // get a value from the dictionary
    public static Object get(String key) {
        return data.get(key);
    }
}
