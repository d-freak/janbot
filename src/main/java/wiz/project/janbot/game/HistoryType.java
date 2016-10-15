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
    JPM,
    
    /**
     * 開始処理 (中国麻雀・ソロ)
     */
    CHM,
    
    /**
     * 開始処理 (台湾麻雀・ソロ)
     */
    TWM,
    
    /**
     * 打牌処理 (ツモ切り)
     */
    DISCARD_TSUMO,
    
    /**
     * 打牌処理 (手出し)
     */
    DISCARD,
    
    /**
     * 副露せずに続行
     */
    CONTINUE,
    
    /**
     * ロン
     */
    RON,
    
    /**
     * ツモ
     */
    TSUMO,
    
    /**
     * チー
     */
    CHI,
    
    /**
     * ポン
     */
    PON,
    
    /**
     * 大明カン
     */
    KAN_LIGHT,
    
    /**
     * 加カン
     */
    KAN_ADD,
    
    /**
     * 暗カン
     */
    KAN_DARK,
    
}

