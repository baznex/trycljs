(ns trycljs.views.home
  (:use [noir.core :only [defpartial defpage]]
        [hiccup form-helpers page-helpers]))

(defpartial links []
  (unordered-list
   [(link-to "http://clojure.org" "The official Clojure website")
    (link-to "http://dev.clojure.org/display/doc/Getting+Started" "Getting started with Clojure")
    (link-to "http://groups.google.com/group/clojure" "Clojure mailing list")
    (link-to "http://java.ociweb.com/mark/clojure/article.html" "A comprehensive Clojure tutorial")
    (link-to "http://joyofclojure.com/" "The Joy of Clojure: a book by Michael Fogus and Chris Houser")
    (link-to "http://disclojure.org" "Disclojure")
    (link-to "http://planet.clojure.in" "Planet Clojure")]))

(defpartial about-html []
  [:p.bottom
   "Please note that this REPL is sandboxed, so you wont be able to do everything in it "
   "that you would in a local unsandboxed REPL. Keep in mind that this site is designed for "
   "beginners to try out Clojure and not necessarily as a general-purpose server-side REPL."]
  [:p.bottom
   "One quirk you might run into is that things you bind with def can sometimes disappear. "
   "The sandbox wipes defs if you def too many things, so don't be surprised. Furthermore, "
   "The sandbox will automatically be wiped after 15 minutes and if you evaluate more after that,"
   "It'll be in an entirely new namespace/sandbox."]
  [:p.bottom
   "You can find the site's source and such on its "
   (link-to "http://github.com/baznex/trycljs" "github")
   " page."]
  [:p.bottom
   "TryCljs is written in Clojure and JavaScript (JQuery), powered by Chris Done's "
   (link-to "https://github.com/chrisdone/jquery-console" "jquery-console")]
  [:p.bottom "Design by " (link-to "http://apgwoz.com" "Andrew Gwozdziewycz")])

(defpartial home-html []
  [:p.bottom
   "Welcome to TryCljs. See that little box up there? That's a ClojureScript repl. You can type "
   "expressions and see their results right here in your browser. We also have a brief tutorial to "
   "give you a taste of ClojureScript. Try it out by typing " [:code.expr "tutorial"] " in the console!"]
  [:p.bottom
   "Check out the site's source on " (link-to "http://github.com/baznex/trycljs" "github") "!"])

(defpartial root-html []
  (html4
   [:head
    (include-css "/resources/public/css/trycljs.css")
    (include-js "/resources/public/javascript/jquery-1.4.2.min.js"
                "/resources/public/javascript/jquery.console.js"
                "/resources/public/javascript/trycljs.js"
                "/resources/public/javascript/cljs/closure/goog/base.js")
    [:title "TryCljs"]]
   [:body
    [:div#wrapper
     [:div#content
      [:div#header
       [:h1
        [:span.logo-try "Try"] " "
        [:span.logo-clojure "Cl" [:em "j"] "s"]]]
      [:div#container
       [:div#console.console]
       [:div#buttons
        [:a#home.buttons "home"]
        [:a#links.buttons "links"]
        [:a#about.buttons.last "about"]]
       [:div#changer (home-html)]]
      [:div.footer
       [:p.bottom "©2011 " (link-to "http://baznex.herokuapp.com/" "B.A.Z.N.E.X.")]
       [:p.bottom "Built with " (link-to "http://webnoir.org" "Noir") "."]]]]]))

(defpage "/" []
  (root-html))

(defpage "/home" []
  (home-html))

(defpage "/about" []
  (about-html))

(defpage "/links" []
  (links))
