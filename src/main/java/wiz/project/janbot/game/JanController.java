/**
 * JanController.java
 * 
 * @author Yuki
 */

package wiz.project.janbot.game;

import java.util.List;
import java.util.Map;

import wiz.project.jan.JanPai;
import wiz.project.jan.Wind;
import wiz.project.janbot.game.exception.JanException;



/**
 * 麻雀コントローラ
 */
interface JanController {
    
    /**
     * 副露
     * 
     * @param playerName プレイヤー名。
     * @param type 副露タイプ。
     * @param target 牌指定。nullを許可する。
     * @throws JanException 例外イベント。
     */
    public void call(final String playerName, final CallType type, final JanPai target) throws JanException;
    
    /**
     * 和了 (ロン)
     * 
     * @param playerName プレイヤー名。
     * @throws JanException 例外イベント。
     */
    public void completeRon(final String playerName) throws JanException;
    
    /**
     * 和了 (ツモ)
     * 
     * @throws JanException 例外イベント。
     */
    public void completeTsumo() throws JanException;
    
    /**
     * 打牌 (ツモ切り)
     * 
     * @throws JanException 例外イベント。
     */
    public void discard() throws JanException;
    
    /**
     * 打牌 (手出し)
     * 
     * @param target 捨て牌。
     * @throws JanException 例外イベント。
     */
    public void discard(final JanPai target) throws JanException;
    
    /**
     * ゲーム情報を取得
     * 
     * @return ゲーム情報。
     */
    public JanInfo getGameInfo();
    
    /**
     * 次のプレイヤーの打牌へ
     * 
     * @throws JanException 例外イベント。
     */
    public void next() throws JanException;
    
    /**
     * リーチ
     * 
     * @throws JanException 例外イベント。
     */
    public void richi(final JanPai target) throws JanException;
    
    /**
     * 開始
     * 
     * @param deck 牌山。
     * @param playerTable プレイヤーテーブル。
     * @throws JanException 例外イベント。
     */
    public void start(final List<JanPai> deck, final Map<Wind, Player> playerTable) throws JanException;
    
}

