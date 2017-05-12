#########################################################################
# File Name: build.sh
# Author: ma6174
# mail: ma6174@163.com
# Created Time: 四  5/11 19:05:19 2017
#########################################################################
#!/bin/bash

if [[ -x "release" ]]; then
    echo "remove release"
    rm -rf ./release
fi

rm *.tar.gz

mvn clean
mvn package -U
mkdir -p ./release
cp -r src/main/resources/* ./release/
cp -rf ./target/search-server-1.0.jar ./release/bin/
tar -cvzf search-server.tar.gz ./release
