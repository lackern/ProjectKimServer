/*
 * TCPClient.java
 *
 * Created on Oct 25, 2011, 5:03:34 PM
 *
 * @author 
 * CS2105 Group 81 
 * a0073937	TEO EE SIANG, LIONEL
 * u097016H	XU CHUNFENG
 * 
 */
package cs2105project;

import java.util.*;
import java.io.*;
import java.net.*;

public class TCPClient {

    Socket s;
    OutputStream os;
    DataOutputStream serverWriter;
    InputStreamReader isrServer;
    BufferedReader serverReader;
    BufferedReader inFromUser;
    String response;
    Socket pop;
    OutputStream popos;
    DataOutputStream popserverWriter;
    InputStreamReader popisrServer;
    BufferedReader popserverReader;
    BufferedReader popinFromUser;

    public static void main(String args[]) throws Exception {
    }

    // SMTP connection based on user's selected SMTP server, defalut is "smtp.nus.edu.sg 25"
    public String connect(String inputUserName, String inputPassword, String inputSMTP) {
        if (inputUserName.isEmpty() != true && inputPassword.isEmpty() != true && inputUserName.length() != 0 && inputPassword.length() != 0) {
            String smtpReturns = "";
            //Encode to Base64
            String username = encode(inputUserName);
            String password = encode(inputPassword);

            try {
                String smtp = inputSMTP.trim();

                // Start connection
                if (smtp.length() != 0) {
                    s = new Socket(smtp, 25);
                } else {
                    s = new Socket("smtp.nus.edu.sg", 25);
                }

                os = s.getOutputStream();
                serverWriter = new DataOutputStream(os);
                isrServer = new InputStreamReader(s.getInputStream());
                serverReader = new BufferedReader(isrServer);


                String response = serverReader.readLine();
                smtpReturns += response + ";";
                System.out.println(response);
                while (serverReader.ready() == true) {
                    response = serverReader.readLine();
                    smtpReturns += response + ";";
                    System.out.println(response);
                }
                inFromUser = new BufferedReader(new InputStreamReader(System.in));

                // EHLO the server
                serverWriter.writeBytes("EHLO" + "\r\n");
                serverWriter.flush();

                response = serverReader.readLine();
                smtpReturns += response + ";";
                System.out.println(response);
                while (serverReader.ready() == true) {
                    response = serverReader.readLine();
                    smtpReturns += response + ";";
                    System.out.println(response);
                }

                // Send userID
                serverWriter.writeBytes("auth login " + username + "\r\n");
                serverWriter.flush();
                response = serverReader.readLine();
                smtpReturns += response + ";";
                System.out.println(response);

                // Send user authentication password
                serverWriter.writeBytes(password);
                response = serverReader.readLine();
                smtpReturns += response + ";";
                System.out.println(response);

                if (response.compareTo("235 2.7.0 Authentication successful") == 0) {
                    return "connected" + ";" + smtpReturns;
                } else {
                    return "notconnected" + ";" + smtpReturns;
                }

            } catch (Exception error) {
                System.out.println("An error has occured at " + error.getStackTrace());
            }
        } else {
            return "notconnected";
        }
        return "notconnected";
    }

    // Sends the composed mail
    public String[] sendMail(String from, String to, String cc, String data, String subject) {
        try {
            // Mail FROM:
            String smtpReturns = "\n\n";
            serverWriter.writeBytes("MAIL FROM:" + from + "\r\n");
            serverWriter.flush();
            response = serverReader.readLine();
            smtpReturns += response + ";";
            System.out.println(response);

            // RCPT TO;
            serverWriter.writeBytes("RCPT TO:" + to + "\r\n");
            serverWriter.flush();
            response = serverReader.readLine();
            smtpReturns += response + ";";
            System.out.println(response);

            // Mail DATA
            serverWriter.writeBytes("DATA" + "\r\n");
            serverWriter.flush();
            response = serverReader.readLine();
            smtpReturns += response + ";";
            System.out.println(response);

            String mailContent = "From:" + from + "\r\n";
            mailContent += "To:" + to + "\r\n";
            mailContent += "Cc:" + cc + "\r\n";
            mailContent += "Subject:" + subject + "\r\n";
            mailContent += "Reply-To:" + from + "\r\n";
            mailContent += data + "\r\n.\r\n";

            serverWriter.writeBytes(mailContent);
            serverWriter.flush();
            response = serverReader.readLine();
            smtpReturns += response + ";";
            System.out.println(response);

            String[] returnArray = new String[2];
            returnArray[0] = "sent" + ";" + smtpReturns;
            returnArray[1] = mailContent;

            return returnArray;

        } catch (Exception e) {
            System.out.println(e.getStackTrace());
        }
        return null;
    }

