package server;

public class ServerLauncher {
    public static final String SERVER_ADDRESS = "localhost";
    public static final int SERVER_PORT = 1337;
    public final static int PORT = 1337;

    public static void main(String[] args) {
        Server server;
        try {
            server = new Server(PORT);
            System.out.println("Server is running...");
            server.run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
