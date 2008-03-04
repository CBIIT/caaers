package gov.nih.nci.cabig.caaers.web.user;

import gov.nih.nci.cabig.caaers.service.UserService;
import gov.nih.nci.cabig.caaers.service.security.PasswordManagerService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.web.servlet.mvc.SimpleFormController;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.validation.BindException;

/**
 * @author Jared Flatow
 */
public class ResetPasswordController extends SimpleFormController {

    private PasswordManagerService passwordManagerService;

    private UserService userService;

    private String emailPretext, emailPosttext;

    public ResetPasswordController() {
        setFormView("user/resetPassword");
        setBindOnNewForm(true);
        initEmailText();
    }

    protected Object formBackingObject(HttpServletRequest request) throws Exception {
        return new UserName(request.getScheme(), request.getServerName(), request.getServerPort(),
                        request.getContextPath());
    }

    @Override
    protected ModelAndView onSubmit(Object command, BindException errors) throws Exception {
        UserName userName = (UserName) command;
        String token = passwordManagerService.requestToken(userName.getUserName());
        userService.sendUserEmail(userName.getUserName(), "Reset caAERS Password", emailPretext
                        + userName.getURL() + "&token=" + token + emailPosttext);
        return new ModelAndView("user/emailSent");
    }

    private void initEmailText() {
        emailPretext = ""
                        + "Did you forgot your password?\n"
                        + "Do you want to select a new password?\n"
                        + "\n"
                        + "We cannot send you your password, because we don't know what it is. However, we can help you change your password to something new.\n"
                        + "\n" + "\n" + "To change your password go to the Change Password page:\n"
                        + "\n";
        emailPosttext = "\n"
                        + "\n"
                        + "Enter a new password for yourself once you get there. After you choose a new password, you can log into caAERS using the new password.\n"
                        + "\n"
                        + "\n"
                        + "Regards,\n"
                        + "The caAERS Notification System.\n"
                        + "\n"
                        + "(Note: If you did not request a new password, please disregard this message.)";
    }

    @Required
    public void setPasswordManagerService(PasswordManagerService passwordManagerService) {
        this.passwordManagerService = passwordManagerService;
    }

    @Required
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    public static String getURL(String scheme, String serverName, int serverPort, String contextPath) {
        return scheme + "://" + serverName + ":" + serverPort + contextPath + UserName.CHANGE_PATH;
    }

    public class UserName {
        private static final String CHANGE_PATH = "/public/user/changePassword?";

        private String userName;

        private String url;

        public UserName(String scheme, String serverName, int serverPort, String contextPath) {
            url = ResetPasswordController.getURL(scheme, serverName, serverPort, contextPath);
        }

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
            this.url += "userName=" + userName;
        }

        public String getURL() {
            return url;
        }
    }
}
