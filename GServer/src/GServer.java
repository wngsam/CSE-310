import java.io.*;
import java.net.*;

/**
 * Game of Nim: CSE 310 Project
 * GServer: The server portion of the two program game.
 * This program handles connections with users and hold data for the game and analyze game progress.
 * GServer holds socket data, game data and user communication info.
 * @author Sam Wang ID: 108107971
 */
public class GServer {
    static ServerSocket welcSocket;
    static Socket connectionSocketOne, connectionSocketTwo;   
    static BufferedReader inFromClientOne, inFromClientTwo;
    static DataOutputStream outToClientOne, outToClientTwo;
    static String playerOne, playerTwo;
    static int[] game;
    static String set;
    static String size;
    static Boolean gameStatus = false;
    final static int port = 7971;
    static String playersTurn = "one";
    
    /**
     * The main program waits for connections then generate the game and proceed to handle user inputs.
     * User inputs are checked and analyzed then the game is updated.
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        
        //Wait for connections then generate game array.
        welcSocket = new ServerSocket(port);
        System.out.println("Server Started.");
        waitConnections();
        generateBoard();
        
        //Loop for game
        while(gameStatus==true){
            
            //The current game status is updated and printed:
            set = "set:  ";
            size = "size: ";
            writeBoard();
            System.out.println(set);
            System.out.println(size);
            
            //Send messages to players to proceed:
            if(playersTurn.equals("one")){
                outToClientOne.writeBytes(message("Your move!"));
                outToClientTwo.writeBytes(message("Waiting for "+playerOne+"..."));
                
            }else{
                outToClientTwo.writeBytes(message("Your move!"));
                outToClientOne.writeBytes(message("Waiting for "+playerTwo+"..."));    
            }
            
            //Send players the game info:
            outToClientOne.writeBytes(message(set));
            outToClientOne.writeBytes(message(size));
            outToClientTwo.writeBytes(message(set));
            outToClientTwo.writeBytes(message(size));
            
            //Take in and manage input from current player:
            String input;
            if(playersTurn.equals("one")){
                System.out.println(playerOne+"'s turn.");
                Boolean valid;
                
                do{
                    input=inFromClientOne.readLine();
                    if(analyze(input.replaceAll("\\s",""))==false){
                        valid=false;
                        outToClientOne.writeBytes(message("404"));
                        //If input is illegal send 404
                    }else{
                        valid = true;
                        
                    }
                }while(valid==false);
                    outToClientOne.writeBytes(message("200")); 
                    //If input is legal send 200
            }else{
                System.out.println(playerTwo+"'s turn.");
                Boolean valid;
                
                do{
                    input=inFromClientTwo.readLine();
                    if(analyze(input.replaceAll("\\s",""))==false){
                        valid=false;
                        outToClientTwo.writeBytes(message("404"));
                        //If input is illegal send 404
                    }else{
                        valid = true;
                        
                    }
                }while(valid==false);
                    outToClientTwo.writeBytes(message("200")); 
                    //If input is legal send 200
            }
            
            //If game ended because someone exited, send winner/loser message:
            if(gameStatus==false){
                if(playersTurn.equals("two")){
                    System.out.println(playerTwo+" has left");
                    outToClientOne.writeBytes(message(playerTwo+" has left, you win!"));
                    outToClientOne.writeBytes(message("Good Bye!"));
                    outToClientTwo.writeBytes(message("You have been disqualified."));
                    System.out.println(playerOne+" wins!");
                    
                }else{
                    System.out.println(playerOne+" has left");
                    outToClientTwo.writeBytes(message(playerOne+" has left, you win!"));
                    outToClientTwo.writeBytes(message("Good Bye!"));
                    outToClientOne.writeBytes(message("You have been disqualified."));
                    System.out.println(playerTwo+" wins!");
                }
            
            //If game ends naturally, send message:
            }else if(countValue(game)==0){
                gameStatus=false;
                if(playersTurn.equals("one")){
                    
                    outToClientOne.writeBytes(message("Congratulations, you have won!"));
                    outToClientTwo.writeBytes(message("You have lost. "+playerOne+" wins!"));
                    System.out.println(playerOne+" wins!");
                    
                }else{
                    outToClientTwo.writeBytes(message("Congratulations, you have won!"));
                    outToClientOne.writeBytes(message("You have lost. "+playerTwo+" wins!"));
                    System.out.println(playerTwo+" wins!");
                }
            
            //Otherwise, switch turn and proceed:
            }else{
                if(playersTurn.equals("one")){
                    playersTurn="two";
                    
                }else{
                    playersTurn="one";
                    
                }
            }
        }
        
        //If game has finish, close server:
        System.out.println("Game has finished. Closing server.");
        connectionSocketOne.close();
        connectionSocketTwo.close();
    }
    /**
     * Checks whether the user's input (move) was legal or illegal, if legal execute it otherwise return false.
     * @param input
     * @return Whether the move was (true) legal or (false) illegal.
     * @throws IOException
     */
    public static Boolean analyze(String input) throws IOException{
        String[] temp = decode(input);
        
        if(temp[1].equalsIgnoreCase("exit")){
            gameStatus=false;
            return true;
        }
        
        int inSet;
        int inSize;
        
        if(temp[1].contains("remove")){
          inSize = temp[1].charAt(6)-'0';
          inSet = temp[1].charAt(7)-'0';
          
          if(inSet>game.length){
                return false; 
                
          }else if(inSize>game[inSet-1]){
                return false;
          }
          
          game[inSet-1]-=inSize;
          if(temp[0].equals("one")){
            System.out.println(playerOne+" removed "+inSize+" items from set "+inSet);
            outToClientTwo.writeBytes(message(playerOne+" removed "+inSize+" items from set "+inSet));
          }else{
            System.out.println(playerTwo+" removed "+inSize+" items from set"+inSet);
            outToClientOne.writeBytes(message(playerTwo+" removed "+inSize+" items from set "+inSet));
          }
          
        }
        return true;
    }
    
