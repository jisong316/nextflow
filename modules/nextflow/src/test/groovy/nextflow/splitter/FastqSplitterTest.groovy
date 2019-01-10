/*
 * Copyright 2013-2019, Centre for Genomic Regulation (CRG)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package nextflow.splitter

import spock.lang.Specification
import test.TestHelper
/**
 *
 * @author Paolo Di Tommaso <paolo.ditommaso@gmail.com>
 */
class FastqSplitterTest extends Specification {

    def testFastqRead() {

        given:
        def text = '''
            @HWI-EAS209_0006_FC706VJ:5:58:5894:21141#ATCACG/1
            TTAATTGGTAAATAAATCTCCTAATAGCTTAGATNTTACCTTNNNNNNNNNNTAGTTTCTTGAGATTTGTTGGGGGAGACATTTTTGTGATTGCCTTGAT
            +HWI-EAS209_0006_FC706VJ:5:58:5894:21141#ATCACG/1
            efcfffffcfeefffcffffffddf`feed]`]_Ba_^__[YBBBBBBBBBBRTT\\]][]dddd`ddd^dddadd^BBBBBBBBBBBBBBBBBBBBBBBB
            '''
            .stripIndent().trim()

        when:
        def items = new FastqSplitter().target(text).list()
        then:
        items.size() == 1
        items.get(0) == '''
            @HWI-EAS209_0006_FC706VJ:5:58:5894:21141#ATCACG/1
            TTAATTGGTAAATAAATCTCCTAATAGCTTAGATNTTACCTTNNNNNNNNNNTAGTTTCTTGAGATTTGTTGGGGGAGACATTTTTGTGATTGCCTTGAT
            +HWI-EAS209_0006_FC706VJ:5:58:5894:21141#ATCACG/1
            efcfffffcfeefffcffffffddf`feed]`]_Ba_^__[YBBBBBBBBBBRTT\\]][]dddd`ddd^dddadd^BBBBBBBBBBBBBBBBBBBBBBBB
            '''.stripIndent().leftTrim()

        when:
        def entries = new FastqSplitter().target(text).options(record:true).list()
        then:
        entries[0].readHeader == 'HWI-EAS209_0006_FC706VJ:5:58:5894:21141#ATCACG/1'
        entries[0].readString == 'TTAATTGGTAAATAAATCTCCTAATAGCTTAGATNTTACCTTNNNNNNNNNNTAGTTTCTTGAGATTTGTTGGGGGAGACATTTTTGTGATTGCCTTGAT'
        entries[0].qualityHeader == 'HWI-EAS209_0006_FC706VJ:5:58:5894:21141#ATCACG/1'
        entries[0].qualityString == 'efcfffffcfeefffcffffffddf`feed]`]_Ba_^__[YBBBBBBBBBBRTT\\]][]dddd`ddd^dddadd^BBBBBBBBBBBBBBBBBBBBBBBB'

    }

    static final FASTAQ1 = '''
            @SRR636272.19519409/1
            GGCCCGGCAGCAGGATGATGCTCTCCCGGGCCAAGCCGGCTGTGGGGAGCACCCCGCCGCAGGGGGACAGGCGGAGGAAGAAAGGGAAGAAGGTGCCACAGATCG
            +
            CCCFFFFDHHD;FF=GGDHGGHIIIGHIIIBDGBFCAHG@E=6?CBDBB;?BB@BD8BB;BDB<>>;@?BB<9>&5<?288AAABDBBBBACBCAC?@AD?CAC?
            @SRR636272.13995011/1
            GCAGGATGATGCTCTCCCGGGCCAAGCCGGCTGTGGGGAGCACCCCGCCGCAGGGGGACAGGCGGAGGAAGAAAGGGAGATCGGAAGAGCACACGTCTGAACTCC
            +
            BBCFDFDEFFHHFIJIHGHGHGIIFIJJJJIGGBFHHIEGBEFEFFCDDDD:@@<BB8BBDDDDDDBBB?AA?CDABDD5?CDDDBB<A<>ACBB8ACDCD@CD>
            @SRR636272.21107783/1
            CGGGGAGCGCGGGCCCGGCAGCAGGATGATGCTCTCCCGGGCCAAGCCGGCTGTAGGGAGCACCCCGCCGCAGGGGGACAGGCGAGATCGGAAGAGCACACGTCT
            +
            BCCFFDFFHHHHHJJJJJIJHHHHFFFFEEEEEEEDDDDDDBDBDBBDBBDBBB(:ABCDDDDDDDDDDDDDDDD@BBBDDDDDDDDDDDDBDDDDDDDDDDADC
            '''.stripIndent().trim()


