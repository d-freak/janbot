/**
 * GameAnnouncer.java
 *
 * @author Yuki
 */

package wiz.project.janbot.game;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import wiz.project.ircbot.IRCBOT;
import wiz.project.jan.ChmCompleteInfo;
import wiz.project.jan.Hand;
import wiz.project.jan.JanPai;
import wiz.project.jan.MenTsu;
import wiz.project.jan.MenTsuType;
import wiz.project.jan.Wind;
import wiz.project.jan.yaku.ChmYaku;
import wiz.project.janbot.game.exception.InvalidInputException;
import wiz.project.janbot.statistics.Statistics;
import wiz.project.janbot.statistics.StatisticsParam;
import wiz.project.janbot.statistics.YakuParam;



/**
 * ゲーム実況者
 */
public class GameAnnouncer implements Observer {
    
    /**
     * コンストラクタ
     */
    public GameAnnouncer() {
    }
    
    
    
    /**
     * 状況更新時の処理
     *
     * @param target 監視対象。
     * @param param 更新パラメータ。
     */
    @SuppressWarnings("unchecked")
    public void update(final Observable target, final Object param) {
        if (target instanceof JanInfo) {
            if (param instanceof EnumSet) {
                final AnnounceParam announceParam = new AnnounceParam((EnumSet<AnnounceFlag>) param);
                updateOnSolo((JanInfo)target, announceParam);
            }
            else if (param instanceof AnnounceFlag) {
                final AnnounceParam announceParam = new AnnounceParam(EnumSet.of((AnnounceFlag) param));
                updateOnSolo((JanInfo)target, announceParam);
            }
            else if (param instanceof AnnounceParam) {
                updateOnSolo((JanInfo)target, (AnnounceParam) param);
            }
            else if (param instanceof HistoryParam) {
                updateOnSolo((JanInfo)target, (HistoryParam) param);
            }
            else if (param instanceof StatisticsParam) {
                updateOnSolo((JanInfo)target, (StatisticsParam) param);
            }
            else if (param instanceof YakuParam) {
                updateOnSolo((JanInfo)target, (YakuParam) param);
            }
        }
    }
    
    
    
    /**
     * 中国麻雀フラグを設定
     *
     * @param isChm
     */
    public void setIsChm(final boolean isChm) {
    	_isChm = isChm;
    }
    
    
    
    /**
     * 副露された雀牌を文字列に変換
     *
     * @param pai 副露された雀牌。
     * @return 変換結果。
     */
    protected String convertCalledJanPaiToString(final JanPai pai) {
        final StringBuilder buf = new StringBuilder();
        buf.append(COLOR_FLAG).append("14");  // 灰色
        buf.append(pai);
        buf.append(COLOR_FLAG);
        return buf.toString();
    }
    
    /**
     * 雀牌を文字列に変換
     *
     * @param pai 雀牌。
     * @return 変換結果。
     */
    protected String convertJanPaiToString(final JanPai pai) {
        final StringBuilder buf = new StringBuilder();
        buf.append(COLOR_FLAG).append(getColorCode(pai));
        buf.append(pai);
        buf.append(COLOR_FLAG);
        return buf.toString();
    }
    
