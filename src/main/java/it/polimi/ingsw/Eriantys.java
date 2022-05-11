package it.polimi.ingsw;

import it.polimi.ingsw.client.CLI.CLI;
import it.polimi.ingsw.client.UI;
import it.polimi.ingsw.server.Server;
import org.apache.commons.cli.*;

/**
 * This class is the main class of the game.
 */
public class Eriantys {

    /**
     * This method is the main method of the game.
     *
     * @param args the command line arguments
     */
    public static void main( String[] args ) {
        Options options = setupOptions();
        HelpFormatter formatter = new HelpFormatter();
        CommandLineParser parser = new DefaultParser();
        CommandLine cmd;

        try {
            cmd = parser.parse(options, args);
            checkOptions(cmd);

            if (cmd.hasOption("h"))
                formatter.printHelp("Eriantys", options);
            else if (cmd.hasOption("s")) {
                Server server;
                if (cmd.hasOption("p")) {
                    int port = Integer.parseInt(cmd.getOptionValue("p"));
                    System.out.println("Starting server on port " + port);
                    server = new Server(port);
                }
                else {
                    System.out.println("Starting server on default port");
                    server = new Server();
                }
                server.handleRequest();
            } else if (cmd.hasOption("c")) {
                System.out.println("Starting game in CLI mode");
                UI userInterface = new CLI();
                userInterface.showStartScreen();
            } else {
                System.out.println("Starting game in GUI mode");
            }

        } catch (ParseException e) {
            System.out.println(e.getMessage());
            formatter.printHelp("Eriantys", options);
        }
    }

    /**
     * Setup of the command line options
     * @return the options
     */
    private static Options setupOptions() {
        Options options = new Options();
        options.addOption("h", "help", false, "Print this help");
        options.addOption("s", "server", false, "Start server");
        options.addOption("p", "port", true, "Server port number");
        options.addOption("c", "cli", false, "Start game in CLI mode");
        options.addOption("g", "gui", false, "Start game in GUI mode");
        return options;
    }

    /**
     * Check the options
     * @param cmd the command line to check
     * @throws ParseException if the options are not valid
     */
    private static void checkOptions(CommandLine cmd) throws ParseException {
        if (cmd.hasOption("s") && cmd.hasOption("c"))
            throw new ParseException("You can't start both server and CLI at the same time");
        if (cmd.hasOption("s") && cmd.hasOption("g"))
            throw new ParseException("You can't start both server and GUI at the same time");
        if (cmd.hasOption("p") && !cmd.hasOption("s"))
            throw new ParseException("You can't specify a port number for a client");
        if (cmd.hasOption("g") && cmd.hasOption("c"))
            throw new ParseException("You can't start both GUI and CLI at the same time");

        if (cmd.hasOption("p")) {
            try {
                int port = Integer.parseInt(cmd.getOptionValue("p"));
                if (port < 1024 || port > 65535)
                    throw new ParseException("Port number must be between 1024 and 65535");
            } catch (NumberFormatException e) {
                throw new ParseException("Port number must be an integer");
            }
        }

    }
}

