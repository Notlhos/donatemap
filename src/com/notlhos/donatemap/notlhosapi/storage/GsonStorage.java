package com.notlhos.donatemap.notlhosapi.storage;

import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public abstract class GsonStorage {
   protected static Gson GSON_SERIALIZE = (new GsonBuilder())
           .setPrettyPrinting()
           .disableHtmlEscaping()
           .setLenient()
           .create();

   private transient File file;

   public static <T extends GsonStorage> T load(Plugin plugin, String fileName, Class<T> clazz) {
     File file = new File(plugin.getDataFolder(), fileName);
     if (file.exists() && file.length() != 0L) {
       try {
         GsonStorage storage = GSON_SERIALIZE.fromJson(Files.newReader(file, StandardCharsets.UTF_8), clazz);
         storage.setFile(file);
         storage.init();
         return (T)storage;
       } catch (FileNotFoundException e) {
         e.printStackTrace();
       }
     }
     file.getParentFile().mkdirs();
     try {
       GsonStorage storage = (GsonStorage)clazz.newInstance();
       storage.setFile(file);
       storage.init();
       storage.save();
       return (T)storage;
    } catch (InstantiationException | IllegalAccessException e) {
       e.printStackTrace();
     }
     return null;
   }

   @SafeVarargs
   public static <T,V> Map<T,V> mapOf(Pair<T,V>... pairs){
       HashMap<T,V> map = new HashMap<>();
       for(Pair<T,V> pair : pairs){
           map.putIfAbsent(pair.key, pair.value);
       }
       return map;
   }

   public static <T,V> Pair<T,V> pairOf(T key, V value){
       return new Pair<>(key, value);
   }

   public void init() {}

   public void save() {
     try {
       Files.write(GSON_SERIALIZE.toJson(this), this.file, StandardCharsets.UTF_8);
     } catch (IOException e) {
       e.printStackTrace();
     }
   }


   public File getFile() { return this.file; }

   public void setFile(File file) { this.file = file; }

   public static class Pair<T,V>{

       private final T key;
       private final V value;

       public Pair(T key, V value) {
           this.key = key;
           this.value = value;
       }

       public T getKey() {
           return key;
       }

       public V getValue() {
           return value;
       }
   }
}