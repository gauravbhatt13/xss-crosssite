package com.banking.application;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class AttackerHttpServer {

    public static void main(String[] args) throws IOException {
        System.out.println("Starting attacker website server");
        com.sun.net.httpserver.HttpServer server = com.sun.net.httpserver.HttpServer.create(new InetSocketAddress("localhost", 8002), 0);
        ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(10);

        server.createContext("/attacker", new AttackerHttpServer().new MyHttpHandler());

        server.setExecutor(threadPoolExecutor);

        server.start();

        System.out.println(" Server started on port 8002");
    }

    public class MyHttpHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange exchange) throws IOException {
          handleResponse(exchange, getDefaultPage());
        }

        private String getDefaultPage() {
            StringBuilder htmlBuilder = new StringBuilder();
            htmlBuilder.append("alert('Hacked');window.location.href = \"http://localhost:8001/banking\";");
            return htmlBuilder.toString();
        }

        private void handleResponse(HttpExchange exchange, String htmlResponse) throws IOException {
            OutputStream outputStream = exchange.getResponseBody();
            exchange.getResponseHeaders().add("Content-Type", "application/javascript");
            exchange.sendResponseHeaders(200, htmlResponse.length());
            outputStream.write(htmlResponse.getBytes());
            outputStream.flush();
            outputStream.close();
        }
    }
}
