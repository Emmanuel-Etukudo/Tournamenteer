package ykim164cs242.tournamentor.Utils;

/**
 * The LevenshteinDistance class is a utility for checking the similarity between
 * two names. It is used for checking possible duplicates when adding a new
 * scorer in the Score Update dialog.
 */
public class LevenshteinDistance {

    /**
     * similarity function Calculates the similarity ratio between two given strings.
     * Two strings will be names of scorers in my app
     */
    public static double similarity(String s1, String s2) {
        String longer = s1, shorter = s2;

        // longer should always have greater length
        if (s1.length() < s2.length()) {
            longer = s2; shorter = s1;
        }

        // weight if either last name or first name is equal
        double weight = 0;

        // If the input is only a single token
        if(longer.split(" ").length == 1 || shorter.split(" ").length == 1) {
            // Continue
        } else {
            if(longer.split(" ")[0].equalsIgnoreCase(shorter.split(" ")[0]) ||
                    longer.split(" ")[1].equalsIgnoreCase(shorter.split(" ")[1]) ||
                    longer.split(" ")[0].equalsIgnoreCase(shorter.split(" ")[1]) ||
                    longer.split(" ")[1].equalsIgnoreCase(shorter.split(" ")[0])) {

                // add 30% weight
                weight += 0.3;
            }
        }

        int longerLength = longer.length();

        return ((longerLength - editDistance(longer, shorter)) / (double) longerLength) + weight;

    }

    /**
     * Calculates the editDistance between two strings.
     * Returns the number of steps required for String s1
     * to become String s2.
     */
    public static int editDistance(String s1, String s2) {

        // Does not turn into lower cases because the app

        int[] costs = new int[s2.length() + 1];
        for (int i = 0; i <= s1.length(); i++) {

            int lastVal = i;
            for (int j = 0; j <= s2.length(); j++) {
                if (i == 0)
                    costs[j] = j;
                else {
                    if (j > 0) {
                        int newVal = costs[j - 1];
                        if (s1.charAt(i - 1) != s2.charAt(j - 1))
                            newVal = Math.min(Math.min(newVal, lastVal), costs[j]) + 1;
                        costs[j - 1] = lastVal;
                        lastVal = newVal;
                    }
                }
            }
            if (i > 0)
                costs[s2.length()] = lastVal;
        }
        return costs[s2.length()];
    }
}