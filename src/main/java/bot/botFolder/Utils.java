package bot.botFolder;

import org.apache.commons.validator.EmailValidator;

public class Utils {
    public static boolean isValidEmailAddress(String email){
        return EmailValidator.getInstance().isValid(email);
    }
}
