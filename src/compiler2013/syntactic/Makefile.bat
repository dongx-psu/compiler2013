java -jar ../../../lib/java-cup-11a.jar -parser Parser -symbols Symbols -expect 10 -interface < spcompiler.cup
java -cp ../../../lib/JFlex.jar JFlex.Main spcompiler.flex