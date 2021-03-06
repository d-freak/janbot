/**
 * GameMaster.java
 *
 * @author Yuki
 */

package wiz.project.janbot.game;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import wiz.io.serializer.Serializer;
import wiz.project.ircbot.IRCBOT;
import wiz.project.jan.JanPai;
import wiz.project.jan.Wind;
import wiz.project.jan.util.JanPaiUtil;
import wiz.project.janbot.game.exception.CallableException;
import wiz.project.janbot.game.exception.InvalidInputException;
import wiz.project.janbot.game.exception.JanException;
import wiz.project.janbot.statistics.StatisticsParam;
import wiz.project.janbot.statistics.YakuParam;



/**
 * ゲーム管理
 */
public final class GameMaster {

    /**
     * コンストラクタを自分自身に限定許可
     */
    private GameMaster() {
    }



    /**
     * インスタンスを取得
     *
     * @return インスタンス。
     */
    public static GameMaster getInstance() {
        return INSTANCE;
    }



    /**
     * ゲームの状態を取得
     *
     * @return ゲームの状態。
     */
    public GameStatus getStatus() {
        synchronized (_STATUS_LOCK) {
            return _status;
        }
    }

    /**
     * チー処理
     *
     * @param playerName プレイヤー名。
     * @param target 先頭牌。
     * @throws JanException ゲーム処理エラー。
     */
    public void onCallChi(final String playerName, final String target) throws JanException {
        if (playerName == null) {
            throw new NullPointerException("Player name is null.");
        }
        if (target == null) {
            throw new NullPointerException("Head pai is null.");
        }
        if (playerName.isEmpty()) {
            throw new NullPointerException("Player name is empty.");
        }

        // 開始判定
        synchronized (_STATUS_LOCK) {
            if (_status.isIdle()) {
                IRCBOT.getInstance().println("--- Not started ---");
                return;
            }
        }

        synchronized (_CONTROLLER_LOCK) {
            if (!_controller.getGameInfo().isActivePlayer(playerName)) {
                _historyList.add(new CommandHistory(HistoryType.CHI, _controller.getGameInfo(), target));

                try {
                    _controller.call(playerName, CallType.CHI, convertStringToJanPai(target));
                }
                catch (final JanException e) {
                    _historyList.pollLast();
                    throw e;
                }
            }
        }
    }

    /**
     * カン処理
     *
     * @param playerName プレイヤー名。
     * @param target 対象牌。
     * @throws JanException ゲーム処理エラー。
     */
    public void onCallKan(final String playerName, final String target) throws JanException {
        if (playerName == null) {
            throw new NullPointerException("Player name is null.");
        }
        if (target == null) {
            throw new NullPointerException("Call target is null.");
        }
        if (playerName.isEmpty()) {
            throw new NullPointerException("Player name is empty.");
        }

        // 開始判定
        synchronized (_STATUS_LOCK) {
            if (_status.isIdle()) {
                IRCBOT.getInstance().println("--- Not started ---");
                return;
            }
        }

        synchronized (_CONTROLLER_LOCK) {
            final JanPai targetPai = convertStringToJanPai(target);
            final JanInfo info = _controller.getGameInfo();
            if (!info.isActivePlayer(playerName)) {
                // 大明カン
                _historyList.add(new CommandHistory(HistoryType.KAN_LIGHT, _controller.getGameInfo(), target));

                try {
                    _controller.call(playerName, CallType.KAN_LIGHT, targetPai);
                }
                catch (final JanException e) {
                    _historyList.pollLast();
                    throw e;
                }
            }
            else {
                if (info.getActiveHand().getMenZenMap().get(targetPai) < 3) {
                    // 加カン
                    _historyList.add(new CommandHistory(HistoryType.KAN_ADD, _controller.getGameInfo(), target));

                    try {
                        _controller.call(playerName, CallType.KAN_ADD, targetPai);
                    }
                    catch (final JanException e) {
                        _historyList.pollLast();
                        throw e;
                    }
                }
                else {
                    // 暗カン
                    _historyList.add(new CommandHistory(HistoryType.KAN_DARK, _controller.getGameInfo(), target));

                    try {
                        _controller.call(playerName, CallType.KAN_DARK, targetPai);
                    }
                    catch (final JanException e) {
                        _historyList.pollLast();
                        throw e;
                    }
                }
            }
        }
    }