    /**
     * 状況更新時の処理
     *
     * @param info 麻雀ゲーム情報。
     * @param param 更新パラメータ。
     */
    protected void updateOnSolo(final JanInfo info, final AnnounceParam param) {
        if (info == null) {
            throw new NullPointerException("Game information is null.");
        }
        if (param == null) {
            throw new NullPointerException("Announce parameter is null.");
        }
        
        final EnumSet<AnnounceFlag> flagSet = param.getFlagSet();
        final Wind playerWind = getPlayerWind(info);
        final List<String> messageList = new ArrayList<>();
        final int turnCount = info.getTurnCount(playerWind);
        if (isCallable(flagSet)) {
            messageList.add(convertCallInfoToString(info.getActiveDiscard(), flagSet));
            messageList.add("(詳細：「jan help」を参照)");
        }
        if (isSelectingDiscard(flagSet)) {
            messageList.add(turnCount + "巡目");
        }
        if (flagSet.contains(AnnounceFlag.AFTER_CALL)) {
            messageList.add("捨て牌を選んでください");
        }
        if (flagSet.contains(AnnounceFlag.FIELD)) {
            messageList.add(convertFieldToString(playerWind, info, flagSet.contains(AnnounceFlag.URA_DORA)));
        }
        if (flagSet.contains(AnnounceFlag.RIVER_SINGLE)) {
            messageList.add(convertRiverToString(info.getRiver(playerWind)));
        }
        if (flagSet.contains(AnnounceFlag.RIVER_ALL)) {
            // 出力文字数制限対策
            // 分割して出力バッファに渡す
            IRCBOT.getInstance().println(messageList);
            IRCBOT.getInstance().println("東" + convertRiverToString(info.getRiver(Wind.TON)));
            IRCBOT.getInstance().println("南" + convertRiverToString(info.getRiver(Wind.NAN)));
            IRCBOT.getInstance().println("西" + convertRiverToString(info.getRiver(Wind.SHA)));
            IRCBOT.getInstance().println("北" + convertRiverToString(info.getRiver(Wind.PEI)));
            messageList.clear();
        }
        final boolean isConfirm = flagSet.contains(AnnounceFlag.CONFIRM);
        
        if (flagSet.contains(AnnounceFlag.HAND)) {
            messageList.add(convertHandToString(playerWind, info, flagSet));
            
            if (isSelectingDiscard(flagSet)) {
                List<JanPai> paiList = new ArrayList<>();
                
                switch (_announceMode) {
                case WATCH:
                    paiList = info.getWatchingJanPaiList();
                    messageList.addAll(getOutsString(info, isConfirm, paiList));
                    break;
                case SEVENTH:
                    if (flagSet.contains(AnnounceFlag.ACTIVE_TSUMO)) {
                        paiList = info.getOddJanPaiList(playerWind, true);
                    }
                    else {
                        paiList = info.getOddJanPaiList(playerWind, false);
                    }
                    messageList.addAll(getSeventhOutsString(info, isConfirm, paiList));
                    break;
                default:
                }
            }
        }
        if (flagSet.contains(AnnounceFlag.WATCHING_START)) {
            _announceMode = AnnounceMode.WATCH;
            messageList.add("監視モードを有効にしました。");
            
            final List<JanPai> paiList = info.getWatchingJanPaiList();
            messageList.addAll(getOutsString(info, isConfirm, paiList));
        }
        if (flagSet.contains(AnnounceFlag.WATCHING_END)) {
            if (AnnounceMode.WATCH.equals(_announceMode)) {
                _announceMode = AnnounceMode.NORMAL;
                messageList.add("監視モードを無効にしました。");
            }
        }
        if (flagSet.contains(AnnounceFlag.OUTS)) {
            final List<JanPai> paiList = param.getPaiList();
            
            messageList.addAll(getOutsString(info, isConfirm, paiList));
        }
        if (flagSet.contains(AnnounceFlag.SEVENTH)) {
            if (AnnounceMode.SEVENTH.equals(_announceMode)) {
                _announceMode = AnnounceMode.NORMAL;
                messageList.add("七対モードを無効にしました。");
            }
            else {
                _announceMode = AnnounceMode.SEVENTH;
                messageList.add("七対モードを有効にしました。");
                
                List<JanPai> paiList = new ArrayList<>();
                
                if (isConfirm) {
                    paiList = info.getOddJanPaiList(playerWind, false);
                }
                else {
                    paiList = info.getOddJanPaiList(playerWind, true);
                }
                messageList.addAll(getSeventhOutsString(info, isConfirm, paiList));
            }
        }
        if (flagSet.contains(AnnounceFlag.COMPLETE_RON)) {
            messageList.add("---- ロン和了(" + turnCount + "巡目) ----");
            recordResultXml(info);
        }
        else if (flagSet.contains(AnnounceFlag.COMPLETE_TSUMO)) {
            messageList.add("---- ツモ和了(" + turnCount + "巡目) ----");
            recordResultXml(info);
        }
        else if (flagSet.contains(AnnounceFlag.GAME_OVER)) {
            messageList.add("---- 流局 ----");
            recordResultXml(info);
        }
        
        if (flagSet.contains(AnnounceFlag.GAME_END)) {
            messageList.add("--- 終了 ---");
            _announceMode = AnnounceMode.NORMAL;
        }
        
        if (flagSet.contains(AnnounceFlag.OVER_TIED_POINT)) {
            final int completableTurn = info.getCompletableTurnCount(playerWind);
            
            messageList.add(completableTurn + "巡目で8点縛りを超えました。");
            
            final List<JanPai> paiList = info.getCompletableJanPaiList(playerWind);
            
            messageList.addAll(getWaitingOutsString(info, flagSet, paiList));
        }
        
        if (flagSet.contains(AnnounceFlag.OVER_TIED_POINT_AND_NO_OUTS)) {
            messageList.add("8点縛りを超えていますが、和了牌がありません。");
        }
        
        if (flagSet.contains(AnnounceFlag.END_OVER_TIED_POINT)) {
            messageList.add("8点縛り超えが終了しました。");
        }
        
        if (flagSet.contains(AnnounceFlag.NOT_OVER_TIED_POINT)) {
            final int totalPoint = info.getCompleteInfo().getTotalPoint();
            
            messageList.add((8 - totalPoint) + "点足りません");
        }
        
        if (flagSet.contains(AnnounceFlag.CHANGE_WAIT)) {
            messageList.add("待ちが変わりました。");
            
            final List<JanPai> paiList = info.getCompletableJanPaiList(playerWind);
            
            messageList.addAll(getWaitingOutsString(info, flagSet, paiList));
        }
        
        if (flagSet.contains(AnnounceFlag.RANKING)) {
            messageList.addAll(getRankingString());
        }
        printMessage(messageList);
        
        if (flagSet.contains(AnnounceFlag.SCORE)) {
            final ChmCompleteInfo completeInfo = info.getCompleteInfo();
            
            printCompleteInfo(completeInfo);
        }
    }
    
