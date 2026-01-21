package xyz.bobkinn.indigoi18n;

import xyz.bobkinn.indigoi18n.source.impl.MapSource;

import java.net.URI;

public class Main {
    public static void main(String[] args) {
        // create and fill source (lets use in-memory map)
        var source = new MapSource(URI.create("mem://map_source")); // use URI for distinguishing & debugging purposes
        // put basic translation text with key 'item.apple' for language id 'en'
        source.put("item.apple", "en", "Apple");
        // load source into IndigoI18n instance
        // here we're using global StringI18n instance stored in Indigo static class
        Indigo.INSTANCE.load(source);
        // once texts are loaded, we can use it using Indigo class and its static methods
        var text = Indigo.parse("en", "item.apple");
        System.out.println(text); // prints "Apple"
    }
}