    /**
     * ポン処理
     *
     * @param playerName プレイヤー名。
     * @throws JanException ゲーム処理エラー。
     */
    public void onCallPon(final String playerName) throws JanException {
        if (playerName == null) {
            throw new NullPointerException("Player name is null.");
        }
        if (playerName.isEmpty()) {
            throw new NullPointerException("Player name is empty.");
        }

        // 開始判定
        synchronized (_STATUS_LOCK) {
            if (_status.isIdle()) {
                IRCBOT.getInstance().println("--- Not started ---");
                return;
            }
        }

        synchronized (_CONTROLLER_LOCK) {
            if (!_controller.getGameInfo().isActivePlayer(playerName)) {
                _historyList.add(new CommandHistory(HistoryType.PON, _controller.getGameInfo()));

                try {
                    _controller.call(playerName, CallType.PON, null);
                }
                catch (final JanException e) {
                    _historyList.pollLast();
                    throw e;
                }
            }
        }
    }

    /**
     * 和了処理
     *
     * @param playerName プレイヤー名。
     * @throws JanException ゲーム処理エラー。
     */
    public void onComplete(final String playerName) throws JanException {
        synchronized (_CONTROLLER_LOCK) {
            final JanInfo info = _controller.getGameInfo();
            
            if (info.getConfirmMode()) {
                onCompleteRon(playerName);
            }
            else {
                onCompleteTsumo(playerName);
            }
        }
    }

    /**
     * ロン和了処理
     *
     * @param playerName プレイヤー名。
     * @throws JanException ゲーム処理エラー。
     */
    public void onCompleteRon(final String playerName) throws JanException {
        if (playerName == null) {
            throw new NullPointerException("Player name is null.");
        }
        if (playerName.isEmpty()) {
            throw new NullPointerException("Player name is empty.");
        }

        // 開始判定
        synchronized (_STATUS_LOCK) {
            if (_status.isIdle()) {
                IRCBOT.getInstance().println("--- Not started ---");
                return;
            }
        }

        synchronized (_CONTROLLER_LOCK) {
            if (!_controller.getGameInfo().isActivePlayer(playerName)) {
                _historyList.add(new CommandHistory(HistoryType.RON, _controller.getGameInfo()));

                try {
                    _controller.completeRon(playerName);
                }
                catch (final JanException e) {
                    _historyList.pollLast();
                    throw e;
                }
            }
        }
    }

    /**
     * ツモ和了処理
     *
     * @param playerName プレイヤー名。
     * @throws JanException ゲーム処理エラー。
     */
    public void onCompleteTsumo(final String playerName) throws JanException {
        if (playerName == null) {
            throw new NullPointerException("Player name is null.");
        }
        if (playerName.isEmpty()) {
            throw new NullPointerException("Player name is empty.");
        }

        // 開始判定
        synchronized (_STATUS_LOCK) {
            if (_status.isIdle()) {
                IRCBOT.getInstance().println("--- Not started ---");
                return;
            }
        }

        synchronized (_CONTROLLER_LOCK) {
            if (_controller.getGameInfo().isActivePlayer(playerName)) {
                _historyList.add(new CommandHistory(HistoryType.TSUMO, _controller.getGameInfo()));

                try {
                    _controller.completeTsumo();
                }
                catch (final JanException e) {
                    _historyList.pollLast();
                    throw e;
                }
            }
        }
    }

    /**
     * 副露せずに続行
     *
     * @throws JanException ゲーム処理エラー。
     */
    public void onContinue() throws JanException {
        // 開始判定
        synchronized (_STATUS_LOCK) {
            if (_status.isIdle()) {
                IRCBOT.getInstance().println("--- Not started ---");
                return;
            }
        }

        synchronized (_CONTROLLER_LOCK) {
            _historyList.add(new CommandHistory(HistoryType.CONTINUE, _controller.getGameInfo()));

            try {
                _controller.next();
            }
            catch (final CallableException e) {
                throw e;
            }
            catch (final JanException e) {
                _historyList.pollLast();
                throw e;
            }
        }
    }

