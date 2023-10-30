import java.io.File;
import java.util.List;

public class Main {
    public static void main(String[] args) {
       PathFinder finder = new PathFinder();
       LogReader reader = new LogReader();
       List<String> paths = finder.search(new File("D:\\logs"));
       for (String path : paths) {
           reader.readLog(path);
       }


    }
}