    /**
     * 状況更新時の処理
     *
     * @param info 麻雀ゲーム情報。
     * @param param 更新パラメータ。
     */
    protected void updateOnSolo(final JanInfo info, final HistoryParam param) {
        if (info == null) {
            throw new NullPointerException("Game information is null.");
        }
        if (param == null) {
            throw new NullPointerException("History parameter is null.");
        }
        final List<String> messageList = new ArrayList<>();
        messageList.addAll(getHistoryString(param));
        
        printMessage(messageList);
    }
    
    /**
     * 状況更新時の処理
     *
     * @param info 麻雀ゲーム情報。
     * @param param 更新パラメータ。
     */
    protected void updateOnSolo(final JanInfo info, final StatisticsParam param) {
        if (info == null) {
            throw new NullPointerException("Game information is null.");
        }
        if (param == null) {
            throw new NullPointerException("Statistics parameter is null.");
        }
        final List<String> messageList = new ArrayList<>();
        messageList.addAll(getStatisticsString(param));
        
        printMessage(messageList);
    }
    
    /**
     * 状況更新時の処理
     *
     * @param info 麻雀ゲーム情報。
     * @param param 更新パラメータ。
     */
    protected void updateOnSolo(final JanInfo info, final YakuParam param) {
        if (info == null) {
            throw new NullPointerException("Game information is null.");
        }
        if (param == null) {
            throw new NullPointerException("YakuStatistics parameter is null.");
        }
        final List<String> messageList = new ArrayList<>();
        messageList.addAll(getYakuStatisticsString(param));
        
        printMessage(messageList);
    }
    
    
    
    /**
     * 副露情報を文字列に変換
     *
     * @param discard 捨て牌。
     * @param flagSet 実況フラグ。
     * @return 変換結果。
     */
    private String convertCallInfoToString(final JanPai discard, final EnumSet<AnnounceFlag> flagSet) {
        final StringBuilder buf = new StringBuilder();
        buf.append(convertJanPaiToString(discard)).append(" <- ");
        if (flagSet.contains(AnnounceFlag.CALLABLE_RON)) {
            buf.append("ロン可能です：  ");
        }
        else {
            buf.append("鳴けそうです：  ");
        }
        if (flagSet.contains(AnnounceFlag.CALLABLE_RON)) {
            buf.append("[ロン]");
        }
        if (flagSet.contains(AnnounceFlag.CALLABLE_CHI)) {
            buf.append("[チー]");
        }
        if (flagSet.contains(AnnounceFlag.CALLABLE_PON)) {
            buf.append("[ポン]");
        }
        if (flagSet.contains(AnnounceFlag.CALLABLE_KAN)) {
            buf.append("[カン]");
        }
        return buf.toString();
    }
    
