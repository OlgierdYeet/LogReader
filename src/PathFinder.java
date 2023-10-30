import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class PathFinder {
    private List<String> result = new ArrayList<>();

    public PathFinder() {
        this.result = new ArrayList<>();
    }

    public List<String> search (File dir){
        File[] elements = dir.listFiles();

        for(File element : elements){
            if(element.isFile()){
                if(element.getAbsolutePath().endsWith(".log")){
                    this.result.add(element.getAbsolutePath());
                }
            } else if (element.isDirectory()) {
                this.search(element.getAbsoluteFile());
            }
        }
        return result;
    }


}
