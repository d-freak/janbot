/**
 * VSJanController.java
 */

package wiz.project.janbot.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import wiz.project.jan.Hand;
import wiz.project.jan.JanPai;
import wiz.project.jan.Wind;
import wiz.project.jan.util.HandCheckUtil;
import wiz.project.jan.util.JanPaiUtil;
import wiz.project.janbot.game.exception.CallableException;
import wiz.project.janbot.game.exception.GameSetException;
import wiz.project.janbot.game.exception.JanException;



/**
 * 麻雀コントローラ (対戦)
 */
class VSJanController implements JanController {
    
    /**
     * コンストラクタ
     */
    public VSJanController() {
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
        }
    }
    
    /**
     * 打牌 (ツモ切り)
     */
    public void discard() throws JanException {
        if (!_onGame) {
            throw new JanException("Game is not started.");
        }
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
        
        synchronized (_GAME_INFO_LOCK) {
        }
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
        
        synchronized (_GAME_INFO_LOCK) {
        }
    }
    
    /**
     * リーチ
     */
    public void richi(final JanPai target) throws JanException {
        if (!_onGame) {
            throw new JanException("Game is not started.");
        }
        
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
            
            // 王牌を生成
            final int deckSize = deck.size();
            _info.setWanPai(new WanPai(new ArrayList<>(deck.subList(deckSize - 14, deckSize))));
            
            // 配牌
            _info.setHand(Wind.TON, new Hand(new ArrayList<JanPai>(deck.subList( 0, 13))));
            _info.setHand(Wind.NAN, new Hand(new ArrayList<JanPai>(deck.subList(13, 26))));
            _info.setHand(Wind.SHA, new Hand(new ArrayList<JanPai>(deck.subList(26, 39))));
            _info.setHand(Wind.PEI, new Hand(new ArrayList<JanPai>(deck.subList(39, 52))));
            _info.setDeckIndex(13 * 4);
            _info.setRemainCount(70);
            
            // 待ち判定
            for (final Wind wind : Wind.values()) {
                if (playerTable.get(wind).getType() == PlayerType.COM) {
                    // NPCはツモ切り固定
                    _completeWait.put(wind, new ArrayList<JanPai>());
                    _chiWait.put(wind, new ArrayList<JanPai>());
                    _ponWait.put(wind, new ArrayList<JanPai>());
                }
                else {
                    updateWaitList(_info, wind);
                }
            }
            
            _info.notifyObservers(ANNOUNCE_FLAG_GAME_START);
            
            // 1巡目
            _info.setActiveWind(Wind.TON);
            onPhase();
        }
    }
    
    
    
    /**
     * 牌を切る
     * 
     * @param target 対象牌。
     * @throws CallableException 副露が可能。
     */
    private void discardCore(final JanPai target) throws CallableException {
        final Wind activeWind = _info.getActiveWind();
        _info.addDiscard(activeWind, target);
        _info.setActiveDiscard(target);
        
        // 他家の待ち判定
        final Map<Wind, List<CallType>> callableTable = new TreeMap<>();
        Wind targetWind = activeWind.getNext();
        while (targetWind != activeWind) {
            // NPCはツモ切り固定
            if (_info.getPlayer(targetWind).getType() != PlayerType.COM) {
                callableTable.put(targetWind, getCallableList(_info, activeWind, targetWind, target));
            }
            targetWind = targetWind.getNext();
        }
        if (!callableTable.isEmpty()) {
            // TODO 副露拡張対応
            // 副露された場合、捨て牌リストのインデックスにマークをつけて灰色表示させたい
        }
    }
    
    /**
     * 可能な副露リストを取得
     * 
     * @param info ゲーム情報。
     * @param activeWind 打牌中の風。
     * @param targetWind 判定対象の風。
     * @param discard 捨て牌。
     * @return 可能な副露リスト。
     */
    private List<CallType> getCallableList(final JanInfo info, final Wind activeWind,  final Wind targetWind, final JanPai discard) {
        final List<CallType> callTypeList = new ArrayList<>();
        // ロン可能か
        if (_completeWait.get(targetWind).contains(discard)) {
            callTypeList.add(CallType.RON);
        }
        
        // チー可能か
        if (activeWind.getNext() == targetWind) {
            if (_chiWait.get(targetWind).contains(discard)) {
                callTypeList.add(CallType.CHI);
            }
        }
        
        // ポン可能か
        if (_ponWait.get(targetWind).contains(discard)) {
            callTypeList.add(CallType.PON);
            if (info.getHand(targetWind).getJanPaiCount(discard) == 3) {
                callTypeList.add(CallType.KAN_LIGHT);
            }
        }
        return callTypeList;
    }
    
    /**
     * チーの待ち牌リストを取得
     * 
     * @param hand クリーン済みの手牌マップ。
     * @return チーの待ち牌リスト。
     */
    private List<JanPai> getChiWaitList(final Map<JanPai, Integer> hand) {
        final List<JanPai> resultList = new ArrayList<>();
        for (final JanPai pai : JanPai.values()) {
            if (isCallableChi(hand, pai)) {
                resultList.add(pai);
            }
        }
        return resultList;
    }
    
    /**
     * プレイヤーの手牌マップを取得
     * 
     * @param info ゲーム情報。
     * @param wind プレイヤーの風。
     * @return プレイヤーの手牌マップ。
     */
    private Map<JanPai, Integer> getHandMap(final JanInfo info, final Wind wind) {
        final Map<JanPai, Integer> hand = info.getHand(wind).getMenZenMap();
        JanPaiUtil.cleanJanPaiMap(hand);
        return hand;
    }
    
    /**
     * 牌をツモる
     * 
     * @return ツモ牌。
     */
    private JanPai getJanPaiFromDeck() {
        final JanPai pai = _info.getJanPaiFromDeck();
        _info.increaseDeckIndex();
        return pai;
    }
    
    /**
     * ポンの待ち牌リストを取得
     * 
     * @param hand クリーン済みの手牌マップ。
     * @return ポンの待ち牌リスト。
     */
    private List<JanPai> getPonWaitList(final Map<JanPai, Integer> hand) {
        final List<JanPai> resultList = new ArrayList<>();
        for (final Map.Entry<JanPai, Integer> entry : hand.entrySet()) {
            if (entry.getValue() >= 2) {
                resultList.add(entry.getKey());
            }
        }
        return resultList;
    }
    
    /**
     * チー可能か
     * 
     * @param hand クリーン済みの手牌マップ。
     * @param discard 捨て牌。
     * @return 判定結果。
     */
    private boolean isCallableChi(final Map<JanPai, Integer> hand, final JanPai discard) {
        if (discard.isJi()) {
            return false;
        }
        
        switch (discard) {
        case MAN_1:
        case PIN_1:
        case SOU_1:
            return hand.containsKey(discard.getNext()) && hand.containsKey(discard.getNext().getNext());
        case MAN_2:
        case PIN_2:
        case SOU_2:
            return (hand.containsKey(discard.getNext()) && hand.containsKey(discard.getNext().getNext())) ||
                   (hand.containsKey(discard.getPrev()) && hand.containsKey(discard.getNext()));
        case MAN_8:
        case PIN_8:
        case SOU_8:
            return (hand.containsKey(discard.getPrev()) && hand.containsKey(discard.getNext())) ||
                   (hand.containsKey(discard.getPrev()) && hand.containsKey(discard.getPrev().getPrev()));
        case MAN_9:
        case PIN_9:
        case SOU_9:
            return hand.containsKey(discard.getPrev()) && hand.containsKey(discard.getPrev().getPrev());
        default:
            return (hand.containsKey(discard.getNext()) && hand.containsKey(discard.getNext().getNext())) ||
                   (hand.containsKey(discard.getPrev()) && hand.containsKey(discard.getNext())) ||
                   (hand.containsKey(discard.getPrev()) && hand.containsKey(discard.getPrev().getPrev()));
        }
    }
    
    /**
     * 巡目ごとの処理
     * 
     * @throws CallableException 副露が可能。
     * @throws GameSetException 局が終了した。
     */
    private void onPhase() throws CallableException, GameSetException {
        if (_info.getRemainCount() == 0) {
            _onGame = false;
            throw new GameSetException(GameSetStatus.GAME_OVER);
        }
        
        // 牌をツモる
        final JanPai activeTsumo = getJanPaiFromDeck();
        _info.setActiveTsumo(activeTsumo);
        _info.decreaseRemainCount();
        
        // 打牌
        final Player activePlayer = _info.getActivePlayer();
        switch (activePlayer.getType()) {
        case COM:
            // ツモ切り
            discardCore(activeTsumo);
            
            // 次巡へ
            _info.setActiveWindToNext();
            onPhase();
            return;
        case HUMAN:
            _info.notifyObservers(ANNOUNCE_FLAG_HAND_TSUMO);
            break;
        }
    }
    
    /**
     * 待ち判定を更新
     * 
     * @param info ゲーム情報。
     * @param targetWind 更新対象の風。
     */
    private void updateWaitList(final JanInfo info, final Wind targetWind) {
        final Map<JanPai, Integer> hand = getHandMap(_info, targetWind);
        _completeWait.put(targetWind, HandCheckUtil.getCompletableJanPaiList(hand));
        _chiWait.put(targetWind, getChiWaitList(hand));
        _ponWait.put(targetWind, getPonWaitList(hand));
    }
    
    
    
    /**
     * 実況フラグ
     */
    private static final EnumSet<AnnounceFlag> ANNOUNCE_FLAG_GAME_START =
        EnumSet.of(AnnounceFlag.GAME_START);
    private static final EnumSet<AnnounceFlag> ANNOUNCE_FLAG_HAND_TSUMO =
        EnumSet.of(AnnounceFlag.HAND, AnnounceFlag.ACTIVE_TSUMO);
    
    
    
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
    
    /**
     * 和了の待ち
     */
    private Map<Wind, List<JanPai>> _completeWait = Collections.synchronizedMap(new TreeMap<Wind, List<JanPai>>());
    
    /**
     * チーの待ち
     */
    private Map<Wind, List<JanPai>> _chiWait = Collections.synchronizedMap(new TreeMap<Wind, List<JanPai>>());
    
    /**
     * ポンの待ち
     */
    private Map<Wind, List<JanPai>> _ponWait = Collections.synchronizedMap(new TreeMap<Wind, List<JanPai>>());
    
}

