package deakin.gopher.guardian.DataBase.DataClasses;

public class Login {

    private String LoginID;
    private String ISLogin;
    private String UserType;
    private String Password;

    private String IsResetPassword;

    public String getLoginID() {
        return LoginID;
    }

    public void setLoginID(final String loginID) {
        LoginID = loginID;
    }

    public String getISLogin() {
        return ISLogin;
    }

    public void setISLogin(final String ISLogin) {
        this.ISLogin = ISLogin;
    }

    public String getUserType() {
        return UserType;
    }

    public void setUserType(final String userType) {
        UserType = userType;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(final String password) {
        Password = password;
    }

    public String getIsResetPassword() {
        return IsResetPassword;
    }

    public void setIsResetPassword(final String isResetPassword) {
        IsResetPassword = isResetPassword;
    }
}
