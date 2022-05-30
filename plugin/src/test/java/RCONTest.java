import com.crypteam.rpc.*;
import org.junit.Test;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import static org.junit.jupiter.api.Assertions.*;



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

    @Test
    public void redisTest () throws InterruptedException, IOException {
        JedisPool pool = new JedisPool("localhost", 6379);
        Jedis subscriber = pool.getResource();
        Jedis publisher = pool.getResource();


        RPCSubscriber listener = new RPCSubscriber();
        new RPCThread(subscriber, listener).start();

        Thread.sleep(1000);

        RPCRequest request = new RPCRequest(RPCCommand.AUTHORIZE_USER, new byte[] {1, 2, 3, 4, 5});
        publisher.publish("rpc", Serializer.serialize(request));
        Thread.sleep(1000*5);
        listener.unsubscribe();
    }

    @Test
    public void serializerTest () throws IOException, ClassNotFoundException {
        RPCRequest request = new RPCRequest(RPCCommand.READ_DATA, new byte[] {1, 2, 3, 4});

        byte[] data = Serializer.serialize(request, true);
        RPCRequest request1 = (RPCRequest) Serializer.deserialize(data, true);

        assertEquals(request1.command, RPCCommand.READ_DATA);
        assertArrayEquals(request.payload, request1.payload);

        request = new RPCRequest(RPCCommand.READ_DATA, new byte[] {1, 2, 3, 4});

        String str_data = Serializer.serialize(request);
        request1 = (RPCRequest) Serializer.deserialize(str_data);

        assertEquals(request1.command, RPCCommand.READ_DATA);
        assertArrayEquals(request.payload, request1.payload);
    }
}
