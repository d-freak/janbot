/**
 * CallableException.java
 * 
 * @author Yuki
 */

package wiz.project.janbot.game.exception;

import java.util.ArrayList;
import java.util.List;

import wiz.project.janbot.game.CallType;



/**
 * 副露例外
 */
public final class CallableException extends JanException {
    
    /**
     * コンストラクタ
     * 
     * @param typeList 副露タイプリスト。
     */
    public CallableException(final List<CallType> typeList) {
        super("[CallableException]");
        setTypeList(typeList);
    }
    
    
    
    /**
     * 副露タイプリストを取得
     * 
     * @return 副露タイプリスト。
     */
    public List<CallType> getTypeList() {
        return deepCopyList(_typeList);
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
     * 副露タイプリストを設定
     * 
     * @param typeList 副露タイプリスト。
     */
    private void setTypeList(final List<CallType> typeList) {
        if (typeList != null) {
            _typeList = deepCopyList(typeList);
        }
        else {
            _typeList.clear();
        }
    }
    
    
    
    /**
     * シリアルバージョン
     */
    private static final long serialVersionUID = 1L;
    
    
    
    /**
     * 副露タイプリスト
     */
    private List<CallType> _typeList = new ArrayList<>();
    
}

