/**
 * GameMasterTest.java
 * 
 * @author Yuki
 */

package wiz.project.janbot.game;

import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import org.junit.Test;

import wiz.project.janbot.MockBOT;
import wiz.project.janbot.game.exception.CallableException;
import wiz.project.janbot.game.exception.JanException;



/**
 * GameMasterのテスト
 */
public final class GameMasterTest {
    
    /**
     * getStatus() のテスト
     */
    @Test
    public void testGetStatus() {
        {
            // 正常 (デフォルト値)
            assertEquals(GameStatus.IDLE, GameMaster.getInstance().getStatus());
        }
    }
    
    /**
     * onCallChi() のテスト
     */
    @Test
    public void testOnCallChi() throws JanException, IOException {
        {
            // 手番でのチー
            MockBOT.initialize();
            MockBOT.connect();
            
            GameMaster.getInstance().onTestChm(TEST_PLAYER_NAME);
            final PrintStream printStream = System.out;
            final ByteArrayOutputStream out = new ByteArrayOutputStream();
            System.setOut(new PrintStream(out));
            // 手牌：[4p][5p][7p][2s][3s][3s][4s][5s][9s][東][發][發][中] [7p]
            GameMaster.getInstance().onCallChi(TEST_PLAYER_NAME, JANPAI_1S);
            
            System.setOut(printStream);
            GameMaster.getInstance().onEnd();
            
            assertTrue(out.toString().equals(""));
        }
        {
            // 副露可能状態でのチー
            MockBOT.initialize();
            MockBOT.connect();
            
            GameMaster.getInstance().onTestChm(TEST_PLAYER_NAME);
            // 手牌：[4p][5p][7p][2s][3s][3s][4s][5s][9s][東][發][發][中] [7p]
            callOnDiscard();
            callOnContinue();
            GameMaster.getInstance().onContinue();
            GameMaster.getInstance().onDiscard();
            GameMaster.getInstance().onDiscard();
            callOnDiscard();
            final PrintStream printStream = System.out;
            final ByteArrayOutputStream out = new ByteArrayOutputStream();
            System.setOut(new PrintStream(out));
            GameMaster.getInstance().onCallChi(TEST_PLAYER_NAME, JANPAI_1S);
            
            System.setOut(printStream);
            GameMaster.getInstance().onEnd();
            
            assertTrue(out.toString().contains("PRIVMSG #test-channel :12[4p]12[5p]12[7p]03[3s]03[4s]03[5s]03[9s]06[東]03[發]03[發]04[中]  03[1s][2s][3s]"));
        }
    }
    
    /**
     * onCallPon() のテスト
     */
    @Test
    public void testOnCallPon() throws JanException, IOException {
        {
            // 手番でのポン
            MockBOT.initialize();
            MockBOT.connect();
            
            GameMaster.getInstance().onTestChm(TEST_PLAYER_NAME);
            final PrintStream printStream = System.out;
            final ByteArrayOutputStream out = new ByteArrayOutputStream();
            System.setOut(new PrintStream(out));
            // 手牌：[4p][5p][7p][2s][3s][3s][4s][5s][9s][東][發][發][中] [7p]
            GameMaster.getInstance().onCallPon(TEST_PLAYER_NAME);
            
            System.setOut(printStream);
            GameMaster.getInstance().onEnd();
            
            assertTrue(out.toString().equals(""));
        }
        {
            // 副露可能状態でのポン
            MockBOT.initialize();
            MockBOT.connect();
            
            GameMaster.getInstance().onTestChm(TEST_PLAYER_NAME);
            // 手牌：[4p][5p][7p][2s][3s][3s][4s][5s][9s][東][發][發][中] [7p]
            callOnDiscard(JANPAI_3S);
            final PrintStream printStream = System.out;
            final ByteArrayOutputStream out = new ByteArrayOutputStream();
            System.setOut(new PrintStream(out));
            GameMaster.getInstance().onCallPon(TEST_PLAYER_NAME);
            
            System.setOut(printStream);
            GameMaster.getInstance().onEnd();
            
            assertTrue(out.toString().contains("PRIVMSG #test-channel :12[4p]12[5p]12[7p]12[7p]03[2s]03[3s]03[4s]03[5s]03[9s]06[東]04[中]  03[發][發][發]"));
        }
    }
    
