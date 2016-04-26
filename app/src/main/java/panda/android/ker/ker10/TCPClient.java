package panda.android.ker.ker10;

import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

/**
 * Created by Harry on 4/7/2016.
 */
public class TCPClient {
    private String serverMessage;
    private String LOG_TAG = "TCPClient";
    //public String SERVERIP = "128.199.68.171";public int SERVERPORT = 80; //your computer IP address
    public String SERVERIP = "192.168.1.104";public int SERVERPORT = 2711;
    //public int SERVERPORT = 80;

    public boolean Isconnected()
    {
        return errorr==false;
    }

    PrintWriter out;
    BufferedReader in;

    private Socket socket;

    public TCPClient() {
        SERVERIP = "192.168.1.104";SERVERPORT = 2711;
        errorr=false;
        try {
            Log.e("TCP Client", "C: Connecting...");
            InetAddress serverAddr = InetAddress.getByName(SERVERIP);
            //create a socket to make the connection with the server
            socket = new Socket(serverAddr, SERVERPORT);
            out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
            //receive the message which the server sends back
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        }
        catch (Exception e){
            Log.e("TCP Client", "can not send, server got problem",e);
            errorr = true;
        }

    }

    public boolean sendMessage(String message){
        if (out != null && !out.checkError()) {
            out.println(message);
            out.flush();
            return true;
        }
        else
        {
            Log.w(LOG_TAG,"buffer == null, no message sent");
            return false;
        }
    }
    public void stopClient(){
        try{
            socket.close();
        }
        catch (Exception e){}

    }

    public boolean errorr;

}
