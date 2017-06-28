package com.fuzzjump.server.base;

import com.steveadoo.server.base.ServerConfig;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;

public class FuzzJumpServerConfig extends ServerConfig {

    private static final int VALIDATE_TIMEOUT = 5000;

    //the port that players use for direct connection
    public final int privatePort;
    //the ip of this machine
    public final String ip;
    //api address
    public final String apiAddress;
    //api username
    public final String apiUsername;
    //api password
    public final String apiPassword;

    public FuzzJumpServerConfig(int port,
                                int privatePort,
                                String ip,
                                String apiAddress,
                                String apiUsername,
                                String apiPassword) {
        super(port, VALIDATE_TIMEOUT);
        this.privatePort = privatePort;
        this.ip = ip;
        this.apiAddress = apiAddress;
        this.apiUsername = apiUsername;
        this.apiPassword = apiPassword;
    }

    public FuzzJumpServerConfig(FuzzJumpServerConfig config) {
        this(config.port, config.privatePort, config.ip, config.apiAddress, config.apiUsername, config.apiPassword);
    }

    public static FuzzJumpServerConfig loadConfig(String[] args) throws IOException {
        String portStr = System.getenv("FUZZ_PORT");
        String privatePortStr = System.getenv("FUZZ_PRIVATE_PORT");
        String apiAddress = System.getenv("FUZZ_API");
        String apiUsername = System.getenv("FUZZ_API_USERNAME");
        String apiPassword = System.getenv("FUZZ_API_PASSWORD");
        String onAwsStr = System.getenv("FUZZ_AWS");
        if (onAwsStr == null || onAwsStr.equals("")) {
            onAwsStr = "false";
        }
        boolean onAws = Boolean.parseBoolean(onAwsStr);
        String directIp = getDirectIp(onAws);
        return new FuzzJumpServerConfig(
                Integer.parseInt(portStr),
                Integer.parseInt(privatePortStr),
                directIp,
                apiAddress,
                apiUsername,
                apiPassword
        );
    }

    public static String getDirectIp(boolean onAws) throws IOException {
        if (!onAws) {
            return InetAddress.getLocalHost().getHostAddress();
        }
        StringBuilder result = new StringBuilder();
        URL url = new URL("http://169.254.169.254/latest/meta-data/public-hostname");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String line;
        while ((line = rd.readLine()) != null) {
            result.append(line);
        }
        rd.close();
        return result.toString();
    }

}