    // Load sent mail database based on useID
    public ArrayList<String> getMail(String inputUserID) {
        try {
            /** Declaration of Variables **/
            ArrayList<String> newAL = new ArrayList();

            int lastSlash = inputUserID.lastIndexOf("\\");
            String userID = "";
            if (lastSlash != -1) {
                userID = inputUserID.substring(lastSlash).toUpperCase();
            } else {
                userID = inputUserID;
            }

            BufferedReader in = new BufferedReader(new FileReader("SavedMail/" + userID + ".txt"));
            String read = in.readLine();
            String content = "";
            while (read != null) {

                if (read.indexOf(";") == 0) {
                    newAL.add(content);
                    content = "";
                } else {
                    content += read + "\n";
                }
                read = in.readLine();
            }

            return newAL;
        } catch (Exception e) {
            return null;
        }
    }

    // Save sent mail to user database
    public void saveMail(String inputUserID, ArrayList<String> sentMailContent) {
        try {
            String strDirectory = "SavedMail";
            boolean success = (new File(strDirectory)).mkdir();

            if (success == true) {
                System.out.println("Directory: " + strDirectory + " created");
            }

            int lastSlash = inputUserID.lastIndexOf("\\");
            String userID = "";
            if (lastSlash != -1) {
                userID = inputUserID.substring(lastSlash).toUpperCase();
            } else {
                userID = inputUserID;
            }
            String fileName = strDirectory + "/" + userID + ".txt";
            BufferedWriter out = new BufferedWriter(new FileWriter(fileName));

            for (int i = 0; i < sentMailContent.size(); i++) {
                String content = sentMailContent.get(i);
                out.write(content + "\n;\n");
                out.flush();
            }
        } catch (Exception error) {
            System.out.println(error.getStackTrace());
        }

    }

    // Disconnect from smtp server
    public String disconnect() {
        try {
            String smtpReturns = "\n\n";
            // Send QUIT command
            serverWriter.writeBytes("QUIT" + "\r\n");
            serverWriter.flush();
            String response = serverReader.readLine();
            smtpReturns += response + ";";
            System.out.println(response);
            if (response.compareTo("221 2.0.0 smtp.nus.edu.sg Service closing transmission channel") == 0) {
                return "closed" + ";" + smtpReturns;
            } else {
                return "notclosed" + ";" + smtpReturns;
            }
        } catch (Exception e) {
            System.out.println(e.getStackTrace());
        }
        return "notclosed";
    }

    /*
     * POP3 Client functions
     * Default POP3 serve : pop.nus.edu.sg 110 
     */
    // Connects to default POP3 server
    public String connectToPop3(String inputUserName, String inputPassword, String inputPOP3) {
        if (inputUserName.isEmpty() != true || inputPassword.isEmpty() != true || inputUserName.length() != 0 || inputPassword.length() != 0) {
            String pop3Returns = "";
            String username = inputUserName;
            String password = inputPassword;
            String pop3 = inputPOP3.trim();
            try {
                // Connect to server
                if (pop3.length() != 0) {
                    pop = new Socket(pop3, 110);
                } else {
                    pop = new Socket("pop.nus.edu.sg", 110);
                }
                popos = pop.getOutputStream();
                popserverWriter = new DataOutputStream(popos);
                popisrServer = new InputStreamReader(pop.getInputStream());
                popserverReader = new BufferedReader(popisrServer);

                String response = popserverReader.readLine();
                pop3Returns += response + ";";
                System.out.println(response);

                while (popserverReader.ready() == true) {
                    response = popserverReader.readLine();
                    pop3Returns += response + ";";
                    System.out.println(response);
                }
                popinFromUser = new BufferedReader(new InputStreamReader(System.in));

                // send userID
                popserverWriter.writeBytes("user " + username + "\r\n");
                popserverWriter.flush();
                response = popserverReader.readLine();
                pop3Returns += response + ";";
                System.out.println(response);

                // Send password
                popserverWriter.writeBytes("PASS " + password + "\r\n");
                response = popserverReader.readLine();
                pop3Returns += response + ";";
                System.out.println(response);

                if (response.compareTo("+OK User successfully logged on.") == 0) {
                    return "connected" + ";" + pop3Returns;
                } else {
                    return "notconnected" + ";" + pop3Returns;
                }

            } catch (Exception error) {
                System.out.println("An error has occured at " + error.getStackTrace());
            }
        } else {
            return "notconnected";
        }
        return "notconnected";
    }

