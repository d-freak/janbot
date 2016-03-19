/**
 * YakuParam.java
 * 
 * @Author
 *   D-freak
 */

package wiz.project.janbot.statistics;



/**
 * 役統計パラメータ (immutable)
 */
public final class YakuParam extends StatisticsParam {
    
    /**
     * コンストラクタ
     */
    public YakuParam(final String playerName, final String option) {
        super(playerName, option);
        
        for (final String string : option.split(" ")) {
            if (string.matches(REGEXP_MAX_COUNT)) {
                try {
                    _maxCount = Integer.parseInt(string.replaceFirst("-c", ""));
                }
                catch (final NumberFormatException e) {
                    // maxCountを更新せず継続
                }
                continue;
            }
            else if (string.matches(REGEXP_MINIMUM_POINT)) {
                try {
                    _minimumPoint = Integer.parseInt(string.replaceFirst("-p", ""));
                }
                catch (final NumberFormatException e) {
                    // minimumPointを更新せず継続
                }
                continue;
            }
        }
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