    /**
     * onCompleteRon() のテスト
     */
    @Test
    public void testOnCompleteRon() throws JanException, IOException {
        {
            // 手番でのロン
            MockBOT.initialize();
            MockBOT.connect();
            
            GameMaster.getInstance().onTestChm(TEST_PLAYER_NAME);
            final PrintStream printStream = System.out;
            final ByteArrayOutputStream out = new ByteArrayOutputStream();
            System.setOut(new PrintStream(out));
            // 手牌：[4p][5p][7p][2s][3s][3s][4s][5s][9s][東][發][發][中] [7p]
            GameMaster.getInstance().onCompleteRon(TEST_PLAYER_NAME);
            
            System.setOut(printStream);
            GameMaster.getInstance().onEnd();
            
            assertTrue(out.toString().equals(""));
        }
        {
            // 副露可能状態でのロン
            MockBOT.initialize();
            MockBOT.connect();
            
            GameMaster.getInstance().onTestChm(TEST_PLAYER_NAME);
            // 手牌：[4p][5p][7p][2s][3s][3s][4s][5s][9s][東][發][發][中] [7p]
            callOnDiscard(JANPAI_CHUN);
            callOnContinue();
            GameMaster.getInstance().onContinue();
            // 手牌：[4p][5p][7p][7p][2s][3s][3s][4s][5s][9s][東][發][發] [7m]
            GameMaster.getInstance().onDiscard(JANPAI_9S);
            // 手牌：[7m][4p][5p][7p][7p][2s][3s][3s][4s][5s][東][發][發] [6m]
            GameMaster.getInstance().onDiscard(JANPAI_TON);
            // 手牌：[6m][7m][4p][5p][7p][7p][2s][3s][3s][4s][5s][發][發] [3m]
            callOnDiscard();
            GameMaster.getInstance().onCallChi(TEST_PLAYER_NAME, JANPAI_1S);
            // 手牌：[6m][7m][4p][5p][7p][7p][3s][4s][5s][發][發]  [1s][2s][3s]
            callOnDiscard(JANPAI_HATU);
            callOnContinue();
            GameMaster.getInstance().onContinue();
            // 手牌：[6m][7m][4p][5p][7p][7p][3s][4s][5s][發] [5m]  [1s][2s][3s]
            callOnDiscard(JANPAI_HATU);
            final PrintStream printStream = System.out;
            final ByteArrayOutputStream out = new ByteArrayOutputStream();
            System.setOut(new PrintStream(out));
            GameMaster.getInstance().onCompleteRon(TEST_PLAYER_NAME);
            
            System.setOut(printStream);
            GameMaster.getInstance().onEnd();
            
            assertTrue(out.toString().contains("PRIVMSG #test-channel :---- ロン和了(7巡目) ----"));
        }
    }
    
