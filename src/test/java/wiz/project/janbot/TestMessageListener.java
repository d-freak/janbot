/**
 * MessageListener.java
 *
 * @author Masasutzu
 */

package wiz.project.janbot;

import org.pircbotx.PircBotX;
import org.pircbotx.hooks.events.MessageEvent;
import org.pircbotx.hooks.events.PrivateMessageEvent;

import wiz.project.ircbot.IRCBOT;
import wiz.project.janbot.game.GameMaster;
import wiz.project.janbot.game.exception.BoneheadException;
import wiz.project.janbot.game.exception.CallableException;
import wiz.project.janbot.game.exception.GameSetException;
import wiz.project.janbot.game.exception.InvalidInputException;
import wiz.project.janbot.game.exception.JanException;



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
        try {
            final String message = event.getMessage();
            final String playerName = event.getUser().getNick();
            if (message.startsWith("jan test")) {
                GameMaster.getInstance().onTestChm(playerName);
            }
        }
        catch (final CallableException e) {
            GameMaster.getInstance().onInfo(convertToCallAnnounceType(e.getTypeList()));
        }
        catch (final GameSetException e) {
            onGameSet(e.getStatus());
        }
        catch (final BoneheadException e) {
            IRCBOT.getInstance().println("(  ´∀｀) ＜ チョンボ");
        }
        catch (final InvalidInputException e) {
            // 指定ミスに対しては何もしない
        }
        catch (final JanException e) {
            IRCBOT.getInstance().println("(  ´∀｀) ＜ " + e.getMessage());
        }
        catch (final Throwable e) {
            IRCBOT.getInstance().println("(  ´∀｀) ＜ " + e.getMessage());
            throw e;
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