    /**
     * 打牌処理 (ツモ切り)
     *
     * @throws JanException ゲーム処理エラー。
     */
    public void onDiscard() throws JanException {
        // 開始判定
        synchronized (_STATUS_LOCK) {
            if (_status.isIdle()) {
                IRCBOT.getInstance().println("--- Not started ---");
                return;
            }
        }

        synchronized (_CONTROLLER_LOCK) {
            _historyList.add(new CommandHistory(HistoryType.DISCARD_TSUMO, _controller.getGameInfo()));

            try {
                _controller.discard();
            }
            catch (final CallableException e) {
                throw e;
            }
            catch (final JanException e) {
                _historyList.pollLast();
                throw e;
            }
        }
    }

    /**
     * 打牌処理 (手出し)
     *
     * @param target 捨て牌。
     * @throws JanException ゲーム処理エラー。
     */
    public void onDiscard(final String target) throws JanException {
        if (target == null) {
            throw new NullPointerException("Discard target is null.");
        }

        // 開始判定
        synchronized (_STATUS_LOCK) {
            if (_status.isIdle()) {
                IRCBOT.getInstance().println("--- Not started ---");
                return;
            }
        }

        if (target.isEmpty()) {
            throw new InvalidInputException("Discard target is empty.");
        }
        final JanPai targetPai = convertStringToJanPai(target);
        synchronized (_CONTROLLER_LOCK) {
            _historyList.add(new CommandHistory(HistoryType.DISCARD, _controller.getGameInfo(), target));

            try {
                _controller.discard(targetPai);
            }
            catch (final CallableException e) {
                throw e;
            }
            catch (final JanException e) {
                _historyList.pollLast();
                throw e;
            }
        }
    }

    /**
     * 打牌処理 (ツモ切り)か副露せずに続行
     *
     * @throws JanException ゲーム処理エラー。
     */
    public void onDiscardOrContinue() throws JanException {
        synchronized (_CONTROLLER_LOCK) {
            final JanInfo info = _controller.getGameInfo();
            
            if (info.getConfirmMode()) {
                onContinue();
            }
            else {
                onDiscard();
            }
        }
    }

    /**
     * 終了処理
     */
    public void onEnd() {
        synchronized (_STATUS_LOCK) {
            if (_status.isIdle()) {
                return;
            }
            _status = GameStatus.IDLE;

            synchronized (_CONTROLLER_LOCK) {
                final JanInfo info = _controller.getGameInfo();
                info.addObserver(_announcer);
                info.notifyObservers(AnnounceFlag.GAME_END);
            }
        }
    }

    /**
     * コマンド履歴表示
     *
     * @throws JanException ゲーム処理エラー。
     */
    public void onHistory() throws JanException {
        synchronized (_CONTROLLER_LOCK) {
            final JanInfo info = _controller.getGameInfo();
            final HistoryParam param = new HistoryParam(_historyList);
            info.addObserver(_announcer);
            info.notifyObservers(param);
        }
    }

    /**
     * 情報表示
     *
     * @param flagSet 情報表示フラグ。
     */
    public void onInfo(final EnumSet<AnnounceFlag> flagSet) {
        if (flagSet == null) {
            throw new NullPointerException("Announce flag is null.");
        }

        // 開始判定
        synchronized (_STATUS_LOCK) {
            if (_status.isIdle()) {
                IRCBOT.getInstance().println("--- Not started ---");
                return;
            }
        }

        synchronized (_CONTROLLER_LOCK) {
            final JanInfo info = _controller.getGameInfo();

            if (info.getConfirmMode()) {
                flagSet.add(AnnounceFlag.CONFIRM);
            }
            info.addObserver(_announcer);
            info.notifyObservers(flagSet);
        }
    }