    /**
     * onCompleteTsumo() のテスト
     */
    @Test
    public void testOnCompleteTsumo() throws JanException, IOException {
        {
            // 手番でのツモ
            MockBOT.initialize();
            MockBOT.connect();
            
            GameMaster.getInstance().onTestChm(TEST_PLAYER_NAME);
            // 手牌：[4p][5p][7p][2s][3s][3s][4s][5s][9s][東][發][發][中] [7p]
            callOnDiscard(JANPAI_CHUN);
            callOnContinue();
            GameMaster.getInstance().onContinue();
            // 手牌：[4p][5p][7p][7p][2s][3s][3s][4s][5s][9s][東][發][發] [7m]
            GameMaster.getInstance().onDiscard(JANPAI_9S);
            // 手牌：[7m][4p][5p][7p][7p][2s][3s][3s][4s][5s][東][發][發] [6m]
            GameMaster.getInstance().onDiscard(JANPAI_TON);
            // 手牌：[6m][7m][4p][5p][7p][7p][2s][3s][3s][4s][5s][發][發] [3m]
            callOnDiscard();
            GameMaster.getInstance().onCallChi(TEST_PLAYER_NAME, JANPAI_1S);
            // 手牌：[6m][7m][4p][5p][7p][7p][3s][4s][5s][發][發]  [1s][2s][3s]
            callOnDiscard(JANPAI_HATU);
            callOnContinue();
            GameMaster.getInstance().onContinue();
            // 手牌：[6m][7m][4p][5p][7p][7p][3s][4s][5s][發] [5m]  [1s][2s][3s]
            callOnDiscard(JANPAI_HATU);
            callOnContinue();
            GameMaster.getInstance().onContinue();
            GameMaster.getInstance().onDiscard();
            callOnDiscard();
            GameMaster.getInstance().onContinue();
            GameMaster.getInstance().onDiscard();
            GameMaster.getInstance().onDiscard();
            // 手牌：[6m][7m][4p][5p][7p][7p][3s][4s][5s][發] [5m]  [1s][2s][3s]
            callOnDiscard(JANPAI_3S);
            GameMaster.getInstance().onContinue();
            GameMaster.getInstance().onDiscard();
            GameMaster.getInstance().onDiscard();
            final PrintStream printStream = System.out;
            final ByteArrayOutputStream out = new ByteArrayOutputStream();
            System.setOut(new PrintStream(out));
            GameMaster.getInstance().onCompleteTsumo(TEST_PLAYER_NAME);
            
            System.setOut(printStream);
            GameMaster.getInstance().onEnd();
            
            assertTrue(out.toString().contains("PRIVMSG #test-channel :---- ツモ和了(14巡目) ----"));
        }
        {
            // 副露可能状態でのツモ
            MockBOT.initialize();
            MockBOT.connect();
            
            GameMaster.getInstance().onTestChm(TEST_PLAYER_NAME);
            // 手牌：[4p][5p][7p][2s][3s][3s][4s][5s][9s][東][發][發][中] [7p]
            callOnDiscard();
            final PrintStream printStream = System.out;
            final ByteArrayOutputStream out = new ByteArrayOutputStream();
            System.setOut(new PrintStream(out));
            GameMaster.getInstance().onCompleteTsumo(TEST_PLAYER_NAME);
            
            System.setOut(printStream);
            GameMaster.getInstance().onEnd();
            
            assertTrue(out.toString().equals(""));
        }
    }
    
    /**
     * onContinue() のテスト
     */
    @Test
    public void testOnContinue() throws JanException, IOException {
        {
            // 正常
            MockBOT.initialize();
            MockBOT.connect();
            
            GameMaster.getInstance().onTestChm(TEST_PLAYER_NAME);
            // 手牌：[4p][5p][7p][2s][3s][3s][4s][5s][9s][東][發][發][中] [7p]
            callOnDiscard();
            callOnContinue();
            final PrintStream printStream = System.out;
            final ByteArrayOutputStream out = new ByteArrayOutputStream();
            System.setOut(new PrintStream(out));
            GameMaster.getInstance().onContinue();
            
            System.setOut(printStream);
            GameMaster.getInstance().onEnd();
            
            assertTrue(out.toString().contains("PRIVMSG #test-channel :12[4p]12[5p]12[7p]03[2s]03[3s]03[3s]03[4s]03[5s]03[9s]06[東]03[發]03[發]04[中] 04[7m]"));
        }
    }
    
    /**
     * onDiscard() のテスト
     */
    @Test
    public void testOnDiscard() throws JanException, IOException {
        // testOnContinue()が兼ねる
        testOnContinue();
    }
    
    /**
     * onDiscard(String) のテスト
     */
    @Test
    public void testOnDiscardWithString() throws JanException, IOException {
        {
            // 正常
            MockBOT.initialize();
            MockBOT.connect();
            
            GameMaster.getInstance().onTestChm(TEST_PLAYER_NAME);
            // 手牌：[4p][5p][7p][2s][3s][3s][4s][5s][9s][東][發][發][中] [7p]
            callOnDiscard(JANPAI_3S);
            final PrintStream printStream = System.out;
            final ByteArrayOutputStream out = new ByteArrayOutputStream();
            System.setOut(new PrintStream(out));
            GameMaster.getInstance().onContinue();
            
            System.setOut(printStream);
            GameMaster.getInstance().onEnd();
            
            assertTrue(out.toString().contains("PRIVMSG #test-channel :12[4p]12[5p]12[7p]12[7p]03[2s]03[3s]03[4s]03[5s]03[9s]06[東]03[發]03[發]04[中] 04[7m]"));
        }
    }
    
