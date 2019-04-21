// A Java program for a Server
import javafx.geometry.Pos;

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
    public BankServer(int port) {
        // starts server and waits for a connection
        try {
        server = new ServerSocket(port);
            while (true){
                System.out.println("Server started");

                System.out.println("Waiting for a client ...");

                socket = server.accept();
                System.out.println("Client accepted");

                // takes input from the client socket
                in = new DataInputStream(
                        new BufferedInputStream(socket.getInputStream()));

                //send output to client socket
                out = new DataOutputStream(socket.getOutputStream());

                String UserInfo = "";

                out.writeUTF("hi from server");
                in.readUTF(); //hi from client
                UserInfo = in.readUTF();
                System.out.println("user info sent from client -> " + UserInfo);


                //retrive all data from file
                UserData[] userData = RetriveData();
                int userNo = CheckInfo(UserInfo, userData);
                if (userData[userNo].FirstName.equalsIgnoreCase("end")) {
                    System.out.println("No User Found.Connection Close");
                    out.writeUTF("NoUserFound");
                } else {
                    System.out.println("User Found.Baki kaj ekhane");
                    out.writeUTF("UserFound");

                    //baki ongso ta ekhane
                    String p = in.readUTF();
                    int price = Integer.parseInt(p);
                    int userCredit, userBalance;
                    userCredit = userData[userNo].Credit;
                    userBalance = userData[userNo].Balance;
                    if (userCredit < price) {
                        System.out.println("Taka nai,muri khan");
                        out.writeUTF("insufficientBalnce");
                    } else {
                        userData[userNo].Credit -= price;
                        userData[userNo].Balance -= price;
                        System.out.println("Kena Complete");
                        out.writeUTF("DoneSuccessfully!");
                        saveData(userData);
                    }

                }


                // reads message from client until "Over" is sent
                /*while (!line.equals("Over"))
                {
                    try
                    {
                        line = in.readUTF();
                        System.out.println(line);

                    }
                    catch(IOException i)
                    {
                        System.out.println(i);
                    }
                }*/
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

    public void saveData(UserData[] userData)
    {
        File file = new File("data.txt");
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
                System.out.println("write kortise --->  " + userinfo);
                writer.write(userinfo+"\n");
            }
            //writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public UserData[] RetriveData()
    {
        String FirstName,FamilyName,PostCode,CreditCard;
        int Balance,Credit;
        String fileName = "data.txt";
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

    public int CheckInfo(String UserInfo,UserData[] userData)
    {
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
                System.out.println("i koto pathasse? = "+i);
                return i;
            }

        }


        return i;
    }


    public static void main(String args[])
    {
        BankServer server = new BankServer(5000);

    }
}
