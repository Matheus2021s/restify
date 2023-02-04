package br.com.mariah.restify.utils;

public class StringUtils {

    private final static String REGEX_SPECIAL_CHARACTERS = "^[a-zA-Z0-9]";
    private final static String REGEX_UPPER_CASE_CHARACTERS_OR_NUMBERS = "[A-Z0-9]";

    private enum CaseType {
        UPPPER, LOWER
    }

    public static String getCamelCaseFirstLetterUpper(String s) {
        return getCamelCase(s, CaseType.UPPPER);
    }

    public static String getCamelCaseFirstLetterLower(String s) {
        return getCamelCase(s, CaseType.LOWER);
    }

    private static String getCamelCase(String s, CaseType caseTypeFirstLetter) {

        StringBuilder word = new StringBuilder();

        for (String value : separateWords(s)) {

            word.append(getWordWithFirstLetterUpperAndRestLower(value));
        }

        if (caseTypeFirstLetter.equals(CaseType.UPPPER)) {
            return getWordWithFirstLetterUpper(word.toString());
        } else {
            return getWordWithFirstLetterLower(word.toString());
        }
    }

    public static String getWordWithFirstLetterUpperAndRestLower(String value) {
        return getFirstLetterUpperCase(value) + getTextWithoutFirstLetterLowerCase(value);
    }

    public static String getWordWithFirstLetterUpper(String word) {
        return getFirstLetterUpperCase(word) + getTextWithoutFirtsLettter(word);
    }


    public static String getWordWithFirstLetterLower(String word) {
        return getFirstLetterLowerCase(word) + getTextWithoutFirtsLettter(word);
    }

    private static String getFirstLetterLowerCase(String word) {
        return word.substring(0, 1).toLowerCase();
    }

    private static String getTextWithoutFirtsLettter(String word) {
        return word.substring(1);
    }

    private static String getTextWithoutFirstLetterLowerCase(String value) {
        return getTextWithoutFirtsLettter(value).toLowerCase();
    }

    private static String getFirstLetterUpperCase(String value) {
        return value.substring(0, 1).toUpperCase();
    }

    public static String getUpperSeparatedByDash(String s) {
        return splitWordAccordingToSeparatorAndCase(s, CaseType.UPPPER, "-");

    }

    public static String getLowerSeparatedByDash(String s) {
        return splitWordAccordingToSeparatorAndCase(s, CaseType.LOWER, "-");

    }


    public static String getUpperSeparatedByUnderscore(String s) {
        return splitWordAccordingToSeparatorAndCase(s, CaseType.UPPPER, "_");

    }

    public static String getLowerSeparatedByUnderscore(String s) {
        return splitWordAccordingToSeparatorAndCase(s, CaseType.LOWER, "_");
    }


    public static String getLowerNoSpaces(String s) {
        return splitWordAccordingToSeparatorAndCase(s, CaseType.LOWER, "");
    }

    private static String splitWordAccordingToSeparatorAndCase(String text, CaseType caseTypeWord, String separator) {
        StringBuilder words = new StringBuilder();
        for (String word : separateWords(text)) {
            if (caseTypeWord.equals(CaseType.UPPPER)) {
                words.append(word.toUpperCase()).append(separator);
            } else {
                words.append(word.toLowerCase()).append(separator);
            }
        }
        if (words.toString().endsWith(separator)) {
            words = new StringBuilder(words.substring(0, words.length() - 1));
        }
        return words.toString();
    }

    private static String[] separateWords(String text) {

        return getSeparatedByComma(text).split(",");

    }

    private static String getSeparatedByComma(String word) {
        String wordWithSpaces = replaceCamelCaseWhitSpaces(word);

        String wordWithComma = replaceSpecialCharactersByComma(wordWithSpaces);

        return removeEmptyStrings(wordWithComma);
    }

    private static String replaceCamelCaseWhitSpaces(String text) {
        StringBuilder wordWithSpaces = new StringBuilder();
        String[] split = text.split("");

        for (int i = 0, splitLength = split.length; i < splitLength; i++) {
            String s = split[i];
            if (i != 0 && isLetterUpperCase(s)
                    && isBackLatterLowerCase(split, i)) {
                wordWithSpaces.append(" ").append(s);
            } else if (i != 0 && isLetterLowercase(s)
                    && isBackLatterUpperCase(split, i)
                    && (i != 1 && isBackLatterUpperCase(split, i - 1))) {
                wordWithSpaces.append(" ").append(s);
            } else {
                wordWithSpaces.append(s);
            }
        }
        return wordWithSpaces.toString();
    }

    private static boolean isLetterLowercase(String s) {
        return !isLetterUpperCase(s);
    }


    private static boolean isBackLatterUpperCase(String[] split, int i) {
        return isLetterUpperCase(split[i - 1]);
    }


    private static boolean isBackLatterLowerCase(String[] split, int i) {
        return isLetterLowercase(split[i - 1]);
    }

    private static boolean isLetterUpperCase(String s) {
        return s.matches(REGEX_UPPER_CASE_CHARACTERS_OR_NUMBERS);
    }

    private static String removeEmptyStrings(String words) {
        StringBuilder word = new StringBuilder();
        for (String s : words.split(",")) {
            word.append(s.isBlank() ? "" : s + ",");
        }
        if (word.toString().endsWith(",")) {
            word = new StringBuilder(word.substring(0, word.length() - 1));
        }
        return word.toString();
    }

    private static String replaceSpecialCharactersByComma(String text) {
        StringBuilder words = new StringBuilder();
        String[] strings = text.split("");

        for (String charr : strings) {
            if (!charr.matches(REGEX_SPECIAL_CHARACTERS)) {
                words.append(",");
            } else {
                words.append(charr);
            }
        }
        return words.toString();
    }

}