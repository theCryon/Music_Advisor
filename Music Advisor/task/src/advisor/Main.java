package advisor;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException, InterruptedException {
        String path = (args.length > 0 && "-access".equals(args[0])) ? args[1] : "https://accounts.spotify.com";
        Driver driver = new Driver(path);
        driver.menu();
    }
}


