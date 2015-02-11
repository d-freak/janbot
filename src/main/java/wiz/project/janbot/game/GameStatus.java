/**
 * GameStatus.java
 * 
 * @author Yuki
 */

package wiz.project.janbot.game;



/**
 * ゲームの状態
 */
public enum GameStatus {
    
    /**
     * ソロプレイ
     */
    PLAYING_SOLO,
    
    /**
     * 対戦プレイ
     */
    PLAYING_VS,
    
    /**
     * 待機
     */
    IDLE;
    
    
    
    /**
     * 待機中か
     * 
     * @return 判定結果。
     */
    public boolean isIdle() {
        return this == IDLE;
    }
    
}