    /**
     * 場情報を文字列に変換
     *
     * @param wind 対象プレイヤーの風。
     * @param info ゲーム情報。
     * @param includeUraDora 裏ドラを表示するか。
     * @return 変換結果。
     */
    private String convertFieldToString(final Wind wind, final JanInfo info, final boolean includeUraDora) {
        final StringBuilder buf = new StringBuilder();
        buf.append("場風：").append(info.getFieldWind()).append("   ");
        buf.append("自風：").append(wind).append("   ");
        
        if (!_isChm) {
            final WanPai wanPai = info.getWanPai();
            buf.append("ドラ：");
            for (final JanPai pai : wanPai.getDoraList()) {
                buf.append(convertJanPaiToString(pai));
            }
            buf.append("   ");
            
            if (includeUraDora) {
                buf.append("裏ドラ：");
                for (final JanPai pai : wanPai.getUraDoraList()) {
                    buf.append(convertJanPaiToString(pai));
                }
                buf.append("   ");
            }
        }
        
        buf.append("残り枚数：").append(info.getRemainCount());
        return buf.toString();
    }
    
    /**
     * 副露牌を文字列に変換
     *
     * @param hand 手牌。
     * @return 変換結果。
     */
    private String convertFixedMenTsuToString(final Hand hand) {
        final StringBuilder buf = new StringBuilder();
        if (hand.getFixedMenTsuCount() == 0) {
            return buf.toString();
        }
        
        buf.append(" ");
        final List<MenTsu> fixedMenTsuList = hand.getFixedMenTsuList();
        Collections.reverse(fixedMenTsuList);
        for (final MenTsu fixedMenTsu : fixedMenTsuList) {
            buf.append(" ");
            final List<JanPai> sourceList = fixedMenTsu.getSource();
            if (fixedMenTsu.getMenTsuType() == MenTsuType.KAN_DARK) {
                final JanPai pai = sourceList.get(0);
                final String source = "[■]" + pai + pai + "[■]";
                buf.append(COLOR_FLAG).append(getColorCode(pai)).append(source).append(COLOR_FLAG);
            }
            else {
                buf.append(COLOR_FLAG).append(getColorCode(sourceList.get(0)));
                for (final JanPai pai : sourceList) {
                    buf.append(pai);
                }
                buf.append(COLOR_FLAG);
            }
        }
        return buf.toString();
    }
    
    /**
     * 手牌を文字列に変換
     *
     * @param wind 対象プレイヤーの風。
     * @param info ゲーム情報。
     * @param flagSet 実況フラグ。
     * @return 変換結果。
     */
    private String convertHandToString(final Wind wind, final JanInfo info, final EnumSet<AnnounceFlag> flagSet) {
        final Hand hand = info.getHand(wind);
        final StringBuilder buf = new StringBuilder();
        buf.append(convertMenzenHandToString(hand));
        if (flagSet.contains(AnnounceFlag.ACTIVE_TSUMO)) {
            buf.append(" ").append(convertJanPaiToString(info.getActiveTsumo()));
        }
        else if (flagSet.contains(AnnounceFlag.ACTIVE_DISCARD)) {
            buf.append(" ").append(convertJanPaiToString(info.getActiveDiscard()));
        }
        buf.append(convertFixedMenTsuToString(hand));
        return buf.toString();
    }
    
    /**
     * 面前手牌を文字列に変換
     *
     * @param hand 手牌。
     * @return 変換結果。
     */
    private String convertMenzenHandToString(final Hand hand) {
        final StringBuilder buf = new StringBuilder();
        for (final JanPai pai : hand.getMenZenList()) {
            buf.append(convertJanPaiToString(pai));
        }
        return buf.toString();
    }
    
