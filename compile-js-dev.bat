@echo off
setLocal EnableDelayedExpansion

set CLOJURESCRIPT_HOME=c:\dev\clojurescript\

set CLASSPATH=src\clj;%CLOJURESCRIPT_HOME%src\clj;%CLOJURESCRIPT_HOME%src\cljs
for /R "lib" %%a in (*.jar) do (
  set CLASSPATH=!CLASSPATH!;%%a
)

set REPL_CLJ="(use 'cljs.closure)(def opts {:output-to \"web/ftree-dev.js\" :output-dir \"web/js\"})(build \"src/cljs\" opts)"

java -cp "%CLASSPATH%" clojure.main -e %REPL_CLJ%
