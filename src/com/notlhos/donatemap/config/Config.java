package com.notlhos.donatemap.config;

import com.notlhos.donatemap.notlhosapi.storage.GsonStorage;

import java.util.Arrays;
import java.util.Collection;

public class Config extends GsonStorage {
    private Collection<String> domains = Arrays.asList("imgur.com");

    public Collection<String> getDomains() {
        return domains;
    }
}
