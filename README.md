
# IndigoI18n

This is a flexible Java localization and internationalization library.

## Features
- Custom output formats (String, Component, any other)
- Highly extensible architecture: provide your own formats, converters and more
- Python-like argument format options (alignment, precision, width, grouping, representations, modes)
- Custom argument converters (Control how custom object argument gets formatted)
- Simple template syntax with argument format options and inlining (embedding other text into current)
- Plurals support based on The Unicode CLDR
- Builtin common argument converters (string, numbers, Temporal, Calendar, Date)
- JSON & Properties format locale loaders.
- [Adventure](https://github.com/PaperMC/adventure) support (includes MiniMessage and Legacy serializers)
- [Paper API](https://docs.papermc.io/paper/) support to use player language

## Examples
Minimal usage:
```java
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
```

Using JSON:
To load texts from json you need to use `indigo-i18n-gson` dependency.
```java
try {
    // create source for language 'en' from jar resource 'en.json'
    var source = GsonTranslationSource.fromResource("en", getClass().getClassLoader(), "en.json");
    // load source. This opens stream and loads JSON, then parsed translations are added to IndigoI18n text map
    Indigo.INSTANCE.load(source);
} catch (URISyntaxException | FileNotFoundException e) {
    throw new RuntimeException("Failed to create source", e);
}
// now we can use texts from loaded json
var text = Indigo.parse("en", "hello");
System.out.println(text); // outputs "Hello"
```

## TODO:
- abstraction for visitor to allow custom template parts
- (adventure) optional style leaking.<br>
  When enabled, inserting '&cArg' into '%s-text' will produce '<red>Arg-text'
  instead of '<red>Arg</red>-text'.<br>
  Leaked style is spread unless new style is specified.<br>
  Not sure of correct rules yet
- readme
- wiki:
  - troubleshooting for `No format type for String` and etc
- module with examples
- maybe rename I18nFormat to renderer
