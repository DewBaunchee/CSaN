package sample;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MacScanner {

    public static String MACRegex = "([\\da-fA-F]{2}-){5}[\\da-fA-F]{2}";

    public static String macToString(byte[] mac) {
        if (mac == null) return "null";
        StringBuilder buffer = new StringBuilder();
        for (int k = 0; k < mac.length; k++) {
            buffer.append(String.format("%02X%s",
                    mac[k], (k < mac.length - 1) ? "-" : ""));
        }
        return buffer.toString().toUpperCase(Locale.ROOT);
    }

    public static String scan() throws Exception {
        StringBuilder answer = new StringBuilder();

        answer.append("Local host:\n    Name: ")
                .append(InetAddress.getLocalHost().getHostName())
                .append("\n    MAC-address: ")
                .append(macToString(NetworkInterface.getByInetAddress(InetAddress
                        .getLocalHost()).getHardwareAddress()))
                .append("\n    Local IP: ");

        Enumeration<NetworkInterface> allNI = NetworkInterface.getNetworkInterfaces();
        ArrayList<InetAddress> localAddresses = new ArrayList<>();

        while(allNI.hasMoreElements())
        {
            Enumeration<InetAddress> addresses = allNI.nextElement().getInetAddresses();
            while (addresses.hasMoreElements())
            {
                InetAddress ia = addresses.nextElement();
                byte[] address = ia.getAddress();

                if(address[0] == (byte) 192 || address[1] == (byte) 168) {
                    localAddresses.add(ia);
                    answer.append(ia.getHostAddress()).append("\n              ");
                }
            }
        }

        System.out.println("Starting scan...");
        answer.append("\n===================================================================================");
        for (InetAddress value : localAddresses) {
            answer.append(scanLocalNetwork(value.getAddress()));
        }
        return answer.toString();
    }

    public static String scanLocalNetwork(byte[] subnet) throws Exception {
        StringBuilder answer = new StringBuilder("Subnet " + (0xFF & subnet[0]) + "." + (0xFF & subnet[1]) + "." + (0xFF & subnet[2]) + ":");
        System.out.println("Subnet " + (0xFF & subnet[0]) + "." + (0xFF & subnet[1]) + "." + (0xFF & subnet[2]) + ":");
        try {
            int index = 1;
            ArrayList<String> reachableAddresses = new ArrayList<>();
            System.out.println("Scanning reachable addresses...");
            for (int i = 1; i < 255; i++) {
                InetAddress address = InetAddress.getByAddress(new byte[]{subnet[0], subnet[1], subnet[2], (byte) i});

                System.out.println(address);
                if (address.isReachable(10)) {
                    reachableAddresses.add(address.toString().substring(1));
                    System.out.println("Reachable address #" + index++
                            + ": " + reachableAddresses.get(index - 2));
                }
            }

            System.out.println("Getting MAC-addresses...");
            for (String addr : reachableAddresses) {
                if(answer.indexOf(addr) > -1) continue;
                String cmdAnswer = getNameAndMACAnswer(addr);

                answer.append("IP-address: ")
                        .append(addr)
                        .append('\n').append(cmdAnswer)
                        .append("\n----------------------------------------------------------------\n");

                System.out.println(cmdAnswer);
            }
            System.out.println("Success.");
        } catch (UnknownHostException | SocketException e) {
            throw new Exception("Unknown host error.");
        }

        System.out.println(answer);
        return answer.toString();
    }

    private static String getNameAndMACAnswer(String addr) {
        String command = "nbtstat -a " + addr;
        StringBuilder cmdAnswer = new StringBuilder(getCmdAnswer(command));

        Matcher matcher = Pattern.compile(MACRegex).matcher(cmdAnswer.toString());

        if(matcher.find()) {
            int index = cmdAnswer.indexOf("<");

            return "MAC-address: " + matcher.group() + '\n' +
                    "Name: " + cmdAnswer.substring(cmdAnswer.lastIndexOf("\n", index) + 1, index);
        } else {
            return getARPAnswer(addr);
        }
    }

    private static String getARPAnswer(String addr) {
        String command = "arp -a " + addr;
        String answer = getCmdAnswer(command);
        Matcher matcher = Pattern.compile(MACRegex).matcher(answer);

        if(matcher.find()) {
            return "MAC-address: " + matcher.group();
        } else {
            return null;
        }
    }

    private static String getCmdAnswer(String command) {
        StringBuilder answer = new StringBuilder();
        try
        {
            Process process = Runtime.getRuntime().exec(command);
            Scanner cmdIn = new Scanner(process.getInputStream());
            while(cmdIn.hasNextLine()) answer.append(cmdIn.nextLine());
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        return answer.toString();
    }
}
