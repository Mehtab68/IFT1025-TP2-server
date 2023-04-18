import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;
import static server.ServerLauncher.SERVER_ADDRESS;
import static server.ServerLauncher.SERVER_PORT;

public class Client {
    private final String host;
    private final int port;

    public Client(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        try (Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT)) {
            System.out.println("Connecté au serveur");

            ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());

            while (true) {
                System.out.print("Entrez une commande (charger ou inscription): ");
                String command = scanner.nextLine();
                if (command.equalsIgnoreCase("charger")) {
                    System.out.print("Entrez la session: ");
                    String session = scanner.nextLine();
                    objectOutputStream.writeObject(command + " " + session);
                    Object response = objectInputStream.readObject();
                    if (response instanceof String) {
                        System.out.println((String) response);
                    } else {
                        System.out.println("Réponse inattendue du serveur: " + response);
                    }
                } else if (command.equalsIgnoreCase("inscription")) {
                    System.out.print("Entrez la session: ");
                    String session = scanner.nextLine();
                    System.out.print("Entrez le code du cours: ");
                    String codeCours = scanner.nextLine();
                    System.out.print("Entrez le matricule: ");
                    String matricule = scanner.nextLine();
                    System.out.print("Entrez le prénom: ");
                    String prenom = scanner.nextLine();
                    System.out.print("Entrez le nom: ");
                    String nom = scanner.nextLine();
                    System.out.print("Entrez l'email: ");
                    String email = scanner.nextLine();
                    String inscription = session + "\t" + codeCours + "\t" + matricule + "\t" + prenom + "\t" + nom + "\t" + email;
                    objectOutputStream.writeObject(command + " " + inscription);
                    Object response = objectInputStream.readObject();
                    if (response instanceof Boolean) {
                        if ((Boolean) response) {
                            System.out.println("Inscription réussie!");
                        } else {
                            System.out.println("Échec de l'inscription.");
                        }
                    } else {
                        System.out.println("Réponse inattendue du serveur: " + response);
                    }
                } else {
                    System.out.println("Commande invalide.");
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
