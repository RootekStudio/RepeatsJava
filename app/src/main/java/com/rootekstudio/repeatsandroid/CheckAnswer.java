package com.rootekstudio.repeatsandroid;

import java.text.Normalizer;
import java.util.Locale;
import java.util.Scanner;

class CheckAnswer {

    static boolean isAnswerCorrect(String user, String correct, String ignore) {
        if (ignore.equals("true")) {
            user = Normalizer.normalize(user, Normalizer.Form.NFD)
                    .replaceAll(" ", "")
                    .replaceAll("Ł", "l")
                    .replaceAll("ł", "l")
                    .replaceAll("[^\\p{ASCII}]", "")
                    .toLowerCase(Locale.getDefault());

            correct = Normalizer.normalize(correct, Normalizer.Form.NFD)
                    .replaceAll(" ", "")
                    .replaceAll("Ł", "l")
                    .replaceAll("ł", "l")
                    .replaceAll("[^\\p{ASCII}]", "")
                    .toLowerCase(Locale.getDefault());

        }

        if(!correct.contains(RepeatsHelper.breakLine)) {
            if (user.equals(correct)) {
                return true;
            } else {
                return false;
            }
        }
        else {
            Scanner scanner = new Scanner(correct);
            boolean foundedCorrect = false;
            while(scanner.hasNextLine()) {
                String singleCorrect = scanner.nextLine();
                if (singleCorrect.equals(user)) {
                    foundedCorrect = true;
                    return true;
                }
            }

            if(!foundedCorrect) {
                return false;
            }
        }

        return false;
    }
}
