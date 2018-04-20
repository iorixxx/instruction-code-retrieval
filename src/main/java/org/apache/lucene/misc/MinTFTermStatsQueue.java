package org.apache.lucene.misc;

import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.PriorityQueue;

import java.io.IOException;
import java.util.Comparator;

/**
 * Copied from {@link org.apache.lucene.misc.HighFreqTerms.TermStatsQueue}
 * Cannot extend it because it is defined as final.
 */
public class MinTFTermStatsQueue extends PriorityQueue<TermStats> {

    private final Comparator<TermStats> comparator;
    private final int minTF;

    public MinTFTermStatsQueue(int size, Comparator<TermStats> comparator, int minTF) {
        super(size);
        this.comparator = comparator;
        this.minTF = minTF;
    }

    @Override
    protected boolean lessThan(TermStats termInfoA, TermStats termInfoB) {
        return comparator.compare(termInfoA, termInfoB) < 0;
    }

    public void fill(String field, TermsEnum termsEnum) throws IOException {
        BytesRef term;
        while ((term = termsEnum.next()) != null) {
            if (termsEnum.totalTermFreq() < minTF) continue;
            insertWithOverflow(new TermStats(field, term, termsEnum.docFreq(), termsEnum.totalTermFreq()));
        }
    }
}
