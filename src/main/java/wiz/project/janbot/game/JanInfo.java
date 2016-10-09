/**
 * JanInfo.java
 * 
 * @author Yuki
 */

package wiz.project.janbot.game;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Observable;
import java.util.TreeMap;

import wiz.project.jan.ChmCompleteInfo;
import wiz.project.jan.CompleteJanPai;
import wiz.project.jan.CompleteType;
import wiz.project.jan.Hand;
import wiz.project.jan.JanPai;
import wiz.project.jan.MenTsu;
import wiz.project.jan.Wind;
import wiz.project.jan.util.ChmHandCheckUtil;
import wiz.project.jan.util.JanPaiUtil;



/**
 * 麻雀ゲームの情報
 */
public final class JanInfo extends Observable implements Cloneable {
    
    /**
     * コンストラクタ
     */
    public JanInfo() {
        for (final Wind wind : Wind.values()) {
            _playerTable.put(wind, new Player());
            _handTable.put(wind, new Hand());
            _riverTable.put(wind, new River());
            _completableJanPaiTable.put(wind, new ArrayList<JanPai>());
            _completableTurnTable.put(wind, 0);
            _turnTable.put(wind, 0);
            _completeWait.put(wind, new ArrayList<JanPai>());
            _chiWait.put(wind, new ArrayList<JanPai>());
            _ponWait.put(wind, new ArrayList<JanPai>());
        }
    }
    
    /**
     * コピーコンストラクタ
     * 
     * @param source 複製元。
     */
    public JanInfo(final JanInfo source) {
        if (source != null) {
            _playerTable = deepCopyMap(source._playerTable);
            _deck = deepCopyList(source._deck);
            _deckIndex = source._deckIndex;
            _deckWallIndex = source._deckWallIndex;
            _wanPai = source._wanPai.clone();
            _fieldWind = source._fieldWind;
            _activeWind = source._activeWind;
            _remainCount = source._remainCount;
            _activeTsumo = source._activeTsumo;
            _activeDiscard = source._activeDiscard;
            _watchingJanPaiList = deepCopyList(source._watchingJanPaiList);
            _completeInfo = source._completeInfo;
            _afterCall = source._afterCall;
            _callKan = source._callKan;
            
            for (final Map.Entry<Wind, Hand> entry : source._handTable.entrySet()) {
                _handTable.put(entry.getKey(), entry.getValue().clone());
            }
            for (final Entry<Wind, River> entry : source._riverTable.entrySet()) {
                _riverTable.put(entry.getKey(), entry.getValue().clone());
            }
            for (final Entry<Wind, List<JanPai>> entry : source._completableJanPaiTable.entrySet()) {
            	_completableJanPaiTable.put(entry.getKey(), entry.getValue());
            }
            for (final Entry<Wind, Integer> entry : source._completableTurnTable.entrySet()) {
            	_completableTurnTable.put(entry.getKey(), entry.getValue());
            }
            for (final Entry<Wind, Integer> entry : source._turnTable.entrySet()) {
                _turnTable.put(entry.getKey(), entry.getValue());
            }
            for (final Entry<Wind, List<JanPai>> entry : source._completeWait.entrySet()) {
            	_completeWait.put(entry.getKey(), entry.getValue());
            }
            for (final Entry<Wind, List<JanPai>> entry : source._chiWait.entrySet()) {
            	_chiWait.put(entry.getKey(), entry.getValue());
            }
            for (final Entry<Wind, List<JanPai>> entry : source._ponWait.entrySet()) {
            	_ponWait.put(entry.getKey(), entry.getValue());
            }
        }
    }
    
    
    
    /**
     * 捨て牌を追加
     * 
     * @param wind 風。
     * @param discard 捨て牌。
     */
    public void addDiscard(final Wind wind, final JanPai discard) {
        if (wind != null) {
            if (discard != null) {
                _riverTable.get(wind).add(discard);
            }
        }
    }
    
    /**
     * フィールドを全消去
     */
    public void clear() {
        _deck.clear();
        _deckIndex = 0;
        _deckWallIndex = 0;
        _wanPai = new WanPai();
        _fieldWind = Wind.TON;
        _activeWind = Wind.TON;
        _remainCount = 0;
        _activeTsumo = JanPai.HAKU;
        _activeDiscard = JanPai.HAKU;
        _watchingJanPaiList.clear();
        _completeInfo = null;
        _afterCall = false;
        _callKan = false;
        
        for (final Wind wind : Wind.values()) {
            _playerTable.put(wind, new Player());
            _handTable.put(wind, new Hand());
            _riverTable.put(wind, new River());
            _completableJanPaiTable.put(wind, new ArrayList<JanPai>());
            _completableTurnTable.put(wind, 0);
            _turnTable.put(wind, 0);
            _completeWait.put(wind, new ArrayList<JanPai>());
            _chiWait.put(wind, new ArrayList<JanPai>());
            _ponWait.put(wind, new ArrayList<JanPai>());
        }
    }
    
