/**
 * WanPai.java
 * 
 * @author Yuki
 */

package wiz.project.janbot.game;

import java.util.ArrayList;
import java.util.List;

import wiz.project.jan.JanPai;



/**
 * 王牌
 */
public final class WanPai implements Cloneable {
    
    /**
     * コンストラクタ
     */
    public WanPai() {
    }
    
    /**
     * コンストラクタ
     * 
     * @param sourceList 王牌を構成する牌リスト。
     */
    public WanPai(final List<JanPai> sourceList) {
        if (sourceList == null) {
            throw new NullPointerException("Source list is null.");
        }
        if (sourceList.size() != 14) {
            throw new IllegalArgumentException("Invalid source list size - " + sourceList.size());
        }
        
        _wallList = new ArrayList<>(sourceList.subList(0, 4));
        _doraPrevList = new ArrayList<>(sourceList.subList(4, 9));
        _uraDoraPrevList = new ArrayList<>(sourceList.subList(9, 14));
        _doraCount = 1;
    }
    
    /**
     * コピーコンストラクタ
     * 
     * @param source 複製元。
     */
    public WanPai(final WanPai source) {
        if (source != null) {
            _wallList = deepCopyList(source._wallList);
            _wallIndex = source._wallIndex;
            _doraPrevList = deepCopyList(source._doraPrevList);
            _uraDoraPrevList = deepCopyList(source._uraDoraPrevList);
            _doraCount = source._doraCount;
        }
    }
    
    
    
    /**
     * オブジェクトを複製
     * 
     * @return 複製結果。
     */
    @Override
    public WanPai clone() {
        return new WanPai(this);
    }
    
    /**
     * ドラリストを取得
     * 
     * @return ドラリスト。(表示牌ではない)
     */
    public List<JanPai> getDoraList() {
        final List<JanPai> doraList = new ArrayList<>();
        for (int i = 0; i < _doraCount; i++) {
            doraList.add(_doraPrevList.get(i).getNext());
        }
        return doraList;
    }
    
    /**
     * ドラ表字牌リストを取得
     * 
     * @return ドラ表字牌リスト。
     */
    public List<JanPai> getDoraPrevList() {
        return deepCopyList(_doraPrevList);
    }
    
    /**
     * 裏ドラリストを取得
     * 
     * @return 裏ドラリスト。(表示牌ではない)
     */
    public List<JanPai> getUraDoraList() {
        final List<JanPai> uraDoraList = new ArrayList<>();
        for (int i = 0; i < _doraCount; i++) {
            uraDoraList.add(_uraDoraPrevList.get(i).getNext());
        }
        return uraDoraList;
    }
    
    /**
     * 裏ドラ表字牌リストを取得
     * 
     * @return 裏ドラ表字牌リスト。
     */
    public List<JanPai> getUraDoraPrevList() {
        return deepCopyList(_uraDoraPrevList);
    }
    
    /**
     * 嶺上牌を取得
     * 
     * @return 嶺上牌。
     */
    public JanPai getWall() {
        return _wallList.get(_wallIndex++);
    }
    
    /**
     * カン可能数の限界か
     * 
     * @return 判定結果。
     */
    public boolean isLimit() {
        return _wallIndex == 4;
    }
    
    /**
     * 新ドラをめくる
     */
    public void openNewDora() {
        if (_doraCount < 5) {
            _doraCount++;
        }
    }
    
    /**
     * 初期状態に戻す
     */
    public void reset() {
        _wallIndex = 0;
        _doraCount = 1;
    }
    
    /**
     * 文字列に変換
     * 
     * @return 変換結果。
     */
    @Override
    public String toString() {
        final List<JanPai> sourceList = new ArrayList<>();
        sourceList.addAll(_wallList);
        sourceList.addAll(_doraPrevList);
        sourceList.addAll(_uraDoraPrevList);
        return sourceList.toString();
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
     * 嶺上牌リスト
     */
    private List<JanPai> _wallList = new ArrayList<>();
    
    /**
     * 嶺上牌インデックス
     */
    private int _wallIndex = 0;
    
    /**
     * ドラ表字牌リスト
     */
    private List<JanPai> _doraPrevList = new ArrayList<>();
    
    /**
     * ドラ表字数
     */
    private int _doraCount = 0;
    
    /**
     * 裏ドラ表字牌リスト
     */
    private List<JanPai> _uraDoraPrevList = new ArrayList<>();
    
}