    def testFastqSplit() {

        when:
        def items = new FastqSplitter().target(FASTAQ1).list()

        then:
        items.size() == 3
        items.get(0) == '''
            @SRR636272.19519409/1
            GGCCCGGCAGCAGGATGATGCTCTCCCGGGCCAAGCCGGCTGTGGGGAGCACCCCGCCGCAGGGGGACAGGCGGAGGAAGAAAGGGAAGAAGGTGCCACAGATCG
            +
            CCCFFFFDHHD;FF=GGDHGGHIIIGHIIIBDGBFCAHG@E=6?CBDBB;?BB@BD8BB;BDB<>>;@?BB<9>&5<?288AAABDBBBBACBCAC?@AD?CAC?
            ''' .stripIndent() .leftTrim()

        items.get(1) == '''
            @SRR636272.13995011/1
            GCAGGATGATGCTCTCCCGGGCCAAGCCGGCTGTGGGGAGCACCCCGCCGCAGGGGGACAGGCGGAGGAAGAAAGGGAGATCGGAAGAGCACACGTCTGAACTCC
            +
            BBCFDFDEFFHHFIJIHGHGHGIIFIJJJJIGGBFHHIEGBEFEFFCDDDD:@@<BB8BBDDDDDDBBB?AA?CDABDD5?CDDDBB<A<>ACBB8ACDCD@CD>
            ''' .stripIndent() .leftTrim()

        items.get(2) == '''
            @SRR636272.21107783/1
            CGGGGAGCGCGGGCCCGGCAGCAGGATGATGCTCTCCCGGGCCAAGCCGGCTGTAGGGAGCACCCCGCCGCAGGGGGACAGGCGAGATCGGAAGAGCACACGTCT
            +
            BCCFFDFFHHHHHJJJJJIJHHHHFFFFEEEEEEEDDDDDDBDBDBBDBBDBBB(:ABCDDDDDDDDDDDDDDDD@BBBDDDDDDDDDDDDBDDDDDDDDDDADC
            ''' .stripIndent() .leftTrim()

    }

    static final FASTQ2 = '''
            @SRR636272.19519409/1
            GGCCCGGCAGCAGGATGATGCTCTCCCGGGCCAAGCCGGCTGTGGGGAGCACCCCGCCGCAGGGGGACAGGCGGAGGAAGAAAGGGAAGAAGGTGCCACAGATCG
            +
            CCCFFFFDHHD;FF=GGDHGGHIIIGHIIIBDGBFCAHG@E=6?CBDBB;?BB@BD8BB;BDB<>>;@?BB<9>&5<?288AAABDBBBBACBCAC?@AD?CAC?
            @SRR636272.13995011/1
            GCAGGATGATGCTCTCCCGGGCCAAGCCGGCTGTGGGGAGCACCCCGCCGCAGGGGGACAGGCGGAGGAAGAAAGGGAGATCGGAAGAGCACACGTCTGAACTCC
            +
            BBCFDFDEFFHHFIJIHGHGHGIIFIJJJJIGGBFHHIEGBEFEFFCDDDD:@@<BB8BBDDDDDDBBB?AA?CDABDD5?CDDDBB<A<>ACBB8ACDCD@CD>
            @SRR636272.21107783/1
            CGGGGAGCGCGGGCCCGGCAGCAGGATGATGCTCTCCCGGGCCAAGCCGGCTGTAGGGAGCACCCCGCCGCAGGGGGACAGGCGAGATCGGAAGAGCACACGTCT
            +
            BCCFFDFFHHHHHJJJJJIJHHHHFFFFEEEEEEEDDDDDDBDBDBBDBBDBBB(:ABCDDDDDDDDDDDDDDDD@BBBDDDDDDDDDDDDBDDDDDDDDDDADC
            '''.stripIndent().trim()

