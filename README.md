# trycljs
=========

TryClojureScript is a online ClojureScript REPL, based off of TryClojure.

Here is how we intend to do it:

1. User enters CLJS code in the browser.
2. The server compiles it to JS and returns that.
3. The browser evals the JS, producing a JS result.
4. The JS result is sent to the server for translating to a CLJS literal
   (or maybe we can do this in CLJS itself on the client?)
5. The result is displayed as CLJS.

## Usage
========

For Linux and Mac users, `lein deps, run`

Windows users will need to run the ClojureScript Windows setup instructions
to place CLJS in resources/private/cljs-compiler.
and run the Windows setup instructions.

Eventually a public version will be here or so: http://k.timmc.org:7011/trycljs

## Credits
==========

Raynes and apgwoz and so forth for the original site.

BAZNEX group members for the ClojureScript-specific stuff.

## License
==========

Licensed under the same thing Clojure is licensed under: the EPL, of which you can find a copy at the root of this directory.
