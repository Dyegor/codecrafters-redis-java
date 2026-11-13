package commandprocessor;

import model.CachedString;
import respwriter.RespWriter;
import storage.StorageHandler;

import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static java.time.LocalDateTime.now;
import static java.time.temporal.ChronoUnit.MILLIS;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

public class CommandProcessor {
    private final RespWriter respWriter = new RespWriter();
    private final StorageHandler storageHandler = new StorageHandler();

    public void processCommand(List<String> inputData, PrintWriter out) {
        String command = inputData.removeFirst();

        switch (command) {
            case "PING" -> {
                respWriter.printOutcome("+PONG", out);
            }
            case "ECHO" -> {
                StringBuilder argumentsToPrint = new StringBuilder();
                for (String argument : inputData) {
                    argumentsToPrint.append(argument);
                }
                respWriter.printOutcome(argumentsToPrint.toString(), out);
            }
            case "SET" -> {
                String keyToCache = inputData.removeFirst();
                String stringToCache = inputData.removeFirst();
                LocalDateTime expiresAt = null;

                if (!inputData.isEmpty()) {
                    expiresAt = now().plus(Integer.parseInt(inputData.getLast()), MILLIS);
                }

                CachedString cachedString = new CachedString(stringToCache, expiresAt);

                storageHandler.getStringsMap().put(keyToCache, cachedString);
                respWriter.printOutcome("+OK", out);
            }
            case "GET" -> {
                CachedString cachedString =  storageHandler.getStringsMap().get(inputData.getFirst());

                if (isNull(cachedString) || (nonNull(cachedString.expiresAt())
                        && now().isAfter(cachedString.expiresAt()))) {
                    respWriter.printOutcome(null, out);
                } else {
                    respWriter.printOutcome(cachedString.cachedValue(), out);
                }
            }
            case "RPUSH" -> {
                List<String> valuesList = storageHandler.getListsMap()
                        .computeIfAbsent(inputData.removeFirst(),key -> new ArrayList<>());

                valuesList.addAll(inputData);

                respWriter.printIntegerOutcome(valuesList.size(), out);
            }
            case "LPUSH" -> {
                List<String> valuesList = storageHandler.getListsMap()
                        .computeIfAbsent(inputData.removeFirst(),key -> new ArrayList<>());

                for (String value : inputData) {
                    valuesList.addFirst(value);
                }

                respWriter.printIntegerOutcome(valuesList.size(), out);
            }
            case "LRANGE" -> {
                List<String> valuesList = storageHandler.getListsMap().get(inputData.removeFirst());

                if (isNull(valuesList)) {
                    respWriter.printArray(new ArrayList<>(), out);
                    return;
                }

                List<String> results = getResults(inputData, valuesList);

                respWriter.printArray(results, out);
            }
            case "LLEN" -> {
                List<String> valuesList = storageHandler.getListsMap()
                        .computeIfAbsent(inputData.removeFirst(),key -> new ArrayList<>());

                respWriter.printIntegerOutcome(valuesList.size(), out);
            }
        }
    }

    private static List<String> getResults(List<String> inputData, List<String> valuesList) {
        int firstIndex =  Integer.parseInt(inputData.getFirst());
        int lastIndex =  Integer.parseInt(inputData.getLast());

        if (lastIndex >= valuesList.size()) {
            lastIndex = valuesList.size() - 1;
        }

        if (Math.abs(firstIndex) > valuesList.size()) {
            firstIndex = 0;
        }

        if (Math.abs(lastIndex) > valuesList.size()) {
            lastIndex = 0;
        }

        if (firstIndex < 0) {
            firstIndex = valuesList.size() - Math.abs(firstIndex);
        }

        if (lastIndex < 0) {
            lastIndex = valuesList.size() - Math.abs(lastIndex);
        }

        List<String> results = new ArrayList<>();

        for (int i = firstIndex; i <= lastIndex; i++) {
            results.add(valuesList.get(i));
        }

        return results;
    }
}