    def testFastqSplitByRecord() {

        when:
        def records = new FastqSplitter().target(FASTQ2).options(record: true).list()
        then:
        records.size() == 3
        records[0].readHeader == 'SRR636272.19519409/1'
        records[0].readString == 'GGCCCGGCAGCAGGATGATGCTCTCCCGGGCCAAGCCGGCTGTGGGGAGCACCCCGCCGCAGGGGGACAGGCGGAGGAAGAAAGGGAAGAAGGTGCCACAGATCG'
        records[0].qualityHeader == ''
        records[0].qualityString == 'CCCFFFFDHHD;FF=GGDHGGHIIIGHIIIBDGBFCAHG@E=6?CBDBB;?BB@BD8BB;BDB<>>;@?BB<9>&5<?288AAABDBBBBACBCAC?@AD?CAC?'

        records[1].readHeader == 'SRR636272.13995011/1'
        records[1].readString == 'GCAGGATGATGCTCTCCCGGGCCAAGCCGGCTGTGGGGAGCACCCCGCCGCAGGGGGACAGGCGGAGGAAGAAAGGGAGATCGGAAGAGCACACGTCTGAACTCC'
        records[1].qualityHeader == ''
        records[1].qualityString == 'BBCFDFDEFFHHFIJIHGHGHGIIFIJJJJIGGBFHHIEGBEFEFFCDDDD:@@<BB8BBDDDDDDBBB?AA?CDABDD5?CDDDBB<A<>ACBB8ACDCD@CD>'

        records[2].readHeader == 'SRR636272.21107783/1'
        records[2].readString == 'CGGGGAGCGCGGGCCCGGCAGCAGGATGATGCTCTCCCGGGCCAAGCCGGCTGTAGGGAGCACCCCGCCGCAGGGGGACAGGCGAGATCGGAAGAGCACACGTCT'
        records[2].qualityHeader == ''
        records[2].qualityString == 'BCCFFDFFHHHHHJJJJJIJHHHHFFFFEEEEEEEDDDDDDBDBDBBDBBDBBB(:ABCDDDDDDDDDDDDDDDD@BBBDDDDDDDDDDDDBDDDDDDDDDDADC'

        when:
        records = new FastqSplitter().target(FASTQ2).options(record: [readHeader:true, readString:true, qualityString:true, qualityHeader: true]).list()
        then:
        records.size() == 3
        records[0].readHeader == 'SRR636272.19519409/1'
        records[0].readString == 'GGCCCGGCAGCAGGATGATGCTCTCCCGGGCCAAGCCGGCTGTGGGGAGCACCCCGCCGCAGGGGGACAGGCGGAGGAAGAAAGGGAAGAAGGTGCCACAGATCG'
        records[0].qualityHeader == ''
        records[0].qualityString == 'CCCFFFFDHHD;FF=GGDHGGHIIIGHIIIBDGBFCAHG@E=6?CBDBB;?BB@BD8BB;BDB<>>;@?BB<9>&5<?288AAABDBBBBACBCAC?@AD?CAC?'

        records[1].readHeader == 'SRR636272.13995011/1'
        records[1].readString == 'GCAGGATGATGCTCTCCCGGGCCAAGCCGGCTGTGGGGAGCACCCCGCCGCAGGGGGACAGGCGGAGGAAGAAAGGGAGATCGGAAGAGCACACGTCTGAACTCC'
        records[1].qualityHeader == ''
        records[1].qualityString == 'BBCFDFDEFFHHFIJIHGHGHGIIFIJJJJIGGBFHHIEGBEFEFFCDDDD:@@<BB8BBDDDDDDBBB?AA?CDABDD5?CDDDBB<A<>ACBB8ACDCD@CD>'

        records[2].readHeader == 'SRR636272.21107783/1'
        records[2].readString == 'CGGGGAGCGCGGGCCCGGCAGCAGGATGATGCTCTCCCGGGCCAAGCCGGCTGTAGGGAGCACCCCGCCGCAGGGGGACAGGCGAGATCGGAAGAGCACACGTCT'
        records[2].qualityHeader == ''
        records[2].qualityString == 'BCCFFDFFHHHHHJJJJJIJHHHHFFFFEEEEEEEDDDDDDBDBDBBDBBDBBB(:ABCDDDDDDDDDDDDDDDD@BBBDDDDDDDDDDDDBDDDDDDDDDDADC'

    }