    /**
     * 指定牌の残り枚数表示
     *
     * @param target 指定牌。
     * @throws JanException ゲーム処理エラー。
     */
    public void onOuts(final String target) throws JanException {
        if (target == null) {
            throw new NullPointerException("Outs target is null.");
        }

        // 開始判定
        synchronized (_STATUS_LOCK) {
            if (_status.isIdle()) {
                IRCBOT.getInstance().println("--- Not started ---");
                return;
            }
        }

        if (target.isEmpty()) {
            throw new InvalidInputException("Outs target is empty.");
        }
        final List<JanPai> paiList = new ArrayList<>();
        for (final String string : target.split(" ")) {
            try {
                paiList.add(convertStringToJanPai(string));
            }
            catch (final InvalidInputException e) {
                // 指定ミスに対しては何もせず継続
            }
        }

        if (paiList.isEmpty()) {
            throw new InvalidInputException("Outs target JanPai is empty.");
        }
        synchronized (_CONTROLLER_LOCK) {
            final JanInfo info = _controller.getGameInfo();
            final EnumSet<AnnounceFlag> flagSet = EnumSet.of(AnnounceFlag.OUTS);

            if (info.getConfirmMode()) {
                flagSet.add(AnnounceFlag.CONFIRM);
            }
            final AnnounceParam param = new AnnounceParam(flagSet, paiList);
            info.addObserver(_announcer);
            info.notifyObservers(param);
        }
    }

    /**
     * ランキングの表示
     *
     * @throws JanException ゲーム処理エラー。
     */
    public void onRanking() throws JanException {
        synchronized (_CONTROLLER_LOCK) {
            final JanInfo info = _controller.getGameInfo();
            info.addObserver(_announcer);
            info.notifyObservers(AnnounceFlag.RANKING);
        }
    }

    /**
     * リプレイ処理
     *
     * @param playerName プレイヤー名。
     * @throws JanException ゲーム処理エラー。
     * @throws IOException ファイル入出力に失敗。
     */
    @SuppressWarnings("unchecked")
    public void onReplay(final String playerName) throws JanException, IOException {
        if (playerName == null) {
            throw new NullPointerException("Player name is null.");
        }
        if (playerName.isEmpty()) {
            throw new IllegalArgumentException("Player name is empty.");
        }

        // 開始済み判定
        synchronized (_STATUS_LOCK) {
            if (!_status.isIdle()) {
                IRCBOT.getInstance().println("--- Already started ---");
                return;
            }
            _status = GameStatus.PLAYING_SOLO;
        }

        if (!Files.exists(Paths.get(DECK_SAVE_PATH)) ||
            !Files.exists(Paths.get(PLAYER_TABLE_SAVE_PATH))) {
            IRCBOT.getInstance().println("--- Replay data is not found ---");
            return;
        }

        // 牌山と席順をロード
        final List<JanPai> deck = (List<JanPai>)Serializer.read(DECK_SAVE_PATH);
        final Map<Wind, Player> playerTable = (Map<Wind, Player>)Serializer.read(PLAYER_TABLE_SAVE_PATH);

        // プレイヤー名を差し替え
        final Wind playerWind = getPlayerWind(playerTable);
        playerTable.put(playerWind, new Player(playerName, PlayerType.HUMAN));

        // ゲーム開始
        synchronized (_CONTROLLER_LOCK) {
            _historyList.clear();
            _controller = createJanController();
            _historyList.add(new CommandHistory(HistoryType.JPM, _controller.getGameInfo()));

            try {
                _controller.start(deck, playerTable);
            }
            catch (final CallableException e) {
                throw e;
            }
            catch (final JanException e) {
                _historyList.pollLast();
                throw e;
            }
        }
    }

