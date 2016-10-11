/**
 * Statistics.java
 *
 * @author Masasutzu
 */

package wiz.project.janbot.statistics;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.dom4j.DocumentException;

import wiz.project.jan.yaku.ChmYaku;
import wiz.project.janbot.game.exception.InvalidInputException;



/**
 * ゲーム統計
 */
public final class Statistics {

    /**
     * コンストラクタ
     */
    public Statistics(final String playerName, int start, int end) throws DocumentException, InvalidInputException {
        final List<String> playerNameList = getPlayerNameList(playerName);
        final boolean isAll = playerNameList.size() > 1 ? true : false;
        start = isAll ? 0 : start;
        end = isAll ? 0 : end;

        for (final String player : playerNameList) {
            _statisticsTable.put(player, new PersonalStatistics(player, start, end));
        }
    }



    /**
     * ゲーム統計を取得
     */
    public List<String> get() {
        final List<String> stringList = new ArrayList<>();

        stringList.add(until6thTurnRate());
        stringList.add(until9thTurnRate());
        stringList.add(until12thTurnRate());
        stringList.add(until15thTurnRate());
        stringList.add(completableRate());
        stringList.add(completableTurnAverage());
        stringList.add(waitCountAverage());
        stringList.add(waitPaiCountAverage());
        stringList.add(completeRate());
        stringList.add(turnAverage());
        stringList.add(pointAverage());
        stringList.add(getPointAverage());
        stringList.add(tsumoRate());
        stringList.add(calledRate());
        stringList.add(oneCalledRate());
        stringList.add(twoCalledRate());
        stringList.add(threeCalledRate());
        stringList.add(fourCalledRate());
        return stringList;
    }

    /**
     * ランキングを取得
     *
     * @return ランキング。
     */
    public List<String> getRanking() {
        final List<String> stringList = new ArrayList<>();

        stringList.add(until6thTurnRateRanking());
        stringList.add(until9thTurnRateRanking());
        stringList.add(until12thTurnRateRanking());
        stringList.add(until15thTurnRateRanking());
        stringList.add(completableRateRanking());
        stringList.add(completableTurnAverageRanking());
        stringList.add(waitCountAverageRanking());
        stringList.add(waitPaiCountAverageRanking());
        stringList.add(completeRateRanking());
        stringList.add(turnAverageRanking());
        stringList.add(pointAverageRanking());
        stringList.add(getPointAverageRanking());
        stringList.add(tsumoRateRanking());
        stringList.add(calledRateRanking());
        stringList.add(oneCalledRateRanking());
        stringList.add(twoCalledRateRanking());
        stringList.add(threeCalledRateRanking());
        stringList.add(fourCalledRateRanking());
        return stringList;
    }

    /**
     * 役のゲーム統計を取得
     *
     * @param maxCount 最大数。
     * @param minimumPoint 最小点。
     * @return 役のゲーム統計。
     */
    public List<String> getYaku(final int maxCount, final int minimumPoint) {
        return yaku(maxCount, minimumPoint);
    }



    /**
     * 副露率
     */
    private String calledRate() {
        final int calledCount = getOneCalledCount() + getTwoCalledCount() + getThreeCalledCount() + getFourCalledCount();
        final int playCountWithWaitCount = getPlayCountWithWaitCount();
        final double calledRate = (double) calledCount * 100 / (double) playCountWithWaitCount;
        final String calledRateString = String.format("%.2f", calledRate);

        return "副露率: " + calledRateString + " % (" + calledCount + "/" + playCountWithWaitCount + ")";
    }

    /**
     * 副露率のランキング
     */
    private String calledRateRanking() {
        final TreeMap<Double, List<String>> rankingTable = new TreeMap<>();

        for (final Entry<String, PersonalStatistics> entry : _statisticsTable.entrySet()) {
            final double calledRate = entry.getValue().calledRate();
            List<String> playerList = rankingTable.get(calledRate);

            if (playerList == null) {
            	playerList = new ArrayList<>();
            }
            playerList.add(entry.getKey().replaceFirst("[aeiou]", "$0 "));
            rankingTable.put(calledRate, playerList);
        }
        String message ="副露率: ";
        int count = 0;

        for (final double calledRate : rankingTable.descendingKeySet()) {
            final List<String> playerList = rankingTable.get(calledRate);
            final String calledRateString = String.format("%.2f", calledRate);
            message += playerList + " (" + calledRateString + "%)";
            count++;

            if (count != rankingTable.size()) {
                message += " > ";
            }
        }
        return message.replaceAll("[\\[\\]]", "");
    }

    /**
     * 聴牌率
     */
    private String completableRate() {
        final int completableCount = getCompletableCount();
        final int playCount = getPlayCount();
        final double completableRate = (double) completableCount * 100 / (double) playCount;
        final String completableRateString = String.format("%.2f", completableRate);

        return "聴牌率: " + completableRateString + " % (" + completableCount + "/" + playCount + ")";
    }

