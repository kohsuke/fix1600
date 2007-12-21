package org.jvnet.fix1600;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Fix <a href="http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6553734">bug 6553734</a>.
 * 
 * @author Kohsuke Kawaguchi
 */
public class Tool {
    public static void main(String[] args) throws IOException {
        if(args.length==0) {
            System.err.println("Usage: java -jar fix1600.jar [DIR]... ");
            System.exit(-1);
        }

        Tool tool = new Tool(new Reporter() {
            public void found(File f) {
                System.out.println(f.getPath());
            }
        });

        for (String arg : args)
            tool.process(new File(arg));
    }

    private final Reporter reporter;

    public Tool(Reporter reporter) {
        this.reporter = reporter;
    }

    public void process(File f) throws IOException {
        if(f.isDirectory())
            processDirectory(f);
        else
        if(f.getName().endsWith(".class"))
            processClassFile(f);
    }

    public void processClassFile(File f) throws IOException {
        try {
            RandomAccessFile raf = new RandomAccessFile(f,"rw");
            try {
                raf.skipBytes(8);
                // constant pool count
                int cpCount = raf.readShort();
                for( int i=1; i<cpCount; i++ ) { // note that this is 1-origin
                    byte tag = raf.readByte();
                    switch (tag) {
                    case 1: // UTF-8
                        raf.readUTF();
                        break;
                    case 3: // Integer
                    case 4: // Float
                        raf.skipBytes(4);
                        break;
                    case 5: // Long
                    case 6: // Double
                        raf.skipBytes(8);
                        break;
                    case 7: // Class
                        raf.skipBytes(2);
                        break;
                    case 8: // String
                        raf.skipBytes(2);
                        break;
                    case 9: // Fieldref
                    case 10: // Methodref
                    case 11: // InterfaceMethodref
                        raf.skipBytes(4);
                        break;
                    case 12: // NameAndType
                        raf.skipBytes(4);
                        break;
                    }
                }

                // now the real part
                short accessModifier = raf.readShort();
                if(accessModifier==0x1600) {
                    reporter.found(f);
                    raf.seek(raf.getFilePointer()-2);
                    raf.writeShort(0x600);
                }
            } finally {
                raf.close();
            }
        } catch (IOException e) {
            IOException x = new IOException("Failed to process " + f);
            x.initCause(e);
            throw x;
        }

    }

    public void processDirectory(File dir) throws IOException {
        for( File f : dir.listFiles() )
            process(f);
    }
}
