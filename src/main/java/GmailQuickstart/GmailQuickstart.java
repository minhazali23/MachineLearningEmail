package GmailQuickstart;
//testing import
import com.google.api.client.repackaged.org.apache.commons.codec.binary.Base64;
import com.google.api.client.repackaged.org.apache.commons.codec.binary.StringUtils;
//testing import
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.GmailScopes;
import com.google.api.services.gmail.model.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.security.GeneralSecurityException;
import java.util.*;

public class GmailQuickstart {
    private static final String APPLICATION_NAME = "Gmail API Java Quickstart";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final String TOKENS_DIRECTORY_PATH = "tokens";

    /**
     * Global instance of the scopes required by this quickstart.
     * If modifying these scopes, delete your previously saved tokens/ folder.
     */
    private static final List<String> SCOPES = Arrays.asList(GmailScopes.GMAIL_READONLY);

    private static final String CREDENTIALS_FILE_PATH = "/credentials.json";

    /**
     * Creates an authorized Credential object.
     * @param HTTP_TRANSPORT The network HTTP Transport.
     * @return An authorized Credential object.
     * @throws IOException If the credentials.json file cannot be found.
     */
    private static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException {
        // Load client secrets.
        InputStream in = GmailQuickstart.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
        if (in == null) {
            throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
        }
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build();
        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
    }

    public static void main(String... args) throws IOException, GeneralSecurityException {
        HashMap<String,String> test = extractMessagesEncoded();
        System.out.print(test);
    }

    private static void getMessageListSize(ListMessagesResponse size){
        ListMessagesResponse s;
    }

