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
    public StatisticsParam(final String name, final String option) {
        for (final String string : option.split(" ")) {
            if (string.matches(REGEXP_RENGE)) {
                try {
                    _start = Integer.parseInt(string.replaceFirst("-\\d*+", ""));
                }
                catch (final NumberFormatException e) {
                    // _startを更新せず継続
                }
                
                try {
                    _end = Integer.parseInt(string.replaceFirst("\\d*+-", ""));
                }
                catch (final NumberFormatException e) {
                    // _endを更新せず継続
                }
                continue;
            }
            else if (string.matches(REGEXP_MAX_COUNT)) {
                continue;
            }
            else if (string.matches(REGEXP_MINIMUM_POINT)) {
                continue;
            }
            _name += string;
        }
        
        if ("".equals(_name)) {
        	_name = name;
        }
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
        return _name;
    }
    
    /**
     * 開始値を取得
     */
    public int getStart() {
        return _start;
    }
    
    
    
    /**
     * 最大数の正規表現
     */
    protected static final String REGEXP_MAX_COUNT     = "-c\\d*+";
    
    /**
     * 最小点の正規表現
     */
    protected static final String REGEXP_MINIMUM_POINT = "-p\\d*+";
    
    
    
    /**
     * 範囲指定の正規表現
     */
    private static final String REGEXP_RENGE           = "\\d*+-\\d*+";
    
    
    
    /**
     * 終了値
     */
    private int _end = 0;
    
    /**
     * プレイヤー名
     */
    private String _name = "";
    
    /**
     * 開始値
     */
    private int _start = 0;
    
}

