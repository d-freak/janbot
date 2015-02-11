/**
 * ChmJanController.java
 */

package wiz.project.janbot.game;

import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Observer;

import wiz.project.jan.JanPai;
import wiz.project.jan.Wind;
import wiz.project.janbot.game.exception.BoneheadException;
import wiz.project.janbot.game.exception.InvalidInputException;
import wiz.project.janbot.game.exception.JanException;



/**
 * 中国麻雀コントローラ
 */
class ChmJanController implements JanController {
    
    /**
     * コンストラクタ
     */
    public ChmJanController() {
    }
    
    /**
     * コンストラクタ
     * 
     * @param observer 監視者。
     */
    public ChmJanController(final Observer observer) {
        if (observer != null) {
            synchronized (_GAME_INFO_LOCK) {
                _info.addObserver(observer);
            }
        }
    }
    
    
    
    /**
     * 副露
     */
    public void call(final String playerName, final CallType type, final JanPai target) throws JanException {
        if (playerName == null) {
            throw new NullPointerException("Player name is null.");
        }
        if (type == null) {
            throw new NullPointerException("Call type is null.");
        }
        if (playerName.isEmpty()) {
            throw new IllegalArgumentException("Player name is empty.");
        }
        if (!_onGame) {
            throw new JanException("Game is not started.");
        }
        
        synchronized (_GAME_INFO_LOCK) {
            if (!_info.isValidPlayer(playerName)) {
                throw new IllegalArgumentException("Inavlid player name - " + playerName);
            }
            if (_info.getRemainCount() == 0) {
                // ラス牌
                throw new BoneheadException("Can't call.");
            }
            
            // 打牌したプレイヤーの風を記録
            final Wind activeWind = _info.getActiveWind();
            try {
                // 副露宣言したプレイヤーをアクティブ化して判定
                _info.setActivePlayer(playerName);
                switch (type) {
                case CHI:
                    if (activeWind.getNext() != _info.getActiveWind()) {
                        throw new InvalidInputException("Can't chi.");
                    }
                    // TODO チー処理
                    // SoloJanController の callChi() をパクればOK？
//                  callChi(target);
                    break;
                case PON:
                    // TODO ポン処理
                    // SoloJanController の callPon() をパクればOK？
//                  callPon();
                    break;
                case KAN_LIGHT:
                    // TODO 明杠処理
                    // SoloJanController の callKanLight() をパクればOK？
//                  callKanLight(target);
                    break;
                case KAN_ADD:
                    // TODO 加杠処理
                    // SoloJanController の callKanAdd() をパクればOK？
//                  callKanAdd(target);
                    break;
                case KAN_DARK:
                    // TODO 暗杠処理
                    // SoloJanController の callKanDark() をパクればOK？
//                  callKanDark(target);
                    break;
                default:
                    throw new IllegalArgumentException("Invalid call type - " + type);
                }
            }
            catch (final Throwable e) {
                // 副露しない場合、アクティブプレイヤーを元に戻す
                _info.setActiveWind(activeWind);
                throw e;
            }
        }
    }
    
    /**
     * 和了 (ロン)
     */
    public void completeRon(final String playerName) throws JanException {
        if (playerName == null) {
            throw new NullPointerException("Player name is null.");
        }
        if (playerName.isEmpty()) {
            throw new IllegalArgumentException("Player name is empty.");
        }
        if (!_onGame) {
            throw new JanException("Game is not started.");
        }
        
        synchronized (_GAME_INFO_LOCK) {
            if (!_info.isValidPlayer(playerName)) {
                throw new IllegalArgumentException("Inavlid player name - " + playerName);
            }
            
            // 打牌したプレイヤーの風を記録
            final Wind activeWind = _info.getActiveWind();
            try {
                // ロン宣言したプレイヤーをアクティブ化して判定
                _info.setActivePlayer(playerName);
                
                // ロン対象牌を取得
                final JanPai discard = _info.getActiveDiscard();
                
                // TODO ロン判定
                // チョンボなら BoneheadException を投げる
                
                // ゲームセット
                _onGame = false;
                _info.notifyObservers(ANNOUNCE_FLAG_COMPLETE_RON);
            }
            catch (final Throwable e) {
                // 和了しない場合、アクティブプレイヤーを元に戻す
                _info.setActiveWind(activeWind);
                throw e;
            }
        }
    }
    
    /**
     * 和了 (ツモ)
     */
    public void completeTsumo() throws JanException {
        if (!_onGame) {
            throw new JanException("Game is not started.");
        }
        
        synchronized (_GAME_INFO_LOCK) {
            // TODO ツモ判定
            // チョンボなら BoneheadException を投げる
            
            // ゲームセット
            _onGame = false;
            _info.notifyObservers(ANNOUNCE_FLAG_COMPLETE_TSUMO);
        }
    }
    
