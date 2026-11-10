package respwriter;

import java.io.PrintWriter;

import static java.util.Objects.isNull;

public class RespWriter {
    public void printOutcome(String outcome, PrintWriter out) {
        if (isNull(outcome)){
            out.print("$-1\r\n");
        } else if (outcome.startsWith("+")) {
            out.print(outcome + "\r\n");
        } else {
            out.print("$" + outcome.length() + "\r\n" + outcome + "\r\n");
        }

        out.flush();
    }

    public void printIntegerOutcome(int value, PrintWriter out) {
        out.print(":" + value + "\r\n");

        out.flush();
    }
}
