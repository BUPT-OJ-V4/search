#!/usr/bin/env bash
if [[ $# -ge 1 ]]; then
java -cp $1/conf:$1/bin/search-server-1.0.jar com.bojv4.search.SearchServer -p 8080
else
java -cp ./conf:./bin/search-server-1.0.jar com.bojv4.search.SearchServer -p 8001
fi
