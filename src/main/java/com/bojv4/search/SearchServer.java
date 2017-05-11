package com.bojv4.search; /**
 * Created by liuwei on 17/5/3.
 */


import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TThreadedSelectorServer;
import org.apache.thrift.server.TThreadedSelectorServer.Args;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TNonblockingServerSocket;


public class SearchServer {
    private static int port = 8080;
    private static int tNum = 2;
    private static String index="./index";
    public static void startServer(SearchEngineImpl handler) throws Exception {
        TNonblockingServerSocket serverSocket = new TNonblockingServerSocket(port);
        Args args = new Args(serverSocket).workerThreads(5);
        args.processor(new SearchEngine.Processor<>(handler));
        args.transportFactory(new TFramedTransport.Factory());
        args.protocolFactory(new TBinaryProtocol.Factory());

        TServer server = new TThreadedSelectorServer(args);
        System.out.println("=============start server============");
        server.serve();
    }
    public static void main(String argv[]) {
        for (int i = 0; i < argv.length; i ++) {
            if (argv[i].equals("-p") || argv[i].equals("--port")) {
                port = Integer.valueOf(argv[i + 1]);
            }
            else if(argv[i].equals("-t") || argv[i].equals("--thread-number")) {
                tNum = Integer.valueOf(argv[i + 1]);
            }
            else if(argv[i].equals("-d") || argv[i].equals("--index-directory")) {
                index = argv[i + 1];
            }
        }
        System.out.println("before start");
        try{
            SearchEngineImpl handler = new SearchEngineImpl();
            startServer(handler);
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
