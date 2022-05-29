import org.junit.Test;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ShortBuffer;

public class RCONTest {

    @Test
    public void readRegionTest () throws IOException {
        Socket socket = new Socket();
        socket.connect(new InetSocketAddress(4004));

        DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
        DataInputStream dis = new DataInputStream(socket.getInputStream());

        dos.writeInt(0); // command
        dos.writeInt(5); // region id

        dos.flush();

        int length = dis.readInt();

        byte[] data = new byte[length * 2];
        dis.readFully(data);

        System.out.println(data.length);
    }
}
