package server;

import javafx.util.Pair;
import server.models.Course;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

/**
 * La classe Server est responsable de la gestion des requêtes des clients
 * pour charger des cours et enregistrer des inscriptions.
 * Cette classe doit être exécutée avant de lancer les clients.
 */
public class Server {

    public final static String REGISTER_COMMAND = "INSCRIRE";
    public final static String LOAD_COMMAND = "CHARGER";
    private final ServerSocket server;
    private Socket client;
    private ObjectInputStream objectInputStream;
    private ObjectOutputStream objectOutputStream;
    private final ArrayList<EventHandler> handlers;

    public Server(int port) throws IOException {
        this.server = new ServerSocket(port, 1);
        this.handlers = new ArrayList<EventHandler>();
        this.addEventHandler(this::handleEvents);
    }

    public void addEventHandler(EventHandler h) {
        this.handlers.add(h);
    }

    private void alertHandlers(String cmd, String arg) {
        for (EventHandler h : this.handlers) {
            h.handle(cmd, arg);
        }
    }

    public void run() {
        while (true) {
            try {
                client = server.accept();
                System.out.println("Connecté au client: " + client);
                objectInputStream = new ObjectInputStream(client.getInputStream());
                objectOutputStream = new ObjectOutputStream(client.getOutputStream());
                listen();
                disconnect();
                System.out.println("Client déconnecté!");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void listen() throws IOException, ClassNotFoundException {
        String line;
        if ((line = this.objectInputStream.readObject().toString()) != null) {
            Pair<String, String> parts = processCommandLine(line);
            String cmd = parts.getKey();
            String arg = parts.getValue();
            this.alertHandlers(cmd, arg);
        }
    }

    public Pair<String, String> processCommandLine(String line) {
        String[] parts = line.split(" ");
        String cmd = parts[0];
        String args = String.join(" ", Arrays.asList(parts).subList(1, parts.length));
        return new Pair<>(cmd, args);
    }

    public void disconnect() throws IOException {
        objectOutputStream.close();
        objectInputStream.close();
        client.close();
    }

    public void handleEvents(String cmd, String arg) {
        if (cmd.equals(REGISTER_COMMAND)) {
            handleRegistration();
        } else if (cmd.equals(LOAD_COMMAND)) {
            handleLoadCourses(arg);
        }
    }

    /**
     * Lire un fichier texte contenant des informations sur les cours et les transofmer en liste d'objets 'Course'.
     * La méthode filtre les cours par la session spécifiée en argument.
     * Ensuite, elle renvoie la liste des cours pour une session au client en utilisant l'objet 'objectOutputStream'.
     * La méthode gère les exceptions si une erreur se produit lors de la lecture du fichier ou de l'écriture de l'objet dans le flux.
     * @param session La session pour laquelle les cours doivent être chargés
     * @return Une liste d'objets Course contenant les informations sur les cours pour la session spécifiée
     * @throws IOException En cas d'erreur lors de la lecture du fichier de cours
     */


    public void handleLoadCourses(String arg) {
        try {
            Scanner reader = new Scanner(new File("src/main/java/server/data/cours.txt"));
            ArrayList<Course> courses = new ArrayList<>();
            String line;

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\t");
                String code = parts[0];
                String name = parts[1];
                String session = parts[2];

                if (session.equalsIgnoreCase(arg)) {
                    System.out.println("Course Added : " + parts[0]);
                    courses.add(new Course(name, code, session));
                }
            }

            reader.close();
            objectOutputStream.writeObject(courses);
            objectOutputStream.flush();
            System.out.println(courses);
        } catch (IOException e) {
            System.out.println("Errorr");
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            System.out.println("Errorr");
            e.printStackTrace();
        }
    }


    /**
     * Récupérer l'objet 'RegistrationForm' envoyé par le client en utilisant 'objectInputStream', l'enregistrer dans un fichier texte
     * et renvoyer un message de confirmation au client.
     * La méthode gére les exceptions si une erreur se produit lors de la lecture de l'objet, l'écriture dans un fichier ou dans le flux de sortie.
     * @param registrationForm Un objet RegistrationForm contenant les informations d'inscription de l'étudiant
     * @return Un message de succès ou d'échec de l'inscription
     * @throws IOException En cas d'erreur lors de l'écriture du fichier d'inscription
     */


    public void handleRegistration() {
        try {
            RegistrationForm registrationForm = (RegistrationForm) objectInputStream.readObject();
            FileWriter fileWriter = new FileWriter("src/main/java/server/data/inscription.txt", true);
            Course course = registrationForm.getCourse();

            String formattedRegistration = String.format("%s\t%s\t%s\t%s\t%s\t%s%n",
                    registrationForm.getCourse().getSession(),
                    registrationForm.getCourse().getCode(),
                    registrationForm.getMatricule(),
                    registrationForm.getPrenom(),
                    registrationForm.getNom(),
                    registrationForm.getEmail());

            Files.write(Paths.get("src/main/java/server/data/inscription.txt"), formattedRegistration.getBytes(), StandardOpenOption.APPEND);

            objectOutputStream.writeObject("Inscription réussie.");
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}



