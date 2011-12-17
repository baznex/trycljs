* change site from TryCljs to TryClojureScript; reasoning being that the site is targeted 
  at beginners, who are likely to not know what cljs is
* add contributors to home.clj @ [:p.bottom "©2011 BAZNEX: contributorA, B, C ..."]
* Use CLJS on browser side to pr-str results back in s-expressions
* Switch views to use Enlive or other templating system, all that hiccup is
  giving me hiccups. ;; note I (Alex) disagree - don't see the value in using Enlive - mostly 
                     ;; because I've had such a frustrating time using Enlive, and 
                     ;; pleasant time with Hiccup
* Need a secure way of giving the CLJS compiler a *missing* tmp file to write to
