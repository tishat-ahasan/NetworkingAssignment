// A Java program for a Client
import java.net.*;
import java.io.*;
import java.util.StringTokenizer;

public class StoreClient
{
    // initialize socket and input output streams
    private Socket socket		 = null;
    private DataInputStream input = null;
    private DataOutputStream out	 = null;
    private DataInputStream in	 = null;
    private String result = "abcd";  // "NoUser","NoMoney" ,"Success"

    public String getResult() {
        return result;
    }

    // constructor to put ip address and port
    public StoreClient(String address, int port,String Info)
    {
        String info = processInfo(Info);
        // establish a connection
        try
        {
            socket = new Socket(address, port);
            System.out.println("Connected");

            // takes input from terminal
            input = new DataInputStream(System.in);

            // sends output to the socket
            out = new DataOutputStream(socket.getOutputStream());
            //input from server
            in = new DataInputStream(
                    new BufferedInputStream(socket.getInputStream()));
        }
        catch(UnknownHostException u)
        {
            System.out.println(u);
        }
        catch(IOException i)
        {
            System.out.println(i);
        }

        // string to read message from input
        String line = "";

        // keep reading until "Over" is input
        try {
            line=in.readUTF();
            System.out.println(line);
            out.writeUTF("hi from client");
            String userInfo=info;
            out.writeUTF(userInfo);
            line=in.readUTF();
            if (line.equalsIgnoreCase("NoUserFound")) {
                result = "NoUser";
                System.out.println("No User found");
            }
            else if (line.equalsIgnoreCase("UserFound"))
            {
                System.out.println("user found\nsend baki information");

                StringTokenizer st = new StringTokenizer(info,"#");
                String s="";
                while (st.hasMoreTokens())
                {
                    s=st.nextToken();
                }
                int price= Integer.parseInt(s);

                out.writeUTF(""+price);

                line = in.readUTF();
                if (line.equalsIgnoreCase("insufficientBalnce"))
                {
                    result="NoMoney";
                    System.out.println("taka nai pagla");
                }
                else if (line.equalsIgnoreCase("DoneSuccessfully!"))
                {
                    result="Success";
                    System.out.println("kena hoise thik thak!");
                }
            }
            input.close();
            out.close();
            socket.close();

        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public String processInfo(String info)
    {
        StringTokenizer st = new StringTokenizer(info,"&=");
        /*while (st.hasMoreTokens())
        {
            System.out.println("tokenizer er kaj ---->"+st.nextToken());
        }*/
        String data ="";
        st.nextToken();
        data += st.nextToken()+"#";     //name
        st.nextToken();
        data += st.nextToken()+"#";      //familyname
        st.nextToken();
        data += st.nextToken()+"#";      //postcode
        st.nextToken();
        data += st.nextToken()+"#";      //credicardno
        st.nextToken();
        String cdid = st.nextToken();
        int price=0;
        if (cdid.equalsIgnoreCase("01")) price=50;
        else if (cdid.equalsIgnoreCase("02")) price=60;
        else if (cdid.equalsIgnoreCase("03")) price=40;
        st.nextToken();
        int quantity = Integer.parseInt(st.nextToken());
        price = price*quantity;
        data += ""+price;
        System.out.println("tokeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeen   "+data);

        return data;
    }

}
