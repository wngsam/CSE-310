import java.io.*;
import java.net.*;

public class NimServer {
    
    static ServerSocket welcSocket;
    static Socket connectionSocketOne, connectionSocketTwo;   
    static BufferedReader inFromClientOne, inFromClientTwo;
    static DataOutputStream outToClientOne, outToClientTwo;
    static String playerOne, playerTwo;
    static int[] game;
    static String set;
    static String size;
    static Boolean gameStatus = false;
    
public static void main(String[] args) throws Exception {
    
        //int port = Integer.parseInt(args[0]);
        int port = 7971;
        welcSocket = new ServerSocket(port);
        //String board;
        System.out.println("Server Ready.");
        
        waitConnections();
        generateBoard();

        //START GAME//
        
        String playersTurn = "one";
        gameStatus = true;
        
        //Message Format: (Split by |) Turn(1 or 2)|Game (true or false)|"Print Msg", ex: "1|true|Print"
  
        while(gameStatus==true){
        System.out.println("Game Run");
        set= "set:  ";
        size="size: ";
        writeBoard();
        if(playersTurn.equals("one")){
        outToClientOne.writeBytes(playersTurn+"|"+gameStatus+"|It's your turn.\n");
        outToClientTwo.writeBytes(playersTurn+"|"+gameStatus+"|Awaiting Player One's Move.\n");
        }else{
        outToClientTwo.writeBytes(playersTurn+"|"+gameStatus+"|It's your turn.\n");
        outToClientOne.writeBytes(playersTurn+"|"+gameStatus+"|Awaiting Player Two's Move.\n");    
        }
            
        outToClientOne.writeBytes(set+"\n");
        outToClientOne.writeBytes(size+"\n");
        outToClientTwo.writeBytes(set+"\n");
        outToClientTwo.writeBytes(size+"\n");
        
        String input;
        if(playersTurn.equals("one")){
            System.out.println("Player One's Turn");
            Boolean valid=true;
            do{
            input=inFromClientOne.readLine();
            System.out.println("Removing");
            if(analyze(input)==false){
                valid=false;
                outToClientOne.writeBytes("false\n");
            }else{
                valid = true;
            }
            }while(valid==false);
                outToClientOne.writeBytes("OK\n");    
        }else{
            System.out.println("Player Two's Turn");
            Boolean valid=true;
            do{
            input=inFromClientTwo.readLine();
            if(analyze(input)==false){
                valid=false;
                outToClientTwo.writeBytes("false\n");
            }else{
                valid = true;
            }
            }while(valid==false);
                outToClientTwo.writeBytes("OK\n");  
        }
        
        if(gameStatus==false){
            if(playersTurn.equals("two")){
            outToClientOne.writeBytes(playersTurn+"|"+gameStatus+"|YOU WIN.\n");
            outToClientTwo.writeBytes("YOU LOST BY FORFEIT.\n");
            }else{
            outToClientTwo.writeBytes(playersTurn+"|"+gameStatus+"|YOU WIN.\n");
            outToClientOne.writeBytes("YOU LOST BY FORFEIT.\n");    
            }
        }
        else if(countValue(game)==0){
            gameStatus=false;
            if(playersTurn.equals("one")){
            outToClientOne.writeBytes(playersTurn+"|"+gameStatus+"|YOU WIN.\n");
            outToClientTwo.writeBytes(playersTurn+"|"+gameStatus+"|YOU LOST.\n");
            }else{
            outToClientTwo.writeBytes(playersTurn+"|"+gameStatus+"|YOU WIN.\n");
            outToClientOne.writeBytes(playersTurn+"|"+gameStatus+"|YOU LOST.\n");    
            }
        }else{
            if(playersTurn.equals("one")){
                playersTurn="two";
            }else{
                playersTurn="one";
            }
        }
        System.out.println(gameStatus);
        //END LOOP
        }
        
        //END GAME//
        System.out.println("Game Finished. Closing server.");
        connectionSocketOne.close();
        connectionSocketTwo.close();
    }
    
    public static void waitConnections() throws IOException{
        
            connectionSocketOne = welcSocket.accept();
            System.out.println("Player One Connected");
            inFromClientOne = new BufferedReader(new InputStreamReader(connectionSocketOne.getInputStream())); 
            outToClientOne = new DataOutputStream(connectionSocketOne.getOutputStream());
            playerOne = inFromClientOne.readLine();
            outToClientOne.writeBytes("Your name is: "+playerOne+". You are player one, please wait for another player. Thank you.\n");

            connectionSocketTwo = welcSocket.accept();
            System.out.println("Player Two Connected");
            inFromClientTwo = new BufferedReader(new InputStreamReader(connectionSocketTwo.getInputStream())); 
            outToClientTwo = new DataOutputStream(connectionSocketTwo.getOutputStream());
            playerTwo = inFromClientTwo.readLine();
            outToClientTwo.writeBytes("Your name is: "+playerTwo+". You are player two, get ready to play!\n");
            outToClientOne.writeBytes("Second player found, get ready to play!\n");
    }
    public static Boolean analyze(String input){
        input = input.replaceAll("\\s","");
        
        int set;
        int size;
          
        if(input.equalsIgnoreCase("exit")){
            gameStatus=false;
        }else if(input.contains("remove")){
          size = input.charAt(6)-'0';
          set = input.charAt(7)-'0';
          
          if(set>game.length){
                return false; 
          }else if(size>game[set-1]){
                return false;
            }  
          game[set-1]-=size;
          System.out.println("Player removed "+size+" from "+set);
        }
        System.out.println("Remove OK");
        return true;
    }
    public static void generateBoard(){
        int M = 3+(int)(Math.random()*3);
        game= new int[M];
        
        for(int i=0;i<M;i++){
            game[i]=1+(int)(Math.random()*7);
        }
    }
    public static void writeBoard(){
        for(int i=0;i<game.length;i++){
            set+=(i+1)+" ";
            size+=game[i]+" ";
        }
        System.out.println(set);
        System.out.println(size);
    }
    public static String drawBoard(int[] array){
        String board = "";
        for(int i=0;i<array.length;i++){
            if(array[i]>0){
                board+="*";
                array[i]+=-1;
            }else{
                board+=" ";
            }
        }
        board+="\n";
        if(countValue(array)>0){
            board=drawBoard(array)+board;
        }
        return board;
    }
    public static int countValue(int[] array){
        int num = 0;
        for(int i=0;i<array.length;i++)
            num += array[i];
        return num;
    }
}
