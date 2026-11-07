package app.machine.vending.service;

public interface AdminService {

    boolean login(String username, String password);

    void logout();

    boolean isAuthenticated();
}