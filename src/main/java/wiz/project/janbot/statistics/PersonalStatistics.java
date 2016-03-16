/**
 * PersonalStatistics.java
 * 
 * @author Masasutzu
 */

package wiz.project.janbot.statistics;

import java.util.Map;
import java.util.TreeMap;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import wiz.project.jan.yaku.ChmYaku;
import wiz.project.janbot.game.exception.InvalidInputException;



/**
 * 個人のゲーム統計
 */
public final class PersonalStatistics {
    
    /**
     * コンストラクタ
     */
    public PersonalStatistics(final String playerName, final int start, final int end) throws DocumentException, InvalidInputException {
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
                        
                        if (completableTurn <= 9) {
                            _until9thTurnCount++;
                        }
                        
                        if (completableTurn <= 12) {
                            _until12thTurnCount++;
                        }
                        
                        if (completableTurn <= 15) {
                            _until15thTurnCount++;
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
        _playCount += countEnd - countStart;
    }
    
    
    
    /**
     * 聴牌率
     */
    public double completableRate() {
        return (double) _completableCount * 100 / (double) _playCount;
    }
    
    /**
     * 平均聴牌巡目
     */
    public double completableTurnAverage() {
        double completableTurnAverage = 0;
        
        if (_completableCount != 0) {
            completableTurnAverage = (double) _completableTurnSum / (double) _completableCount;
        }
        return completableTurnAverage;
    }
    
    /**
     * 和了率
     */
    public double completeRate() {
        return (double) _completeCount * 100 / (double) _playCount;
    }
    
    /**
     * 中国麻雀の役の点数を取得
     * 
     * @param name 中国麻雀の役の名前。
     */
    public int getChmYakuPoint(final String name) {
        for (final ChmYaku yaku : ChmYaku.values()) {
            final String yakuName = yaku.toString();
            
            if (name.equals(yakuName)) {
                return yaku.getPoint();
            }
        }
        return 0;
    }
    
    /**
     * 聴牌回数を取得
     * 
     * @return 聴牌回数。
     */
    public int getCompletableCount() {
        return _completableCount;
    }
    
    /**
     * 聴牌巡目の合計を取得
     * 
     * @return 聴牌巡目の合計。
     */
    public int getCompletableTurnSum() {
        return _completableTurnSum;
    }
    
    /**
     * 和了回数を取得
     * 
     * @return 和了回数。
     */
    public int getCompleteCount() {
        return _completeCount;
    }
    
    /**
     * 獲得点数の合計を取得
     * 
     * @return 獲得点数の合計。
     */
    public int getGetPointSum() {
        return _getPointSum;
    }
    
    /**
     * ゲーム回数を取得
     * 
     * @return ゲーム回数。
     */
    public int getPlayCount() {
        return _playCount;
    }
    
    /**
     * 役があるゲーム回数を取得
     * 
     * @return 役があるゲーム回数。
     */
    public int getPlayCountWithYaku() {
        return _playCountWithYaku;
    }
    
    /**
     * 平均獲得点数
     */
    public double getPointAverage() {
        double getPointAverage = 0;
        
        if (_completeCount != 0) {
            getPointAverage = (double) _getPointSum / (double) _completeCount;
        }
        return getPointAverage;
    }
    
    /**
     * 点数の合計を取得
     * 
     * @return 点数の合計。
     */
    public int getPointSum() {
        return _pointSum;
    }
    
    /**
     * ツモ回数を取得
     * 
     * @return ツモ回数。
     */
    public int getTsumoCount() {
        return _tsumoCount;
    }
    
    /**
     * 和了巡目の合計を取得
     * 
     * @return 和了巡目の合計。
     */
    public int getTurnSum() {
        return _turnSum;
    }
    
    /**
     * 6巡目までに聴牌した回数を取得
     * 
     * @return 6巡目までに聴牌した回数。
     */
    public int getUntil6thTurnCount() {
        return _until6thTurnCount;
    }
    
    /**
     * 9巡目までに聴牌した回数を取得
     * 
     * @return 9巡目までに聴牌した回数。
     */
    public int getUntil9thTurnCount() {
        return _until9thTurnCount;
    }
    
    /**
     * 12巡目までに聴牌した回数を取得
     * 
     * @return 12巡目までに聴牌した回数。
     */
    public int getUntil12thTurnCount() {
        return _until12thTurnCount;
    }
    
    /**
     * 15巡目までに聴牌した回数を取得
     * 
     * @return 15巡目までに聴牌した回数。
     */
    public int getUntil15thTurnCount() {
        return _until15thTurnCount;
    }
    
    /**
     * 役カウントテーブルを取得
     * 
     * @return 役カウントテーブル。
     */
    public Map<String, Integer> getYakuCountTable() {
        return _yakuCountTable;
    }
    
    /**
     * 平均点数
     */
    public double pointAverage() {
    	double pointAverage = 0;
        
        if (_completeCount != 0) {
            pointAverage = (double) _pointSum / (double) _completeCount;
        }
        return pointAverage;
    }
    
    /**
     * ツモ率
     */
    public double tsumoRate() {
    	double tsumoRate = 0;
        
        if (_completeCount != 0) {
            tsumoRate = (double) _tsumoCount  * 100 / (double) _completeCount;
        }
        return tsumoRate;
    }
    
    /**
     * 平均和了巡目
     */
    public double turnAverage() {
        double turnAverage = 0.0;
        
        if (_completeCount != 0) {
            turnAverage = (double) _turnSum / (double) _completeCount;
        }
        return turnAverage;
    }
    
    /**
     * 6巡目までの聴牌率
     */
    public double until6thTurnRate() {
        return (double) _until6thTurnCount * 100 / (double) _playCount;
    }
    
    /**
     * 9巡目までの聴牌率
     */
    public double until9thTurnRate() {
        return (double) _until9thTurnCount * 100 / (double) _playCount;
    }
    
    /**
     * 12巡目までの聴牌率
     */
    public double until12thTurnRate() {
        return (double) _until12thTurnCount * 100 / (double) _playCount;
    }
    
    /**
     * 15巡目までの聴牌率
     */
    public double until15thTurnRate() {
        return (double) _until15thTurnCount * 100 / (double) _playCount;
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
     * 9巡目までに聴牌した回数
     */
    private int _until9thTurnCount = 0;
    
    /**
     * 12巡目までに聴牌した回数
     */
    private int _until12thTurnCount = 0;
    
    /**
     * 15巡目までに聴牌した回数
     */
    private int _until15thTurnCount = 0;
    
    /**
     * 役カウントテーブル
     */
    private Map<String, Integer> _yakuCountTable = new TreeMap<>();
    
}
