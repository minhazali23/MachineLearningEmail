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
        HashMap<String,String> test = extractMessagesPerPage();
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
    private static HashMap<String,String> extractMessagesPerPage() throws IOException, GeneralSecurityException{
        HashMap<String,String> messageHash = new HashMap<>();
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        Gmail service = new Gmail.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                .setApplicationName(APPLICATION_NAME)
                .build();
        String user = "me";
        ListMessagesResponse response = service.users().messages().list(user).execute();
        List<Message> fullMessageList = new ArrayList<Message>();

        while(response.getMessages() != null){
            fullMessageList.addAll(response.getMessages());
            if(response.getNextPageToken() != null){
                String nextPageToken = response.getNextPageToken();
                response = service.users().messages().list(user).setPageToken(nextPageToken).execute();
            }else{
                break;
            }
        }
        System.out.print(fullMessageList);
//        TEST
        int s = 0;
//        TEST
        String tempVarFrom = null;
        String tempVarSubject = null;
        String tempVarBody;
        for(Message i : fullMessageList){

            Message messenger = service.users().messages().get(user, i.getId()).setFormat("full").execute();

            for(MessagePartHeader message: messenger.getPayload().getHeaders()){
                if(message.getName().contains("From")) {
                    tempVarFrom = message.getValue(); System.out.println(s++);
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

   return messageHash;
    }

}