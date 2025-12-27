package app.io;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Consumer;

public final class FileLoader {
    public static final FileLoader instance = new FileLoader();

    private FileLoader(){}

    public void loadFiles(Path root, List<Path> dest) {
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(root)) {
            for(Path path: stream) {
                dest.add(path.toAbsolutePath());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private JFileChooser createFileChooser(final Consumer<File> consumer) {
        return new JFileChooser(){
            @Override
            public void approveSelection() {
                File f = getSelectedFile();
                consumer.accept(f);
                super.approveSelection();
            }
        };
    }

    public void getDirectory(Consumer<File> consumer) {
        JFileChooser chooser = createFileChooser(consumer);
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setDialogTitle("Choose Directory.");
        chooser.showDialog(null, "Select");
    }

}
