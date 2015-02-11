/**
 * MockBOT.java
 * 
 * @author Yuki
 */

package wiz.project.janbot;

import java.io.IOException;
import java.lang.reflect.Field;

import org.pircbotx.PircBotX;
import org.pircbotx.exception.IrcException;
import org.pircbotx.hooks.Event;
import org.pircbotx.hooks.Listener;

import wiz.project.ircbot.IRCBOT;



/**
 * JanBOTのMock
 */
public final class MockBOT {
    
    /**
     * コンストラクタ利用禁止
     */
    private MockBOT() {}
    
    
    
    /**
     * ダミーサーバ接続処理
     */
    public static void connect() {
        final String nickname = "MockBOT";
        final String serverHost = "dummy.server.com";
        final int serverPort = 6667;
        final String channel = "#test-channel";
        final Listener<PircBotX> listener = createMessageListener();
        try {
            IRCBOT.getInstance().initialize(nickname, serverHost, serverPort, channel, listener);
        }
        catch (final IrcException | IOException e) {
            throw new InternalError(e.toString());
        }
    }
    
    /**
     * 初期化処理
     */
    public static void initialize() {
        try {
            final String fieldName = "_core";
            setField(IRCBOT.getInstance(), IRCBOT.class, fieldName, new MockCore());
        }
        catch (final NoSuchFieldException e) {
            throw new InternalError(e.toString());
        }
    }
    
    
    
    /**
     * メッセージリスナーを取得
     * 
     * @return メッセージリスナー。
     */
    private static <T extends PircBotX> Listener<T> createMessageListener() {
        return new Listener<T>() {
            public void onEvent(final Event<T> event) throws Exception {
                // 何もしない
            }
        };
    }
    
    /**
     * フィールド情報を取得
     * 
     * @param classInfo 取得元クラス情報。
     * @param fieldName 取得対象フィールド名。
     * @return フィールド情報。
     * @throws NoSuchFieldException 指定されたフィールドが存在しない。
     */
    private static Field getFieldInfo(final Class<?> classInfo, final String fieldName)
            throws NoSuchFieldException {
        final Field field = classInfo.getDeclaredField(fieldName);
        field.setAccessible(true);
        return field;
    }
    
    /**
     * フィールドに値を設定
     * 
     * @param source 設定対象オブジェクト。クラスフィールドの場合はnullを指定する。
     * @param classInfo 設定対象クラス情報。
     * @param fieldName 設定対象フィールド名。
     * @param value 設定値。nullを許可する。
     * @throws IllegalArgumentException 不正な引数。
     * @throws NoSuchFieldException 指定されたフィールドが存在しない。
     */
    private static void setField(final Object source,
                                 final Class<?> classInfo,
                                 final String fieldName,
                                 final Object value)
            throws IllegalArgumentException, NoSuchFieldException {
        try {
            final Field field = getFieldInfo(classInfo, fieldName);
            field.set(source, value);
        }
        catch (final SecurityException | IllegalAccessException e) {
            throw new IllegalArgumentException(e);
        }
    }
    
    
    
    /**
     * Mock本体
     */
    private static final class MockCore extends PircBotX {
        
        /**
         * コンストラクタ
         */
        public MockCore() {
        }
        
        
        
        /**
         * サーバに接続
         */
        @Override
        public synchronized void connect(final String serverHost, final int serverPort) {
            // 何もしない
        }
        
        /**
         * サーバーから切断
         */
        @Override
        public synchronized void disconnect() {
            // 何もしない
        }
        
        /**
         * チャンネルに参加
         */
        @Override
        public void joinChannel(final String channelName) {
            // 何もしない
        }
        
        /**
         * メッセージを送信
         */
        @Override
        public void sendMessage(final String target, final String message) {
            System.out.println("PRIVMSG " + target + " :" + message);
        }
        
    }
    
}