    /**
     * リプレイ処理 (中国麻雀)
     *
     * @param playerName プレイヤー名。
     * @throws JanException ゲーム処理エラー。
     * @throws IOException ファイル入出力に失敗。
     */
    @SuppressWarnings("unchecked")
    public void onReplayChm(final String playerName) throws JanException, IOException {
        if (playerName == null) {
            throw new NullPointerException("Player name is null.");
        }
        if (playerName.isEmpty()) {
            throw new IllegalArgumentException("Player name is empty.");
        }

        // 開始済み判定
        synchronized (_STATUS_LOCK) {
            if (!_status.isIdle()) {
                IRCBOT.getInstance().println("--- Already started ---");
                return;
            }
            _status = GameStatus.PLAYING_SOLO;
        }

        if (!Files.exists(Paths.get(DECK_SAVE_PATH)) ||
            !Files.exists(Paths.get(PLAYER_TABLE_SAVE_PATH))) {
            IRCBOT.getInstance().println("--- Replay data is not found ---");
            return;
        }

        // 牌山と席順をロード
        final List<JanPai> deck = (List<JanPai>)Serializer.read(DECK_SAVE_PATH);
        final Map<Wind, Player> playerTable = (Map<Wind, Player>)Serializer.read(PLAYER_TABLE_SAVE_PATH);

        // プレイヤー名を差し替え
        final Wind playerWind = getPlayerWind(playerTable);
        playerTable.put(playerWind, new Player(playerName, PlayerType.HUMAN));

        // ゲーム開始
        synchronized (_CONTROLLER_LOCK) {
            _historyList.clear();
            _controller = createChmJanController();
            _historyList.add(new CommandHistory(HistoryType.CHM, _controller.getGameInfo()));

            try {
                _controller.start(deck, playerTable);
            }
            catch (final CallableException e) {
                throw e;
            }
            catch (final JanException e) {
                _historyList.pollLast();
                throw e;
            }
        }
    }

    /**
     * リプレイ処理
     *
     * @param playerName プレイヤー名。
     * @param gameCode ゲームコード。
     * @throws JanException ゲーム処理エラー。
     * @throws IOException ファイル入出力に失敗。
     */
    public void onReplay(final String playerName, final String gameCode) throws JanException, IOException {
        if (playerName == null) {
            throw new NullPointerException("Player name is null.");
        }
        if (playerName.isEmpty()) {
            throw new IllegalArgumentException("Player name is empty.");
        }

        // TODO ゲーム指定リプレイ
        onReplay(playerName);
    }

    /**
     * 開始処理 (ソロ)
     *
     * @param playerName プレイヤー名。
     * @throws JanException ゲーム処理エラー。
     * @throws IOException ファイル入出力に失敗。
     */
    public void onStartSolo(final String playerName) throws JanException, IOException {
        if (playerName == null) {
            throw new NullPointerException("Player name is null.");
        }
        if (playerName.isEmpty()) {
            throw new IllegalArgumentException("Player name is empty.");
        }

        // 開始済み判定
        synchronized (_STATUS_LOCK) {
            if (!_status.isIdle()) {
                IRCBOT.getInstance().println("--- Already started ---");
                return;
            }
            _status = GameStatus.PLAYING_SOLO;
        }

        // 牌山生成と席決め
        final List<JanPai> deck = createDeck();
        final Map<Wind, Player> playerTable = createPlayerTable(Arrays.asList(playerName));

        // 保存 (リプレイ用)
        Serializer.writeOverwrite(deck, DECK_SAVE_PATH);
        Serializer.writeOverwrite(playerTable, PLAYER_TABLE_SAVE_PATH);

        // ゲーム開始
        synchronized (_CONTROLLER_LOCK) {
            _historyList.clear();
            _controller = createJanController();
            _historyList.add(new CommandHistory(HistoryType.JPM, _controller.getGameInfo()));

            try {
                _controller.start(deck, playerTable);
            }
            catch (final CallableException e) {
                throw e;
            }
            catch (final JanException e) {
                _historyList.pollLast();
                throw e;
            }
        }
    }

    /**
     * 開始処理 (中国麻雀・ソロ)
     *
     * @param playerName プレイヤー名。
     * @throws JanException ゲーム処理エラー。
     * @throws IOException ファイル入出力に失敗。
     */
    public void onStartChmSolo(final String playerName) throws JanException, IOException {
        if (playerName == null) {
            throw new NullPointerException("Player name is null.");
        }
        if (playerName.isEmpty()) {
            throw new IllegalArgumentException("Player name is empty.");
        }

        // 開始済み判定
        synchronized (_STATUS_LOCK) {
            if (!_status.isIdle()) {
                IRCBOT.getInstance().println("--- Already started ---");
                return;
            }
            _status = GameStatus.PLAYING_SOLO;
        }

        // 牌山生成と席決め
        final List<JanPai> deck = createDeck();
        final Map<Wind, Player> playerTable = createPlayerTable(Arrays.asList(playerName));

        // 保存 (リプレイ用)
        Serializer.writeOverwrite(deck, DECK_SAVE_PATH);
        Serializer.writeOverwrite(playerTable, PLAYER_TABLE_SAVE_PATH);

        // ゲーム開始
        synchronized (_CONTROLLER_LOCK) {
            _historyList.clear();
            _controller = createChmJanController();
            _historyList.add(new CommandHistory(HistoryType.CHM, _controller.getGameInfo()));

            try {
                _controller.start(deck, playerTable);
            }
            catch (final CallableException e) {
                throw e;
            }
            catch (final JanException e) {
                _historyList.pollLast();
                throw e;
            }
        }
    }

