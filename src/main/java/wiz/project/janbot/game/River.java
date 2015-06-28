/**
 * River.java
 * 
 * @author Yuki
 */

package wiz.project.janbot.game;

import java.util.ArrayList;
import java.util.List;

import wiz.project.jan.JanPai;



/**
 * 捨て牌
 */
public final class River implements Cloneable {
    
    /**
     * コンストラクタ
     */
    public River() {
    }
    
    /**
     * コンストラクタ
     * 
     * @param paiList 捨て牌リスト。
     */
    public River(final List<JanPai> paiList) {
        this();
        _paiList.addAll(paiList);
    }
    
    /**
     * コピーコンストラクタ
     * 
     * @param source 複製元オブジェクト。
     */
    public River(final River source) {
        if (source != null) {
            _paiList = deepCopyList(source._paiList);
            _richiIndex = source._richiIndex;
            _calledIndexList = deepCopyList(source._calledIndexList);
        }
    }
    
    
    
    /**
     * 捨て牌を追加
     * 
     * @param pai 捨て牌。
     */
    public void add(final JanPai pai) {
        _paiList.add(pai);
    }
    
    /**
     * 自分自身を複製 (ディープコピー)
     * 
     * @return 複製結果。
     */
    @Override
    public River clone() {
        return new River(this);
    }
    
    /**
     * 捨て牌リストを取得
     * 
     * @return 捨て牌リスト。
     */
    public List<JanPai> get() {
        return _paiList;
    }
    
    /**
     * 被副露牌インデックスを取得
     * 
     * @return 被副露牌インデックス。
     */
    public List<Integer> getCalledIndexList() {
        return _calledIndexList;
    }
    
    /**
     * 被副露牌インデックスを設定
     */
    public void setCalledIndex() {
        _calledIndexList.add(_paiList.size());
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
     * 捨て牌リスト
     */
    private List<JanPai> _paiList = new ArrayList<>();
    
    /**
     * リーチ宣言牌インデックス
     */
    private int _richiIndex = 0;
    
    /**
     * 被副露牌インデックス
     */
    private List<Integer> _calledIndexList = new ArrayList<>();
    
}

