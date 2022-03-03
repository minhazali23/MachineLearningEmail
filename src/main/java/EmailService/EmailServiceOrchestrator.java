package EmailService;

import EmailService.Gmail.GmailAuthenticatorService;
import EmailService.Gmail.GmailMessageExtractor;
import Model.User;

import java.io.IOException;
import java.security.GeneralSecurityException;

public class EmailServiceOrchestrator {

    GmailAuthenticatorService gmailService = new GmailAuthenticatorService();
    GmailMessageExtractor gmailMessageExtractor = new GmailMessageExtractor();
    User user = new User();

    public void startGmailService() throws GeneralSecurityException, IOException {
        gmailMessageExtractor.extractMessagesPerPage(gmailService.getGmailService(), user);
    }

}
