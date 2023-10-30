import java.io.*;
import java.time.*;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.io.File;


public final class LogReader {

    public void readLog(String path) {
        Instant start = Instant.now();
        String line;

        Set<String> uniqueLibrary = new HashSet<>();
        HashMap<String, Integer> severity = new HashMap<>();
        LocalDate firstDate = null;
        LocalTime firstTime = null;
        LocalDate lastDate = null;
        LocalTime lastTime = null;
        File f = new File(path);

        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(f))) {
            while ((line = bufferedReader.readLine())  != null) {
                String[] splitted = line.split(" ");

                LocalDate localDate = LogReader.getLocalDate(splitted[0]);
                if (localDate == null) {
                    continue;
                }

                if(firstDate == null){
                    firstDate = localDate;
                }

                LocalTime localTime = LocalTime.parse(splitted[1].replace(",", "."));

                if(firstTime == null){
                    firstTime = localTime;
                }

                if(splitted[3] != null && !splitted[3].equals("")){
                    uniqueLibrary.add(splitted[3]);
                }

                this.snitch(severity, splitted[2]);
                lastTime = localTime;
                lastDate = localDate;
            }
        } catch (IOException e) {
            System.out.println("Błędy:");
            e.printStackTrace();
        }

        if (this.areDatesSet(firstDate, firstTime, lastDate, lastTime)) {
            System.out.println(String.format("Plik: %s nie zawierał logów", path));
            return;
        }

        LocalDateTime firstDateTime = LocalDateTime.of(firstDate, firstTime);
        LocalDateTime lastDateTime = LocalDateTime.of(lastDate,lastTime);
        Duration diff = Duration.between(firstDateTime, lastDateTime);

        Instant end = Instant.now();
        long readingTime = end.toEpochMilli() - start.toEpochMilli();
        this.printer(uniqueLibrary, severity, diff, readingTime);
    }

    public void printer(
            Set<String> uniqueLibraries,
            Map<String, Integer> severity,
            Duration difference,
            long readingTime

    ){
        System.out.printf("Czas wczytywania pliku: %s milisekund\n", readingTime);
        int sum = severity.values()
                .stream()
                .mapToInt(myInt -> myInt.intValue())
                .sum();

        System.out.printf("Zakres logów w pliku: %s", sum);
        System.out.printf("Różnica czasu pomiędzy najstarszym i najmłodszym logiem: %s\n", difference.toHours());
        System.out.println("Ilość logów wg. serverity:");
        System.out.printf("ERROR: %s\n", severity.get("ERROR"));
        System.out.printf("INFO: %s\n", severity.get("INFO"));
        System.out.printf("WARN: %s\n", severity.get("WARN"));
        System.out.printf("FATAL: %s\n", severity.get("FATAL"));
        System.out.printf("DEBUG: %s\n", severity.get("DEBUG"));
        int errorOrHigher = severity.get("ERROR") + severity.get("FATAL");
        System.out.printf("Stosunkek logow ERROR i wyzszych do reszty: %s/%s\n", errorOrHigher, sum);
        System.out.printf("Ilość unikalnych wystąpień bibliotek w logu: %s\n", uniqueLibraries.size());
        System.out.println("Lista unikalnych bibliotek:");
        uniqueLibraries.forEach(library -> System.out.println(library));
        System.out.println("----------------------------------------");
    }

    private boolean areDatesSet(
            LocalDate firstDate, LocalTime firstTime, LocalDate lastDate, LocalTime lastTime
    ) {
        return firstDate == null && firstTime == null && lastDate == null && lastTime == null;
    }

    private void snitch(Map<String, Integer> severity, String logType) {
        switch (logType) {
            case "ERROR":
                Integer count = severity.get("ERROR");
                if (count == null) {
                    severity.put("ERROR", 1);
                } else {
                    severity.put("ERROR", count+1);
                }
                break;

            case "DEBUG":
                Integer count1 = severity.get("DEBUG");
                if(count1 == null) {
                    severity.put("DEBUG", 1);
                } else {
                    severity.put("DEBUG", count1+1);
                }
                break;

            case "FATAL":
                Integer count2 = severity.get("FATAL");
                if (count2 == null) {
                    severity.put("FATAL", 1);
                } else {
                    severity.put("ERROR", count2+1);
                }
                break;

            case "INFO":
                Integer count3 = severity.get("INFO");
                if (count3 == null) {
                    severity.put("INFO", 1);
                } else {
                    severity.put("INFO", count3+1);
                }
                break;

            case "WARN":
                Integer count4 = severity.get("WARN");
                if(count4 == null){
                    severity.put("WARN", 1);
                }else{
                    severity.put("WARN", count4+1);
                }
                break;
            default:
                break;

        }
    }

    private static LocalDate getLocalDate(String date) {
        try {
            return LocalDate.parse(date);
        } catch (DateTimeParseException e) {
            return null;
        }
    }
}
