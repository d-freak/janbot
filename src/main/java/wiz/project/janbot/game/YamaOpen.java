/**
 * YamaOpen.java
 * 
 * @author Yuki
 */

package wiz.project.janbot.game;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import wiz.io.serializer.Serializer;
import wiz.project.jan.JanPai;



/**
 * 山開け
 */
public final class YamaOpen {
    
    /**
     * エントリポイント
     */
    @SuppressWarnings("unchecked")
    public static void main(final String[] paramList) throws NoSuchFileException, IOException {
        if (paramList.length > 0) {
            final List<JanPai> deck = (List<JanPai>)Serializer.read(paramList[0]);
            
            final int deckSize = deck.size();
            
            // 配牌
            final List<JanPai> ton = new ArrayList<JanPai>(deck.subList( 0, 13));
            final List<JanPai> nan = new ArrayList<JanPai>(deck.subList(13, 26));
            final List<JanPai> sha = new ArrayList<JanPai>(deck.subList(26, 39));
            final List<JanPai> pei = new ArrayList<JanPai>(deck.subList(39, 52));
            final List<JanPai> tsumo;
            if (paramList.length == 2 && paramList[1].equals("chm")) {
                tsumo = new ArrayList<JanPai>(deck.subList(52, deckSize));
            }
            else {
                tsumo = new ArrayList<JanPai>(deck.subList(52, deckSize - 14));
            }
            final List<JanPai> tonTsumo = new ArrayList<>();
            final List<JanPai> nanTsumo = new ArrayList<>();
            final List<JanPai> shaTsumo = new ArrayList<>();
            final List<JanPai> peiTsumo = new ArrayList<>();
            for (int i = 0; i < tsumo.size(); i++) {
                switch (i % 4) {
                case 0:
                    tonTsumo.add(tsumo.get(i));
                    break;
                case 1:
                    nanTsumo.add(tsumo.get(i));
                    break;
                case 2:
                    shaTsumo.add(tsumo.get(i));
                    break;
                case 3:
                    peiTsumo.add(tsumo.get(i));
                    break;
                }
            }
            
            final List<String> sourceList = new ArrayList<>();
            sourceList.add("東初手：" + convertHandToString(ton));
            sourceList.add("南初手：" + convertHandToString(nan));
            sourceList.add("西初手：" + convertHandToString(sha));
            sourceList.add("北初手：" + convertHandToString(pei));
            if (paramList.length == 2 && paramList[1].equals("chm")) {
            }
            else {
            	final WanPai wanPai = new WanPai(new ArrayList<>(deck.subList(deckSize - 14, deckSize)));
                sourceList.add("嶺上      ：" + wanPai.getWall() + wanPai.getWall() + wanPai.getWall() + wanPai.getWall());
                for (int i = 0; i < 4; i++) {
                    wanPai.openNewDora();
                }
                sourceList.add("ドラ表示  ：" + convertPaiListToString(wanPai.getDoraPrevList()));
                sourceList.add("裏ドラ表示：" + convertPaiListToString(wanPai.getUraDoraPrevList()));
            }
            
            sourceList.add("東ツモ：" + convertPaiListToString(tonTsumo));
            sourceList.add("南ツモ：" + convertPaiListToString(nanTsumo));
            sourceList.add("西ツモ：" + convertPaiListToString(shaTsumo));
            sourceList.add("北ツモ：" + convertPaiListToString(peiTsumo));
            
            try (final Writer buf = Files.newBufferedWriter(Paths.get("./yama-open.log"), Charset.forName("UTF-8"), StandardOpenOption.WRITE, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
                 final PrintWriter writer = new PrintWriter(buf)) {
                for (final String source : sourceList) {
                    if (source != null) {
                        writer.println(source);
                    }
                }
            }
        }
    }
    
    private static String convertHandToString(final List<JanPai> hand) {
        Collections.sort(hand);
        return convertPaiListToString(hand);
    }
    
    private static String convertPaiListToString(final List<JanPai> source) {
        final StringBuilder buf = new StringBuilder();
        for (final JanPai pai : source) {
            buf.append(pai);
        }
        return buf.toString();
    }
    
}

