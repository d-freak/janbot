/**
 * SoloJanController.java
 * 
 * @author Yuki
 */

package wiz.project.janbot.game;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Observer;
import java.util.TreeMap;

import wiz.project.jan.Hand;
import wiz.project.jan.JanPai;
import wiz.project.jan.MenTsu;
import wiz.project.jan.MenTsuType;
import wiz.project.jan.Wind;
import wiz.project.jan.util.HandCheckUtil;
import wiz.project.jan.util.JanPaiUtil;
import wiz.project.janbot.game.exception.BoneheadException;
import wiz.project.janbot.game.exception.CallableException;
import wiz.project.janbot.game.exception.GameSetException;
import wiz.project.janbot.game.exception.InvalidInputException;
import wiz.project.janbot.game.exception.JanException;



/**
 * 麻雀コントローラ (ソロ)
 */
class SoloJanController implements JanController {
    
    /**
     * コンストラクタ
     */
    public SoloJanController() {
    }
    
    /**
     * コンストラクタ
     * 
     * @param observer 監視者。
     */
    public SoloJanController(final Observer observer) {
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
                    callChi(target);
                    break;
                case PON:
                    callPon();
                    break;
                case KAN_LIGHT:
                    callKanLight(target);
                    break;
                case KAN_ADD:
                    callKanAdd(target);
                    break;
                case KAN_DARK:
                    callKanDark(target);
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
                final JanPai discard = _info.getActiveDiscard();
                final Map<JanPai, Integer> handWithDiscard = getHandMap(_info, _info.getActiveWind(), discard);
                if (!HandCheckUtil.isComplete(handWithDiscard)) {
                    // チョンボ
                    throw new BoneheadException("Not completed.");
                }
                if (_info.getActiveRiver().contains(discard)) {
                    // フリテン
                    throw new BoneheadException("Furiten.");
                }
                
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
            final Map<JanPai, Integer> handWithTsumo = getHandMap(_info, _info.getActiveWind(), _info.getActiveTsumo());
            if (!HandCheckUtil.isComplete(handWithTsumo)) {
                // チョンボ
                throw new BoneheadException("Not completed.");
            }
            
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
        
        _firstPhase = false;
        
        if (_afterCall) {
            throw new InvalidInputException("Tsumo pai is not exist.");
        }
        
        synchronized (_GAME_INFO_LOCK) {
            discardCore(_info.getActiveTsumo());
            
            // 次の打牌へ
            _info.setActiveWindToNext();
            onPhase();
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
            final JanPai activeTsumo = _info.getActiveTsumo();
            if (!_afterCall) {
                if (target == activeTsumo) {
                    // 直前のツモ牌が指定された
                    discard();
                    return;
                }
            }
            
            final Hand hand = _info.getActiveHand();
            if (hand.getMenZenMap().get(target) <= 0) {
                // 手牌に存在しないが指定された
                throw new InvalidInputException("Invalid discard target - " + target);
            }
            
            // 打牌
            _firstPhase = false;
            hand.removeJanPai(target);
            if (!_afterCall) {
                hand.addJanPai(activeTsumo);
            }
            _afterCall = false;
            
            final Wind activeWind = _info.getActiveWind();
            _info.setHand(activeWind, hand);
            
            // 手変わりがあったので待ち判定更新
            updateWaitList(_info, activeWind);
            
            discardCore(target);
            
            // 次の打牌へ
            _info.setActiveWindToNext();
            onPhase();
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
        
        _firstPhase = false;
        
        synchronized (_GAME_INFO_LOCK) {
            _info.setActiveWindToNext();
            onPhase();
        }
    }
    
    /**
     * リーチ
     */
    public void richi(final JanPai target) throws JanException {
        if (!_onGame) {
            throw new JanException("Game is not started.");
        }
        
        // TODO リーチ対応
        // _onRichiフラグ見て何かしたい
        
        _onRichi = true;
        
        discard(target);
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
            
            // 1巡目
            _onRichi = false;
            _firstPhase = true;
            _info.setActiveWind(Wind.TON);
            onPhase();
        }
    }
    
    
    
    /**
     * チー
     * 
     * @param target 先頭牌指定。
     * @throws JanException 例外イベント。
     */
    private void callChi(final JanPai target) throws JanException {
        if (target == null) {
            throw new NullPointerException("Call target is null.");
        }
        
        switch (target) {
        case MAN_8:
        case MAN_9:
        case PIN_8:
        case PIN_9:
        case SOU_8:
        case SOU_9:
        case TON:
        case NAN:
        case SHA:
        case PEI:
        case HAKU:
        case HATU:
        case CHUN:
            // チー不可
            throw new InvalidInputException("Can't chi.");
        default:
            break;
        }
        
        final List<JanPai> targetList = Arrays.asList(target, target.getNext(), target.getNext().getNext());
        final JanPai discard = _info.getActiveDiscard();
        if (!targetList.contains(discard)) {
            // チー不可
            throw new InvalidInputException("Can't chi.");
        }
        
        // 直前の捨て牌を手牌に加える
        final Hand hand = _info.getActiveHand();
        hand.addJanPai(discard);
        
        for (final JanPai targetPai : targetList) {
            if (hand.getMenZenJanPaiCount(targetPai) == 0) {
                // チー不可
                throw new InvalidInputException("Can't chi.");
            }
        }
        
        // チー対象牌を削除
        for (final JanPai targetPai : targetList) {
            hand.removeJanPai(targetPai);
        }
        
        // 固定面子を追加
        final MenTsu chi = new MenTsu(targetList, MenTsuType.CHI);
        hand.addFixedMenTsu(chi);
        
        // 手牌を更新
        final Wind activeWind = _info.getActiveWind();
        _info.setHand(activeWind, hand);
        
        // 捨て牌選択
        _afterCall = true;
        _info.notifyObservers(ANNOUNCE_FLAG_HAND_AFTER_CALL);
        
        // 手変わりに対する待ち判定更新は、ここではなく打牌時に行う。
    }
    
