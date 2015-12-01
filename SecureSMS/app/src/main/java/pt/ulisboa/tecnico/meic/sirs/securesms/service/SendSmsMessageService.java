package pt.ulisboa.tecnico.meic.sirs.securesms.service;

import android.telephony.SmsManager;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import pt.ulisboa.tecnico.meic.sirs.securesms.dataAccess.ContactManager;
import pt.ulisboa.tecnico.meic.sirs.securesms.domain.Contact;
import pt.ulisboa.tecnico.meic.sirs.securesms.domain.Cryptography;
import pt.ulisboa.tecnico.meic.sirs.securesms.domain.SmsMessage;
import pt.ulisboa.tecnico.meic.sirs.securesms.dataAccess.SmsMessageManager;
import pt.ulisboa.tecnico.meic.sirs.securesms.domain.exceptions.FailedToEncryptSmsMessageException;
import pt.ulisboa.tecnico.meic.sirs.securesms.domain.exceptions.FailedToCreateSmsMessageException;
import pt.ulisboa.tecnico.meic.sirs.securesms.domain.exceptions.FailedToRetrieveContactException;
import pt.ulisboa.tecnico.meic.sirs.securesms.service.exceptions.FailedServiceException;

/**
 * Created by lribeirogomes on 15/11/15.
 */

public class SendSmsMessageService extends SecureSmsService {
    private final short SMS_PORT= 8998;
    private ArrayList<String> _phoneNumbers;
    private String _plainTextSms;

    public SendSmsMessageService(ArrayList<String> phoneNumbers, String plainTextSms) {
        _phoneNumbers = phoneNumbers;
        _plainTextSms = plainTextSms;
    }

    public void execute() throws FailedServiceException {
        try {
            for (String phoneNumber : _phoneNumbers) {
                Contact contact = ContactManager.retrieveContactByPhoneNumber(phoneNumber);
                SmsMessage sms = SmsMessageManager.createSmsMessage(contact, _plainTextSms);

                SmsManager manager = SmsManager.getDefault();
                manager.sendDataMessage(phoneNumber,
                        null, // TODO: define scAddress if needed
                        SMS_PORT,
                        sms.getEncryptedContent(),
                        null,  // TODO: define sentIntent if needed
                        null); // TODO: define deliveryIntent if needed
            }
        } catch ( IllegalArgumentException
                | FailedToRetrieveContactException
                | FailedToCreateSmsMessageException
                | FailedToEncryptSmsMessageException exception) {
            throw new FailedServiceException("send sms message", exception);
        }
    }
}