    /**
     * message produces a server message using the desired String and package other data onto it for client to extract.
     * @param msg
     * @return Encoded server message.
     */
    public static String message(String msg){
        return playersTurn+"|"+gameStatus+"|"+msg+"\n";
    }
    
    /**
     * decode breaks up the client's message into understandable parts of data.
     * @param msg
     * @return Client message in String array.
     */
    public static String[] decode(String msg){
        return msg.split("\\|");
    }
    
    /**
     * Wait for initial connections from users and assign their player #.
     * @throws IOException
     */
    public static void waitConnections() throws IOException{
        
            connectionSocketOne = welcSocket.accept();
            System.out.println("One Player Connected");
            inFromClientOne = new BufferedReader(new InputStreamReader(connectionSocketOne.getInputStream())); 
            outToClientOne = new DataOutputStream(connectionSocketOne.getOutputStream());
            playerOne = decode(inFromClientOne.readLine())[1];
            outToClientOne.writeBytes(message("Your name is: "+playerOne+". You are player one. Waiting for another player..."));

            connectionSocketTwo = welcSocket.accept();
            System.out.println("Two Players Connected\nGame Starting");
            gameStatus=true;
            inFromClientTwo = new BufferedReader(new InputStreamReader(connectionSocketTwo.getInputStream())); 
            outToClientTwo = new DataOutputStream(connectionSocketTwo.getOutputStream());
            playerTwo = decode(inFromClientTwo.readLine())[1];
            outToClientTwo.writeBytes(message("Your name is: "+playerTwo+". You are player two. Your opponent is "+playerOne+". Get Ready!"));
            outToClientOne.writeBytes(message("Second player found. You are playing against "+playerOne+". Get Ready!"));
    }
    
    /**
     * countValue counts the current # of objects in game to determine whether the game has finished.
     * @param array
     * @return total # of objects currently in-game.
     */
    public static int countValue(int[] array){
        int num = 0;
        for(int i=0;i<array.length;i++)
            num += array[i];
        return num;
    }
    
    /**
     * generateBoard randomizes the number of objects in a random number of sets for the game.
     */
    public static void generateBoard(){
        int M = 3+(int)(Math.random()*3);
        game= new int[M];
        
        for(int i=0;i<M;i++){
            game[i]=1+(int)(Math.random()*7);
        }
    }
    
    /**
     * writeBoard takes the game array of objects in sets and puts it in a String that is easy to read for users.
     */
    public static void writeBoard(){
        for(int i=0;i<game.length;i++){
            set+=(i+1)+" ";
            size+=game[i]+" ";
        }
    }
    
}
