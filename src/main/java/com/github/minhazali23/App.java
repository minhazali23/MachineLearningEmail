package com.github.minhazali23;
import EmailService.EmailServiceOrchestrator;

import java.io.IOException;
import java.security.GeneralSecurityException;


public class App
{
    public static void main( String[] args ) throws GeneralSecurityException, IOException {
        EmailServiceOrchestrator newServiceInstance = new EmailServiceOrchestrator();
        newServiceInstance.startGmailService();
    }




}
