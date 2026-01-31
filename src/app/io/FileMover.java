package app.io;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

public final class FileMover {
    public static final String MOVE_TRASH = "MoveToTrash";
    public static final String MOVE_KEEP = "MoveToKeep";

    private final String mDestName;
    private final Path mRootPath;

    public FileMover(Path rootPath, String type) {
        mDestName = switch (type) {
            case MOVE_KEEP -> "__keep__";
            case MOVE_TRASH -> "__trash__";
            default -> "unknown";
        };
        mRootPath = rootPath;
    }

    public void moveFiles(List<Path> filesToMove) throws IOException {
        Path destination = Paths.get(mRootPath.toAbsolutePath().toString(), mDestName);
        if (!Files.exists(destination)) {
            Files.createDirectory(destination);
        }
        if (!Files.isDirectory(destination)) {
            Files.delete(destination);
            Files.createDirectory(destination);
        }
        for (Path path : filesToMove) {
            System.out.println("Moving: " + path.toAbsolutePath() + " to " + destination.toAbsolutePath());
            Path movedFilePath = Paths.get(destination.toAbsolutePath().toString(), path.getFileName().toString());
            Files.move(path, movedFilePath, StandardCopyOption.REPLACE_EXISTING);
        }
    }
}
