package com.easyuitools.util.factory;

/**
 * Created by suzy2 on 2016/12/12.
 */

import java.io.*;

class StreamPrinter extends Thread {
    private InputStream is;
    private String msg;
    private OutputStream os;

    StreamPrinter(InputStream stream, String type, OutputStream redirect) {
        is = stream;
        msg = type;
        os = redirect;
    }

    public void run() {
        try {
            PrintWriter pw = null;
            if (os != null) {
                pw = new PrintWriter(os);
            }
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            String line = br.readLine();
            while (line != null) {
                if (pw != null) {
                    pw.println(msg + " " + line);
                }
                line = br.readLine();
            }
            if (pw != null) {
                pw.flush();
            }
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
    }
}

