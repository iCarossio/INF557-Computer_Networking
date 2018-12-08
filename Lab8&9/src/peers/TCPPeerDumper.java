package peers;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class TCPPeerDumper implements Runnable {

    public static final int TCP_PORT = 4243;

    private static final TCPPeerDumper instance = new TCPPeerDumper();

    public static TCPPeerDumper getInstance() {
        return instance;
    }

    private static final int PEERID_COL_W = 20;
    private static final int PEERADDR_COL_W = 20;
    private static final int PEERSEQ_COL_W = 20;
    private static final int PEEREXPIRATION_COL_W = 30;
    private static final int PEERSTATE_COL_W = 20;

    private static final int VERTICAL_PADDING = 1;
    private static final int HORIZONTAL_PADDING = 2;

    private static String header; // No '\n'
    private static String line; // Full line, no '\n'
    private static String emptyLine; // No '\n'
    private static String emptyPadding; // Starts with a '\n'

    private ServerSocket serverSocket;
    private boolean initialized;

    private TCPPeerDumper() {
    }

    public void initialize() throws IOException {
        line = generateLine();
        emptyLine = generateEmptyLine();
        emptyPadding = generateEmptyPadding();
        header = generateHeader();
        initialized = true;
        serverSocket = new ServerSocket(TCP_PORT);
    }

    public void run() throws IllegalStateException {

        if (!initialized)
            throw new IllegalStateException("TCPPeerDumper must be initialized before running!");

        while (!Thread.currentThread().isInterrupted()) {
            Socket socket = null;
            try {
                socket = serverSocket.accept();
                DataOutputStream outToClient = new DataOutputStream(socket.getOutputStream());
                outToClient.writeBytes(getAsciiPeerTable());
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Store all information of the PeerTable in an ASCII-art string,
     */
    private static String getAsciiPeerTable() {
        HashMap<String, PeerRecord> localPeerTable = PeerTableManager.getInstance().getPeerTableCopy();
        long currentTimeMillis = System.currentTimeMillis();

        StringBuilder retBuilder = new StringBuilder();

        retBuilder.append(header);
        retBuilder.append('\n');

        for (Map.Entry<String, PeerRecord> entry : localPeerTable.entrySet())
            retBuilder.append(generateRow(entry.getKey(), entry.getValue(), currentTimeMillis));

        retBuilder.append(line);
        retBuilder.append('\n');

        return retBuilder.toString();
    }

    /**
     * @return a row without a bottom line but with a trailing '\n"
     */
    private static String generateRow(String peerId, PeerRecord record, long currentTimeMillis) {

        String ret = "";

        ret += line;
        // Empty padding already starts with a '\n'
        ret += emptyPadding;
        ret += '\n';
        ret += generateRowContent(peerId, record, currentTimeMillis);
        // Empty padding already starts with a '\n'
        ret += emptyPadding;
        ret += '\n';


        return ret;

    }

    private static String generateRowContent(String peerId, PeerRecord record, long currentTimeMillis) {
        String ret = "";

        ret += "|";
        ret += generateCellContent(peerId, PEERID_COL_W);
        ret += "|";
        ret += generateCellContent(record.getPeerAddress().toString(), PEERADDR_COL_W);
        ret += "|";
        ret += generateCellContent("" + record.getPeerSequenceNumber(), PEERSEQ_COL_W);
        ret += "|";
        ret += generateCellContent("" + (record.getExpirationTime() - currentTimeMillis) + " ms", PEEREXPIRATION_COL_W);
        ret += "|";
        ret += generateCellContent(record.getPeerState().name(), PEERSTATE_COL_W);
        ret += "|";

        return ret;
    }


    private String generateHeader() {
        String ret = "";

        ret += " ";
        ret += generateCellContent("PeerID", PEERID_COL_W);
        ret += " ";
        ret += generateCellContent("PeerAddress", PEERADDR_COL_W);
        ret += " ";
        ret += generateCellContent("PeerSeqNb", PEERSEQ_COL_W);
        ret += " ";
        ret += generateCellContent("PeerExpirationRemaining", PEEREXPIRATION_COL_W);
        ret += " ";
        ret += generateCellContent("PeerState", PEERSTATE_COL_W);
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
        for (int i = 0; i < PEERID_COL_W + 2 * HORIZONTAL_PADDING; i++)
            ret.append("-");
        ret.append("+");
        for (int i = 0; i < PEERADDR_COL_W + 2 * HORIZONTAL_PADDING; i++)
            ret.append("-");
        ret.append("+");
        for (int i = 0; i < PEERSEQ_COL_W + 2 * HORIZONTAL_PADDING; i++)
            ret.append("-");
        ret.append("+");
        for (int i = 0; i < PEEREXPIRATION_COL_W + 2 * HORIZONTAL_PADDING; i++)
            ret.append("-");
        ret.append("+");
        for (int i = 0; i < PEERSTATE_COL_W + 2 * HORIZONTAL_PADDING; i++)
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
        for (int i = 0; i < PEERID_COL_W + 2 * HORIZONTAL_PADDING; i++)
            ret.append(" ");
        ret.append("|");
        for (int i = 0; i < PEERADDR_COL_W + 2 * HORIZONTAL_PADDING; i++)
            ret.append(" ");
        ret.append("|");
        for (int i = 0; i < PEERSEQ_COL_W + 2 * HORIZONTAL_PADDING; i++)
            ret.append(" ");
        ret.append("|");
        for (int i = 0; i < PEEREXPIRATION_COL_W + 2 * HORIZONTAL_PADDING; i++)
            ret.append(" ");
        ret.append("|");
        for (int i = 0; i < PEERSTATE_COL_W + 2 * HORIZONTAL_PADDING; i++)
            ret.append(" ");
        ret.append("|");

        return ret.toString();
    }
}