    /**
     * オブジェクトを複製 (ディープコピー)
     * 
     * @return 複製結果。
     */
    @Override
    public JanInfo clone() {
        return new JanInfo(this);
    }
    
    /**
     * 残り枚数を減少
     */
    public void decreaseRemainCount() {
        if (_remainCount > 0) {
            _remainCount--;
        }
    }
    
    /**
     * 指定した風の巡目を減少
     * 
     * @param wind 風。
     */
    public void decreaseTurnCount(final Wind wind) {
        final int turnCount = _turnTable.get(wind);
        
        if (turnCount > 0) {
            _turnTable.put(wind, turnCount - 1);
        }
    }
    
    /**
     * 直前の捨て牌を取得
     * 
     * @return 直前の捨て牌。
     */
    public JanPai getActiveDiscard() {
        return _activeDiscard;
    }
    
    /**
     * アクティブプレイヤーの手牌を取得
     * 
     * @return アクティブプレイヤーの手牌。
     */
    public Hand getActiveHand() {
        return getHand(_activeWind);
    }
    
    /**
     * アクティブプレイヤーを取得
     * 
     * @return アクティブプレイヤー。
     */
    public Player getActivePlayer() {
        return getPlayer(_activeWind);
    }
    
    /**
     * アクティブプレイヤーの捨て牌リストを取得
     * 
     * @return アクティブプレイヤーの捨て牌リスト。
     */
    public List<JanPai> getActiveRiver() {
        return getRiver(_activeWind).get();
    }
    
    /**
     * 直前のツモ牌を取得
     * 
     * @return 直前のツモ牌。
     */
    public JanPai getActiveTsumo() {
        return _activeTsumo;
    }
    
    /**
     * アクティブプレイヤーの風を取得
     * 
     * @return アクティブプレイヤーの風。
     */
    public Wind getActiveWind() {
        return _activeWind;
    }
    
    /**
     * 副露後の打牌フラグを取得
     * 
     * @return 副露後の打牌フラグ。
     */
    public boolean getAfterCall() {
        return _afterCall;
    }
    
    /**
     * 可能な副露リストを取得
     * 
     * @param activeWind 打牌中の風。
     * @param targetWind 判定対象の風。
     * @param discard 捨て牌。
     * @return 可能な副露リスト。
     */
    public List<CallType> getCallableList(final Wind activeWind, final Wind targetWind, final JanPai discard) {
        final List<CallType> callTypeList = new ArrayList<>();
        // ロン可能か
        if (_completeWait.get(targetWind).contains(discard)) {
            callTypeList.add(CallType.RON);
        }
        
        if (getRemainCount() == 0) {
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
            if (getHand(targetWind).getMenZenJanPaiCount(discard) == 3) {
                callTypeList.add(CallType.KAN_LIGHT);
            }
        }
        return callTypeList;
    }
    
    /**
     * 指定した風の和了可能牌を取得
     * 
     * @param wind 風。
     * @return 指定した風の和了可能牌。
     */
    public List<JanPai> getCompletableJanPaiList(final Wind wind) {
        return _completableJanPaiTable.get(wind);
    }
    
    /**
     * 指定した風の和了可能巡目を取得
     * 
     * @param wind 風。
     * @return 指定した風の和了可能巡目。
     */
    public Integer getCompletableTurnCount(final Wind wind) {
        return _completableTurnTable.get(wind);
    }
    
    /**
     * 和了情報を取得
     * 
     * @return 和了情報。
     */
    public ChmCompleteInfo getCompleteInfo() {
        return _completeInfo;
    }
    
    /**
     * 牌山を取得
     * 
     * @return 牌山。
     */
    public List<JanPai> getDeck() {
        return deepCopyList(_deck);
    }
    
    /**
     * 牌山インデックスを取得
     * 
     * @return 牌山インデックス。
     */
    public int getDeckIndex() {
        return _deckIndex;
    }
    
    /**
     * 場風を取得
     * 
     * @return 場風。
     */
    public Wind getFieldWind() {
        return _fieldWind;
    }
    
    /**
     * 手牌を取得
     * 
     * @param wind 風。
     * @return 手牌。
     */
    public Hand getHand(final Wind wind) {
        if (wind != null) {
            return _handTable.get(wind).clone();
        }
        else {
            return new Hand();
        }
    }
    
