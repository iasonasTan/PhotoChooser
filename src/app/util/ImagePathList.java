package app.util;

import java.nio.file.Path;
import java.util.ArrayList;

public class ImagePathList extends ArrayList<Path> {
    @Override
    public boolean add(Path path) {
        if(checkPath(path))
            return super.add(path);
        return false;
    }

    @Override
    public void add(int index, Path path) {
        if(checkPath(path))
            super.add(index, path);
    }

    private boolean checkPath(Path path) {
        String fileName = path.getFileName().toString();
        if(Utils.endsWith(fileName, ".png", ".jpg", ".JPG"))
            return true;

        IO.println("[DEBUG] Rejected item \""+fileName+"\" unrecognizable file extension.");
        return false;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("ImagePathList:{");
        for (Path path: this)
            builder.append("\n\t").append(path);
        builder.append("\n}");
        return builder.toString();
    }
}
