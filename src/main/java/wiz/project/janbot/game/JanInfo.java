/**
 * JanInfo.java
 * 
 * @author Yuki
 */

package wiz.project.janbot.game;

import java.util.ArrayList;
import java.util.Arrays;
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
            _completableTurnTable.put(wind, 0);
            _turnTable.put(wind, 0);
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
            
            for (final Map.Entry<Wind, Hand> entry : source._handTable.entrySet()) {
                _handTable.put(entry.getKey(), entry.getValue().clone());
            }
            for (final Entry<Wind, River> entry : source._riverTable.entrySet()) {
                _riverTable.put(entry.getKey(), entry.getValue().clone());
            }
            for (final Entry<Wind, Integer> entry : source._completableTurnTable.entrySet()) {
            	_completableTurnTable.put(entry.getKey(), entry.getValue());
            }
            for (final Entry<Wind, Integer> entry : source._turnTable.entrySet()) {
                _turnTable.put(entry.getKey(), entry.getValue());
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
        
        for (final Wind wind : Wind.values()) {
            _playerTable.put(wind, new Player());
            _handTable.put(wind, new Hand());
            _riverTable.put(wind, new River());
            _completableTurnTable.put(wind, 0);
            _turnTable.put(wind, 0);
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
     * 和了牌の残り枚数を取得
     * 
     * @param pai 和了牌。
     * @return 和了牌の残り枚数。
     */
    public int getCompleteOuts(final JanPai pai) {
        final int completeOuts = getVisibleOuts(pai) - 1;
        
        return completeOuts;
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
        final JanPai activeTsumo = getActiveTsumo();
        final Integer activeTsumoCount = outs.get(activeTsumo);
        
        if (activeTsumoCount != null) {
            outs.put(activeTsumo, activeTsumoCount - 1);
        }
        return outs;
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
     * 河の残り枚数を取得
     * 
     * @param pai 牌。
     * @return 河の残り枚数。
     */
    public int getVisibleOuts(final JanPai pai) {
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
    public Map<JanPai, Integer> getVisibleOuts(final List<JanPai> paiList) {
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
     * 指定した風の和了可能巡目を設定
     * 
     * @param wind 風。
     * @param completableJanPaiList 待ち牌リスト。
     */
    public void setCompletableTurnCount(final Wind wind, final List<JanPai> completableJanPaiList) {
        final int completableTurn = _completableTurnTable.get(wind);
        
        if (completableTurn != 0) {
            return;
        }
        final boolean isOverTiedPoint = isOverTiedPoint(wind, completableJanPaiList);
        
        if (!isOverTiedPoint) {
            return;
        }
        final int turnCount = _turnTable.get(wind);
        _completableTurnTable.put(wind, turnCount);
        
        final AnnounceParam param = new AnnounceParam(AnnounceFlag.IS_OVER_TIED_POINT, turnCount);
        notifyObservers(param);
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
     * 和了情報を取得
     * 
     * @param playerWind プレイヤーの風。
     * @param pai 和了牌。
     * @param isRon ロン和了か。
     * @param wind 和了情報。
     */
    private ChmCompleteInfo getCompleteInfo(final Wind playerWind, final JanPai pai, final boolean isRon) {
        final int remainCount = getCompleteOuts(pai);
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
     * 8点縛り超えか
     * 
     * @param wind 風。
     * @param completableJanPaiList 待ち牌リスト。
     * @return 判定結果。
     */
    private boolean isOverTiedPoint(final Wind wind, final List<JanPai> completableJanPaiList) {
        for (final JanPai pai : completableJanPaiList) {
            final ChmCompleteInfo completeInfo = getCompleteInfo(wind, pai, true);
            final int totalPoint = completeInfo.getTotalPoint();
            
            if (totalPoint >= 7) {
                return true;
            }
            final boolean isMenZen = getHand(wind).isMenZen();
            
            if (isMenZen && totalPoint >= 4) {
                return true;
            }
        }
        return false;
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
     * プレイヤーテーブル
     */
    private Map<Wind, Player> _playerTable = new TreeMap<>();
    
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
     * 王牌
     */
    private WanPai _wanPai = new WanPai();
    
    /**
     * 場風
     */
    private Wind _fieldWind = Wind.TON;
    
    /**
     * アクティブな風
     */
    private Wind _activeWind = Wind.TON;
    
    /**
     * 残り枚数
     */
    private int _remainCount = 0;
    
    /**
     * 手牌テーブル
     */
    private Map<Wind, Hand> _handTable = new TreeMap<>();
    
    /**
     * 捨て牌テーブル
     */
    private Map<Wind, River> _riverTable = new TreeMap<>();
    
    /**
     * 和了可能巡目テーブル
     */
    private Map<Wind, Integer> _completableTurnTable = new TreeMap<>();
    
    /**
     * 巡目テーブル
     */
    private Map<Wind, Integer> _turnTable = new TreeMap<>();
    
    /**
     * 直前のツモ牌
     */
    private JanPai _activeTsumo = JanPai.HAKU;
    
    /**
     * 直前の捨て牌
     */
    private JanPai _activeDiscard = JanPai.HAKU;
    
    /**
     * 監視牌のリスト
     */
    private List<JanPai> _watchingJanPaiList = new ArrayList<>();
    
    /**
     * 和了情報
     */
    private ChmCompleteInfo _completeInfo = null;
    
    /**
     * カンフラグ
     */
    private volatile boolean _callKan = false;
    
}