    /**
     * 人間の風を取得
     * 
     * @return 人間の風。
     */
    public Wind getHumanWind() {
        for (final Entry<Wind, Player> entry : _playerTable.entrySet()) {
            final boolean isHuman = entry.getValue().getType() == PlayerType.HUMAN;
            
            if (isHuman) {
                return entry.getKey();
            }
        }
        return Wind.TON;
    }
    
    /**
     * 牌山から牌を取得
     * 
     * @return インデックスの指す牌。
     */
    public JanPai getJanPaiFromDeck() {
        return _deck.get(_deckIndex);
    }
    
    /**
     * 牌山から嶺上牌を取得(中国麻雀用)
     * 
     * @return インデックスの指す牌。
     */
    public JanPai getJanPaiFromDeckWall() {
        return _deck.get(_deckWallIndex);
    }
    
    /**
     * 残り枚数テーブルを取得
     * 
     * @param paiList 牌リスト。
     * @param wind 風。
     * @return 残り枚数テーブル。
     */
    public Map<JanPai, Integer> getOuts(final List<JanPai> paiList, final Wind wind) {
        final Map<JanPai, Integer> outs = getOutsOnConfirm(paiList, wind);
        
        if (!_afterCall) {
            final JanPai activeTsumo = getActiveTsumo();
            final Integer activeTsumoCount = outs.get(activeTsumo);
            
            if (activeTsumoCount != null) {
                outs.put(activeTsumo, activeTsumoCount - 1);
            }
        }
        return outs;
    }
    
    /**
     * 残り枚数テーブルを取得(確認メッセージ用)
     *
     * @param pai 牌。
     * @param wind 風。
     * @return 残り枚数。
     */
    public int getOutsOnConfirm(final JanPai pai, final Wind wind) {
        final List<JanPai> paiList = Arrays.asList(pai);
        final Map<JanPai, Integer> outs = getOutsOnConfirm(paiList, wind);
        return outs.get(pai);
    }
    
    /**
     * 残り枚数テーブルを取得(確認メッセージ用)
     * 
     * @param paiList 牌リスト。
     * @param wind 風。
     * @return 残り枚数テーブル。
     */
    public Map<JanPai, Integer> getOutsOnConfirm(final List<JanPai> paiList, final Wind wind) {
        final Map<JanPai, Integer> outs = getVisibleOuts(paiList);
        
        for (final JanPai pai : paiList) {
            int visibleCount = 0;
            visibleCount += getHand(wind).getMenZenMap().get(pai);
            
            final List<MenTsu> fixedMenTsuList = getHand(wind).getFixedMenTsuList();
            
            for (final MenTsu fixedMenTsu : fixedMenTsuList) {
                final boolean isCalled = fixedMenTsu.getMenTsuType().isCalled();
                
                if (!isCalled) {
                    visibleCount += fixedMenTsu.getJanPaiCount(pai);
                }
            }
            outs.put(pai, outs.get(pai) - visibleCount);
        }
        return outs;
    }
    
    /**
     * プレイヤーを取得
     * 
     * @param wind 風。
     * @return プレイヤー。
     */
    public Player getPlayer(final Wind wind) {
        if (wind != null) {
            return _playerTable.get(wind);
        }
        else {
            return new Player();
        }
    }
    
    /**
     * プレイヤーテーブルを取得
     * 
     * @return プレイヤーテーブル。
     */
    public Map<Wind, Player> getPlayerTable() {
        return deepCopyMap(_playerTable);
    }
    
    /**
     * 残り枚数を取得
     * 
     * @return 残り枚数。
     */
    public int getRemainCount() {
        return _remainCount;
    }
    
    /**
     * 捨て牌リストを取得
     * 
     * @param wind 風。
     * @return 捨て牌リスト。
     */
    public River getRiver(final Wind wind) {
        if (wind != null) {
            return _riverTable.get(wind).clone();
        }
        else {
            return new River();
        }
    }
    
    /**
     * 手牌で1枚だけの牌リストを取得
     * 
     * @param wind 風。
     * @param isTsumo ツモっているか。
     * @return 手牌で1枚だけの牌リスト。
     */
    public List<JanPai> getSingleJanPaiList(final Wind wind, final boolean isTsumo) {
        final List<JanPai> paiList = new ArrayList<>();
        Map<JanPai, Integer> paiMap = new TreeMap<JanPai, Integer>();
        final int usableSize = getHand(wind).getUsableSize();
        
        if (isTsumo && usableSize != 0) {
            paiMap = getHand(wind).getCleanMenZenMap(getActiveTsumo());
        }
        else {
            paiMap = getHand(wind).getMenZenMap();
            JanPaiUtil.cleanJanPaiMap(paiMap);
        }
        
        for (final JanPai pai : paiMap.keySet()) {
            final int paiCount = paiMap.get(pai);
            
            if (paiCount == 1) {
                paiList.add(pai);
            }
        }
        return paiList;
    }
    
