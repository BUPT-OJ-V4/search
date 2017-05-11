namespace java com.bojv4.search

service SearchEngine {
    list<string> search(1:string query)
    bool index(1:map<string, string> params)
}
