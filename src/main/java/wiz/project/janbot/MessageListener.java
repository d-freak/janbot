/**
 * MessageListener.java
 *
 * @author Yuki
 */

package wiz.project.janbot;

import java.io.File;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;

import org.pircbotx.PircBotX;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.MessageEvent;

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
class MessageListener<T extends PircBotX> extends ListenerAdapter<T> {

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
            final String message = event.getMessage();
            final String playerName = event.getUser().getNick();
            if (message.equals("ochiro")) {
                IRCBOT.getInstance().println("(  ；∀；)");
                IRCBOT.getInstance().disconnect();
            }
            else if (message.equals("s") || message.equals("s chm") || message.equals("chm s")) {
                GameMaster.getInstance().onStartChmSolo(playerName);
            }
            else if (message.equals("s jpm") || message.equals("jpm s")) {
                GameMaster.getInstance().onStartSolo(playerName);
            }
            else if (message.equals("e") || message.equals("end")) {
                GameMaster.getInstance().onEnd();
            }
            else if (message.equals("d")) {
                GameMaster.getInstance().onDiscardOrContinue();
            }
            else if (message.startsWith("d ")) {
                GameMaster.getInstance().onDiscard(message.substring(2));
            }
            else if (message.equals("u") || message.equals("undo")) {
                GameMaster.getInstance().onUndo(playerName);
            }
            else if (message.equals("i")) {
                GameMaster.getInstance().onInfo(ANNOUNCE_FLAG_FIELD);
            }
            else if (message.equals("r")) {
                GameMaster.getInstance().onInfo(ANNOUNCE_FLAG_RIVER);
            }
            else if (message.equals("ra")) {
                GameMaster.getInstance().onInfo(ANNOUNCE_FLAG_RIVER_ALL);
            }
            else if (message.equals("i r") || message.equals("r i")) {
                GameMaster.getInstance().onInfo(ANNOUNCE_FLAG_FIELD_AND_RIVER);
            }
            else if (message.equals("i ra")) {
                GameMaster.getInstance().onInfo(ANNOUNCE_FLAG_FIELD_AND_RIVER_ALL);
            }
            else if (message.equals("w")) {
                GameMaster.getInstance().onInfo(ANNOUNCE_FLAG_WATCHING_END);
            }
            else if (message.equals("7th")) {
                GameMaster.getInstance().onInfo(ANNOUNCE_FLAG_SEVENTH);
            }
            else if (message.equals("h")) {
                GameMaster.getInstance().onHistory();
            }
            else if (message.startsWith("chi ")) {
                GameMaster.getInstance().onCallChi(playerName, message.substring(4));
            }
            else if (message.equals("pon")) {
                GameMaster.getInstance().onCallPon(playerName);
            }
            else if (message.startsWith("kan ")) {
                GameMaster.getInstance().onCallKan(playerName, message.substring(4));
            }
            else if (message.equals("hu")) {
                GameMaster.getInstance().onComplete(playerName);
            }
            else if (message.equals("ron")) {
                GameMaster.getInstance().onCompleteRon(playerName);
            }
            else if (message.equals("tsumo")) {
                GameMaster.getInstance().onCompleteTsumo(playerName);
            }
            else if (message.equals("sr")) {
                GameMaster.getInstance().onRanking();
            }
            else if (message.equals("ss")) {
                GameMaster.getInstance().onStatistics(playerName, "");
            }
            else if (message.equals("sy")) {
                GameMaster.getInstance().onYaku(playerName, "");
            }
            else if (message.startsWith("ss ")) {
                GameMaster.getInstance().onStatistics(playerName, message.substring(3));
            }
            else if (message.startsWith("sy ")) {
                GameMaster.getInstance().onYaku(playerName, message.substring(3));
            }
            else if (message.startsWith("o ")) {
                GameMaster.getInstance().onOuts(message.substring(2));
            }
            else if (message.startsWith("w ")) {
                GameMaster.getInstance().onWatch(message.substring(2));
            }
            else if (message.equals("replay") || message.equals("replay chm") || message.equals("chm replay")) {
                GameMaster.getInstance().onReplayChm(playerName);
            }
            else if (message.equals("replay jpm") || message.equals("jpm replay")) {
                GameMaster.getInstance().onReplay(playerName);
            }
            else if (message.startsWith("replay ")) {
                GameMaster.getInstance().onReplay(playerName, message.substring(7));
            }
            else if (message.equals("download")) {
                new Thread() {
                    @Override
                    public void run() {
                        try {
                            IRCBOT.getInstance().println("---- 牌山を" + playerName + "に送信 ----");
                            event.getBot().dccSendFile(new File("./deck.bin"), event.getUser(), 180000);
                        }
                        catch (final Throwable e) {
                            // 何もしない
                        }
                    }
                }.start();

                new Thread() {
                    @Override
                    public void run() {
                        try {
                            IRCBOT.getInstance().println("---- プレイヤーテーブルを" + playerName + "に送信 ----");
                            event.getBot().dccSendFile(new File("./player_table.bin"), event.getUser(), 180000);
                        }
                        catch (final Throwable e) {
                            // 何もしない
                        }
                    }
                }.start();
            }
            else if (message.equals("help")) {
                final List<String> messageList =
                    Arrays.asList("ss [X] [開始値-終了値]：指定したプレイヤーのゲーム統計を表示",
                                  "sy [X] [開始値-終了値] [-c表示する役の最大数] [-p表示する役の最小点]：",
                                  "指定したプレイヤーの役のゲーム統計を表示   sr：ランキングを表示",
                                  "※ ss, syはXにa llと指定すると全員分を表示、その場合範囲指定は無効",
                                  "s, s chm：中国麻雀を開始   s jpm：日本麻雀を開始   e：終了",
                                  "replay, replay chm：中国麻雀でリプレイ   replay jpm：日本麻雀でリプレイ",
                                  "i：状態   r：捨て牌   d X：指定牌(ex.9p)を切る (X指定無し：ツモ切り、鳴きキャンセル)",
                                  "chi X：指定牌(ex.3p)を先頭牌としてチー   pon：ポン   kan X：指定牌でカン",
                                  "ron, hu：ロン   tsumo, hu：ツモ和了   u, undo：取り消し   h：コマンド履歴表示",
                                  "ra：他家を含む全ての捨て牌   w：指定牌の残り枚数の自動表示終了",
                                  "w X：指定牌の残り枚数の自動表示(複数指定可) ※ ドラ表示牌はカウント対象外(未実装)",
                                  "o X：指定牌の残り枚数(複数指定可) ※ ドラ表示牌はカウント対象外(未実装)",
                                  "7th：七対モード(手牌に1枚のみの牌の残り枚数を自動表示)切り替え(デフォルトはOFF)");
                IRCBOT.getInstance().println(messageList);
            }
            else if (message.equals("chm help")) {
                final List<String> messageList =
                    Arrays.asList("chm s：中国麻雀を開始   chm replay：中国麻雀でリプレイ");
                IRCBOT.getInstance().println(messageList);
            }
            else if (message.equals("jpm help")) {
                final List<String> messageList =
                    Arrays.asList("jpm s：日本麻雀を開始   jpm replay：日本麻雀でリプレイ");
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
     * 副露実況フラグに変換
     *
     * @param callTypeList 副露タイプリスト。
     * @return 副露実況フラグ。
     */
    protected EnumSet<AnnounceFlag> convertToCallAnnounceType(final List<CallType> callTypeList) {
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
     * ゲーム終了時の処理
     *
     * @param status ゲーム終了状態。
     */
    protected void onGameSet(final GameSetStatus status) {
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
    private static final EnumSet<AnnounceFlag> ANNOUNCE_FLAG_WATCHING_END =
        EnumSet.of(AnnounceFlag.WATCHING_END);
    private static final EnumSet<AnnounceFlag> ANNOUNCE_FLAG_SEVENTH =
        EnumSet.of(AnnounceFlag.SEVENTH);

    /**
     * 色付けフラグ
     */
    private static final char COLOR_FLAG = 3;

}