    /**
     * 打牌 (ツモ切り)
     */
    public void discard() throws JanException {
        if (!_onGame) {
            throw new JanException("Game is not started.");
        }
        
        // TODO 打牌処理
        // SoloJanController からパクればOK？
    }
    
    /**
     * 打牌 (手出し)
     */
    public void discard(final JanPai target) throws JanException {
        if (target == null) {
            throw new NullPointerException("Discard target is null.");
        }
        if (!_onGame) {
            throw new JanException("Game is not started.");
        }
        
        // TODO 打牌処理
        // SoloJanController からパクればOK？
    }
    
    /**
     * ゲーム情報を取得
     */
    public JanInfo getGameInfo() {
        synchronized (_GAME_INFO_LOCK) {
            return _info.clone();
        }
    }
    
    /**
     * 次のプレイヤーの打牌へ
     */
    public void next() throws JanException {
        if (!_onGame) {
            throw new JanException("Game is not started.");
        }
        
        // TODO 打牌処理
        // SoloJanController からパクればOK？
    }
    
    /**
     * リーチ
     */
    public void richi(final JanPai target) throws JanException {
        // 上が未実装なのでここには来ない
    }
    
    /**
     * 開始
     */
    public void start(final List<JanPai> deck, final Map<Wind, Player> playerTable) throws JanException {
        if (deck == null) {
            throw new NullPointerException("Deck is null.");
        }
        if (playerTable == null) {
            throw new NullPointerException("Player table is null.");
        }
        if (deck.size() != (JanPai.values().length * 4)) {
            throw new IllegalArgumentException("Invalid deck size - " + deck.size());
        }
        if (playerTable.size() != 4) {
            throw new IllegalArgumentException("Invalid player table size - " + playerTable.size());
        }
        if (_onGame) {
            throw new JanException("Game is already started.");
        }
        
        synchronized (_GAME_INFO_LOCK) {
            _onGame = true;
            _info.clear();
            
            // 席決めと山積み
            _info.setFieldWind(Wind.TON);
            _info.setPlayerTable(playerTable);
            _info.setDeck(deck);
            
            // TODO ゲーム開始処理
            // 基本は SoloJanController から流用でOK
            // 王牌を生成しなかったり残り枚数が70じゃなかったり色々
        }
    }
    
    
    
    /**
     * 実況フラグ
     */
    private static final EnumSet<AnnounceFlag> ANNOUNCE_FLAG_COMPLETE_RON =
        EnumSet.of(AnnounceFlag.COMPLETE_RON, AnnounceFlag.FIELD, AnnounceFlag.URA_DORA, AnnounceFlag.RIVER_SINGLE, AnnounceFlag.HAND, AnnounceFlag.ACTIVE_DISCARD);
    private static final EnumSet<AnnounceFlag> ANNOUNCE_FLAG_COMPLETE_TSUMO =
        EnumSet.of(AnnounceFlag.COMPLETE_TSUMO, AnnounceFlag.FIELD, AnnounceFlag.URA_DORA, AnnounceFlag.RIVER_SINGLE, AnnounceFlag.HAND, AnnounceFlag.ACTIVE_TSUMO);
    private static final EnumSet<AnnounceFlag> ANNOUNCE_FLAG_HAND_TSUMO =
        EnumSet.of(AnnounceFlag.HAND, AnnounceFlag.ACTIVE_TSUMO);
    private static final EnumSet<AnnounceFlag> ANNOUNCE_FLAG_HAND_TSUMO_FIELD =
        EnumSet.of(AnnounceFlag.HAND, AnnounceFlag.ACTIVE_TSUMO, AnnounceFlag.FIELD);
    private static final EnumSet<AnnounceFlag> ANNOUNCE_FLAG_HAND_AFTER_CALL =
        EnumSet.of(AnnounceFlag.HAND, AnnounceFlag.AFTER_CALL);
    private static final EnumSet<AnnounceFlag> ANNOUNCE_FLAG_HAND_TSUMO_FIELD_AFTER_CALL =
        EnumSet.of(AnnounceFlag.HAND, AnnounceFlag.ACTIVE_TSUMO, AnnounceFlag.FIELD, AnnounceFlag.AFTER_CALL);
    
    
    
    /**
     * ロックオブジェクト
     */
    private final Object _GAME_INFO_LOCK = new Object();
    
    
    
    /**
     * 麻雀ゲーム情報
     */
    private JanInfo _info = new JanInfo();
    
    /**
     * ゲーム中か
     */
    private volatile boolean _onGame = false;
    
}

