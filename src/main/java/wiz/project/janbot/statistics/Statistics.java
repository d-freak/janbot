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

import wiz.project.janbot.game.exception.InvalidInputException;



/**
 * ゲーム統計
 */
public final class Statistics {
    
    /**
     * コンストラクタ
     */
    @SuppressWarnings("unchecked")
    public Statistics(final String playerName, final int start, final int end) throws DocumentException, InvalidInputException {
        final String path = "./" + playerName + ".xml";
        final SAXReader reader = new SAXReader();
        final Document readDocument = reader.read(path);
        final List<Node> completableTurnList = readDocument.selectNodes("/results/result/completableTurn");
        final List<Node> completeTypeList = readDocument.selectNodes("/results/result/completeType");
        final List<Node> completeTurnList = readDocument.selectNodes("/results/result/completeTurn");
        final List<Node> pointList = readDocument.selectNodes("/results/result/point");
        final int size = completeTypeList.size();
        final int countStart = start != 0 ? start - 1 : 0;
        final int countEnd = end != 0 ? end : size;
        
        if (countStart >= countEnd || countEnd > size) {
            throw new InvalidInputException("start is greater or equal end");
        }
        
        for (int count = countStart; count < countEnd; count++) {
            final String completableTurn = completableTurnList.get(count).getStringValue();
            
            if (!completableTurn.equals("-")) {
                _completableCount++;
                _completableTurnSum += Integer.parseInt(completableTurn);
            }
        }
        
        for (int count = countStart; count < countEnd; count++) {
            final String type = completeTypeList.get(count).getStringValue();
            
            if (type.equals("tsumo")) {
                _tsumoCount++;
                _completeCount++;
            }
            else if (type.equals("ron")) {
                _completeCount++;
            }
        }
        
        for (int count = countStart; count < countEnd; count++) {
            final String turn = completeTurnList.get(count).getStringValue();
            
            if (!turn.equals("-")) {
                _turnSum += Integer.parseInt(turn);
            }
        }
        
        for (int count = countStart; count < countEnd; count++) {
            final String pointString = pointList.get(count).getStringValue();
            
            if (!pointString.equals("-")) {
                final int point = Integer.parseInt(pointString);
                
                _pointSum += point;
                
                final String type = completeTypeList.get(count).getStringValue();
                int getPoint = 0;
                
                if (type.equals("tsumo")) {
                    getPoint = (point + 8) * 3;
                }
                else if (type.equals("ron")) {
                    getPoint = point + 8 * 3;
                }
                _getPointSum += getPoint;
            }
        }
        _playCount = countEnd - countStart;
    }
    
    
    
    /**
     * 聴牌率
     */
    public String completableRate() {
        final double completableRate = (double) _completableCount * 100 / (double) _playCount;
        final String completableRateString = String.format("%.2f", completableRate);
        
        return "聴牌率: " + completableRateString + " % (" + _completableCount + "/" + _playCount + ")";
    }
    
    /**
     * 平均聴牌巡目
     */
    public String completableTurnAverage() {
        String completableTurnAverageString = "-";
        
        if (_completableCount != 0) {
            final double completableTurnAverage = (double) _completableTurnSum / (double) _completableCount;
            completableTurnAverageString = String.format("%.2f", completableTurnAverage);
        }
        return "平均聴牌巡目: " + completableTurnAverageString + " 巡目";
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
     * 平均獲得点数
     */
    public String getPointAverage() {
        String getPointAverageString = "-";
        
        if (_completeCount != 0) {
            final double getPointAverage = (double) _getPointSum / (double) _completeCount;
            getPointAverageString = String.format("%.2f", getPointAverage);
        }
        return "平均獲得点数: " + getPointAverageString + " 点";
    }
    
    /**
     * 平均点数
     */
    public String pointAverage() {
        String pointAverageString = "-";
        
        if (_completeCount != 0) {
            final double pointAverage = (double) _pointSum / (double) _completeCount;
            pointAverageString = String.format("%.2f", pointAverage);
        }
        return "平均点数: " + pointAverageString + " 点";
    }
    
    /**
     * ツモ率
     */
    public String tsumoRate() {
        String tsumoRateString = "-";
        
        if (_completeCount != 0) {
            final double tsumoRate = (double) _tsumoCount  * 100 / (double) _completeCount;
            tsumoRateString = String.format("%.2f", tsumoRate);
        }
        return "ツモ率: " + tsumoRateString + " % (" + _tsumoCount + "/" + _completeCount + ")";
    }
    
    /**
     * 平均和了巡目
     */
    public String turnAverage() {
        String turnAverageString = "-";
        
        if (_completeCount != 0) {
            final double turnAverage = (double) _turnSum / (double) _completeCount;
            turnAverageString = String.format("%.2f", turnAverage);
        }
        return "平均和了巡目: " + turnAverageString + " 巡目";
    }
    
    
    
    /**
     * 聴牌回数
     */
    private int _completableCount = 0;
    
    /**
     * 聴牌巡目の合計
     */
    private int _completableTurnSum = 0;
    
    /**
     * 和了回数
     */
    private int _completeCount = 0;
    
    /**
     * 獲得点数の合計
     */
    private int _getPointSum = 0;
    
    /**
     * ゲーム回数
     */
    private int _playCount = 0;
    
    /**
     * 点数の合計
     */
    private int _pointSum = 0;
    
    /**
     * ツモ回数
     */
    private int _tsumoCount = 0;
    
    /**
     * 和了巡目の合計
     */
    private int _turnSum = 0;
    
}

