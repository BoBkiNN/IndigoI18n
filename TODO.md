## TODO:
- abstraction for visitor to allow custom template parts
- (adventure) optional style leaking.<br>
  When enabled, inserting '&cArg' into '%s-text' will produce '<red>Arg-text'
  instead of '<red>Arg</red>-text'.<br>
  Leaked style is spread unless new style is specified.<br>
  Not sure of correct rules yet
- wiki:
    - move and separate some readme sections into wiki
- module with examples
- better context injection methods in mixins
- warn about missing plural strings for plural translations
- flat map translation source to avoid nested object (plurals like `key.one`, `key.many`)
- Translation cache pre-computes cache for entire string, while in runtime lookup for this key will
be performed using other strings produced by renderer. For example: translation cache is created, 
then renderer uses only parts of original text to lookup for parsed entry. 
In result wrong or unused parsed entry is stored in cache. Should cache be pre-computed for each renderer available?
