package commandprocessor;

import model.CachedString;
import respwriter.RespWriter;
import storage.StorageHandler;

import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        }
    }
}
