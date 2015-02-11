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
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import wiz.io.serializer.Serializer;
import wiz.project.ircbot.IRCBOT;
import wiz.project.jan.JanPai;
import wiz.project.jan.Wind;
import wiz.project.jan.util.JanPaiUtil;
import wiz.project.janbot.game.exception.InvalidInputException;
import wiz.project.janbot.game.exception.JanException;



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
                _controller.call(playerName, CallType.CHI, convertStringToJanPai(target));
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
                _controller.call(playerName, CallType.KAN_LIGHT, targetPai);
            }
            else {
                if (info.getActiveHand().getMenZenMap().get(targetPai) < 3) {
                    // 加カン
                    _controller.call(playerName, CallType.KAN_ADD, targetPai);
                }
                else {
                    // 暗カン
                    _controller.call(playerName, CallType.KAN_DARK, targetPai);
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
                _controller.call(playerName, CallType.PON, null);
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
                _controller.completeRon(playerName);
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
                _controller.completeTsumo();
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
            _controller.next();
        }
    }
    
    /**
     * 打牌処理
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
            _controller.discard();
        }
    }
    
    /**
     * 打牌処理
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
            _controller.discard(targetPai);
        }
    }
    
    /**
     * 終了処理
     */
    public void onEnd() {
        synchronized (_STATUS_LOCK) {
            if (_status != GameStatus.IDLE) {
                _status = GameStatus.IDLE;
                IRCBOT.getInstance().println("--- 終了 ---");
            }
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
            info.addObserver(_announcer);
            info.notifyObservers(flagSet);
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
            _controller = createJanController(true);
            _controller.start(deck, playerTable);
        }
    }
    
    /**
     * リーチ処理
     * 
     * @param target 捨て牌。
     * @throws JanException ゲーム処理エラー。
     */
    public void onRichi(final String target) throws JanException {
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
            _controller.richi(targetPai);
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
            _controller = createJanController(true);
            _controller.start(deck, playerTable);
        }
    }
    
    /**
     * 開始処理 (対戦)
     * 
     * @param playerNameList プレイヤー名のリスト。
     * @throws JanException ゲーム処理エラー。
     */
    public void onStartVS(final List<String> playerNameList) throws JanException {
        if (playerNameList == null) {
            throw new NullPointerException("Player name list is null.");
        }
        if (playerNameList.isEmpty()) {
            throw new IllegalArgumentException("Player name list is empty.");
        }
        
        // 開始済み判定
        synchronized (_STATUS_LOCK) {
            if (!_status.isIdle()) {
                IRCBOT.getInstance().println("--- Already started ---");
                return;
            }
            _status = GameStatus.PLAYING_VS;
        }
        
        // 牌山生成と席決め
        final List<JanPai> deck = createDeck();
        final Map<Wind, Player> playerTable = createPlayerTable(playerNameList);
        
        // ゲーム開始
        synchronized (_CONTROLLER_LOCK) {
            _controller = createJanController(false);
            _controller.start(deck, playerTable);
        }
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
     * @param solo ソロプレイか。
     * @return 麻雀コントローラ。
     */
    private JanController createJanController(final boolean solo) {
        if (solo) {
            return new SoloJanController(_announcer);
        }
        else {
            return new VSJanController();
        }
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
     * 自分自身のインスタンス
     */
    private static final GameMaster INSTANCE = new GameMaster();
    
    /**
     * 保存パス
     */
    private static final String DECK_SAVE_PATH         = "./deck.bin";
    private static final String PLAYER_TABLE_SAVE_PATH = "./player_table.bin";
    
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
     * ゲームコントローラ
     */
    private JanController _controller = null;
    
    /**
     * ゲーム実況者
     */
    private GameAnnouncer _announcer = new GameAnnouncer();
    
    /**
     * ゲームの状態
     */
    private GameStatus _status = GameStatus.IDLE;
    
}

