// A Java program for a Server

import java.net.*;
import java.io.*;
import java.util.StringTokenizer;

public class BankServer
{
    //initialize socket and input stream
    private Socket		 socket = null;
    private ServerSocket server = null;
    private DataInputStream in	 = null;
    private DataOutputStream out	 = null;


    // constructor with port
    public BankServer(int port)  {

        try {
            // starts server and waits for a connection
            server = new ServerSocket(port);
            while (true){
                System.out.println("Bank Server started");
                System.out.println("Host ip: " +"127.0.0.1 "+"port: "+port);

                System.out.println("Waiting for a client ...");


                socket = server.accept(); //connection established with client.
                System.out.println("Client accepted");

                // takes input from the client socket
                in = new DataInputStream(
                        new BufferedInputStream(socket.getInputStream()));

                //send output to client socket
                out = new DataOutputStream(socket.getOutputStream());

                String UserInfo = "";

                out.writeUTF("hi from server");
                String clientHi = in.readUTF(); //hi from client

                UserInfo = in.readUTF(); //user info send from client
                                         // format: FirstName#FamilyName#PostCode#CreditCardNo


                //retrive all data from 'database.txt' file and saving it
                // creating instance of UserData class
                UserData[] userData = RetriveData();

                int userNo = CheckInfo(UserInfo, userData); //now checking if client given user
                                                            // info match with any registered user

                if (userData[userNo].FirstName.equalsIgnoreCase("end")) { //User not found
                                                                                        //connction will be closed
                    out.writeUTF("NoUserFound");
                }
                else {                              //userfound,Now will take the withdrawal ammount from client
                    out.writeUTF("UserFound");
                    String p = in.readUTF();        //client sends withdrawal amount
                    int price = Integer.parseInt(p);
                    int userCredit, userBalance;
                    userCredit = userData[userNo].Credit;
                    userBalance = userData[userNo].Balance;
                    if (userCredit < price) {       //if withdrawal amount is less than user credit
                        out.writeUTF("insufficientBalnce"); // no transaction will be made.Connection closed

                    } else {
                        userData[userNo].Credit -= price;  // if user has enough credit. Transaction will be
                        userData[userNo].Balance -= price;  //successful and amount will be subtracted from user account
                        out.writeUTF("DoneSuccessfully!");
                        saveData(userData);     //now save the changed data to 'database.txt'
                    }

                }


                System.out.println("Closing connection");

                // close connection
                socket.close();
                in.close();
            }
        }
        catch (IOException i) {
            System.out.println(i);
        }
    }

    public void saveData(UserData[] userData)  // this method saves the updated data back to 'database.txt file
    {
        File file = new File("database.txt");
        try {
            FileWriter writer = new FileWriter(file);
            for (int i=0;i<20;i++)
            {
                if (userData[i].FirstName.equalsIgnoreCase("end")) break;
                String userinfo = ""+userData[i].FirstName+"#"
                        +userData[i].FamilyName+"#"
                        +userData[i].PostCode+"#"
                        +userData[i].CreditCard+"#"
                        +userData[i].Balance+"#"
                        +userData[i].Credit;
                //System.out.println("write kortise --->  " + userinfo);
                writer.write(userinfo+"\n");
            }
            //writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public UserData[] RetriveData()   //this method retrives user data from 'database.txt' and put it in 'UserData' class
    {
        String FirstName,FamilyName,PostCode,CreditCard;
        int Balance,Credit;
        String fileName = "database.txt";
        String line = null;
        UserData[] userData = new UserData[20];
        int i=0;
        try {
            FileReader fileReader = new FileReader(fileName);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            while((line = bufferedReader.readLine()) != null) {
                //System.out.println(line);
                StringTokenizer st = new StringTokenizer(line,"#");
                FirstName = st.nextToken();
                FamilyName = st.nextToken();
                PostCode = st.nextToken();
                CreditCard = st.nextToken();
                Balance = Integer.parseInt(st.nextToken());
                Credit = Integer.parseInt(st.nextToken());
                //System.out.println("F -> "+FirstName+" Fam -> "+FamilyName+" Post ->"+PostCode+" CreditCardno ->"+CreditCard+" Balance -> "+Balance+" Credit -> "+Credit);
                userData[i++]= new UserData(FirstName,FamilyName,PostCode,CreditCard,Balance,Credit);
            }
            userData[i++] = new UserData("end","nai","nai","nai",0,0);

            bufferedReader.close();
        }
        catch (Exception e) {
            System.out.println("exception occour : "+e);
        }
        return userData;
    }

    public int CheckInfo(String UserInfo,UserData[] userData) //this method checks if the client given informations
    {                                                           //match with any user data
        String FirstName1,FamilyName1,PostCode1,CreditCard1;
        StringTokenizer st = new StringTokenizer(UserInfo,"#");
        FirstName1 = st.nextToken();
        FamilyName1 = st.nextToken();
        PostCode1 = st.nextToken();
        CreditCard1 = st.nextToken();

        int i=0;
        for (i=0;i<20;i++)
        {
            if (userData[i].FirstName.equalsIgnoreCase("end")) return i;
            else if (userData[i].FirstName.equalsIgnoreCase(FirstName1) && userData[i].FamilyName.equalsIgnoreCase(FamilyName1) && userData[i].PostCode.equalsIgnoreCase(PostCode1) && userData[i].CreditCard.equalsIgnoreCase(CreditCard1)){
                //System.out.println("i koto pathasse? = "+i);
                return i;
            }
        }
        return i;
    }


    public static void main(String args[])
    {
        int port=8888; //using a default port in case user doesn't
                        //give a port number explicitly.
        if (args.length==1) port= Integer.parseInt(args[0]);  //user given port number.
        BankServer server = new BankServer(port); //Bank server will run in this port.
    }
}
