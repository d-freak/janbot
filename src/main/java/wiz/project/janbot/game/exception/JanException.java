/**
 * JanException.java
 * 
 * @author Yuki
 */

package wiz.project.janbot.game.exception;



/**
 * 麻雀ゲームの例外
 */
public class JanException extends Exception {
    
    /**
     * コンストラクタ
     */
    public JanException() {
        super("[JanException]");
    }
    
    /**
     * コンストラクタ
     * 
     * @param message 例外メッセージ。
     */
    public JanException(final String message) {
        super(message);
    }
    
    /**
     * コンストラクタ
     * 
     * @param e 例外オブジェクト。
     */
    public JanException(final Throwable e) {
        super(e);
    }
    
    /**
     * コンストラクタ
     * 
     * @param message 例外メッセージ。
     * @param e 例外オブジェクト。
     */
    public JanException(final String message, final Throwable e) {
        super(message, e);
    }
    
    
    
    /**
     * シリアルバージョン
     */
    private static final long serialVersionUID = 1L;
    
}

