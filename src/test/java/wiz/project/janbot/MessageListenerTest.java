/**
 * MessageListenerTest.java
 * 
 * @author Yuki
 */

package wiz.project.janbot;

import static org.junit.Assert.*;

import org.junit.Test;
import org.pircbotx.PircBotX;
import org.pircbotx.hooks.events.MessageEvent;



/**
 * MessageListenerのテスト
 */
public final class MessageListenerTest {
    
    /**
     * onMessage() のテスト
     */
    @Test
    public void testOnMessage() throws Exception {
        {
            // 正常 (終了)
            MockBOT.initialize();
            MockBOT.connect();
            
            final String message = TEST_MESSAGE_END;
            final MessageEvent<PircBotX> event = new TestEvent(message);
            
            final MessageListener<PircBotX> listener = createMessageListener();
            listener.onMessage(event);
        }
        {
            // 正常 (シャットダウン)
            MockBOT.initialize();
            MockBOT.connect();
            
            final String message = TEST_MESSAGE_SHUTDOWN;
            final MessageEvent<PircBotX> event = new TestEvent(message);
            
            final MessageListener<PircBotX> listener = createMessageListener();
            listener.onMessage(event);
        }
        {
            // エラー (Nullポインタ)
            MockBOT.initialize();
            MockBOT.connect();
            
            try {
                final MessageListener<PircBotX> listener = createMessageListener();
                listener.onMessage(null);
                fail();
            }
            catch (final NullPointerException e) {
                assertEquals("Event information is null.", e.getMessage());
            }
        }
    }
    
    /**
     * onPrivateMessage() のテスト
     */
    @Test
    public void testOnPrivateMessage() throws Exception {
        {
            // 正常
            // TODO ネトマ未対応
        }
        {
            // エラー (Nullポインタ)
            MockBOT.initialize();
            MockBOT.connect();
            
            try {
                final MessageListener<PircBotX> listener = createMessageListener();
                listener.onPrivateMessage(null);
                fail();
            }
            catch (final NullPointerException e) {
                assertEquals("Event information is null.", e.getMessage());
            }
        }
    }
    
    
    
    /**
     * メッセージリスナーを生成
     * 
     * @return メッセージリスナー。
     */
    private <T extends PircBotX> MessageListener<T> createMessageListener() {
        return new MessageListener<T>();
    }
    
    
    
    /**
     * テスト用メッセージ
     */
    private static final String TEST_MESSAGE_END      = "jan end";
    private static final String TEST_MESSAGE_SHUTDOWN = "jan ochiro";
    
    
    
    /**
     * テスト用イベント情報
     */
    private static final class TestEvent extends MessageEvent<PircBotX> {
        
        /**
         * コンストラクタ
         * 
         * @param rawMessage メッセージ。
         */
        public TestEvent(final String rawMessage) {
            super(new PircBotX(), null, null, rawMessage);
        }
        
    }
    
}

