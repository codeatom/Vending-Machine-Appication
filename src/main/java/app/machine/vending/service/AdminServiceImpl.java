package app.machine.vending.service;

public class AdminServiceImpl implements AdminService {

    private static final String ADMIN_USERNAME = "admin";
    private static final String ADMIN_PASSWORD = "admin123";

    private boolean authenticated = false;

    @Override
    public boolean login(String username, String password) {

        if (ADMIN_USERNAME.equals(username)
                && ADMIN_PASSWORD.equals(password)) {

            authenticated = true;
            return true;
        }

        return false;
    }

    @Override
    public void logout() {
        authenticated = false;
    }

    @Override
    public boolean isAuthenticated() {
        return authenticated;
    }
}