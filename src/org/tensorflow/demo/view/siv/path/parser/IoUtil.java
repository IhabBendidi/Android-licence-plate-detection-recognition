package org.tensorflow.demo.view.siv.path.parser;

import java.io.InputStream;

public class IoUtil {
    public static final void closeQuitely(InputStream is) {
        if(is != null) {
            try {
                is.close();
            } catch (Throwable ignored) {
                //ignored
            }
        }
    }
}
