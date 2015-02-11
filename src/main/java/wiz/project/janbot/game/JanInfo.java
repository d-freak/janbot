/**
 * JanInfo.java
 * 
 * @author Yuki
 */

package wiz.project.janbot.game;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.TreeMap;

import wiz.project.jan.Hand;
import wiz.project.jan.JanPai;
import wiz.project.jan.Wind;



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
            _riverTable.put(wind, new ArrayList<JanPai>());
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
            _wanPai = source._wanPai.clone();
            _fieldWind = source._fieldWind;
            _activeWind = source._activeWind;
            _remainCount = source._remainCount;
            _activeTsumo = source._activeTsumo;
            _activeDiscard = source._activeDiscard;
            
            for (final Map.Entry<Wind, Hand> entry : source._handTable.entrySet()) {
                _handTable.put(entry.getKey(), entry.getValue().clone());
            }
            for (final Map.Entry<Wind, List<JanPai>> entry : source._riverTable.entrySet()) {
                _riverTable.put(entry.getKey(), deepCopyList(entry.getValue()));
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
        _wanPai = new WanPai();
        _fieldWind = Wind.TON;
        _activeWind = Wind.TON;
        _remainCount = 0;
        _activeTsumo = JanPai.HAKU;
        _activeDiscard = JanPai.HAKU;
        
        for (final Wind wind : Wind.values()) {
            _playerTable.put(wind, new Player());
            _handTable.put(wind, new Hand());
            _riverTable.put(wind, new ArrayList<JanPai>());
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
        return getRiver(_activeWind);
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
    public List<JanPai> getRiver(final Wind wind) {
        if (wind != null) {
            return deepCopyList(_riverTable.get(wind));
        }
        else {
            return new ArrayList<>();
        }
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
     * 牌山インデックスを増加
     */
    public void increaseDeckIndex() {
        setDeckIndex(_deckIndex + 1);
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
        if (pai != null) {
            _activeTsumo = pai;
        }
        else {
            _activeTsumo = JanPai.HAKU;
        }
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
                _riverTable.put(wind, deepCopyList(river));
            }
            else {
                _riverTable.put(wind, new ArrayList<JanPai>());
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
    private Map<Wind, List<JanPai>> _riverTable = new TreeMap<>();
    
    /**
     * 直前のツモ牌
     */
    private JanPai _activeTsumo = JanPai.HAKU;
    
    /**
     * 直前の捨て牌
     */
    private JanPai _activeDiscard = JanPai.HAKU;
    
}

