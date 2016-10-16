/**
 * AnnounceParam.java
 * 
 * @Author
 *   Masasutzu
 */

package wiz.project.janbot.game;

import java.util.LinkedList;
import java.util.List;



/**
 * コマンド履歴パラメータ (immutable)
 */
final class HistoryParam {
    
    /**
     * コンストラクタ
     */
    public HistoryParam(final List<CommandHistory> historyList) {
        _historyList = historyList;
    }
    
    
    
    /**
     * コマンド履歴リストを取得
     */
    public List<CommandHistory> getHistoryList() {
        return _historyList;
    }
    
    
    
    /**
     * コマンド履歴リスト
     */
    private List<CommandHistory> _historyList = new LinkedList<>();
    
}

