import java.io.*;
import java.net.*;

/**
 * Game of Nim: CSE 310 Project
 * GClient: The client portion of the two program game. 
 * This client helps the user communicate with the server program along with doing simple commands and functions.
 * The client holds socket communication data and whether the user is logged in [as player one or two].
 * @author Sam Wang ID: 108107971
 */
public class GClient {
    static BufferedReader inFromUser, inFromServer;
    static DataOutputStream outToServer;
    static Socket clientSocket;
    static Boolean logged = false;
    static String playerNum="zero";
    final static int port = 7971;
    
    /**
     * The main program is the core structure for menu control, server communication and user accessibility.
     * @param args
     * @throws IOException
     * @throws Exception
     */
    public static void main(String[] args) throws IOException, Exception {
        
        //Prints out and takes care of user commands
        String command="";
        inFromUser = new BufferedReader(new InputStreamReader(System.in));
        System.out.print("AVAILABLE COMMANDS:\n-Help\n-Login <name>\n-Remove <n> <s>\n-Exit\nInput: ");
        
        //Looping user command handling and game communication from server
        while(!command.equalsIgnoreCase("exit")){
            //The following section prints messages from the server when in-game:
            if(logged==true){
                String[] temp = decode(inFromServer.readLine());
                System.out.println(temp[2]);
                
                if(temp[1].equals("true")){
                    System.out.println(decode(inFromServer.readLine())[2]);
                    System.out.println(decode(inFromServer.readLine())[2]);
                    
                    if(temp[0].equals(playerNum)){
                        System.out.println("Type: remove <n> <s> to remove.");
                        command = "remove";
                    }else{
                        command = "Skip";
                    }
                }else{
                    logged=false;
                    command="exit";
                }
            }else{
                command = inFromUser.readLine().trim();
            }
            
            //Below here is the user menu handling:
            if(command.length()>5&&command.substring(0,5).equalsIgnoreCase("login")){
                if(command.equalsIgnoreCase("login")){
                    System.out.print("\nNo user name specified.\nInput: ");
                }else if(logged==false){
                    String addr = args[0]; //Dynamic address
                    //String addr = "allv25.all.cs.sunysb.edu"; //Set hard-coded default address
                    login(addr, command.substring((6)));
                }else{
                    System.out.print("\nYou are already logged in.\n Input: ");
                }
            }else if(command.equalsIgnoreCase("help")){
                System.out.println("\nLIST OF COMMANDS:\n\n-Login <name> - Type login followed by your desired username to login to server and play.\n-Remove <n> <s> - (Game function) Type remove followed by the bumber of obj to remove (greater than 1) and the set# to remove from.\n-Exit - Type exit to exit.\n");
            
            }else if(command.toLowerCase().contains("remove")){
                if(logged==true){
                    command = remove(command);
                }else{
                    System.out.println("You are not logged in.");
                }
            
            }else if(command.equals("Skip")){
                System.out.println("Please be patient.");
                System.out.println(decode(inFromServer.readLine())[2]);
            
            }else if(!command.equalsIgnoreCase("exit")){
                System.out.print("\nInvalid Command.\nInput: ");
            }
            
        }
        
        //Closing client statements and closure:
        if(logged==true){
            outToServer.writeBytes(message(command));
            inFromServer.readLine();
            System.out.println(decode(inFromServer.readLine())[2]);
            System.out.println("Exiting... Good Bye!");
            clientSocket.close();}
        
        else{
            System.out.println("Exiting... Good Bye!");
            
        }
    }
    
    /**
     * The remove function does simple checks to see if user input is correct and sends it to the server.
     * @param command
     * @return skip (to proceed) or exit
     * @throws IOException
     */
    public static String remove(String command) throws IOException{
        command = inFromUser.readLine().trim();
        Boolean valid;
        //Checks if input is in the correct format and legal, (if not, prompts again) help/exit option is possible as well:
        do{ 
            command=command.replaceAll("\\s","");
            if(command.equalsIgnoreCase("exit")){
                return "exit";   
                
            }else if(command.equalsIgnoreCase("help")){
                valid = false;
                System.out.println("\nLIST OF COMMANDS:\n\n-Login <name> - Type login followed by your desired username to login to server and play.\n-Remove <n> <s> - (Game function) Type remove followed by the bumber of obj to remove (greater than 1) and the set# to remove from.\n-Exit - Type exit to exit.\n");
                command = inFromUser.readLine().trim();
            
            }else if(command.length()<8||command.length()>8||command.charAt(6)<'0'||command.charAt(6)>'9'||command.charAt(7)<'0'||command.charAt(7)>'9'){
                valid= false;
                System.out.println("Invalid, please try again!");
                command = inFromUser.readLine().trim();
            
            }else{
                valid = true;
            }
            
        }while(valid==false);
        
        //Get server response whether input was legal in game then returns to main:
            outToServer.writeBytes(message(command));
            if(decode(inFromServer.readLine())[2].equals("404")){
                System.out.println("Invalid Set or Size #, Please Try Again.");
                remove("");
                
            }
            return "Skip";
    }
    
    /**
     * message function packages a string message using a protocol the server will decode and understand.
     * @param msg
     * @return packaged message
     */
    public static String message(String msg){
        return playerNum+"|"+msg+"\n";
    }
    
    /**
     * decode decodes the server's message and extracts the data into a String array.
     * @param msg
     * @return decoded String array of the server message
     */
    public static String[] decode(String msg){
        return msg.split("\\|");
    }
    
    /**
     * The login function sets up socket communication with the server and registers the userid.
     * @param a
     * @param name
     * @throws Exception
     */
    public static void login(String a, String name) throws Exception{
        clientSocket = new Socket(a, port);
	inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        outToServer = new DataOutputStream(clientSocket.getOutputStream()); 
        
        //Input name to server:
        System.out.println("Registering Username...");
        outToServer.writeBytes(message(name));
        
        //Print server response
        String[] msg = decode(inFromServer.readLine());
        System.out.println(msg[2]);
        
        //Determine if player one or two
        if(msg[1].equals("false")){
            playerNum="one";
            System.out.println(decode(inFromServer.readLine())[2]);
            
        }else{
            playerNum = "two";
            
        }
        logged = true;
    }
    
}
