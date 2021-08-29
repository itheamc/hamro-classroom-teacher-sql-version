package com.itheamc.hamroclassroom_teachers.utils;


import android.util.Base64;

import java.nio.charset.StandardCharsets;
import java.util.Locale;

public class Amcryption {

    /*
    Getters for the encoder and decoder
     */
    public static Decoder getDecoder() {
        return new Decoder();
    }

    public static Encoder getEncoder() {
        return new Encoder();
    }

    /*____________________________________________Encoder and Decoder Classes___________________________*/

    /*
        Decoder Class
         */
    public static class Decoder {

        // Constructor
        public Decoder() {
            lowerChars = getLowerChars();
            upperChars = getUpperChars();
            numChars = getNumChars();
        }

        // Function to decode the user number
        public String decode(String string) {

            // At first decode with Base64 decoder
            byte[] decodedBytes = Base64.decode(string, Base64.NO_WRAP);
            String s = new String(decodedBytes);

            // And again decode with custom decoder
            StringBuilder decodeNumber = new StringBuilder();
            for (int i = s.length() - 1; i >= 0; i--) {
                if ((i % 3) == 0) {
                    decodeNumber.append(decodeString(String.valueOf(s.charAt(i)) +
                            s.charAt(i + 1) + s.charAt(i + 2)));
                }
            }

            return decodeNumber.toString();
        }

        // Custom Decoding Method
        private String decodeString(String s) {
            String str = decodeA2Z(s);

            if (str.length() <= 0) {
                str = decodea2z(s);

                if (str.length() <= 0) {
                    str = decodeNums(s);

                    if (str.length() <= 0) {
                        str = decodeOthersChar(s);
                    }
                }
            }

            return str;

        }

        /*___________________________________Decoding Function____________________*/

        /*
        Some Important arrays
         */
        private char[] lowerChars = new char[26];
        private char[] upperChars = new char[26];
        private char[] numChars = new char[10];

        /*
         Function to encode [A - Z]
         */
        private String decodeA2Z(String s) {
            String str = "";
            String[] seq = getCharSequence(lowerChars, upperChars);
            for (int i = 0; i < seq.length; i++) {
                if (seq[i].toLowerCase(Locale.ENGLISH).equals(s.toLowerCase(Locale.ENGLISH))) {
                    str = String.valueOf(upperChars[upperChars.length - 1 - i]);
                    break;
                }
            }

            return str;
        }

        /*
        Function to encode [a - z]
        */
        private String decodea2z(String s) {
            String str = "";
            String[] seq = getCharSequence1(lowerChars, upperChars);
            for (int i = 0; i < seq.length; i++) {
                if (seq[i].toLowerCase(Locale.ENGLISH).equals(s.toLowerCase(Locale.ENGLISH))) {
                    str = String.valueOf(lowerChars[lowerChars.length - 1 - i]);
                    break;
                }
            }

            return str;
        }

        /*
        Function to encode [Numbers 0 - 9]
         */
        private String decodeNums(String s) {
            String str = "";
            String[] seq = getCharSequence2(lowerChars, upperChars);
            for (int i = 0; i < seq.length; i++) {
                if (seq[i].toLowerCase(Locale.ENGLISH).equals(s.toLowerCase(Locale.ENGLISH))) {
                    str = String.valueOf(numChars[numChars.length - 1 - i]);
                    break;
                }
            }

            return str;
        }

        /*
        If other than known char are sent for encoding
         */
        private String decodeOthersChar(String s) {
            return String.valueOf(s.charAt(1));
        }

    }
    /*_______________________End of Decoding Class------------*/

    /*
    Encoder Class
     */
    public static class Encoder {

        // Constructor
        public Encoder() {
            lowerChars = getLowerChars();
            upperChars = getUpperChars();
            numChars = getNumChars();
        }

        // Function to encode the user number in the encrypted form
        public String encode(String string) {

            // Encoding number with custom encoder
            StringBuilder encodedString = new StringBuilder();
            for (int i = string.length() - 1; i >= 0; i--) {
                encodedString.append(encodeChar(string.charAt(i)));
            }

            // Encoding encoded number with Base64 again and returning it
            return Base64.encodeToString(encodedString.toString().getBytes(StandardCharsets.UTF_8), Base64.NO_WRAP);
        }


        // Custom Encoder method
        private String encodeChar(char c) {
            String s = encodeA2Z(c);

            if (s.length() <= 0) {
                s = encodea2z(c);

                if (s.length() <= 0) {
                    s = encodeNums(c);

                    if (s.length() <= 0) {
                        s = encodeOthersChar(c);
                    }
                }
            }


            return s;

        }

        /*___________________________________Encoding Function____________________*/

        /*
        Some Important arrays
         */
        private char[] lowerChars = new char[26];
        private char[] upperChars = new char[26];
        private char[] numChars = new char[10];

        /*
    Function to encode [A - Z]
    */
        private String encodeA2Z(char c) {
            String s = "";
            String[] seq = getCharSequence(lowerChars, upperChars);
            for (int i = 0; i < upperChars.length; i++) {
                if (upperChars[i] == c) {
                    s = seq[seq.length - 1 - i];
                    break;
                }
            }

            return s;
        }


        /*
         Function to encode [a - z]
         */
        private String encodea2z(char c) {
            String s = "";
            String[] seq = getCharSequence1(lowerChars, upperChars);
            for (int i = 0; i < lowerChars.length; i++) {
                if (lowerChars[i] == c) {
                    s = seq[seq.length - 1 - i];
                    break;
                }
            }

            return s;
        }