    /** EXRACTMESSAGESENCODED
     *  This function invokes Google API with readonly scope,
     *  This function will loop through list of message ID's, and will extract header value, which is
     *  where the email address is from. The function will also extract encoded body with email contents.
     *  In the case where there is no data in body, it will instead extract data from the subject of the
     *  email.
     */
    private static HashMap<String,String> extractMessagesEncoded() throws IOException, GeneralSecurityException{
        HashMap<String,String> messageHash = new HashMap<>();
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        Gmail service = new Gmail.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                .setApplicationName(APPLICATION_NAME)
                .build();
        String user = "me";
        ListMessagesResponse messageList = service.users().messages().list(user).execute();

//        TEST
//        System.out.println(messageList);
//        Message messengert = service.users().messages().get(user, "173c9ec575d649c3").setFormat("full").execute();
//        System.out.println(messengert.getPayload().getHeaders().get(messengert.getPayload().getHeaders().indexOf("From")));
//        System.out.println(messengert.getPayload().getHeaders());
//        TEST
        String tempVarFrom = null;
        String tempVarSubject = null;
        String tempVarBody;
        for(Message i : messageList.getMessages()){

            Message messenger = service.users().messages().get(user, i.getId()).setFormat("full").execute();

            for(MessagePartHeader message: messenger.getPayload().getHeaders()){
                if(message.getName().contains("From")) {
                    tempVarFrom = message.getValue();
                }
                if(message.getName().contains("Subject")){
                    tempVarSubject = message.getValue();
                }
            }
            try {
                tempVarBody = StringUtils.newStringUtf8(Base64.decodeBase64(messenger.getPayload().getParts().get(0).getBody().getData()));
            }catch (Exception e){
                tempVarBody = tempVarSubject;
            }
            messageHash.put(tempVarFrom,tempVarBody);
        }

//        System.out.println(StringUtils.newStringUtf8(Base64.decodeBase64(messenger.getPayload().getBody().decodeData())));
//        System.out.print(StringUtils.newStringUtf8(Base64.decodeBase64("VmlldyB5b3VyIGxhdGVzdCBzdGF0ZW1lbnQgb25saW5lDQoNCkRlYXIgTWluaGF6LA0KDQpUaGUgc3RhdGVtZW50IGZvciB5b3VyIFREIEFtZXJpdHJhZGUgYWNjb3VudCBlbmRpbmcgaW4gIDU5MzYgaXMgbm93IGF2YWlsYWJsZSBvbmxpbmUgYW5kIHRocm91Z2ggb3VyIFREIEFtZXJpdHJhZGUgbW9iaWxlIGFwcHMgZm9yIEFuZHJvaWQgYW5kIGlPUy4gDQoNClRvIGFjY2VzcyB5b3VyIHN0YXRlbWVudHMgdmlhIHRoZSB3ZWIsIHBsZWFzZSBsb2cgaW4gdG8geW91ciBhY2NvdW50IGFuZCBnbyB0byBNeSBBY2NvdW50ID4gSGlzdG9yeSAmIA0KU3RhdGVtZW50cy4NCmh0dHBzOi8vaW52ZXN0LmFtZXJpdHJhZGUuY29tL2dyaWQvcC9zaXRlI3I9alBhZ2UvY2dpLWJpbi9hcHBzL3UvSGlzdG9yeQ0KDQpUbyBhY2Nlc3MgeW91ciBzdGF0ZW1lbnRzIHZpYSB0aGUgVEQgQW1lcml0cmFkZSBNb2JpbGUgYXBwLCBwbGVhc2UgbG9nIGluIGFuZCBnbyB0byBBY2NvdW50cyA-IERvY3MuIA0KDQpXZSdyZSBIZXJlIHRvIEhlbHANCg0KSWYgeW91IGhhdmUgYW55IHF1ZXN0aW9ucywgcGxlYXNlIGxldCB1cyBrbm93LiBZb3UgY2FuICBsb2cgaW4gdG8geW91ciBhY2NvdW50IHZpYSB0aGUgd2ViIGFuZCBnbyB0byBDbGllbnQgU2VydmljZXMgPiBNZXNzYWdlIENlbnRlciAgdG8gd3JpdGUgdXMuIElmIHlvdSdkIHByZWZlciwgb3IgaWYgeW91J3JlIG9uIHRoZSBtb2JpbGUgYXBwLCBjYWxsIENsaWVudCBTZXJ2aWNlcyBhdCA4MDAtNjY5LTM5MDAuIFdlJ3JlIGF2YWlsYWJsZSAyNCBob3VycyBhIGRheSwgc2V2ZW4gZGF5cyBhIHdlZWsuDQpodHRwczovL2ludmVzdC5hbWVyaXRyYWRlLmNvbS9ncmlkL3Avc2l0ZSNyPWpQYWdlL2NnaS1iaW4vYXBwcy91L0luYm94SG9tZQ0KDQpTaW5jZXJlbHksDQoNClREIEFtZXJpdHJhZGUgQ2xpZW50IFNlcnZpY2VzDQoNCl9fX19fX19fX19fX19fX19fX19fX19fX19fX19fX19fX19fX19fX19fX19fX19fX19fX19fX19fX19fX19fX19fX18NCg0KVGhpcyBpcyBhbiBhdXRvbWF0ZWQgZW1haWwgYW5kIHdlIHdvbid0IHJlY2VpdmUgeW91ciBtZXNzYWdlIGlmIHlvdSByZXBseS4gVG8gY29udGFjdCB1cywgcGxlYXNlIGxvZyBpbiB0byB5b3VyIGFjY291bnQgYW5kIGdvIHRvIHRoZSBNZXNzYWdlIENlbnRlci4NCg0KVEQgQW1lcml0cmFkZSB1bmRlcnN0YW5kcyB0aGUgaW1wb3J0YW5jZSBvZiBwcm90ZWN0aW5nIHlvdXIgcHJpdmFjeS4gRnJvbSB0aW1lIHRvIHRpbWUgd2UgbmVlZCB0byBzZW5kIHlvdSBub3RpZmljYXRpb25zIGxpa2UgdGhpcyBvbmUgdG8gZ2l2ZSB5b3UgaW1wb3J0YW50IGluZm9ybWF0aW9uIGFib3V0IHlvdXIgYWNjb3VudC4gSWYgeW91J3ZlIG9wdGVkIG91dCBvZiByZWNlaXZpbmcgcHJvbW90aW9uYWwgbWFya2V0aW5nIGNvbW11bmljYXRpb25zIGZyb20gdXMsIGNvbnRhaW5pbmcgbmV3cyBhYm91dCBuZXcgYW5kIHZhbHVhYmxlIFREIEFtZXJpdHJhZGUgc2VydmljZXMsIHdlIHdpbGwgY29udGludWUgdG8gaG9ub3IgeW91ciByZXF1ZXN0Lg0KDQpNYXJrZXQgdm9sYXRpbGl0eSwgdm9sdW1lLCBhbmQgc3lzdGVtIGF2YWlsYWJpbGl0eSBtYXkgZGVsYXkgYWNjb3VudCBhY2Nlc3MgYW5kIHRyYWRlIGV4ZWN1dGlvbi4NCg0KVEQgQW1lcml0cmFkZSwgSW5jLiwgbWVtYmVyIEZJTlJBL1NJUEMuIChodHRwOi8vd3d3LmZpbnJhLm9yZyAvIGh0dHA6Ly93d3cuc2lwYy5vcmcpIFREIEFtZXJpdHJhZGUgaXMgYSB0cmFkZW1hcmsgam9pbnRseSBvd25lZCBieSBURCBBbWVyaXRyYWRlIElQIENvbXBhbnksIEluYy4gYW5kIFRoZSBUb3JvbnRvLURvbWluaW9uIEJhbmsuIENvcHlyaWdodCAyMDIwIFREIEFtZXJpdHJhZGUuIA0KDQpEaXN0cmlidXRlZCBieSBURCBBbWVyaXRyYWRlLCBJbmMuLCAyMDAgU291dGggMTA4dGggQXZlbnVlLCBPbWFoYSwgTkUgNjgxNTQtMjYzMQ0KDQpUREEgMTAwMDU5MSBFTSAxMC8xOA0K")));
    return messageHash;
    }

}