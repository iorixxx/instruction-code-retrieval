package edu.anadolu;

import com.lexicalscope.jewel.cli.CliFactory;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.TypeAttribute;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Hello world!
 */
public class App {

    static final char TOKEN_SEPARATOR = '|';

    static final byte SEPARATOR_CODE_POINT = (byte) Character.codePointAt(new char[]{TOKEN_SEPARATOR}, 0);

    public static void main(String[] args) throws Exception {

        final Params params = CliFactory.parseArguments(Params.class, args);

        Path input = Paths.get(params.input());

        Path indexPath = params.index() == null ? Files.createTempDirectory(null) : Paths.get(params.index());

        Indexer indexer = new Indexer();

        indexer.index(indexPath, input);

        try (HighFreq highFreq = new HighFreq(indexPath)) {
            highFreq.listTerms(params.numTerms());
            highFreq.listLongestMatches(params.numTerms(), params.minTF());
        }
    }


    static void analyze(Analyzer analyzer, Reader reader) throws IOException {

        try (TokenStream ts = analyzer.tokenStream("field", reader)) {

            CharTermAttribute term = ts.addAttribute(CharTermAttribute.class);
            TypeAttribute type = ts.addAttribute(TypeAttribute.class);
            ts.reset(); // Resets this stream to the beginning. (Required)
            while (ts.incrementToken()) {
                System.out.println(term.toString() + " " + type.type());
            }
            ts.end();   // Perform end-of-stream operations, e.g. set the final offset.
        } finally {
            reader.close();
        }

    }

    static void analyze(Analyzer analyzer, String text) throws IOException {
        analyze(analyzer, new StringReader(text));
    }
}