    static final FASTQ3 = '''
        @SRR636272.19519409/1
        GGCCCGGCAGCAGGATGATGCTCTCCCGGGCCAAGCCGGCTGTGGGGAGCACCCCGCCGCAGGGGGACAGGCGGAGGAAGAAAGGGAAGAAGGTGCCACAGATCG
        +
        CCCFFFFDHHD;FF=GGDHGGHIIIGHIIIBDGBFCAHG@E=6?CBDBB;?BB@BD8BB;BDB<>>;@?BB<9>&5<?288AAABDBBBBACBCAC?@AD?CAC?
        @SRR636272.13995011/1
        GCAGGATGATGCTCTCCCGGGCCAAGCCGGCTGTGGGGAGCACCCCGCCGCAGGGGGACAGGCGGAGGAAGAAAGGGAGATCGGAAGAGCACACGTCTGAACTCC
        +
        BBCFDFDEFFHHFIJIHGHGHGIIFIJJJJIGGBFHHIEGBEFEFFCDDDD:@@<BB8BBDDDDDDBBB?AA?CDABDD5?CDDDBB<A<>ACBB8ACDCD@CD>
        @SRR636272.21107783/1
        CGGGGAGCGCGGGCCCGGCAGCAGGATGATGCTCTCCCGGGCCAAGCCGGCTGTAGGGAGCACCCCGCCGCAGGGGGACAGGCGAGATCGGAAGAGCACACGTCT
        +
        BCCFFDFFHHHHHJJJJJIJHHHHFFFFEEEEEEEDDDDDDBDBDBBDBBDBBB(:ABCDDDDDDDDDDDDDDDD@BBBDDDDDDDDDDDDBDDDDDDDDDDADC
        @SRR636272.23331539/1
        GGAGCACCCCGCCGCAGGGGGACAGGCGGAGGAAGAAAGGGAAGAAGGTGCCACAGCTGGAGGAGCTGCTGGCCGGGAGGGACTTCACCGGCGAGATCGGAAGAG
        +
        CCCFFFFFHHHHHJJJJJJJJJJJJJJJHFDDBDDBDDDDDDDDDDDDADDDDDDDDDDDDDDDDDDDDDDDDDDBDBDDD9@DDDDDDDDDDDDBBDDDBDD@@
        @SRR636272.7306321/1
        CGGCCAGCAGCTCCTCCAGCTGTGGCACCTTCTTCCCTTTCTTCCTAGTGCACTCTGGCCGGGCCTCCCCCCGCAGCCCTCGCTCCTCTCCCTAGATCGGAAGAG
        +
        CCCFFFFFHHHHHJJJJIJJJJIJJJIJJJJJJJJJJIJJIJJJJIJIIGIJJJJJGIJJJJIHFFFDDDDDDDDDDDDDDBDDDDDDDDDDCCDDDDDDD<BDB
        @SRR636272.23665592/1
        GGAAGAAGGTGCCACAGCTGGAGGAGCTGCTGGCCGGGAGGGACTTCACCGGCGCCATCGCCTTGCTGGAGTTTCAGCGGCACGCGGGTGAGCAAGATCGGAAGA
        +
        BC@DFFFFHDFHHJJJJJJJJJJIIIJJJJIIGGHIJJGJJJFHIJJIHHHFFDDDDDDDDDDDDDDDDDDCCCDCCDDDDDBDDDDD<BDDDDDDDDDDDDDD?
        @SRR636272.1267179/1
        CGCGGCAACGGCGCCATCGGCGGCGCGGGGAGCGCGGGCCCGGCAGCAGGATGATGCTCTCCCGGGCCAAGCCGGCTGTGGGGAGCACCCCGCCGCAGGGGGGCA
        +
        CCCFFFFFHHHGHJJJJJJJJJHBB?BDDD5:1:6@DDBBD@D68@<<?B8>CCDDCDDDCDDBBD>@?AA?B@D55@<ACDD8@9<?BDDDBB<<9>@BBD###
        '''
            .stripIndent().trim()

