/**
 * GameAnnouncerTest.java
 * 
 * @author Yuki
 */

package wiz.project.janbot.game;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.junit.Test;

import wiz.project.jan.Hand;
import wiz.project.jan.JanPai;
import wiz.project.jan.Wind;
import wiz.project.jan.util.JanPaiUtil;
import wiz.project.janbot.MockBOT;



/**
 * GameAnnouncerのテスト
 */
public final class GameAnnouncerTest {
    
    /**
     * update() のテスト
     */
    @Test
    public void testUpdate() {
        {
            // 正常 (手牌)
            MockBOT.initialize();
            MockBOT.connect();
            
            final String playerName = TEST_PLAYER_NAME;
            final JanInfo info = createGameInfo(playerName);
            final GameAnnouncer announcer = createAnnouncer();
            announcer.update(info, AnnounceFlag.HAND);
        }
        {
            // 正常 (場情報)
            MockBOT.initialize();
            MockBOT.connect();
            
            final String playerName = TEST_PLAYER_NAME;
            final JanInfo info = createGameInfo(playerName);
            final GameAnnouncer announcer = createAnnouncer();
            announcer.update(info, AnnounceFlag.RIVER_SINGLE);
        }
        {
            // 正常 (捨て牌)
            MockBOT.initialize();
            MockBOT.connect();
            
            final String playerName = TEST_PLAYER_NAME;
            final JanInfo info = createGameInfo(playerName);
            final GameAnnouncer announcer = createAnnouncer();
            announcer.update(info, AnnounceFlag.FIELD);
        }
    }
    
    
    
    /**
     * アナウンサーを生成
     * 
     * @return アナウンサー。
     */
    private GameAnnouncer createAnnouncer() {
        return new GameAnnouncer() {
            @Override
            protected String convertJanPaiToString(final JanPai pai) {
                return pai.toString();
            }
        };
    }
    
    /**
     * 牌山を生成
     * 
     * @return 牌山。
     */
    private List<JanPai> createDeck() {
        final List<JanPai> deck = JanPaiUtil.createAllJanPaiList();
        Collections.shuffle(deck, new SecureRandom());
        return deck;
    }
    
    /**
     * ゲーム情報を生成
     * 
     * @param playerName プレイヤー名。
     * @return ゲーム情報。
     */
    private JanInfo createGameInfo(final String playerName) {
        final JanInfo info = new JanInfo();
        info.setFieldWind(Wind.TON);
        info.setPlayerTable(createPlayerTable(playerName, Wind.SHA));
        
        final List<JanPai> deck = createDeck();
        info.setDeck(deck);
        
        final int deckSize = deck.size();
        info.setWanPai(new WanPai(new ArrayList<>(deck.subList(deckSize - 14, deckSize))));
        
        info.setHand(Wind.TON, new Hand(new ArrayList<JanPai>(deck.subList( 0, 13))));
        info.setHand(Wind.NAN, new Hand(new ArrayList<JanPai>(deck.subList(13, 26))));
        info.setHand(Wind.SHA, new Hand(new ArrayList<JanPai>(deck.subList(26, 39))));
        info.setHand(Wind.PEI, new Hand(new ArrayList<JanPai>(deck.subList(39, 52))));
        info.setDeckIndex(13 * 4);
        info.setRemainCount(70);
        info.setActiveWind(Wind.TON);
        return info;
    }
    
    /**
     * プレイヤーテーブルを生成
     * 
     * @param playerName プレイヤー名。
     * @param playerWind プレイヤーの風。
     * @return プレイヤーテーブル。
     */
    private Map<Wind, Player> createPlayerTable(final String playerName, final Wind playerWind) {
        final Map<Wind, Player> playerTable = new TreeMap<>();
        for (final Wind wind : Wind.values()) {
            if (wind == playerWind) {
                playerTable.put(wind, new Player(playerName, PlayerType.HUMAN));
            }
            else {
                playerTable.put(wind, new Player("COM", PlayerType.COM));
            }
        }
        return playerTable;
    }
    
    
    
    /**
     * プレイヤー名
     */
    private static final String TEST_PLAYER_NAME = "テストプレイヤー";
    
}

