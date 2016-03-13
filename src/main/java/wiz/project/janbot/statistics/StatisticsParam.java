/**
 * StatisticsParam.java
 * 
 * @Author
 *   D-freak
 */

package wiz.project.janbot.statistics;



/**
 * 統計パラメータ (immutable)
 */
public class StatisticsParam {
    
    /**
     * コンストラクタ
     */
    public StatisticsParam(final String playerName, final int start, final int end) {
        _playerName = playerName;
        _start = start;
        _end = end;
    }
    
    
    
    /**
     * 終了値を取得
     */
    public int getEnd() {
        return _end;
    }
    
    /**
     * プレイヤー名を取得
     */
    public String getPlayerName() {
        return _playerName;
    }
    
    /**
     * 開始値を取得
     */
    public int getStart() {
        return _start;
    }
    
    
    
    /**
     * 終了値
     */
    private int _end = 0;
    
    /**
     * プレイヤー名
     */
    private String _playerName = "";
    
    /**
     * 開始値
     */
    private int _start = 0;
}

