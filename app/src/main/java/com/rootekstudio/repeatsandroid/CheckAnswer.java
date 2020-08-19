package com.rootekstudio.repeatsandroid;

import java.text.Normalizer;
import java.util.Locale;
import java.util.Scanner;

public class CheckAnswer {

    public static boolean isAnswerCorrect(String user, String correct, boolean ignore) {

        user = user.trim();
        user = user.toLowerCase();
        correct = correct.toLowerCase();

        if (ignore) {
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

        if (!correct.contains(RepeatsHelper.breakLine)) {
            if (user.equals(correct)) {
                return true;
            } else {
                return false;
            }
        } else {
            Scanner scanner = new Scanner(correct);
            while (scanner.hasNextLine()) {
                String singleCorrect = scanner.nextLine();
                if (singleCorrect.equals(user)) {
                    return true;
                }
            }
        }

        return false;
    }
}