    /**
     * ゲーム統計の表示
     *
     * @param name プレイヤー名。
     * @param option オプション。
     * @throws JanException ゲーム処理エラー。
     */
    public void onStatistics(final String name, final String option) throws JanException {
        if (option == null) {
            throw new NullPointerException("Statistics target is null.");
        }

        synchronized (_CONTROLLER_LOCK) {
            final JanInfo info = _controller.getGameInfo();
            final StatisticsParam param = new StatisticsParam(name, option);
            info.addObserver(_announcer);
            info.notifyObservers(param);
        }
    }

    /**
     * リプレイ処理 (中国麻雀)
     *
     * @param playerName プレイヤー名。
     * @throws JanException ゲーム処理エラー。
     * @throws IOException ファイル入出力に失敗。
     */
    @SuppressWarnings("unchecked")
    public void onTestChm(final String playerName) throws JanException, IOException {
        if (playerName == null) {
            throw new NullPointerException("Player name is null.");
        }
        if (playerName.isEmpty()) {
            throw new IllegalArgumentException("Player name is empty.");
        }

        // 開始済み判定
        synchronized (_STATUS_LOCK) {
            if (!_status.isIdle()) {
                IRCBOT.getInstance().println("--- Already started ---");
                return;
            }
            _status = GameStatus.PLAYING_SOLO;
        }

        if (!Files.exists(Paths.get(TEST_DECK_SAVE_PATH)) ||
            !Files.exists(Paths.get(TEST_PLAYER_TABLE_SAVE_PATH))) {
            IRCBOT.getInstance().println("--- Test data is not found ---");
            return;
        }

        // 牌山と席順をロード
        final List<JanPai> deck = (List<JanPai>)Serializer.read(TEST_DECK_SAVE_PATH);
        final Map<Wind, Player> playerTable = (Map<Wind, Player>)Serializer.read(TEST_PLAYER_TABLE_SAVE_PATH);

        // プレイヤー名を差し替え
        final Wind playerWind = getPlayerWind(playerTable);
        playerTable.put(playerWind, new Player(playerName, PlayerType.HUMAN));

        // ゲーム開始
        synchronized (_CONTROLLER_LOCK) {
            _controller = createChmJanController();
            _controller.start(deck, playerTable);
        }
    }

    /**
     * 取り消し
     *
     * @param name プレイヤー名。
     * @throws JanException ゲーム処理エラー。
     * @throws IOException ファイル入出力に失敗。
     */
    public void onUndo(final String name) throws JanException, IOException {
        // 開始判定
        synchronized (_STATUS_LOCK) {
            if (_status.isIdle()) {
                IRCBOT.getInstance().println("--- Not started ---");
                return;
            }
        }

        synchronized (_CONTROLLER_LOCK) {
            final boolean onGame = _controller.getOnGame();

            if (!onGame) {
                throw new JanException("Game is not started.");
            }
            undo(name);
        }
    }

    /**
     * 指定牌の残り枚数の自動表示
     *
     * @param target 指定牌。
     * @throws JanException ゲーム処理エラー。
     */
    public void onWatch(final String target) throws JanException {
        if (target == null) {
            throw new NullPointerException("Watch target is null.");
        }

        // 開始判定
        synchronized (_STATUS_LOCK) {
            if (_status.isIdle()) {
                IRCBOT.getInstance().println("--- Not started ---");
                return;
            }
        }

        if (target.isEmpty()) {
            throw new InvalidInputException("Watch target is empty.");
        }
        final List<JanPai> paiList = new ArrayList<>();
        for (final String string : target.split(" ")) {
            try {
                paiList.add(convertStringToJanPai(string));
            }
            catch (final InvalidInputException e) {
                // 指定ミスに対しては何もせず継続
            }
        }

        if (paiList.isEmpty()) {
            throw new InvalidInputException("Watch target JanPai is empty.");
        }
        synchronized (_CONTROLLER_LOCK) {
            _controller.watch(paiList);
        }
    }

