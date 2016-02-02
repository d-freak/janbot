/**
 * AnnounceParam.java
 * 
 * @Author
 *   D-freak
 */

package wiz.project.janbot.game;

import java.util.EnumSet;
import java.util.List;

import wiz.project.jan.JanPai;



/**
 * 実況パラメータ (immutable)
 */
final class AnnounceParam {
    
    /**
     * コンストラクタ
     */
    public AnnounceParam(final AnnounceFlag flag) {
        _flagSet = EnumSet.of(flag);
    }
    
    /**
     * コンストラクタ
     */
    public AnnounceParam(final AnnounceFlag flag, final int completableTurn) {
        _flagSet = EnumSet.of(flag);
        _completableTurn = completableTurn;
    }
    
    /**
     * コンストラクタ
     */
    public AnnounceParam(final AnnounceFlag flag, final String playerName, final int start, final int end) {
        _flagSet = EnumSet.of(flag);
        _playerName = playerName;
        _start = start;
        _end = end;
    }
    
    /**
     * コンストラクタ
     */
    public AnnounceParam(final AnnounceFlag flag, final List<JanPai> paiList) {
        _flagSet = EnumSet.of(flag);
        _paiList = paiList;
    }
    
    /**
     * コンストラクタ
     */
    public AnnounceParam(final EnumSet<AnnounceFlag> flagSet) {
        _flagSet = flagSet;
    }
    
    /**
     * コンストラクタ
     */
    public AnnounceParam(final EnumSet<AnnounceFlag> flagSet, final List<JanPai> paiList) {
        _flagSet = flagSet;
        _paiList = paiList;
    }
    
    
    
    /**
     * 和了可能巡目を取得
     */
    public int getCompletableTurn() {
        return _completableTurn;
    }
    
    /**
     * 終了値を取得
     */
    public int getEnd() {
        return _end;
    }
    
    /**
     * 実況フラグを取得
     */
    public EnumSet<AnnounceFlag> getFlagSet() {
        return _flagSet;
    }
    
    /**
     * 牌リストを取得
     */
    public List<JanPai> getPaiList() {
        return _paiList;
    }
    
    /**
     * プレイヤー名を取得
     */
    public String getPlayerName() {
        return _playerName;
    }
    
    /**
     * 開始値を取得
     */
    public int getStart() {
        return _start;
    }
    
    
    
    /**
     * 和了可能巡目
     */
    private int _completableTurn = 0;
    
    /**
     * 終了値
     */
    private int _end = 0;
    
    /**
     * 実況フラグ
     */
    private EnumSet<AnnounceFlag> _flagSet = null;
    
    /**
     * 牌リスト
     */
    private List<JanPai> _paiList = null;
    
    /**
     * プレイヤー名
     */
    private String _playerName = null;
    
    /**
     * 開始値
     */
    private int _start = 0;
}

