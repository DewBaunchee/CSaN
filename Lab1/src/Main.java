import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;

public class Main {

    public static String scanLocalNetwork() {
        StringBuilder answer = new StringBuilder();
        try {
            for (byte j = 0; j < 2; j++) {
                for(int i = 1; i < 255; i++) {
                    InetAddress address = InetAddress.getByAddress(new byte[]{(byte) 192, (byte) 168, j, (byte) i});
                    NetworkInterface networkInterface = NetworkInterface.getByInetAddress(address);
                    if(networkInterface != null) {
                        byte[] mac = networkInterface.getHardwareAddress();
                        if (mac != null) {
                            answer.append("Address: ").append(address.getHostAddress()).append("\n")
                                    .append("MAC-address: ");
                            for (int k = 0; k < mac.length; k++) {
                                answer.append(String.format("%02X%s",
                                        mac[k], (k < mac.length - 1) ? "-" : ""));
                            }
                            answer.append("\n").append("--------------------------------------------------");
                        }
                    }
                }
            }
        } catch (UnknownHostException | SocketException e) {
            System.out.println("Sorry, Dobby cannot..." );
        }
        return answer.toString();
    }

    public static void main(String[] args) {
        System.out.println(scanLocalNetwork());
    }
}
