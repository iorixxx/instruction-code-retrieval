package edu.anadolu;

import com.lexicalscope.jewel.cli.Option;
import com.lexicalscope.jewel.cli.Unparsed;

public interface Params {

    @Option(defaultValue = "2", longName = "minTF", shortName = "m")
    int minTF();

    @Option(defaultValue = "10", longName = "numTerms", shortName = "n")
    int numTerms();

    @Option(defaultToNull = true, longName = "index", shortName = "i")
    String index();

    @Unparsed(name = "input")
    String input();
}