    /**
     * 聴牌率のランキング
     */
    private String completableRateRanking() {
        final TreeMap<Double, List<String>> rankingTable = new TreeMap<>();

        for (final Entry<String, PersonalStatistics> entry : _statisticsTable.entrySet()) {
            final double completableRate = entry.getValue().completableRate();
            List<String> playerList = rankingTable.get(completableRate);

            if (playerList == null) {
            	playerList = new ArrayList<>();
            }
            playerList.add(entry.getKey().replaceFirst("[aeiou]", "$0 "));
            rankingTable.put(completableRate, playerList);
        }
        String message ="聴牌率: ";
        int count = 0;

        for (final double completableRate : rankingTable.descendingKeySet()) {
            final List<String> playerList = rankingTable.get(completableRate);
            final String completableRateString = String.format("%.2f", completableRate);
            message += playerList + " (" + completableRateString + "%)";
            count++;

            if (count != rankingTable.size()) {
                message += " > ";
            }
        }
        return message.replaceAll("[\\[\\]]", "");
    }

    /**
     * 平均聴牌巡目
     */
    private String completableTurnAverage() {
        final int completableCount = getCompletableCount();
        String completableTurnAverageString = "-";

        if (completableCount != 0) {
            final int completableTurnSum = getCompletableTurnSum();
            final double completableTurnAverage = (double) completableTurnSum / (double) completableCount;
            completableTurnAverageString = String.format("%.2f", completableTurnAverage);
        }
        return "平均聴牌巡目: " + completableTurnAverageString + " 巡目";
    }

    /**
     * 平均聴牌巡目のランキング
     */
    private String completableTurnAverageRanking() {
        final Map<Double, List<String>> rankingTable = new TreeMap<>();

        for (final Entry<String, PersonalStatistics> entry : _statisticsTable.entrySet()) {
            final double completableTurnAverage = entry.getValue().completableTurnAverage();
            List<String> playerList = rankingTable.get(completableTurnAverage);

            if (playerList == null) {
            	playerList = new ArrayList<>();
            }
            playerList.add(entry.getKey().replaceFirst("[aeiou]", "$0 "));
            rankingTable.put(completableTurnAverage, playerList);
        }
        String message ="平均聴牌巡目: ";
        int count = 0;

        for (final double completableTurnAverage : rankingTable.keySet()) {
            final List<String> playerList = rankingTable.get(completableTurnAverage);
            final String completableTurnAverageString = String.format("%.2f", completableTurnAverage);
            message += playerList + " (" + completableTurnAverageString + "巡目)";
            count++;

            if (count != rankingTable.size()) {
                message += " > ";
            }
        }
        return message.replaceAll("[\\[\\]]", "");
    }

    /**
     * 和了率
     */
    private String completeRate() {
        final int completeCount = getCompleteCount();
        final int playCount = getPlayCount();
        final double completeRate = (double) completeCount * 100 / (double) playCount;
        final String completeRateString = String.format("%.2f", completeRate);

        return "和了率: " + completeRateString + " % (" + completeCount + "/" + playCount + ")";
    }

    /**
     * 和了率のランキング
     */
    private String completeRateRanking() {
        final TreeMap<Double, List<String>> rankingTable = new TreeMap<>();

        for (final Entry<String, PersonalStatistics> entry : _statisticsTable.entrySet()) {
            final double completableRate = entry.getValue().completableRate();
            List<String> playerList = rankingTable.get(completableRate);

            if (playerList == null) {
            	playerList = new ArrayList<>();
            }
            playerList.add(entry.getKey().replaceFirst("[aeiou]", "$0 "));
            rankingTable.put(completableRate, playerList);
        }
        String message ="和了率: ";
        int count = 0;

        for (final double completableRate : rankingTable.descendingKeySet()) {
            final List<String> playerList = rankingTable.get(completableRate);
            final String completableRateString = String.format("%.2f", completableRate);
            message += playerList + " (" + completableRateString + "%)";
            count++;

            if (count != rankingTable.size()) {
                message += " > ";
            }
        }
        return message.replaceAll("[\\[\\]]", "");
    }

    /**
     * 4副露率
     */
    private String fourCalledRate() {
        final int fourCalledCount = getFourCalledCount();
        final int playCountWithWaitCount = getPlayCountWithWaitCount();
        final double fourCalledRate = (double) fourCalledCount * 100 / (double) playCountWithWaitCount;
        final String fourCalledRateString = String.format("%.2f", fourCalledRate);

        return "4副露率: " + fourCalledRateString + " % (" + fourCalledCount + "/" + playCountWithWaitCount + ")";
    }

    /**
     * 4副露率のランキング
     */
    private String fourCalledRateRanking() {
        final TreeMap<Double, List<String>> rankingTable = new TreeMap<>();

        for (final Entry<String, PersonalStatistics> entry : _statisticsTable.entrySet()) {
            final double fourCalledRate = entry.getValue().fourCalledRate();
            List<String> playerList = rankingTable.get(fourCalledRate);

            if (playerList == null) {
            	playerList = new ArrayList<>();
            }
            playerList.add(entry.getKey().replaceFirst("[aeiou]", "$0 "));
            rankingTable.put(fourCalledRate, playerList);
        }
        String message ="4副露率: ";
        int count = 0;

        for (final double fourCalledRate : rankingTable.descendingKeySet()) {
            final List<String> playerList = rankingTable.get(fourCalledRate);
            final String fourCalledRateString = String.format("%.2f", fourCalledRate);
            message += playerList + " (" + fourCalledRateString + "%)";
            count++;

            if (count != rankingTable.size()) {
                message += " > ";
            }
        }
        return message.replaceAll("[\\[\\]]", "");
    }

