package org.tensorflow.demo.view.siv.path.parser;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

class CopyInputStream {
    private static final String TAG = SvgToPath.TAG;

    private final InputStream _is;
    private ByteArrayOutputStream _copy;

    public CopyInputStream(InputStream is) {
        _is = is;

        try {
            copy();
        }
        catch(IOException ex) {
        }
    }

    private void copy() throws IOException {
        _copy = new ByteArrayOutputStream();
        int chunk;
        byte[] data = new byte[256];

        while(-1 != (chunk = _is.read(data))) {
            _copy.write(data, 0, chunk);
        }
        _copy.flush();
    }

    public ByteArrayInputStream getCopy() {
        return new ByteArrayInputStream(_copy.toByteArray());
    }
}

