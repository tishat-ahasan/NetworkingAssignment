/*
 * Copyright (c) 2019. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class Server{

    Socket socket;
    ServerSocket serverSocket;

    File htmlFile;
    File file;


    Server()throws Exception {

        String responseMessage="";
        responseMessage = ProcessHTML("index.html");
        serverSocket = new ServerSocket(50000);
        while (true){
            try {

                System.out.println("before");
                socket = serverSocket.accept();

                InputStream inputStream = socket.getInputStream();
                OutputStream outputStream = socket.getOutputStream();
                BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));

                while (true) {
                    String receivedMessage = "";
                    while ((receivedMessage = in.readLine()) == null) {
                        System.out.println("-------------------------------------------------> no line");
                        socket = serverSocket.accept();
                        inputStream = socket.getInputStream();
                        outputStream = socket.getOutputStream();
                        in = new BufferedReader(new InputStreamReader(inputStream));
                    }


                    System.out.println("line 70 -------->" + receivedMessage);

                    String receivedMessageArray[] = receivedMessage.split(" ");


                    if (receivedMessageArray[0].equals("GET")) {

                        if (receivedMessage.equals("GET /index.html HTTP/1.1")) {

                            outputStream.write(responseMessage.getBytes());

                        } else {

                        }
                    } else if (receivedMessageArray[0].equals("POST")) {
                        String postMessage = "";
                        int contentLength = 0;
                        while (true) {
                            postMessage = in.readLine();
                            System.out.println("found ---> " + postMessage);
                            String parts[] = postMessage.split(" ");
                            if (parts[0].equals("Content-Length:")) {
                                contentLength = Integer.valueOf(parts[1]);
                                System.out.println("found content length = " + contentLength);
                            }
                            if (postMessage.equals("")) {
                                System.out.println("line break");
                                break;
                            }
                        }

                        byte b[] = new byte[contentLength + 1];
                        for (int i = 0; i < contentLength; i++) {
                            b[i] = (byte) in.read();
                            System.out.print((char) b[i]);
                        }
                        b[contentLength] = '&';
                        System.out.println("PURA b[] te ki ache dekhi nicer line e---------- >");

                        String userInfo = new String(b);
                        StoreClient storeClient = new StoreClient("127.0.0.1",5000,userInfo);
                        String result = storeClient.getResult();
                        //System.out.println(userInfo);
                        // "NoUser","NoMoney" ,"Success"
                        String responseMessage2 = "";
                        if(result.equalsIgnoreCase("NoUser")){
                            responseMessage2 = ProcessHTML("NoUser.html");
                        }
                        else if (result.equalsIgnoreCase("NoMoney"))
                        {
                            responseMessage2 = ProcessHTML("NoMoney.html");
                        }
                        else if(result.equalsIgnoreCase("Success"))
                        {
                            responseMessage2 = ProcessHTML("success.html");
                        }
                        else {
                            responseMessage2 = ProcessHTML("Error.html");
                        }


                        outputStream.write(responseMessage2.getBytes());

                        socket.close();
                    }


                }


            } catch (Exception e) {
                System.out.println(e);
            }
        }

    }



    public String ProcessHTML(String filename)
    {
        String data="";
        try {
            BufferedReader br = new BufferedReader(new FileReader(filename));

            String str = "";
            while ((str = br.readLine()) != null) {
                data += str+"\n";
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        String message="";
        message += "HTTP/1.1 200 OK\n";
        message += "Content-Length: " + data.length() + "\n";
        message += "Content-Type: text/html\n";
        message += "Connection: keep-alive\r\n";
        message += "\r\n";
        message += data;
        System.out.println(message);
        return message;
    }
}