    def testFastqSplitBy3() {

        when:
        def items = new FastqSplitter().target(FASTQ3).options(by:3).list()

        then:
        items.get(0) == '''
            @SRR636272.19519409/1
            GGCCCGGCAGCAGGATGATGCTCTCCCGGGCCAAGCCGGCTGTGGGGAGCACCCCGCCGCAGGGGGACAGGCGGAGGAAGAAAGGGAAGAAGGTGCCACAGATCG
            +
            CCCFFFFDHHD;FF=GGDHGGHIIIGHIIIBDGBFCAHG@E=6?CBDBB;?BB@BD8BB;BDB<>>;@?BB<9>&5<?288AAABDBBBBACBCAC?@AD?CAC?
            @SRR636272.13995011/1
            GCAGGATGATGCTCTCCCGGGCCAAGCCGGCTGTGGGGAGCACCCCGCCGCAGGGGGACAGGCGGAGGAAGAAAGGGAGATCGGAAGAGCACACGTCTGAACTCC
            +
            BBCFDFDEFFHHFIJIHGHGHGIIFIJJJJIGGBFHHIEGBEFEFFCDDDD:@@<BB8BBDDDDDDBBB?AA?CDABDD5?CDDDBB<A<>ACBB8ACDCD@CD>
            @SRR636272.21107783/1
            CGGGGAGCGCGGGCCCGGCAGCAGGATGATGCTCTCCCGGGCCAAGCCGGCTGTAGGGAGCACCCCGCCGCAGGGGGACAGGCGAGATCGGAAGAGCACACGTCT
            +
            BCCFFDFFHHHHHJJJJJIJHHHHFFFFEEEEEEEDDDDDDBDBDBBDBBDBBB(:ABCDDDDDDDDDDDDDDDD@BBBDDDDDDDDDDDDBDDDDDDDDDDADC
            '''
            .stripIndent().leftTrim()

        items.get(1) == '''
            @SRR636272.23331539/1
            GGAGCACCCCGCCGCAGGGGGACAGGCGGAGGAAGAAAGGGAAGAAGGTGCCACAGCTGGAGGAGCTGCTGGCCGGGAGGGACTTCACCGGCGAGATCGGAAGAG
            +
            CCCFFFFFHHHHHJJJJJJJJJJJJJJJHFDDBDDBDDDDDDDDDDDDADDDDDDDDDDDDDDDDDDDDDDDDDDBDBDDD9@DDDDDDDDDDDDBBDDDBDD@@
            @SRR636272.7306321/1
            CGGCCAGCAGCTCCTCCAGCTGTGGCACCTTCTTCCCTTTCTTCCTAGTGCACTCTGGCCGGGCCTCCCCCCGCAGCCCTCGCTCCTCTCCCTAGATCGGAAGAG
            +
            CCCFFFFFHHHHHJJJJIJJJJIJJJIJJJJJJJJJJIJJIJJJJIJIIGIJJJJJGIJJJJIHFFFDDDDDDDDDDDDDDBDDDDDDDDDDCCDDDDDDD<BDB
            @SRR636272.23665592/1
            GGAAGAAGGTGCCACAGCTGGAGGAGCTGCTGGCCGGGAGGGACTTCACCGGCGCCATCGCCTTGCTGGAGTTTCAGCGGCACGCGGGTGAGCAAGATCGGAAGA
            +
            BC@DFFFFHDFHHJJJJJJJJJJIIIJJJJIIGGHIJJGJJJFHIJJIHHHFFDDDDDDDDDDDDDDDDDDCCCDCCDDDDDBDDDDD<BDDDDDDDDDDDDDD?
            '''
                .stripIndent().leftTrim()

        items.get(2) == '''
            @SRR636272.1267179/1
            CGCGGCAACGGCGCCATCGGCGGCGCGGGGAGCGCGGGCCCGGCAGCAGGATGATGCTCTCCCGGGCCAAGCCGGCTGTGGGGAGCACCCCGCCGCAGGGGGGCA
            +
            CCCFFFFFHHHGHJJJJJJJJJHBB?BDDD5:1:6@DDBBD@D68@<<?B8>CCDDCDDDCDDBBD>@?AA?B@D55@<ACDD8@9<?BDDDBB<<9>@BBD###
            '''
                .stripIndent().leftTrim()

    }

