package sample;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
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
        return buffer.toString().toLowerCase(Locale.ROOT);
    }

    public static StringBuilder wholeAnswer;

    public static byte[] mask = {(byte) 255, (byte) 255, (byte) 255, 0};

    public static String scan() throws Exception {
        wholeAnswer = new StringBuilder();
        byte[] localAddress = InetAddress.getLocalHost().getAddress();

        wholeAnswer.append("Local host:\n    Name: ")
                .append(InetAddress.getLocalHost().getHostName())
                .append("\n    MAC-address: ")
                .append(macToString(NetworkInterface.getByInetAddress(InetAddress
                        .getLocalHost()).getHardwareAddress()))
                .append("\n    Local IP: ");

        Enumeration<NetworkInterface> allNI = NetworkInterface.getNetworkInterfaces();
        ArrayList<InetAddress> localAddresses = new ArrayList<>();

        while(allNI.hasMoreElements())
        {
            NetworkInterface ni = allNI.nextElement();
            Enumeration<InetAddress> addresses = ni.getInetAddresses();
            while (addresses.hasMoreElements())
            {
                InetAddress ia = addresses.nextElement();
                byte[] address = ia.getAddress();

                if(address[0] == localAddress[0] || address[1] ==  localAddress[1]) {
                    localAddresses.add(ia);
                    wholeAnswer.append(ia.getHostAddress()).append("\n                  ");
                }
            }
        }

        System.out.println("Local addresses ready to scan: " + localAddresses);
        System.out.println("Starting scan...");
        wholeAnswer.append("\n===================================================================================\n");
        for (InetAddress value : localAddresses) {
            wholeAnswer.append(scanLocalNetwork(value.getAddress()));
        }
        System.out.println(wholeAnswer.toString());
        System.out.println("Success.");
        return wholeAnswer.toString();
    }

    public static List<String> findInterfaces() throws UnknownHostException, SocketException {
        byte[] localAddress = InetAddress.getLocalHost().getAddress();

        Enumeration<NetworkInterface> allNI = NetworkInterface.getNetworkInterfaces();
        List<String> localAddresses = new ArrayList<>();

        while(allNI.hasMoreElements())
        {
            NetworkInterface ni = allNI.nextElement();
            Enumeration<InetAddress> addresses = ni.getInetAddresses();
            while (addresses.hasMoreElements())
            {
                InetAddress ia = addresses.nextElement();
                byte[] address = ia.getAddress();

                if(address[0] == localAddress[0] || address[1] ==  localAddress[1]) {
                    localAddresses.add((0xFF & address[0]) + "." + (0xFF & address[1])
                            + "." + (0xFF & address[2]));
                }
            }
        }

        return localAddresses;
    }

    static class ScanIP implements Callable<String> {

        private final byte[] addr;

        public ScanIP(byte[] inAddr) {
            addr = inAddr;
        }

        @Override
        public String call() throws IOException {
            if(InetAddress.getByAddress(addr).isReachable(1000)) {
                return (0xFF & addr[0]) + "." + (0xFF & addr[1]) + "." + (0xFF & addr[2]) + "." + (0xFF & addr[3]);
            } else {
                return null;
            }
        }
    }

    public static String scanLocalNetwork(byte[] subnet) throws Exception {
        StringBuilder answer = new StringBuilder("Subnet " + (0xFF & subnet[0]) + "." + (0xFF & subnet[1]) + "." + (0xFF & subnet[2]) + ":\n");
        System.out.println("Subnet " + (0xFF & subnet[0]) + "." + (0xFF & subnet[1]) + "." + (0xFF & subnet[2]) + ":");
        try {
            ExecutorService executor = Executors.newFixedThreadPool(10);
            ArrayList<Future<String>> reachableAddresses = new ArrayList<>();
            System.out.println("   Scanning reachable addresses...");
            for (int i = 1; i <= (0xFF & mask[3] - 2); i++) {
                InetAddress address = InetAddress.getByAddress(new byte[]{subnet[0], subnet[1], subnet[2], (byte) i});
             //   System.out.println(address);

                Callable<String> localScan = new ScanIP(address.getAddress());
                reachableAddresses.add(executor.submit(localScan));
            }

            System.out.println("   Getting MAC-addresses...");
            for (Future<String> future : reachableAddresses) {
                String address = future.get();
                if (address == null) continue;
                if(wholeAnswer != null && wholeAnswer.indexOf(address) > -1)  {
                    continue;
                };
                System.out.println("      Getting answer from " + address + "...");
                String cmdAnswer = getARPAnswer(address);
                if(cmdAnswer == null) {
                    cmdAnswer = "MAC-address: " + macToString(NetworkInterface
                            .getByInetAddress(InetAddress.getByAddress(strToAddr(address, 4))).getHardwareAddress());
                }

                answer.append("   IP-address: ").append(address)
                        .append("\n   ").append(cmdAnswer)
                        .append("\n   Name: ").append(InetAddress.getByAddress(strToAddr(address, 4)).getCanonicalHostName())
                        .append("\n----------------------------------------------------------------\n");

                System.out.println("      " + cmdAnswer);
            }
            executor.shutdown();
        } catch (UnknownHostException e) {
            throw new Exception("Unknown host error.");
        }

        if(answer.indexOf("MAC") == -1) {
            answer.append("   No nodes");
        }
        System.out.println("Local success.");
        return answer.toString();
    }

    public static byte[] strToAddr(String addr, int count) {
        byte[] answer = new byte[count];
        addr = addr + ".";
        for(int i = 0; i < count; i++) {
            answer[i] = (byte) Integer.parseInt(addr.substring(0, addr.indexOf('.')));
            addr = addr.substring(addr.indexOf('.') + 1);
        }

        return answer;
    }

    private static String getARPAnswer(String addr) {
        getCmdAnswer("arp refresh");
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
