/**
 * ChmJanController.java
 * 
 * @author Masasutzu
 */

package wiz.project.janbot.statistics;

import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;



/**
 * ゲーム統計
 */
public final class Statistics {
    
    /**
     * コンストラクタ
     */
    @SuppressWarnings("unchecked")
    public Statistics(final String playerName) throws DocumentException {
        final String path = "./" + playerName + ".xml";
        final SAXReader reader = new SAXReader();
        final Document readDocument = reader.read(path);
        final List<Node> completeTypeList = readDocument.selectNodes("/results/result/completeType");
        final List<Node> completeTurnList = readDocument.selectNodes("/results/result/completeTurn");
        _completeCount = 0;
        _tsumoCount = 0;
        
        for (final Node node : completeTypeList) {
            final String type = node.getStringValue();
            
            if (type.equals("tsumo")) {
                _tsumoCount++;
                _completeCount++;
            }
            else if (type.equals("ron")) {
                _completeCount++;
            }
        }
        _turnSum = 0;
        
        for (final Node node : completeTurnList) {
            final String turn = node.getStringValue();
            
            if (!turn.equals("-")) {
                _turnSum += Integer.parseInt(turn);
            }
        }
        _playCount = completeTypeList.size();
    }
    
    
    
    /**
     * 和了率
     */
    public String completeRate() {
        final double completeRate = (double) _completeCount * 100 / (double) _playCount;
        final String completeRateString = String.format("%.2f", completeRate);
        
        return "和了率: " + completeRateString + " % (" + _completeCount + "/" + _playCount + ")";
    }
    
    /**
     * ツモ率
     */
    public String tsumoRate() {
        final double tsumoRate = (double) _tsumoCount  * 100 / (double) _completeCount;
        final String tsumoRateString = String.format("%.2f", tsumoRate);
        
        return "ツモ率: " + tsumoRateString + " % (" + _tsumoCount + "/" + _completeCount + ")";
    }
    
    /**
     * 平均和了巡目
     */
    public String turnAverage() {
        final double turnAverage = (double) _turnSum / (double) _completeCount;
        final String turnAverageString = String.format("%.2f", turnAverage);
        
        return "平均和了巡目: " + turnAverageString + " 巡目";
    }
    
    
    
    /**
     * 和了回数
     */
    private int _completeCount = 0;
    
    /**
     * ゲーム回数
     */
    private int _playCount = 0;
    
    /**
     * ツモ回数
     */
    private int _tsumoCount = 0;
    
    /**
     * 和了巡目の合計
     */
    private int _turnSum = 0;
    
}
