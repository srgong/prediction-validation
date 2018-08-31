#!/bin/bash
#
# Use this shell script to compile (if necessary) your code and then execute it. Below is an example of what might be found in this file if your program was written in Python
#
#python ./src/prediction-validation.py ./input/window.txt ./input/actual.txt ./input/predicted.txt ./output/comparison.txt

javac ./src/main/java/Driver.java
java Driver ./src/main/resources/input/actual.txt ./src/main/resources/input/predicted.txt ./src/main/resources/input/window.txt ./src/main/resources/output/comparison.txt
