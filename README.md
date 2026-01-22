
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

## Core of system
The Core module defines system layers and abstract classes/interfaces with default implementations.
Going from top to bottom, these layers and abstract classes are:
- I18nMixin: defines high-level methods that are attached to IndigoI18n.
- IndigoI18n: empty entrypoint class which can be populated or subclassed for customization
  - TranslationSource: source that can load translation texts into IndigoI18n from JSON and other formats
  - TranslationResolver: defines strategy used to lookup translation by key
- Renderer: renderer controls how to work with input string: how to treat it, what template formatter to use
  - TemplateCache: caches parsed templates and invokes parser
  - ITemplateParser: parses input template text into parts
- TemplateFormatter: template formatter takes template parts, 
  replaces arguments and builds resulting object from them
- ArgumentConverter: implementations of this interface controls how different types of arguments are converted

## Template Syntax

The default template parser supports **placeholder substitution** and **inline translations** with a flexible 
syntax inspired by printf-style formatting and custom templates.

### 1. **Plain Text**

Any text outside of `%` sequences is treated as plain text.

```text
Hello, world! → "Hello, world!"
```

### 2. **Simple Argument Placeholder**

* **Syntax:** `%s`
* **Description:** Represents the next sequential argument. The parser automatically assigns it the next index.

```text
"Hello, %s!" → first argument replaces %s
```

### 3. **Indexed Argument Placeholder**

* **Syntax:** `%n` where `n` is a 1-based argument index.
* **Description:** Explicitly references an argument at the given index.

```text
"Hello, %1!" → replaces with the first argument
```

### 4. **Advanced Argument Placeholder**

* **Syntax:** `%{...}`
* **Description:** Enclosed in `%{` and `}`, these placeholders can include optional index, sequential marker, conversion, and formatting specifications.

#### Components

| Component     | Description                                                           |
|---------------|-----------------------------------------------------------------------|
| `index`       | Optional, a number starting from 1, explicitly selects an argument    |
| `'s'`         | Optional, indicates the next sequential argument (equivalent to `%s`) |
| `!conversion` | Optional, a single character representing a conversion                |
| `:formatSpec` | Optional, defines formatting rules (via `FormatPattern`)              |

#### Examples

```text
"%{1!r}"   → argument 1 with conversion 'r'
"%{s!r}"   → next sequential argument with conversion 'r'
"%{:0.2f}" → next sequential argument with format specifier 0.2f
"%{s}"     → next sequential argument, default format
"%{}"      → equivalent to "%s", next sequential argument
"%{:}"     → equivalent to "%s", next sequential argument
```

*Notes:*

* Explicit indexes (`1`, `2`, …) override sequential indexing.
* `'s'` is used to explicitly indicate sequential arguments inside `%{...}`.
* If no index or `'s'` is specified, the argument will use the next sequential index.
* `%{}`, `%{s}`, and `%{:}` are all functionally equivalent to `%s`.

### 5. **Inline Translation**

* **Syntax:** `%{t:key[:depth][:lang]}`
* **Description:** Represents a translation entry with optional recursion depth and language override.

#### Components

| Component | Description                                                                                      |
|-----------|--------------------------------------------------------------------------------------------------|
| `key`     | Translation key (required)                                                                       |
| `depth`   | Optional, maximum depth of recursion (default = 1). Applied only if not inside recursion already |
| `lang`    | Optional, language override                                                                      |

#### Examples

```text
"%{t:greeting}"         → translate key "greeting"
"%{t:greeting:2}"       → translate key "greeting" with depth = 2
"%{t:greeting::fr}"     → translate key "greeting" in French
"%{t:greeting:3:es}"    → translate key "greeting", depth 3, Spanish
```

### 6. **Escaping Percent Sign**

* **Syntax:** `%%`
* **Description:** Represents a literal percent sign.

```text
"Progress: 50%%" → "Progress: 50%"
```

### **Argument format syntax**

