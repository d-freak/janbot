/**
 * YakuStatisticsParam.java
 * 
 * @Author
 *   D-freak
 */

package wiz.project.janbot.statistics;



/**
 * 役統計パラメータ (immutable)
 */
public final class YakuStatisticsParam extends StatisticsParam {
    
    /**
     * コンストラクタ
     */
    public YakuStatisticsParam(final String playerName, final int start, final int end, final int maxCount, final int minimumPoint) {
        super(playerName, start, end);
        _maxCount = maxCount;
        _minimumPoint = minimumPoint;
    }
    
    
    
    /**
     * 最大数を取得
     */
    public int getMaxCount() {
        return _maxCount;
    }
    
    /**
     * 最小点を取得
     */
    public int getMinimumPoint() {
        return _minimumPoint;
    }
    
    
    
    /**
     * 最大数
     */
    private int _maxCount = Integer.MAX_VALUE;
    
    /**
     * 最小点
     */
    private int _minimumPoint = 0;
    
}

