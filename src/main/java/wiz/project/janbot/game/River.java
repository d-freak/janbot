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
public final class River {
    
    /**
     * コンストラクタ
     */
    public River() {
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
    private int richiIndex = 0;
    
    /**
     * 被副露牌インデックス
     */
    private List<Integer> _calledIndexList = new ArrayList<>();
    
}

