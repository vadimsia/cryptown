import com.crypteam.rpc.*;
import com.crypteam.rpc.requests.ReadDataRequest;
import com.crypteam.rpc.requests.ReadDataResponse;
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

        RPCRequest request = new RPCRequest(RPCCommand.AUTHORIZE_USER);
        publisher.publish("rpc", Serializer.serialize(request));
        Thread.sleep(1000*5);
        listener.unsubscribe();
    }

    @Test
    public void readDataRequestTest () throws IOException, ClassNotFoundException {
        ReadDataRequest request = new ReadDataRequest(1);

        String data = Serializer.serialize(request);
        RPCRequest request1 = (RPCRequest) Serializer.deserialize(data);

        ReadDataRequest typed_request = (ReadDataRequest) request1;

        assertEquals(typed_request.command, RPCCommand.READ_DATA);
        assertEquals(typed_request.area_id, 1);
    }

    @Test
    public void readDataResponseTest () throws IOException, ClassNotFoundException {
        ReadDataRequest request = new ReadDataRequest(1);
        ReadDataResponse response = new ReadDataResponse(request, new short[] {1,2,3,4,5});

        String data = Serializer.serialize(response);
        RPCRequest request1 = (RPCRequest) Serializer.deserialize(data);

        ReadDataResponse typed_response = (ReadDataResponse) request1;

        assertEquals(typed_response.command, RPCCommand.READ_DATA_RESPONSE);
        assertEquals(request.uuid, typed_response.uuid);
        assertArrayEquals(typed_response.payload, new short[] {1,2,3,4,5});
    }
}