        /*
        Function to encode [Numbers 0 - 9]
         */
        private String encodeNums(char c) {
            String s = "";
            String[] seq = getCharSequence2(lowerChars, upperChars);
            for (int i = 0; i < numChars.length; i++) {
                if (numChars[i] == c) {
                    s = seq[seq.length - 1 - i];
                    break;
                }
            }

            return s;
        }

        /*
        If other than known char are sent for encoding
         */
        private String encodeOthersChar(char c) {
            return String.valueOf(upperChars[7])
                    + c
                    + lowerChars[25];
        }

    }


    /*
     Getters for the lowerChars,
     upperChars
     and numChars
     */

    /* a-z */
    public static char[] getLowerChars() {
        char[] tempLowerChars = new char[26];
        int n = 0;
        for (char c = 'a'; c <= 'z'; c++) {
            tempLowerChars[n] = c;
            n++;
        }

        return tempLowerChars;
    }

    /* A-Z */
    public static char[] getUpperChars() {
        char[] tempupperChars = new char[26];
        int n = 0;
        for (char c = 'A'; c <= 'Z'; c++) {
            tempupperChars[n] = c;
            n++;
        }

        return tempupperChars;
    }

    /* 0-9 */
    public static char[] getNumChars() {
        char[] tempNumChars = new char[10];

        for (int i = 0; i < 10; i++) {
            tempNumChars[i] = String.valueOf(i).charAt(0);
        }

        return tempNumChars;
    }


    /* CharSequences */
    public static String[] getCharSequence(char[] lowerChars, char[] upperChars) {
        String[] charSequence = new String[26];
        int m = 6;
        int n = 17;
        for (int j = 0; j < charSequence.length; j++) {
            if (n < 26) {
                if ((j % 3) == 0) {
                    charSequence[j] = String.valueOf(upperChars[n]) +
                            lowerChars[charSequence.length - 1 - j] +
                            upperChars[j];

                } else if ((j % 5) == 0) {
                    charSequence[j] = String.valueOf(lowerChars[charSequence.length - 1 - j]) +
                            upperChars[n] +
                            lowerChars[j];
                } else {
                    charSequence[j] = String.valueOf(lowerChars[charSequence.length - 1 - j]) +
                            upperChars[j] +
                            upperChars[n];
                }
                n++;
            } else {
                if ((j % 2) == 0) {
                    charSequence[j] = String.valueOf(upperChars[j]) +
                            lowerChars[charSequence.length - 1 - j] +
                            upperChars[m];

                } else if ((j % 7) == 0) {
                    charSequence[j] = String.valueOf(lowerChars[m]) +
                            upperChars[j] +
                            lowerChars[charSequence.length - 1 - j];
                } else {
                    charSequence[j] = String.valueOf(lowerChars[j]) +
                            upperChars[m] +
                            upperChars[charSequence.length - 1 - j];
                }
                m++;
            }

        }

        return charSequence;
    }

    /* CharSequences1 */

    public static String[] getCharSequence1(char[] lowerChars, char[] upperChars) {
        String[] charSequence1 = new String[26];
        int m = 0;
        int n = 11;
        for (int j = 0; j < charSequence1.length; j++) {
            if (n < 26) {
                if ((j % 2) == 0) {
                    charSequence1[j] = String.valueOf(lowerChars[n]) +
                            upperChars[charSequence1.length - 1 - j] +
                            lowerChars[j];

                } else if ((j % 5) == 0) {
                    charSequence1[j] = String.valueOf(upperChars[charSequence1.length - 1 - j]) +
                            lowerChars[j] + upperChars[n];
                } else {
                    charSequence1[j] = String.valueOf(lowerChars[j]) +
                            upperChars[n] +
                            upperChars[charSequence1.length - 1 - j];
                }
                n++;
            } else {
                if ((j % 2) == 0) {
                    charSequence1[j] = String.valueOf(lowerChars[m]) +
                            upperChars[charSequence1.length - 1 - j] +
                            lowerChars[j];

                } else if ((j % 5) == 0) {
                    charSequence1[j] = String.valueOf(upperChars[charSequence1.length - 1 - j]) +
                            lowerChars[j] + upperChars[m];
                } else {
                    charSequence1[j] = String.valueOf(lowerChars[j]) +
                            upperChars[m] +
                            upperChars[charSequence1.length - 1 - j];
                }
                m++;
            }

        }

        return charSequence1;
    }

    /* CharSequence2 */
    public static String[] getCharSequence2(char[] lowerChars, char[] upperChars) {
        String[] charSequence2 = new String[10];
        int m = 3;
        int n = 15;
        int o = 8;
        for (int j = 0; j < charSequence2.length; j++) {

            if ((j % 2) == 0) {
                charSequence2[j] = String.valueOf(lowerChars[n]) +
                        upperChars[m] +
                        lowerChars[o];

            } else if ((j % 5) == 0) {
                charSequence2[j] = String.valueOf(upperChars[o]) +
                        lowerChars[n] + upperChars[m];
            } else {
                charSequence2[j] = String.valueOf(lowerChars[m]) +
                        upperChars[n] +
                        upperChars[o];
            }
            n++;
            m++;
            o++;
        }


        return charSequence2;
    }
}
