/**
 * SoloJanControllerTest.java
 */

package wiz.project.janbot.game;

import static org.junit.Assert.*;

import java.security.SecureRandom;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.junit.Test;

import wiz.project.jan.JanPai;
import wiz.project.jan.Wind;
import wiz.project.jan.util.JanPaiUtil;
import wiz.project.janbot.game.exception.InvalidInputException;
import wiz.project.janbot.game.exception.JanException;



/**
 * SoloJanControllerのテスト
 */
public final class SoloJanControllerTest {
    
    /**
     * call() のテスト
     */
    @Test
    public void testCall() throws JanException {
        {
            // エラー (ゲームが未開始)
            final String playerName = TEST_PLAYER_NAME;
            final CallType type = CallType.PON;
            final JanPai target = null;
            try {
                final JanController controller = createJanController();
                controller.call(playerName, type, target);
                fail();
            }
            catch (final JanException e) {
                assertEquals("Game is not started.", e.getMessage());
            }
        }
        {
            // エラー (不正なプレイヤー名)
            final Wind playerWind = Wind.TON;
            final Map<Wind, Player> playerTable = createPlayerTable(TEST_PLAYER_NAME, playerWind);
            
            final JanController controller = createJanController();
            controller.start(createDeck(), playerTable);
            
            final String playerName = "不正なプレイヤー名";
            final CallType type = CallType.PON;
            final JanPai target = null;
            try {
                controller.call(playerName, type, target);
                fail();
            }
            catch (final IllegalArgumentException e) {
                assertEquals("Inavlid player name - " + playerName, e.getMessage());
            }
        }
        {
            // エラー (プレイヤー名が空文字列)
            final Wind playerWind = Wind.TON;
            final Map<Wind, Player> playerTable = createPlayerTable(TEST_PLAYER_NAME, playerWind);
            
            final JanController controller = createJanController();
            controller.start(createDeck(), playerTable);
            
            final String playerName = "";
            final CallType type = CallType.PON;
            final JanPai target = null;
            try {
                controller.call(playerName, type, target);
                fail();
            }
            catch (final IllegalArgumentException e) {
                assertEquals("Player name is empty.", e.getMessage());
            }
        }
        {
            // エラー (プレイヤー名がNull)
            final Wind playerWind = Wind.TON;
            final Map<Wind, Player> playerTable = createPlayerTable(TEST_PLAYER_NAME, playerWind);
            
            final JanController controller = createJanController();
            controller.start(createDeck(), playerTable);
            
            final String playerName = null;
            final CallType type = CallType.PON;
            final JanPai target = null;
            try {
                controller.call(playerName, type, target);
                fail();
            }
            catch (final NullPointerException e) {
                assertEquals("Player name is null.", e.getMessage());
            }
        }
        {
            // エラー (副露タイプがNull)
            final String playerName = TEST_PLAYER_NAME;
            final Wind playerWind = Wind.TON;
            final Map<Wind, Player> playerTable = createPlayerTable(TEST_PLAYER_NAME, playerWind);
            
            final JanController controller = createJanController();
            controller.start(createDeck(), playerTable);
            
            final CallType type = null;
            final JanPai target = null;
            try {
                controller.call(playerName, type, target);
                fail();
            }
            catch (final NullPointerException e) {
                assertEquals("Player name is null.", e.getMessage());
            }
        }
    }
    
    /**
     * completeRon() のテスト
     */
    @Test
    public void testCompleteRon() throws JanException {
        {
            // エラー (不正なプレイヤー名)
            final Wind playerWind = Wind.TON;
            final Map<Wind, Player> playerTable = createPlayerTable(TEST_PLAYER_NAME, playerWind);
            
            final JanController controller = createJanController();
            controller.start(createDeck(), playerTable);
            
            final String playerName = "不正なプレイヤー名";
            try {
                controller.completeRon(playerName);
                fail();
            }
            catch (final IllegalArgumentException e) {
                assertEquals("Inavlid player name - " + playerName, e.getMessage());
            }
        }
        {
            // エラー (ゲームが未開始)
            final String playerName = TEST_PLAYER_NAME;
            try {
                final JanController controller = createJanController();
                controller.completeRon(playerName);
                fail();
            }
            catch (final JanException e) {
                assertEquals("Game is not started.", e.getMessage());
            }
        }
        {
            // エラー (プレイヤー名が空文字列)
            final Wind playerWind = Wind.TON;
            final Map<Wind, Player> playerTable = createPlayerTable(TEST_PLAYER_NAME, playerWind);
            
            final JanController controller = createJanController();
            controller.start(createDeck(), playerTable);
            
            final String playerName = "";
            try {
                controller.completeRon(playerName);
                fail();
            }
            catch (final IllegalArgumentException e) {
                assertEquals("Player name is empty.", e.getMessage());
            }
        }
        {
            // エラー (プレイヤー名がNull)
            final Wind playerWind = Wind.TON;
            final Map<Wind, Player> playerTable = createPlayerTable(TEST_PLAYER_NAME, playerWind);
            
            final JanController controller = createJanController();
            controller.start(createDeck(), playerTable);
            
            final String playerName = null;
            try {
                controller.completeRon(playerName);
                fail();
            }
            catch (final NullPointerException e) {
                assertEquals("Player name is null.", e.getMessage());
            }
        }
    }
    
