package edu.anadolu;

import org.apache.lucene.index.FilteredTermsEnum;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.util.BytesRef;

import java.io.IOException;

public class MinTFTermsEnum extends FilteredTermsEnum {
    private final int minTF;

    public MinTFTermsEnum(TermsEnum tenum, int minTF) {
        super(tenum);
        this.minTF = minTF;
    }

    @Override
    protected AcceptStatus accept(BytesRef term) throws IOException {
        return tenum.totalTermFreq() < minTF ? MinTFTermsEnum.AcceptStatus.NO : MinTFTermsEnum.AcceptStatus.YES;
    }
}
