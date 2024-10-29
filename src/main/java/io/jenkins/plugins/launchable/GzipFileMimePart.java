package io.jenkins.plugins.launchable;

import org.apache.commons.io.output.CloseShieldOutputStream;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MIME;
import org.apache.http.entity.mime.content.AbstractContentBody;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.GZIPOutputStream;

final class GzipFileMimePart extends AbstractContentBody {
    private final File file;
    private final String filename;

    public GzipFileMimePart(File file, ContentType contentType, String filename) {
        super(contentType);
        this.file = file;
        this.filename = filename == null ? file.getName() : filename;
    }

    @Override
    public void writeTo(OutputStream out) throws IOException {
        try (OutputStream o = new GZIPOutputStream(new CloseShieldOutputStream(out));
             InputStream in = new FileInputStream(this.file)) {
            final byte[] tmp = new byte[4096];
            int l;
            while ((l = in.read(tmp)) != -1) {
                o.write(tmp, 0, l);
            }
        }
    }

    @Override
    public String getTransferEncoding() {
        return MIME.ENC_BINARY;
    }

    @Override
    public long getContentLength() {
        return this.file.length();
    }

    @Override
    public String getFilename() {
        return filename;
    }
}
