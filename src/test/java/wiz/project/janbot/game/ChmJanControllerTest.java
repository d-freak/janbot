/**
 * ChmJanControllerTest.java
 * 
 * @author Masasutzu
 */

package wiz.project.janbot.game;

import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.Test;
import org.pircbotx.PircBotX;
import org.pircbotx.hooks.events.MessageEvent;

import wiz.project.janbot.MockBOT;
import wiz.project.janbot.TestMessageListener;



/**
 * 中国麻雀コントローラのテスト
 */
public final class ChmJanControllerTest {
    
    /**
     * 加槓のテスト
     */
    @Test
    public void testAddKan() throws Exception {
        MockBOT.initialize();
        MockBOT.connect();
        
        final PircBotX pircBotX = new PircBotX();
        final TestMessageListener<PircBotX> listener = createMessageListener();
        
        callOnMessage(pircBotX, listener, MESSAGE_TEST);
        // 手牌：[4p][5p][7p][2s][3s][3s][4s][5s][9s][東][發][發][中] [7p]
        callOnMessage(pircBotX, listener, MESSAGE_DISCARD);
        callOnMessage(pircBotX, listener, MESSAGE_PON);
        // 手牌：[4p][5p][7p][2s][4s][5s][9s][東][發][發][中]  [3s][3s][3s]
        callOnMessage(pircBotX, listener, MESSAGE_DISCARD_CHUN);
        callOnMessage(pircBotX, listener, MESSAGE_DISCARD);
        
        final PrintStream printStream = System.out;
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));
        // 手牌：[4p][5p][7p][2s][4s][5s][9s][東][發][發] [8m]  [3s][3s][3s]
        callOnMessage(pircBotX, listener, MESSAGE_KAN_3S);
        
        assertTrue(out.toString().equals(""));
        
        System.setOut(printStream);
        callOnMessage(pircBotX, listener, MESSAGE_END);
    }
    
    /**
     * ポン直後の加槓のテスト
     */
    @Test
    public void testAddKanAfterCall() throws Exception {
        MockBOT.initialize();
        MockBOT.connect();
        
        final PircBotX pircBotX = new PircBotX();
        final TestMessageListener<PircBotX> listener = createMessageListener();
        
        callOnMessage(pircBotX, listener, MESSAGE_TEST);
        // 手牌：[4p][5p][7p][2s][3s][3s][4s][5s][9s][東][發][發][中] [7p]
        callOnMessage(pircBotX, listener, MESSAGE_DISCARD);
        callOnMessage(pircBotX, listener, MESSAGE_PON);
        
        final PrintStream printStream = System.out;
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));
        // 手牌：[4p][5p][7p][2s][4s][5s][9s][東][發][發][中]  [3s][3s][3s]
        callOnMessage(pircBotX, listener, MESSAGE_KAN_3S);
        
        assertTrue(out.toString().equals(""));
        
        System.setOut(printStream);
        callOnMessage(pircBotX, listener, MESSAGE_END);
    }
    
    /**
     * ツモ牌の加槓のテスト
     */
    @Test
    public void testAddKanFromTsumo() throws Exception {
        MockBOT.initialize();
        MockBOT.connect();
        
        final PircBotX pircBotX = new PircBotX();
        final TestMessageListener<PircBotX> listener = createMessageListener();
        
        callOnMessage(pircBotX, listener, MESSAGE_TEST);
        // 手牌：[4p][5p][7p][2s][3s][3s][4s][5s][9s][東][發][發][中] [7p]
        callOnMessage(pircBotX, listener, MESSAGE_DISCARD);
        callOnMessage(pircBotX, listener, MESSAGE_PON);
        // 手牌：[4p][5p][7p][2s][4s][5s][9s][東][發][發][中]  [3s][3s][3s]
        callOnMessage(pircBotX, listener, MESSAGE_DISCARD_CHUN);
        callOnMessage(pircBotX, listener, MESSAGE_DISCARD);
        callOnMessage(pircBotX, listener, MESSAGE_DISCARD);
        callOnMessage(pircBotX, listener, MESSAGE_DISCARD);
        callOnMessage(pircBotX, listener, MESSAGE_DISCARD);
        callOnMessage(pircBotX, listener, MESSAGE_DISCARD);
        callOnMessage(pircBotX, listener, MESSAGE_DISCARD);
        callOnMessage(pircBotX, listener, MESSAGE_DISCARD);
        callOnMessage(pircBotX, listener, MESSAGE_DISCARD);
        callOnMessage(pircBotX, listener, MESSAGE_DISCARD);
        callOnMessage(pircBotX, listener, MESSAGE_DISCARD);
        callOnMessage(pircBotX, listener, MESSAGE_DISCARD);
        callOnMessage(pircBotX, listener, MESSAGE_DISCARD);
        callOnMessage(pircBotX, listener, MESSAGE_DISCARD);
        callOnMessage(pircBotX, listener, MESSAGE_DISCARD);
        callOnMessage(pircBotX, listener, MESSAGE_DISCARD);
        callOnMessage(pircBotX, listener, MESSAGE_DISCARD);
        callOnMessage(pircBotX, listener, MESSAGE_DISCARD);
        callOnMessage(pircBotX, listener, MESSAGE_DISCARD);
        callOnMessage(pircBotX, listener, MESSAGE_DISCARD);
        
        final PrintStream printStream = System.out;
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));
        // 手牌：[4p][5p][7p][2s][4s][5s][9s][東][發][發] [3s]  [3s][3s][3s]
        callOnMessage(pircBotX, listener, MESSAGE_KAN_3S);
        
        assertTrue(!out.toString().equals(""));
        
        System.setOut(printStream);
        callOnMessage(pircBotX, listener, MESSAGE_END);
    }
    
    /**
     * 手牌の加槓のテスト
     */
    @Test
    public void testAddKanFromHand() throws Exception {
        MockBOT.initialize();
        MockBOT.connect();
        
        final PircBotX pircBotX = new PircBotX();
        final TestMessageListener<PircBotX> listener = createMessageListener();
        
        callOnMessage(pircBotX, listener, MESSAGE_TEST);
        // 手牌：[4p][5p][7p][2s][3s][3s][4s][5s][9s][東][發][發][中] [7p]
        callOnMessage(pircBotX, listener, MESSAGE_DISCARD);
        callOnMessage(pircBotX, listener, MESSAGE_PON);
        // 手牌：[4p][5p][7p][2s][4s][5s][9s][東][發][發][中]  [3s][3s][3s]
        callOnMessage(pircBotX, listener, MESSAGE_DISCARD_CHUN);
        callOnMessage(pircBotX, listener, MESSAGE_DISCARD);
        callOnMessage(pircBotX, listener, MESSAGE_DISCARD);
        // 手牌：[4p][5p][7p][2s][4s][5s][9s][東][發][發] [9s]  [3s][3s][3s]
        callOnMessage(pircBotX, listener, MESSAGE_DISCARD_4P);
        // 手牌：[5p][7p][2s][4s][5s][9s][9s][東][發][發] [9s]  [3s][3s][3s]
        callOnMessage(pircBotX, listener, MESSAGE_DISCARD_5P);
        callOnMessage(pircBotX, listener, MESSAGE_DISCARD);
        callOnMessage(pircBotX, listener, MESSAGE_DISCARD);
        callOnMessage(pircBotX, listener, MESSAGE_DISCARD);
        callOnMessage(pircBotX, listener, MESSAGE_DISCARD);
        callOnMessage(pircBotX, listener, MESSAGE_DISCARD);
        callOnMessage(pircBotX, listener, MESSAGE_DISCARD);
        callOnMessage(pircBotX, listener, MESSAGE_DISCARD);
        callOnMessage(pircBotX, listener, MESSAGE_PON);
        // 手牌：[7p][2s][4s][5s][9s][東][發][發]  [9s][9s][9s] [3s][3s][3s]
        callOnMessage(pircBotX, listener, MESSAGE_DISCARD_TON);
        
        final PrintStream printStream = System.out;
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));
        // 手牌：[7p][2s][4s][5s][9s][發][發] [4m]  [9s][9s][9s] [3s][3s][3s]
        callOnMessage(pircBotX, listener, MESSAGE_KAN_9S);
        
        assertTrue(!out.toString().equals(""));
        
        System.setOut(printStream);
        callOnMessage(pircBotX, listener, MESSAGE_END);
    }
    
    /**
     * 後付け和絶張のテスト
     */
    @Test
    public void testBackdoorLastTile() throws Exception {
        MockBOT.initialize();
        MockBOT.connect();
        
        final PircBotX pircBotX = new PircBotX();
        final TestMessageListener<PircBotX> listener = createMessageListener();
        
        callOnMessage(pircBotX, listener, MESSAGE_TEST);
        // 手牌：[4p][5p][7p][2s][3s][3s][4s][5s][9s][東][發][發][中] [7p]
        callOnMessage(pircBotX, listener, MESSAGE_DISCARD_TON);
        callOnMessage(pircBotX, listener, MESSAGE_DISCARD);
        callOnMessage(pircBotX, listener, MESSAGE_PON);
        // 手牌：[4p][5p][7p][7p][2s][3s][3s][4s][5s][9s][中]  [發][發][發]
        callOnMessage(pircBotX, listener, MESSAGE_DISCARD_CHUN);
        callOnMessage(pircBotX, listener, MESSAGE_DISCARD);
        callOnMessage(pircBotX, listener, MESSAGE_DISCARD);
        // 手牌：[4p][5p][7p][7p][2s][3s][3s][4s][5s][9s] [1s]  [發][發][發]
        callOnMessage(pircBotX, listener, MESSAGE_DISCARD_9S);
        callOnMessage(pircBotX, listener, MESSAGE_DISCARD);
        callOnMessage(pircBotX, listener, MESSAGE_DISCARD);
        // 手牌：[4p][5p][7p][7p][1s][2s][3s][3s][4s][5s] [5m]  [發][發][發]
        callOnMessage(pircBotX, listener, MESSAGE_DISCARD_4P);
        callOnMessage(pircBotX, listener, MESSAGE_DISCARD);
        callOnMessage(pircBotX, listener, MESSAGE_DISCARD);
        callOnMessage(pircBotX, listener, MESSAGE_DISCARD);
        // 手牌：[5m][5p][7p][7p][1s][2s][3s][3s][4s][5s] [6m]  [發][發][發]
        callOnMessage(pircBotX, listener, MESSAGE_DISCARD_5P);
        callOnMessage(pircBotX, listener, MESSAGE_DISCARD);
        // 手牌：[5m][6m][7p][7p][1s][2s][3s][3s][4s][5s] [6s]  [發][發][發]
        callOnMessage(pircBotX, listener, MESSAGE_DISCARD_3S);
        callOnMessage(pircBotX, listener, MESSAGE_DISCARD);
        callOnMessage(pircBotX, listener, MESSAGE_DISCARD);
        callOnMessage(pircBotX, listener, MESSAGE_DISCARD);
        callOnMessage(pircBotX, listener, MESSAGE_DISCARD);
        callOnMessage(pircBotX, listener, MESSAGE_DISCARD);
        callOnMessage(pircBotX, listener, MESSAGE_DISCARD);
        callOnMessage(pircBotX, listener, MESSAGE_DISCARD);
        callOnMessage(pircBotX, listener, MESSAGE_HU);
        
        final PrintStream printStream = System.out;
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));
        // 手牌：[5m][6m][7p][7p][1s][2s][3s][4s][5s][6s] [4m]  [發][發][發] ※ 和了済み
        callOnMessage(pircBotX, listener, MESSAGE_HU);
        
        assertTrue(out.toString().equals("PRIVMSG #test-channel :(  ´∀｀) ＜ Game is not started." + System.lineSeparator()));
        
        System.setOut(printStream);
        callOnMessage(pircBotX, listener, MESSAGE_END);
    }
    
    /**
     * 待ち牌変更表示のテスト
     */
    @Test
    public void testChangeWaitingOuts1() throws Exception {
        MockBOT.initialize();
        MockBOT.connect();
        
        final PircBotX pircBotX = new PircBotX();
        final TestMessageListener<PircBotX> listener = createMessageListener();
        
        callOnMessage(pircBotX, listener, MESSAGE_TEST);
        // 手牌：[4p][5p][7p][2s][3s][3s][4s][5s][9s][東][發][發][中] [7p]
        callOnMessage(pircBotX, listener, MESSAGE_DISCARD_TON);
        callOnMessage(pircBotX, listener, MESSAGE_DISCARD);
        callOnMessage(pircBotX, listener, MESSAGE_PON);
        // 手牌：[4p][5p][7p][7p][2s][3s][3s][4s][5s][9s][中]  [發][發][發]
        callOnMessage(pircBotX, listener, MESSAGE_DISCARD_CHUN);
        callOnMessage(pircBotX, listener, MESSAGE_DISCARD);
        callOnMessage(pircBotX, listener, MESSAGE_DISCARD);
        // 手牌：[4p][5p][7p][7p][2s][3s][3s][4s][5s][9s] [1s]  [發][發][發]
        callOnMessage(pircBotX, listener, MESSAGE_DISCARD_9S);
        callOnMessage(pircBotX, listener, MESSAGE_DISCARD);
        callOnMessage(pircBotX, listener, MESSAGE_DISCARD);
        // 手牌：[4p][5p][7p][7p][1s][2s][3s][3s][4s][5s] [5m]  [發][發][發]
        callOnMessage(pircBotX, listener, MESSAGE_DISCARD_4P);
        callOnMessage(pircBotX, listener, MESSAGE_DISCARD);
        callOnMessage(pircBotX, listener, MESSAGE_DISCARD);
        callOnMessage(pircBotX, listener, MESSAGE_DISCARD);
        // 手牌：[5m][5p][7p][7p][1s][2s][3s][3s][4s][5s] [6m]  [發][發][發]
        callOnMessage(pircBotX, listener, MESSAGE_DISCARD_5P);
        callOnMessage(pircBotX, listener, MESSAGE_DISCARD);
        // 手牌：[5m][6m][7p][7p][1s][2s][3s][3s][4s][5s] [6s]  [發][發][發]
        callOnMessage(pircBotX, listener, MESSAGE_DISCARD_3S);
        callOnMessage(pircBotX, listener, MESSAGE_DISCARD);
        callOnMessage(pircBotX, listener, MESSAGE_DISCARD);
        callOnMessage(pircBotX, listener, MESSAGE_DISCARD);
        callOnMessage(pircBotX, listener, MESSAGE_DISCARD);
        callOnMessage(pircBotX, listener, MESSAGE_DISCARD);
        
        final PrintStream printStream = System.out;
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));
        
        callOnMessage(pircBotX, listener, MESSAGE_DISCARD);
        
        assertTrue(out.toString().contains("PRIVMSG #test-channel :待ち牌：04[4m]：残り1枚, 04[7m]：残り1枚, 計：残り2枚" + System.lineSeparator()));
        
        System.setOut(printStream);
        callOnMessage(pircBotX, listener, MESSAGE_END);
    }
    
    /**
     * 待ち牌変更表示のテスト(手出し牌が待ち牌)
     */
    @Test
    public void testChangeWaitingOuts2() throws Exception {
        MockBOT.initialize();
        MockBOT.connect();
        
        final PircBotX pircBotX = new PircBotX();
        final TestMessageListener<PircBotX> listener = createMessageListener();
        
        callOnMessage(pircBotX, listener, MESSAGE_TEST);
        // 手牌：[4p][5p][7p][2s][3s][3s][4s][5s][9s][東][發][發][中] [7p]
        callOnMessage(pircBotX, listener, MESSAGE_DISCARD_TON);
        callOnMessage(pircBotX, listener, MESSAGE_PON);
        // 手牌：[4p][5p][7p][7p][2s][4s][5s][9s][發][發][中]  [3s][3s][3s]
        callOnMessage(pircBotX, listener, MESSAGE_DISCARD_CHUN);
        callOnMessage(pircBotX, listener, MESSAGE_DISCARD);
        // 手牌：[4p][5p][7p][7p][2s][4s][5s][9s][發][發] [8m]  [3s][3s][3s]
        callOnMessage(pircBotX, listener, MESSAGE_DISCARD_2S);
        // 手牌：[8m][4p][5p][7p][7p][4s][5s][9s][發][發] [9s]  [3s][3s][3s]
        callOnMessage(pircBotX, listener, MESSAGE_DISCARD_4P);
        // 手牌：[8m][5p][7p][7p][4s][5s][9s][9s][發][發] [9s]  [3s][3s][3s]
        callOnMessage(pircBotX, listener, MESSAGE_DISCARD_5P);
        callOnMessage(pircBotX, listener, MESSAGE_DISCARD);
        callOnMessage(pircBotX, listener, MESSAGE_DISCARD);
        callOnMessage(pircBotX, listener, MESSAGE_DISCARD);
        callOnMessage(pircBotX, listener, MESSAGE_DISCARD);
        callOnMessage(pircBotX, listener, MESSAGE_DISCARD);
        callOnMessage(pircBotX, listener, MESSAGE_DISCARD);
        callOnMessage(pircBotX, listener, MESSAGE_DISCARD);
        callOnMessage(pircBotX, listener, MESSAGE_DISCARD);
        callOnMessage(pircBotX, listener, MESSAGE_PON);
        // 手牌：[8m][7p][7p][4s][5s][9s][發][發]  [9s][9s][9s] [3s][3s][3s]
        callOnMessage(pircBotX, listener, MESSAGE_DISCARD_4S);
        callOnMessage(pircBotX, listener, MESSAGE_DISCARD);
        callOnMessage(pircBotX, listener, MESSAGE_PON);
        // 手牌：[8m][7p][7p][5s][9s]  [發][發][發] [9s][9s][9s] [3s][3s][3s]
        callOnMessage(pircBotX, listener, MESSAGE_DISCARD_5S);
        // 手牌：[8m][7p][7p][9s] [7m]  [發][發][發] [9s][9s][9s] [3s][3s][3s]
        callOnMessage(pircBotX, listener, MESSAGE_KAN_9S);
        
        final PrintStream printStream = System.out;
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));
        // 手牌：[7m][8m][7p][7p] [9m]  [發][發][發] [9s][9s][9s][9s] [3s][3s][3s]
        callOnMessage(pircBotX, listener, MESSAGE_DISCARD_7M);
        
        assertTrue(out.toString().contains("PRIVMSG #test-channel :待ち牌：04[7m]：残り1枚, 計：残り1枚" + System.lineSeparator()));
        
        System.setOut(printStream);
        callOnMessage(pircBotX, listener, MESSAGE_END);
    }
    
    /**
     * 待ち牌変更表示のテスト(ツモ切り牌が待ち牌)
     */
    @Test
    public void testChangeWaitingOuts3() throws Exception {
        MockBOT.initialize();
        MockBOT.connect();
        
        final PircBotX pircBotX = new PircBotX();
        final TestMessageListener<PircBotX> listener = createMessageListener();
        
        callOnMessage(pircBotX, listener, MESSAGE_TEST);
        // 手牌：[4p][5p][7p][2s][3s][3s][4s][5s][9s][東][發][發][中] [7p]
        callOnMessage(pircBotX, listener, MESSAGE_DISCARD_TON);
        callOnMessage(pircBotX, listener, MESSAGE_PON);
        // 手牌：[4p][5p][7p][7p][2s][4s][5s][9s][發][發][中]  [3s][3s][3s]
        callOnMessage(pircBotX, listener, MESSAGE_DISCARD_CHUN);
        callOnMessage(pircBotX, listener, MESSAGE_DISCARD);
        // 手牌：[4p][5p][7p][7p][2s][4s][5s][9s][發][發] [8m]  [3s][3s][3s]
        callOnMessage(pircBotX, listener, MESSAGE_DISCARD_2S);
        // 手牌：[8m][4p][5p][7p][7p][4s][5s][9s][發][發] [9s]  [3s][3s][3s]
        callOnMessage(pircBotX, listener, MESSAGE_DISCARD_4P);
        // 手牌：[8m][5p][7p][7p][4s][5s][9s][9s][發][發] [9s]  [3s][3s][3s]
        callOnMessage(pircBotX, listener, MESSAGE_DISCARD_5P);
        callOnMessage(pircBotX, listener, MESSAGE_DISCARD);
        callOnMessage(pircBotX, listener, MESSAGE_DISCARD);
        callOnMessage(pircBotX, listener, MESSAGE_DISCARD);
        callOnMessage(pircBotX, listener, MESSAGE_DISCARD);
        callOnMessage(pircBotX, listener, MESSAGE_DISCARD);
        callOnMessage(pircBotX, listener, MESSAGE_DISCARD);
        callOnMessage(pircBotX, listener, MESSAGE_DISCARD);
        callOnMessage(pircBotX, listener, MESSAGE_DISCARD);
        callOnMessage(pircBotX, listener, MESSAGE_PON);
        // 手牌：[8m][7p][7p][4s][5s][9s][發][發]  [9s][9s][9s] [3s][3s][3s]
        callOnMessage(pircBotX, listener, MESSAGE_DISCARD_4S);
        callOnMessage(pircBotX, listener, MESSAGE_DISCARD);
        callOnMessage(pircBotX, listener, MESSAGE_PON);
        // 手牌：[8m][7p][7p][5s][9s]  [發][發][發] [9s][9s][9s] [3s][3s][3s]
        callOnMessage(pircBotX, listener, MESSAGE_DISCARD_5S);
        // 手牌：[8m][7p][7p][9s] [7m]  [發][發][發] [9s][9s][9s] [3s][3s][3s]
        callOnMessage(pircBotX, listener, MESSAGE_KAN_9S);
        
        final PrintStream printStream = System.out;
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));
        // 手牌：[7m][8m][7p][7p] [9m]  [發][發][發] [9s][9s][9s][9s] [3s][3s][3s]
        callOnMessage(pircBotX, listener, MESSAGE_DISCARD_9M);
        
        assertTrue(out.toString().contains("PRIVMSG #test-channel :待ち牌：04[9m]：残り1枚, 計：残り1枚" + System.lineSeparator()));
        
        System.setOut(printStream);
        callOnMessage(pircBotX, listener, MESSAGE_END);
    }
    
    /**
     * 8点縛り超え終了のテスト
     */
    @Test
    public void testEndOverTiedPoint() throws Exception {
        MockBOT.initialize();
        MockBOT.connect();
        
        final PircBotX pircBotX = new PircBotX();
        final TestMessageListener<PircBotX> listener = createMessageListener();
        
        callOnMessage(pircBotX, listener, MESSAGE_TEST);
        // 手牌：[4p][5p][7p][2s][3s][3s][4s][5s][9s][東][發][發][中] [7p]
        callOnMessage(pircBotX, listener, MESSAGE_DISCARD_TON);
        callOnMessage(pircBotX, listener, MESSAGE_DISCARD);
        callOnMessage(pircBotX, listener, MESSAGE_PON);
        // 手牌：[4p][5p][7p][7p][2s][3s][3s][4s][5s][9s][中]  [發][發][發]
        callOnMessage(pircBotX, listener, MESSAGE_DISCARD_CHUN);
        callOnMessage(pircBotX, listener, MESSAGE_DISCARD);
        callOnMessage(pircBotX, listener, MESSAGE_DISCARD);
        // 手牌：[4p][5p][7p][7p][2s][3s][3s][4s][5s][9s] [1s]  [發][發][發]
        callOnMessage(pircBotX, listener, MESSAGE_DISCARD_9S);
        callOnMessage(pircBotX, listener, MESSAGE_DISCARD);
        callOnMessage(pircBotX, listener, MESSAGE_DISCARD);
        // 手牌：[4p][5p][7p][7p][1s][2s][3s][3s][4s][5s] [5m]  [發][發][發]
        callOnMessage(pircBotX, listener, MESSAGE_DISCARD_4P);
        callOnMessage(pircBotX, listener, MESSAGE_DISCARD);
        callOnMessage(pircBotX, listener, MESSAGE_DISCARD);
        callOnMessage(pircBotX, listener, MESSAGE_DISCARD);
        // 手牌：[5m][5p][7p][7p][1s][2s][3s][3s][4s][5s] [6m]  [發][發][發]
        callOnMessage(pircBotX, listener, MESSAGE_DISCARD_5P);
        callOnMessage(pircBotX, listener, MESSAGE_DISCARD);
        // 手牌：[5m][6m][7p][7p][1s][2s][3s][3s][4s][5s] [6s]  [發][發][發]
        callOnMessage(pircBotX, listener, MESSAGE_DISCARD_3S);
        callOnMessage(pircBotX, listener, MESSAGE_DISCARD);
        callOnMessage(pircBotX, listener, MESSAGE_DISCARD);
        callOnMessage(pircBotX, listener, MESSAGE_DISCARD);
        callOnMessage(pircBotX, listener, MESSAGE_DISCARD);
        
        final PrintStream printStream = System.out;
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));
        // 手牌：[5m][6m][7p][7p][1s][2s][3s][4s][5s][6s] [3p]  [發][發][發]
        callOnMessage(pircBotX, listener, MESSAGE_DISCARD_2S);
        
        assertTrue(out.toString().contains("PRIVMSG #test-channel :8点縛り超えが終了しました。" + System.lineSeparator()));
        
        System.setOut(printStream);
        callOnMessage(pircBotX, listener, MESSAGE_END);
    }
    
    /**
     * 指定牌の残り枚数のテスト
     */
    @Test
    public void testOuts() throws Exception {
        MockBOT.initialize();
        MockBOT.connect();
        
        final PircBotX pircBotX = new PircBotX();
        final TestMessageListener<PircBotX> listener = createMessageListener();
        
        callOnMessage(pircBotX, listener, MESSAGE_TEST);
        
        final PrintStream printStream = System.out;
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));
        // 手牌：[4p][5p][7p][2s][3s][3s][4s][5s][9s][東][發][發][中] [7p]
        callOnMessage(pircBotX, listener, MESSAGE_OUTS_7P);
        
        assertTrue(out.toString().equals("PRIVMSG #test-channel :12[7p]：残り2枚, 計：残り2枚" + System.lineSeparator()));
        
        System.setOut(printStream);
        callOnMessage(pircBotX, listener, MESSAGE_END);
    }
    
    /**
     * 指定牌の残り枚数のテスト(確認メッセージ)
     */
    @Test
    public void testOutsOnConfirm() throws Exception {
        MockBOT.initialize();
        MockBOT.connect();
        
        final PircBotX pircBotX = new PircBotX();
        final TestMessageListener<PircBotX> listener = createMessageListener();
        
        callOnMessage(pircBotX, listener, MESSAGE_TEST);
        // 手牌：[4p][5p][7p][2s][3s][3s][4s][5s][9s][東][發][發][中] [7p]
        callOnMessage(pircBotX, listener, MESSAGE_DISCARD);
        
        final PrintStream printStream = System.out;
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));
        
        callOnMessage(pircBotX, listener, MESSAGE_OUTS_7P);
        
        assertTrue(out.toString().equals("PRIVMSG #test-channel :12[7p]：残り2枚, 計：残り2枚" + System.lineSeparator()));
        
        System.setOut(printStream);
        callOnMessage(pircBotX, listener, MESSAGE_END);
    }
    
    /**
     * 指定牌の残り枚数のテスト(副露直後)
     */
    @Test
    public void testOutsAfterCall() throws Exception {
        MockBOT.initialize();
        MockBOT.connect();
        
        final PircBotX pircBotX = new PircBotX();
        final TestMessageListener<PircBotX> listener = createMessageListener();
        
        callOnMessage(pircBotX, listener, MESSAGE_TEST);
        // 手牌：[4p][5p][7p][2s][3s][3s][4s][5s][9s][東][發][發][中] [7p]
        callOnMessage(pircBotX, listener, MESSAGE_DISCARD);
        callOnMessage(pircBotX, listener, MESSAGE_PON);
        
        final PrintStream printStream = System.out;
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));
        // 手牌：[4p][5p][7p][2s][4s][5s][9s][東][發][發][中]  [3s][3s][3s]
        callOnMessage(pircBotX, listener, MESSAGE_OUTS_3S);
        
        assertTrue(out.toString().equals("PRIVMSG #test-channel :03[3s]：残り1枚, 計：残り1枚" + System.lineSeparator()));
        
        System.setOut(printStream);
        callOnMessage(pircBotX, listener, MESSAGE_END);
    }
    
    /**
     * 和了可能牌リストの更新のテスト
     */
    @Test
    public void testUpdateCompletableJanPaiList() throws Exception {
        MockBOT.initialize();
        MockBOT.connect();
        
        final PircBotX pircBotX = new PircBotX();
        final TestMessageListener<PircBotX> listener = createMessageListener();
        
        callOnMessage(pircBotX, listener, MESSAGE_TEST);
        // 手牌：[4p][5p][7p][2s][3s][3s][4s][5s][9s][東][發][發][中] [7p]
        callOnMessage(pircBotX, listener, MESSAGE_DISCARD);
        callOnMessage(pircBotX, listener, MESSAGE_DISCARD);
        callOnMessage(pircBotX, listener, MESSAGE_PON);
        // 手牌：[4p][5p][7p][2s][3s][3s][4s][5s][9s][東][中]  [發][發][發]
        callOnMessage(pircBotX, listener, MESSAGE_DISCARD_7P);
        callOnMessage(pircBotX, listener, MESSAGE_DISCARD);
        callOnMessage(pircBotX, listener, MESSAGE_DISCARD);
        // 手牌：[4p][5p][2s][3s][3s][4s][5s][9s][東][中] [1s]  [發][發][發]
        callOnMessage(pircBotX, listener, MESSAGE_DISCARD_4P);
        callOnMessage(pircBotX, listener, MESSAGE_CHI_1S);
        // 手牌：[5p][2s][3s][4s][5s][9s][東][中]  [1s][2s][3s] [發][發][發]
        callOnMessage(pircBotX, listener, MESSAGE_DISCARD_5P);
        callOnMessage(pircBotX, listener, MESSAGE_DISCARD);
        callOnMessage(pircBotX, listener, MESSAGE_DISCARD);
        callOnMessage(pircBotX, listener, MESSAGE_DISCARD);
        callOnMessage(pircBotX, listener, MESSAGE_DISCARD);
        callOnMessage(pircBotX, listener, MESSAGE_DISCARD);
        // 手牌：[2s][3s][4s][5s][9s][東][中] [7s]  [1s][2s][3s] [發][發][發]
        callOnMessage(pircBotX, listener, MESSAGE_DISCARD_TON);
        callOnMessage(pircBotX, listener, MESSAGE_DISCARD);
        callOnMessage(pircBotX, listener, MESSAGE_DISCARD);
        callOnMessage(pircBotX, listener, MESSAGE_DISCARD);
        callOnMessage(pircBotX, listener, MESSAGE_DISCARD);
        callOnMessage(pircBotX, listener, MESSAGE_DISCARD);
        callOnMessage(pircBotX, listener, MESSAGE_DISCARD);
        callOnMessage(pircBotX, listener, MESSAGE_DISCARD);
        callOnMessage(pircBotX, listener, MESSAGE_DISCARD);
        callOnMessage(pircBotX, listener, MESSAGE_DISCARD);
        // 手牌：[2s][3s][4s][5s][7s][9s][中] [3s]  [1s][2s][3s] [發][發][發]
        callOnMessage(pircBotX, listener, MESSAGE_DISCARD_CHUN);
        
        final PrintStream printStream = System.out;
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));
        // 手牌：[2s][3s][3s][4s][5s][7s][9s] [4s]  [1s][2s][3s] [發][發][發]
        callOnMessage(pircBotX, listener, MESSAGE_DISCARD_9S);
        
        assertTrue(out.toString().contains("PRIVMSG #test-channel :待ち牌：03[7s]：残り2枚, 計：残り2枚" + System.lineSeparator()));
        
        System.setOut(printStream);
        callOnMessage(pircBotX, listener, MESSAGE_END);
    }
    
    /**
     * 待ち牌表示のテスト
     */
    @Test
    public void testWaitingOuts() throws Exception {
        MockBOT.initialize();
        MockBOT.connect();
        
        final PircBotX pircBotX = new PircBotX();
        final TestMessageListener<PircBotX> listener = createMessageListener();
        
        callOnMessage(pircBotX, listener, MESSAGE_TEST);
        // 手牌：[4p][5p][7p][2s][3s][3s][4s][5s][9s][東][發][發][中] [7p]
        callOnMessage(pircBotX, listener, MESSAGE_DISCARD_TON);
        callOnMessage(pircBotX, listener, MESSAGE_DISCARD);
        callOnMessage(pircBotX, listener, MESSAGE_PON);
        // 手牌：[4p][5p][7p][7p][2s][3s][3s][4s][5s][9s][中]  [發][發][發]
        callOnMessage(pircBotX, listener, MESSAGE_DISCARD_CHUN);
        callOnMessage(pircBotX, listener, MESSAGE_DISCARD);
        callOnMessage(pircBotX, listener, MESSAGE_DISCARD);
        // 手牌：[4p][5p][7p][7p][2s][3s][3s][4s][5s][9s] [1s]  [發][發][發]
        callOnMessage(pircBotX, listener, MESSAGE_DISCARD_9S);
        callOnMessage(pircBotX, listener, MESSAGE_DISCARD);
        callOnMessage(pircBotX, listener, MESSAGE_DISCARD);
        // 手牌：[4p][5p][7p][7p][1s][2s][3s][3s][4s][5s] [5m]  [發][發][發]
        callOnMessage(pircBotX, listener, MESSAGE_DISCARD_4P);
        callOnMessage(pircBotX, listener, MESSAGE_DISCARD);
        callOnMessage(pircBotX, listener, MESSAGE_DISCARD);
        callOnMessage(pircBotX, listener, MESSAGE_DISCARD);
        // 手牌：[5m][5p][7p][7p][1s][2s][3s][3s][4s][5s] [6m]  [發][發][發]
        callOnMessage(pircBotX, listener, MESSAGE_DISCARD_5P);
        callOnMessage(pircBotX, listener, MESSAGE_DISCARD);
        // 手牌：[5m][6m][7p][7p][1s][2s][3s][3s][4s][5s] [6s]  [發][發][發]
        callOnMessage(pircBotX, listener, MESSAGE_DISCARD_3S);
        callOnMessage(pircBotX, listener, MESSAGE_DISCARD);
        callOnMessage(pircBotX, listener, MESSAGE_DISCARD);
        callOnMessage(pircBotX, listener, MESSAGE_DISCARD);
        
        final PrintStream printStream = System.out;
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));
        
        callOnMessage(pircBotX, listener, MESSAGE_DISCARD);
        
        assertTrue(out.toString().contains("PRIVMSG #test-channel :待ち牌：04[4m]：残り1枚, 計：残り1枚" + System.lineSeparator()));
        
        System.setOut(printStream);
        callOnMessage(pircBotX, listener, MESSAGE_END);
    }
    
    /**
     * 待ち牌表示のテスト(槓直後)
     */
    @Test
    public void testWaitingOutsAfterKan() throws Exception {
        MockBOT.initialize();
        MockBOT.connect();
        
        final PircBotX pircBotX = new PircBotX();
        final TestMessageListener<PircBotX> listener = createMessageListener();
        
        callOnMessage(pircBotX, listener, MESSAGE_TEST);
        // 手牌：[4p][5p][7p][2s][3s][3s][4s][5s][9s][東][發][發][中] [7p]
        callOnMessage(pircBotX, listener, MESSAGE_DISCARD_TON);
        callOnMessage(pircBotX, listener, MESSAGE_PON);
        // 手牌：[4p][5p][7p][7p][2s][4s][5s][9s][發][發][中]  [3s][3s][3s]
        callOnMessage(pircBotX, listener, MESSAGE_DISCARD_CHUN);
        callOnMessage(pircBotX, listener, MESSAGE_DISCARD);
        // 手牌：[4p][5p][7p][7p][2s][4s][5s][9s][發][發] [8m]  [3s][3s][3s]
        callOnMessage(pircBotX, listener, MESSAGE_DISCARD_2S);
        // 手牌：[8m][4p][5p][7p][7p][4s][5s][9s][發][發] [9s]  [3s][3s][3s]
        callOnMessage(pircBotX, listener, MESSAGE_DISCARD_4P);
        // 手牌：[8m][5p][7p][7p][4s][5s][9s][9s][發][發] [9s]  [3s][3s][3s]
        callOnMessage(pircBotX, listener, MESSAGE_DISCARD_5P);
        callOnMessage(pircBotX, listener, MESSAGE_DISCARD);
        callOnMessage(pircBotX, listener, MESSAGE_DISCARD);
        callOnMessage(pircBotX, listener, MESSAGE_DISCARD);
        callOnMessage(pircBotX, listener, MESSAGE_DISCARD);
        callOnMessage(pircBotX, listener, MESSAGE_DISCARD);
        callOnMessage(pircBotX, listener, MESSAGE_DISCARD);
        callOnMessage(pircBotX, listener, MESSAGE_DISCARD);
        callOnMessage(pircBotX, listener, MESSAGE_DISCARD);
        callOnMessage(pircBotX, listener, MESSAGE_PON);
        // 手牌：[8m][7p][7p][4s][5s][9s][發][發]  [9s][9s][9s] [3s][3s][3s]
        callOnMessage(pircBotX, listener, MESSAGE_DISCARD_4S);
        callOnMessage(pircBotX, listener, MESSAGE_DISCARD);
        callOnMessage(pircBotX, listener, MESSAGE_PON);
        // 手牌：[8m][7p][7p][5s][9s]  [發][發][發] [9s][9s][9s] [3s][3s][3s]
        callOnMessage(pircBotX, listener, MESSAGE_DISCARD_5S);
        
        final PrintStream printStream = System.out;
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));
        // 手牌：[8m][7p][7p][9s] [7m]  [發][發][發] [9s][9s][9s] [3s][3s][3s]
        callOnMessage(pircBotX, listener, MESSAGE_KAN_9S);
        
        assertTrue(out.toString().contains("PRIVMSG #test-channel :待ち牌：04[6m]：残り2枚, 04[9m]：残り2枚, 計：残り4枚" + System.lineSeparator()));
        
        System.setOut(printStream);
        callOnMessage(pircBotX, listener, MESSAGE_END);
    }
    
    
    
    /**
     * listenerのonMessage()の呼び出し
     * 
     * @param listener メッセージリスナー。
     * @param rawMessage メッセージ。
     */
    private void callOnMessage(final PircBotX pircBotX, final TestMessageListener<PircBotX> listener, final String rawMessage) throws Exception {
        final MessageEvent<PircBotX> messageEvent = new TestEvent(pircBotX, rawMessage);
        
        listener.onMessage(messageEvent);
    }
    
    /**
     * メッセージリスナーを生成
     * 
     * @return メッセージリスナー。
     */
    private <T extends PircBotX> TestMessageListener<T> createMessageListener() {
        return new TestMessageListener<T>();
    }
    
    
    
    /**
     * テスト用メッセージ
     */
    private static final String MESSAGE_CHI_1S       = "jan chi 1s";
    private static final String MESSAGE_DISCARD      = "jan d";
    private static final String MESSAGE_DISCARD_CHUN = "jan d ch";
    private static final String MESSAGE_DISCARD_TON  = "jan d ton";
    private static final String MESSAGE_DISCARD_7M   = "jan d 7m";
    private static final String MESSAGE_DISCARD_9M   = "jan d 9m";
    private static final String MESSAGE_DISCARD_4P   = "jan d 4p";
    private static final String MESSAGE_DISCARD_5P   = "jan d 5p";
    private static final String MESSAGE_DISCARD_7P   = "jan d 7p";
    private static final String MESSAGE_DISCARD_2S   = "jan d 2s";
    private static final String MESSAGE_DISCARD_3S   = "jan d 3s";
    private static final String MESSAGE_DISCARD_4S   = "jan d 4s";
    private static final String MESSAGE_DISCARD_5S   = "jan d 5s";
    private static final String MESSAGE_DISCARD_9S   = "jan d 9s";
    private static final String MESSAGE_END          = "jan e";
    private static final String MESSAGE_KAN_3S       = "jan kan 3s";
    private static final String MESSAGE_KAN_9S       = "jan kan 9s";
    private static final String MESSAGE_OUTS_3S      = "jan o 3s";
    private static final String MESSAGE_OUTS_7P      = "jan o 7p";
    private static final String MESSAGE_HU           = "jan hu";
    private static final String MESSAGE_PON          = "jan pon";
    private static final String MESSAGE_TEST         = "jan test";
    
    
    
    /**
     * テスト用イベント情報
     */
    private static final class TestEvent extends MessageEvent<PircBotX> {
        
        /**
         * コンストラクタ
         * 
         * @param rawMessage メッセージ。
         */
        public TestEvent(final PircBotX pircBoxX, final String rawMessage) {
            super(pircBoxX, null, pircBoxX.getUser("test"), rawMessage);
        }
        
    }
    
}