    /**
     * 捨て牌リストを文字列に変換
     *
     * @param river 捨て牌リスト。
     * @return 変換結果。
     */
    private String convertRiverToString(final River river) {
        final StringBuilder buf = new StringBuilder();
        int count = 0;
        int calledIndex = 0;
        buf.append("捨牌：");
        for (final JanPai pai : river.get()) {
            if (calledIndex < river.getCalledIndexList().size() && count == river.getCalledIndexList().get(calledIndex)) {
                buf.append(convertCalledJanPaiToString(pai));
                calledIndex++;
            }
            else {
            	buf.append(convertJanPaiToString(pai));
            }
            if (count % 6 == 5) {
                buf.append("  ");
            }
            count++;
        }
        return buf.toString();
    }
    
    /**
     * 色コードを取得
     *
     * @param pai 雀牌。
     * @return 対応する色コード。
     */
    private String getColorCode(final JanPai pai) {
        switch (pai) {
        case MAN_1:
        case MAN_2:
        case MAN_3:
        case MAN_4:
        case MAN_5:
        case MAN_6:
        case MAN_7:
        case MAN_8:
        case MAN_9:
        case CHUN:
            return "04";  // 赤
        case PIN_1:
        case PIN_2:
        case PIN_3:
        case PIN_4:
        case PIN_5:
        case PIN_6:
        case PIN_7:
        case PIN_8:
        case PIN_9:
            return "12";  // 青
        case SOU_1:
        case SOU_2:
        case SOU_3:
        case SOU_4:
        case SOU_5:
        case SOU_6:
        case SOU_7:
        case SOU_8:
        case SOU_9:
        case HATU:
            return "03";  // 緑
        case TON:
        case NAN:
        case SHA:
        case PEI:
            return "06";  // 紫
        default:
            return "01";  // 黒
        }
    }
    
    /**
     * 残り枚数テーブルの文字列を取得
     *
     * @param info ゲーム情報。
     * @param flagSet 実況フラグ。
     * @param paiList 牌リスト。
     * @return 残り枚数テーブルの文字列。
     */
    private List<String> getOutsString(final JanInfo info, final boolean isConfirm, final List<JanPai> paiList) {
        final Wind playerWind = getPlayerWind(info);
        Map<JanPai, Integer> outs = null;
        
        if (isConfirm) {
            outs = info.getOutsOnConfirm(paiList, playerWind);
        }
        else {
            outs = info.getOuts(paiList, playerWind);
        }
        final List<String> messageList = new ArrayList<>();
        final StringBuilder buf = new StringBuilder();
        int total = 0;
        int count = 1;
        boolean isEmptyBuf = true;
        
        for (final JanPai pai : outs.keySet()) {
            final Integer outsCount = outs.get(pai);
            buf.append(convertJanPaiToString(pai));
            buf.append("：残り" + outsCount.toString() + "枚, ");
            total += outsCount;
            
            if (isEmptyBuf) {
                isEmptyBuf = false;
            }
            
            if (count % 9 == 0) {
                messageList.add(buf.toString());
                buf.delete(0, buf.length());
            }
            count++;
        }
        
        if (!isEmptyBuf) {
            buf.append("計：残り" + total + "枚");
            messageList.add(buf.toString());
        }
        return messageList;
    }
    
    /**
     * プレイヤーの風を取得
     *
     * @param info ゲーム情報。
     * @return プレイヤーの風。
     */
    private Wind getPlayerWind(final JanInfo info) {
        for (final Map.Entry<Wind, Player> entry : info.getPlayerTable().entrySet()) {
            if (entry.getValue().getType() != PlayerType.COM) {
                return entry.getKey();
            }
        }
        throw new InternalError();
    }
    
    /**
     * ランキングの文字列を取得
     *
     * @return ランキングの文字列。
     */
    private List<String> getRankingString() {
        final List<String> messageList = new ArrayList<>();
        Statistics statistics = null;
        
        try {
            statistics = new Statistics("all", 0, 0);
        }
        catch (DocumentException e) {
            messageList.add("プレイヤーの記録がありません。");
            return messageList;
        }
        catch (InvalidInputException e) {
            messageList.add("不正な開始値、終了値が指定されました。");
            return messageList;
        }
        messageList.addAll(statistics.getRanking());
        return messageList;
    }
    
