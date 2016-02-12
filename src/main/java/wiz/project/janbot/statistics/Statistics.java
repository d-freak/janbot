/**
 * ChmJanController.java
 * 
 * @author Masasutzu
 */

package wiz.project.janbot.statistics;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import wiz.project.janbot.game.exception.InvalidInputException;



/**
 * ゲーム統計
 */
public final class Statistics {
    
    /**
     * コンストラクタ
     */
    public Statistics(final String playerName, final int start, final int end) throws DocumentException, InvalidInputException {
        final String path = "./" + playerName + ".xml";
        final SAXReader reader = new SAXReader();
        final Document readDocument = reader.read(path);
        final Element readRoot = readDocument.getRootElement();
        final int size = readRoot.elements().size();
        final int countStart = start != 0 ? start - 1 : 0;
        final int countEnd = end != 0 ? end : size;
        
        if (countStart >= countEnd || countEnd > size) {
            throw new InvalidInputException("start is greater or equal end");
        }
        int count = 0;
        
        for (final Object element : readRoot.elements()) {
            if (count < countStart) {
                count++;
                continue;
            }
            else if (count == countEnd) {
                break;
            }
            final Element readResult = (Element) element;
            String completeType = "";
            
            for (final Object e : readResult.elements()) {
                final Element data = (Element) e;
                final String valueString = data.getStringValue();
                
                if (!valueString.equals("-")) {
                    final String name = data.getName();
                    
                    switch (name) {
                    case "completableTurn":
                        final int completableTurn = Integer.parseInt(valueString);
                        
                        _completableCount++;
                        _completableTurnSum += completableTurn;
                        
                        if (completableTurn <= 6) {
                            _until6thTurnCount++;
                        }
                        
                        if (completableTurn <= 12) {
                            _until12thTurnCount++;
                        }
                        break;
                    case "completeType":
                        completeType = valueString;
                        
                        if (valueString.equals("tsumo")) {
                            _tsumoCount++;
                            _completeCount++;
                        }
                        else if (valueString.equals("ron")) {
                            _completeCount++;
                        }
                        break;
                    case "completeTurn":
                        _turnSum += Integer.parseInt(valueString);
                        break;
                    case "point":
                        final int point = Integer.parseInt(valueString);
                        int getPoint = 0;
                        
                        _pointSum += point;
                        
                        if (completeType.equals("tsumo")) {
                            getPoint = (point + 8) * 3;
                        }
                        else if (completeType.equals("ron")) {
                            getPoint = point + 8 * 3;
                        }
                        _getPointSum += getPoint;
                        break;
                    case "yaku":
                        for (final String string : valueString.split("[\\[, \\]]")) {
                            if ("".equals(string)) {
                                continue;
                            }
                            Integer yakuCount = _yakuCountTable.get(string);
                            
                            if (yakuCount != null) {
                                _yakuCountTable.put(string, ++yakuCount);
                            }
                            else {
                                _yakuCountTable.put(string, 1);
                            }
                        }
                        _playCountWithYaku++;
                        break;
                    default:
                    }
                }
            }
            count++;
        }
        _playCount = countEnd - countStart;
    }
    
    
    
    /**
     * ゲーム統計を取得
     */
    public List<String> get() {
        final List<String> stringList = new ArrayList<>();

        stringList.add(until6thTurnRate());
        stringList.add(until12thTurnRate());
        stringList.add(completableRate());
        stringList.add(completeRate());
        stringList.add(completableTurnAverage());
        stringList.add(turnAverage());
        stringList.add(pointAverage());
        stringList.add(getPointAverage());
        stringList.add(tsumoRate());
        return stringList;
    }
    
    /**
     * 役のゲーム統計を取得
     */
    public List<String> getYaku() {
        return yaku();
    }
    
    
    
    /**
     * 聴牌率
     */
    private String completableRate() {
        final double completableRate = (double) _completableCount * 100 / (double) _playCount;
        final String completableRateString = String.format("%.2f", completableRate);
        
        return "聴牌率: " + completableRateString + " % (" + _completableCount + "/" + _playCount + ")";
    }
    
    /**
     * 平均聴牌巡目
     */
    private String completableTurnAverage() {
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
    private String completeRate() {
        final double completeRate = (double) _completeCount * 100 / (double) _playCount;
        final String completeRateString = String.format("%.2f", completeRate);
        
        return "和了率: " + completeRateString + " % (" + _completeCount + "/" + _playCount + ")";
    }
    
    /**
     * 平均獲得点数
     */
    private String getPointAverage() {
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
    private String pointAverage() {
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
    private String tsumoRate() {
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
    private String turnAverage() {
        String turnAverageString = "-";
        
        if (_completeCount != 0) {
            final double turnAverage = (double) _turnSum / (double) _completeCount;
            turnAverageString = String.format("%.2f", turnAverage);
        }
        return "平均和了巡目: " + turnAverageString + " 巡目";
    }
    
    /**
     * 6巡目までの聴牌率
     */
    private String until6thTurnRate() {
        final double until6thTurnRate = (double) _until6thTurnCount * 100 / (double) _playCount;
        final String until6thTurnRateString = String.format("%.2f", until6thTurnRate);
        
        return "6巡目までの聴牌率: " + until6thTurnRateString + " % (" + _until6thTurnCount + "/" + _playCount + ")";
    }
    
    /**
     * 12巡目までの聴牌率
     */
    private String until12thTurnRate() {
        final double until12thTurnRate = (double) _until12thTurnCount * 100 / (double) _playCount;
        final String until12thTurnRateString = String.format("%.2f", until12thTurnRate);
        
        return "12巡目までの聴牌率: " + until12thTurnRateString + " % (" + _until12thTurnCount + "/" + _playCount + ")";
    }
    
    /**
     * 役
     */
    private List<String> yaku() {
        final TreeMap<Integer, List<String>> countMap = new TreeMap<>();
        
        for (final Entry<String, Integer> entry : _yakuCountTable.entrySet()) {
            final int yakuCount = entry.getValue();
            List<String> yakuList = countMap.get(yakuCount);
            
            if (yakuList == null) {
                yakuList = new ArrayList<>();
            }
            yakuList.add(entry.getKey());
            countMap.put(yakuCount, yakuList);
        }
        final List<String> yakuStringList = new ArrayList<>();
        
        for (final Integer yakuCount : countMap.descendingKeySet()) {
            final double yakuRate = (double) yakuCount * 100 / (double) _playCountWithYaku;
            final String yakuRateString = String.format("%.2f", yakuRate);
            
            for (final String yakuString : countMap.get(yakuCount)) {
                yakuStringList.add(yakuString + ": " + yakuRateString + " % (" + yakuCount + "/" + _playCountWithYaku + ")");
            }
        }
        return yakuStringList;
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
     * 役があるゲーム回数
     */
    private int _playCountWithYaku = 0;
    
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
    
    /**
     * 6巡目までに聴牌した回数
     */
    private int _until6thTurnCount = 0;
    
    /**
     * 12巡目までに聴牌した回数
     */
    private int _until12thTurnCount = 0;
    
    /**
     * 役カウントテーブル
     */
    private Map<String, Integer> _yakuCountTable = new TreeMap<>();
    
}

