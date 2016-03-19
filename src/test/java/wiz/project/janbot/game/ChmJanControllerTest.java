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
        callOnMessage(pircBotX, listener, MESSAGE_DISCARD);
        callOnMessage(pircBotX, listener, MESSAGE_PON);
        callOnMessage(pircBotX, listener, MESSAGE_DISCARD_CHUN);
        callOnMessage(pircBotX, listener, MESSAGE_DISCARD);
        
        final PrintStream printStream = System.out;
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));
        
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
        callOnMessage(pircBotX, listener, MESSAGE_DISCARD);
        callOnMessage(pircBotX, listener, MESSAGE_PON);
        
        final PrintStream printStream = System.out;
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));
        
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
        callOnMessage(pircBotX, listener, MESSAGE_DISCARD);
        callOnMessage(pircBotX, listener, MESSAGE_PON);
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
        callOnMessage(pircBotX, listener, MESSAGE_DISCARD);
        callOnMessage(pircBotX, listener, MESSAGE_PON);
        callOnMessage(pircBotX, listener, MESSAGE_DISCARD_CHUN);
        callOnMessage(pircBotX, listener, MESSAGE_DISCARD);
        callOnMessage(pircBotX, listener, MESSAGE_DISCARD);
        callOnMessage(pircBotX, listener, MESSAGE_DISCARD_4P);
        callOnMessage(pircBotX, listener, MESSAGE_DISCARD_5P);
        callOnMessage(pircBotX, listener, MESSAGE_DISCARD);
        callOnMessage(pircBotX, listener, MESSAGE_DISCARD);
        callOnMessage(pircBotX, listener, MESSAGE_DISCARD);
        callOnMessage(pircBotX, listener, MESSAGE_DISCARD);
        callOnMessage(pircBotX, listener, MESSAGE_DISCARD);
        callOnMessage(pircBotX, listener, MESSAGE_DISCARD);
        callOnMessage(pircBotX, listener, MESSAGE_DISCARD);
        callOnMessage(pircBotX, listener, MESSAGE_PON);
        callOnMessage(pircBotX, listener, MESSAGE_DISCARD_TON);
        
        final PrintStream printStream = System.out;
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));
        
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
        callOnMessage(pircBotX, listener, MESSAGE_DISCARD_TON);
        callOnMessage(pircBotX, listener, MESSAGE_DISCARD);
        callOnMessage(pircBotX, listener, MESSAGE_PON);
        callOnMessage(pircBotX, listener, MESSAGE_DISCARD_CHUN);
        callOnMessage(pircBotX, listener, MESSAGE_DISCARD);
        callOnMessage(pircBotX, listener, MESSAGE_DISCARD);
        callOnMessage(pircBotX, listener, MESSAGE_DISCARD_9S);
        callOnMessage(pircBotX, listener, MESSAGE_DISCARD);
        callOnMessage(pircBotX, listener, MESSAGE_DISCARD);
        callOnMessage(pircBotX, listener, MESSAGE_DISCARD_4P);
        callOnMessage(pircBotX, listener, MESSAGE_DISCARD);
        callOnMessage(pircBotX, listener, MESSAGE_DISCARD);
        callOnMessage(pircBotX, listener, MESSAGE_DISCARD);
        callOnMessage(pircBotX, listener, MESSAGE_DISCARD_5P);
        callOnMessage(pircBotX, listener, MESSAGE_DISCARD);
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
        
        callOnMessage(pircBotX, listener, MESSAGE_HU);
        
        assertTrue(out.toString().equals("PRIVMSG #test-channel :(  ´∀｀) ＜ Game is not started." + System.lineSeparator()));
        
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
        callOnMessage(pircBotX, listener, MESSAGE_DISCARD);
        callOnMessage(pircBotX, listener, MESSAGE_PON);
        
        final PrintStream printStream = System.out;
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));
        
        callOnMessage(pircBotX, listener, MESSAGE_OUTS_3S);
        
        assertTrue(out.toString().equals("PRIVMSG #test-channel :03[3s]：残り1枚, 計：残り1枚" + System.lineSeparator()));
        
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
    private static final String MESSAGE_DISCARD      = "jan d";
    private static final String MESSAGE_DISCARD_CHUN = "jan d ch";
    private static final String MESSAGE_DISCARD_TON  = "jan d ton";
    private static final String MESSAGE_DISCARD_4P   = "jan d 4p";
    private static final String MESSAGE_DISCARD_5P   = "jan d 5p";
    private static final String MESSAGE_DISCARD_3S   = "jan d 3s";
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

