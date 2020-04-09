import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

/**
 * 浏览器访问 localhost:8080 依旧出现问题
 * 或者postman 访问 localhost:8080 get请求 多次出现问题
 */
public class HTTPServer {
    private static final int PORT = 8080;
    private ServerSocket server;

    private Listener listener;

    public HTTPServer() {
        try {
            server = new ServerSocket(PORT);
            listener = new Listener();
            listener.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void close() {
        listener.shutdown();
    }

    private class Listener extends Thread {
        public void shutdown() {
            try {
                server.close();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                this.interrupt();
            }
        }

        @Override
        public void run() {
            Socket client = null;
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    client = server.accept();
                    System.out.println(client);
                    InputStream in = client.getInputStream();
                    BufferedInputStream bis = new BufferedInputStream(in);
                    byte[] buf = null;
                    buf = new byte[bis.available()];
                    int len = bis.read(buf);
                    if (len <= 0) {
                        throw new RuntimeException("问题出现了");
                    }
                    System.out.println(new String(buf));
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        client.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public static void main(String[] args) {
        HTTPServer server = new HTTPServer();
        Scanner scanner = new Scanner(System.in);
        String order;
        while (scanner.hasNext()) {
            order = scanner.next();
            if (order.equals("EXIT")) {
                server.close();
            }
        }
    }
}