    /**
     * 加カン
     * 
     * @param target 牌指定。
     * @throws JanException 例外イベント。
     */
    private void callKanAdd(final JanPai target) throws JanException {
        if (target == null) {
            throw new NullPointerException("Call target is null.");
        }
        
        final Hand hand = _info.getActiveHand();
        if (!hasPonMenTsu(hand, target)) {
            // 指定牌のポン面子を持っていない
            throw new InvalidInputException("Can't kan.");
        }
        
        // 直前のツモ牌を手牌に加える
        hand.addJanPai(_info.getActiveTsumo());
        
        // カン対象牌を削除
        hand.removeJanPai(target);
        
        // 固定面子リストを更新
        final List<MenTsu> fixedMenTsuList = hand.getFixedMenTsuList();
        for (int i = 0; i < fixedMenTsuList.size(); i++) {
            final MenTsu menTsu = fixedMenTsuList.get(i);
            if (menTsu.getMenTsuType() == MenTsuType.PON) {
                if (menTsu.getSource().get(0) == target) {
                    final MenTsu kanLight = new MenTsu(Arrays.asList(target, target, target, target), MenTsuType.KAN_LIGHT);
                    fixedMenTsuList.set(i, kanLight);
                    hand.setFixedMenTsuList(fixedMenTsuList);
                    break;
                }
            }
        }
        
        // 手牌を更新
        final Wind activeWind = _info.getActiveWind();
        _info.setHand(activeWind, hand);
        
        // 王牌操作
        postProcessKan(activeWind);
        
        // 捨て牌選択
        _info.notifyObservers(ANNOUNCE_FLAG_HAND_TSUMO_FIELD_AFTER_CALL);
    }
    
    /**
     * 暗カン
     * 
     * @param target 牌指定。
     * @throws JanException 例外イベント。
     */
    private void callKanDark(final JanPai target) throws JanException {
        if (target == null) {
            throw new NullPointerException("Call target is null.");
        }
        
        final Wind activeWind = _info.getActiveWind();
        final JanPai activeTsumo = _info.getActiveTsumo();
        final Map<JanPai, Integer> count = getHandMap(_info, activeWind, activeTsumo);
        if (!count.containsKey(target) || count.get(target) < 4) {
            // 指定牌を4枚持っていない
            throw new InvalidInputException("Can't kan.");
        }
        
        // 直前のツモ牌を手牌に加える
        final Hand hand = _info.getActiveHand();
        hand.addJanPai(activeTsumo);
        
        // カン対象牌を削除
        for (int i = 0; i < 4; i++) {
            hand.removeJanPai(target);
        }
        
        // 固定面子を追加
        final MenTsu kanDark = new MenTsu(Arrays.asList(target, target, target, target), MenTsuType.KAN_DARK);
        hand.addFixedMenTsu(kanDark);
        
        // 手牌を更新
        _info.setHand(activeWind, hand);
        
        // 王牌操作
        postProcessKan(activeWind);
        
        // 捨て牌選択
        _info.notifyObservers(ANNOUNCE_FLAG_HAND_TSUMO_FIELD_AFTER_CALL);
    }
    
    /**
     * 大明カン
     * 
     * @param target 牌指定。
     * @throws JanException 例外イベント。
     */
    private void callKanLight(final JanPai target) throws JanException {
        if (target == null) {
            throw new NullPointerException("Call target is null.");
        }
        
        final Hand hand = _info.getActiveHand();
        if (hand.getMenZenJanPaiCount(target) < 3) {
            // 指定牌を3枚持っていない
            throw new InvalidInputException("Can't kan.");
        }
        
        // カン対象牌を削除
        for (int i = 0; i < 3; i++) {
            hand.removeJanPai(target);
        }
        
        // 固定面子を追加
        final MenTsu kanLight = new MenTsu(Arrays.asList(target, target, target, target), MenTsuType.KAN_LIGHT);
        hand.addFixedMenTsu(kanLight);
        
        // 手牌を更新
        final Wind activeWind = _info.getActiveWind();
        _info.setHand(activeWind, hand);
        
        // 王牌操作
        postProcessKan(activeWind);
        
        // 捨て牌選択
        _info.notifyObservers(ANNOUNCE_FLAG_HAND_TSUMO_FIELD_AFTER_CALL);
    }
    