    /**
     * onEnd() のテスト
     */
    @Test
    public void testOnEnd() throws JanException, IOException {
        {
            // 正常
            MockBOT.initialize();
            MockBOT.connect();
            
            try {
                GameMaster.getInstance().onStartSolo(TEST_PLAYER_NAME);
            }
            catch (CallableException e) {
            }
            finally {
                GameMaster.getInstance().onEnd();
                assertEquals(GameStatus.IDLE, GameMaster.getInstance().getStatus());
            }
        }
    }
    
    /**
     * onReplay() のテスト
     */
    @Test
    public void testOnReplay() throws JanException, IOException {
        {
            // 正常
            MockBOT.initialize();
            MockBOT.connect();
            
            final String playerName = TEST_PLAYER_NAME;
            try {
                GameMaster.getInstance().onReplay(playerName);
            }
            catch (CallableException e) {
            }
            finally {
                assertEquals(GameStatus.PLAYING_SOLO, GameMaster.getInstance().getStatus());
                GameMaster.getInstance().onEnd();
            }
        }
        {
            // 正常 (開始済み)
            MockBOT.initialize();
            MockBOT.connect();
            
            final String playerName = TEST_PLAYER_NAME;
            try {
                GameMaster.getInstance().onReplay(playerName);
            }
            catch (CallableException e) {
            }
            try {
                GameMaster.getInstance().onReplay(playerName);
            }
            catch (CallableException e) {
            }
            finally {
                assertEquals(GameStatus.PLAYING_SOLO, GameMaster.getInstance().getStatus());
                GameMaster.getInstance().onEnd();
            }
        }
        {
            // エラー (空のプレイヤー名)
            MockBOT.initialize();
            MockBOT.connect();
            
            final String playerName = "";
            try {
                GameMaster.getInstance().onReplay(playerName);
                fail();
            }
            catch (final IllegalArgumentException e) {
                assertEquals("Player name is empty.", e.getMessage());
                assertEquals(GameStatus.IDLE, GameMaster.getInstance().getStatus());
            }
            finally {
                GameMaster.getInstance().onEnd();
            }
        }
        {
            // エラー (プレイヤー名がNull)
            MockBOT.initialize();
            MockBOT.connect();
            
            final String playerName = null;
            try {
                GameMaster.getInstance().onReplay(playerName);
                fail();
            }
            catch (final NullPointerException e) {
                assertEquals("Player name is null.", e.getMessage());
                assertEquals(GameStatus.IDLE, GameMaster.getInstance().getStatus());
            }
            finally {
                GameMaster.getInstance().onEnd();
            }
        }
    }
    
    /**
     * onReplayChm() のテスト
     */
    @Test
    public void testOnReplayChm() throws JanException, IOException {
        {
            // 正常
            MockBOT.initialize();
            MockBOT.connect();
            
            final String playerName = TEST_PLAYER_NAME;
            try {
                GameMaster.getInstance().onReplayChm(playerName);
            }
            catch (CallableException e) {
            }
            finally {
                assertEquals(GameStatus.PLAYING_SOLO, GameMaster.getInstance().getStatus());
                GameMaster.getInstance().onEnd();
            }
        }
        {
            // 正常 (開始済み)
            MockBOT.initialize();
            MockBOT.connect();
            
            final String playerName = TEST_PLAYER_NAME;
            try {
                GameMaster.getInstance().onReplayChm(playerName);
            }
            catch (CallableException e) {
            }
            try {
                GameMaster.getInstance().onReplayChm(playerName);
            }
            catch (CallableException e) {
            }
            finally {
                assertEquals(GameStatus.PLAYING_SOLO, GameMaster.getInstance().getStatus());
                GameMaster.getInstance().onEnd();
            }
        }
        {
            // エラー (空のプレイヤー名)
            MockBOT.initialize();
            MockBOT.connect();
            
            final String playerName = "";
            try {
                GameMaster.getInstance().onReplayChm(playerName);
                fail();
            }
            catch (final IllegalArgumentException e) {
                assertEquals("Player name is empty.", e.getMessage());
                assertEquals(GameStatus.IDLE, GameMaster.getInstance().getStatus());
            }
            finally {
                GameMaster.getInstance().onEnd();
            }
        }
        {
            // エラー (プレイヤー名がNull)
            MockBOT.initialize();
            MockBOT.connect();
            
            final String playerName = null;
            try {
                GameMaster.getInstance().onReplayChm(playerName);
                fail();
            }
            catch (final NullPointerException e) {
                assertEquals("Player name is null.", e.getMessage());
                assertEquals(GameStatus.IDLE, GameMaster.getInstance().getStatus());
            }
            finally {
                GameMaster.getInstance().onEnd();
            }
        }
    }
    
