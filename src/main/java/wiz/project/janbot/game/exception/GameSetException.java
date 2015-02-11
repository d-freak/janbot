/**
 * GameSetException.java
 * 
 * @author Yuki
 */

package wiz.project.janbot.game.exception;

import wiz.project.janbot.game.GameSetStatus;



/**
 * ゲーム終了例外
 */
public final class GameSetException extends JanException {
    
    /**
     * コンストラクタ
     * 
     * @param status ゲーム終了状態。
     */
    public GameSetException(final GameSetStatus status) {
        super("[GameSetException]");
        setStatus(status);
    }
    
    
    
    /**
     * ゲーム終了状態を取得
     * 
     * @return ゲーム終了状態。
     */
    public GameSetStatus getStatus() {
        return _status;
    }
    
    
    
    /**
     * ゲーム終了状態を設定
     * 
     * @param status ゲーム終了状態。
     */
    private void setStatus(final GameSetStatus status) {
        if (status != null) {
            _status = status;
        }
        else {
            _status = GameSetStatus.GAME_OVER;
        }
    }
    
    
    
    /**
     * シリアルバージョン
     */
    private static final long serialVersionUID = 1L;
    
    
    
    /**
     * ゲーム終了状態
     */
    private GameSetStatus _status = GameSetStatus.GAME_OVER;
    
}

