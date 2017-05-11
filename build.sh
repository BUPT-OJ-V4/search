#########################################################################
# File Name: build.sh
# Author: ma6174
# mail: ma6174@163.com
# Created Time: 四  5/11 19:05:19 2017
#########################################################################
#!/bin/bash

if [[ -f ./release ]]; then
    rm -rf ./release
fi

mvn clean
mvn package -U
mkdir -p ./release
mv ./target/search-server-1.0.jar ./release/
mv src/main/resources/* ./release/
