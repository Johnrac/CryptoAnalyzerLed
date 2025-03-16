package com.javarush.pukhov.command;

import java.io.OutputStream;
import java.io.Writer;
import java.util.List;
import java.util.Objects;

import com.javarush.pukhov.io.ConsolePrinter;
import com.javarush.pukhov.io.Output;
import com.javarush.pukhov.view.console.constants.Messages;

public final class Exit implements Action {

    Output<OutputStream, Writer> output = new ConsolePrinter();

    @Override
    public void execute(List<String> parameters) {
        output.print(Messages.EXIT_PROGRAM);
    }

    @Override
    public int hashCode() {
        return Objects.hash(output);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Exit)) {
            return false;
        }
        Exit other = (Exit) obj;
        return Objects.equals(output, other.output);
    }

    
}
