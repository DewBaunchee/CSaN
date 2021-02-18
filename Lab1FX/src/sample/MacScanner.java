package sample;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;

public class MacScanner {
    public static String scanLocalNetwork() throws Exception {
        StringBuilder answer = new StringBuilder();
        try {
            for (int j = 0; j < 256; j++) {
                for(int i = 1; i < 255; i++) {
                    InetAddress address = InetAddress.getByAddress(new byte[]{(byte) 192, (byte) 168, (byte) j, (byte) i});
                    System.out.println(address.toString());
                    NetworkInterface networkInterface = NetworkInterface.getByInetAddress(address);
                    if(networkInterface != null) {
                        byte[] mac = networkInterface.getHardwareAddress();
                        if (mac != null) {
                            StringBuilder buffer = new StringBuilder();
                            buffer.append("Address: ").append(address.getHostAddress()).append("\n")
                                    .append("MAC-address: ");
                            for (int k = 0; k < mac.length; k++) {
                                buffer.append(String.format("%02X%s",
                                        mac[k], (k < mac.length - 1) ? "-" : ""));
                            }
                            System.out.println(buffer);
                            answer.append(buffer);
                            answer.append("\n").append("--------------------------------------------------");
                        }
                    }
                }
            }
        } catch (UnknownHostException | SocketException e) {
            throw new Exception("Unknown host error.");
        }
        return answer.toString();
    }
}