    /**
     * onStartChmSolo() のテスト
     */
    @Test
    public void testOnStartChmSolo() throws JanException, IOException {
        {
            // 正常
            MockBOT.initialize();
            MockBOT.connect();
            
            final String playerName = TEST_PLAYER_NAME;
            try {
                GameMaster.getInstance().onStartChmSolo(playerName);
            }
            catch (CallableException e) {
            }
            finally {
                assertEquals(GameStatus.PLAYING_SOLO, GameMaster.getInstance().getStatus());
                GameMaster.getInstance().onEnd();
            }
        }
        {
            // 正常 (開始済み)
            MockBOT.initialize();
            MockBOT.connect();
            
            final String playerName = TEST_PLAYER_NAME;
            try {
                GameMaster.getInstance().onStartChmSolo(playerName);
            }
            catch (CallableException e) {
            }
            try {
                GameMaster.getInstance().onStartChmSolo(playerName);
            }
            catch (CallableException e) {
            }
            finally {
                assertEquals(GameStatus.PLAYING_SOLO, GameMaster.getInstance().getStatus());
                GameMaster.getInstance().onEnd();
            }
        }
        {
            // エラー (空のプレイヤー名)
            MockBOT.initialize();
            MockBOT.connect();
            
            final String playerName = "";
            try {
                GameMaster.getInstance().onStartChmSolo(playerName);
                fail();
            }
            catch (final IllegalArgumentException e) {
                assertEquals("Player name is empty.", e.getMessage());
                assertEquals(GameStatus.IDLE, GameMaster.getInstance().getStatus());
            }
            finally {
                GameMaster.getInstance().onEnd();
            }
        }
        {
            // エラー (プレイヤー名がNull)
            MockBOT.initialize();
            MockBOT.connect();
            
            final String playerName = null;
            try {
                GameMaster.getInstance().onStartChmSolo(playerName);
                fail();
            }
            catch (final NullPointerException e) {
                assertEquals("Player name is null.", e.getMessage());
                assertEquals(GameStatus.IDLE, GameMaster.getInstance().getStatus());
            }
            finally {
                GameMaster.getInstance().onEnd();
            }
        }
    }
    
    /**
     * onStartSolo() のテスト
     */
    @Test
    public void testOnStartSolo() throws JanException, IOException {
        {
            // 正常
            MockBOT.initialize();
            MockBOT.connect();
            
            final String playerName = TEST_PLAYER_NAME;
            try {
                GameMaster.getInstance().onStartSolo(playerName);
            }
            catch (CallableException e) {
            }
            finally {
                assertEquals(GameStatus.PLAYING_SOLO, GameMaster.getInstance().getStatus());
                GameMaster.getInstance().onEnd();
            }
        }
        {
            // 正常 (開始済み)
            MockBOT.initialize();
            MockBOT.connect();
            
            final String playerName = TEST_PLAYER_NAME;
            try {
                GameMaster.getInstance().onStartSolo(playerName);
            }
            catch (CallableException e) {
            }
            try {
                GameMaster.getInstance().onStartSolo(playerName);
            }
            catch (CallableException e) {
            }
            finally {
                assertEquals(GameStatus.PLAYING_SOLO, GameMaster.getInstance().getStatus());
                GameMaster.getInstance().onEnd();
            }
        }
        {
            // エラー (空のプレイヤー名)
            MockBOT.initialize();
            MockBOT.connect();
            
            final String playerName = "";
            try {
                GameMaster.getInstance().onStartSolo(playerName);
                fail();
            }
            catch (final IllegalArgumentException e) {
                assertEquals("Player name is empty.", e.getMessage());
                assertEquals(GameStatus.IDLE, GameMaster.getInstance().getStatus());
            }
            finally {
                GameMaster.getInstance().onEnd();
            }
        }
        {
            // エラー (プレイヤー名がNull)
            MockBOT.initialize();
            MockBOT.connect();
            
            final String playerName = null;
            try {
                GameMaster.getInstance().onStartSolo(playerName);
                fail();
            }
            catch (final NullPointerException e) {
                assertEquals("Player name is null.", e.getMessage());
                assertEquals(GameStatus.IDLE, GameMaster.getInstance().getStatus());
            }
            finally {
                GameMaster.getInstance().onEnd();
            }
        }
    }
    
