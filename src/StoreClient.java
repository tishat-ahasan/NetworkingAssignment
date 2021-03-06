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
        String info = processInfo(Info);  //processes the user info in the
                                          //format FirstName#FamilyName#PostCode#CreditCardNo

        try
        {
            socket = new Socket(address, port);  // establish a connection
            System.out.println("Connected");
            input = new DataInputStream(System.in);
            out = new DataOutputStream(socket.getOutputStream());
            in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));

            String line = "";
            line=in.readUTF();
            System.out.println(line);
            out.writeUTF("hi from client");
            String userInfo=info;
            out.writeUTF(userInfo); //sends userinfo to Bankserver
            line=in.readUTF();      //recieves bankserver response
            if (line.equalsIgnoreCase("NoUserFound")) { //if user not found close the connection
                result = "NoUser";
            }
            else if (line.equalsIgnoreCase("UserFound"))
            {
                StringTokenizer st = new StringTokenizer(info,"#");
                String s="";
                while (st.hasMoreTokens())
                {
                    s=st.nextToken();
                }
                int price= Integer.parseInt(s);

                out.writeUTF(""+price);  //if user found.send the transaction ammount

                line = in.readUTF();
                if (line.equalsIgnoreCase("insufficientBalnce"))
                {
                    result="NoMoney";
                }
                else if (line.equalsIgnoreCase("DoneSuccessfully!"))
                {
                    result="Success";
                }
            }
            input.close();
            out.close();
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String processInfo(String info)
    {
        StringTokenizer st = new StringTokenizer(info,"&=");
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
        if (cdid.equalsIgnoreCase("01") || cdid.equalsIgnoreCase("1") ) price=50;
        else if (cdid.equalsIgnoreCase("02") || cdid.equalsIgnoreCase("2")) price=60;
        else if (cdid.equalsIgnoreCase("03") || cdid.equalsIgnoreCase("3")) price=40;
        st.nextToken();
        int quantity = Integer.parseInt(st.nextToken());
        price = price*quantity;
        data += ""+price;
        return data;
    }
}
