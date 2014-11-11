all:  
	@rm -rf bin
	@mkdir -p bin
	@javac src/*/*/*.java -classpath lib/gson-2.2.2.jar:lib/java-cup-11a.jar:lib/java-cup/11a-runtime.jar -d bin
	@echo "Success!"
clean:
	@rm -rf bin