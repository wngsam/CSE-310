import java.io.*;
import java.net.*;

public class NimClient {
    
    static BufferedReader inFromUser, inFromServer;
    static DataOutputStream outToServer;
    static Socket clientSocket;
    static Boolean logged = false;
    static Boolean yourTurn = false;
    static String playerNum;
    
    public static void main(String[] args) throws Exception{
        
        //int port = Integer.parseInt(args[1]);
        int port = 7971;
        inFromUser = new BufferedReader(new InputStreamReader(System.in));
        
        System.out.print("Commands:\n-help\n-login <name>\n-remove <n> <s>\n-exit\nInput: ");
        String command = "";
        
        while(!command.equalsIgnoreCase("exit")){

            if(logged==true){
                String server = inFromServer.readLine();
                System.out.println(server);
                String[] msg = server.split("\\|");
                System.out.println(msg[2]);
                
                if(msg[1].equals("true")){
                System.out.println(inFromServer.readLine());
                System.out.println(inFromServer.readLine());
                }
                if(msg[1].equals("false")){
                    
                logged=false;
                command="exit";
                
                
                }else if(msg[0].equals(playerNum)){
                    System.out.println("Please Remove: ");
                    //command = inFromUser.readLine().trim();
                    command = "remove";
                }else{
                    command = "AVOID";
                }
            }
            else{
            command = inFromUser.readLine().trim();
            }
            
        if(command.length()>5&&command.substring(0,5).equalsIgnoreCase("login")){
            if(command.equalsIgnoreCase("login")){
                System.out.print("\nNo user name specified.\nInput: ");
                }
            else if(logged==false){
                //String addr = args[0];
                String addr = "allv25.all.cs.sunysb.edu"; 
                login(addr, port, command.substring((6)));
                logged = true;
            }else{
                System.out.print("\nYou are already logged in.\n Input: ");
                }
        }else if(command.equalsIgnoreCase("help")){
            System.out.println("\nType in one of the following command with parameters if applicable to start:\n\nhelp - Prints this help text.\nlogin <name> - Type login followed by your desired username to login to server to play.\nremove <n> <s> - Type remove followed by the bumber of obj to remove (greater than 1) and the set# to remove from.\nexit - Type to exit.\n");
            }
        
        else if(command.toLowerCase().contains("remove")){
            if(logged==true){
                command=inFromUser.readLine().trim();
                command=remove(command);
                
            }else{
                System.out.println("You are not logged in.");
                }
        }else if(command.equals("AVOID")){
        System.out.println("AVOID");
        }else if(!command.equalsIgnoreCase("exit")){
            System.out.print("\nInvalid Command.\nInput: ");
            }
        }
        if(logged==true){
            System.out.println("EXIT, LOGGED");
            outToServer.writeBytes(command + '\n');
            inFromServer.readLine();
            System.out.println(inFromServer.readLine());
            System.out.println("Exiting... Good Bye!");
            clientSocket.close();}
        else{
        System.out.println("Exiting... Good Bye!");}
    }
    
    public static void login(String a, int port, String name) throws Exception{
        clientSocket = new Socket(a, port);
	inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        outToServer = new DataOutputStream(clientSocket.getOutputStream()); 

        System.out.println("Registering Username...");
        outToServer.writeBytes(name + '\n');
        String temp = inFromServer.readLine();
        System.out.println(temp);
        if(temp.contains("player one")){
            playerNum = "one";
            System.out.println("Waiting...");
            System.out.println(inFromServer.readLine());
        }
        else{
            playerNum = "two";
        }
    }
    
    public static String remove(String command) throws IOException{
                
                Boolean valid;
                do{
                String temp = command.replaceAll("\\s","");
                if(command.equalsIgnoreCase("exit")){
                    return "exit";    
                    }
                    
                else if(command.equalsIgnoreCase("help")){
                    valid = false;
                    System.out.println("Type in one of the following command with parameters if applicable to start:\nhelp - Prints this help text.\nlogin <name> - Type login followed by your desired username to login to server to play.\nremove <n> <s> - Type remove followed by the bumber of obj to remove (greater than 1) and the set# to remove from.\nexit - Type to exit.\n");
                    command = inFromUser.readLine().trim();
                    }
                    else if(temp.length()<8||temp.length()>8||temp.charAt(6)<'0' || temp.charAt(6)>'9'||temp.charAt(7)<'0' || temp.charAt(7)>'9'){
                    valid= false;
                    System.out.println("Invalid remove command, please try again.");
                    command = inFromUser.readLine().trim();
                    
                }else{
                    valid = true;
                }
                
                }while(valid==false);
        
        outToServer.writeBytes(command + '\n');
        System.out.println("Command Sent");
        if(!inFromServer.readLine().equals("OK")){
        System.out.println("Invalid Set or Size #, Please Try Again.");
        //command = inFromUser.readLine().trim();
        remove(inFromUser.readLine().trim());
        }
        System.out.println("Remove ");
        return "AVOID";
    }
}
