/**
 * MessageListener.java
 * 
 * @author Yuki
 */

package wiz.project.janbot;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;

import org.pircbotx.PircBotX;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.MessageEvent;
import org.pircbotx.hooks.events.PrivateMessageEvent;

import wiz.project.ircbot.IRCBOT;
import wiz.project.janbot.game.AnnounceFlag;
import wiz.project.janbot.game.CallType;
import wiz.project.janbot.game.GameMaster;
import wiz.project.janbot.game.GameSetStatus;
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
final class MessageListener<T extends PircBotX> extends ListenerAdapter<T> {
    
    /**
     * コンストラクタ
     */
    public MessageListener() {
    }
    
    
    
    /**
     * メッセージ受信時の処理
     * 
     * @param event イベント情報。
     * @throws InterruptedException 処理に失敗。
     */
    @Override
    public void onMessage(final MessageEvent<T> event) throws Exception {
        if (event == null) {
            throw new NullPointerException("Event information is null.");
        }
        
        // メッセージ解析
        try {
            if (_confirmMode) {
                onConfirmMessage(event);
                return;
            }
            
            final String message = event.getMessage();
            final String playerName = event.getUser().getNick();
            if (message.equals("jan ochiro")) {
                IRCBOT.getInstance().println("(  ；∀；)");
                IRCBOT.getInstance().disconnect();
            }
            else if (message.equals("jan s") || message.equals("jan start")) {
                GameMaster.getInstance().onStartSolo(playerName);
            }
            else if (message.equals("jan e") || message.equals("jan end")) {
                GameMaster.getInstance().onEnd();
            }
            else if (message.equals("jan d")) {
                GameMaster.getInstance().onDiscard();
            }
            else if (message.startsWith("jan d ")) {
                GameMaster.getInstance().onDiscard(message.substring(6));
            }
            else if (message.equals("jan i")) {
                GameMaster.getInstance().onInfo(ANNOUNCE_FLAG_FIELD);
            }
            else if (message.equals("jan r")) {
                GameMaster.getInstance().onInfo(ANNOUNCE_FLAG_RIVER);
            }
            else if (message.equals("jan ra")) {
                GameMaster.getInstance().onInfo(ANNOUNCE_FLAG_RIVER_ALL);
            }
            else if (message.equals("jan i r") || message.equals("jan r i")) {
                GameMaster.getInstance().onInfo(ANNOUNCE_FLAG_FIELD_AND_RIVER);
            }
            else if (message.equals("jan i ra")) {
                GameMaster.getInstance().onInfo(ANNOUNCE_FLAG_FIELD_AND_RIVER_ALL);
            }
//            else if (message.startsWith("jan ri- ")) {
//                TODO リーチ対応
//                GameMaster.getInstance().onRichi(message.substring(8));
//            }
            else if (message.startsWith("jan kan ")) {
                GameMaster.getInstance().onCallKan(playerName, message.substring(8));
            }
            else if (message.equals("jan tsumo")) {
                GameMaster.getInstance().onCompleteTsumo(playerName);
            }
            else if (message.equals("jan replay")) {
                GameMaster.getInstance().onReplay(playerName);
            }
            else if (message.startsWith("jan replay ")) {
                GameMaster.getInstance().onReplay(playerName, message.substring(11));
            }
//            else if (message.equals("jan download")) {
//            上手く動かないので封印
//                new Thread() {
//                    @Override
//                    public void run() {
//                        try {
//                            IRCBOT.getInstance().println("---- 牌山を" + playerName + "に送信中 ----");
//                            event.getBot().dccSendFile(new File("./deck.bin"), event.getUser(), 180000);
//                        }
//                        catch (final Throwable e) {
//                            // 何もしない
//                        }
//                    }
//                }.start();
//            }
            else if (message.equals("jan help")) {
                final List<String> messageList =
                    Arrays.asList("s：開始   e：終了   replay：リプレイ",
                                  "i：状態   r：捨て牌   d X：指定牌(ex.9p)を切る (X指定無し：ツモ切り)",
                                  "ra：他家を含む全ての捨て牌",
                                  "tsumo：ツモ和了   kan X：指定牌でカン");
                IRCBOT.getInstance().println(messageList);
            }
            else if (message.startsWith("ri-chi!") || message.startsWith("りち！") || message.startsWith("りぃち！") || message.startsWith("りーち！") || message.startsWith("リーチ！")) {
                IRCBOT.getInstance().println("⊂" + COLOR_FLAG + "04" + "●" + COLOR_FLAG + "⊃");
            }
            else if (message.startsWith("ri-chi") || message.startsWith("りち") || message.startsWith("りぃち") || message.startsWith("りーち") || message.startsWith("リーチ")) {
                IRCBOT.getInstance().println("⊂" + COLOR_FLAG + "04" + "・" + COLOR_FLAG + "⊃");
            }
            else if (message.startsWith("カロセン")) {
                IRCBOT.getInstance().println("⊂" + COLOR_FLAG + "04" + "㌍㌢" + COLOR_FLAG + "⊃");
            }
            else if (message.startsWith("キュイン")) {
                IRCBOT.getInstance().println("⊂" + COLOR_FLAG + "04" + "㌒㌅" + COLOR_FLAG + "⊃");
            }
        }
        catch (final CallableException e) {
            _confirmMode = true;
            GameMaster.getInstance().onInfo(convertToCallAnnounceType(e.getTypeList()));
        }
        catch (final GameSetException e) {
            _confirmMode = false;
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
    
    
    
    /**
     * 副露実況フラグに変換
     * 
     * @param callTypeList 副露タイプリスト。
     * @return 副露実況フラグ。
     */
    private EnumSet<AnnounceFlag> convertToCallAnnounceType(final List<CallType> callTypeList) {
        final EnumSet<AnnounceFlag> result = EnumSet.noneOf(AnnounceFlag.class);
        if (callTypeList.contains(CallType.RON)) {
            result.add(AnnounceFlag.CALLABLE_RON);
        }
        if (callTypeList.contains(CallType.CHI)) {
            result.add(AnnounceFlag.CALLABLE_CHI);
        }
        if (callTypeList.contains(CallType.PON)) {
            result.add(AnnounceFlag.CALLABLE_PON);
        }
        if (callTypeList.contains(CallType.KAN_LIGHT)) {
            result.add(AnnounceFlag.CALLABLE_KAN);
        }
        result.add(AnnounceFlag.FIELD);
        result.add(AnnounceFlag.HAND);
        return result;
    }
    
    /**
     * 確認メッセージの処理
     * 
     * @param event イベント情報。
     * @throws JanException 例外イベント。
     */
    private void onConfirmMessage(final MessageEvent<T> event) throws JanException {
        final String message = event.getMessage();
        final String playerName = event.getUser().getNick();
        try {
            if (message.equals("jan ochiro")) {
                _confirmMode = false;
                IRCBOT.getInstance().println("(  ；∀；)");
                IRCBOT.getInstance().disconnect();
            }
            else if (message.equals("jan e") || message.equals("jan end")) {
                _confirmMode = false;
                GameMaster.getInstance().onEnd();
            }
            else if (message.equals("jan d")) {
                _confirmMode = false;
                GameMaster.getInstance().onContinue();
            }
            else if (message.equals("jan i")) {
                GameMaster.getInstance().onInfo(ANNOUNCE_FLAG_FIELD);
            }
            else if (message.equals("jan r")) {
                GameMaster.getInstance().onInfo(ANNOUNCE_FLAG_RIVER);
            }
            else if (message.equals("jan ra")) {
                GameMaster.getInstance().onInfo(ANNOUNCE_FLAG_RIVER_ALL);
            }
            else if (message.equals("jan i r") || message.equals("jan r i")) {
                GameMaster.getInstance().onInfo(ANNOUNCE_FLAG_FIELD_AND_RIVER);
            }
            else if (message.startsWith("jan chi ")) {
                _confirmMode = false;
                GameMaster.getInstance().onCallChi(playerName, message.substring(8));
            }
            else if (message.equals("jan pon")) {
                _confirmMode = false;
                GameMaster.getInstance().onCallPon(playerName);
            }
            else if (message.startsWith("jan kan ")) {
                _confirmMode = false;
                GameMaster.getInstance().onCallKan(playerName, message.substring(8));
            }
            else if (message.equals("jan ron")) {
                _confirmMode = false;
                GameMaster.getInstance().onCompleteRon(playerName);
            }
            else if (message.equals("jan help")) {
                final List<String> messageList =
                    Arrays.asList("chi X：指定牌(ex.3p)を先頭牌としてチー",
                                  "pon：ポン   kan X：指定牌でカン   ron：ロン",
                                  "ra：他家を含む全ての捨て牌",
                                  "d：キャンセル");
                IRCBOT.getInstance().println(messageList);
            }
        }
        catch (final Throwable e) {
            // 確認モード継続
            _confirmMode = true;
            throw e;
        }
    }
    
    /**
     * ゲーム終了時の処理
     * 
     * @param status ゲーム終了状態。
     */
    private void onGameSet(final GameSetStatus status) {
        switch (status) {
        case GAME_OVER:
            GameMaster.getInstance().onInfo(ANNOUNCE_FLAG_GAME_OVER);
            break;
        default:
            throw new InternalError();
        }
    }
    
    
    
    /**
     * 実況フラグ
     */
    private static final EnumSet<AnnounceFlag> ANNOUNCE_FLAG_GAME_OVER =
        EnumSet.of(AnnounceFlag.GAME_OVER, AnnounceFlag.FIELD, AnnounceFlag.RIVER_SINGLE, AnnounceFlag.HAND);
    private static final EnumSet<AnnounceFlag> ANNOUNCE_FLAG_FIELD =
        EnumSet.of(AnnounceFlag.FIELD);
    private static final EnumSet<AnnounceFlag> ANNOUNCE_FLAG_RIVER =
        EnumSet.of(AnnounceFlag.RIVER_SINGLE);
    private static final EnumSet<AnnounceFlag> ANNOUNCE_FLAG_RIVER_ALL =
        EnumSet.of(AnnounceFlag.RIVER_ALL);
    private static final EnumSet<AnnounceFlag> ANNOUNCE_FLAG_FIELD_AND_RIVER =
        EnumSet.of(AnnounceFlag.FIELD, AnnounceFlag.RIVER_SINGLE);
    private static final EnumSet<AnnounceFlag> ANNOUNCE_FLAG_FIELD_AND_RIVER_ALL =
        EnumSet.of(AnnounceFlag.FIELD, AnnounceFlag.RIVER_ALL);
    
    /**
     * 色付けフラグ
     */
    private static final char COLOR_FLAG = 3;
    
    
    
    /**
     * 確認モード
     */
    private volatile boolean _confirmMode = false;
    
}