    /**
     * 中国麻雀の役の点数を取得
     *
     * @param name 中国麻雀の役の名前。
     * @return 中国麻雀の役の点数。
     */
    private int getChmYakuPoint(final String name) {
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
    private int getCompletableCount() {
        int completableCount = 0;

        for (final PersonalStatistics statistics : _statisticsTable.values()) {
            completableCount += statistics.getCompletableCount();
        }
        return completableCount;
    }

    /**
     * 聴牌巡目の合計を取得
     *
     * @return 聴牌巡目の合計。
     */
    private int getCompletableTurnSum() {
        int completableTurnSum = 0;

        for (final PersonalStatistics statistics : _statisticsTable.values()) {
            completableTurnSum += statistics.getCompletableTurnSum();
        }
        return completableTurnSum;
    }

    /**
     * 和了回数を取得
     *
     * @return 和了回数。
     */
    private int getCompleteCount() {
        int completeCount = 0;

        for (final PersonalStatistics statistics : _statisticsTable.values()) {
            completeCount += statistics.getCompleteCount();
        }
        return completeCount;
    }

    /**
     * 4回鳴いたゲーム回数を取得
     *
     * @return 4回鳴いたゲーム回数。
     */
    private int getFourCalledCount() {
        int fourCalledCount = 0;

        for (final PersonalStatistics statistics : _statisticsTable.values()) {
            fourCalledCount += statistics.getFourCalledCount();
        }
        return fourCalledCount;
    }

    /**
     * 獲得点数の合計を取得
     *
     * @return 獲得点数の合計。
     */
    private int getGetPointSum() {
        int getPointSum = 0;

        for (final PersonalStatistics statistics : _statisticsTable.values()) {
            getPointSum += statistics.getGetPointSum();
        }
        return getPointSum;
    }

    /**
     * 1回鳴いたゲーム回数を取得
     *
     * @return 1回鳴いたゲーム回数。
     */
    private int getOneCalledCount() {
        int oneCalledCount = 0;

        for (final PersonalStatistics statistics : _statisticsTable.values()) {
            oneCalledCount += statistics.getOneCalledCount();
        }
        return oneCalledCount;
    }

    /**
     * ゲーム回数を取得
     *
     * @return ゲーム回数。
     */
    private int getPlayCount() {
        int playCount = 0;

        for (final PersonalStatistics statistics : _statisticsTable.values()) {
            playCount += statistics.getPlayCount();
        }
        return playCount;
    }

    /**
     * 待ち数があるゲーム回数を取得
     *
     * @return 待ち数があるゲーム回数。
     */
    private int getPlayCountWithWaitCount() {
        int playCountWithWaitCount = 0;

        for (final PersonalStatistics statistics : _statisticsTable.values()) {
            playCountWithWaitCount += statistics.getPlayCountWithWaitCount();
        }
        return playCountWithWaitCount;
    }

    /**
     * 役があるゲーム回数を取得
     *
     * @return 役があるゲーム回数。
     */
    private int getPlayCountWithYaku() {
        int playCountWithYaku = 0;

        for (final PersonalStatistics statistics : _statisticsTable.values()) {
            playCountWithYaku += statistics.getPlayCountWithYaku();
        }
        return playCountWithYaku;
    }

    /**
     * プレイヤーの名前リストを取得。
     *
     * @return プレイヤーの名前リスト。
     */
    private List<String> getPlayerNameList(final String playerName) {
        final List<String> playerNameList = new ArrayList<>();

        if ("all".equals(playerName)){
            final File dir = new File(".");
            final File[] files = dir.listFiles();
            final String reg = ".*\\.xml$";

            for (int i = 0; i < files.length; i++) {
                final String fileName = files[i].getName();

                if (fileName.matches(reg)) {
                    playerNameList.add(fileName.replaceFirst("\\.xml$", ""));
                }
            }
        }
        else {
            playerNameList.add(playerName);
        }
        return playerNameList;
    }

    /**
     * 平均獲得点数
     */
    private String getPointAverage() {
        final int completeCount = getCompleteCount();
        String getPointAverageString = "-";

        if (completeCount != 0) {
            final int getPointSum = getGetPointSum();
            final double getPointAverage = (double) getPointSum / (double) completeCount;
            getPointAverageString = String.format("%.2f", getPointAverage);
        }
        return "平均獲得点数: " + getPointAverageString + " 点";
    }

    /**
     * 平均獲得点数のランキング
     */
    private String getPointAverageRanking() {
        final TreeMap<Double, List<String>> rankingTable = new TreeMap<>();

        for (final Entry<String, PersonalStatistics> entry : _statisticsTable.entrySet()) {
            final double getPointAverage = entry.getValue().getPointAverage();
            List<String> playerList = rankingTable.get(getPointAverage);

            if (playerList == null) {
            	playerList = new ArrayList<>();
            }
            playerList.add(entry.getKey().replaceFirst("[aeiou]", "$0 "));
            rankingTable.put(getPointAverage, playerList);
        }
        String message ="平均獲得点数: ";
        int count = 0;

        for (final double getPointAverage : rankingTable.descendingKeySet()) {
            final List<String> playerList = rankingTable.get(getPointAverage);
            final String getPointAverageString = String.format("%.2f", getPointAverage);
            message += playerList + " (" + getPointAverageString + "点)";
            count++;

            if (count != rankingTable.size()) {
                message += " > ";
            }
        }
        return message.replaceAll("[\\[\\]]", "");
    }

    /**
     * 点数の合計を取得
     *
     * @return 点数の合計。
     */
    private int getPointSum() {
        int pointSum = 0;

        for (final PersonalStatistics statistics : _statisticsTable.values()) {
            pointSum += statistics.getPointSum();
        }
        return pointSum;
    }

    /**
     * 3回鳴いたゲーム回数を取得
     *
     * @return 3回鳴いたゲーム回数。
     */
    private int getThreeCalledCount() {
        int threeCalledCount = 0;

        for (final PersonalStatistics statistics : _statisticsTable.values()) {
            threeCalledCount += statistics.getThreeCalledCount();
        }
        return threeCalledCount;
    }

    /**
     * ツモ回数を取得
     *
     * @return ツモ回数。
     */
    private int getTsumoCount() {
        int tsumoCount = 0;

        for (final PersonalStatistics statistics : _statisticsTable.values()) {
            tsumoCount += statistics.getTsumoCount();
        }
        return tsumoCount;
    }

    /**
     * 和了巡目の合計を取得
     *
     * @return 和了巡目の合計。
     */
    private int getTurnSum() {
        int turnSum = 0;

        for (final PersonalStatistics statistics : _statisticsTable.values()) {
            turnSum += statistics.getTurnSum();
        }
        return turnSum;
    }

    /**
     * 2回鳴いたゲーム回数を取得
     *
     * @return 2回鳴いたゲーム回数。
     */
    private int getTwoCalledCount() {
        int twoCalledCount = 0;

        for (final PersonalStatistics statistics : _statisticsTable.values()) {
            twoCalledCount += statistics.getTwoCalledCount();
        }
        return twoCalledCount;
    }

    /**
     * 6巡目までに聴牌した回数を取得
     *
     * @return 6巡目までに聴牌した回数。
     */
    private int getUntil6thTurnCount() {
        int until6thTurnCount = 0;

        for (final PersonalStatistics statistics : _statisticsTable.values()) {
            until6thTurnCount += statistics.getUntil6thTurnCount();
        }
        return until6thTurnCount;
    }

    /**
     * 9巡目までに聴牌した回数を取得
     *
     * @return 9巡目までに聴牌した回数。
     */
    private int getUntil9thTurnCount() {
        int until9thTurnCount = 0;

        for (final PersonalStatistics statistics : _statisticsTable.values()) {
            until9thTurnCount += statistics.getUntil9thTurnCount();
        }
        return until9thTurnCount;
    }

    /**
     * 12巡目までに聴牌した回数を取得
     *
     * @return 12巡目までに聴牌した回数。
     */
    private int getUntil12thTurnCount() {
        int until12thTurnCount = 0;

        for (final PersonalStatistics statistics : _statisticsTable.values()) {
            until12thTurnCount += statistics.getUntil12thTurnCount();
        }
        return until12thTurnCount;
    }

    /**
     * 15巡目までに聴牌した回数を取得
     *
     * @return 15巡目までに聴牌した回数。
     */
    private int getUntil15thTurnCount() {
        int until15thTurnCount = 0;

        for (final PersonalStatistics statistics : _statisticsTable.values()) {
            until15thTurnCount += statistics.getUntil15thTurnCount();
        }
        return until15thTurnCount;
    }

    /**
     * 待ち数を取得
     *
     * @return 待ち数。
     */
    private int getWaitCount() {
        int waitCount = 0;

        for (final PersonalStatistics statistics : _statisticsTable.values()) {
        	waitCount += statistics.getWaitCount();
        }
        return waitCount;
    }

    /**
     * 待ち枚数を取得
     *
     * @return 待ち枚数。
     */
    private int getWaitPaiCount() {
        int waitPaiCount = 0;

        for (final PersonalStatistics statistics : _statisticsTable.values()) {
        	waitPaiCount += statistics.getWaitPaiCount();
        }
        return waitPaiCount;
    }

    /**
     * 役カウントテーブルを取得
     *
     * @return 役カウントテーブル。
     */
    private Map<String, Integer> getYakuCountTable() {
        final Map<String, Integer> yakuCountTable = new TreeMap<>();

        for (final PersonalStatistics statistics : _statisticsTable.values()) {
        	yakuCountTable.putAll(statistics.getYakuCountTable());
        }
        return yakuCountTable;
    }

    /**
     * 1副露率
     */
    private String oneCalledRate() {
        final int oneCalledCount = getOneCalledCount();
        final int playCountWithWaitCount = getPlayCountWithWaitCount();
        final double oneCalledRate = (double) oneCalledCount * 100 / (double) playCountWithWaitCount;
        final String oneCalledRateString = String.format("%.2f", oneCalledRate);

        return "1副露率: " + oneCalledRateString + " % (" + oneCalledCount + "/" + playCountWithWaitCount + ")";
    }

    /**
     * 1副露率のランキング
     */
    private String oneCalledRateRanking() {
        final TreeMap<Double, List<String>> rankingTable = new TreeMap<>();

        for (final Entry<String, PersonalStatistics> entry : _statisticsTable.entrySet()) {
            final double oneCalledRate = entry.getValue().fourCalledRate();
            List<String> playerList = rankingTable.get(oneCalledRate);

            if (playerList == null) {
            	playerList = new ArrayList<>();
            }
            playerList.add(entry.getKey().replaceFirst("[aeiou]", "$0 "));
            rankingTable.put(oneCalledRate, playerList);
        }
        String message ="1副露率: ";
        int count = 0;

        for (final double oneCalledRate : rankingTable.descendingKeySet()) {
            final List<String> playerList = rankingTable.get(oneCalledRate);
            final String oneCalledRateString = String.format("%.2f", oneCalledRate);
            message += playerList + " (" + oneCalledRateString + "%)";
            count++;

            if (count != rankingTable.size()) {
                message += " > ";
            }
        }
        return message.replaceAll("[\\[\\]]", "");
    }

    /**
     * 平均点数
     */
    private String pointAverage() {
        final int completeCount = getCompleteCount();
        String pointAverageString = "-";

        if (completeCount != 0) {
            final int pointSum = getPointSum();
            final double pointAverage = (double) pointSum / (double) completeCount;
            pointAverageString = String.format("%.2f", pointAverage);
        }
        return "平均点数: " + pointAverageString + " 点";
    }

    /**
     * 平均点数のランキング
     */
    private String pointAverageRanking() {
        final TreeMap<Double, List<String>> rankingTable = new TreeMap<>();

        for (final Entry<String, PersonalStatistics> entry : _statisticsTable.entrySet()) {
            final double pointAverage = entry.getValue().pointAverage();
            List<String> playerList = rankingTable.get(pointAverage);

            if (playerList == null) {
            	playerList = new ArrayList<>();
            }
            playerList.add(entry.getKey().replaceFirst("[aeiou]", "$0 "));
            rankingTable.put(pointAverage, playerList);
        }
        String message ="平均点数: ";
        int count = 0;

        for (final double pointAverage : rankingTable.descendingKeySet()) {
            final List<String> playerList = rankingTable.get(pointAverage);
            final String pointAverageString = String.format("%.2f", pointAverage);
            message += playerList + " (" + pointAverageString + "点)";
            count++;

            if (count != rankingTable.size()) {
                message += " > ";
            }
        }
        return message.replaceAll("[\\[\\]]", "");
    }

    /**
     * 3副露率
     */
    private String threeCalledRate() {
        final int threeCalledCount = getThreeCalledCount();
        final int playCountWithWaitCount = getPlayCountWithWaitCount();
        final double threeCalledRate = (double) threeCalledCount * 100 / (double) playCountWithWaitCount;
        final String threeCalledRateString = String.format("%.2f", threeCalledRate);

        return "3副露率: " + threeCalledRateString + " % (" + threeCalledCount + "/" + playCountWithWaitCount + ")";
    }

    /**
     * 3副露率のランキング
     */
    private String threeCalledRateRanking() {
        final TreeMap<Double, List<String>> rankingTable = new TreeMap<>();

        for (final Entry<String, PersonalStatistics> entry : _statisticsTable.entrySet()) {
            final double threeCalledRate = entry.getValue().threeCalledRate();
            List<String> playerList = rankingTable.get(threeCalledRate);

            if (playerList == null) {
            	playerList = new ArrayList<>();
            }
            playerList.add(entry.getKey().replaceFirst("[aeiou]", "$0 "));
            rankingTable.put(threeCalledRate, playerList);
        }
        String message ="3副露率: ";
        int count = 0;

        for (final double threeCalledRate : rankingTable.descendingKeySet()) {
            final List<String> playerList = rankingTable.get(threeCalledRate);
            final String threeCalledRateString = String.format("%.2f", threeCalledRate);
            message += playerList + " (" + threeCalledRateString + "%)";
            count++;

            if (count != rankingTable.size()) {
                message += " > ";
            }
        }
        return message.replaceAll("[\\[\\]]", "");
    }

    /**
     * ツモ率
     */
    private String tsumoRate() {
        final int tsumoCount = getTsumoCount();
        final int completeCount = getCompleteCount();
        String tsumoRateString = "-";

        if (completeCount != 0) {
            final double tsumoRate = (double) tsumoCount  * 100 / (double) completeCount;
            tsumoRateString = String.format("%.2f", tsumoRate);
        }
        return "ツモ率: " + tsumoRateString + " % (" + tsumoCount + "/" + completeCount + ")";
    }

    /**
     * ツモ率のランキング
     */
    private String tsumoRateRanking() {
        final TreeMap<Double, List<String>> rankingTable = new TreeMap<>();

        for (final Entry<String, PersonalStatistics> entry : _statisticsTable.entrySet()) {
            final double tsumoRate = entry.getValue().tsumoRate();
            List<String> playerList = rankingTable.get(tsumoRate);

            if (playerList == null) {
            	playerList = new ArrayList<>();
            }
            playerList.add(entry.getKey().replaceFirst("[aeiou]", "$0 "));
            rankingTable.put(tsumoRate, playerList);
        }
        String message ="ツモ率: ";
        int count = 0;

        for (final double tsumoRate : rankingTable.descendingKeySet()) {
            final List<String> playerList = rankingTable.get(tsumoRate);
            final String tsumoRateString = String.format("%.2f", tsumoRate);
            message += playerList + " (" + tsumoRateString + "%)";
            count++;

            if (count != rankingTable.size()) {
                message += " > ";
            }
        }
        return message.replaceAll("[\\[\\]]", "");
    }

    /**
     * 平均和了巡目
     */
    private String turnAverage() {
        final int completeCount = getCompleteCount();
        String turnAverageString = "-";

        if (completeCount != 0) {
            final int turnSum = getTurnSum();
            final double turnAverage = (double) turnSum / (double) completeCount;
            turnAverageString = String.format("%.2f", turnAverage);
        }
        return "平均和了巡目: " + turnAverageString + " 巡目";
    }

    /**
     * 平均和了巡目のランキング
     */
    private String turnAverageRanking() {
        final Map<Double, List<String>> rankingTable = new TreeMap<>();

        for (final Entry<String, PersonalStatistics> entry : _statisticsTable.entrySet()) {
            final double turnAverage = entry.getValue().turnAverage();
            List<String> playerList = rankingTable.get(turnAverage);

            if (playerList == null) {
            	playerList = new ArrayList<>();
            }
            playerList.add(entry.getKey().replaceFirst("[aeiou]", "$0 "));
            rankingTable.put(turnAverage, playerList);
        }
        String message ="平均和了巡目: ";
        int count = 0;

        for (final double turnAverage : rankingTable.keySet()) {
            final List<String> playerList = rankingTable.get(turnAverage);
            final String turnAverageString = String.format("%.2f", turnAverage);
            message += playerList + " (" + turnAverageString + "巡目)";
            count++;

            if (count != rankingTable.size()) {
                message += " > ";
            }
        }
        return message.replaceAll("[\\[\\]]", "");
    }

    /**
     * 2副露率
     */
    private String twoCalledRate() {
        final int twoCalledCount = getTwoCalledCount();
        final int playCountWithWaitCount = getPlayCountWithWaitCount();
        final double twoCalledRate = (double) twoCalledCount * 100 / (double) playCountWithWaitCount;
        final String twoCalledRateString = String.format("%.2f", twoCalledRate);

        return "2副露率: " + twoCalledRateString + " % (" + twoCalledCount + "/" + playCountWithWaitCount + ")";
    }

    /**
     * 2副露率のランキング
     */
    private String twoCalledRateRanking() {
        final TreeMap<Double, List<String>> rankingTable = new TreeMap<>();

        for (final Entry<String, PersonalStatistics> entry : _statisticsTable.entrySet()) {
            final double twoCalledRate = entry.getValue().twoCalledRate();
            List<String> playerList = rankingTable.get(twoCalledRate);

            if (playerList == null) {
            	playerList = new ArrayList<>();
            }
            playerList.add(entry.getKey().replaceFirst("[aeiou]", "$0 "));
            rankingTable.put(twoCalledRate, playerList);
        }
        String message ="2副露率: ";
        int count = 0;

        for (final double twoCalledRate : rankingTable.descendingKeySet()) {
            final List<String> playerList = rankingTable.get(twoCalledRate);
            final String twoCalledRateString = String.format("%.2f", twoCalledRate);
            message += playerList + " (" + twoCalledRateString + "%)";
            count++;

            if (count != rankingTable.size()) {
                message += " > ";
            }
        }
        return message.replaceAll("[\\[\\]]", "");
    }

    /**
     * 6巡目までの聴牌率
     */
    private String until6thTurnRate() {
        final int until6thTurnCount = getUntil6thTurnCount();
        final int playCount = getPlayCount();
        final double until6thTurnRate = (double) until6thTurnCount * 100 / (double) playCount;
        final String until6thTurnRateString = String.format("%.2f", until6thTurnRate);

        return "6巡目までの聴牌率: " + until6thTurnRateString + " % (" + until6thTurnCount + "/" + playCount + ")";
    }

    /**
     * 6巡目までの聴牌率のランキング
     */
    private String until6thTurnRateRanking() {
        final TreeMap<Double, List<String>> rankingTable = new TreeMap<>();

        for (final Entry<String, PersonalStatistics> entry : _statisticsTable.entrySet()) {
            final double until6thTurnRate = entry.getValue().until6thTurnRate();
            List<String> playerList = rankingTable.get(until6thTurnRate);

            if (playerList == null) {
            	playerList = new ArrayList<>();
            }
            playerList.add(entry.getKey().replaceFirst("[aeiou]", "$0 "));
            rankingTable.put(until6thTurnRate, playerList);
        }
        String message ="6巡目までの聴牌率: ";
        int count = 0;

        for (final double until6thTurnRate : rankingTable.descendingKeySet()) {
            final List<String> playerList = rankingTable.get(until6thTurnRate);
            final String until6thTurnRateString = String.format("%.2f", until6thTurnRate);
            message += playerList + " (" + until6thTurnRateString + "%)";
            count++;

            if (count != rankingTable.size()) {
                message += " > ";
            }
        }
        return message.replaceAll("[\\[\\]]", "");
    }

    /**
     * 9巡目までの聴牌率
     */
    private String until9thTurnRate() {
        final int until9thTurnCount = getUntil9thTurnCount();
        final int playCount = getPlayCount();
        final double until9thTurnRate = (double) until9thTurnCount * 100 / (double) playCount;
        final String until9thTurnRateString = String.format("%.2f", until9thTurnRate);

        return "9巡目までの聴牌率: " + until9thTurnRateString + " % (" + until9thTurnCount + "/" + playCount + ")";
    }

    /**
     * 9巡目までの聴牌率のランキング
     */
    private String until9thTurnRateRanking() {
        final TreeMap<Double, List<String>> rankingTable = new TreeMap<>();

        for (final Entry<String, PersonalStatistics> entry : _statisticsTable.entrySet()) {
            final double until9thTurnRate = entry.getValue().until9thTurnRate();
            List<String> playerList = rankingTable.get(until9thTurnRate);

            if (playerList == null) {
            	playerList = new ArrayList<>();
            }
            playerList.add(entry.getKey().replaceFirst("[aeiou]", "$0 "));
            rankingTable.put(until9thTurnRate, playerList);
        }
        String message ="9巡目までの聴牌率: ";
        int count = 0;

        for (final double until9thTurnRate : rankingTable.descendingKeySet()) {
            final List<String> playerList = rankingTable.get(until9thTurnRate);
            final String until9thTurnRateString = String.format("%.2f", until9thTurnRate);
            message += playerList + " (" + until9thTurnRateString + "%)";
            count++;

            if (count != rankingTable.size()) {
                message += " > ";
            }
        }
        return message.replaceAll("[\\[\\]]", "");
    }

    /**
     * 12巡目までの聴牌率
     */
    private String until12thTurnRate() {
        final int until12thTurnCount = getUntil12thTurnCount();
        final int playCount = getPlayCount();
        final double until12thTurnRate = (double) until12thTurnCount * 100 / (double) playCount;
        final String until12thTurnRateString = String.format("%.2f", until12thTurnRate);

        return "12巡目までの聴牌率: " + until12thTurnRateString + " % (" + until12thTurnCount + "/" + playCount + ")";
    }

    /**
     * 12巡目までの聴牌率のランキング
     */
    private String until12thTurnRateRanking() {
        final TreeMap<Double, List<String>> rankingTable = new TreeMap<>();

        for (final Entry<String, PersonalStatistics> entry : _statisticsTable.entrySet()) {
            final double until12thTurnRate = entry.getValue().until12thTurnRate();
            List<String> playerList = rankingTable.get(until12thTurnRate);

            if (playerList == null) {
            	playerList = new ArrayList<>();
            }
            playerList.add(entry.getKey().replaceFirst("[aeiou]", "$0 "));
            rankingTable.put(until12thTurnRate, playerList);
        }
        String message ="12巡目までの聴牌率: ";
        int count = 0;

        for (final double until12thTurnRate : rankingTable.descendingKeySet()) {
            final List<String> playerList = rankingTable.get(until12thTurnRate);
            final String until12thTurnRateString = String.format("%.2f", until12thTurnRate);
            message += playerList + " (" + until12thTurnRateString + "%)";
            count++;

            if (count != rankingTable.size()) {
                message += " > ";
            }
        }
        return message.replaceAll("[\\[\\]]", "");
    }

    /**
     * 15巡目までの聴牌率
     */
    private String until15thTurnRate() {
        final int until15thTurnCount = getUntil15thTurnCount();
        final int playCount = getPlayCount();
        final double until15thTurnRate = (double) until15thTurnCount * 100 / (double) playCount;
        final String until15thTurnRateString = String.format("%.2f", until15thTurnRate);

        return "15巡目までの聴牌率: " + until15thTurnRateString + " % (" + until15thTurnCount + "/" + playCount + ")";
    }

    /**
     * 15巡目までの聴牌率のランキング
     */
    private String until15thTurnRateRanking() {
        final TreeMap<Double, List<String>> rankingTable = new TreeMap<>();

        for (final Entry<String, PersonalStatistics> entry : _statisticsTable.entrySet()) {
            final double until15thTurnRate = entry.getValue().until15thTurnRate();
            List<String> playerList = rankingTable.get(until15thTurnRate);

            if (playerList == null) {
            	playerList = new ArrayList<>();
            }
            playerList.add(entry.getKey().replaceFirst("[aeiou]", "$0 "));
            rankingTable.put(until15thTurnRate, playerList);
        }
        String message ="15巡目までの聴牌率: ";
        int count = 0;

        for (final double until15thTurnRate : rankingTable.descendingKeySet()) {
            final List<String> playerList = rankingTable.get(until15thTurnRate);
            final String until15thTurnRateString = String.format("%.2f", until15thTurnRate);
            message += playerList + " (" + until15thTurnRateString + "%)";
            count++;

            if (count != rankingTable.size()) {
                message += " > ";
            }
        }
        return message.replaceAll("[\\[\\]]", "");
    }

    /**
     * 平均待ち数
     */
    private String waitCountAverage() {
        final int waitCount = getWaitCount();
        final int playCountWithWaitCount = getPlayCountWithWaitCount();
        final double waitCountAverage = (double) waitCount / (double) playCountWithWaitCount;
        final String waitCountAverageString = String.format("%.2f", waitCountAverage);

        return "平均待ち数: " + waitCountAverageString + " (" + waitCount + "/" + playCountWithWaitCount + ")";
    }

    /**
     * 平均待ち数のランキング
     */
    private String waitCountAverageRanking() {
        final TreeMap<Double, List<String>> rankingTable = new TreeMap<>();

        for (final Entry<String, PersonalStatistics> entry : _statisticsTable.entrySet()) {
            final double waitCountAverage = entry.getValue().waitCountAverage();
            List<String> playerList = rankingTable.get(waitCountAverage);

            if (playerList == null) {
            	playerList = new ArrayList<>();
            }
            playerList.add(entry.getKey().replaceFirst("[aeiou]", "$0 "));
            rankingTable.put(waitCountAverage, playerList);
        }
        String message ="平均待ち数: ";
        int count = 0;

        for (final double waitCountAverage : rankingTable.descendingKeySet()) {
            final List<String> playerList = rankingTable.get(waitCountAverage);
            final String waitCountAverageString = String.format("%.2f", waitCountAverage);
            message += playerList + " (" + waitCountAverageString + ")";
            count++;

            if (count != rankingTable.size()) {
                message += " > ";
            }
        }
        return message.replaceAll("[\\[\\]]", "");
    }

    /**
     * 平均待ち枚数
     */
    private String waitPaiCountAverage() {
        final int waitPaiCount = getWaitPaiCount();
        final int playCountWithWaitCount = getPlayCountWithWaitCount();
        final double waitCountAverage = (double) waitPaiCount / (double) playCountWithWaitCount;
        final String waitCountAverageString = String.format("%.2f", waitCountAverage);

        return "平均待ち枚数: " + waitCountAverageString + " 枚 (" + waitPaiCount + "/" + playCountWithWaitCount + ")";
    }

    /**
     * 平均待ち枚数のランキング
     */
    private String waitPaiCountAverageRanking() {
        final TreeMap<Double, List<String>> rankingTable = new TreeMap<>();

        for (final Entry<String, PersonalStatistics> entry : _statisticsTable.entrySet()) {
            final double waitPaiCountAverage = entry.getValue().waitPaiCountAverage();
            List<String> playerList = rankingTable.get(waitPaiCountAverage);

            if (playerList == null) {
            	playerList = new ArrayList<>();
            }
            playerList.add(entry.getKey().replaceFirst("[aeiou]", "$0 "));
            rankingTable.put(waitPaiCountAverage, playerList);
        }
        String message ="平均待ち枚数: ";
        int count = 0;

        for (final double waitPaiCountAverage : rankingTable.descendingKeySet()) {
            final List<String> playerList = rankingTable.get(waitPaiCountAverage);
            final String waitPaiCountAverageString = String.format("%.2f", waitPaiCountAverage);
            message += playerList + " (" + waitPaiCountAverageString + ")";
            count++;

            if (count != rankingTable.size()) {
                message += " > ";
            }
        }
        return message.replaceAll("[\\[\\]]", "");
    }

    /**
     * 役
     *
     * @param maxCount 最大数。
     * @param minimumPoint 最小点。
     * @return 役のゲーム統計。
     */
    private List<String> yaku(final int maxCount, final int minimumPoint) {
        final Map<String, Integer> yakuCountTable = getYakuCountTable();
        final TreeMap<Integer, List<String>> countMap = new TreeMap<>();

        for (final Entry<String, Integer> entry : yakuCountTable.entrySet()) {
            final int yakuPoint = getChmYakuPoint(entry.getKey());

            if (yakuPoint < minimumPoint) {
                continue;
            }
            final int yakuCount = entry.getValue();
            List<String> yakuList = countMap.get(yakuCount);

            if (yakuList == null) {
                yakuList = new ArrayList<>();
            }
            yakuList.add(entry.getKey());
            countMap.put(yakuCount, yakuList);
        }
        final int playCountWithYaku = getPlayCountWithYaku();
        final List<String> yakuStringList = new ArrayList<>();
        int count = 0;

        for (final Integer yakuCount : countMap.descendingKeySet()) {
            final double yakuRate = (double) yakuCount * 100 / (double) playCountWithYaku;
            final String yakuRateString = String.format("%.2f", yakuRate);
            boolean isEnd = false;

            for (final String yakuString : countMap.get(yakuCount)) {
                yakuStringList.add(yakuString + ": " + yakuRateString + " % (" + yakuCount + "/" + playCountWithYaku + ")");
                count++;

                if (count == maxCount) {
                    isEnd = true;
                    break;
                }
            }

            if (isEnd) {
                break;
            }
        }
        return yakuStringList;
    }



    /**
     * 個人のゲーム統計リスト
     */
    Map<String, PersonalStatistics> _statisticsTable = new TreeMap<>();

}

