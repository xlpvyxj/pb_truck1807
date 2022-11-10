package pb_truck;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.sql.*;
import java.util.InputMismatchException;
import java.util.Scanner;


public class main {
    private static boolean isDone = false;
    
    public static void main(String[] args) throws FileNotFoundException {
        info();
        databaseConnection();
       
    }
    public static void info() {
        System.out.println("Panera Bread Truck Order Helper v1.0");
        System.out.println("\tDesigned for internal use only.");
        System.out.println("\tNathan Saur, 2022");
    }
    public static void databaseConnection() throws FileNotFoundException {
        Scanner scanner = new Scanner(System.in);
        File error = new File("error.log");
        PrintStream stream = new PrintStream(error);
        
        try {
            System.out.println("Attempting to establish connection to the inventory database.");
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connect = DriverManager.getConnection("jdbc:mysql://localhost:3306/truck", "root", "password");
            System.out.println("Success! Database connected.");
            Thread.sleep(1000);
            System.out.println("====================================");
            System.out.println(" _____                           ");
            System.out.println("|  __ \\                          ");
            System.out.println("| |__) |_ _ _ __   ___ _ __ __ _ ");
            System.out.println("|  ___/ _` | '_ \\ / _ \\ '__/ _` |");
            System.out.println("| |  | (_| | | | |  __/ | | (_| |");
            System.out.println("|_|   \\__,_|_| |_|\\___|_|  \\__,_|\n");
            System.out.println("====================================");
            Thread.sleep(500);
            while(!isDone) {
                System.out.println("Feel free to use any of the following commands:\n"
                        + "\t- R: Run the application.\n"
                        + "\t- C: Close the current truck order and return to this menu.\n"
                        + "\t- V: View current truck order.\n"
                        + "\t- E: Export the current truck order to a text file.\n"
                        + "\t- Q: Quit the application.");
                String continueWhenReady = scanner.next();
                if(continueWhenReady.equalsIgnoreCase("R")) {
                    ResultSet rs = connect.createStatement().executeQuery("select * from data");
                    while(rs.next()) {
                        System.out.println(rs.getString("ITEM_NAME")
                            + " (" + rs.getString("SHORTHAND").toUpperCase()
                            + ")\n\tPlease enter Y or N.");
                        String query = scanner.next();
                        
                        if(query.equalsIgnoreCase("Y")) {
                            System.out.print("How many weeks' worth do you need? ");
                            String temp = scanner.next();
                            int quantity = 0;
                            try {
                            	quantity = Integer.parseInt(temp);
                            	connect.createStatement().executeUpdate("UPDATE data SET QUANTITY =" + quantity + " WHERE ITEM_NAME ='" + rs.getString("ITEM_NAME") + "'");
                            } catch(NumberFormatException e) { }
                            connect.createStatement().executeUpdate("UPDATE data SET QUANTITY =" + quantity + " WHERE ITEM_NAME ='" + rs.getString("ITEM_NAME") + "'");
                            System.out.println("Your truck order has been updated to include " + quantity + " weeks worth of " + rs.getString("ITEM_NAME") +"\n");
                        }
                        else if(query.equalsIgnoreCase("N")) {
                            connect.createStatement().executeUpdate("UPDATE data SET QUANTITY =" + 0 + " WHERE ITEM_NAME ='" + rs.getString("ITEM_NAME") + "'");
                        }
                        else if(query.equalsIgnoreCase("C")) break;
                    }
                    System.out.println("End of truck order reached.");
                } else if(continueWhenReady.equalsIgnoreCase("V")) {
                	view(connect, false);
                } else if(continueWhenReady.equalsIgnoreCase("E")) {
                	view(connect, true);
                } else if(continueWhenReady.equalsIgnoreCase("Q")) {
                	isDone = true;
                } else System.out.println("Please enter a valid command.\n");
            }
            System.out.println("Program safely exited.");
            System.exit(0);
            
        } catch(Exception e) {
            System.out.println("\n===================================="
            		+ "\nThe program has encountered an error and has been exited for safety reasons."
            		+ "\nView the stack trace here:\n\t" + System.getProperty("user.dir") + "/error.log");
            e.printStackTrace(stream);
            
        }
    }

    public static void view(Connection connect, boolean export) throws SQLException {
    	if(!export) {
        	ResultSet rs = connect.createStatement().executeQuery("select * from data");
        	while(rs.next()) {
        		System.out.println(rs.getString("ITEM_NAME").toUpperCase() + ", " + rs.getString("QUANTITY"));
        	}
    	} else {
    		try {
    		FileWriter file = new FileWriter("output.txt");
    		ResultSet rs = connect.createStatement().executeQuery("select * from data");
        	while(rs.next()) {
        		file.write(rs.getString("ITEM_NAME").toUpperCase() + ", QUANTITY:" + rs.getString("QUANTITY") + "\n");
        	}
        	file.close();
        	System.out.println("\nOutput has successfully been exported to:\n\t" + System.getProperty("user.dir") + "/output.txt\n");
    		} catch(IOException e) { }
    	}
    }
}