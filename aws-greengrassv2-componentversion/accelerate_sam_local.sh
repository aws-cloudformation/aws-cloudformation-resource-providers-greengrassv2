#!/bin/bash

# Unzips the jar file built by `mvn package` and updates the SAM template file to point to it,
# such that `sam local start-lambda` no longer needs to unzip the .jar file during every
# invocation.
echo "Cleaning previously unzipped .jar..."
rm -rf ./target/unzipped_jar
echo "Unzipping package .jar..."
unzip -q ./target/aws-greengrassv2-componentversion-handler-1.0-SNAPSHOT.jar -d ./target/unzipped_jar
# OSX form of the sed command. GNU form does not have the "" after -i
sed -i "" 's/aws-greengrassv2-componentversion-handler-1.0-SNAPSHOT.jar/unzipped_jar/g' template.yml
