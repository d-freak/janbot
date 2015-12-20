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
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;
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
     * @param flagSet 実況フラグ。
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
        final Player player = info.getPlayer(playerWind);
        final Integer turnCount = info.getTurnCount(playerWind);
        if (isCallable(flagSet)) {
            messageList.add(convertCallInfoToString(info.getActiveDiscard(), flagSet));
            messageList.add("(詳細：「jan help」を参照)");
        }
        if (flagSet.contains(AnnounceFlag.ACTIVE_TSUMO) || flagSet.contains(AnnounceFlag.AFTER_CALL)) {
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
        if (flagSet.contains(AnnounceFlag.HAND)) {
            messageList.add(convertHandToString(playerWind, info, flagSet));
            
            if (_7thMode && isSelectingDiscard(flagSet)) {
                List<JanPai> paiList = new ArrayList<>();
                
                if (flagSet.contains(AnnounceFlag.ACTIVE_TSUMO)) {
                    paiList = info.getSingleJanPaiList(playerWind, true);
                }
                else {
                    paiList = info.getSingleJanPaiList(playerWind, false);
                }
                addOutsString(messageList, getOuts(info, flagSet, paiList));
            }
        }
        if (flagSet.contains(AnnounceFlag.OUTS)) {
            final List<JanPai> paiList = param.getPaiList();
            
            addOutsString(messageList, getOuts(info, flagSet, paiList));
        }
        if (flagSet.contains(AnnounceFlag.SEVENTH)) {
            if (_7thMode) {
                _7thMode = false;
                messageList.add("七対モードを無効にしました。");
            }
            else {
                _7thMode = true;
                messageList.add("七対モードを有効にしました。");
                
                List<JanPai> paiList = new ArrayList<>();
                
                if (flagSet.contains(AnnounceFlag.CONFIRM)) {
                    paiList = info.getSingleJanPaiList(playerWind, false);
                }
                else {
                    paiList = info.getSingleJanPaiList(playerWind, true);
                }
                addOutsString(messageList, getOuts(info, flagSet, paiList));
            }
        }
        if (flagSet.contains(AnnounceFlag.COMPLETE_RON)) {
            messageList.add("---- ロン和了 ----");
            recordResultXml(player, turnCount, flagSet);
        }
        else if (flagSet.contains(AnnounceFlag.COMPLETE_TSUMO)) {
            messageList.add("---- ツモ和了 ----");
            recordResultXml(player, turnCount, flagSet);
        }
        else if (flagSet.contains(AnnounceFlag.GAME_OVER)) {
            messageList.add("---- 流局 ----");
            recordResultXml(player, turnCount, flagSet);
        }
        
        if (flagSet.contains(AnnounceFlag.GAME_END)) {
            messageList.add("--- 終了 ---");
            _7thMode = false;
        }
        
        IRCBOT.getInstance().println(messageList);
        
        if (flagSet.contains(AnnounceFlag.SCORE)) {
            printCompleteInfo(info, flagSet);
        }
    }
    
    
    
    /**
     * 残り枚数テーブルを文字列に変換し出力内容に追加
     * 
     * @param messageList 出力内容。
     * @param outs 残り枚数テーブル。
     */
    private void addOutsString(final List<String> messageList, final Map<JanPai, Integer> outs) {
        final StringBuilder buf = new StringBuilder();
        Integer total = 0;
        int count = 1;
        for (final JanPai pai : outs.keySet()) {
            final Integer outsCount = outs.get(pai);
            buf.append(convertJanPaiToString(pai));
            buf.append("：残り" + outsCount.toString() + "枚, ");
            total += outsCount;
            
            if (count % 9 == 0) {
                messageList.add(buf.toString());
                buf.delete(0, buf.length());
            }
            count++;
        }
        buf.append("計：残り" + total.toString() + "枚");
        messageList.add(buf.toString());
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
     * プレイヤーの風を取得
     * 
     * @param info ゲーム情報。
     * @return プレイヤーの風。
     */
    private Map<JanPai, Integer> getOuts(final JanInfo info, final EnumSet<AnnounceFlag> flagSet, final List<JanPai> paiList) {
        final Wind playerWind = getPlayerWind(info);
        
        if (flagSet.contains(AnnounceFlag.CONFIRM)) {
            return info.getOutsOnConfirm(paiList, playerWind);
        }
        else {
            return info.getOuts(paiList, playerWind);
        }
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
     * @param info ゲーム情報。
     * @param flagSet 実況フラグ。
     */
    private void printCompleteInfo(final JanInfo info, final EnumSet<AnnounceFlag> flagSet) {
        final ChmCompleteInfo completeInfo = info.getCompleteInfo();
        if (completeInfo.getYakuList().isEmpty()) {
            return;
        }
        Integer total = 0;
        for (final ChmYaku yaku : completeInfo.getYakuList()) {
        	IRCBOT.getInstance().println(yaku.toString() + " : " + yaku.toStringUS() + String.valueOf(yaku.getPoint()) + "点");
            total += yaku.getPoint();
        }
        if (flagSet.contains(AnnounceFlag.ACTIVE_TSUMO)) {
        	IRCBOT.getInstance().println("合計(" + total.toString() + "+8)a点");
        }
        else if (flagSet.contains(AnnounceFlag.ACTIVE_DISCARD)) {
        	IRCBOT.getInstance().println("合計" + total.toString() + "+8a点");
        }
    }
    
    /**
     * ゲーム結果をxmlに保存
     * 
     * @param player プレイヤー。
     * @param turnCount 巡目。
     * @param flagSet 実況フラグ。
     */
    @SuppressWarnings("unchecked")
	private void recordResultXml(final Player player, final Integer turnCount, final EnumSet<AnnounceFlag> flagSet) {
        final String path = "./" + player.getName() + ".xml";
        List<Node> completeType = new ArrayList<Node>();
        List<Node> completeTurn = new ArrayList<Node>();
        try {
            final SAXReader reader = new SAXReader();
            final Document readDocument = reader.read(path);
            completeType = readDocument.selectNodes("/results/result/completeType");
            completeTurn = readDocument.selectNodes("/results/result/completeTurn");
        } catch (DocumentException e) {
        }
        String addCompleteType = null;
        String addCompleteTurn = null;
        if (flagSet.contains(AnnounceFlag.ACTIVE_TSUMO)) {
            addCompleteType = "tsumo";
            addCompleteTurn = turnCount.toString();
        }
        else if (flagSet.contains(AnnounceFlag.ACTIVE_DISCARD)) {
            addCompleteType = "ron";
            addCompleteTurn = turnCount.toString();
        }
        else {
            addCompleteType = "-";
            addCompleteTurn = "-";
        }
        
        final Document writeDocument = DocumentHelper.createDocument();
        final Element root = writeDocument.addElement("results");
        for (int count = 0; count < completeType.size(); count++) {
            final Element result = root.addElement("result");
            result.addElement("completeType").setText(completeType.get(count).getStringValue());
            result.addElement("completeTurn").setText(completeTurn.get(count).getStringValue());
        }
        final Element result = root.addElement("result");
        result.addElement("completeType").setText(addCompleteType);
        result.addElement("completeTurn").setText(addCompleteTurn);
        
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
     * 七対モード
     */
    private boolean _7thMode = false;
    
}

