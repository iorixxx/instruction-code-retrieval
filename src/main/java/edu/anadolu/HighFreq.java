package edu.anadolu;

import org.apache.lucene.index.*;
import org.apache.lucene.misc.HighFreqTerms;
import org.apache.lucene.misc.MinTFTermStatsQueue;
import org.apache.lucene.misc.TermStats;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.BytesRef;

import java.io.Closeable;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.Locale;

import static edu.anadolu.App.SEPARATOR_CODE_POINT;
import static org.apache.lucene.misc.HighFreqTerms.DEFAULT_NUMTERMS;


public class HighFreq implements Closeable {

    private final IndexReader reader;

    HighFreq(Path indexPath) throws IOException {
        this.reader = DirectoryReader.open(FSDirectory.open(indexPath));
    }

    public void listTerms() throws Exception {
        listTerms(DEFAULT_NUMTERMS);
    }

    void listTerms(int numTerms) throws Exception {

        TermStats[] terms = org.apache.lucene.misc.HighFreqTerms.getHighFreqTerms(reader, numTerms, "code", new HighFreqTerms.TotalTermFreqComparator());

        System.out.println("Top-k instruction groups sorted by frequency");
        System.out.println("term \t totalTF \t shingle");

        for (TermStats termStats : terms) {
            System.out.printf(Locale.ROOT, "%s \t %d \t %d \n",
                    termStats.termtext.utf8ToString(), termStats.totalTermFreq, shingleLength(termStats.termtext));

        }

    }

    void listLongestMatches(int numTerms, int minTF) throws Exception {

        TermStats[] terms = getHighFreqTerms(reader, numTerms, "code", new ShingleLengthComparator(), minTF);
        System.out.println("Top-k instruction groups sorted by shingle length");
        System.out.println("term \t totalTF \t shingle");

        for (TermStats termStats : terms) {
            System.out.printf(Locale.ROOT, "%s \t %d \t %d \n",
                    termStats.termtext.utf8ToString(), termStats.totalTermFreq, shingleLength(termStats.termtext));

        }
    }

    public void listLongestMatches(int numTerms) throws Exception {
        listLongestMatches(numTerms, 2);
    }

    public void listLongestMatches() throws Exception {
        listLongestMatches(DEFAULT_NUMTERMS, 2);
    }

    @Override
    public void close() throws IOException {
        reader.close();
    }

    /**
     * Compares terms by shingle length
     */
    public static final class ShingleLengthComparator implements Comparator<TermStats> {

        @Override
        public int compare(TermStats a, TermStats b) {
            int res = Integer.compare(shingleLength(a.termtext), shingleLength(b.termtext));
            if (res == 0) {
                res = a.field.compareTo(b.field);
                if (res == 0) {
                    res = a.termtext.compareTo(b.termtext);
                }
            }
            return res;
        }
    }

    /**
     * Copied/Inspired from {@link org.apache.lucene.util.BytesRef#toString()}
     *
     * @param bytesRef BytesRef to be searched
     * @return the number of token separator found in the BytesRef
     */
    private static int shingleLength(BytesRef bytesRef) {

        int count = 0;

        final int end = bytesRef.offset + bytesRef.length;
        for (int i = bytesRef.offset; i < end; i++) {
            if (SEPARATOR_CODE_POINT == bytesRef.bytes[i]) count++;
        }

        return count + 1;
    }


    /**
     * Copied from {@link HighFreqTerms#getHighFreqTerms(org.apache.lucene.index.IndexReader, int, java.lang.String, java.util.Comparator)}
     */
    private static TermStats[] getHighFreqTerms(IndexReader reader, int numTerms, String field, Comparator<TermStats> comparator, int minTF) throws Exception {

        Terms terms = MultiFields.getTerms(reader, field);
        if (terms == null) {
            throw new RuntimeException("field " + field + " not found");
        }

        TermsEnum termsEnum = terms.iterator();
        MinTFTermStatsQueue tiq = new MinTFTermStatsQueue(numTerms, comparator, minTF);
        tiq.fill(field, termsEnum);

        TermStats[] result = new TermStats[tiq.size()];
        // we want highest first so we read the queue and populate the array
        // starting at the end and work backwards
        int count = tiq.size() - 1;
        while (tiq.size() != 0) {
            result[count] = tiq.pop();
            count--;
        }
        return result;
    }

}
