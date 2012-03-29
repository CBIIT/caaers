package gov.nih.nci.cabig.caaers.tools;

import org.springframework.context.MessageSource;

import java.util.Locale;

/**
 * @author Ion C. Olaru
 *         Date: 3/28/12 -11:37 AM
 */
public class Messages {

    MessageSource messageSource;
    static MessageSource _messageSource;

    public static String get(String code) {
        return _messageSource.getMessage(code, new Object[]{}, Locale.getDefault());
    }

    public MessageSource getMessageSource() {
        return messageSource;
    }

    public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
        _messageSource = messageSource;
    }
}