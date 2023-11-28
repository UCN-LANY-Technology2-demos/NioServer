import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class NioServer {

    public static void main(String[] args) throws Exception {
        // Create a selector
        Selector selector = Selector.open();

        // Open a server socket channel
        ServerSocketChannel serverSocket = ServerSocketChannel.open();
        serverSocket.bind(new InetSocketAddress(8080));
        serverSocket.configureBlocking(false);

        // Register the server socket with the selector for accept operations
        serverSocket.register(selector, SelectionKey.OP_ACCEPT);

        // Create a buffer for reading data
        ByteBuffer buffer = ByteBuffer.allocate(256);

        while (true) {
            // Wait for an event
            selector.select();

            // Get the set of ready keys
            Set<SelectionKey> selectedKeys = selector.selectedKeys();
            Iterator<SelectionKey> iter = selectedKeys.iterator();

            while (iter.hasNext()) {
                SelectionKey key = iter.next();

                // Check if the key is ready for new connection
                if (key.isAcceptable()) {
                    // Accept the connection
                    SocketChannel client = serverSocket.accept();
                    client.configureBlocking(false);

                    // Register the socket with the selector for read operations
                    client.register(selector, SelectionKey.OP_READ);
                }

                // Check if the key is ready for reading
                if (key.isReadable()) {
                    SocketChannel client = (SocketChannel) key.channel();

                    // Read data to buffer and print
                    buffer.clear();
                    int numRead = client.read(buffer);
                    if (numRead > 0) {
                        String receivedString = new String(buffer.array()).trim();
                        System.out.println("Received: " + receivedString);
                    }
                }

                iter.remove();
            }
        }
    }
}
