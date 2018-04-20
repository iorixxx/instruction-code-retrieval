package edu.anadolu;

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

        Path input = Paths.get("/Users/iorixxx/Desktop/input.txt");

        Path indexPath = Files.createTempDirectory("index");

        Indexer indexer = new Indexer();

        indexer.index(indexPath, input);

        try (HighFreq highFreq = new HighFreq(indexPath)) {
            highFreq.listTerms(10);
            highFreq.listLongestMatches(10, 50);
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