    /**
     * 七対モードの残り枚数テーブルの文字列を取得
     * ※ 残り枚数が3枚の場合は表示しない
     *
     * @param info ゲーム情報。
     * @param flagSet 実況フラグ。
     * @param paiList 牌リスト。
     * @return 残り枚数テーブルの文字列。
     */
    private List<String> getSeventhOutsString(final JanInfo info, final boolean isConfirm, final List<JanPai> paiList) {
        final Wind playerWind = getPlayerWind(info);
        Map<JanPai, Integer> outs = null;
        
        if (isConfirm) {
            outs = info.getOutsOnConfirm(paiList, playerWind);
        }
        else {
            outs = info.getOuts(paiList, playerWind);
        }
        final List<String> messageList = new ArrayList<>();
        final StringBuilder buf = new StringBuilder();
        int total = 0;
        int count = 1;
        boolean isEmptyBuf = true;
        
        for (final JanPai pai : outs.keySet()) {
            final Integer outsCount = outs.get(pai);
            
            if (outsCount == 3) {
                continue;
            }
            buf.append(convertJanPaiToString(pai));
            buf.append("：残り" + outsCount.toString() + "枚, ");
            total += outsCount;
            
            if (isEmptyBuf) {
                isEmptyBuf = false;
            }
            
            if (count % 9 == 0) {
                messageList.add(buf.toString());
                buf.delete(0, buf.length());
            }
            count++;
        }
        
        if (!isEmptyBuf) {
            buf.append("計：残り" + total + "枚");
            messageList.add(buf.toString());
        }
        return messageList;
    }
    
    /**
     * コマンド履歴の文字列を取得
     *
     * @param param 更新パラメータ。
     * @return コマンド履歴の文字列。
     */
    private List<String> getHistoryString(final HistoryParam param) {
        final List<String> messageList = new ArrayList<>();
        final List<CommandHistory> historyList = param.getHistoryList();
        final StringBuilder buf = new StringBuilder();
        int count = 1;
        
        for (final CommandHistory history : historyList) {
            final HistoryType historyType = history.getHistoryType();
            
            buf.append(historyType.toString());
            
            final String pai = history.getJanPai();
            
            if (!pai.equals("")) {
                buf.append(pai);
            }
            buf.append(", ");
            
            if (count % 9 == 0) {
                messageList.add(buf.toString());
                buf.delete(0, buf.length());
            }
        }
        messageList.add(buf.toString());
        buf.delete(0, buf.length());
        
        return messageList;
    }
    
    /**
     * 指定したプレイヤー名のゲーム統計の文字列を取得
     *
     * @param param 更新パラメータ。
     * @return 指定したプレイヤー名のゲーム統計の文字列。
     */
    private List<String> getStatisticsString(final StatisticsParam param) {
        final List<String> messageList = new ArrayList<>();
        final String playerName = param.getPlayerName();
        final int start = param.getStart();
        final int end = param.getEnd();
        Statistics statistics = null;
        
        try {
            statistics = new Statistics(playerName, start, end);
        }
        catch (DocumentException e) {
            messageList.add(playerName + "というプレイヤーの記録はありません。");
            return messageList;
        }
        catch (InvalidInputException e) {
            messageList.add("不正な開始値、終了値が指定されました。");
            return messageList;
        }
        messageList.addAll(statistics.get());
        return messageList;
    }
    
    /**
     * 待ち牌の残り枚数テーブルの文字列を取得
     *
     * @param info ゲーム情報。
     * @param flagSet 実況フラグ。
     * @param paiList 牌リスト。
     * @return 待ち牌の残り枚数テーブルの文字列。
     */
    private List<String> getWaitingOutsString(final JanInfo info, final EnumSet<AnnounceFlag> flagSet, final List<JanPai> paiList) {
        final LinkedList<String> messageList = new LinkedList<>();
        messageList.addAll(getOutsString(info, true, paiList));
        
        String firstMessage = messageList.pollFirst();
        firstMessage = firstMessage.replaceFirst("^", "待ち牌：$0");
        messageList.addFirst(firstMessage);
        
        return messageList;
    }
    
