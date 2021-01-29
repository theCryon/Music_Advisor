package advisor;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Scanner;

public class Driver {
    Scanner scanner = new Scanner(System.in);
    static String    authCode = "";
    private final String serverPath;

    public Driver(String serverPath) {
        this.serverPath = serverPath;
    }

    public void menu() throws IOException, InterruptedException {
        boolean field = false;
        while (true) {
            switch (scanner.next()) {
                case "auth":
                    authorisation();
                    access();
                    field = true;
                    break;
                case "featured":
                    featured(field);
                    break;
                case "new":
                    newReleases(field);
                    break;
                case "categories":
                    categories(field);
                    break;
                case "playlists":
                    playlists(field);
                    break;
                case "exit":
                    System.out.println("---GOODBYE!---");
                    return;
            }
        }
    }



    public void authorisation() throws IOException, InterruptedException {
        HttpServer server = HttpServer.create();
        server.bind(new InetSocketAddress(8080), 0);
        server.start();
        String auth = serverPath+"/authorize?client_id=b690f319d3754e56a049a6601fc392f1&response_type=code&redirect_uri=http://localhost:8080&state=34fFs29kd09&show_dialog=true";
        System.out.println("use this link to request the access code:");
        System.out.println(auth);
        server.createContext("/", new HttpHandler() {
            @Override
            public void handle(HttpExchange exchange) throws IOException {
                while(authCode.equals("")) {
                    authCode = exchange.getRequestURI().getQuery();
                    System.out.println("waiting for code...");
                    if (authCode.equals("")) {
                        String errorString = "Authorization code not found. Try again.";
                        exchange.sendResponseHeaders(200, errorString.length());
                        exchange.getResponseBody().write(errorString.getBytes());
                    } else {
                        System.out.println("Got the code. Return back to your program.");
                        server.stop(1);
                    }
                exchange.getResponseBody().close();
                authCode = authCode.substring(0, authCode.indexOf("&"));
                }
            }
        });
    }

    public void access() throws IOException, InterruptedException {
        System.out.println("making http request for access_token...");
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .header("Content-Type", "application/x-www-form-urlencoded")
                .uri(URI.create(serverPath+"/api/token"))
                .POST(HttpRequest.BodyPublishers.ofString("grant_type=authorization_code&"
                        +authCode
                        +"&redirect_uri=http://localhost:8080&"
                        +"client_id=b690f319d3754e56a049a6601fc392f1&"
                        +"client_secret=940d0284545345fa818e72840c9630a1"))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println("response: " + (response != null ? response.body() : null));
        System.out.println("---SUCCESS---");
    }

    public boolean checkAuth(boolean field) {
        if (!field) {
            System.out.println("Please, provide access for application.");
            return false;
        } else {
            return true;
        }
    }

    public void featured(boolean field) {
        if (checkAuth(field)) {
            System.out.println("---FEATURED---\n" +
                    "Mellow Morning\n" +
                    "Wake Up and Smell the Coffee\n" +
                    "Monday Motivation\n" +
                    "Songs to Sing in the Shower");
        }
    }

    public void newReleases(boolean field) {
        if (checkAuth(field)) {
            System.out.println("---NEW RELEASES---\n" +
                    "Mountains [Sia, Diplo, Labrinth]\n" +
                    "Runaway [Lil Peep]\n" +
                    "The Greatest Show [Panic! At The Disco]\n" +
                    "All Out Life [Slipknot]");
        }
    }

    public void categories(boolean field) {
        if (checkAuth(field)) {
            System.out.println("---CATEGORIES---\n" +
                    "Top Lists\n" +
                    "Pop\n" +
                    "Mood\n" +
                    "Latin");
        }
    }

    public void playlists(boolean field) {
        if (checkAuth(field)) {
            String name = scanner.next();
            if ("Mood".equals(name)) {
                System.out.println("---MOOD PLAYLISTS---\n" +
                        "Walk Like A Badass  \n" +
                        "Rage Beats  \n" +
                        "Arab Mood Booster  \n" +
                        "Sunday Stroll");
            }
        }
    }
}