    // Check user inbox status
    public String checkInboxStatus() {
        try {
            String inBoxStatus = "\n\n";
            popserverWriter.writeBytes("STAT\r\n");
            popserverWriter.flush();
            response = popserverReader.readLine();
            inBoxStatus += response + ";";
            System.out.println(response);

            popserverWriter.writeBytes("LIST\r\n");
            popserverWriter.flush();
            response = popserverReader.readLine();
            inBoxStatus += response + ";";
            System.out.println(response);

            while (popserverReader.ready() == true) {
                response = popserverReader.readLine();
                inBoxStatus += response + ";";
                System.out.println(response);
            }

            return "inBoxStatus" + ";" + inBoxStatus;
        } catch (Exception e) {
            System.out.println(e.getStackTrace());
        }
        return null;
    }

    // Test function to RETR a mail
    public String testRETR() {
        try {
            String retr = "\n\n";

            serverWriter.writeBytes("RETR 20\r\n");
            serverWriter.flush();
            response = serverReader.readLine();
            retr += response + ";";
            System.out.println(response);
            response = serverReader.readLine();

            if (response != null) {

                while (response.compareTo(".") != 0) {
                    retr += response + ";";
                    response = serverReader.readLine();
                }
                retr += ".;";
            }

            return "retr" + ";" + retr;

        } catch (Exception e) {
            System.out.println(e.getStackTrace());
        }
        return null;
    }

    // RETR all mails from user inbox and returns them
    public ArrayList<ArrayList<String>> retrInbox(int numOfMails) {
        try {
            ArrayList<ArrayList<String>> inboxList = new ArrayList();

            // For loop to save all mails
            for (int i = 1; i <= numOfMails; i++) {
                ArrayList<String> mail = new ArrayList();
                System.out.println("Downloading Mail: " + i);
                // RETR mail number i
                popserverWriter.writeBytes("RETR " + i + "\r\n");
                popserverWriter.flush();
                response = popserverReader.readLine();

                // Merge every lines of mail content
                while (response.compareTo(".") != 0) {
                    mail.add(response);
                    response = popserverReader.readLine();
                }

                mail.add("" + response);
                inboxList.add(mail);
                //inboxList.add(mailInfo+".\n");
            }
            return inboxList;
        } catch (Exception e) {
            System.out.println(e.getStackTrace());
        }
        return null;
    }

    // Disconnect from POP3 server 
    public String quitPop3() {
        try {
            String quit = "\n\n";

            // Send QUIT command
            popserverWriter.writeBytes("QUIT\r\n");
            popserverWriter.flush();
            response = popserverReader.readLine();
            quit += response + ";";
            System.out.println(response);

            return "quit" + ";" + quit;

        } catch (Exception e) {
            System.out.println(e.getStackTrace());
        }
        return null;
    }
    /*
     * Base64 convertor
     * Reference: http://www.wikihow.com/Encode-a-String-to-Base64-With-Java
     */
    private static final String base64code = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
            + "abcdefghijklmnopqrstuvwxyz" + "0123456789" + "+/";
    private static final int splitLinesAt = 76;

    public static byte[] zeroPad(int length, byte[] bytes) {
        byte[] padded = new byte[length]; // initialized to zero by JVM
        System.arraycopy(bytes, 0, padded, 0, bytes.length);
        return padded;
    }

    public static String encode(String string) {

        String encoded = "";
        byte[] stringArray;
        try {
            stringArray = string.getBytes("UTF-8");  // use appropriate encoding string!
        } catch (Exception ignored) {
            stringArray = string.getBytes();  // use locale default rather than croak
        }
        // determine how many padding bytes to add to the output
        int paddingCount = (3 - (stringArray.length % 3)) % 3;
        // add any necessary padding to the input
        stringArray = zeroPad(stringArray.length + paddingCount, stringArray);
        // process 3 bytes at a time, churning out 4 output bytes
        // worry about CRLF insertions later
        for (int i = 0; i < stringArray.length; i += 3) {
            int j = ((stringArray[i] & 0xff) << 16)
                    + ((stringArray[i + 1] & 0xff) << 8)
                    + (stringArray[i + 2] & 0xff);
            encoded = encoded + base64code.charAt((j >> 18) & 0x3f)
                    + base64code.charAt((j >> 12) & 0x3f)
                    + base64code.charAt((j >> 6) & 0x3f)
                    + base64code.charAt(j & 0x3f);
        }
        // replace encoded padding nulls with "="
        return splitLines(encoded.substring(0, encoded.length()
                - paddingCount) + "==".substring(0, paddingCount));

    }

    public static String splitLines(String string) {

        String lines = "";
        for (int i = 0; i < string.length(); i += splitLinesAt) {

            lines += string.substring(i, Math.min(string.length(), i + splitLinesAt));
            lines += "\r\n";

        }
        return lines;

    }
}