    /**
     * ポン
     * 
     * @throws JanException 例外イベント。
     */
    private void callPon() throws JanException {
        final JanPai discard = _info.getActiveDiscard();
        final Hand hand = _info.getActiveHand();
        if (hand.getMenZenJanPaiCount(discard) < 2) {
            // 指定牌を2枚持っていない
            throw new InvalidInputException("Can't pon.");
        }
        
        // ポン対象牌を削除
        for (int i = 0; i < 2; i++) {
            hand.removeJanPai(discard);
        }
        
        // 固定面子を追加
        final MenTsu pon = new MenTsu(Arrays.asList(discard, discard, discard), MenTsuType.PON);
        hand.addFixedMenTsu(pon);
        
        // 手牌を更新
        final Wind activeWind = _info.getActiveWind();
        _info.setHand(activeWind, hand);
        
        // 捨て牌選択
        _afterCall = true;
        _info.notifyObservers(ANNOUNCE_FLAG_HAND_AFTER_CALL);
        
        // 手変わりに対する待ち判定更新は、ここではなく打牌時に行う。
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
        Wind targetWind = activeWind.getNext();
        while (targetWind != activeWind) {
            if (_info.getPlayer(targetWind).getType() != PlayerType.COM) {
                // NPCはツモ切り固定
                final List<CallType> callableList = getCallableList(_info, activeWind, targetWind, target);
                if (!callableList.isEmpty()) {
                    throw new CallableException(callableList);
                }
            }
            targetWind = targetWind.getNext();
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
    private List<CallType> getCallableList(final JanInfo info, final Wind activeWind, final Wind targetWind, final JanPai discard) {
        final List<CallType> callTypeList = new ArrayList<>();
        // ロン可能か
        if (_completeWait.get(targetWind).contains(discard)) {
            callTypeList.add(CallType.RON);
        }
        
        if (_onRichi) {
            // リーチ中は副露不可
            return callTypeList;
        }
        if (info.getRemainCount() == 0) {
            // 残り0枚なら副露不可
            return callTypeList;
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
            if (info.getHand(targetWind).getMenZenJanPaiCount(discard) == 3) {
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
     * 指定牌込みでプレイヤーの手牌マップを取得
     * 
     * @param info ゲーム情報。
     * @param wind プレイヤーの風。
     * @param source 手牌に追加する牌。
     * @return プレイヤーの手牌マップ。
     */
    private Map<JanPai, Integer> getHandMap(final JanInfo info, final Wind wind, final JanPai source) {
        final Map<JanPai, Integer> hand = info.getHand(wind).getMenZenMap();
        JanPaiUtil.addJanPai(hand, source, 1);
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
     * 指定牌のポン面子を持っているか
     * 
     * @param sourceHand 確認元手牌。
     * @param target 確認対象牌。
     * @return 確認結果。
     */
    private boolean hasPonMenTsu(final Hand sourceHand, final JanPai target) {
        for (final MenTsu menTsu : sourceHand.getFixedMenTsuList()) {
            if (menTsu.getMenTsuType() == MenTsuType.PON) {
                if (menTsu.getSource().get(0) == target) {
                    return true;
                }
            }
        }
        return false;
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
            if (_firstPhase) {
                _firstPhase = false;
                _info.notifyObservers(ANNOUNCE_FLAG_HAND_TSUMO_FIELD);
            }
            else {
                _info.notifyObservers(ANNOUNCE_FLAG_HAND_TSUMO);
            }
            break;
        }
    }
    
    /**
     * カンの後処理 (王牌操作)
     * 
     * @param activeWind アクティブプレイヤーの風。
     */
    private void postProcessKan(final Wind activeWind) {
        // ドラを追加
        final WanPai wanPai = _info.getWanPai();
        wanPai.openNewDora();
        
        // 嶺上牌をツモる
        final JanPai activeTsumo = wanPai.getWall();
        _info.setActiveTsumo(activeTsumo);
        _info.decreaseRemainCount();
        _info.setWanPai(wanPai);
        
        // 手変わりがあったので待ち判定更新
        updateWaitList(_info, activeWind);
    }
    
    /**
     * 待ち判定を更新
     * 
     * @param info ゲーム情報。
     * @param targetWind 更新対象の風。
     */
    private void updateWaitList(final JanInfo info, final Wind targetWind) {
        final Map<JanPai, Integer> hand = getHandMap(info, targetWind);
        _completeWait.put(targetWind, HandCheckUtil.getCompletableJanPaiList(hand));
        _chiWait.put(targetWind, getChiWaitList(hand));
        _ponWait.put(targetWind, getPonWaitList(hand));
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
    
    /**
     * 初巡フラグ
     */
    private volatile boolean _firstPhase = true;
    
    /**
     * リーチフラグ
     */
    private volatile boolean _onRichi = false;
    
    /**
     * 副露後の打牌フラグ
     */
    private volatile boolean _afterCall = false;
    
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

