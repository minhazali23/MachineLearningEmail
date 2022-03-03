package EmailService.Gmail;

import Model.User;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.repackaged.org.apache.commons.codec.binary.Base64;
import com.google.api.client.repackaged.org.apache.commons.codec.binary.StringUtils;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.ListMessagesResponse;
import com.google.api.services.gmail.model.Message;
import com.google.api.services.gmail.model.MessagePartHeader;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GmailMessageExtractor{

    HashMap<String,String> messageHash = new HashMap<>();


    /** EXRACTMESSAGESENCODED
     *  This function invokes Google API with readonly scope,
     *  This function will loop through list of message ID's, and will extract header value, which is
     *  where the email address is from. The function will also extract encoded body with email contents.
     *  In the case where there is no data in body, it will instead extract data from the subject of the
     *  email.
     */
    public HashMap<String,String> extractMessagesPerPage(Gmail service, User user) throws IOException, GeneralSecurityException {

        String userName = user.getUsername();

        ListMessagesResponse response = service.users().messages().list(userName).execute();

        List<Message> fullMessageList = new ArrayList<Message>();

        while(response.getMessages() != null){
            System.out.println(response.getMessages());
            fullMessageList.addAll(response.getMessages());
            if(response.getNextPageToken() != null){
                String nextPageToken = response.getNextPageToken();
                response = service.users().messages().list(userName).setPageToken(nextPageToken).execute();
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

            Message messenger = service.users().messages().get(userName, i.getId()).setFormat("full").execute();

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
            this.messageHash.put(tempVarFrom,tempVarBody);
        }

        return this.messageHash;
    }

}