    /**
     * completeTsumo() のテスト
     */
    @Test
    public void testCompleteTsumo() {
        {
            // エラー (ゲームが未開始)
            try {
                final JanController controller = createJanController();
                controller.completeTsumo();
                fail();
            }
            catch (final JanException e) {
                assertEquals("Game is not started.", e.getMessage());
            }
        }
    }
    
    /**
     * discard() のテスト
     */
    @Test
    public void testDiscard() throws JanException {
        {
            // 正常 (ツモ切り)
            final Wind playerWind = Wind.TON;
            final Map<Wind, Player> playerTable = createPlayerTable(TEST_PLAYER_NAME, playerWind);
            
            final JanController controller = createJanController();
            controller.start(createDeck(), playerTable);
            final JanInfo sourceInfo = controller.getGameInfo();
            final Map<JanPai, Integer> sourceHand = sourceInfo.getHand(playerWind).getMenZenMap();
            
            controller.discard();
            final JanInfo resultInfo = controller.getGameInfo();
            final Map<JanPai, Integer> resultHand = resultInfo.getHand(playerWind).getMenZenMap();
            assertEquals(sourceHand, resultHand);
        }
        {
            // 正常 (手出し)
            final Wind playerWind = Wind.TON;
            final Map<Wind, Player> playerTable = createPlayerTable(TEST_PLAYER_NAME, playerWind);
            
            final JanController controller = createJanController();
            controller.start(createDeck(), playerTable);
            final JanInfo sourceInfo = controller.getGameInfo();
            final Map<JanPai, Integer> sourceHand = sourceInfo.getHand(playerWind).getMenZenMap();
            final JanPai activeTsumo = sourceInfo.getActiveTsumo();
            final JanPai discardTarget = sourceInfo.getHand(playerWind).getMenZenList().get(0);
            
            controller.discard(discardTarget);
            final JanInfo resultInfo = controller.getGameInfo();
            final Map<JanPai, Integer> resultHand = resultInfo.getHand(playerWind).getMenZenMap();
            assertEquals(sourceHand.get(activeTsumo) + 1, (int)resultHand.get(activeTsumo));
            assertEquals(sourceHand.get(discardTarget) - 1, (int)resultHand.get(discardTarget));
        }
        {
            // 正常 (ツモ牌を指定)
            final Wind playerWind = Wind.TON;
            final Map<Wind, Player> playerTable = createPlayerTable(TEST_PLAYER_NAME, playerWind);
            
            final JanController controller = createJanController();
            controller.start(createDeck(), playerTable);
            final JanInfo sourceInfo = controller.getGameInfo();
            final Map<JanPai, Integer> sourceHand = sourceInfo.getHand(playerWind).getMenZenMap();
            final JanPai activeTsumo = sourceInfo.getActiveTsumo();
            
            controller.discard(activeTsumo);
            final JanInfo resultInfo = controller.getGameInfo();
            final Map<JanPai, Integer> resultHand = resultInfo.getHand(playerWind).getMenZenMap();
            assertEquals(sourceHand, resultHand);
        }
        {
            // エラー (手牌に存在しない牌を指定)
            final Wind playerWind = Wind.TON;
            final Map<Wind, Player> playerTable = createPlayerTable(TEST_PLAYER_NAME, playerWind);
            
            try {
                final JanController controller = createJanController();
                controller.start(createDeck(), playerTable);
                final JanInfo sourceInfo = controller.getGameInfo();
                final Map<JanPai, Integer> sourceHand = sourceInfo.getHand(playerWind).getMenZenMap();
                for (final JanPai discardTarget : JanPai.values()) {
                    if (sourceHand.get(discardTarget) == 0) {
                        controller.discard(discardTarget);
                    }
                }
                fail();
            }
            catch (final InvalidInputException e) {
                assertTrue(e.getMessage().startsWith("Invalid discard target - "));
            }
        }
        {
            // エラー (ゲームが未開始 - ツモ切り)
            try {
                final JanController controller = createJanController();
                controller.discard();
                fail();
            }
            catch (final JanException e) {
                assertEquals("Game is not started.", e.getMessage());
            }
        }
        {
            // エラー (ゲームが未開始 - 手出し)
            try {
                final JanController controller = createJanController();
                controller.discard(JanPai.TON);
                fail();
            }
            catch (final JanException e) {
                assertEquals("Game is not started.", e.getMessage());
            }
        }
        {
            // エラー (指定牌がNull)
            final Wind playerWind = Wind.TON;
            final Map<Wind, Player> playerTable = createPlayerTable(TEST_PLAYER_NAME, playerWind);
            
            try {
                final JanController controller = createJanController();
                controller.start(createDeck(), playerTable);
                controller.discard(null);
                fail();
            }
            catch (final NullPointerException e) {
                assertEquals("Discard target is null.", e.getMessage());
            }
        }
    }
    
