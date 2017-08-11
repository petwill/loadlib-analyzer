#!/bin/bash -ex
mvn compile package

# Use dx to convert .class to .dex
cd target/classes && ./../../dx --dex --output=output.dex com/android/vndk/Test.class
mv output.dex ../.. && cd ../..
cp output.dex classes.dex
zip output.apk classes.dex

set +e

JAR="target/loadlib-analyzer-1.0-SNAPSHOT-jar-with-dependencies.jar"

touch empty
ANDROID_JAR="android-26.jar"
zip "${ANDROID_JAR}" empty
zip -d "${ANDROID_JAR}" empty
rm empty

# java -cp ${JAR} com.android.vndk.ClassAnalyzer CLASS_NAME METHOD_NAME
java -cp ${JAR} com.android.vndk.ClassAnalyzer com.android.vndk.Test main > class.txt
# java -jar ${JAR} com.android.vndk.ApkAnalyzer ANDROID_JAR DEX_FILE/APK_FILE
java -cp ${JAR} com.android.vndk.ApkAnalyzer "${ANDROID_JAR}" output.dex > dex.txt
java -cp ${JAR} com.android.vndk.ApkAnalyzer "${ANDROID_JAR}" output.apk > apk.txt
