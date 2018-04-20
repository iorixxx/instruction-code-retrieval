package edu.anadolu;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.custom.CustomAnalyzer;
import org.apache.lucene.analysis.miscellaneous.LengthFilterFactory;
import org.apache.lucene.analysis.pattern.SimplePatternSplitTokenizerFactory;
import org.apache.lucene.analysis.shingle.ShingleFilterFactory;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.ConcurrentMergeScheduler;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class Indexer {


    static Analyzer shingle() throws IOException {
        return CustomAnalyzer.builder()
                .withTokenizer(SimplePatternSplitTokenizerFactory.class, "pattern", "[\r\n]+")
                .addTokenFilter("trim")
                .addTokenFilter(LengthFilterFactory.class, "min", "1", "max", "255")
                .addTokenFilter("lowercase")
                .addTokenFilter(ShingleFilterFactory.class,
                        "minShingleSize", "2",
                        "maxShingleSize", "50",
                        "outputUnigrams", "false",
                        "outputUnigramsIfNoShingles", "false",
                        "tokenSeparator", Character.toString(App.TOKEN_SEPARATOR),
                        "fillerToken", "#"
                ).build();
    }

    public int index(Path indexPath, Path input) throws IOException {

        System.out.println("Indexing " + input.toString() + " to directory '" + indexPath + "'...");

        Directory dir = FSDirectory.open(indexPath);

        IndexWriterConfig iwc = new IndexWriterConfig(shingle());

        iwc.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
        iwc.setUseCompoundFile(false);
        iwc.setMergeScheduler(new ConcurrentMergeScheduler());
        iwc.setRAMBufferSizeMB(256.0);

        final IndexWriter writer = new IndexWriter(dir, iwc);


        Document document = new Document();
        document.add(new StringField("path", input.toString(), Field.Store.YES));
        document.add(new TextField("code", Files.newBufferedReader(input, StandardCharsets.US_ASCII)));

        writer.addDocument(document);

        final int numIndexed = writer.maxDoc();

        try {
            writer.commit();
            writer.forceMerge(1);
        } finally {
            writer.close();
            dir.close();
        }

        return numIndexed;
    }
}