    /**
     * getGameInfo() のテスト
     */
    @Test
    public void testGetGameInfo() {
        {
            // 正常 (デフォルト値)
            final JanController controller = createJanController();
            final JanInfo info = controller.getGameInfo();
            assertTrue(info.getDeck().isEmpty());
        }
    }
    
    /**
     * next() のテスト
     */
    @Test
    public void testNext() throws JanException {
        {
            // 正常
            final Wind playerWind = Wind.TON;
            final Map<Wind, Player> playerTable = createPlayerTable(TEST_PLAYER_NAME, playerWind);
            
            final JanController controller = createJanController();
            controller.start(createDeck(), playerTable);
            final JanInfo sourceInfo = controller.getGameInfo();
            
            controller.next();
            final JanInfo resultInfo = controller.getGameInfo();
            assertTrue(sourceInfo.getRemainCount() > resultInfo.getRemainCount());
        }
        {
            // エラー (ゲームが未開始)
            try {
                final JanController controller = createJanController();
                controller.next();
                fail();
            }
            catch (final JanException e) {
                assertEquals("Game is not started.", e.getMessage());
            }
        }
    }
    
    /**
     * start() のテスト
     */
    @Test
    public void testStart() throws JanException {
        {
            // 正常
            final String playerName = TEST_PLAYER_NAME;
            final Wind playerWind = Wind.TON;
            final Map<Wind, Player> playerTable = createPlayerTable(playerName, playerWind);
            final List<JanPai> deck = createDeck();
            
            final JanController controller = createJanController();
            controller.start(deck, playerTable);
            
            final JanInfo info = controller.getGameInfo();
            assertEquals(deck, info.getDeck());
            assertEquals(playerTable, info.getPlayerTable());
        }
        {
            // エラー (開始済み)
            final String playerName = TEST_PLAYER_NAME;
            final Wind playerWind = Wind.TON;
            final Map<Wind, Player> playerTable = createPlayerTable(playerName, playerWind);
            final List<JanPai> deck = createDeck();
            
            final JanController controller = createJanController();
            controller.start(deck, playerTable);
            
            try {
                controller.start(deck, playerTable);
                fail();
            }
            catch (final JanException e) {
                assertEquals("Game is already started.", e.getMessage());
            }
        }
        {
            // エラー (牌山のサイズが不正)
            final String playerName = TEST_PLAYER_NAME;
            final Wind playerWind = Wind.TON;
            final Map<Wind, Player> playerTable = createPlayerTable(playerName, playerWind);
            final List<JanPai> deck = createDeck();
            deck.add(JanPai.TON);
            
            try {
                final JanController controller = createJanController();
                controller.start(deck, playerTable);
                fail();
            }
            catch (final IllegalArgumentException e) {
                assertEquals("Invalid deck size - " + deck.size(), e.getMessage());
            }
        }
        {
            // エラー (プレイヤーテーブルのサイズが不正)
            final String playerName = TEST_PLAYER_NAME;
            final Wind playerWind = Wind.TON;
            final Map<Wind, Player> playerTable = createPlayerTable(playerName, playerWind);
            final List<JanPai> deck = createDeck();
            playerTable.remove(Wind.PEI);
            
            try {
                final JanController controller = createJanController();
                controller.start(deck, playerTable);
                fail();
            }
            catch (final IllegalArgumentException e) {
                assertEquals("Invalid player table size - " + playerTable.size(), e.getMessage());
            }
        }
        {
            // エラー (牌山がNull)
            final String playerName = TEST_PLAYER_NAME;
            final Wind playerWind = Wind.TON;
            final Map<Wind, Player> playerTable = createPlayerTable(playerName, playerWind);
            final List<JanPai> deck = null;
            
            try {
                final JanController controller = createJanController();
                controller.start(deck, playerTable);
                fail();
            }
            catch (final NullPointerException e) {
                assertEquals("Deck is null.", e.getMessage());
            }
        }
        {
            // エラー (プレイヤーテーブルがNull)
            final Map<Wind, Player> playerTable = null;
            final List<JanPai> deck = createDeck();
            
            try {
                final JanController controller = createJanController();
                controller.start(deck, playerTable);
                fail();
            }
            catch (final NullPointerException e) {
                assertEquals("Player table is null.", e.getMessage());
            }
        }
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
     * 麻雀コントローラを生成
     * 
     * @return 麻雀コントローラ。
     */
    private SoloJanController createJanController() {
        return new SoloJanController(new GameAnnouncer() {
            @Override
            protected String convertJanPaiToString(final JanPai pai) {
                return pai.toString();
            }
        });
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