    def testFastqSplitBy3ToRecord() {

        when:
        def items = new FastqSplitter().target(FASTQ3).options(by:3, record:[readHeader:true]).list()
        then:
        items[0] == [ [readHeader: 'SRR636272.19519409/1'], [readHeader: 'SRR636272.13995011/1'], [readHeader: 'SRR636272.21107783/1']]
        items[1] == [ [readHeader: 'SRR636272.23331539/1'], [readHeader: 'SRR636272.7306321/1'], [readHeader: 'SRR636272.23665592/1']]
        items[2] == [ [readHeader: 'SRR636272.1267179/1'] ]

    }

    def testFastqSplitBy3ToFile() {
        given:
        def folder = TestHelper.createInMemTempDir()

        when:
        def items = new FastqSplitter().target(FASTQ3).options(by:3, file: folder).list()
        then:
        items.get(0).text == '''
            @SRR636272.19519409/1
            GGCCCGGCAGCAGGATGATGCTCTCCCGGGCCAAGCCGGCTGTGGGGAGCACCCCGCCGCAGGGGGACAGGCGGAGGAAGAAAGGGAAGAAGGTGCCACAGATCG
            +
            CCCFFFFDHHD;FF=GGDHGGHIIIGHIIIBDGBFCAHG@E=6?CBDBB;?BB@BD8BB;BDB<>>;@?BB<9>&5<?288AAABDBBBBACBCAC?@AD?CAC?
            @SRR636272.13995011/1
            GCAGGATGATGCTCTCCCGGGCCAAGCCGGCTGTGGGGAGCACCCCGCCGCAGGGGGACAGGCGGAGGAAGAAAGGGAGATCGGAAGAGCACACGTCTGAACTCC
            +
            BBCFDFDEFFHHFIJIHGHGHGIIFIJJJJIGGBFHHIEGBEFEFFCDDDD:@@<BB8BBDDDDDDBBB?AA?CDABDD5?CDDDBB<A<>ACBB8ACDCD@CD>
            @SRR636272.21107783/1
            CGGGGAGCGCGGGCCCGGCAGCAGGATGATGCTCTCCCGGGCCAAGCCGGCTGTAGGGAGCACCCCGCCGCAGGGGGACAGGCGAGATCGGAAGAGCACACGTCT
            +
            BCCFFDFFHHHHHJJJJJIJHHHHFFFFEEEEEEEDDDDDDBDBDBBDBBDBBB(:ABCDDDDDDDDDDDDDDDD@BBBDDDDDDDDDDDDBDDDDDDDDDDADC
            '''
                .stripIndent().leftTrim()

        items.get(1).text == '''
            @SRR636272.23331539/1
            GGAGCACCCCGCCGCAGGGGGACAGGCGGAGGAAGAAAGGGAAGAAGGTGCCACAGCTGGAGGAGCTGCTGGCCGGGAGGGACTTCACCGGCGAGATCGGAAGAG
            +
            CCCFFFFFHHHHHJJJJJJJJJJJJJJJHFDDBDDBDDDDDDDDDDDDADDDDDDDDDDDDDDDDDDDDDDDDDDBDBDDD9@DDDDDDDDDDDDBBDDDBDD@@
            @SRR636272.7306321/1
            CGGCCAGCAGCTCCTCCAGCTGTGGCACCTTCTTCCCTTTCTTCCTAGTGCACTCTGGCCGGGCCTCCCCCCGCAGCCCTCGCTCCTCTCCCTAGATCGGAAGAG
            +
            CCCFFFFFHHHHHJJJJIJJJJIJJJIJJJJJJJJJJIJJIJJJJIJIIGIJJJJJGIJJJJIHFFFDDDDDDDDDDDDDDBDDDDDDDDDDCCDDDDDDD<BDB
            @SRR636272.23665592/1
            GGAAGAAGGTGCCACAGCTGGAGGAGCTGCTGGCCGGGAGGGACTTCACCGGCGCCATCGCCTTGCTGGAGTTTCAGCGGCACGCGGGTGAGCAAGATCGGAAGA
            +
            BC@DFFFFHDFHHJJJJJJJJJJIIIJJJJIIGGHIJJGJJJFHIJJIHHHFFDDDDDDDDDDDDDDDDDDCCCDCCDDDDDBDDDDD<BDDDDDDDDDDDDDD?
            '''
                .stripIndent().leftTrim()

        items.get(2).text == '''
            @SRR636272.1267179/1
            CGCGGCAACGGCGCCATCGGCGGCGCGGGGAGCGCGGGCCCGGCAGCAGGATGATGCTCTCCCGGGCCAAGCCGGCTGTGGGGAGCACCCCGCCGCAGGGGGGCA
            +
            CCCFFFFFHHHGHJJJJJJJJJHBB?BDDD5:1:6@DDBBD@D68@<<?B8>CCDDCDDDCDDBBD>@?AA?B@D55@<ACDD8@9<?BDDDBB<<9>@BBD###
            '''
                .stripIndent().leftTrim()

    }

