package tn.esprit.utils;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;

public class SmsService {
    private final String ACCOUNT_SID;
    private final String AUTH_TOKEN;
    private final String MESSAGING_SERVICE_SID;

    public SmsService(String accountSid, String authToken, String messagingServiceSid) {
        this.ACCOUNT_SID = accountSid;
        this.AUTH_TOKEN = authToken;
        this.MESSAGING_SERVICE_SID = messagingServiceSid;
        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
    }

    public boolean sendSms(String to, String body) {
        try {
            Message message = Message.creator(
                    new com.twilio.type.PhoneNumber(to),
                    MESSAGING_SERVICE_SID,
                    body
            ).create();
            return message.getSid() != null;
        } catch (Exception e) {
            System.err.println("Erreur lors de l'envoi du SMS: " + e.getMessage());
            return false;
        }
    }
} 