    /**
     * 指定した風の巡目を取得
     * 
     * @param wind 風。
     * @return 指定した風の巡目。
     */
    public Integer getTurnCount(final Wind wind) {
        return _turnTable.get(wind);
    }
    
    /**
     * 王牌を取得
     * 
     * @return 王牌。
     */
    public WanPai getWanPai() {
        return _wanPai.clone();
    }
    
    /**
     * 監視牌のリストを取得
     * 
     * @return 監視牌のリスト。
     */
    public List<JanPai> getWatchingJanPaiList() {
        return deepCopyList(_watchingJanPaiList);
    }
    
    /**
     * 牌山インデックスを増加
     */
    public void increaseDeckIndex() {
        if (isSameIndex()) {
            moveDeckWallIndex();
        }
        
        setDeckIndex(_deckIndex + 1);
        
        if (isLastShitatsumo()) {
            setDeckIndex(_deckIndex + 1);
        }
    }
    
    /**
     * 指定した風の巡目を増加
     * 
     * @param wind 風。
     */
    public void increaseTurnCount(final Wind wind) {
        _turnTable.put(wind, _turnTable.get(wind) + 1);
    }
    
    /**
     * アクティブプレイヤーか
     * 
     * @param playerName プレイヤー名。
     * @return 判定結果。
     */
    public boolean isActivePlayer(final String playerName) {
        if (playerName == null) {
            return false;
        }
        return getActivePlayer().getName().equals(playerName);
    }
    