    def testFastqSplitWithLimit() {

        when:
        def items = new FastqSplitter().options(limit:3, record:[readHeader:true]).target(FASTQ3).list()

        then:
        items.size() == 3
        items[0] == [readHeader: 'SRR636272.19519409/1']
        items[1] == [readHeader: 'SRR636272.13995011/1']
        items[2] == [readHeader: 'SRR636272.21107783/1']

    }


    static fastq33 = '''
            @SRR636272.19519409/1
            GGCCCGGCAGCAGGATGATGCTCTCCCGGGCCAAGCCGGCTGTGGGGAGCACCCCGCCGCAGGGGGACAGGCGGAGGAAGAAAGGGAAGAAGGTGCCACAGATCG
            +
            CCCFFFFDHHD;FF=GGDHGGHIIIGHIIIBDGBFCAHG@E=6?CBDBB;?BB@BD8BB;BDB<>>;@?BB<9>&5<?288AAABDBBBBACBCAC?@AD?CAC?
            @SRR636272.13995011/1
            GCAGGATGATGCTCTCCCGGGCCAAGCCGGCTGTGGGGAGCACCCCGCCGCAGGGGGACAGGCGGAGGAAGAAAGGGAGATCGGAAGAGCACACGTCTGAACTCC
            +
            BBCFDFDEFFHHFIJIHGHGHGIIFIJJJJIGGBFHHIEGBEFEFFCDDDD:@@<BB8BBDDDDDDBBB?AA?CDABDD5?CDDDBB<A<>ACBB8ACDCD@CD>
            @SRR636272.21107783/1
            CGGGGAGCGCGGGCCCGGCAGCAGGATGATGCTCTCCCGGGCCAAGCCGGCTGTAGGGAGCACCCCGCCGCAGGGGGACAGGCGAGATCGGAAGAGCACACGTCT
            +
            BCCFFDFFHHHHHJJJJJIJHHHHFFFFEEEEEEEDDDDDDBDBDBBDBBDBBB(:ABCDDDDDDDDDDDDDDDD@BBBDDDDDDDDDDDDBDDDDDDDDDDADC
            '''.stripIndent().trim()

