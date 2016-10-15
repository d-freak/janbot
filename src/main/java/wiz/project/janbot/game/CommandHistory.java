/**
 * CommandHistory.java
 * 
 * @Author
 *   Masasutzu
 */

package wiz.project.janbot.game;



/**
 * コマンド履歴
 */
final class CommandHistory {
    
    /**
     * コンストラクタ
     */
    public CommandHistory(final HistoryType historyType, final JanInfo info) {
        _historyType = historyType;
        _info = info;
    }
    
    /**
     * コンストラクタ
     */
    public CommandHistory(final HistoryType historyType, final JanInfo info, final String pai) {
        _historyType = historyType;
        _info = info;
        _pai = pai;
    }
    
    
    
    /**
     * 履歴タイプを取得
     */
    public HistoryType getHistoryType() {
        return _historyType;
    }
    
    /**
     * 麻雀ゲームの情報を取得
     */
    public JanInfo getJanInfo() {
        return _info;
    }
    
    /**
     * 牌を取得
     */
    public String getJanPai() {
        return _pai;
    }
    
    
    
    /**
     * 履歴タイプ
     */
    private HistoryType _historyType = HistoryType.DISCARD_TSUMO;
    
    /**
     * 麻雀ゲームの情報
     */
    private JanInfo _info = new JanInfo();
    
    /**
     * 牌
     */
    private String _pai = "";
    
}