    /**
     * 役のゲーム統計を表示
     *
     * @param name プレイヤー名。
     * @param option オプション。
     * @throws JanException ゲーム処理エラー。
     */
    public void onYaku(final String name, final String option) throws JanException {
        if (option == null) {
            throw new NullPointerException("Statistics target is null.");
        }

        synchronized (_CONTROLLER_LOCK) {
            final JanInfo info = _controller.getGameInfo();
            final YakuParam param = new YakuParam(name, option);
            info.addObserver(_announcer);
            info.notifyObservers(param);
        }
    }



    /**
     * 中国麻雀コントローラを生成
     *
     * @return 中国麻雀コントローラ。
     */
    private JanController createChmJanController() {
        _announcer.setIsChm(true);
        return new ChmJanController(_announcer);
    }

    /**
     * プレイヤーの風を取得
     *
     * @param playerTable プレイヤーテーブル。
     * @return プレイヤーの風。
     */
    private Wind getPlayerWind(final Map<Wind, Player> playerTable) {
        for (final Map.Entry<Wind, Player> entry : playerTable.entrySet()) {
            if (entry.getValue().getType() != PlayerType.COM) {
                return entry.getKey();
            }
        }
        throw new InternalError();
    }



    /**
     * 文字列を牌に変換
     *
     * @param source 変換元。
     * @return 変換結果。
     * @throws InvalidInputException 不正な入力。
     */
    private JanPai convertStringToJanPai(final String source) throws InvalidInputException {
        switch (source) {
        case "1m":
            return JanPai.MAN_1;
        case "2m":
            return JanPai.MAN_2;
        case "3m":
            return JanPai.MAN_3;
        case "4m":
            return JanPai.MAN_4;
        case "5m":
            return JanPai.MAN_5;
        case "6m":
            return JanPai.MAN_6;
        case "7m":
            return JanPai.MAN_7;
        case "8m":
            return JanPai.MAN_8;
        case "9m":
            return JanPai.MAN_9;
        case "1p":
            return JanPai.PIN_1;
        case "2p":
            return JanPai.PIN_2;
        case "3p":
            return JanPai.PIN_3;
        case "4p":
            return JanPai.PIN_4;
        case "5p":
            return JanPai.PIN_5;
        case "6p":
            return JanPai.PIN_6;
        case "7p":
            return JanPai.PIN_7;
        case "8p":
            return JanPai.PIN_8;
        case "9p":
            return JanPai.PIN_9;
        case "1s":
            return JanPai.SOU_1;
        case "2s":
            return JanPai.SOU_2;
        case "3s":
            return JanPai.SOU_3;
        case "4s":
            return JanPai.SOU_4;
        case "5s":
            return JanPai.SOU_5;
        case "6s":
            return JanPai.SOU_6;
        case "7s":
            return JanPai.SOU_7;
        case "8s":
            return JanPai.SOU_8;
        case "9s":
            return JanPai.SOU_9;
        case "東":
        case "ton":
        case "dong":
            return JanPai.TON;
        case "南":
        case "nan":
            return JanPai.NAN;
        case "西":
        case "sha":
        case "sya":
        case "xi":
            return JanPai.SHA;
        case "北":
        case "pei":
        case "pe":
        case "bei":
            return JanPai.PEI;
        case "白":
        case "haku":
        case "bai":
            return JanPai.HAKU;
        case "發":
        case "hatu":
        case "hatsu":
        case "fa":
            return JanPai.HATU;
        case "中":
        case "chun":
        case "ch":
        case "zhong":
            return JanPai.CHUN;
        default:
            throw new InvalidInputException("Invalid jan pai - " + source);
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
    private JanController createJanController() {
        _announcer.setIsChm(false);
        return new SoloJanController(_announcer);
    }

    /**
     * プレイヤーテーブルを生成
     *
     * @param playerNameList 参加プレイヤー名のリスト。
     * @return プレイヤーテーブル。
     */
    private Map<Wind, Player> createPlayerTable(final List<String> playerNameList) {
        // 風をシャッフル
        final List<Wind> windList = new ArrayList<>(Arrays.asList(Wind.values()));
        Collections.shuffle(windList, new SecureRandom());

        // プレイヤーを格納
        final Map<Wind, Player> playerTable = new TreeMap<>();
        for (final String playerName : playerNameList) {
            playerTable.put(windList.remove(0), new Player(playerName, PlayerType.HUMAN));
        }

        // 4人になるまでNPCで埋める
        final int limitCOM = 4 - playerNameList.size();
        for (int i = 0; i < limitCOM; i++) {
            playerTable.put(windList.remove(0), NPC_LIST.get(i));
        }
        return playerTable;
    }

    /**
     * 取り消し
     *
     * @param name プレイヤー名。
     * @throws JanException ゲーム処理エラー。
     * @throws IOException ファイル入出力に失敗。
     */
    private void undo(final String name) throws JanException, IOException {
        final int size = _historyList.size();

        if (size <= 1) {
            IRCBOT.getInstance().println("--- No command ---");
            return;
        }
        _historyList.pollLast();

        final CommandHistory history = _historyList.pollLast();
        final JanInfo info = history.getJanInfo();

        _controller.setGameInfo(info);

        final HistoryType historyType = history.getHistoryType();
        final String pai = history.getJanPai().replaceAll("[\\[\\]]", "");

        synchronized (_STATUS_LOCK) {
            if (historyType == HistoryType.JPM || historyType == HistoryType.CHM) {
                _status = GameStatus.IDLE;
            }
            else {
                _status = GameStatus.PLAYING_SOLO;
            }
        }

        switch (historyType) {
        case JPM:
            onReplay(name);
            break;
        case CHM:
            onReplayChm(name);
            break;
        case DISCARD_TSUMO:
            onDiscard();
            break;
        case DISCARD_TSUMO_OR_CONTINUE:
            onDiscardOrContinue();
            break;
        case DISCARD:
            onDiscard(pai);
            break;
        case CONTINUE:
            onContinue();
            break;
        case COMPLETE:
            onComplete(name);
            break;
        case RON:
            onCompleteRon(name);
            break;
        case TSUMO:
            onCompleteTsumo(name);
            break;
        case CHI:
            onCallChi(name, pai);
            break;
        case PON:
            onCallPon(name);
            break;
        case KAN_LIGHT:
        case KAN_ADD:
        case KAN_DARK:
            onCallKan(name, pai);
            break;
        }
    }



    /**
     * 自分自身のインスタンス
     */
    private static final GameMaster INSTANCE = new GameMaster();

    /**
     * 保存パス
     */
    private static final String DECK_SAVE_PATH         = "./deck.bin";
    private static final String PLAYER_TABLE_SAVE_PATH = "./player_table.bin";
    private static final String TEST_DECK_SAVE_PATH = "./test/deck.bin";
    private static final String TEST_PLAYER_TABLE_SAVE_PATH = "./test/player_table.bin";

    /**
     * NPCリスト
     */
    private static final List<Player> NPC_LIST =
        Collections.unmodifiableList(Arrays.asList(new Player("COM_01", PlayerType.COM),
                                                   new Player("COM_02", PlayerType.COM),
                                                   new Player("COM_03", PlayerType.COM)));



    /**
     * ロックオブジェクト (ゲームコントローラ)
     */
    private final Object _CONTROLLER_LOCK = new Object();

    /**
     * ロックオブジェクト (ゲームの状態)
     */
    private final Object _STATUS_LOCK = new Object();



    /**
     * ゲーム実況者
     */
    private GameAnnouncer _announcer = new GameAnnouncer();

    /**
     * ゲームコントローラ
     */
    private JanController _controller = null;

    /**
     * コマンド履歴リスト
     */
    private LinkedList<CommandHistory> _historyList = new LinkedList<>();

    /**
     * ゲームの状態
     */
    private GameStatus _status = GameStatus.IDLE;

}

