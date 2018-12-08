package database;

import javax.xml.crypto.Data;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TCPDatabaseDumper implements Runnable {

    public static final int TCP_PORT = 4244;

    private static final TCPDatabaseDumper instance = new TCPDatabaseDumper();

    public static TCPDatabaseDumper getInstance() {
        return instance;
    }

    private static final int COL_W = 50;

    private static final int VERTICAL_PADDING = 0;
    private static final int HORIZONTAL_PADDING = 2;

    private static String line; // Full line, no '\n'
    private static String emptyLine; // No '\n'
    private static String emptyPadding; // Starts with a '\n'

    private ServerSocket serverSocket;
    private boolean initialized;

    private TCPDatabaseDumper() {
    }

    public void initialize() throws IOException {
        line = generateLine();
        emptyLine = generateEmptyLine();
        emptyPadding = generateEmptyPadding();
        initialized = true;
        serverSocket = new ServerSocket(TCP_PORT);
    }

    public void run() throws IllegalStateException {

        if (!initialized)
            throw new IllegalStateException("TCPDatabaseDumper must be initialized before running!");

        while (!Thread.currentThread().isInterrupted()) {
            Socket socket = null;
            try {
                socket = serverSocket.accept();
                DataOutputStream outToClient = new DataOutputStream(socket.getOutputStream());
                outToClient.writeBytes(getAsciiDatabase());
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Store all information of the Database in an ASCII-art string,
     */
    private static String getAsciiDatabase() {
        List<String> localCopy = new ArrayList<>(DatabaseManager.getInstance().getData(DatabaseManager.getInstance().getDatabaseSequenceNb()));

        StringBuilder retBuilder = new StringBuilder();

        HashMap<String, List<String>> databases = DatabaseManager.getInstance().getDatabases();

        for (String dbName : databases.keySet()) {
            retBuilder.append(generateHeader(dbName));
            retBuilder.append('\n');

            for (String entry : databases.get(dbName))
                retBuilder.append(generateRow(entry));

            retBuilder.append(line);
            retBuilder.append('\n');
            retBuilder.append('\n');
            retBuilder.append('\n');
        }





        return retBuilder.toString();
    }

    /**
     * @return a row without a bottom line but with a trailing '\n"
     */
    private static String generateRow(String content) {

        String ret = "";

        ret += line;
        // Empty padding already starts with a '\n'
        ret += emptyPadding;
        ret += '\n';
        ret += generateRowContent(content);
        // Empty padding already starts with a '\n'
        ret += emptyPadding;
        ret += '\n';


        return ret;

    }

    private static String generateRowContent(String content) {
        String ret = "";

        ret += "|";
        ret += generateCellContent(content, COL_W);
        ret += "|";

        return ret;
    }


    private static String generateHeader(String dbName) {
        String ret = "";

        ret += " ";
        ret += generateCellContent(dbName, COL_W);
        ret += " ";

        return ret;
    }

    /**
     * @warning Do not specify padding, it will be added automatically
     */
    private static String generateCellContent(String content, int widthWithoutPadding) {
        StringBuilder ret = new StringBuilder();

        // What will actually be written in the cell (may be only a part of the identifier
        String contentToWrite;

        if (content.length() <= widthWithoutPadding)
            contentToWrite = content;
        else
            contentToWrite = content.substring(0, widthWithoutPadding - 2) + "..";

        int additionalPadding = (widthWithoutPadding - contentToWrite.length());

        int additionalPaddingLeft = additionalPadding / 2;
        int additionalPaddingRight = additionalPadding - additionalPaddingLeft;

        for (int i = 0; i < additionalPaddingLeft + HORIZONTAL_PADDING; i++)
            ret.append(" ");
        ret.append(contentToWrite);
        for (int i = 0; i < additionalPaddingRight + HORIZONTAL_PADDING; i++)
            ret.append(" ");

        return ret.toString();
    }

    private static String generateLine() {
        StringBuilder ret = new StringBuilder();

        ret.append("+");
        for (int i = 0; i < COL_W + 2 * HORIZONTAL_PADDING; i++)
            ret.append("-");
        ret.append("+");

        return ret.toString();
    }

    private static String generateEmptyPadding() {
        StringBuilder ret = new StringBuilder();

        for (int i = 0; i < VERTICAL_PADDING; i++) {
            ret.append('\n');
            ret.append(emptyLine);
        }

        return ret.toString();
    }

    private static String generateEmptyLine() {
        StringBuilder ret = new StringBuilder();

        ret.append("|");
        for (int i = 0; i < COL_W + 2 * HORIZONTAL_PADDING; i++)
            ret.append(" ");
        ret.append("|");

        return ret.toString();
    }
}
