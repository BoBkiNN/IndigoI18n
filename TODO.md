## TODO:
- abstraction for visitor to allow custom template parts
- (adventure) optional style leaking.<br>
  When enabled, inserting '&cArg' into '%s-text' will produce '<red>Arg-text'
  instead of '<red>Arg</red>-text'.<br>
  Leaked style is spread unless new style is specified.<br>
  Not sure of correct rules yet
- complete readme
- wiki:
    - troubleshooting for `No format type for String` and etc
- module with examples
- warn about missing plural strings for plural translations
- flat map translation source to avoid nested object (plurals like `key.one`, `key.many`)
- possibly move string arguments legacy converter into utility methods instead of using it in builtin AdventureI18n 
