//package bgu.spl171.net.impl;
//
//import bgu.spl171.net.impl.echo.EchoProtocol;
//import bgu.spl171.net.impl.echo.LineMessageEncoderDecoder;
//import bgu.spl171.net.srv.Reactor;
//import bgu.spl171.net.srv.Server;
//
//import java.util.HashMap;
//import java.util.Map;
//
///**
// * Created by alonam on 1/15/17.
// */
//public class ExampleMain {
//
//    public static void main(String[] args) {
//        Map<String, String> shared = new HashMap<>();
//
//        Server reactor = Server.reactor(
//                10,
//                7777,
//                () -> new ExampleProtocol(shared),
//                LineMessageEncoderDecoder::new);
//
//        reactor.serve();
//    }
//}
