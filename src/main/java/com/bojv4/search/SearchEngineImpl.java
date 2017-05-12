package com.bojv4.search;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.document.NumericDocValuesField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.codecs.Codec;

import org.apache.thrift.TException;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import java.nio.file.Paths;
import java.util.Properties;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
/**
 * Created by liuwei on 17/5/4.
 */
public class SearchEngineImpl implements SearchEngine.Iface{

    private static String storage_dir;
    private static Directory directory;
    private static IndexWriter writer ;
    private static volatile long buffer_size = 0;
    private static ReferenceManager<IndexSearcher> searcherManager;
    static {
        Analyzer analyzer = new StandardAnalyzer();
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        config.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
        config.setRAMBufferSizeMB(256.0);
        try {
            Properties properties = new Properties();
            properties.load(SearchEngineImpl.class.getClassLoader().getResourceAsStream("config.properties"));
            storage_dir = properties.getProperty("lucene.index");
            System.out.println("index===============" + storage_dir);
            directory = FSDirectory.open(Paths.get(storage_dir));
            writer = new IndexWriter(directory, config);
            searcherManager = new SearcherManager(writer, true, true, new SearcherFactory());
            ControlledRealTimeReopenThread th = new ControlledRealTimeReopenThread<>(writer, searcherManager, 15, 10);
            th.start();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public SearchEngineImpl() {
    }

    @Override
    public boolean index(Map<String, String> params) throws TException {
        String content = params.get("content");
        try{
            Document doc = new Document();
            doc.add(new StringField("title", params.get("title"), Field.Store.YES));
            doc.add(new NumericDocValuesField("key", Integer.valueOf(params.get("key"))));
            doc.add(new TextField("content", content, Field.Store.YES));
            writer.addDocument(doc);
            writer.commit();
        } catch (Exception e) {
            return false;
        }
        return true;
    }


    @Override
    public List<String> search(String query) throws TException{
        List<String> ans = new ArrayList<>();
        try{
            IndexSearcher searcher = searcherManager.acquire();
            System.out.println(directory.toString());
            Analyzer analyzer = new StandardAnalyzer();
            QueryParser parser = new QueryParser("content", analyzer);
            Query q = parser.parse("text");
            TopDocs result = searcher.search(q, 100);
            ScoreDoc[] hits = result.scoreDocs;
            for (int i = 0; i < hits.length; i ++) {
                Document hitDoc = searcher.doc(hits[i].doc);
                System.out.println(hitDoc.get("title"));
            }
            searcherManager.release(searcher);
        } catch (Exception e) {
            System.out.println("Caught exception when search");
            System.out.println(e);
        }
        return ans;
    }
}