    /**
     * 指定したプレイヤー名の役のゲーム統計の文字列を取得
     *
     * @param param 更新パラメータ。
     * @return 指定したプレイヤー名の役のゲーム統計の文字列。
     */
    private List<String> getYakuStatisticsString(final YakuParam param) {
        final List<String> messageList = new ArrayList<>();
        final String playerName = param.getPlayerName();
        final int start = param.getStart();
        final int end = param.getEnd();
        Statistics statistics = null;
        
        try {
            statistics = new Statistics(playerName, start, end);
        }
        catch (DocumentException e) {
            messageList.add(playerName + "というプレイヤーの記録はありません。");
            return messageList;
        }
        catch (InvalidInputException e) {
            messageList.add("不正な開始値、終了値が指定されました。");
            return messageList;
        }
        final int maxCount = param.getMaxCount();
        final int minimumPoint = param.getMinimumPoint();
        
        messageList.addAll(statistics.getYaku(maxCount, minimumPoint));
        return messageList;
    }
    
    /**
     * 副露可能か
     *
     * @param flagSet 実況フラグ。
     * @return 判定結果。
     */
    private boolean isCallable(final EnumSet<AnnounceFlag> flagSet) {
        for (final AnnounceFlag flag : flagSet) {
            if (flag.isCallable()) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * 捨て牌を選択中か
     *
     * @param flagSet 実況フラグ。
     * @return 判定結果。
     */
    private boolean isSelectingDiscard(final EnumSet<AnnounceFlag> flagSet) {
        if (flagSet.contains(AnnounceFlag.RIVER_SINGLE)) {
            return false;
        }
        
        if (flagSet.contains(AnnounceFlag.ACTIVE_TSUMO)) {
            return true;
        }
        
        if (flagSet.contains(AnnounceFlag.AFTER_CALL)) {
            return true;
        }
        return false;
    }
    
    /**
     * 和了情報を出力
     *
     * @param completeInfo 和了情報。
     */
    private void printCompleteInfo(final ChmCompleteInfo completeInfo) {
        if (completeInfo.getYakuList().isEmpty()) {
            return;
        }
        for (final ChmYaku yaku : completeInfo.getYakuList()) {
            IRCBOT.getInstance().println(yaku.toString() + " : " + yaku.toStringUS() + String.valueOf(yaku.getPoint()) + "点");
        }
        final boolean isRon = completeInfo.getCompleteType().isRon();
        final Integer total = completeInfo.getTotalPoint();
        
        if (isRon) {
            IRCBOT.getInstance().println("合計" + total.toString() + "+8a点");
        }
        else {
            IRCBOT.getInstance().println("合計(" + total.toString() + "+8)a点");
        }
    }
    
    /**
     * メッセージを出力
     *
     * @param messageList メッセージリスト。
     */
    private void printMessage(final List<String> messageList) {
        final int maxLineCount = 6;
        int lineCount = 0;
        
        for (; messageList.size() - lineCount > maxLineCount; lineCount += maxLineCount) {
            IRCBOT.getInstance().println(messageList.subList(lineCount, lineCount + maxLineCount));
        }
        IRCBOT.getInstance().println(messageList.subList(lineCount, messageList.size()));
    }
    
    /**
     * ゲーム結果をxmlに保存
     *
     * @param player プレイヤー。
     * @param turnCount 巡目。
     * @param completableTurnCount 和了可能巡目。
     * @param completeInfo 和了情報。
     */
    private void recordResultXml(final JanInfo info) {
        if (!_isChm) {
            return;
        }
        final Wind wind = getPlayerWind(info);
        final int completableTurnCount = info.getCompletableTurnCount(wind);
        String addCompletableTurn;
        String addWaitCount;
        String addWaitPaiCount;
        if (completableTurnCount != 0) {
            addCompletableTurn = String.valueOf(completableTurnCount);
            final List<JanPai> completableJanPaiList = info.getCompletableJanPaiList(wind);
            addWaitCount = String.valueOf(completableJanPaiList.size());
            int waitPaiCount = 0;
            
            for (final Integer count : info.getOutsOnConfirm(completableJanPaiList, wind).values()) {
                waitPaiCount += count;
            }
            addWaitPaiCount = String.valueOf(waitPaiCount);
        }
        else {
            addCompletableTurn = "-";
            addWaitCount = "-";
            addWaitPaiCount = "-";
        }
        final int calledMenTsuCount = info.getHand(wind).getCalledMenTsuCount();
        final String addCalledMenTsuCount = String.valueOf(calledMenTsuCount);
        final ChmCompleteInfo completeInfo = info.getCompleteInfo();
        String addCompleteType;
        String addCompleteTurn;
        String addPoint;
        String addYaku;
        if (completeInfo == null) {
            addCompleteTurn = "-";
            addCompleteType = "-";
            addPoint = "-";
            addYaku = "-";
        }
        else {
            final boolean isRon = completeInfo.getCompleteType().isRon();
            
            if (isRon) {
                addCompleteType = "ron";
            }
            else {
                addCompleteType = "tsumo";
            }
            final int turnCount = info.getTurnCount(wind);
            addCompleteTurn = String.valueOf(turnCount);
            addPoint = String.valueOf(completeInfo.getTotalPoint());
            addYaku = String.valueOf(completeInfo.getYakuList());
        }
        
        final Document writeDocument = DocumentHelper.createDocument();
        final Element writeRoot = writeDocument.addElement("results");
        final String playerName = info.getPlayer(wind).getName();
        final String path = "./" + playerName + ".xml";
        try {
            final SAXReader reader = new SAXReader();
            final Document readDocument = reader.read(path);
            final Element readRoot = readDocument.getRootElement();
            for (final Object element : readRoot.elements()) {
                final Element readResult = (Element) element;
                final Element writeResult = writeRoot.addElement("result");
                
                for (final Object e : readResult.elements()) {
                    final Element data = (Element) e;
                    final String name = data.getName();
                    
                    switch (name) {
                    case "calledMenTsuCount":
                        writeResult.addElement("calledMenTsuCount").setText(data.getStringValue());
                        break;
                    case "completableTurn":
                        writeResult.addElement("completableTurn").setText(data.getStringValue());
                        break;
                    case "completeTurn":
                        writeResult.addElement("completeTurn").setText(data.getStringValue());
                        break;
                    case "completeType":
                        writeResult.addElement("completeType").setText(data.getStringValue());
                        break;
                    case "point":
                        writeResult.addElement("point").setText(data.getStringValue());
                        break;
                    case "waitCount":
                        writeResult.addElement("waitCount").setText(data.getStringValue());
                        break;
                    case "waitPaiCount":
                        writeResult.addElement("waitPaiCount").setText(data.getStringValue());
                        break;
                    case "yaku":
                        writeResult.addElement("yaku").setText(data.getStringValue());
                        break;
                    default:
                    }
                }
            }
        } catch (DocumentException e) {
        }
        final Element result = writeRoot.addElement("result");
        result.addElement("calledMenTsuCount").setText(addCalledMenTsuCount);
        result.addElement("completableTurn").setText(addCompletableTurn);
        result.addElement("completeTurn").setText(addCompleteTurn);
        result.addElement("completeType").setText(addCompleteType);
        result.addElement("point").setText(addPoint);
        result.addElement("waitCount").setText(addWaitCount);
        result.addElement("waitPaiCount").setText(addWaitPaiCount);
        result.addElement("yaku").setText(addYaku);
        
        XMLWriter writer = null;
        try {
            final FileOutputStream outputStream = new FileOutputStream(path);
            final OutputFormat format = new OutputFormat("  ", true, "UTF-8");
            writer = new XMLWriter(outputStream, format);
            writer.write(writeDocument);
        } catch (FileNotFoundException e) {
        } catch (UnsupportedEncodingException e) {
        } catch (IOException e) {
        } finally {
            try {
                writer.close();
            } catch (IOException e) {
            }
        }
    }
    
    
    
    /**
     * 色付けフラグ
     */
    private static final char COLOR_FLAG = 3;
    
    
    
    /**
     * 中国麻雀フラグ
     */
    private boolean _isChm = false;
    
    /**
     * 実況モード
     */
    private AnnounceMode _announceMode = AnnounceMode.NORMAL;
    
}

