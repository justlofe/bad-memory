import java.security.MessageDigest;
import java.util.Random;

public final class BadMemory {

    private static final int PASSWORD_LENGTH = 16;
    private static final String CHARACTERS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789!/?*@#&";

    private static long hash(String first, String second) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest((first + second).getBytes());

            long seed = 0;
            for (int i = 0; i < 8; i++) {
                seed = (seed << 8) | (hash[i] & 0xFF);
            }
            return seed;
        }
        catch (Exception ex) {
            return (first + second).hashCode();
        }
    }

    private static Random createRandom(String masterPassword, String service) {
        return new Random(hash(masterPassword, service));
    }

    private static String generatePassword(String masterPassword, String service) {
        Random random = createRandom(masterPassword, service);
        StringBuilder builder = new StringBuilder(PASSWORD_LENGTH);
        for (int i = 0; i < PASSWORD_LENGTH; i++) {
            builder.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
        }
        return builder.toString();
    }

    private static boolean submitToClipboard(String text) {
        try {
            java.awt.Toolkit.getDefaultToolkit()
                    .getSystemClipboard()
                    .setContents(new java.awt.datatransfer.StringSelection(text), null);

            return true;
        }
        catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void main(String[] args) {
        if(args.length != 2 && args.length != 3) {
            System.out.println("fatal error: too many/few arguments. usage: <master_password> <service>");
            return;
        }

        if(args[0].length() < 12 && (args.length < 3 || !args[2].equalsIgnoreCase("--unsafe"))) {
            System.out.println("fatal error: master_password is too small. at least 12 characters should be in.");
            System.out.println("suggestion: add --unsafe flag to the of the command to ignore this check.");
            return;
        }

        String generatedPassword = generatePassword(args[0], args[1]);

        System.out.printf("password for %s: ", args[1]);
        System.out.printf(generatedPassword);

        if(submitToClipboard(generatedPassword)) System.out.print(" (copied to clipboard)\n");
        else System.out.println();
    }

}