Using advanced argument syntax, formatting rules can be changed for that argument.
Format syntax is almost same as 
[Python 3.11 Format Specification Mini-Language](https://docs.python.org/3.11/library/string.html#formatspec)
so you can use it for syntax reference.

Differences are:
* `z` flag are not supported
* each argument converter can handle this format with its own rules so illegal combinations isn't checked when parsing.

## Argument converters rules
There are several common argument converters builtin into core module.
They implement how `FormatPattern` is used to format input argument.

\<TODO>

## Extending

You can extend different parts of system by subclassing and overriding methods
or adding new converters, renderers etc.

### Adding argument converter
When you obtain an instance of `TemplateFormatter` you can use method `putConverter` to attach
your implementation of `ArgumentConverter` for some class. Example:
```java
// obtain template formatter 
TemplateFormatter<String> f = ...
// argument converter that returns MyObject value wrapped in square brackets
ArgumentConverter<MyObject, String> converter = (ctx, obj, format) -> "["+obj.value+"]";
// use this converter for MyObject class
f.putConverter(MyObject.class, converter);
```

### Adding renderer type

```java
// obtain IndigoI18n instance
var i = new IndigoI18n();
// create render type or use existing
var type = new RenderType<>(String.class);
// add StringRenderer keyed with this type
i.addRenderer(type, StringRenderer::new);
// use this renderer using render type
i.parse(type, null, "en", "test", List.of());
```

Example simple implementation is
[StringRenderer](core/src/main/java/xyz/bobkinn/indigoi18n/render/impl/StringRenderer.java).<br>
To create your own Renderer you need to subclass
[Renderer<O>](core/src/main/java/xyz/bobkinn/indigoi18n/render/Renderer.java) and implement abstract
methods. Type variable O is output type of you renderer, for example String or your custom text.


## Custom I18n

You can customize high-level api by adding renderers and mixins to IndigoI18n subclass. Usual steps are:

1. Subclass IndigoI18n or its subclass:
    ```java
      public class MyI18n extends IndigoI18n {
      }
    ```
2. Add and configure renderers:
    Override `addDefaultRenderers` method to add your renderers
    ```java
        @Override
        protected void addDefaultRenderers() {
            // add string renderer with key RenderType.STRING
            addRenderer(RenderType.STRING, StringRenderer::new);
        }
    ```
3. Add mixins:
    Mixins are high-level interfaces with all default methods. <br>
    They provide useful shortcuts for different render types and other common-used utilities.<br>
    You can use them or create your own, for example they can be used to: 
      - retrieve language from thread local or system environment
      - accept text viewer and extract language from it
      - alter created context with own data
    ```java
      public class MyI18n extends IndigoI18n implements StringI18nMixin
    ```
   The StringI18nMixin adds new `parse(...)` methods that uses `RenderType.STRING`.<br>
   Note that if mixin uses render type which is not registered, runtime errors like
   `Unknown renderer for output String` will occur later.
4. Setup:
    Before using your instance, you must call `setup()` method
    to add renderers. Also, you probably want to load some sources.
    ```java
    var i18n = new MyI18n();
    // invoke setup to register renderers
    i18n.setup();
    // obtain source
    TranslationSource source = ...
    // load source
    i18n.load(source);
    // use high-level methods provided by mixin
    i18n.parse("en", "key", "arg1");
    ```
5. (Optional) Static shortcut:
    You can generate static shortcut using @GenStaticDefault annotation and annotation processor.<br>
    ```java
    // specify generated class name and static method to use
    @GenStaticDefault(name = "I18n", creator = "create")
    public class MyI18n extends IndigoI18n implements StringI18nMixin {
    
        // static method that is called by generated class to obtain instance.
        // must return instance of this class and take no args
        public static MyI18n create() {
            var r = new MyI18n();
            r.setup(); // do not forget to setup newly created instance
            return r;
        }
    }
    ```
   Now you can use: `I18n.parse("en", "key", "arg1")`.<br>
   Generated class is located in same package and have `public static final INSTANCE` 
   field with type of your class