    /**
     * onTestChm() のテスト
     */
    @Test
    public void testOnTestChm() throws JanException, IOException {
        {
            // 正常
            MockBOT.initialize();
            MockBOT.connect();
            
            final String playerName = TEST_PLAYER_NAME;
            try {
                GameMaster.getInstance().onTestChm(playerName);
            }
            catch (CallableException e) {
            }
            finally {
                assertEquals(GameStatus.PLAYING_SOLO, GameMaster.getInstance().getStatus());
                GameMaster.getInstance().onEnd();
            }
        }
        {
            // 正常 (開始済み)
            MockBOT.initialize();
            MockBOT.connect();
            
            final String playerName = TEST_PLAYER_NAME;
            try {
                GameMaster.getInstance().onTestChm(playerName);
            }
            catch (CallableException e) {
            }
            try {
                GameMaster.getInstance().onTestChm(playerName);
            }
            catch (CallableException e) {
            }
            finally {
                assertEquals(GameStatus.PLAYING_SOLO, GameMaster.getInstance().getStatus());
                GameMaster.getInstance().onEnd();
            }
        }
        {
            // エラー (空のプレイヤー名)
            MockBOT.initialize();
            MockBOT.connect();
            
            final String playerName = "";
            try {
                GameMaster.getInstance().onTestChm(playerName);
                fail();
            }
            catch (final IllegalArgumentException e) {
                assertEquals("Player name is empty.", e.getMessage());
                assertEquals(GameStatus.IDLE, GameMaster.getInstance().getStatus());
            }
            finally {
                GameMaster.getInstance().onEnd();
            }
        }
        {
            // エラー (プレイヤー名がNull)
            MockBOT.initialize();
            MockBOT.connect();
            
            final String playerName = null;
            try {
                GameMaster.getInstance().onTestChm(playerName);
                fail();
            }
            catch (final NullPointerException e) {
                assertEquals("Player name is null.", e.getMessage());
                assertEquals(GameStatus.IDLE, GameMaster.getInstance().getStatus());
            }
            finally {
                GameMaster.getInstance().onEnd();
            }
        }
    }
    
    
    
    /**
     * onContinue()を呼び出す
     */
    private void callOnContinue() throws JanException {
        try {
            GameMaster.getInstance().onContinue();
        }
        catch (CallableException e) {
        }
    }
    /**
     * onDiscard()を呼び出す
     */
    private void callOnDiscard() throws JanException {
        try {
            GameMaster.getInstance().onDiscard();
        }
        catch (CallableException e) {
        }
    }
    /**
     * onDiscard(String)を呼び出す
     */
    private void callOnDiscard(final String janPai) throws JanException {
        try {
            GameMaster.getInstance().onDiscard(janPai);
        }
        catch (CallableException e) {
        }
    }
    
    
    
    /**
     * プレイヤー名
     */
    private static final String TEST_PLAYER_NAME = "テストプレイヤー";
    
    /**
     * 牌指定
     */
    private static final String JANPAI_1S       = "1s";
    private static final String JANPAI_3S       = "3s";
    private static final String JANPAI_9S       = "9s";
    private static final String JANPAI_TON      = "ton";
    private static final String JANPAI_HATU     = "hatu";
    private static final String JANPAI_CHUN     = "ch";
    
}

