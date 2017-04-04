/**
 * MessageListener.java
 *
 * @author Masasutzu
 */

package wiz.project.janbot;

import org.pircbotx.PircBotX;
import org.pircbotx.hooks.events.MessageEvent;
import org.pircbotx.hooks.events.PrivateMessageEvent;

import dFreak.project.janbotlib.JanBotLib;



/**
 * メッセージ受付
 *
 * @param <T> PircBoxT、またはその継承クラス。
 */
public class TestMessageListener<T extends PircBotX> extends MessageListener<T> {

    /**
     * コンストラクタ
     */
    public TestMessageListener() {
    }



    /**
     * メッセージ受信時の処理
     *
     * @param event イベント情報。
     * @throws InterruptedException 処理に失敗。
     */
    @Override
    public void onMessage(final MessageEvent<T> event) throws Exception {
    	super.onMessage(event);

        // メッセージ解析
        final String message = event.getMessage();
        final String playerName = event.getUser().getNick();
        if (message.startsWith("test")) {
            JanBotLib.testChm(playerName);
        }
    }

    /**
     * トーク受信時の処理
     *
     * @param event イベント情報。
     * @throws Exception 処理に失敗。
     */
    @Override
    public void onPrivateMessage(final PrivateMessageEvent<T> event) throws Exception {
        if (event == null) {
            throw new NullPointerException("Event information is null.");
        }

        // TODO ネトマ未対応
        super.onPrivateMessage(event);
    }

}

