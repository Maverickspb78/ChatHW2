package project;

public interface AuthService {

    void addUser(String name, String pass);

    boolean auth(String name, String pass);

}