package net.comboro.server.files;

public class ServerInfoFile extends ExternalFile {

    private String name = "SimpleNet Server";
    private int port = 47247;
    private int maxCharsPerLine = 200;
    private boolean debugging = false;

    ServerInfoFile() {
        super("server.info");

        // Update variables
        String line;
        while((line=readLine())!=null){
            if (line.startsWith("//"))
                continue;
            if (line.contains(":")) {
                String[] split = line.split(":");
                String variable = split[0].trim();
                String value = split[1].trim();
                switch (variable) {
                    case "Name":
                        name = String.valueOf(value);
                        break;
                    case "Port":
                        port = Integer.valueOf(value);
                        break;
                    case "Debugging":
                        debugging = Boolean.valueOf(value);
                        break;
                    case "MaxCharsPerLine":
                        setMaxCharsPerLine(Integer.valueOf(value));
                        break;
                }
            }
        }
    }

    @Override
    protected void close() {
        clearFile();
        println("Name: " + getName());
        println("Port: " + getPort());
        println("Debugging: " + isDebugging());
        println("MaxCharsPerLine: " + getMaxCharsPerLine());
    }

    public void changeName(String name){
        this.name = name;
    }

    public void changePort(int port){
        this.port = port;
    }

    public void setDebugging(boolean debugging){
        this.debugging = debugging;
    }

    public String getName() {
        return name;
    }

    public int getPort() {
        return port;
    }

    public boolean isDebugging() {
        return debugging;
    }

    public int getMaxCharsPerLine() {
        return maxCharsPerLine;
    }

    public void setMaxCharsPerLine(int maxCharsPerLine) {
        this.maxCharsPerLine = maxCharsPerLine;
    }
}
