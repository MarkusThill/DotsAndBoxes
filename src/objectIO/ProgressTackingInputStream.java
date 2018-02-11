package objectIO;


import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;

public class ProgressTackingInputStream extends ObjectInputStream implements IGetProgress {
    private final IOProgress progess;

    public ProgressTackingInputStream(InputStream in, IOProgress progess) throws IOException {
        super(in);
        this.progess = progess;
    }

    public IOProgress getProgess() {
        return progess;
    }

    public int read(byte[] b, int off, int len) throws IOException {
        try {
            return super.read(b, off, len);
        } finally {
            this.progess.update(len);
        }
    }

}