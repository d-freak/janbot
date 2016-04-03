/**
 * ChmJanController.java
 * 
 * @author Masasutzu
 */

package wiz.project.janbot.game;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Observer;

import wiz.project.jan.Hand;
import wiz.project.jan.JanPai;
import wiz.project.jan.MenTsu;
import wiz.project.jan.MenTsuType;
import wiz.project.jan.Wind;
import wiz.project.jan.util.ChmHandCheckUtil;
import wiz.project.jan.util.JanPaiUtil;
import wiz.project.janbot.game.exception.BoneheadException;
import wiz.project.janbot.game.exception.CallableException;
import wiz.project.janbot.game.exception.GameSetException;
import wiz.project.janbot.game.exception.InvalidInputException;
import wiz.project.janbot.game.exception.JanException;



/**
 * 中国麻雀コントローラ (ソロ)
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
                throw new BoneheadException("Can't call.");
            }
            
            // 打牌したプレイヤーの風を記録
            final Wind calledWind = _info.getActiveWind();
            // 副露宣言したプレイヤーをアクティブ化して判定
            _info.setActivePlayer(playerName);
            final Wind activeWind = _info.getActiveWind();
            _info.increaseTurnCount(activeWind);
            try {
                switch (type) {
                case CHI:
                    if (calledWind.getNext() != activeWind) {
                        throw new InvalidInputException("Can't chi.");
                    }
                    callChi(target, calledWind);
                    break;
                case PON:
                    callPon(calledWind);
                    break;
                case KAN_LIGHT:
                    callKanLight(target, calledWind);
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
                // 副露しない場合、巡目とアクティブプレイヤーを元に戻す
                _info.decreaseTurnCount(activeWind);
                _info.setActiveWind(calledWind);
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
            final Wind calledWind = _info.getActiveWind();
            // ロン宣言したプレイヤーをアクティブ化して判定
            _info.setActivePlayer(playerName);
            final Wind activeWind = _info.getActiveWind();
            _info.increaseTurnCount(activeWind);
            try {
                // ロン対象牌を取得
                final JanPai discard = _info.getActiveDiscard();
                final Map<JanPai, Integer> handWithDiscard = getHandMap(_info, activeWind, discard);
                if (!ChmHandCheckUtil.isComplete(handWithDiscard)) {
                    // チョンボ
                    throw new BoneheadException("Not completed.");
                }
                _info.setCalledIndex(calledWind);
                _info.setCompleteInfo(activeWind, true);
                
                final int totalPoint = _info.getCompleteInfo().getTotalPoint();
                
                if (totalPoint < 8) {
                    // チョンボ
                    _info.notifyObservers(ANNOUNCE_FLAG_NOT_OVER_TIED_POINT);
                    throw new BoneheadException("Not over tied point.");
                }
                // ゲームセット
                _onGame = false;
                _info.notifyObservers(ANNOUNCE_FLAG_COMPLETE_RON);
            }
            catch (final Throwable e) {
                // 和了しない場合、巡目とアクティブプレイヤー、被副露牌インデックスを元に戻す
                _info.decreaseTurnCount(activeWind);
                _info.setActiveWind(calledWind);
                _info.removeCalledIndex(calledWind);
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
            // ツモ対象牌を取得
            final JanPai tsumo = _info.getActiveTsumo();
            final Wind activeWind = _info.getActiveWind();
            final Map<JanPai, Integer> handWithTsumo = getHandMap(_info, activeWind, tsumo);
            if (!ChmHandCheckUtil.isComplete(handWithTsumo)) {
                // チョンボ
                throw new BoneheadException("Not completed.");
            }
            _info.setCompleteInfo(activeWind, false);
            
            final int totalPoint = _info.getCompleteInfo().getTotalPoint();
            
            if (totalPoint < 8) {
                // チョンボ
                _info.notifyObservers(ANNOUNCE_FLAG_NOT_OVER_TIED_POINT);
                throw new BoneheadException("Not over tied point.");
            }
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
        
        _firstPhase = false;
        
        if (_info.getAfterCall()) {
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
            if (!_info.getAfterCall()) {
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
            if (!_info.getAfterCall()) {
                hand.addJanPai(activeTsumo);
            }
            _info.setAfterCall(false);
            
            final Wind activeWind = _info.getActiveWind();
            _info.setHand(activeWind, hand);
            
            // 手変わりがあったので待ち判定更新
            _info.updateWaitList(activeWind);
            
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
            _info.updateCompletableInfo();
            _info.setActiveWindToNext();
            onPhase();
        }
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
            
            // 配牌
            _info.setHand(Wind.TON, new Hand(new ArrayList<JanPai>(deck.subList( 0, 13))));
            _info.setHand(Wind.NAN, new Hand(new ArrayList<JanPai>(deck.subList(13, 26))));
            _info.setHand(Wind.SHA, new Hand(new ArrayList<JanPai>(deck.subList(26, 39))));
            _info.setHand(Wind.PEI, new Hand(new ArrayList<JanPai>(deck.subList(39, 52))));
            _info.setDeckIndex(13 * 4);
            _info.setDeckWallIndex(34 * 4 - 1 - 1);
            // 中国麻雀は王牌がないため、残り枚数は84枚 ※花牌を除く
            _info.setRemainCount(84);
            
            // 待ち判定
            for (final Wind wind : Wind.values()) {
                if (playerTable.get(wind).getType() == PlayerType.HUMAN) {
                    _info.updateWaitList(wind);
                }
            }
            
            // 1巡目
            _firstPhase = true;
            _info.setActiveWind(Wind.TON);
            onPhase();
        }
    }
    
    /**
     * 監視
     */
    public void watch(final List<JanPai> watchList) throws JanException {
        if (watchList == null) {
            throw new NullPointerException("WatchList is null.");
        }
        _info.setWatchingJanPaiList(watchList);
        _info.notifyObservers(ANNOUNCE_FLAG_WATCHING_START);
    }
    
    
    
    /**
     * チー
     * 
     * @param target 先頭牌指定。
     * @param calledWind 副露された風。
     * @throws JanException 例外イベント。
     */
    private void callChi(final JanPai target, final Wind calledWind) throws JanException {
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
        _info.setCalledIndex(calledWind);
        
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
        _info.setAfterCall(true);
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
        final boolean hasJanPai = hand.getMenZenJanPaiCount(target) != 0;
        final JanPai pai = _info.getActiveTsumo();
        
        if (!hasJanPai && !target.equals(pai)) {
            // 指定牌を持っていない
            throw new InvalidInputException("Can't kan.");
        }
        
        if (!hasPonMenTsu(hand, target)) {
            // 指定牌のポン面子を持っていない
            throw new InvalidInputException("Can't kan.");
        }
        
        if (_info.getAfterCall()) {
            // ポン、チー直後
            throw new InvalidInputException("Can't kan.");
        }
        
        // 直前のツモ牌を手牌に加える
        hand.addJanPai(pai);
        
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
     * @param calledWind 副露された風。
     * @throws JanException 例外イベント。
     */
    private void callKanLight(final JanPai target, final Wind calledWind) throws JanException {
        if (target == null) {
            throw new NullPointerException("Call target is null.");
        }
        
        final Hand hand = _info.getActiveHand();
        if (hand.getMenZenJanPaiCount(target) < 3) {
            // 指定牌を3枚持っていない
            throw new InvalidInputException("Can't kan.");
        }
        _info.setCalledIndex(calledWind);
        
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
     * @param calledWind 副露された風。
     * @throws JanException 例外イベント。
     */
    private void callPon(final Wind calledWind) throws JanException {
        final JanPai discard = _info.getActiveDiscard();
        final Hand hand = _info.getActiveHand();
        if (hand.getMenZenJanPaiCount(discard) < 2) {
            // 指定牌を2枚持っていない
            throw new InvalidInputException("Can't pon.");
        }
        _info.setCalledIndex(calledWind);
        
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
        _info.setAfterCall(true);
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
        _info.setCallKan(false);
        
        final Wind humanWind = _info.getHumanWind();
        // 他家の待ち判定
        if (activeWind != humanWind) {
            // NPCはツモ切り固定
            final List<CallType> callableList = _info.getCallableList(activeWind, humanWind, target);
            
            if (!callableList.isEmpty()) {
                throw new CallableException(callableList);
            }
        }
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
     * 嶺上牌をツモる
     * 
     * @return ツモ牌。
     */
    private JanPai getJanPaiFromDeckWall() {
        final JanPai pai = _info.getJanPaiFromDeckWall();
        _info.moveDeckWallIndex();
        return pai;
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
        _info.setCallKan(false);
        
        // 打牌
        final Player activePlayer = _info.getActivePlayer();
        final Wind activeWind = _info.getActiveWind();
        _info.increaseTurnCount(activeWind);
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
        // 嶺上牌をツモる
        final JanPai activeTsumo = getJanPaiFromDeckWall();
        _info.setActiveTsumo(activeTsumo);
        _info.decreaseRemainCount();
        _info.setCallKan(true);
        
        // 手変わりがあったので待ち判定更新
        _info.updateWaitList(activeWind);
    }
    
    
    
    /**
     * 実況フラグ
     */
    private static final EnumSet<AnnounceFlag> ANNOUNCE_FLAG_COMPLETE_RON =
        EnumSet.of(AnnounceFlag.COMPLETE_RON, AnnounceFlag.FIELD, AnnounceFlag.URA_DORA, AnnounceFlag.RIVER_SINGLE, AnnounceFlag.HAND, AnnounceFlag.ACTIVE_DISCARD, AnnounceFlag.SCORE);
    private static final EnumSet<AnnounceFlag> ANNOUNCE_FLAG_COMPLETE_TSUMO =
        EnumSet.of(AnnounceFlag.COMPLETE_TSUMO, AnnounceFlag.FIELD, AnnounceFlag.URA_DORA, AnnounceFlag.RIVER_SINGLE, AnnounceFlag.HAND, AnnounceFlag.ACTIVE_TSUMO, AnnounceFlag.SCORE);
    private static final EnumSet<AnnounceFlag> ANNOUNCE_FLAG_HAND_TSUMO =
        EnumSet.of(AnnounceFlag.HAND, AnnounceFlag.ACTIVE_TSUMO);
    private static final EnumSet<AnnounceFlag> ANNOUNCE_FLAG_HAND_TSUMO_FIELD =
        EnumSet.of(AnnounceFlag.HAND, AnnounceFlag.ACTIVE_TSUMO, AnnounceFlag.FIELD);
    private static final EnumSet<AnnounceFlag> ANNOUNCE_FLAG_HAND_AFTER_CALL =
        EnumSet.of(AnnounceFlag.HAND, AnnounceFlag.AFTER_CALL);
    private static final EnumSet<AnnounceFlag> ANNOUNCE_FLAG_HAND_TSUMO_FIELD_AFTER_CALL =
        EnumSet.of(AnnounceFlag.HAND, AnnounceFlag.ACTIVE_TSUMO, AnnounceFlag.FIELD, AnnounceFlag.AFTER_CALL);
    private static final EnumSet<AnnounceFlag> ANNOUNCE_FLAG_WATCHING_START =
        EnumSet.of(AnnounceFlag.WATCHING_START);
    private static final EnumSet<AnnounceFlag> ANNOUNCE_FLAG_NOT_OVER_TIED_POINT =
        EnumSet.of(AnnounceFlag.NOT_OVER_TIED_POINT, AnnounceFlag.SCORE);
    
    
    
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
    
}