    static fastq64 = '''
                    @V00-HWI-EAS132_5_30HYMAAXX:4:1:707:1948
                    AAGCACTCTTACGGCTAGTTAAAGCAGCGATCTTTG
                    +V00-HWI-EAS132_5_30HYMAAXX:4:1:707:1948
                    ]]]]]]]]]]]]]]]b`]]b`]]b`]]`]CZYEZYS
                    @V00-HWI-EAS132_5_30HYMAAXX:4:1:685:1955
                    ACTGGAATGCCCAACATAGAGGCTTGGGCAACAACT
                    +V00-HWI-EAS132_5_30HYMAAXX:4:1:685:1955
                    ]]]]]]]]]]]]]]]]]]]]]]]]]]]]H]XNYXNU
                    @V00-HWI-EAS132_5_30HYMAAXX:4:1:716:1946
                    AGACTGTGTACGTATCATTGCTAACTTCAATCCAAG
                    +V00-HWI-EAS132_5_30HYMAAXX:4:1:716:1946
                    ][]][]][]]ZYV]]]]]]]]]]]]J[W]VVZCZYW
                    @V00-HWI-EAS132_5_30HYMAAXX:4:1:714:1935
                    AAGATACTATAGCTTCCGCTAGTAAATGTCGGTAAA
                    +V00-HWI-EAS132_5_30HYMAAXX:4:1:714:1935
                    ]]]]]]]]]]][]]]]][]]]][]]]]]][YZYZZW
                    @V00-HWI-EAS132_5_30HYMAAXX:4:1:685:1945
                    ATAAGGTGGTCGCAAACTTCGAGGATCAAGGGAAAT
                    +V00-HWI-EAS132_5_30HYMAAXX:4:1:685:1945
                    ]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]Y[[[[V
                    @V00-HWI-EAS132_5_30HYMAAXX:4:1:686:1949
                    ACAAAACAAAAAAAAAGAATTATGGACTATGCCACA
                    +V00-HWI-EAS132_5_30HYMAAXX:4:1:686:1949
                    ][]]][[]]]]]][]]]]]]]]]]]]]]]]YYTZWW
                    @V00-HWI-EAS132_5_30HYMAAXX:4:1:725:1945
                    AAGGAATATTCCCTTCTAACTATGTGCGTTTGTTGG
                    +V00-HWI-EAS132_5_30HYMAAXX:4:1:725:1945
                    ]]]]]]]Z]]Q]Z]]]]Z]W]]]]]ZPZ]]ZZYVZV
                    @V00-HWI-EAS132_5_30HYMAAXX:4:1:370:1109
                    AATGGATTAGACACTAGCTAATATACGCTTTTTCAT
                    +V00-HWI-EAS132_5_30HYMAAXX:4:1:370:1109
                    ]]]]]]]ZQ]]]Z]ZYZMYY]]]]U]S]]]ZYZJHV
                    @V00-HWI-EAS132_5_30HYMAAXX:4:1:700:1931
                    ATCCAATTTATCAATCTAGTACCCTTCAGGAATAGA
                    +V00-HWI-EAS132_5_30HYMAAXX:4:1:700:1931
                    ]]]W]]][][[]]][]]OZ][]]]W]][J[ZWYZYC
                    @V00-HWI-EAS132_5_30HYMAAXX:4:1:746:1938
                    AAGGTCTTCAAGCGTGCCAAGAAGAATAGTAATAAG
                    +V00-HWI-EAS132_5_30HYMAAXX:4:1:746:1938
                    ]]]]]]]][]]][]X[[L]X]]][TX[]]HZCXXZV
                   '''
            .stripIndent().trim()



    def testQualityCheckWithString () {

        FastqSplitter splitter

        when:
        splitter = new FastqSplitter().target(fastq33)
        then:
        splitter.qualityScore() == 33

        when:
        splitter = new FastqSplitter().target(fastq64)
        then:
        splitter.qualityScore() == 64

    }


    def testQualityCheckWithPath () {

        given:
        def file = TestHelper.createInMemTempFile()
        file.text = fastq64

        when:
        FastqSplitter splitter = new FastqSplitter().target(file)
        then:
        splitter.qualityScore() == 64

    }


    def testDetectFastqQuality() {
        given:
        def quality33 = "CCCFFFFDHHD;FF=GGDHGGHIIIGHIIIBDGBFCAHG@E=6?CBDBB;?BB@BD8BB;BDB<>>;@?BB<9>&5<?288AAABDBBBBACBCAC?@AD?CAC?"
        def quality64 = "efcfffffcfeefffcffffffddf`feed]`]_Ba_^__[YBBBBBBBBBBRTT\\]][]dddd`ddd^dddadd^BBBBBBBBBBBBBBBBBBBBBBBB"
        def qualityUnknown = ";<=>?@ABCDEFGHI"

        expect:
        FastqSplitter.detectQualityString(quality33) == 33
        FastqSplitter.detectQualityString(quality64) == 64
        FastqSplitter.detectQualityString('hello') == 64       // <- ask to emilio
        FastqSplitter.detectQualityString(qualityUnknown) == -1
        FastqSplitter.detectQualityString(null) == -1

    }

}
