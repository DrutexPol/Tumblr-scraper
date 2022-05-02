public class ScrapingProxy {
    private String address;
    private String port;
    private String username;
    private String password;

    public ScrapingProxy(String address, String port, String username, String password) {
        this.address = address;
        this.port = port;
        this.username = username;
        this.password = password;
    }

    public String getAddress() {
        return address;
    }

    public String getPort() {
        return port;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    @Override
    public String toString() {
        String ret = null;
        if (this.username.equals("")) {
            ret = address + ':' + port;
        } else {
            ret = address + ':' + port + ':' + username + ':' + password ;
        }
        return ret;
    }
}