    /**
     * ゲームに参加中のプレイヤーか
     * 
     * @param playerName プレイヤー名。
     * @return 判定結果。
     */
    public boolean isValidPlayer(final String playerName) {
        if (playerName == null) {
            return false;
        }
        for (final Player player : _playerTable.values()) {
            if (player.getName().equals(playerName)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * 牌山の嶺上インデックスを移動(中国麻雀用)
     */
    public void moveDeckWallIndex() {
        if (isUwatsumo()) {
        	setDeckWallIndex(_deckWallIndex + 1);
        }
        else {
            setDeckWallIndex(_deckWallIndex - 3);
        }
    }
    
    /**
     * 監視者に状態を通知 (強制)
     * 
     * @param param 通知パラメータ。
     */
    @Override
    public void notifyObservers(final Object param) {
        setChanged();
        super.notifyObservers(param);
    }
    
    /**
     * 被副露牌インデックスを削除
     * 
     * @param wind 副露された風。
     */
    public void removeCalledIndex(final Wind wind) {
        if (wind != null) {
            _riverTable.get(wind).removeCalledIndex();
        }
    }
    
    /**
     * 直前の捨て牌を設定
     * 
     * @param pai 直前の捨て牌。
     */
    public void setActiveDiscard(final JanPai pai) {
        if (pai != null) {
            _activeDiscard = pai;
        }
        else {
            _activeDiscard = JanPai.HAKU;
        }
    }
    
    /**
     * アクティブプレイヤーを設定
     * 
     * @param playerName プレイヤー名。
     */
    public void setActivePlayer(final String playerName) {
        if (playerName != null) {
            for (final Map.Entry<Wind, Player> entry : _playerTable.entrySet()) {
                if (entry.getValue().getName().equals(playerName)) {
                    setActiveWind(entry.getKey());
                }
            }
        }
    }
    
    /**
     * 直前のツモ牌を設定
     * 
     * @param pai 直前のツモ牌。
     */
    public void setActiveTsumo(final JanPai pai) {
        _activeTsumo = pai;
    }
    
    /**
     * アクティブプレイヤーの風を設定
     * 
     * @param wind アクティブプレイヤーの風。
     */
    public void setActiveWind(final Wind wind) {
        if (wind != null) {
            _activeWind = wind;
        }
        else {
            _activeWind = Wind.TON;
        }
    }
    
    /**
     * アクティブプレイヤーの風を次に移す
     */
    public void setActiveWindToNext() {
        _activeWind = _activeWind.getNext();
    }
    
    /**
     * 副露後の打牌フラグを設定
     * 
     * @param afterCall 副露後の打牌フラグ。
     */
    public void setAfterCall(final boolean afterCall) {
        _afterCall = afterCall;
    }
    
    /**
     * 被副露牌インデックスを設定
     * 
     * @param wind 副露された風。
     */
    public void setCalledIndex(final Wind wind) {
        if (wind != null) {
            _riverTable.get(wind).setCalledIndex();
        }
    }
    
    /**
     * カンフラグを設定
     * 
     * @param callKan カンフラグ。
     */
    public void setCallKan(final boolean callKan) {
        _callKan = callKan;
    }
    
    /**
     * 和了情報を設定
     * 
     * @param playerWind プレイヤーの風。
     * @param isRon ロン和了か。
     * @param wind 和了情報。
     */
    public void setCompleteInfo(final Wind playerWind, final boolean isRon) {
        JanPai pai = getActiveTsumo();
        
        if (isRon) {
            pai = getActiveDiscard();
        }
        _completeInfo = getCompleteInfo(playerWind, pai, isRon);
    }
    
    /**
     * 牌山を設定
     * 
     * @param deck 牌山。
     */
    public void setDeck(final List<JanPai> deck) {
        if (deck != null) {
            _deck = deepCopyList(deck);
        }
        else {
            _deck.clear();
        }
    }
    
    /**
     * 牌山インデックスを設定
     * 
     * @param index 牌山インデックス。
     */
    public void setDeckIndex(final int index) {
        if (index > 0) {
            final int deckSize = _deck.size();
            if (index < deckSize) {
                _deckIndex = index;
            }
            else {
                _deckIndex = deckSize - 1;
            }
        }
        else {
            _deckIndex = 0;
        }
    }
    
    /**
     * 牌山の嶺上インデックスを設定(中国麻雀用)
     * 
     * @param index 嶺上牌山インデックス。
     */
    public void setDeckWallIndex(final int index) {
        if (index > 0) {
            _deckWallIndex = index;
        }
        else {
            _deckWallIndex = 0;
        }
    }
    
    /**
     * 場風を設定
     * 
     * @param wind 場風。
     */
    public void setFieldWind(final Wind wind) {
        if (wind != null) {
            _fieldWind = wind;
        }
        else {
            _fieldWind = Wind.TON;
        }
    }
    
    /**
     * 手牌を設定
     * 
     * @param wind 風。
     * @param hand 手牌。
     */
    public void setHand(final Wind wind, final Hand hand) {
        if (wind != null) {
            if (hand != null) {
                _handTable.put(wind, hand.clone());
            }
            else {
                _handTable.put(wind, new Hand());
            }
        }
    }
    
    /**
     * プレイヤーテーブルを設定
     * 
     * @param playerTable プレイヤーテーブル。
     */
    public void setPlayerTable(final Map<Wind, Player> playerTable) {
        if (playerTable != null) {
            _playerTable = deepCopyMap(playerTable);
        }
        else {
            _playerTable.clear();
        }
    }
    
    /**
     * 残り枚数を設定
     * 
     * @param remainCount 残り枚数。
     */
    public void setRemainCount(final int remainCount) {
        if (remainCount > 0) {
            _remainCount = remainCount;
        }
        else {
            _remainCount = 0;
        }
    }
    
    /**
     * 捨て牌リストを設定
     * 
     * @param wind 風。
     * @param river 捨て牌リスト。
     */
    public void setRiver(final Wind wind, final List<JanPai> river) {
        if (wind != null) {
            if (river != null) {
                _riverTable.put(wind, new River(river));
            }
            else {
                _riverTable.put(wind, new River());
            }
        }
    }
    
    /**
     * 王牌を設定
     * 
     * @param wanPai 王牌。
     */
    public void setWanPai(final WanPai wanPai) {
        if (wanPai != null) {
            _wanPai = wanPai.clone();
        }
        else {
            _wanPai = new WanPai();
        }
    }
    
    /**
     * 監視牌のリストを設定
     * 
     * @param watchingJanPaiList 監視牌のリスト。
     */
    public void setWatchingJanPaiList(final List<JanPai> watchingJanPaiList) {
        if (watchingJanPaiList != null) {
            _watchingJanPaiList = deepCopyList(watchingJanPaiList);
        }
        else {
        	_watchingJanPaiList.clear();
        }
    }
    
    /**
     * 和了可能情報を更新
     * 
     */
    public void updateCompletableInfo() {
        final Wind humanWind = getHumanWind();
        final JanPai activeTsumo = getActiveTsumo();
        final boolean contains = _completeWait.get(humanWind).contains(activeTsumo);
        
        if (!contains) {
            return;
        }
        updateCompletableInfo(humanWind);
    }
    
    /**
     * 待ち判定を更新
     * 
     * @param info ゲーム情報。
     * @param targetWind 更新対象の風。
     */
    public void updateWaitList(final Wind targetWind) {
        final Map<JanPai, Integer> hand = getHandMap(targetWind);
        final List<JanPai> completableJanPaiList = ChmHandCheckUtil.getCompletableJanPaiList(hand);
        _completeWait.put(targetWind, completableJanPaiList);
        _chiWait.put(targetWind, getChiWaitList(hand));
        _ponWait.put(targetWind, getPonWaitList(hand));
        
        updateCompletableInfo(targetWind);
    }
    
    
    
    /**
     * 指定した風の和了可能牌リストを消去
     * 
     * @param wind 風。
     */
    private void clearCompletableJanPaiList(final Wind wind) {
        final boolean isEmpty = _completableJanPaiTable.get(wind).isEmpty();
        
        if (!isEmpty) {
            _completableJanPaiTable.put(wind, new ArrayList<JanPai>());
        }
    }
    
    /**
     * 指定した風の和了可能巡目を消去
     * 
     * @param wind 風。
     */
    private void clearCompletableTurnCount(final Wind wind) {
        final int completableTurn = _completableTurnTable.get(wind);
        
        if (completableTurn != 0) {
            _completableTurnTable.put(wind, 0);
            notifyObservers(AnnounceFlag.END_OVER_TIED_POINT);
        }
    }
    
    /**
     * リストをディープコピー
     * 
     * @param sourceList 複製元。
     * @return 複製結果。
     */
    private <E> List<E> deepCopyList(final List<E> sourceList) {
        return new ArrayList<>(sourceList);
    }
    
    /**
     * マップをディープコピー
     * 
     * @param source 複製元。
     * @return 複製結果。
     */
    private <S, T> Map<S, T> deepCopyMap(final Map<S, T> source) {
        return new TreeMap<>(source);
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
     * 和了情報を取得
     * 
     * @param playerWind プレイヤーの風。
     * @param pai 和了牌。
     * @param isRon ロン和了か。
     * @param wind 和了情報。
     */
    private ChmCompleteInfo getCompleteInfo(final Wind playerWind, final JanPai pai, final boolean isRon) {
        // 和了牌の1枚分を事前に引いて判定
        final int remainCount = getVisibleOuts(pai) - 1;
        final CompleteType completeType = getCompleteType(playerWind, isRon, _callKan);
        final CompleteJanPai completePai = new CompleteJanPai(pai, remainCount, completeType);
        final Hand hand = getHand(playerWind);
        final Wind fieldWind = getFieldWind();
        
        return ChmHandCheckUtil.getCompleteInfo(hand, completePai, playerWind, fieldWind);
    }
    
    /**
     * 和了タイプを取得
     * 
     * @param playerWind プレイヤーの風。
     * @param isRon ロン和了か。
     * @param isKan カンか。
     * @return 和了タイプ。
     */
    private CompleteType getCompleteType(final Wind playerWind, final boolean isRon, final boolean isKan) {
        final boolean isMenZen = getHand(playerWind).isMenZen();
        final int remainCount = getRemainCount();
        
        if (isRon) {
            if (remainCount == 0) {
                if (isMenZen) {
                    return CompleteType.RON_MENZEN_HO_TEI;
                }
                else {
                    return CompleteType.RON_NOT_MENZEN_HO_TEI;
                }
            }
            else {
                if (isMenZen) {
                    return CompleteType.RON_MENZEN;
                }
                else {
                    return CompleteType.RON_NOT_MENZEN;
                }
            }
        }
        else {
            if (isKan) {
                if (isMenZen) {
                    return CompleteType.TSUMO_MENZEN_RIN_SYAN;
                }
                else {
                    return CompleteType.TSUMO_NOT_MENZEN_RIN_SYAN;
                }
            }
            else {
                if (remainCount == 0) {
                    if (isMenZen) {
                        return CompleteType.TSUMO_MENZEN_HAI_TEI;
                    }
                    else {
                        return CompleteType.TSUMO_NOT_MENZEN_HAI_TEI;
                    }
                }
                else {
                    if (isMenZen) {
                        return CompleteType.TSUMO_MENZEN;
                    }
                    else {
                        return CompleteType.TSUMO_NOT_MENZEN;
                    }
                }
            }
        }
    }
    
    /**
     * プレイヤーの手牌マップを取得
     * 
     * @param wind プレイヤーの風。
     * @return プレイヤーの手牌マップ。
     */
    private Map<JanPai, Integer> getHandMap(final Wind wind) {
        final Map<JanPai, Integer> hand = getHand(wind).getMenZenMap();
        JanPaiUtil.cleanJanPaiMap(hand);
        return hand;
    }
    
    /**
     * 8点縛りを超えた待ち牌を取得
     * 
     * @param wind 風。
     * @return 8点縛りを超えた待ち牌。
     */
    private List<JanPai> getOverTiedPointJanPaiList(final Wind wind) {
        final List<JanPai> completeWaitJanPaiList = _completeWait.get(wind);
        final List<JanPai> paiList = new ArrayList<>();
        
        for (final JanPai pai : completeWaitJanPaiList) {
            final int ronPoint = getCompleteInfo(wind, pai, true).getTotalPoint();
            
            if (ronPoint >= 8) {
                paiList.add(pai);
                continue;
            }
            final int tsumoPoint = getCompleteInfo(wind, pai, false).getTotalPoint();
            
            if (tsumoPoint >= 8) {
                paiList.add(pai);
            }
        }
        return paiList;
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
     * 河の残り枚数を取得
     * 
     * @param pai 牌。
     * @return 河の残り枚数。
     */
    private int getVisibleOuts(final JanPai pai) {
        final List<JanPai> paiList = Arrays.asList(pai);
        final Map<JanPai, Integer> outs = getVisibleOuts(paiList);
        return outs.get(pai);
    }
    
    /**
     * 河の残り枚数テーブルを取得
     * 
     * @param paiList 牌リスト。
     * @return 河の残り枚数テーブル。
     */
    private Map<JanPai, Integer> getVisibleOuts(final List<JanPai> paiList) {
        final Map<JanPai, Integer> outs = new TreeMap<>();
        
        for (final JanPai pai : paiList) {
            int visibleCount = 0;
            
            for (final Wind wind : Wind.values()) {
                final List<JanPai> river = getRiver(wind).get();
                final List<Integer> calledIndexList = getRiver(wind).getCalledIndexList();
                
                for (int count = 0; count < river.size(); count++) {
                    boolean isCalledIndex = false;
                    
                    for (final Integer index : calledIndexList) {
                        if (count == index) {
                            isCalledIndex = true;
                        }
                    }
                    
                    if (isCalledIndex) {
                        continue;
                    }
                    
                    if (pai.equals(river.get(count))) {
                        visibleCount++;
                    }
                }
                final List<MenTsu> fixedMenTsuList = getHand(wind).getFixedMenTsuList();
                
                for (final MenTsu fixedMenTsu : fixedMenTsuList) {
                    final boolean isCalled = fixedMenTsu.getMenTsuType().isCalled();
                    
                    if (isCalled) {
                        visibleCount += fixedMenTsu.getJanPaiCount(pai);
                    }
                }
            }
            outs.put(pai, 4 - visibleCount);
        }
        return outs;
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
     * 最後の下ヅモか(中国麻雀用)
     * 
     * @return 判定結果。
     */
    private boolean isLastShitatsumo() {
        if (_deckIndex != _deckWallIndex - 1) {
            return false;
        }
        if (isUwatsumo()) {
            return false;
        }
        return true;
    }
    
    /**
     * 牌山インデックスが嶺上インデックスと同じか(中国麻雀用)
     * 
     * @return 判定結果。
     */
    private boolean isSameIndex() {
        return _deckIndex == _deckWallIndex;
    }
    
    /**
     * 嶺上牌が上ヅモか(中国麻雀用)
     * 
     * @return 判定結果。
     */
    private boolean isUwatsumo() {
        if (_deckWallIndex % 2 == 0) {
            return true;
        }
        else {
            return false;
        }
    }
    
    /**
     * 指定した風の和了可能牌リストを設定
     * 
     * @param wind 風。
     * @param completableJanPaiList 待ち牌リスト。
     */
    private void setCompletableJanPaiList(final Wind wind, final List<JanPai> completableJanPaiList) {
        final List<JanPai> oldCompletableJanPaiList = _completableJanPaiTable.get(wind);
        final boolean isContainsAll = completableJanPaiList.containsAll(oldCompletableJanPaiList);
        
        if (isContainsAll) {
            final List<JanPai> addJanPaiList = deepCopyList(completableJanPaiList);
            addJanPaiList.removeAll(oldCompletableJanPaiList);
            
            for (final JanPai pai : addJanPaiList) {
                final int outs = getOutsOnConfirm(pai, wind);
                
                if (outs == 0) {
                    completableJanPaiList.remove(pai);
                }
            }
        }
        final boolean isEmpty = oldCompletableJanPaiList.isEmpty();
        final boolean isEquals = completableJanPaiList.equals(oldCompletableJanPaiList);
        
        _completableJanPaiTable.put(wind, completableJanPaiList);
        
        if (!isEmpty && !isEquals) {
            notifyObservers(AnnounceFlag.CHANGE_WAIT);
        }
    }
    
    /**
     * 指定した風の和了可能巡目を設定
     * 
     * @param wind 風。
     */
    private void setCompletableTurnCount(final Wind wind) {
        final int completableTurn = _completableTurnTable.get(wind);
        
        if (completableTurn == 0) {
            final int turnCount = _turnTable.get(wind);
            
            _completableTurnTable.put(wind, turnCount);
            notifyObservers(AnnounceFlag.OVER_TIED_POINT);
        }
    }
    
    /**
     * 指定した風の和了可能情報を更新
     * 
     * @param wind 風。
     */
    private void updateCompletableInfo(final Wind wind) {
        final List<JanPai> paiList = getOverTiedPointJanPaiList(wind);
        final boolean isEmpty = paiList.isEmpty();
        
        if (isEmpty) {
            clearCompletableJanPaiList(wind);
            clearCompletableTurnCount(wind);
            return;
        }
        final Map<JanPai, Integer> outsMap = getOutsOnConfirm(paiList, wind);
        boolean noOuts = true;
        
        for (final int outs : outsMap.values()) {
            if (outs != 0) {
                noOuts = false;
                break;
            }
        }
        
        if (noOuts) {
            notifyObservers(AnnounceFlag.OVER_TIED_POINT_AND_NO_OUTS);
            clearCompletableJanPaiList(wind);
            clearCompletableTurnCount(wind);
            return;
        }
        setCompletableJanPaiList(wind, paiList);
        setCompletableTurnCount(wind);
    }
    
    
    
    /**
     * 直前の捨て牌
     */
    private JanPai _activeDiscard = JanPai.HAKU;
    
    /**
     * 直前のツモ牌
     */
    private JanPai _activeTsumo = JanPai.HAKU;
    
    /**
     * アクティブな風
     */
    private Wind _activeWind = Wind.TON;
    
    /**
     * チーの待ち
     */
    private Map<Wind, List<JanPai>> _chiWait = Collections.synchronizedMap(new TreeMap<Wind, List<JanPai>>());
    
    /**
     * 和了情報
     */
    private ChmCompleteInfo _completeInfo = null;
    
    /**
     * 和了可能牌テーブル
     */
    private Map<Wind, List<JanPai>> _completableJanPaiTable = new TreeMap<>();
    
    /**
     * 和了可能巡目テーブル
     */
    private Map<Wind, Integer> _completableTurnTable = new TreeMap<>();
    
    /**
     * 和了の待ち
     */
    private Map<Wind, List<JanPai>> _completeWait = Collections.synchronizedMap(new TreeMap<Wind, List<JanPai>>());
    
    /**
     * 牌山
     */
    private List<JanPai> _deck = new ArrayList<>();
    
    /**
     * 牌山インデックス
     */
    private int _deckIndex = 0;
    
    /**
     * 牌山の嶺上インデックス(中国麻雀用)
     */
    private int _deckWallIndex = 0;
    
    /**
     * 場風
     */
    private Wind _fieldWind = Wind.TON;
    
    /**
     * 手牌テーブル
     */
    private Map<Wind, Hand> _handTable = new TreeMap<>();
    
    /**
     * プレイヤーテーブル
     */
    private Map<Wind, Player> _playerTable = new TreeMap<>();
    
    /**
     * ポンの待ち
     */
    private Map<Wind, List<JanPai>> _ponWait = Collections.synchronizedMap(new TreeMap<Wind, List<JanPai>>());
    
    /**
     * 残り枚数
     */
    private int _remainCount = 0;
    
    /**
     * 捨て牌テーブル
     */
    private Map<Wind, River> _riverTable = new TreeMap<>();
    
    /**
     * 巡目テーブル
     */
    private Map<Wind, Integer> _turnTable = new TreeMap<>();
    
    /**
     * 王牌
     */
    private WanPai _wanPai = new WanPai();
    
    /**
     * 監視牌のリスト
     */
    private List<JanPai> _watchingJanPaiList = new ArrayList<>();
    
    
    
    /**
     * 副露後の打牌フラグ
     */
    private volatile boolean _afterCall = false;
    
    /**
     * カンフラグ
     */
    private volatile boolean _callKan = false;
    
}

