/**
 * HistoryType.java
 * 
 * @author Masasutzu
 */

package wiz.project.janbot.game;



/**
 * 履歴タイプ
 */
public enum HistoryType {
    
    /**
     * 開始処理 (ソロ)
     */
    JPM("jan s"),
    
    /**
     * 開始処理 (中国麻雀・ソロ)
     */
    CHM("chm s"),
    
    /**
     * 打牌処理 (ツモ切り)
     */
    DISCARD_TSUMO("jan d"),
    
    /**
     * 打牌処理 (ツモ切り)か副露せずに続行
     */
    DISCARD_TSUMO_OR_CONTINUE("jan d"),
    
    /**
     * 打牌処理 (手出し)
     */
    DISCARD("jan d "),
    
    /**
     * 副露せずに続行
     */
    CONTINUE("jan d"),
    
    /**
     * 和了
     */
    COMPLETE("jan hu"),
    
    /**
     * ロン
     */
    RON("jan ron"),
    
    /**
     * ツモ
     */
    TSUMO("jan tsumo"),
    
    /**
     * チー
     */
    CHI("jan chi "),
    
    /**
     * ポン
     */
    PON("jan pon "),
    
    /**
     * 大明カン
     */
    KAN_LIGHT("jan kan "),
    
    /**
     * 加カン
     */
    KAN_ADD("jan kan "),
    
    /**
     * 暗カン
     */
    KAN_DARK("jan kan ");
    
    
    
    HistoryType(final String command) {
        _command = command;
    }
    
    
    
    @Override
    public String toString() {
        return _command;
    }
    
    
    
    private String _command = "";
    
}

