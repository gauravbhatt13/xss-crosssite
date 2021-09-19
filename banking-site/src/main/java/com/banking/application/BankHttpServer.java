package com.banking.application;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class BankHttpServer {
    public static void main(String[] args) throws IOException {
        System.out.println("Starting banking website server");
        com.sun.net.httpserver.HttpServer server = com.sun.net.httpserver.HttpServer.create(new InetSocketAddress("localhost", 8001), 0);
        ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(10);

        server.createContext("/banking", new BankHttpServer().new MyHttpHandler());

        server.setExecutor(threadPoolExecutor);

        server.start();

        System.out.println(" Server started on port 8001");
    }

    public class MyHttpHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if("GET".equals(exchange.getRequestMethod()) && exchange.getRequestURI().getQuery() != null && exchange.getRequestURI().getQuery().contains("productName")) {
                handleResponse(exchange, getSearchPage(exchange.getRequestURI().getQuery()));
            } else {
                handleResponse(exchange, getDefaultPage());
            }
        }

        private String getDefaultPage() {
            StringBuilder htmlBuilder = new StringBuilder();
            htmlBuilder.append("<html><body><form action=\"\" method=\"get\">\n" +
                    "  <div>\n" +
                    "    <label for=\"name\">Product Name: </label>\n" +
                    "    <input size=\"100\" type=\"text\" name=\"productName\" id=\"productName\">\n" +
                    "    <input type=\"submit\" value=\"Search\">\n" +
                    "  </div>\n" +
                    "</form>").append("</body></html>");
            return htmlBuilder.toString();
        }

        private String getSearchPage(String query) {
            System.out.println("query: " + query);
            query = query.substring(query.indexOf("=") + 1);
            String searchParam = query.replace("+", " ");
            StringBuilder htmlBuilder = new StringBuilder();
            htmlBuilder.append("<html><body><form action=\"search\" method=\"get\">\n" +
                    "  <div><span>").append(searchParam).append("</span></div></body></html>");
            return htmlBuilder.toString();
        }

        private void handleResponse(HttpExchange exchange, String htmlResponse) throws IOException {
            OutputStream outputStream = exchange.getResponseBody();
            exchange.sendResponseHeaders(200, htmlResponse.length());
            outputStream.write(htmlResponse.getBytes());
            outputStream.flush();
            outputStream.close();
        }
    }
}
